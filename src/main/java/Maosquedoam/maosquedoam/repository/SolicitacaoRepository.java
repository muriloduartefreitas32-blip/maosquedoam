package Maosquedoam.maosquedoam.repository;

import Maosquedoam.maosquedoam.entity.Solicitacao;
import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByBeneficiarioId(Long beneficiarioId);

    List<Solicitacao> findByItemDoadorId(Long doadorId);

    List<Solicitacao> findByStatus(StatusSolicitacao status);

    long countByStatus(StatusSolicitacao status);


}
