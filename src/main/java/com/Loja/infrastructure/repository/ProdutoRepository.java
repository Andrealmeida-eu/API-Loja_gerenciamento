package com.Loja.infrastructure.repository;


import com.Loja.infrastructure.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Busca apenas produtos ativos
    List<Produto> findByAtivoTrue();

    // Busca por ID apenas se estiver ativo
    Optional<Produto> findByIdAndAtivoTrue(Long id);

    @Query("SELECT p FROM Produto p WHERE p.id = :id")
    Optional<Produto> findByIdIncludingDeleted(@Param("id") Long id);

    // Método padrão que ignora deletados
    Optional<Produto> findById(Long id);
}

