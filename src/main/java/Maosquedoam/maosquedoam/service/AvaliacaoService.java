package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.dto.AvaliacaoDto;
import Maosquedoam.maosquedoam.entity.Avaliacao;
import Maosquedoam.maosquedoam.entity.Solicitacao;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import Maosquedoam.maosquedoam.repository.AvaliacaoRepository;
import Maosquedoam.maosquedoam.repository.SolicitacaoRepository;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Avaliacao avaliar(AvaliacaoDto dto, String emailAvaliador) {
        Usuario avaliador = usuarioRepository.findByEmail(emailAvaliador)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        Solicitacao solicitacao = solicitacaoRepository.findById(dto.getSolicitacaoId())
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.CONCLUIDA) {
            throw new RuntimeException("Só é possível avaliar solicitações concluídas");
        }

        boolean jaAvaliou = avaliacaoRepository.existsBySolicitacaoIdAndAvaliadorId(
                dto.getSolicitacaoId(), avaliador.getId()
        );
        if (jaAvaliou) {
            throw new RuntimeException("Você já avaliou esta solicitação");
        }

        // Define quem é o avaliado
        Usuario avaliado;
        if (solicitacao.getBeneficiario().getEmail().equals(emailAvaliador)) {
            avaliado = solicitacao.getItem().getDoador(); // beneficiario avalia doador
        } else if (solicitacao.getItem().getDoador().getEmail().equals(emailAvaliador)) {
            avaliado = solicitacao.getBeneficiario(); // doador avalia beneficiario
        } else {
            throw new RuntimeException("Sem permissão para avaliar esta solicitação");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAvaliado(avaliado);
        avaliacao.setSolicitacao(solicitacao);
        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarAvaliacoesRecebidas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return avaliacaoRepository.findByAvaliadoId(usuario.getId());
    }
}
