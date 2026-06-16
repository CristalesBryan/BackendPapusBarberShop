package com.papusbarbershop.service;

import com.papusbarbershop.dto.VentaMerchCreateDTO;
import com.papusbarbershop.dto.VentaMerchDTO;
import com.papusbarbershop.entity.ProductoMerch;
import com.papusbarbershop.entity.VarianteProductoMerch;
import com.papusbarbershop.entity.VentaMerch;
import com.papusbarbershop.exception.RecursoNoEncontradoException;
import com.papusbarbershop.repository.ProductoMerchRepository;
import com.papusbarbershop.repository.VarianteProductoMerchRepository;
import com.papusbarbershop.repository.VentaMerchRepository;
import com.papusbarbershop.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class VentaMerchService {

    @Autowired
    private VentaMerchRepository ventaMerchRepository;

    @Autowired
    private ProductoMerchRepository productoMerchRepository;

    @Autowired
    private VarianteProductoMerchRepository varianteRepository;

    @Transactional
    public VentaMerchDTO registrarVenta(VentaMerchCreateDTO dto) {
        ProductoMerch producto = productoMerchRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("El producto no está disponible");
        }

        int cantidad = dto.getCantidad() != null ? dto.getCantidad() : 1;
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }

        VarianteProductoMerch variante = null;
        BigDecimal precioUnitario = BigDecimalUtil.nvl(producto.getPrecioBase());
        String talla = null;

        if (dto.getVarianteId() != null) {
            variante = varianteRepository.findByIdAndProductoId(dto.getVarianteId(), producto.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Variante no encontrada"));
            talla = variante.getTalla();
            if (variante.getPrecio() != null) {
                precioUnitario = variante.getPrecio();
            }
            if (variante.getStock() < cantidad) {
                throw new IllegalArgumentException("Stock insuficiente para la talla " + talla);
            }
            variante.setStock(variante.getStock() - cantidad);
            varianteRepository.save(variante);
        }

        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);

        VentaMerch venta = new VentaMerch();
        venta.setProducto(producto);
        venta.setVariante(variante);
        venta.setProductoNombre(producto.getNombre());
        venta.setCategoria(producto.getCategoria());
        venta.setTalla(talla);
        venta.setCantidad(cantidad);
        venta.setPrecioUnitario(precioUnitario);
        venta.setTotal(total);
        venta.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "Efectivo");
        venta.setPersonalizacionNombre(dto.getPersonalizacionNombre());
        venta.setPersonalizacionNumero(dto.getPersonalizacionNumero());
        venta.setFecha(LocalDate.now());
        venta.setHora(LocalTime.now());

        VentaMerch saved = ventaMerchRepository.save(venta);
        return toDTO(saved);
    }

    private VentaMerchDTO toDTO(VentaMerch venta) {
        VentaMerchDTO dto = new VentaMerchDTO();
        dto.setId(venta.getId());
        dto.setProductoNombre(venta.getProductoNombre());
        dto.setCategoria(venta.getCategoria());
        dto.setTalla(venta.getTalla());
        dto.setCantidad(venta.getCantidad());
        dto.setPrecioUnitario(venta.getPrecioUnitario());
        dto.setTotal(venta.getTotal());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setPersonalizacionNombre(venta.getPersonalizacionNombre());
        dto.setPersonalizacionNumero(venta.getPersonalizacionNumero());
        dto.setFecha(venta.getFecha());
        dto.setHora(venta.getHora());
        return dto;
    }
}
