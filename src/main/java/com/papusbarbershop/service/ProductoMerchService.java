package com.papusbarbershop.service;

import com.papusbarbershop.dto.*;
import com.papusbarbershop.entity.ImagenProductoMerch;
import com.papusbarbershop.entity.ProductoMerch;
import com.papusbarbershop.entity.VarianteProductoMerch;
import com.papusbarbershop.exception.RecursoNoEncontradoException;
import com.papusbarbershop.repository.ImagenProductoMerchRepository;
import com.papusbarbershop.repository.ProductoMerchRepository;
import com.papusbarbershop.repository.VarianteProductoMerchRepository;
import com.papusbarbershop.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductoMerchService {

    @Autowired
    private ProductoMerchRepository productoMerchRepository;

    @Autowired
    private ImagenProductoMerchRepository imagenRepository;

    @Autowired
    private VarianteProductoMerchRepository varianteRepository;

    public List<ProductoMerchDTO> findActivos() {
        return productoMerchRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoMerchDTO> findAll() {
        return productoMerchRepository.findAllByOrderByNombreAsc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoMerchDTO findById(Long id) {
        ProductoMerch producto = productoMerchRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + id + " no encontrado"));
        return toDTO(producto);
    }

    public ProductoMerchDTO findActivoById(Long id) {
        ProductoMerch producto = productoMerchRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + id + " no encontrado"));
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new RecursoNoEncontradoException("Producto no disponible");
        }
        return toDTO(producto);
    }

    @Transactional
    public ProductoMerchDTO create(ProductoMerchCreateDTO dto) {
        ProductoMerch producto = new ProductoMerch();
        applyCreateUpdate(producto, dto);
        ProductoMerch saved = productoMerchRepository.save(producto);
        syncVariantes(saved, dto.getVariantes());
        return toDTO(productoMerchRepository.findById(saved.getId()).orElse(saved));
    }

    @Transactional
    public ProductoMerchDTO update(Long id, ProductoMerchCreateDTO dto) {
        ProductoMerch producto = productoMerchRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + id + " no encontrado"));
        applyCreateUpdate(producto, dto);
        producto.getVariantes().clear();
        productoMerchRepository.save(producto);
        syncVariantes(producto, dto.getVariantes());
        return toDTO(productoMerchRepository.findById(id).orElse(producto));
    }

    @Transactional
    public void delete(Long id) {
        if (!productoMerchRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Producto merch con ID " + id + " no encontrado");
        }
        productoMerchRepository.deleteById(id);
    }

    @Transactional
    public ProductoMerchDTO addImagen(Long productoId, ImagenMerchUploadDTO upload) {
        ProductoMerch producto = productoMerchRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + productoId + " no encontrado"));

        ImagenProductoMerch imagen = new ImagenProductoMerch();
        imagen.setProducto(producto);
        imagen.setS3Key(upload.getS3Key());
        imagen.setUrl(upload.getUrl());
        int orden = upload.getOrden() != null ? upload.getOrden() : producto.getImagenes().size();
        imagen.setOrden(orden);
        imagenRepository.save(imagen);

        return toDTO(productoMerchRepository.findById(productoId).orElse(producto));
    }

    @Transactional
    public ProductoMerchDTO deleteImagen(Long productoId, Long imagenId) {
        ProductoMerch producto = productoMerchRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + productoId + " no encontrado"));
        ImagenProductoMerch imagen = imagenRepository.findById(imagenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Imagen no encontrada"));
        if (!imagen.getProducto().getId().equals(productoId)) {
            throw new RecursoNoEncontradoException("Imagen no pertenece al producto");
        }
        producto.getImagenes().remove(imagen);
        imagenRepository.delete(imagen);
        reordenarImagenes(producto);
        return toDTO(productoMerchRepository.findById(productoId).orElse(producto));
    }

    @Transactional
    public ProductoMerchDTO reorderImagenes(Long productoId, List<Long> imagenIds) {
        ProductoMerch producto = productoMerchRepository.findById(productoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto merch con ID " + productoId + " no encontrado"));
        for (int i = 0; i < imagenIds.size(); i++) {
            Long imgId = imagenIds.get(i);
            for (ImagenProductoMerch img : producto.getImagenes()) {
                if (img.getId().equals(imgId)) {
                    img.setOrden(i);
                    break;
                }
            }
        }
        productoMerchRepository.save(producto);
        return toDTO(producto);
    }

    private void applyCreateUpdate(ProductoMerch producto, ProductoMerchCreateDTO dto) {
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioBase(BigDecimalUtil.nvl(dto.getPrecioBase()));
        producto.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        producto.setPermitePersonalizacion(dto.getPermitePersonalizacion() != null ? dto.getPermitePersonalizacion() : false);
        producto.setEsNuevo(dto.getEsNuevo() != null ? dto.getEsNuevo() : false);
        producto.setBadge(dto.getBadge());
    }

    private void syncVariantes(ProductoMerch producto, List<VarianteProductoMerchDTO> variantesDto) {
        if (variantesDto == null || variantesDto.isEmpty()) {
            return;
        }
        for (VarianteProductoMerchDTO vDto : variantesDto) {
            VarianteProductoMerch variante = new VarianteProductoMerch();
            variante.setProducto(producto);
            variante.setTalla(vDto.getTalla());
            variante.setPrecio(vDto.getPrecio());
            variante.setStock(vDto.getStock() != null ? vDto.getStock() : 0);
            varianteRepository.save(variante);
        }
    }

    private void reordenarImagenes(ProductoMerch producto) {
        List<ImagenProductoMerch> sorted = producto.getImagenes().stream()
                .sorted(Comparator.comparing(ImagenProductoMerch::getOrden))
                .collect(Collectors.toList());
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setOrden(i);
        }
    }

    private ProductoMerchDTO toDTO(ProductoMerch producto) {
        ProductoMerchDTO dto = new ProductoMerchDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setCategoria(producto.getCategoria());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioBase(BigDecimalUtil.nvl(producto.getPrecioBase()));
        dto.setActivo(producto.getActivo());
        dto.setPermitePersonalizacion(producto.getPermitePersonalizacion());
        dto.setEsNuevo(producto.getEsNuevo());
        dto.setBadge(producto.getBadge());

        List<ImagenProductoMerchDTO> imagenes = producto.getImagenes().stream()
                .sorted(Comparator.comparing(ImagenProductoMerch::getOrden))
                .map(img -> {
                    ImagenProductoMerchDTO i = new ImagenProductoMerchDTO();
                    i.setId(img.getId());
                    i.setS3Key(img.getS3Key());
                    i.setUrl(img.getUrl());
                    i.setOrden(img.getOrden());
                    return i;
                }).collect(Collectors.toList());
        dto.setImagenes(imagenes);

        List<VarianteProductoMerchDTO> variantes = producto.getVariantes().stream()
                .map(v -> {
                    VarianteProductoMerchDTO vd = new VarianteProductoMerchDTO();
                    vd.setId(v.getId());
                    vd.setTalla(v.getTalla());
                    vd.setPrecio(v.getPrecio());
                    vd.setStock(v.getStock());
                    return vd;
                }).collect(Collectors.toList());
        dto.setVariantes(variantes);

        int stockTotal = variantes.stream().mapToInt(v -> v.getStock() != null ? v.getStock() : 0).sum();
        dto.setStockTotal(stockTotal);

        BigDecimal precioBase = BigDecimalUtil.nvl(producto.getPrecioBase());
        BigDecimal precioMin = precioBase;
        BigDecimal precioMax = precioBase;
        for (VarianteProductoMerchDTO v : variantes) {
            BigDecimal p = v.getPrecio() != null ? v.getPrecio() : precioBase;
            if (p.compareTo(precioMin) < 0) precioMin = p;
            if (p.compareTo(precioMax) > 0) precioMax = p;
        }
        dto.setPrecioMin(precioMin.setScale(2, RoundingMode.HALF_UP));
        dto.setPrecioMax(precioMax.setScale(2, RoundingMode.HALF_UP));

        return dto;
    }
}
