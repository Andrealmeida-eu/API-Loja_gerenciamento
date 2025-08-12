package com.Loja.business.services;

import com.Loja.business.converter.ItemVendaConverter;
import com.Loja.business.converter.VendaConverter;
import com.Loja.business.dto.in.ItemVendaRequestDTO;
import com.Loja.business.dto.in.VendaRequestDTO;
import com.Loja.business.dto.out.VendaResponseDTO;
import com.Loja.business.dto.out.VendasMensalResponseDTO;
import com.Loja.infrastructure.entity.ItemVenda;
import com.Loja.infrastructure.entity.Produto;
import com.Loja.infrastructure.entity.Venda;
import com.Loja.infrastructure.exceptions.ConflitException;
import com.Loja.infrastructure.repository.ProdutoRepository;
import com.Loja.infrastructure.repository.VendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// VendaService.java
@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaConverter vendaConverter;
    private final ItemVendaConverter itemVendaConverter;
    private final ProdutoService produtoService;


    /**
     * Realiza uma nova venda no sistema
     *
     * @param vendaDTO DTO contendo os dados da venda e itens
     * @return VendaDTO com os dados da venda realizada
     * @throws ConflitException Se houver problemas de validação
     */
    @Transactional
    public VendaResponseDTO realizarVenda(VendaRequestDTO vendaDTO) {
        // Validação básica do DTO de entrada
        if (vendaDTO == null || vendaDTO.getItens() == null || vendaDTO.getItens().isEmpty()) {
            throw new ConflitException("A venda deve conter pelo menos um item");
        }

        // Cria e persiste a venda inicial (com valor zerado)
        Venda venda = new Venda();
        venda.setData(LocalDateTime.now()); // Data/hora atual
        venda.setValorTotal(0.0); // Valor inicial zerado
        venda = vendaRepository.save(venda); // Persiste para gerar ID

        List<ItemVenda> itens = new ArrayList<>();
        double valorTotal = 0.0;

        // Processa cada item da venda
        for (ItemVendaRequestDTO itemDTO : vendaDTO.getItens()) {
            // Validação do item
            if (itemDTO.getProdutoId() == null || itemDTO.getQuantidade() == null || itemDTO.getQuantidade() <= 0) {
                throw new ConflitException("Item de venda inválido: produtoId e quantidade são obrigatórios");
            }

            // Busca o produto no banco de dados
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new ConflitException("Produto não encontrado: " + itemDTO.getProdutoId()));

            // Verifica se há estoque suficiente
            if (produto.getQuantidadeEstoque() < itemDTO.getQuantidade()) {
                throw new ConflitException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Cria o item de venda com todos os campos necessários
            ItemVenda item = criarItemVenda(venda, produto, itemDTO);

            itens.add(item);
            valorTotal += item.getSubtotal();

            // Atualiza o estoque do produto
            produtoService.removerEstoque(produto.getId(), itemDTO.getQuantidade());
        }

        // Atualiza o valor total da venda
        venda.setValorTotal(valorTotal);
        venda.setItens(itens);

        // Salva a venda completa (com itens e valor total)
        venda = vendaRepository.save(venda);

        // Converte para DTO e retorna
        return vendaConverter.toDTO(venda);
    }

    /**
     * Cria um item de venda com todos os campos necessários
     *
     * @param venda   Venda relacionada
     * @param produto Produto vendido
     * @param itemDTO DTO com dados do item
     * @return ItemVenda configurado
     */
    private ItemVenda criarItemVenda(Venda venda, Produto produto, ItemVendaRequestDTO itemDTO) {
        ItemVenda item = new ItemVenda();
        item.setVenda(venda);
        item.setProduto(produto);
        item.setQuantidade(itemDTO.getQuantidade());
        item.setPrecoUnitario(produto.getPrecoVenda());

        // Campos históricos (importante para relatórios futuros)
        item.setProdutoNome(produto.getNome());
        item.setProdutoPrecoCompra(produto.getPrecoCompra());
        item.setProdutoPrecoVenda(produto.getPrecoVenda());

        // Calcula subtotal (preço venda × quantidade)
        item.setSubtotal(produto.getPrecoVenda() * itemDTO.getQuantidade());

        return item;
    }

    /**
     * Lista todas as vendas em um período específico
     *
     * @param inicio Data de início do período
     * @param fim    Data final do período
     * @return Lista de VendaDTO com as vendas do período
     */
    public List<VendaResponseDTO> listarVendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        // Converte as datas para LocalDateTime para incluir todo o dia
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atTime(23, 59, 59);

        // Busca vendas no período e converte para DTO
        return vendaRepository.findByDataBetween(inicioDateTime, fimDateTime).stream()
                .map(vendaConverter::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consulta a quantidade de vendas por mês em um ano específico
     *
     * @param ano Ano para consulta
     * @return Lista de VendasMensalDTO com a quantidade de vendas por mês
     */
    public List<VendasMensalResponseDTO> consultarQuantidadeVendasMensal(int ano) {
        // Validação do ano
        if (ano < 2000 || ano > LocalDate.now().getYear() + 1) {
            throw new ConflitException("Ano inválido");
        }

        // Processa cada mês do ano (1 a 12)
        return IntStream.rangeClosed(1, 12)
                .mapToObj(mes -> {
                    // Define o intervalo do mês (do dia 1 ao último dia)
                    LocalDate inicioMes = LocalDate.of(ano, mes, 1);
                    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                    // Converte para LocalDateTime
                    LocalDateTime inicio = inicioMes.atStartOfDay();
                    LocalDateTime fim = fimMes.atTime(23, 59, 59);

                    // Conta as vendas no período
                    long quantidadeVendas = vendaRepository.countByDataBetween(inicio, fim);

                    // Cria o DTO com os dados
                    return new VendasMensalResponseDTO(
                            YearMonth.of(ano, mes),
                            quantidadeVendas
                    );
                })
                .filter(dto -> dto.getQuantidadeVendas() > 0) // Filtra meses sem vendas
                .collect(Collectors.toList());
    }
}

