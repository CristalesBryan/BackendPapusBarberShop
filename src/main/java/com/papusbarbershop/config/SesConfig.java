package com.papusbarbershop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

import jakarta.annotation.PostConstruct;

/**
 * Configuración para Amazon SES (Simple Email Service).
 * 
 * Este bean configura el cliente SES usando credenciales de AWS IAM
 * obtenidas de variables de entorno.
 */
@Configuration
public class SesConfig {

    private static final Logger logger = LoggerFactory.getLogger(SesConfig.class);

    @Value("${aws.ses.access-key:}")
    private String accessKey;

    @Value("${aws.ses.secret-key:}")
    private String secretKey;

    @Value("${aws.ses.region:us-east-1}")
    private String region;

    /**
     * Valida la configuración de SES después de la inicialización.
     */
    @PostConstruct
    public void validateConfiguration() {
        logger.info("=== Validando configuración de Amazon SES ===");
        logger.info("Región: {}", region);
        logger.info("Access Key presente: {}", (accessKey != null && !accessKey.isEmpty()));
        logger.info("Secret Key presente: {}", (secretKey != null && !secretKey.isEmpty()));
        
        if (accessKey == null || accessKey.isEmpty()) {
            logger.error("ERROR: AWS_SES_ACCESS_KEY no está configurada");
            throw new IllegalStateException("AWS_SES_ACCESS_KEY no está configurada");
        }
        
        if (secretKey == null || secretKey.isEmpty()) {
            logger.error("ERROR: AWS_SES_SECRET_KEY no está configurada");
            throw new IllegalStateException("AWS_SES_SECRET_KEY no está configurada");
        }
        
        // Validar formato de región
        try {
            Region.of(region);
            logger.info("Región válida: {}", region);
        } catch (Exception e) {
            logger.error("ERROR: Región inválida: {}", region, e);
            throw new IllegalStateException("Región AWS inválida: " + region, e);
        }
        
        logger.info("=== Configuración de SES validada correctamente ===");
    }

    /**
     * Crea y configura el cliente SES.
     * 
     * @return Cliente SES configurado
     */
    @Bean
    public SesClient sesClient() {
        logger.info("Creando SesClient con región: {}", region);
        
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        SesClient client = SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        
        logger.info("SesClient creado exitosamente");
        return client;
    }
}

