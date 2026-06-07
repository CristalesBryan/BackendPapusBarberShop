package com.papusbarbershop.dto;

import java.math.BigDecimal;

/**
 * Totales agregados para un método de pago (cantidad de cortes y monto).
 */
public class ResumenMetodoPagoItemDTO {

    private int cantidad;
    private BigDecimal total;

    public ResumenMetodoPagoItemDTO() {
        this.cantidad = 0;
        this.total = BigDecimal.ZERO;
    }

    public ResumenMetodoPagoItemDTO(int cantidad, BigDecimal total) {
        this.cantidad = cantidad;
        this.total = total;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
