package com.Loja.business.converter;



import com.Loja.business.dto.in.ProdutoRequestDTO;
import com.Loja.business.dto.out.ProdutoResponseDTO;
import com.Loja.infrastructure.entity.Produto;
import org.springframework.stereotype.Component;


@Component
public class ProdutoConverter {
    public ProdutoResponseDTO toDTO(Produto produto) {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setPrecoCompra(produto.getPrecoCompra());
        dto.setPrecoVenda(produto.getPrecoVenda());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        return dto;
    }

    public Produto toEntity(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setId(dto.getId());
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPrecoCompra(dto.getPrecoCompra());
        produto.setPrecoVenda(dto.getPrecoVenda());
        produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        return produto;
    }
}

