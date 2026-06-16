package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoMerchDTO {
    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private BigDecimal precioBase;
    private Boolean activo;
    private Boolean permitePersonalizacion;
    private Boolean esNuevo;
    private String badge;
    private Integer stockTotal;
    private BigDecimal precioMin;
    private BigDecimal precioMax;
    private List<ImagenProductoMerchDTO> imagenes = new ArrayList<>();
    private List<VarianteProductoMerchDTO> variantes = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public Boolean getPermitePersonalizacion() { return permitePersonalizacion; }
    public void setPermitePersonalizacion(Boolean permitePersonalizacion) { this.permitePersonalizacion = permitePersonalizacion; }
    public Boolean getEsNuevo() { return esNuevo; }
    public void setEsNuevo(Boolean esNuevo) { this.esNuevo = esNuevo; }
    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
    public BigDecimal getPrecioMin() { return precioMin; }
    public void setPrecioMin(BigDecimal precioMin) { this.precioMin = precioMin; }
    public BigDecimal getPrecioMax() { return precioMax; }
    public void setPrecioMax(BigDecimal precioMax) { this.precioMax = precioMax; }
    public List<ImagenProductoMerchDTO> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenProductoMerchDTO> imagenes) { this.imagenes = imagenes; }
    public List<VarianteProductoMerchDTO> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteProductoMerchDTO> variantes) { this.variantes = variantes; }
}
