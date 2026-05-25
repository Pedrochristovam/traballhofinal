package br.com.sigevi.repository;

import br.com.sigevi.model.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    List<Relatorio> findByVistoriaIdOrderByCriadoEmDesc(Long vistoriaId);
}
