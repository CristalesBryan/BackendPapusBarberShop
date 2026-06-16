package com.papusbarbershop.controller;

import com.papusbarbershop.dto.ProductoMerchDTO;
import com.papusbarbershop.service.ProductoMerchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchandising")
@CrossOrigin(origins = "*")
public class MerchandisingPublicController {

    @Autowired
    private ProductoMerchService productoMerchService;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoMerchDTO>> getProductosActivos() {
        return ResponseEntity.ok(productoMerchService.findActivos());
    }

    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoMerchDTO> getProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoMerchService.findActivoById(id));
    }
}
