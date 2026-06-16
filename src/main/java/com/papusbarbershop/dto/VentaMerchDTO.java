package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class VentaMerchDTO {
    private Long id;
    private String productoNombre;
    private String categoria;
    private String talla;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private String metodoPago;
    private String personalizacionNombre;
    private String personalizacionNumero;
    private LocalDate fecha;
    private LocalTime hora;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getPersonalizacionNombre() { return personalizacionNombre; }
    public void setPersonalizacionNombre(String personalizacionNombre) { this.personalizacionNombre = personalizacionNombre; }
    public String getPersonalizacionNumero() { return personalizacionNumero; }
    public void setPersonalizacionNumero(String personalizacionNumero) { this.personalizacionNumero = personalizacionNumero; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
}
