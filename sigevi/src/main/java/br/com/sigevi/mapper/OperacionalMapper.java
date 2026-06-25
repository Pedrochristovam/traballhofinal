package br.com.sigevi.mapper;

import br.com.sigevi.dto.response.VistoriaOperacionalResponse;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.StatusVistoria;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class OperacionalMapper {

    public VistoriaOperacionalResponse toOperacionalResponse(Vistoria vistoria, LocalDate referencia) {
        LocalDate dataReferencia = referencia != null ? referencia : LocalDate.now();
        boolean atrasada = isAtrasada(vistoria, dataReferencia);
        Long diasAtraso = atrasada
                ? ChronoUnit.DAYS.between(vistoria.getDataVistoria(), dataReferencia)
                : null;

        return VistoriaOperacionalResponse.builder()
                .id(vistoria.getId())
                .imovelId(vistoria.getImovel().getId())
                .imovelMatricula(vistoria.getImovel().getMatricula())
                .imovelEndereco(vistoria.getImovel().getEndereco())
                .inspetorId(vistoria.getInspetor().getId())
                .inspetorNome(vistoria.getInspetor().getNome())
                .status(vistoria.getStatus())
                .dataVistoria(vistoria.getDataVistoria())
                .diasAtraso(diasAtraso)
                .atrasada(atrasada)
                .build();
    }

    public boolean isAtrasada(Vistoria vistoria, LocalDate referencia) {
        return vistoria.getDataVistoria().isBefore(referencia)
                && (vistoria.getStatus() == StatusVistoria.AGENDADA
                || vistoria.getStatus() == StatusVistoria.EM_ANDAMENTO);
    }
}
