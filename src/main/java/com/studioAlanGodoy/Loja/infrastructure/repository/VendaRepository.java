package com.studioAlanGodoy.Loja.infrastructure.repository;


import com.studioAlanGodoy.Loja.infrastructure.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);


    // Consulta para contar vendas (método consultarQuantidadeVendasMensal)
    @Query("SELECT COUNT(v) FROM Venda v WHERE v.data BETWEEN :inicio AND :fim")
    long countByDataBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Consulta otimizada para buscar meses que têm vendas (opcional)
    @Query("SELECT DISTINCT YEAR(v.data) as ano, MONTH(v.data) as mes FROM Venda v WHERE YEAR(v.data) = :ano ORDER BY mes")
    List<Object[]> findMesesComVendasPorAno(@Param("ano") int ano);
}
