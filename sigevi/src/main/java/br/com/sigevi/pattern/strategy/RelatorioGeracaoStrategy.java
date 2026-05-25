package br.com.sigevi.pattern.strategy;

import br.com.sigevi.model.Vistoria;

import java.nio.file.Path;

/**
 * Strategy Pattern (OCP): novas estrategias de relatorio podem ser adicionadas sem alterar o contexto.
 */
public interface RelatorioGeracaoStrategy {

    Path gerar(Vistoria vistoria, Path diretorioSaida);

    String getTipo();
}
