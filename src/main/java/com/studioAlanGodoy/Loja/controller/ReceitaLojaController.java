package com.studioAlanGodoy.Loja.controller;



import com.studioAlanGodoy.Loja.business.dto.out.ReceitaResponseDTO;
import com.studioAlanGodoy.Loja.business.dto.out.ReceitaMensalDTO;
import com.studioAlanGodoy.Loja.business.services.ReceitaLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

// ReceitaController.java
@RestController
@RequestMapping("/admin/loja/receita")
@RequiredArgsConstructor
public class ReceitaLojaController {

    private final ReceitaLojaService receitaService;

    @GetMapping
    public ResponseEntity<ReceitaResponseDTO> calcularReceitaLoja(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(receitaService.calcularReceita(inicio, fim));
    }

    @GetMapping("/mensal/{ano}")
    public ResponseEntity<List<ReceitaMensalDTO>> calcularReceitaMensalLoja(
            @PathVariable int ano) {
        List<ReceitaMensalDTO> receitas = receitaService.calcularReceitaMensal(ano);

        if (receitas.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // Retorna 200 com lista vazia
        }

        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/mensal/{ano}/{mes}")
    public ResponseEntity<ReceitaMensalDTO> getReceitaDoMesLoja(
            @PathVariable int ano,
            @PathVariable int mes) {
        return ResponseEntity.ok(receitaService.consultarReceitaDoMes(ano, mes));
    }

}
