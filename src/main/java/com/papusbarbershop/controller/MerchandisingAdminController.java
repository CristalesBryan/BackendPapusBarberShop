package com.papusbarbershop.controller;

import com.papusbarbershop.dto.ImagenMerchUploadDTO;
import com.papusbarbershop.dto.ProductoMerchCreateDTO;
import com.papusbarbershop.dto.ProductoMerchDTO;
import com.papusbarbershop.service.ProductoMerchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/merchandising/productos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class MerchandisingAdminController {

    @Autowired
    private ProductoMerchService productoMerchService;

    @GetMapping
    public ResponseEntity<List<ProductoMerchDTO>> getAll() {
        return ResponseEntity.ok(productoMerchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoMerchDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoMerchService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoMerchDTO> create(@RequestBody ProductoMerchCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoMerchService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoMerchDTO> update(@PathVariable Long id, @RequestBody ProductoMerchCreateDTO dto) {
        return ResponseEntity.ok(productoMerchService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoMerchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/imagenes")
    public ResponseEntity<ProductoMerchDTO> addImagen(@PathVariable Long id, @RequestBody ImagenMerchUploadDTO upload) {
        return ResponseEntity.ok(productoMerchService.addImagen(id, upload));
    }

    @DeleteMapping("/{id}/imagenes/{imagenId}")
    public ResponseEntity<ProductoMerchDTO> deleteImagen(@PathVariable Long id, @PathVariable Long imagenId) {
        return ResponseEntity.ok(productoMerchService.deleteImagen(id, imagenId));
    }

    @PutMapping("/{id}/imagenes/orden")
    public ResponseEntity<ProductoMerchDTO> reorderImagenes(@PathVariable Long id, @RequestBody List<Long> imagenIds) {
        return ResponseEntity.ok(productoMerchService.reorderImagenes(id, imagenIds));
    }
}
