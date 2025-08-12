package com.Loja.business.dto.in;


import com.Loja.infrastructure.enums.TipoMovimentacao;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimentacaoEstoqueRequestDTO {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private TipoMovimentacao tipo;
    private LocalDateTime data;

    // Getters e Setters
}
