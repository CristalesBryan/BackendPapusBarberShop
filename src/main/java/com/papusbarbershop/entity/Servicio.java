package com.papusbarbershop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad que representa un servicio (corte) realizado en la barberia.
 */
@Entity
@Table(name = "servicios")
public class Servicio {

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

    @NotBlank(message = "El tipo de corte es obligatorio")
    @Column(name = "tipo_corte", nullable = false, length = 100)
    private String tipoCorte;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    @NotNull(message = "El precio original es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio original debe ser mayor o igual a 0")
    @Column(name = "precio_original", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioOriginal;

    @NotNull(message = "El descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @NotNull(message = "El precio final es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio final debe ser mayor o igual a 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public Servicio() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public Barbero getBarbero() { return barbero; }
    public void setBarbero(Barbero barbero) { this.barbero = barbero; }
    public String getTipoCorte() { return tipoCorte; }
    public void setTipoCorte(String tipoCorte) { this.tipoCorte = tipoCorte; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public BigDecimal getPrecioOriginal() { return precioOriginal; }
    public void setPrecioOriginal(BigDecimal precioOriginal) { this.precioOriginal = precioOriginal; }
    public BigDecimal getDescuentoPorcentaje() { return descuentoPorcentaje; }
    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) { this.descuentoPorcentaje = descuentoPorcentaje; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
