package com.papusbarbershop.dto;

public class TopProductoMerchDTO {
    private String nombre;
    private int cantidad;
    private java.math.BigDecimal total;

    public TopProductoMerchDTO() {}
    public TopProductoMerchDTO(String nombre, int cantidad, java.math.BigDecimal total) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.total = total;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public java.math.BigDecimal getTotal() { return total; }
    public void setTotal(java.math.BigDecimal total) { this.total = total; }
}
