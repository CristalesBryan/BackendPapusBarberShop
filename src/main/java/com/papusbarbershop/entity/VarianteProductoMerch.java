package com.papusbarbershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "variantes_producto_merch")
public class VarianteProductoMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoMerch producto;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String talla;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer stock = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductoMerch getProducto() { return producto; }
    public void setProducto(ProductoMerch producto) { this.producto = producto; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
