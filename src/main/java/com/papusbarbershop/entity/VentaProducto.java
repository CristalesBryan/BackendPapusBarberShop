package com.papusbarbershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa una venta de producto.
 */
@Entity
@Table(name = "ventas_productos")
public class VentaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @NotNull(message = "El barbero es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "barbero_id", nullable = false)
    private Barbero barbero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = true)
    private Producto producto;

    @Column(name = "producto_nombre", nullable = true, length = 200)
    private String productoNombre;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull(message = "El importe original es obligatorio")
    @DecimalMin(value = "0.0", message = "El importe original debe ser mayor o igual a 0")
    @Column(name = "importe_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeOriginal;

    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @NotNull(message = "El importe final es obligatorio")
    @DecimalMin(value = "0.0", message = "El importe final debe ser mayor o igual a 0")
    @Column(name = "importe", nullable = false, precision = 10, scale = 2)
    private BigDecimal importe;

    @NotNull(message = "El stock antes es obligatorio")
    @Column(name = "stock_antes", nullable = false)
    private Integer stockAntes;

    @NotNull(message = "El stock despues es obligatorio")
    @Column(name = "stock_despues", nullable = false)
    private Integer stockDespues;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    public VentaProducto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public Barbero getBarbero() { return barbero; }
    public void setBarbero(Barbero barbero) { this.barbero = barbero; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getImporteOriginal() { return importeOriginal; }
    public void setImporteOriginal(BigDecimal importeOriginal) { this.importeOriginal = importeOriginal; }
    public BigDecimal getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public Integer getStockAntes() { return stockAntes; }
    public void setStockAntes(Integer stockAntes) { this.stockAntes = stockAntes; }
    public Integer getStockDespues() { return stockDespues; }
    public void setStockDespues(Integer stockDespues) { this.stockDespues = stockDespues; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    @PostLoad
    void normalizarMontos() {
        if (precioUnitario == null) {
            precioUnitario = BigDecimal.ZERO;
        }
        if (importeOriginal == null) {
            importeOriginal = BigDecimal.ZERO;
        }
        if (descuentoPorcentaje == null) {
            descuentoPorcentaje = BigDecimal.ZERO;
        }
        if (importe == null) {
            importe = BigDecimal.ZERO;
        }
    }
}
