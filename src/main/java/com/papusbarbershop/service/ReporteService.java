package com.papusbarbershop.service;

import com.papusbarbershop.dto.DetalleCorteDTO;
import com.papusbarbershop.dto.DetalleVentaProductoDTO;
import com.papusbarbershop.dto.ResumenBarberoDTO;
import com.papusbarbershop.dto.ResumenDiarioDTO;
import com.papusbarbershop.dto.ResumenMensualDTO;
import com.papusbarbershop.entity.Barbero;
import com.papusbarbershop.entity.Producto;
import com.papusbarbershop.repository.BarberoRepository;
import com.papusbarbershop.repository.ServicioRepository;
import com.papusbarbershop.repository.VentaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la generación de reportes y resúmenes.
 */
@Service
public class ReporteService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private VentaProductoRepository ventaProductoRepository;

    @Autowired
    private BarberoRepository barberoRepository;

    public ResumenDiarioDTO generarResumenDiario(LocalDate fecha) {
        ResumenDiarioDTO resumen = new ResumenDiarioDTO();
        resumen.setFecha(fecha);

        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFecha(fecha);
        BigDecimal totalServicios = servicios.stream()
                .map(com.papusbarbershop.entity.Servicio::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalServicios(totalServicios);
        resumen.setCantidadServicios(servicios.size());

        List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByFecha(fecha);
        BigDecimal totalVentas = ventas.stream()
                .map(com.papusbarbershop.entity.VentaProducto::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalVentas(totalVentas);
        resumen.setCantidadVentas(ventas.size());

        BigDecimal totalComisiones = ventas.stream()
                .map(venta -> {
                    Integer comision = 0;
                    if (venta.getProducto() != null) {
                        comision = venta.getProducto().getComision();
                        if (comision == null) comision = 0;
                    }
                    return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(venta.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalComisiones(totalComisiones);

        resumen.setTotalGeneral(totalServicios.add(totalVentas).setScale(2, RoundingMode.HALF_UP));

        BigDecimal totalDescuentosServicios = servicios.stream()
                .map(s -> (s.getPrecioOriginal() != null ? s.getPrecioOriginal() : BigDecimal.ZERO)
                        .subtract(s.getPrecio() != null ? s.getPrecio() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuentosVentas = ventas.stream()
                .map(v -> (v.getImporteOriginal() != null ? v.getImporteOriginal() : BigDecimal.ZERO)
                        .subtract(v.getImporte() != null ? v.getImporte() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        resumen.setTotalDescuentosAplicados(totalDescuentosServicios.add(totalDescuentosVentas).setScale(2, RoundingMode.HALF_UP));

        List<ResumenBarberoDTO> resumenBarberos = calcularResumenBarberos(fecha, fecha);
        resumen.setResumenBarberos(resumenBarberos);
        resumen.setTotalGananciaBarberia(sumarGananciaBarberia(resumenBarberos));

        return resumen;
    }

    public ResumenMensualDTO generarResumenMensual(YearMonth yearMonth) {
        ResumenMensualDTO resumen = new ResumenMensualDTO();
        resumen.setMes(yearMonth);

        LocalDate fechaInicio = yearMonth.atDay(1);
        LocalDate fechaFin = yearMonth.atEndOfMonth();

        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalServicios = servicios.stream()
                .map(com.papusbarbershop.entity.Servicio::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalServicios(totalServicios);
        resumen.setCantidadServicios(servicios.size());

        List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalVentas = ventas.stream()
                .map(com.papusbarbershop.entity.VentaProducto::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalVentas(totalVentas);
        resumen.setCantidadVentas(ventas.size());

        BigDecimal totalComisiones = ventas.stream()
                .map(venta -> {
                    Integer comision = 0;
                    if (venta.getProducto() != null) {
                        comision = venta.getProducto().getComision();
                        if (comision == null) comision = 0;
                    }
                    return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(venta.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalComisiones(totalComisiones);

        resumen.setTotalGeneral(totalServicios.add(totalVentas).setScale(2, RoundingMode.HALF_UP));

        BigDecimal totalDescuentosServicios = servicios.stream()
                .map(s -> (s.getPrecioOriginal() != null ? s.getPrecioOriginal() : BigDecimal.ZERO)
                        .subtract(s.getPrecio() != null ? s.getPrecio() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuentosVentas = ventas.stream()
                .map(v -> (v.getImporteOriginal() != null ? v.getImporteOriginal() : BigDecimal.ZERO)
                        .subtract(v.getImporte() != null ? v.getImporte() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        resumen.setTotalDescuentosAplicados(totalDescuentosServicios.add(totalDescuentosVentas).setScale(2, RoundingMode.HALF_UP));

        List<ResumenBarberoDTO> resumenBarberos = calcularResumenBarberos(fechaInicio, fechaFin);
        resumen.setResumenBarberos(resumenBarberos);
        resumen.setTotalGananciaBarberia(sumarGananciaBarberia(resumenBarberos));

        return resumen;
    }

    public ResumenDiarioDTO generarResumenPorFecha(LocalDate fecha) {
        return generarResumenDiario(fecha);
    }

    private List<ResumenBarberoDTO> calcularResumenBarberos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Barbero> barberos = barberoRepository.findAll();
        List<ResumenBarberoDTO> resumenBarberos = new ArrayList<>();

        for (Barbero barbero : barberos) {
            ResumenBarberoDTO resumen = new ResumenBarberoDTO();
            resumen.setBarberoId(barbero.getId());
            resumen.setBarberoNombre(barbero.getNombre());
            resumen.setPorcentajeServicio(barbero.getPorcentajeServicio());

            BigDecimal totalServicios = servicioRepository.calcularTotalPorBarbero(barbero.getId(), fechaInicio, fechaFin);
            resumen.setTotalServicios(totalServicios);

            List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByBarberoId(barbero.getId());
            servicios = servicios.stream()
                    .filter(s -> !s.getFecha().isBefore(fechaInicio) && !s.getFecha().isAfter(fechaFin))
                    .toList();
            resumen.setCantidadServicios(servicios.size());
            resumen.setDetallesCortes(servicios.stream().map(servicio -> {
                DetalleCorteDTO detalle = new DetalleCorteDTO();
                detalle.setFecha(servicio.getFecha());
                detalle.setHora(servicio.getHora());
                detalle.setTipoCorte(servicio.getTipoCorte());
                detalle.setMetodoPago(servicio.getMetodoPago());
                detalle.setPrecioOriginal(servicio.getPrecioOriginal());
                detalle.setDescuentoPorcentaje(servicio.getDescuentoPorcentaje());
                detalle.setPrecio(servicio.getPrecio());
                return detalle;
            }).toList());

            BigDecimal totalVentas = ventaProductoRepository.calcularTotalPorBarbero(barbero.getId(), fechaInicio, fechaFin);
            resumen.setTotalVentas(totalVentas);

            List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByBarberoId(barbero.getId());
            ventas = ventas.stream()
                    .filter(v -> !v.getFecha().isBefore(fechaInicio) && !v.getFecha().isAfter(fechaFin))
                    .toList();
            resumen.setCantidadVentas(ventas.size());
            resumen.setDetallesVentas(ventas.stream().map(venta -> {
                DetalleVentaProductoDTO detalle = new DetalleVentaProductoDTO();
                detalle.setFecha(venta.getFecha());
                detalle.setHora(venta.getHora());
                Producto producto = venta.getProducto();
                String nombreProducto = producto != null ? producto.getNombre() : venta.getProductoNombre();
                detalle.setProducto(nombreProducto != null ? nombreProducto : "Producto eliminado");
                detalle.setCantidad(venta.getCantidad());
                detalle.setPrecioUnitario(venta.getPrecioUnitario());
                detalle.setImporteOriginal(venta.getImporteOriginal());
                detalle.setDescuentoPorcentaje(venta.getDescuentoPorcentaje());
                detalle.setImporte(venta.getImporte());
                detalle.setMetodoPago(venta.getMetodoPago());
                return detalle;
            }).toList());

            BigDecimal totalComisiones = ventas.stream()
                    .map(venta -> {
                        Integer comision = 0;
                        if (venta.getProducto() != null) {
                            comision = venta.getProducto().getComision();
                            if (comision == null) comision = 0;
                        }
                        return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(venta.getCantidad()));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            resumen.setTotalComisiones(totalComisiones);

            BigDecimal totalGenerado = totalServicios.add(totalVentas);
            resumen.setTotalGenerado(totalGenerado);

            BigDecimal porcentaje = barbero.getPorcentajeServicio().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal pagoPorServicios = totalServicios.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pagoBarbero = pagoPorServicios.add(totalComisiones).setScale(2, RoundingMode.HALF_UP);
            resumen.setPagoBarbero(pagoBarbero);

            BigDecimal gananciaBarberia = totalGenerado.subtract(pagoBarbero).setScale(2, RoundingMode.HALF_UP);
            resumen.setGananciaBarberia(gananciaBarberia);

            resumenBarberos.add(resumen);
        }

        return resumenBarberos;
    }

    private BigDecimal sumarGananciaBarberia(List<ResumenBarberoDTO> resumenBarberos) {
        return resumenBarberos.stream()
                .map(ResumenBarberoDTO::getGananciaBarberia)
                .filter(g -> g != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
