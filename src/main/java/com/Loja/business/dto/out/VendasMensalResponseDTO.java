package com.Loja.business.dto.out;

import lombok.*;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendasMensalResponseDTO {
    private YearMonth mesAno;
    private long quantidadeVendas;

    // Construtor, getters e setters
}
