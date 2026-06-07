package com.papusbarbershop.service;

import com.papusbarbershop.dto.DetalleCorteDTO;
import com.papusbarbershop.dto.DetalleReporteItemDTO;
import com.papusbarbershop.dto.DetalleVentaProductoDTO;
import com.papusbarbershop.dto.ResumenBarberoDTO;
import com.papusbarbershop.dto.ResumenBarberoPagoDTO;
import com.papusbarbershop.dto.ResumenDiarioDTO;
import com.papusbarbershop.dto.ResumenMetodoPagoItemDTO;
import com.papusbarbershop.dto.ResumenMensualDTO;
import com.papusbarbershop.dto.ResumenPorMetodoPagoDTO;
import com.papusbarbershop.entity.Barbero;
import com.papusbarbershop.entity.Producto;
import com.papusbarbershop.repository.BarberoRepository;
import com.papusbarbershop.repository.ServicioRepository;
import com.papusbarbershop.repository.VentaProductoRepository;
import com.papusbarbershop.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Servicio para la generacion de reportes y resumenes.
 */
@Service
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private VentaProductoRepository ventaProductoRepository;

    @Autowired
    private BarberoRepository barberoRepository;

    public ResumenDiarioDTO generarResumenDiario(LocalDate fecha) {
        try {
            return generarResumenDiarioInternal(fecha);
        } catch (Exception e) {
            log.error("generarResumenDiario fallo para fecha={}", fecha, e);
            throw e;
        }
    }

    private ResumenDiarioDTO generarResumenDiarioInternal(LocalDate fecha) {
        ResumenDiarioDTO resumen = new ResumenDiarioDTO();
        resumen.setFecha(fecha);

        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFecha(fecha);
        BigDecimal totalServicios = servicios.stream()
                .map(s -> BigDecimalUtil.nvl(s.getPrecio()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalServicios(totalServicios);
        resumen.setCantidadServicios(servicios.size());

        List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByFecha(fecha);
        BigDecimal totalVentas = ventas.stream()
                .map(v -> BigDecimalUtil.nvl(v.getImporte()))
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
                    return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(venta.getCantidad())));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalComisiones(totalComisiones);

        resumen.setTotalGeneral(totalServicios.add(totalVentas).setScale(2, RoundingMode.HALF_UP));

        BigDecimal totalDescuentosServicios = servicios.stream()
                .map(s -> BigDecimalUtil.nvl(s.getPrecioOriginal()).subtract(BigDecimalUtil.nvl(s.getPrecio())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuentosVentas = ventas.stream()
                .map(v -> BigDecimalUtil.nvl(v.getImporteOriginal()).subtract(BigDecimalUtil.nvl(v.getImporte())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        resumen.setTotalDescuentosAplicados(totalDescuentosServicios.add(totalDescuentosVentas).setScale(2, RoundingMode.HALF_UP));

        List<ResumenBarberoDTO> resumenBarberos = calcularResumenBarberos(fecha, fecha);
        resumen.setResumenBarberos(resumenBarberos);
        resumen.setTotalGananciaBarberia(sumarGananciaBarberia(resumenBarberos));

        return resumen;
    }

    public ResumenMensualDTO generarResumenMensual(YearMonth yearMonth) {
        try {
            return generarResumenMensualInternal(yearMonth);
        } catch (Exception e) {
            log.error("generarResumenMensual fallo para mes={}", yearMonth, e);
            throw e;
        }
    }

    private ResumenMensualDTO generarResumenMensualInternal(YearMonth yearMonth) {
        ResumenMensualDTO resumen = new ResumenMensualDTO();
        resumen.setMes(yearMonth);

        LocalDate fechaInicio = yearMonth.atDay(1);
        LocalDate fechaFin = yearMonth.atEndOfMonth();

        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalServicios = servicios.stream()
                .map(s -> BigDecimalUtil.nvl(s.getPrecio()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalServicios(totalServicios);
        resumen.setCantidadServicios(servicios.size());

        List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByFechaBetween(fechaInicio, fechaFin);
        BigDecimal totalVentas = ventas.stream()
                .map(v -> BigDecimalUtil.nvl(v.getImporte()))
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
                    return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(venta.getCantidad())));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        resumen.setTotalComisiones(totalComisiones);

        resumen.setTotalGeneral(totalServicios.add(totalVentas).setScale(2, RoundingMode.HALF_UP));

        BigDecimal totalDescuentosServicios = servicios.stream()
                .map(s -> BigDecimalUtil.nvl(s.getPrecioOriginal()).subtract(BigDecimalUtil.nvl(s.getPrecio())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescuentosVentas = ventas.stream()
                .map(v -> BigDecimalUtil.nvl(v.getImporteOriginal()).subtract(BigDecimalUtil.nvl(v.getImporte())))
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

    /**
     * Resumen de cortes por método de pago (efectivo / tarjeta) en un rango de fechas.
     */
    public ResumenPorMetodoPagoDTO generarResumenPorMetodoPago(LocalDate fechaInicio, LocalDate fechaFin) {
        validarRangoFechas(fechaInicio, fechaFin);
        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFechaBetween(fechaInicio, fechaFin);

        ResumenMetodoPagoItemDTO efectivo = new ResumenMetodoPagoItemDTO();
        ResumenMetodoPagoItemDTO tarjeta = new ResumenMetodoPagoItemDTO();

        for (com.papusbarbershop.entity.Servicio servicio : servicios) {
            BigDecimal precio = BigDecimalUtil.nvl(servicio.getPrecio());
            String metodo = normalizarMetodoPago(servicio.getMetodoPago());
            if ("EFECTIVO".equals(metodo)) {
                efectivo.setCantidad(efectivo.getCantidad() + 1);
                efectivo.setTotal(efectivo.getTotal().add(precio));
            } else if ("TARJETA".equals(metodo)) {
                tarjeta.setCantidad(tarjeta.getCantidad() + 1);
                tarjeta.setTotal(tarjeta.getTotal().add(precio));
            }
        }

        efectivo.setTotal(efectivo.getTotal().setScale(2, RoundingMode.HALF_UP));
        tarjeta.setTotal(tarjeta.getTotal().setScale(2, RoundingMode.HALF_UP));

        ResumenPorMetodoPagoDTO resumen = new ResumenPorMetodoPagoDTO();
        resumen.setEfectivo(efectivo);
        resumen.setTarjeta(tarjeta);
        return resumen;
    }

    /**
     * Detalle de cortes en un rango, opcionalmente filtrado por método de pago.
     */
    public List<DetalleReporteItemDTO> generarDetalleReporte(LocalDate fechaInicio, LocalDate fechaFin, String metodoPago) {
        validarRangoFechas(fechaInicio, fechaFin);
        String filtroMetodo = metodoPago != null && !metodoPago.isBlank()
                ? normalizarMetodoPago(metodoPago)
                : null;

        List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByFechaBetween(fechaInicio, fechaFin);
        List<DetalleReporteItemDTO> detalle = new ArrayList<>();

        for (com.papusbarbershop.entity.Servicio servicio : servicios) {
            String metodo = normalizarMetodoPago(servicio.getMetodoPago());
            if (filtroMetodo != null && !filtroMetodo.equals(metodo)) {
                continue;
            }
            if (filtroMetodo == null && !"EFECTIVO".equals(metodo) && !"TARJETA".equals(metodo)) {
                continue;
            }

            DetalleReporteItemDTO item = new DetalleReporteItemDTO();
            item.setFecha(servicio.getFecha());
            item.setHora(servicio.getHora());
            item.setBarberoNombre(servicio.getBarbero() != null ? servicio.getBarbero().getNombre() : "—");
            item.setCliente("—");
            item.setTipoCorte(servicio.getTipoCorte());
            item.setPrecioOriginal(BigDecimalUtil.nvl(servicio.getPrecioOriginal()));
            item.setDescuentoPorcentaje(BigDecimalUtil.nvl(servicio.getDescuentoPorcentaje()));
            item.setTotal(BigDecimalUtil.nvl(servicio.getPrecio()));
            item.setMetodoPago(capitalizarMetodoPago(metodo));
            detalle.add(item);
        }

        detalle.sort(Comparator
                .comparing(DetalleReporteItemDTO::getFecha).reversed()
                .thenComparing(DetalleReporteItemDTO::getHora, Comparator.nullsLast(Comparator.reverseOrder())));
        return detalle;
    }

    /**
     * Rendimiento por barbero con desglose efectivo/tarjeta y comisión del período.
     */
    public List<ResumenBarberoPagoDTO> generarResumenPorBarberoPago(LocalDate fechaInicio, LocalDate fechaFin) {
        validarRangoFechas(fechaInicio, fechaFin);
        List<Barbero> barberos = barberoRepository.findAll();
        List<ResumenBarberoPagoDTO> resultado = new ArrayList<>();

        for (Barbero barbero : barberos) {
            List<com.papusbarbershop.entity.Servicio> servicios = servicioRepository.findByBarberoId(barbero.getId());
            servicios = servicios.stream()
                    .filter(s -> !s.getFecha().isBefore(fechaInicio) && !s.getFecha().isAfter(fechaFin))
                    .toList();

            int cortesEfectivo = 0;
            int cortesTarjeta = 0;
            BigDecimal montoTotal = BigDecimal.ZERO;

            for (com.papusbarbershop.entity.Servicio servicio : servicios) {
                String metodo = normalizarMetodoPago(servicio.getMetodoPago());
                BigDecimal precio = BigDecimalUtil.nvl(servicio.getPrecio());
                montoTotal = montoTotal.add(precio);
                if ("EFECTIVO".equals(metodo)) {
                    cortesEfectivo++;
                } else if ("TARJETA".equals(metodo)) {
                    cortesTarjeta++;
                }
            }

            if (servicios.isEmpty()) {
                continue;
            }

            BigDecimal porcentaje = BigDecimalUtil.nvl(barbero.getPorcentajeServicio())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal comisionServicios = montoTotal.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);

            List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByBarberoId(barbero.getId());
            ventas = ventas.stream()
                    .filter(v -> !v.getFecha().isBefore(fechaInicio) && !v.getFecha().isAfter(fechaFin))
                    .toList();
            BigDecimal comisionProductos = ventas.stream()
                    .map(venta -> {
                        Integer comision = 0;
                        if (venta.getProducto() != null) {
                            comision = venta.getProducto().getComision();
                            if (comision == null) comision = 0;
                        }
                        return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(venta.getCantidad())));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ResumenBarberoPagoDTO resumen = new ResumenBarberoPagoDTO();
            resumen.setBarberoId(barbero.getId());
            resumen.setBarberoNombre(barbero.getNombre());
            resumen.setCortesEfectivo(cortesEfectivo);
            resumen.setCortesTarjeta(cortesTarjeta);
            resumen.setTotalCortes(servicios.size());
            resumen.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));
            resumen.setComisionCalculada(comisionServicios.add(comisionProductos).setScale(2, RoundingMode.HALF_UP));
            resultado.add(resumen);
        }

        resultado.sort(Comparator.comparing(ResumenBarberoPagoDTO::getMontoTotal).reversed());
        return resultado;
    }

    private void validarRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorias");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("fechaFin no puede ser anterior a fechaInicio");
        }
    }

    private String normalizarMetodoPago(String metodoPago) {
        if (metodoPago == null || metodoPago.isBlank()) {
            return "";
        }
        return metodoPago.trim().toUpperCase(Locale.ROOT);
    }

    private String capitalizarMetodoPago(String metodoNormalizado) {
        if (metodoNormalizado == null || metodoNormalizado.isBlank()) {
            return "—";
        }
        String lower = metodoNormalizado.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private List<ResumenBarberoDTO> calcularResumenBarberos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Barbero> barberos = barberoRepository.findAll();
        List<ResumenBarberoDTO> resumenBarberos = new ArrayList<>();

        for (Barbero barbero : barberos) {
            ResumenBarberoDTO resumen = new ResumenBarberoDTO();
            resumen.setBarberoId(barbero.getId());
            resumen.setBarberoNombre(barbero.getNombre());
            resumen.setPorcentajeServicio(BigDecimalUtil.nvl(barbero.getPorcentajeServicio()));

            BigDecimal totalServicios = BigDecimalUtil.nvl(servicioRepository.calcularTotalPorBarbero(barbero.getId(), fechaInicio, fechaFin));
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
                detalle.setPrecioOriginal(BigDecimalUtil.nvl(servicio.getPrecioOriginal()));
                detalle.setDescuentoPorcentaje(BigDecimalUtil.nvl(servicio.getDescuentoPorcentaje()));
                detalle.setPrecio(BigDecimalUtil.nvl(servicio.getPrecio()));
                return detalle;
            }).toList());

            List<com.papusbarbershop.entity.VentaProducto> ventas = ventaProductoRepository.findByBarberoId(barbero.getId());
            ventas = ventas.stream()
                    .filter(v -> !v.getFecha().isBefore(fechaInicio) && !v.getFecha().isAfter(fechaFin))
                    .toList();
            BigDecimal totalVentasImporteOriginal = ventas.stream()
                    .map(v -> BigDecimalUtil.nvl(v.getImporteOriginal()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            resumen.setTotalVentasImporteOriginal(totalVentasImporteOriginal);
            BigDecimal totalVentas = ventas.stream()
                    .map(v -> BigDecimalUtil.nvl(v.getImporte()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            resumen.setTotalVentas(totalVentas);
            resumen.setCantidadVentas(ventas.size());
            resumen.setDetallesVentas(ventas.stream().map(venta -> {
                DetalleVentaProductoDTO detalle = new DetalleVentaProductoDTO();
                detalle.setFecha(venta.getFecha());
                detalle.setHora(venta.getHora());
                Producto producto = venta.getProducto();
                String nombreProducto = producto != null ? producto.getNombre() : venta.getProductoNombre();
                detalle.setProducto(nombreProducto != null ? nombreProducto : "Producto eliminado");
                detalle.setCantidad(BigDecimalUtil.nvlInt(venta.getCantidad()));
                detalle.setPrecioUnitario(BigDecimalUtil.nvl(venta.getPrecioUnitario()));
                detalle.setImporteOriginal(BigDecimalUtil.nvl(venta.getImporteOriginal()));
                detalle.setDescuentoPorcentaje(BigDecimalUtil.nvl(venta.getDescuentoPorcentaje()));
                detalle.setImporte(BigDecimalUtil.nvl(venta.getImporte()));
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
                        return BigDecimal.valueOf(comision).multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(venta.getCantidad())));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            resumen.setTotalComisiones(totalComisiones);

            BigDecimal totalGenerado = totalServicios.add(totalVentas);
            resumen.setTotalGenerado(totalGenerado);

            BigDecimal porcentaje = BigDecimalUtil.nvl(barbero.getPorcentajeServicio()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal pagoPorServicios = totalServicios.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
            BigDecimal pagoBarbero = pagoPorServicios.add(totalComisiones).setScale(2, RoundingMode.HALF_UP);
            resumen.setPagoBarbero(pagoBarbero);

            // Ganancia neta para la barberia: solo servicios/cortes (ventas no suman ni restan en este monto)
            BigDecimal gananciaBarberia = totalServicios.subtract(pagoPorServicios).setScale(2, RoundingMode.HALF_UP);
            resumen.setGananciaBarberia(gananciaBarberia);

            resumenBarberos.add(resumen);
        }

        return resumenBarberos;
    }

    private BigDecimal sumarGananciaBarberia(List<ResumenBarberoDTO> resumenBarberos) {
        return resumenBarberos.stream()
                .map(r -> BigDecimalUtil.nvl(r.getGananciaBarberia()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
