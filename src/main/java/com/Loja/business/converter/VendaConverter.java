package com.Loja.business.converter;


import com.Loja.business.dto.out.ItemVendaResponseDTO;
import com.Loja.business.dto.out.VendaResponseDTO;
import com.Loja.infrastructure.entity.Venda;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class VendaConverter {

    private final ItemVendaConverter itemVendaConverter;

    public VendaResponseDTO toDTO(Venda venda) {
        VendaResponseDTO dto = new VendaResponseDTO();
        dto.setData(venda.getData());
        dto.setValorTotal(venda.getValorTotal());

        List<ItemVendaResponseDTO> itensDTO = venda.getItens().stream()
                .map(itemVendaConverter::toDTO)
                .collect(Collectors.toList());
        dto.setItens(itensDTO);

        return dto;
    }

}

