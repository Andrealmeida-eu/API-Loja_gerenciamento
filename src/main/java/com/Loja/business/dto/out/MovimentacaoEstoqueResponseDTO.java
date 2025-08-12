package com.Loja.business.dto.out;


import com.Loja.infrastructure.enums.TipoMovimentacao;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimentacaoEstoqueResponseDTO {

    private String produtoNome;
    private Integer quantidade;
    private TipoMovimentacao tipo;
    private LocalDateTime data;

    // Getters e Setters
}
