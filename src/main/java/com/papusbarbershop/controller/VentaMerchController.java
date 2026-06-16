package com.papusbarbershop.controller;

import com.papusbarbershop.dto.VentaMerchCreateDTO;
import com.papusbarbershop.dto.VentaMerchDTO;
import com.papusbarbershop.service.VentaMerchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ventas-merch")
@CrossOrigin(origins = "*")
public class VentaMerchController {

    @Autowired
    private VentaMerchService ventaMerchService;

    @PostMapping
    public ResponseEntity<VentaMerchDTO> registrarVenta(@RequestBody VentaMerchCreateDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaMerchService.registrarVenta(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
