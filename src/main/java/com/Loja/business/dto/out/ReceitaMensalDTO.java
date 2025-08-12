package com.Loja.business.dto.out;

import lombok.*;

import java.time.YearMonth;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceitaMensalDTO {
    private YearMonth mes;
    private Double receitaTotal;
    private Double custoTotal;
    private Double lucroTotal;

    // Constructor, Getters e Setters
}