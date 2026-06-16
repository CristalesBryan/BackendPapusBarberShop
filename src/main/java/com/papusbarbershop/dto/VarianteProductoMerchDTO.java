package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VarianteProductoMerchDTO {
    private Long id;
    private String talla;
    private BigDecimal precio;
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
