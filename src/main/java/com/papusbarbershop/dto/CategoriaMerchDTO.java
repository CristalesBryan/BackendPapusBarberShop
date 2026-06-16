package com.papusbarbershop.dto;

import java.math.BigDecimal;

public class CategoriaMerchDTO {
    private String categoria;
    private int cantidad;
    private BigDecimal total;

    public CategoriaMerchDTO() {}
    public CategoriaMerchDTO(String categoria, int cantidad, BigDecimal total) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
