package com.papusbarbershop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Servicio asíncrono para el envío de correos electrónicos usando Amazon SES.
 * 
 * Este servicio desacopla el envío de correos del flujo principal de la aplicación,
 * ejecutando las operaciones de envío en segundo plano mediante un ExecutorService.
 * 
 * ARQUITECTURA:
 * - EmailAsyncService: Coordina el envío asíncrono
 * - EmailExecutor: Gestiona el pool de hilos (ExecutorService)
 * - SesClient: Realiza el envío real del correo usando Amazon SES
 * 
 * VENTAJAS:
 * - No bloquea las respuestas del servidor
 * - Las excepciones se manejan dentro del hilo asíncrono
 * - Escalable: puede manejar múltiples envíos simultáneos
 * - Confiable: Amazon SES maneja la entrega de correos
 */
@Service
public class EmailAsyncService {

    private static final Logger logger = LoggerFactory.getLogger(EmailAsyncService.class);

    @Autowired(required = false)
    private SesClient sesClient;

    @Autowired
    private EmailExecutor emailExecutor;

    @Value("${ses.from.email:noreply@papusbarbershop.com}")
    private String emailFrom;

    /**
     * Valida la configuración de email después de la inicialización.
     */
    @PostConstruct
    public void validateEmailConfiguration() {
        logger.info("=== Validando configuración de Amazon SES ===");
        
        if (sesClient == null) {
            logger.error("⚠️  ADVERTENCIA: SesClient no está configurado.");
            logger.error("⚠️  Los correos de confirmación NO se enviarán.");
            logger.error("⚠️  Para habilitar el envío de correos, configura las siguientes variables de entorno en Railway:");
            logger.error("⚠️    - AWS_SES_ACCESS_KEY (Access Key ID de AWS IAM)");
            logger.error("⚠️    - AWS_SES_SECRET_KEY (Secret Access Key de AWS IAM)");
            logger.error("⚠️    - AWS_SES_REGION (Región de AWS, ej: us-east-2)");
            logger.error("⚠️    - SES_FROM_EMAIL (Email remitente verificado en SES)");
        } else {
            logger.info("✓ SesClient configurado correctamente");
            logger.info("✓ Email remitente: {}", emailFrom);
        }
        
        logger.info("=== Validación de Amazon SES completada ===");
    }

    /**
     * Envía un correo de confirmación de cita de forma ASÍNCRONA.
     * 
     * Este método NO bloquea la ejecución. El correo se envía en segundo plano
     * y cualquier error se registra sin afectar la respuesta al usuario.
     * 
     * @param correos Lista de correos destinatarios
     * @param nombreCliente Nombre del cliente
     * @param fecha Fecha de la cita
     * @param hora Hora de la cita
     * @param barberoNombre Nombre del barbero
     * @param tipoCorteNombre Nombre del tipo de corte
     * @param comentarios Comentarios adicionales
     */
    public void enviarConfirmacionCitaAsync(List<String> correos, String nombreCliente, 
                                           String fecha, String hora, String barberoNombre,
                                           String tipoCorteNombre, String comentarios) {
        
        // Validar que hay correos para enviar
        if (correos == null || correos.isEmpty()) {
            logger.warn("No se proporcionaron correos para enviar la confirmación (asíncrono)");
            return;
        }

        // Filtrar correos vacíos o inválidos
        List<String> correosValidos = correos.stream()
                .filter(c -> c != null && !c.trim().isEmpty())
                .toList();

        if (correosValidos.isEmpty()) {
            logger.warn("No hay correos válidos para enviar la confirmación (asíncrono)");
            return;
        }

        // Ejecutar el envío de forma asíncrona
        emailExecutor.ejecutarEnvioAsincrono(() -> {
            enviarCorreoConfirmacion(correosValidos, nombreCliente, fecha, hora, 
                                   barberoNombre, tipoCorteNombre, comentarios);
        });
        
        logger.info("Tarea de envío de correo de confirmación enviada al pool asíncrono. " +
                   "Destinatarios: {}. El correo se enviará en segundo plano.", correosValidos);
    }

    /**
     * Método genérico para enviar un correo de forma asíncrona.
     * 
     * @param destinatario Correo del destinatario
     * @param asunto Asunto del correo
     * @param mensaje Cuerpo del mensaje
     */
    public void enviarCorreoAsync(String destinatario, String asunto, String mensaje) {
        if (destinatario == null || destinatario.trim().isEmpty()) {
            logger.warn("No se proporcionó destinatario para el correo");
            return;
        }

        emailExecutor.ejecutarEnvioAsincrono(() -> {
            enviarCorreoSimple(destinatario, asunto, mensaje);
        });
        
        logger.info("Tarea de envío de correo genérico enviada al pool asíncrono. " +
                   "Destinatario: {}. El correo se enviará en segundo plano.", destinatario);
    }

    /**
     * Método privado que realiza el envío real del correo de confirmación usando Amazon SES.
     * Este método se ejecuta dentro del hilo asíncrono.
     */
    private void enviarCorreoConfirmacion(List<String> correos, String nombreCliente, 
                                         String fecha, String hora, String barberoNombre,
                                         String tipoCorteNombre, String comentarios) {
        
        if (sesClient == null) {
            logger.warn("SesClient no está configurado. No se enviará el correo.");
            logger.info("Correo que se habría enviado a: {}", correos);
            return;
        }

        try {
            String asunto = "Confirmación de Cita - Papus BarberShop";
            String cuerpoTexto = construirCuerpoEmailTexto(nombreCliente, fecha, hora, barberoNombre, 
                                                          tipoCorteNombre, comentarios);
            String cuerpoHtml = construirCuerpoEmailHtml(nombreCliente, fecha, hora, barberoNombre, 
                                                        tipoCorteNombre, comentarios);

            logger.info("Iniciando envío asíncrono de correos de confirmación. Remitente: {}, Destinatarios: {}", 
                    emailFrom, correos);

            int correosEnviadosExitosamente = 0;
            for (String correo : correos) {
                try {
                    // Construir el mensaje con contenido HTML y texto plano
                    Content subject = Content.builder()
                            .data(asunto)
                            .charset("UTF-8")
                            .build();

                    Content textBody = Content.builder()
                            .data(cuerpoTexto)
                            .charset("UTF-8")
                            .build();

                    Content htmlBody = Content.builder()
                            .data(cuerpoHtml)
                            .charset("UTF-8")
                            .build();

                    Body body = Body.builder()
                            .text(textBody)
                            .html(htmlBody)
                            .build();

                    Message message = Message.builder()
                            .subject(subject)
                            .body(body)
                            .build();

                    Destination destination = Destination.builder()
                            .toAddresses(correo.trim())
                            .build();

                    SendEmailRequest emailRequest = SendEmailRequest.builder()
                            .source(emailFrom)
                            .destination(destination)
                            .message(message)
                            .build();

                    SendEmailResponse response = sesClient.sendEmail(emailRequest);
                    correosEnviadosExitosamente++;
                    logger.info("✓ Correo de confirmación enviado exitosamente a: {} (asíncrono). MessageId: {}", 
                            correo, response.messageId());
                } catch (Exception e) {
                    logger.error("✗ Error al enviar correo a {} (asíncrono): {}", correo, e.getMessage(), e);
                    // Continuar con los demás correos aunque uno falle
                }
            }
            
            logger.info("Proceso de envío asíncrono de correos completado. Total enviados: {}/{}", 
                    correosEnviadosExitosamente, correos.size());
        } catch (Exception e) {
            logger.error("Error crítico en envío asíncrono de correos de confirmación: {}", e.getMessage(), e);
            // No propagar la excepción - ya está dentro del hilo asíncrono
        }
    }

    /**
     * Método privado que realiza el envío real de un correo simple usando Amazon SES.
     * Este método se ejecuta dentro del hilo asíncrono.
     */
    private void enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        if (sesClient == null) {
            logger.warn("SesClient no está configurado. No se enviará el correo.");
            return;
        }

        try {
            Content subject = Content.builder()
                    .data(asunto)
                    .charset("UTF-8")
                    .build();

            Content textBody = Content.builder()
                    .data(mensaje)
                    .charset("UTF-8")
                    .build();

            Body body = Body.builder()
                    .text(textBody)
                    .build();

            Message message = Message.builder()
                    .subject(subject)
                    .body(body)
                    .build();

            Destination destination = Destination.builder()
                    .toAddresses(destinatario.trim())
                    .build();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(emailFrom)
                    .destination(destination)
                    .message(message)
                    .build();

            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            logger.info("✓ Correo enviado exitosamente a: {} (asíncrono). MessageId: {}", 
                    destinatario, response.messageId());
        } catch (Exception e) {
            logger.error("✗ Error al enviar correo a {} (asíncrono): {}", destinatario, e.getMessage(), e);
            // No propagar la excepción - ya está dentro del hilo asíncrono
        }
    }

    /**
     * Construye el cuerpo del correo electrónico de confirmación en formato texto plano.
     */
    private String construirCuerpoEmailTexto(String nombreCliente, String fecha, String hora,
                                            String barberoNombre, String tipoCorteNombre, 
                                            String comentarios) {
        StringBuilder cuerpo = new StringBuilder();
        cuerpo.append("¡Hola ").append(nombreCliente).append("! 👋\n\n");
        cuerpo.append("✨ Su cita ha sido confirmada exitosamente ✨\n\n");
        cuerpo.append("📋 Detalles de la cita:\n");
        cuerpo.append("📅 Fecha: ").append(fecha).append("\n");
        cuerpo.append("🕐 Hora: ").append(hora).append("\n");
        cuerpo.append("💇 Barbero: ").append(barberoNombre).append("\n");
        cuerpo.append("✂️ Tipo de Corte: ").append(tipoCorteNombre).append("\n");
        
        if (comentarios != null && !comentarios.trim().isEmpty()) {
            cuerpo.append("💬 Comentarios: ").append(comentarios).append("\n");
        }
        
        cuerpo.append("\n");
        cuerpo.append("🎯 Esperamos verle pronto en Papus BarberShop 🎯\n\n");
        cuerpo.append("Saludos cordiales,\n");
        cuerpo.append("Equipo Papus BarberShop 💈");
        
        return cuerpo.toString();
    }

    /**
     * Construye el cuerpo del correo electrónico de confirmación en formato HTML.
     */
    private String construirCuerpoEmailHtml(String nombreCliente, String fecha, String hora,
                                           String barberoNombre, String tipoCorteNombre, 
                                           String comentarios) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">");
        html.append("<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">");
        html.append("<h2 style=\"color: #2c3e50;\">¡Hola ").append(escapeHtml(nombreCliente)).append("! 👋</h2>");
        html.append("<p style=\"font-size: 18px; color: #27ae60;\">✨ Su cita ha sido confirmada exitosamente ✨</p>");
        html.append("<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0;\">");
        html.append("<h3 style=\"color: #2c3e50; margin-top: 0;\">📋 Detalles de la cita:</h3>");
        html.append("<p><strong>📅 Fecha:</strong> ").append(escapeHtml(fecha)).append("</p>");
        html.append("<p><strong>🕐 Hora:</strong> ").append(escapeHtml(hora)).append("</p>");
        html.append("<p><strong>💇 Barbero:</strong> ").append(escapeHtml(barberoNombre)).append("</p>");
        html.append("<p><strong>✂️ Tipo de Corte:</strong> ").append(escapeHtml(tipoCorteNombre)).append("</p>");
        
        if (comentarios != null && !comentarios.trim().isEmpty()) {
            html.append("<p><strong>💬 Comentarios:</strong> ").append(escapeHtml(comentarios)).append("</p>");
        }
        
        html.append("</div>");
        html.append("<p style=\"font-size: 16px; color: #2c3e50;\">🎯 Esperamos verle pronto en Papus BarberShop 🎯</p>");
        html.append("<p>Saludos cordiales,<br>Equipo Papus BarberShop 💈</p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    /**
     * Escapa caracteres HTML para prevenir XSS.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}

