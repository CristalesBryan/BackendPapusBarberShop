package com.papusbarbershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos_merch")
public class ProductoMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "permite_personalizacion", nullable = false)
    private Boolean permitePersonalizacion = false;

    @Column(name = "es_nuevo", nullable = false)
    private Boolean esNuevo = false;

    @Column(length = 50)
    private String badge;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orden ASC")
    private List<ImagenProductoMerch> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VarianteProductoMerch> variantes = new ArrayList<>();

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
    public List<ImagenProductoMerch> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenProductoMerch> imagenes) { this.imagenes = imagenes; }
    public List<VarianteProductoMerch> getVariantes() { return variantes; }
    public void setVariantes(List<VarianteProductoMerch> variantes) { this.variantes = variantes; }
}
