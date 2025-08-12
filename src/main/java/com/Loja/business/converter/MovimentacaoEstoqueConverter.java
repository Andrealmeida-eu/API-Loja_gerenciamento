package com.Loja.business.converter;



import com.Loja.business.dto.out.MovimentacaoEstoqueResponseDTO;
import com.Loja.infrastructure.entity.MovimentacaoEstoque;
import com.Loja.infrastructure.entity.Produto;
import org.springframework.stereotype.Component;


@Component
public class MovimentacaoEstoqueConverter {
    public MovimentacaoEstoqueResponseDTO toDTO(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueResponseDTO dto = new MovimentacaoEstoqueResponseDTO();
        dto.setProdutoNome(movimentacao.getProduto().getNome());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setTipo(movimentacao.getTipo());
        dto.setData(movimentacao.getData());
        return dto;
    }

    public MovimentacaoEstoque toEntity(MovimentacaoEstoqueResponseDTO dto, Produto produto) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setQuantidade(dto.getQuantidade());
        movimentacao.setTipo(dto.getTipo());
        movimentacao.setData(dto.getData());
        return movimentacao;
    }
}

