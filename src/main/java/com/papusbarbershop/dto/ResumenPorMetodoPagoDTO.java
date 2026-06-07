package com.papusbarbershop.dto;

/**
 * Resumen de cortes agrupados por método de pago en un rango de fechas.
 */
public class ResumenPorMetodoPagoDTO {

    private ResumenMetodoPagoItemDTO efectivo;
    private ResumenMetodoPagoItemDTO tarjeta;

    public ResumenPorMetodoPagoDTO() {
        this.efectivo = new ResumenMetodoPagoItemDTO();
        this.tarjeta = new ResumenMetodoPagoItemDTO();
    }

    public ResumenMetodoPagoItemDTO getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(ResumenMetodoPagoItemDTO efectivo) {
        this.efectivo = efectivo;
    }

    public ResumenMetodoPagoItemDTO getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(ResumenMetodoPagoItemDTO tarjeta) {
        this.tarjeta = tarjeta;
    }
}
