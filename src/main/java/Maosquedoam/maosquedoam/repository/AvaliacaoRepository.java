package Maosquedoam.maosquedoam.repository;

import Maosquedoam.maosquedoam.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByAvaliadoId(Long avaliadoId);

    List<Avaliacao> findByAvaliadorId(Long avaliadorId);

    boolean existsBySolicitacaoIdAndAvaliadorId(Long solicitacaoId, Long avaliadorId);
}
