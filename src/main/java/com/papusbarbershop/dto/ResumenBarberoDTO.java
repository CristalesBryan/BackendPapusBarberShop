package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para resúmenes por barbero.
 */
public class ResumenBarberoDTO {

    private Long barberoId;
    private String barberoNombre;
    private BigDecimal porcentajeServicio;
    private BigDecimal totalServicios;
    /** Suma de importe_original de ventas (antes de descuento). */
    private BigDecimal totalVentasImporteOriginal;
    /** Suma de importe final de ventas (con descuento aplicado). */
    private BigDecimal totalVentas;
    private BigDecimal totalComisiones;
    private BigDecimal totalGenerado;
    private BigDecimal pagoBarbero;
    /** Lo que queda para la barbería por servicios (totalServicios - pago % servicios). */
    private BigDecimal gananciaBarberia;
    private Integer cantidadServicios;
    private Integer cantidadVentas;
    private List<DetalleCorteDTO> detallesCortes = new ArrayList<>();
    private List<DetalleVentaProductoDTO> detallesVentas = new ArrayList<>();

    // ==================== CONSTRUCTORES ====================

    public ResumenBarberoDTO() {
    }

    // ==================== GETTERS Y SETTERS ====================

    public Long getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(Long barberoId) {
        this.barberoId = barberoId;
    }

    public String getBarberoNombre() {
        return barberoNombre;
    }

    public void setBarberoNombre(String barberoNombre) {
        this.barberoNombre = barberoNombre;
    }

    public BigDecimal getPorcentajeServicio() {
        return porcentajeServicio;
    }

    public void setPorcentajeServicio(BigDecimal porcentajeServicio) {
        this.porcentajeServicio = porcentajeServicio;
    }

    public BigDecimal getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(BigDecimal totalServicios) {
        this.totalServicios = totalServicios;
    }

    public BigDecimal getTotalVentasImporteOriginal() {
        return totalVentasImporteOriginal;
    }

    public void setTotalVentasImporteOriginal(BigDecimal totalVentasImporteOriginal) {
        this.totalVentasImporteOriginal = totalVentasImporteOriginal;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalGenerado() {
        return totalGenerado;
    }

    public void setTotalGenerado(BigDecimal totalGenerado) {
        this.totalGenerado = totalGenerado;
    }

    public BigDecimal getPagoBarbero() {
        return pagoBarbero;
    }

    public void setPagoBarbero(BigDecimal pagoBarbero) {
        this.pagoBarbero = pagoBarbero;
    }

    public BigDecimal getGananciaBarberia() {
        return gananciaBarberia;
    }

    public void setGananciaBarberia(BigDecimal gananciaBarberia) {
        this.gananciaBarberia = gananciaBarberia;
    }

    public Integer getCantidadServicios() {
        return cantidadServicios;
    }

    public void setCantidadServicios(Integer cantidadServicios) {
        this.cantidadServicios = cantidadServicios;
    }

    public Integer getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(Integer cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public BigDecimal getTotalComisiones() {
        return totalComisiones;
    }

    public void setTotalComisiones(BigDecimal totalComisiones) {
        this.totalComisiones = totalComisiones;
    }

    public List<DetalleCorteDTO> getDetallesCortes() {
        return detallesCortes;
    }

    public void setDetallesCortes(List<DetalleCorteDTO> detallesCortes) {
        this.detallesCortes = detallesCortes;
    }

    public List<DetalleVentaProductoDTO> getDetallesVentas() {
        return detallesVentas;
    }

    public void setDetallesVentas(List<DetalleVentaProductoDTO> detallesVentas) {
        this.detallesVentas = detallesVentas;
    }
}

