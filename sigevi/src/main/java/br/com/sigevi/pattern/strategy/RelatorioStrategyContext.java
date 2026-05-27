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
 * Strategy Pattern (OCP) — escolhe qual "receita" de PDF usar.
 * Resumido ou completo? O contexto delega sem ficar cheio de if/else feio.
 */
@Component
public class RelatorioStrategyContext {

    // Spring injeta todas as estratégias (@Component) e a gente indexa pelo tipo
    private final Map<String, RelatorioGeracaoStrategy> strategies;

    public RelatorioStrategyContext(List<RelatorioGeracaoStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(RelatorioGeracaoStrategy::getTipo, Function.identity()));
    }

    public Path gerar(TipoRelatorio tipo, Vistoria vistoria, Path diretorio) {
        RelatorioGeracaoStrategy strategy = strategies.get(tipo.name());
        if (strategy == null) {
            throw new BusinessException("Tipo de relatorio nao suportado: " + tipo); // tipo que num existe, uai
        }
        return strategy.gerar(vistoria, diretorio);
    }
}
