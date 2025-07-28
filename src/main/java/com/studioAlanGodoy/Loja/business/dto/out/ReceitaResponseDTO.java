package com.studioAlanGodoy.Loja.business.dto.out;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceitaResponseDTO {
    private Double receitaTotal;
    private Double custoTotal;
    private Double lucroTotal;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // Getters e Setters
}
