package br.com.sigevi.validator;

import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.model.enums.StatusVistoria;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Regrinha de ouro do status da vistoria (SRP).
 * Aqui a gente num deixa o povo inventar transição que num existe no processo real, tá ligado?
 */
@Component
public class StatusVistoriaValidator {

    // mapa de "de onde" -> "pra onde pode ir"
    private static final Map<StatusVistoria, Set<StatusVistoria>> TRANSICOES = new EnumMap<>(StatusVistoria.class);

    static {
        TRANSICOES.put(StatusVistoria.AGENDADA, Set.of(StatusVistoria.EM_ANDAMENTO, StatusVistoria.CANCELADA));
        TRANSICOES.put(StatusVistoria.EM_ANDAMENTO, Set.of(StatusVistoria.CONCLUIDA, StatusVistoria.REPROVADA, StatusVistoria.CANCELADA));
        TRANSICOES.put(StatusVistoria.CONCLUIDA, Set.of());
        TRANSICOES.put(StatusVistoria.REPROVADA, Set.of(StatusVistoria.EM_ANDAMENTO));
        TRANSICOES.put(StatusVistoria.CANCELADA, Set.of());
    }

    public void validarTransicao(StatusVistoria atual, StatusVistoria novo) {
        Set<StatusVistoria> permitidos = TRANSICOES.getOrDefault(atual, Set.of());
        if (!permitidos.contains(novo)) {
            // se tentar um pulo inválido, devolve erro claro pro front — bah, num pode assim
            throw new BusinessException(
                    String.format("Transicao invalida de %s para %s", atual, novo));
        }
    }
}
