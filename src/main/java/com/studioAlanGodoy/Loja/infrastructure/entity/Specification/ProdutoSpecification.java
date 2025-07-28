package com.studioAlanGodoy.Loja.infrastructure.entity.Specification;

import com.studioAlanGodoy.Loja.infrastructure.entity.Produto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Setter
@Getter
@Builder
public class ProdutoSpecification {
    public static Specification<Produto> comNomeSemelhante(String nome) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Produto> comPrecoVendaMaiorQue(Double preco) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("precoVenda"), preco);
    }

    public static Specification<Produto> comPrecoVendaMenorQue(Double preco) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("precoVenda"), preco);
    }

    public static Specification<Produto> comEstoqueDisponivel() {
        return (root, query, cb) ->
                cb.greaterThan(root.get("quantidadeEstoque"), 0);
    }
}

