package com.papusbarbershop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenes_producto_merch")
public class ImagenProductoMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoMerch producto;

    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;

    @Column(nullable = false, length = 1000)
    private String url;

    @Column(nullable = false)
    private Integer orden = 0;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductoMerch getProducto() { return producto; }
    public void setProducto(ProductoMerch producto) { this.producto = producto; }
    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
