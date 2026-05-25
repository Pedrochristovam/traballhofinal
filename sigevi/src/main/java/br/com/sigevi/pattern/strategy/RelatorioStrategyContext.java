package br.com.sigevi.pattern.strategy;

import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.TipoRelatorio;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Strategy Pattern: contexto que delega a estrategia correta em tempo de execucao.
 */
@Component
public class RelatorioStrategyContext {

    private final Map<String, RelatorioGeracaoStrategy> strategies;

    public RelatorioStrategyContext(List<RelatorioGeracaoStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RelatorioGeracaoStrategy::getTipo, Function.identity()));
    }

    public Path gerar(TipoRelatorio tipo, Vistoria vistoria, Path diretorio) {
        RelatorioGeracaoStrategy strategy = strategies.get(tipo.name());
        if (strategy == null) {
            throw new BusinessException("Tipo de relatorio nao suportado: " + tipo);
        }
        return strategy.gerar(vistoria, diretorio);
    }
}
