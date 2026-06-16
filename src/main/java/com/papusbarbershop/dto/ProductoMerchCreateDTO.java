package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoMerchCreateDTO {
    private String nombre;
    private String categoria;
    private String descripcion;
    private BigDecimal precioBase;
    private Boolean activo = true;
    private Boolean permitePersonalizacion = false;
    private Boolean esNuevo = false;
    private String badge;
    private List<VarianteProductoMerchDTO> variantes = new ArrayList<>();

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
    public List<VarianteProductoMerchDTO> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteProductoMerchDTO> variantes) { this.variantes = variantes; }
}
