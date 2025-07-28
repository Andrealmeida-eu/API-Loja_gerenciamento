package com.studioAlanGodoy.Loja.business.dto.in;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendaRequestDTO {
    private Long id;
    private LocalDateTime data;
    private Double valorTotal;
    private List<ItemVendaRequestDTO> itens;

    // Getters e Setters
}

