package br.com.sigevi.repository;

import br.com.sigevi.model.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    Optional<Imovel> findByMatricula(String matricula);

    List<Imovel> findByEnderecoContainingIgnoreCase(String endereco);

    boolean existsByMatricula(String matricula);

    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}
