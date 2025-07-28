package com.studioAlanGodoy.Loja.business.converter;


import com.studioAlanGodoy.Loja.business.dto.out.ItemVendaResponseDTO;
import com.studioAlanGodoy.Loja.infrastructure.entity.ItemVenda;
import com.studioAlanGodoy.Loja.infrastructure.entity.Produto;
import com.studioAlanGodoy.Loja.infrastructure.entity.Venda;
import org.springframework.stereotype.Component;


@Component
public class ItemVendaConverter {
    public ItemVendaResponseDTO toDTO(ItemVenda item) {
        ItemVendaResponseDTO dto = new ItemVendaResponseDTO();
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }

    public ItemVenda toEntity(ItemVendaResponseDTO dto, Venda venda, Produto produto) {
        ItemVenda item = new ItemVenda();
        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(dto.getQuantidade());
        item.setPrecoUnitario(dto.getPrecoUnitario());
        item.setSubtotal(dto.getSubtotal());
        return item;
    }
}