package com.Loja.infrastructure.repository;


import com.Loja.infrastructure.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    List<MovimentacaoEstoque> findByProdutoId(Long produtoId);

    List<MovimentacaoEstoque> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
}
