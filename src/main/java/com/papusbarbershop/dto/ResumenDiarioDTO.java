package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para resúmenes diarios.
 */
public class ResumenDiarioDTO {

    private LocalDate fecha;
    private BigDecimal totalServicios;
    private BigDecimal totalVentas;
    private BigDecimal totalComisiones;
    private BigDecimal totalGeneral;`r`n    private BigDecimal totalDescuentosAplicados;
    /** Suma de lo que queda para la barbería por cada barbero (después de pagos) */
    private BigDecimal totalGananciaBarberia;
    private Integer cantidadServicios;
    private Integer cantidadVentas;
    private List<ResumenBarberoDTO> resumenBarberos;

    // ==================== CONSTRUCTORES ====================

    public ResumenDiarioDTO() {
    }

    // ==================== GETTERS Y SETTERS ====================

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotalServicios() {
        return totalServicios;
    }

    public void setTotalServicios(BigDecimal totalServicios) {
        this.totalServicios = totalServicios;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalGeneral() {
        return totalGeneral;
    }

    public void setTotalGeneral(BigDecimal totalGeneral) {
        this.totalGeneral = totalGeneral;
    }

    public BigDecimal getTotalDescuentosAplicados() {`r`n        return totalDescuentosAplicados;`r`n    }`r`n`r`n    public void setTotalDescuentosAplicados(BigDecimal totalDescuentosAplicados) {`r`n        this.totalDescuentosAplicados = totalDescuentosAplicados;`r`n    }`r`n`r`n    public BigDecimal getTotalGananciaBarberia() {
        return totalGananciaBarberia;
    }

    public void setTotalGananciaBarberia(BigDecimal totalGananciaBarberia) {
        this.totalGananciaBarberia = totalGananciaBarberia;
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

    public List<ResumenBarberoDTO> getResumenBarberos() {
        return resumenBarberos;
    }

    public void setResumenBarberos(List<ResumenBarberoDTO> resumenBarberos) {
        this.resumenBarberos = resumenBarberos;
    }
}

