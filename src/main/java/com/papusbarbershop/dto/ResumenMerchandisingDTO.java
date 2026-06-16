package com.papusbarbershop.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ResumenMerchandisingDTO {
    private BigDecimal totalVendido = BigDecimal.ZERO;
    private int totalUnidades;
    private String productoMasVendido;
    private List<VentaMerchDTO> ventas = new ArrayList<>();
    private List<TopProductoMerchDTO> topProductos = new ArrayList<>();
    private List<CategoriaMerchDTO> distribucionCategoria = new ArrayList<>();

    public BigDecimal getTotalVendido() { return totalVendido; }
    public void setTotalVendido(BigDecimal totalVendido) { this.totalVendido = totalVendido; }
    public int getTotalUnidades() { return totalUnidades; }
    public void setTotalUnidades(int totalUnidades) { this.totalUnidades = totalUnidades; }
    public String getProductoMasVendido() { return productoMasVendido; }
    public void setProductoMasVendido(String productoMasVendido) { this.productoMasVendido = productoMasVendido; }
    public List<VentaMerchDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaMerchDTO> ventas) { this.ventas = ventas; }
    public List<TopProductoMerchDTO> getTopProductos() { return topProductos; }
    public void setTopProductos(List<TopProductoMerchDTO> topProductos) { this.topProductos = topProductos; }
    public List<CategoriaMerchDTO> getDistribucionCategoria() { return distribucionCategoria; }
    public void setDistribucionCategoria(List<CategoriaMerchDTO> distribucionCategoria) { this.distribucionCategoria = distribucionCategoria; }
}
