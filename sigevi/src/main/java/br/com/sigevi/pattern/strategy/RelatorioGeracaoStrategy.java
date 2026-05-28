package br.com.sigevi.pattern.strategy;

import br.com.sigevi.model.Vistoria;

import java.nio.file.Path;

public interface RelatorioGeracaoStrategy {

    Path gerar(Vistoria vistoria, Path diretorioSaida);

    String getTipo();
}
