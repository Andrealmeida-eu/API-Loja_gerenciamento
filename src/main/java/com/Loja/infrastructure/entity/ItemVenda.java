package com.Loja.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "itemVenda")
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Venda venda;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Produto produto;

    // Campos adicionais para histórico
    @Column(nullable = false)
    private String produtoNome;

    @Column(nullable = false)
    private Double produtoPrecoCompra;

    @Column(nullable = false)
    private Double produtoPrecoVenda;


    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double precoUnitario;

    @Column(nullable = false)
    private Double subtotal;

    @PrePersist
    @PreUpdate
    public void calcularCampos() {
        // Garante que os campos históricos estejam preenchidos
        if (produto != null) {
            this.produtoNome = produto.getNome();
            this.produtoPrecoCompra = produto.getPrecoCompra();
            this.produtoPrecoVenda = produto.getPrecoVenda();

            // Se precoUnitario não foi definido, usa o preço de venda do produto
            if (this.precoUnitario == null) {
                this.precoUnitario = produto.getPrecoVenda();
            }
        }

        // Garante que subtotal seja sempre calculado
        if (this.precoUnitario != null && this.quantidade != null) {
            this.subtotal = this.precoUnitario * this.quantidade;
        } else {
            throw new IllegalStateException("Não foi possível calcular subtotal: precoUnitario ou quantidade estão nulos");
        }
    }
    // Getters e Setters
}

