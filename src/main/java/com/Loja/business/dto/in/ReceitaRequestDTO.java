package com.Loja.business.dto.in;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceitaRequestDTO {
    private Double receitaTotal;
    private Double custoTotal;
    private Double lucroTotal;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Getters e Setters
}
