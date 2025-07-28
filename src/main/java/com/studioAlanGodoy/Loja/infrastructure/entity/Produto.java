package com.studioAlanGodoy.Loja.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "produto")
@SQLDelete(sql = "UPDATE produto SET ativo = false WHERE id = ?") // Hibernate irá executar isso ao invés de DELETE
@Where(clause = "ativo = true") // Filtra automaticamente apenas produtos ativos
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Double precoCompra;

    @Column(nullable = false)
    private Double precoVenda;

    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private boolean ativo = true;


    // Getters e Setters
}
