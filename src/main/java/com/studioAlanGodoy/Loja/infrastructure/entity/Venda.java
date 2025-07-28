package com.studioAlanGodoy.Loja.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "venda")
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private Double valorTotal;

    // Getters e Setters
}

