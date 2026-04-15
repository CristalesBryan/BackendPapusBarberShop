package com.papusbarbershop.service;

import com.papusbarbershop.dto.ServicioCreateDTO;
import com.papusbarbershop.dto.ServicioDTO;
import com.papusbarbershop.entity.Barbero;
import com.papusbarbershop.entity.Servicio;
import com.papusbarbershop.exception.RecursoNoEncontradoException;
import com.papusbarbershop.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de servicios (cortes).
 */
@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private BarberoService barberoService;

    @Transactional
    public ServicioDTO create(ServicioCreateDTO servicioCreateDTO) {
        Barbero barbero = barberoService.findEntityById(servicioCreateDTO.getBarberoId());

        BigDecimal precioOriginal = servicioCreateDTO.getPrecio() != null ? servicioCreateDTO.getPrecio() : BigDecimal.ZERO;
        BigDecimal descuentoPorcentaje = normalizarDescuento(servicioCreateDTO.getDescuentoPorcentaje());
        BigDecimal precioFinal = aplicarDescuento(precioOriginal, descuentoPorcentaje);

        Servicio servicio = new Servicio();
        servicio.setFecha(servicioCreateDTO.getFecha());
        servicio.setHora(servicioCreateDTO.getHora());
        servicio.setBarbero(barbero);
        servicio.setTipoCorte(servicioCreateDTO.getTipoCorte());
        servicio.setMetodoPago(servicioCreateDTO.getMetodoPago());
        servicio.setPrecioOriginal(precioOriginal.setScale(2, RoundingMode.HALF_UP));
        servicio.setDescuentoPorcentaje(descuentoPorcentaje);
        servicio.setPrecio(precioFinal);

        Servicio saved = servicioRepository.save(servicio);
        return convertToDTO(saved);
    }

    public List<ServicioDTO> findAll() {
        return servicioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServicioDTO> findByFecha(LocalDate fecha) {
        return servicioRepository.findByFecha(fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ServicioDTO findById(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio con ID " + id + " no encontrado"));
        return convertToDTO(servicio);
    }

    @Transactional
    public ServicioDTO update(Long id, ServicioCreateDTO servicioCreateDTO) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio con ID " + id + " no encontrado"));

        Barbero barbero = barberoService.findEntityById(servicioCreateDTO.getBarberoId());
        BigDecimal precioOriginal = servicioCreateDTO.getPrecio() != null ? servicioCreateDTO.getPrecio() : BigDecimal.ZERO;
        BigDecimal descuentoPorcentaje = normalizarDescuento(servicioCreateDTO.getDescuentoPorcentaje());
        BigDecimal precioFinal = aplicarDescuento(precioOriginal, descuentoPorcentaje);

        servicio.setFecha(servicioCreateDTO.getFecha());
        servicio.setHora(servicioCreateDTO.getHora());
        servicio.setBarbero(barbero);
        servicio.setTipoCorte(servicioCreateDTO.getTipoCorte());
        servicio.setMetodoPago(servicioCreateDTO.getMetodoPago());
        servicio.setPrecioOriginal(precioOriginal.setScale(2, RoundingMode.HALF_UP));
        servicio.setDescuentoPorcentaje(descuentoPorcentaje);
        servicio.setPrecio(precioFinal);

        Servicio saved = servicioRepository.save(servicio);
        return convertToDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Servicio con ID " + id + " no encontrado"));
        servicioRepository.delete(servicio);
    }

    private ServicioDTO convertToDTO(Servicio servicio) {
        return new ServicioDTO(
                servicio.getId(),
                servicio.getFecha(),
                servicio.getHora(),
                servicio.getBarbero().getId(),
                servicio.getBarbero().getNombre(),
                servicio.getTipoCorte(),
                servicio.getMetodoPago(),
                servicio.getPrecioOriginal(),
                servicio.getDescuentoPorcentaje(),
                servicio.getPrecio()
        );
    }

    private BigDecimal normalizarDescuento(BigDecimal descuento) {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return descuento.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal aplicarDescuento(BigDecimal monto, BigDecimal descuentoPorcentaje) {
        BigDecimal factor = BigDecimal.ONE.subtract(descuentoPorcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            factor = BigDecimal.ZERO;
        }
        return monto.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
