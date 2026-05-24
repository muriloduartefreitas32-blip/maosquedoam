package Maosquedoam.maosquedoam.repository;

import Maosquedoam.maosquedoam.entity.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findBySolicitacaoId(Long solicitacaoId);

    List<Mensagem> findByDestinatarioIdAndLidaFalse(Long destinatarioId);

    List<Mensagem> findByRemetenteIdOrDestinatarioId(Long remetenteId, Long destinatarioId);
}
