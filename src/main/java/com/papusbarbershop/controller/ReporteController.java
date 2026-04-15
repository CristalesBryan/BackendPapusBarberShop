package com.papusbarbershop.controller;

import com.papusbarbershop.dto.ResumenDiarioDTO;
import com.papusbarbershop.dto.ResumenMensualDTO;
import com.papusbarbershop.service.ReporteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Controlador para la generación de reportes.
 */
@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    @Autowired
    private ReporteService reporteService;

    /**
     * Obtiene el resumen diario.
     * Si se envía {@code fecha} (YYYY-MM-DD), se usa esa fecha en el calendario del negocio (navegador).
     * Si no, se usa la fecha actual del servidor (puede desfasarse respecto al usuario en UTC).
     *
     * @param fecha opcional, día a consultar
     * @return Resumen diario
     */
    @GetMapping("/diario")
    public ResponseEntity<ResumenDiarioDTO> getResumenDiario(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate dia = fecha != null ? fecha : LocalDate.now();
        try {
            ResumenDiarioDTO resumen = reporteService.generarResumenDiario(dia);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            log.error("GET /reportes/diario fecha={}", dia, e);
            throw e;
        }
    }

    /**
     * Obtiene el resumen mensual.
     * 
     * @param mes Mes en formato YYYY-MM (opcional, por defecto el mes actual)
     * @return Resumen mensual
     */
    @GetMapping("/mensual")
    public ResponseEntity<ResumenMensualDTO> getResumenMensual(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        if (mes == null) {
            mes = YearMonth.now();
        }
        try {
            ResumenMensualDTO resumen = reporteService.generarResumenMensual(mes);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            log.error("GET /reportes/mensual mes={}", mes, e);
            throw e;
        }
    }

    /**
     * Obtiene el resumen para una fecha específica.
     * 
     * @param fecha Fecha del resumen
     * @return Resumen diario
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<ResumenDiarioDTO> getResumenPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        ResumenDiarioDTO resumen = reporteService.generarResumenPorFecha(fecha);
        return ResponseEntity.ok(resumen);
    }
}

