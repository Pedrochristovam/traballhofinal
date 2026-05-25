package br.com.sigevi.repository;

import br.com.sigevi.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Long> {

    List<Foto> findByVistoriaId(Long vistoriaId);
}
