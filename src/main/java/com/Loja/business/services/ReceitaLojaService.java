package com.Loja.business.services;


import com.Loja.business.dto.out.ReceitaResponseDTO;
import com.Loja.business.dto.out.ReceitaMensalDTO;
import com.Loja.infrastructure.entity.ItemVenda;
import com.Loja.infrastructure.entity.Produto;
import com.Loja.infrastructure.entity.Venda;
import com.Loja.infrastructure.exceptions.ConflitException;
import com.Loja.infrastructure.exceptions.ResourceNotFoundException;
import com.Loja.infrastructure.repository.ProdutoRepository;
import com.Loja.infrastructure.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReceitaLojaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    /**
     * Calcula a receita total, custo total e lucro total em um período específico
     * @param inicio Data de início do período
     * @param fim Data final do período
     * @return ReceitaDTO contendo os valores calculados
     */
    public ReceitaResponseDTO calcularReceita(LocalDate inicio, LocalDate fim) {
        // Valida as datas fornecidas
        validateDates(inicio, fim);

        // Converte as datas LocalDate para LocalDateTime para incluir todo o dia
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atTime(23, 59, 59);

        // Busca todas as vendas no período especificado
        List<Venda> vendas = vendaRepository.findByDataBetween(inicioDateTime, fimDateTime);

        // Se não houver vendas, retorna DTO com valores zerados
        if (vendas.isEmpty()) {
            return buildReceitaDTO(0.0, 0.0, 0.0, inicio, fim);
        }

        try {
            // Usando BigDecimal para cálculos precisos (evitar problemas de arredondamento)
            BigDecimal receitaTotal = new BigDecimal(calculateTotalRevenue(vendas));
            BigDecimal custoTotal = new BigDecimal(calculateTotalCost(vendas));
            BigDecimal lucroTotal = receitaTotal.subtract(custoTotal);

            // Constrói e retorna o DTO com os valores calculados
            return buildReceitaDTO(
                    receitaTotal.doubleValue(),
                    custoTotal.doubleValue(),
                    lucroTotal.doubleValue(),
                    inicio,
                    fim
            );
        } catch (Exception ex) {
            // Lança exceção em caso de erro nos cálculos
            throw new ConflitException("Erro ao calcular receita: " + ex.getMessage(), ex);
        }
    }

    /**
     * Valida se as datas fornecidas são válidas
     * @param inicio Data de início
     * @param fim Data final
     * @throws ConflitException Se as datas forem inválidas
     */
    private void validateDates(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new ConflitException("Datas de início e fim são obrigatórias");
        }

        if (fim.isBefore(inicio)) {
            throw new ConflitException("Data final não pode ser anterior à data inicial");
        }
    }

    /**
     * Calcula a receita total de uma lista de vendas
     * @param vendas Lista de vendas
     * @return Valor total da receita
     */
    private double calculateTotalRevenue(List<Venda> vendas) {
        return vendas.stream()
                .mapToDouble(Venda::getValorTotal) // Extrai o valor total de cada venda
                .sum(); // Soma todos os valores
    }

    /**
     * Calcula o custo total de uma lista de vendas
     * @param vendas Lista de vendas
     * @return Valor total do custo
     */
    private double calculateTotalCost(List<Venda> vendas) {
        return vendas.stream()
                .flatMap(venda -> venda.getItens().stream()) // Transforma cada venda em stream de itens
                .mapToDouble(this::calculateItemCost) // Calcula o custo de cada item
                .sum(); // Soma todos os custos
    }

    /**
     * Calcula o custo de um item de venda específico
     * @param item Item de venda
     * @return Custo do item (preço de compra × quantidade)
     * @throws ResourceNotFoundException Se o produto não for encontrado
     */
    private double calculateItemCost(ItemVenda item) {
        Long produtoId = item.getProduto().getId();
        // Busca o produto (incluindo deletados)
        Optional<Produto> produtoOpt = produtoRepository.findByIdIncludingDeleted(produtoId);

        // Calcula o custo ou lança exceção se produto não existir
        return produtoOpt.map(produto -> produto.getPrecoCompra() * item.getQuantidade())
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Produto com ID %d não encontrado (mesmo incluindo deletados)", produtoId)
                ));
    }

    /**
     * Calcula a receita mensal para todos os meses de um ano específico
     * @param ano Ano para cálculo
     * @return Lista de ReceitaMensalDTO com os dados de cada mês
     */
    public List<ReceitaMensalDTO> calcularReceitaMensal(int ano) {
        // Validação básica do ano
        if (ano < 2000 || ano > LocalDate.now().getYear() + 1) {
            throw new ConflitException("Ano inválido");
        }

        // Gera uma stream de meses (1 a 12) e processa cada um
        return IntStream.rangeClosed(1, 12)
                .mapToObj(mes -> {
                    // Define o intervalo do mês (do dia 1 ao último dia)
                    LocalDate inicioMes = LocalDate.of(ano, mes, 1);
                    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

                    // Calcula a receita para o mês
                    ReceitaResponseDTO receita = calcularReceita(inicioMes, fimMes);

                    // Cria o DTO mensal com os resultados
                    return new ReceitaMensalDTO(
                            YearMonth.of(ano, mes), // Ano e mês combinados
                            receita.getReceitaTotal(),
                            receita.getCustoTotal(),
                            receita.getLucroTotal()
                    );
                })
                .collect(Collectors.toList()); // Coleta todos os resultados em uma lista
    }

    /**
     * Consulta a receita de um mês específico
     * @param ano Ano para consulta
     * @param mes Mês para consulta (1-12)
     * @return ReceitaMensalDTO com os dados do mês
     */
    public ReceitaMensalDTO consultarReceitaDoMes(int ano, int mes) {
        // Validação do ano
        if (ano < 2000 || ano > LocalDate.now().getYear() + 1) {
            throw new ConflitException("Ano inválido");
        }
        // Validação do mês
        if (mes < 1 || mes > 12) {
            throw new ConflitException("Mês inválido. Deve ser entre 1 e 12");
        }

        // Define o intervalo do mês (do dia 1 ao último dia)
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        // Calcula a receita para o mês
        ReceitaResponseDTO receita = calcularReceita(inicioMes, fimMes);

        // Retorna o DTO com os resultados
        return new ReceitaMensalDTO(
                YearMonth.of(ano, mes),
                receita.getReceitaTotal(),
                receita.getCustoTotal(),
                receita.getLucroTotal()
        );
    }


    private ReceitaResponseDTO buildReceitaDTO(double receitaTotal, double custoTotal,
                                               double lucroTotal, LocalDate inicio, LocalDate fim) {
        ReceitaResponseDTO dto = new ReceitaResponseDTO();
        dto.setReceitaTotal(receitaTotal);
        dto.setCustoTotal(custoTotal);
        dto.setLucroTotal(lucroTotal);
        dto.setDataInicio(inicio);
        dto.setDataFim(fim);
        return dto;
    }
}