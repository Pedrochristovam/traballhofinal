package br.com.sigevi.pattern.factory;

import br.com.sigevi.model.Relatorio;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.TipoRelatorio;

/**
 * Factory Pattern — monta o objeto Relatorio sempre do mesmo jeito.
 * Evita espalhar builder repetido no service e esquecer campo, né.
 */
public final class RelatorioFactory {

    private RelatorioFactory() {
        // construtor privado: essa classe é só método estático mesmo
    }

    public static Relatorio criar(Vistoria vistoria, TipoRelatorio tipo, String caminho, Usuario geradoPor) {
        return Relatorio.builder()
                .vistoria(vistoria)
                .tipo(tipo)
                .caminhoArquivo(caminho)
                .geradoPor(geradoPor)
                .build();
    }
}
