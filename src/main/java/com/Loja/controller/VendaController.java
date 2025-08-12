package com.Loja.controller;


import com.Loja.business.dto.in.VendaRequestDTO;
import com.Loja.business.dto.out.VendaResponseDTO;
import com.Loja.business.dto.out.VendasMensalResponseDTO;
import com.Loja.business.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// VendaController.java
@RestController
@RequestMapping("admin/loja/vendas")
public class VendaController {
    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponseDTO> realizarVenda(@RequestBody VendaRequestDTO vendaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.realizarVenda(vendaDTO));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<VendaResponseDTO>> listarVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(vendaService.listarVendasPorPeriodo(inicio, fim));
    }

    @GetMapping("/mensal/{ano}")
    public ResponseEntity<List<VendasMensalResponseDTO>> getVendasMensal(@PathVariable int ano) {
        return ResponseEntity.ok(vendaService.consultarQuantidadeVendasMensal(ano));
    }
}

