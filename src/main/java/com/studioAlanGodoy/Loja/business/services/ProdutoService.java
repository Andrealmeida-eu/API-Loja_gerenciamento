package com.studioAlanGodoy.Loja.business.services;


import com.studioAlanGodoy.Loja.business.converter.ProdutoConverter;
import com.studioAlanGodoy.Loja.business.dto.in.ProdutoRequestDTO;
import com.studioAlanGodoy.Loja.business.dto.out.ProdutoResponseDTO;
import com.studioAlanGodoy.Loja.infrastructure.entity.MovimentacaoEstoque;
import com.studioAlanGodoy.Loja.infrastructure.entity.Produto;
import com.studioAlanGodoy.Loja.infrastructure.entity.Specification.ProdutoFiltro;
import com.studioAlanGodoy.Loja.infrastructure.entity.Specification.ProdutoSpecification;
import com.studioAlanGodoy.Loja.infrastructure.enums.TipoMovimentacao;
import com.studioAlanGodoy.Loja.infrastructure.exceptions.ConflitException;
import com.studioAlanGodoy.Loja.infrastructure.repository.MovimentacaoEstoqueRepository;
import com.studioAlanGodoy.Loja.infrastructure.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoConverter produtoConverter;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    /**
     * Lista todos os produtos ativos
     * @return Lista de ProdutoDTO
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findByAtivoTrue().stream()
                .map(produtoConverter::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um produto ativo por ID
     * @param id ID do produto
     * @return ProdutoDTO
     * @throws ConflitException Se o produto não for encontrado ou estiver inativo
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ConflitException("Produto não encontrado ou inativo"));
        return produtoConverter.toDTO(produto);
    }

    /**
     * Salva um novo produto ou atualiza um existente
     * @param dto DTO com os dados do produto
     * @return ProdutoDTO salvo
     */
    @Transactional
    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto) {
        Produto produto = produtoConverter.toEntity(dto);
        produto.setAtivo(true); // Garante que novo produto seja ativo
        produto = produtoRepository.save(produto);
        return produtoConverter.toDTO(produto);
    }

    /**
     * Realiza soft delete de um produto (inativa)
     * @param id ID do produto
     * @throws ConflitException Se o produto não for encontrado ou já estiver inativo
     */
    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ConflitException("Produto não encontrado"));

        if (!produto.isAtivo()) {
            throw new ConflitException("Produto já está inativo");
        }

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    /**
     * Adiciona quantidade ao estoque de um produto
     * @param produtoId ID do produto
     * @param quantidade Quantidade a adicionar
     * @throws ConflitException Se o produto não for encontrado ou estiver inativo
     */
    @Transactional
    public void adicionarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new ConflitException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository.findByIdAndAtivoTrue(produtoId)
                .orElseThrow(() -> new ConflitException("Produto não encontrado ou inativo"));

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtoRepository.save(produto);

        registrarMovimentacao(produto, quantidade, TipoMovimentacao.ENTRADA);
    }

    /**
     * Remove quantidade do estoque de um produto
     * @param produtoId ID do produto
     * @param quantidade Quantidade a remover
     * @throws ConflitException Se o produto não for encontrado, estiver inativo ou não tiver estoque suficiente
     */
    @Transactional
    public void removerEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new ConflitException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository.findByIdAndAtivoTrue(produtoId)
                .orElseThrow(() -> new ConflitException("Produto não encontrado ou inativo"));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new ConflitException("Quantidade em estoque insuficiente");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);

        registrarMovimentacao(produto, quantidade, TipoMovimentacao.SAIDA);
    }

    /**
     * Registra uma movimentação de estoque
     * @param produto Produto movimentado
     * @param quantidade Quantidade movimentada
     * @param tipo Tipo de movimentação (ENTRADA/SAIDA)
     */
    private void registrarMovimentacao(Produto produto, Integer quantidade, TipoMovimentacao tipo) {
        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setTipo(tipo);
        movimentacao.setData(LocalDateTime.now());
        movimentacaoRepository.save(movimentacao);
    }

    /**
     * Reativa um produto inativo
     * @param id ID do produto
     * @throws ConflitException Se o produto não for encontrado ou já estiver ativo
     */
    @Transactional
    public void reativarProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ConflitException("Produto não encontrado"));

        if (produto.isAtivo()) {
            throw new ConflitException("Produto já está ativo");
        }

        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    /**
     * Atualiza os dados básicos de um produto
     * @param id ID do produto
     * @param produtoDTO DTO com os novos dados
     * @return ProdutoDTO atualizado
     * @throws ConflitException Se o produto não for encontrado
     */
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoDTO) {
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new ConflitException("Produto não encontrado com ID: " + id));

        // Atualiza apenas campos permitidos
        produtoExistente.setNome(produtoDTO.getNome());
        produtoExistente.setDescricao(produtoDTO.getDescricao());
        produtoExistente.setPrecoVenda(produtoDTO.getPrecoVenda());
        produtoExistente.setPrecoCompra(produtoDTO.getPrecoCompra()); // Adicionado para atualização

        produtoRepository.save(produtoExistente);
        return produtoConverter.toDTO(produtoExistente);
    }

    /**
     * Pesquisa produtos com base em filtros
     * @param filtro Objeto com os critérios de pesquisa
     * @return Lista de ProdutoDTO que atendem aos filtros
     */
    public List<ProdutoResponseDTO> pesquisarProdutos(ProdutoFiltro filtro) {
        Specification<Produto> spec = Specification.where(null);

        if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
            spec = spec.and(ProdutoSpecification.comNomeSemelhante(filtro.getNome()));
        }

        if (filtro.getPrecoMin() != null) {
            spec = spec.and(ProdutoSpecification.comPrecoVendaMaiorQue(filtro.getPrecoMin()));
        }

        if (filtro.getPrecoMax() != null) {
            spec = spec.and(ProdutoSpecification.comPrecoVendaMenorQue(filtro.getPrecoMax()));
        }

        if (filtro.getComEstoque() != null && filtro.getComEstoque()) {
            spec = spec.and(ProdutoSpecification.comEstoqueDisponivel());
        }

        return produtoRepository.findAll(spec).stream()
                .map(produtoConverter::toDTO)
                .collect(Collectors.toList());
    }
}

