package com.studioAlanGodoy.Loja.business.dto.in;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemVendaRequestDTO {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    // Getters e Setters
}
