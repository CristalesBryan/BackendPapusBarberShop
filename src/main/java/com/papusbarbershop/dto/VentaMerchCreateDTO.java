package com.papusbarbershop.dto;

import java.math.BigDecimal;

public class VentaMerchCreateDTO {
    private Long productoId;
    private Long varianteId;
    private Integer cantidad;
    private String metodoPago;
    private String personalizacionNombre;
    private String personalizacionNumero;

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Long getVarianteId() { return varianteId; }
    public void setVarianteId(Long varianteId) { this.varianteId = varianteId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getPersonalizacionNombre() { return personalizacionNombre; }
    public void setPersonalizacionNombre(String personalizacionNombre) { this.personalizacionNombre = personalizacionNombre; }
    public String getPersonalizacionNumero() { return personalizacionNumero; }
    public void setPersonalizacionNumero(String personalizacionNumero) { this.personalizacionNumero = personalizacionNumero; }
}
