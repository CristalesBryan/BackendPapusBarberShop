package com.papusbarbershop.dto;

import java.math.BigDecimal;

/**
 * Rendimiento de un barbero desglosado por método de pago en un período.
 */
public class ResumenBarberoPagoDTO {

    private Long barberoId;
    private String barberoNombre;
    private int cortesEfectivo;
    private int cortesTarjeta;
    private int totalCortes;
    private BigDecimal montoTotal;
    private BigDecimal comisionCalculada;

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

    public int getCortesEfectivo() {
        return cortesEfectivo;
    }

    public void setCortesEfectivo(int cortesEfectivo) {
        this.cortesEfectivo = cortesEfectivo;
    }

    public int getCortesTarjeta() {
        return cortesTarjeta;
    }

    public void setCortesTarjeta(int cortesTarjeta) {
        this.cortesTarjeta = cortesTarjeta;
    }

    public int getTotalCortes() {
        return totalCortes;
    }

    public void setTotalCortes(int totalCortes) {
        this.totalCortes = totalCortes;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getComisionCalculada() {
        return comisionCalculada;
    }

    public void setComisionCalculada(BigDecimal comisionCalculada) {
        this.comisionCalculada = comisionCalculada;
    }
}
