package com.Loja.business.dto.out;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemVendaResponseDTO {

    private String produtoNome;
    private Integer quantidade;
    private Double precoUnitario;
    private Double subtotal;

    // Getters e Setters
}
