package com.studioAlanGodoy.Loja.controller;


import com.studioAlanGodoy.Loja.business.dto.in.ProdutoRequestDTO;
import com.studioAlanGodoy.Loja.business.dto.out.ProdutoResponseDTO;
import com.studioAlanGodoy.Loja.business.services.ProdutoService;
import com.studioAlanGodoy.Loja.infrastructure.entity.Specification.ProdutoFiltro;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ProdutoController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/loja/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listarTodosProdutos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutosPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> salvarProdutos(@RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.salvar(dto));
    }

    @PutMapping("/{id}/up")
    public ResponseEntity<ProdutoResponseDTO> atualizarProdutos(@PathVariable Long id, @RequestBody ProdutoRequestDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(produtoService.salvar(dto));
    }

    @DeleteMapping("/{id}/deletar")
    public ResponseEntity<Void> deletarProdutos(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/adicionar-estoque")
    public ResponseEntity<Void> adicionarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        produtoService.adicionarEstoque(id, quantidade);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/remover-estoque")
    public ResponseEntity<Void> removerEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        produtoService.removerEstoque(id, quantidade);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Validated @RequestBody ProdutoRequestDTO produtoDTO) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, produtoDTO));
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<ProdutoResponseDTO>> pesquisarProdutos(
            @ModelAttribute ProdutoFiltro filtro) {
        return ResponseEntity.ok(produtoService.pesquisarProdutos(filtro));
    }
}
