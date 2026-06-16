package com.papusbarbershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "ventas_merch")
public class VentaMerch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoMerch producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private VarianteProductoMerch variante;

    @Column(name = "producto_nombre", nullable = false, length = 200)
    private String productoNombre;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(length = 10)
    private String talla;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @NotBlank
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @Column(name = "personalizacion_nombre", length = 100)
    private String personalizacionNombre;

    @Column(name = "personalizacion_numero", length = 20)
    private String personalizacionNumero;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(nullable = false)
    private LocalTime hora;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductoMerch getProducto() { return producto; }
    public void setProducto(ProductoMerch producto) { this.producto = producto; }
    public VarianteProductoMerch getVariante() { return variante; }
    public void setVariante(VarianteProductoMerch variante) { this.variante = variante; }
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
