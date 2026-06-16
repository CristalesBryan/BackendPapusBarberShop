package com.papusbarbershop.service;

import com.papusbarbershop.dto.*;
import com.papusbarbershop.entity.VentaMerch;
import com.papusbarbershop.repository.VentaMerchRepository;
import com.papusbarbershop.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteMerchandisingService {

    @Autowired
    private VentaMerchRepository ventaMerchRepository;

    public ResumenMerchandisingDTO generarResumen(LocalDate fechaInicio, LocalDate fechaFin, String categoria) {
        List<VentaMerch> ventas;
        if (categoria != null && !categoria.isBlank()) {
            ventas = ventaMerchRepository.findByFechaBetweenAndCategoria(fechaInicio, fechaFin, categoria);
        } else {
            ventas = ventaMerchRepository.findByFechaBetween(fechaInicio, fechaFin);
        }

        ResumenMerchandisingDTO resumen = new ResumenMerchandisingDTO();

        BigDecimal totalVendido = ventas.stream()
                .map(v -> BigDecimalUtil.nvl(v.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        int totalUnidades = ventas.stream().mapToInt(VentaMerch::getCantidad).sum();

        resumen.setTotalVendido(totalVendido);
        resumen.setTotalUnidades(totalUnidades);
        resumen.setVentas(ventas.stream().map(this::toVentaDTO).collect(Collectors.toList()));

        Map<String, int[]> porProducto = new LinkedHashMap<>();
        for (VentaMerch v : ventas) {
            porProducto.merge(v.getProductoNombre(), new int[]{v.getCantidad(), 0}, (a, b) -> {
                a[0] += b[0];
                return a;
            });
        }
        String masVendido = porProducto.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue()[0]))
                .map(Map.Entry::getKey)
                .orElse("—");
        resumen.setProductoMasVendido(masVendido);

        Map<String, TopProductoMerchDTO> topMap = new LinkedHashMap<>();
        for (VentaMerch v : ventas) {
            topMap.compute(v.getProductoNombre(), (k, existing) -> {
                if (existing == null) {
                    return new TopProductoMerchDTO(k, v.getCantidad(), BigDecimalUtil.nvl(v.getTotal()));
                }
                existing.setCantidad(existing.getCantidad() + v.getCantidad());
                existing.setTotal(existing.getTotal().add(BigDecimalUtil.nvl(v.getTotal())));
                return existing;
            });
        }
        List<TopProductoMerchDTO> top5 = topMap.values().stream()
                .sorted(Comparator.comparingInt(TopProductoMerchDTO::getCantidad).reversed())
                .limit(5)
                .collect(Collectors.toList());
        resumen.setTopProductos(top5);

        Map<String, CategoriaMerchDTO> catMap = new LinkedHashMap<>();
        for (VentaMerch v : ventas) {
            catMap.compute(v.getCategoria(), (k, existing) -> {
                if (existing == null) {
                    return new CategoriaMerchDTO(k, v.getCantidad(), BigDecimalUtil.nvl(v.getTotal()));
                }
                existing.setCantidad(existing.getCantidad() + v.getCantidad());
                existing.setTotal(existing.getTotal().add(BigDecimalUtil.nvl(v.getTotal())));
                return existing;
            });
        }
        resumen.setDistribucionCategoria(new ArrayList<>(catMap.values()));

        return resumen;
    }

    private VentaMerchDTO toVentaDTO(VentaMerch v) {
        VentaMerchDTO dto = new VentaMerchDTO();
        dto.setId(v.getId());
        dto.setProductoNombre(v.getProductoNombre());
        dto.setCategoria(v.getCategoria());
        dto.setTalla(v.getTalla());
        dto.setCantidad(v.getCantidad());
        dto.setPrecioUnitario(v.getPrecioUnitario());
        dto.setTotal(v.getTotal());
        dto.setMetodoPago(v.getMetodoPago());
        dto.setPersonalizacionNombre(v.getPersonalizacionNombre());
        dto.setPersonalizacionNumero(v.getPersonalizacionNumero());
        dto.setFecha(v.getFecha());
        dto.setHora(v.getHora());
        return dto;
    }
}
