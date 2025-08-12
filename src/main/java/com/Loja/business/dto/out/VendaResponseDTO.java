package com.Loja.business.dto.out;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaResponseDTO {

    private LocalDateTime data;
    private Double valorTotal;
    private List<ItemVendaResponseDTO> itens;

    // Getters e Setters
}

