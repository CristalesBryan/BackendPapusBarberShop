package com.papusbarbershop.service;

import com.papusbarbershop.dto.VentaProductoCreateDTO;
import com.papusbarbershop.dto.VentaProductoDTO;
import com.papusbarbershop.entity.Barbero;
import com.papusbarbershop.entity.Producto;
import com.papusbarbershop.entity.VentaProducto;
import com.papusbarbershop.exception.RecursoNoEncontradoException;
import com.papusbarbershop.exception.ValidacionException;
import com.papusbarbershop.repository.VentaProductoRepository;
import com.papusbarbershop.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaProductoService {

    private static final Logger log = LoggerFactory.getLogger(VentaProductoService.class);

    @Autowired
    private VentaProductoRepository ventaProductoRepository;

    @Autowired
    private BarberoService barberoService;

    @Autowired
    private ProductoService productoService;

    @Transactional
    public VentaProductoDTO create(VentaProductoCreateDTO ventaCreateDTO) {
        Barbero barbero = barberoService.findEntityById(ventaCreateDTO.getBarberoId());
        Producto producto = productoService.findEntityById(ventaCreateDTO.getProductoId());

        Integer stockAntes = producto.getStock();
        if (BigDecimalUtil.nvlInt(stockAntes) < BigDecimalUtil.nvlInt(ventaCreateDTO.getCantidad())) {
            throw new ValidacionException("Stock insuficiente. Stock disponible: " + stockAntes +
                    ", cantidad solicitada: " + ventaCreateDTO.getCantidad());
        }

        BigDecimal precioUnitario = BigDecimalUtil.nvl(producto.getPrecioVenta());
        BigDecimal importeOriginal = precioUnitario.multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(ventaCreateDTO.getCantidad()))).setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuentoPorcentaje = normalizarDescuento(ventaCreateDTO.getDescuentoPorcentaje());
        BigDecimal importeFinal = aplicarDescuento(importeOriginal, descuentoPorcentaje);

        Integer stockDespues = stockAntes - ventaCreateDTO.getCantidad();
        producto.setStock(stockDespues);
        productoService.update(producto.getId(), convertProductoToDTO(producto));

        VentaProducto venta = new VentaProducto();
        venta.setFecha(ventaCreateDTO.getFecha());
        venta.setHora(ventaCreateDTO.getHora());
        venta.setBarbero(barbero);
        venta.setProducto(producto);
        venta.setProductoNombre(producto.getNombre());
        venta.setCantidad(ventaCreateDTO.getCantidad());
        venta.setPrecioUnitario(precioUnitario);
        venta.setImporteOriginal(importeOriginal);
        venta.setDescuentoPorcentaje(descuentoPorcentaje);
        venta.setImporte(importeFinal);
        venta.setStockAntes(stockAntes);
        venta.setStockDespues(stockDespues);
        venta.setMetodoPago(ventaCreateDTO.getMetodoPago());

        VentaProducto saved = ventaProductoRepository.save(venta);
        return convertToDTO(saved);
    }

    public List<VentaProductoDTO> findAll() {
        try {
            return ventaProductoRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("findAll ventas-productos", e);
            throw e;
        }
    }

    public List<VentaProductoDTO> findByFecha(LocalDate fecha) {
        return ventaProductoRepository.findByFecha(fecha).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public VentaProductoDTO findById(Long id) {
        VentaProducto venta = ventaProductoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));
        return convertToDTO(venta);
    }

    @Transactional
    public VentaProductoDTO update(Long id, VentaProductoCreateDTO ventaCreateDTO) {
        VentaProducto venta = ventaProductoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));

        Barbero barbero = barberoService.findEntityById(ventaCreateDTO.getBarberoId());
        Producto producto = productoService.findEntityById(ventaCreateDTO.getProductoId());

        boolean productoCambio = venta.getProducto() == null || !venta.getProducto().getId().equals(ventaCreateDTO.getProductoId());
        boolean cantidadCambio = !venta.getCantidad().equals(ventaCreateDTO.getCantidad());

        if (productoCambio || cantidadCambio) {
            if (venta.getProducto() != null) {
                Producto productoAnterior = venta.getProducto();
                Integer stockActualAnterior = productoAnterior.getStock();
                Integer stockRestaurado = stockActualAnterior + venta.getCantidad();
                productoAnterior.setStock(stockRestaurado);
                productoService.update(productoAnterior.getId(), convertProductoToDTO(productoAnterior));
            }

            Integer stockActual = producto.getStock();
            Integer cantidadNecesaria = ventaCreateDTO.getCantidad();

            if (productoCambio) {
                if (stockActual < cantidadNecesaria) {
                    throw new ValidacionException("Stock insuficiente. Stock disponible: " + stockActual +
                            ", cantidad solicitada: " + cantidadNecesaria);
                }
            } else {
                Integer diferencia = cantidadNecesaria - venta.getCantidad();
                if (diferencia > 0 && stockActual < diferencia) {
                    throw new ValidacionException("Stock insuficiente. Stock disponible: " + stockActual +
                            ", cantidad adicional necesaria: " + diferencia);
                }
            }

            Integer stockAntes = producto.getStock();
            Integer stockDespues = stockActual - cantidadNecesaria;
            producto.setStock(stockDespues);
            productoService.update(producto.getId(), convertProductoToDTO(producto));

            venta.setStockAntes(stockAntes);
            venta.setStockDespues(stockDespues);
        }

        BigDecimal precioUnitario = BigDecimalUtil.nvl(producto.getPrecioVenta());
        BigDecimal importeOriginal = precioUnitario.multiply(BigDecimal.valueOf(BigDecimalUtil.nvlInt(ventaCreateDTO.getCantidad()))).setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuentoPorcentaje = normalizarDescuento(ventaCreateDTO.getDescuentoPorcentaje());
        BigDecimal importeFinal = aplicarDescuento(importeOriginal, descuentoPorcentaje);

        venta.setFecha(ventaCreateDTO.getFecha());
        venta.setHora(ventaCreateDTO.getHora());
        venta.setBarbero(barbero);
        venta.setProducto(producto);
        venta.setProductoNombre(producto.getNombre());
        venta.setCantidad(ventaCreateDTO.getCantidad());
        venta.setPrecioUnitario(precioUnitario);
        venta.setImporteOriginal(importeOriginal);
        venta.setDescuentoPorcentaje(descuentoPorcentaje);
        venta.setImporte(importeFinal);
        venta.setMetodoPago(ventaCreateDTO.getMetodoPago());

        VentaProducto saved = ventaProductoRepository.save(venta);
        return convertToDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        VentaProducto venta = ventaProductoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + id));

        if (venta.getProducto() != null) {
            Producto producto = venta.getProducto();
            Integer stockActual = producto.getStock();
            Integer stockRestaurado = BigDecimalUtil.nvlInt(stockActual) + BigDecimalUtil.nvlInt(venta.getCantidad());
            producto.setStock(stockRestaurado);
            productoService.update(producto.getId(), convertProductoToDTO(producto));
        }

        ventaProductoRepository.deleteById(id);
    }

    private VentaProductoDTO convertToDTO(VentaProducto venta) {
        VentaProductoDTO dto = new VentaProductoDTO();
        dto.setId(venta.getId());
        dto.setFecha(venta.getFecha());
        dto.setHora(venta.getHora());
        dto.setBarberoId(venta.getBarbero().getId());
        dto.setBarberoNombre(venta.getBarbero().getNombre());
        dto.setProductoId(venta.getProducto() != null ? venta.getProducto().getId() : null);
        dto.setProductoNombre(venta.getProducto() != null ? venta.getProducto().getNombre() : (venta.getProductoNombre() != null ? venta.getProductoNombre() : "Producto eliminado"));
        dto.setCantidad(BigDecimalUtil.nvlInt(venta.getCantidad()));
        dto.setPrecioUnitario(BigDecimalUtil.nvl(venta.getPrecioUnitario()));
        dto.setImporteOriginal(BigDecimalUtil.nvl(venta.getImporteOriginal()));
        dto.setDescuentoPorcentaje(BigDecimalUtil.nvl(venta.getDescuentoPorcentaje()));
        dto.setImporte(BigDecimalUtil.nvl(venta.getImporte()));
        dto.setStockAntes(venta.getStockAntes());
        dto.setStockDespues(venta.getStockDespues());
        dto.setMetodoPago(venta.getMetodoPago());
        return dto;
    }

    private com.papusbarbershop.dto.ProductoCreateDTO convertProductoToDTO(Producto producto) {
        com.papusbarbershop.dto.ProductoCreateDTO dto = new com.papusbarbershop.dto.ProductoCreateDTO();
        dto.setNombre(producto.getNombre());
        dto.setStock(producto.getStock());
        dto.setPrecioCosto(producto.getPrecioCosto());
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setComision(producto.getComision());
        dto.setComisionHabilitada(producto.getComision() != null);
        dto.setDescripcion(producto.getDescripcion());
        return dto;
    }

    private BigDecimal normalizarDescuento(BigDecimal descuento) {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return descuento.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal aplicarDescuento(BigDecimal monto, BigDecimal descuentoPorcentaje) {
        BigDecimal factor = BigDecimal.ONE.subtract(descuentoPorcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            factor = BigDecimal.ZERO;
        }
        return monto.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
