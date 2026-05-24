package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.dto.MensagemDto;
import Maosquedoam.maosquedoam.entity.Mensagem;
import Maosquedoam.maosquedoam.entity.Solicitacao;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.repository.MensagemRepository;
import Maosquedoam.maosquedoam.repository.SolicitacaoRepository;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    public Mensagem enviar(MensagemDto dto, String emailRemetente) {
        Usuario remetente = usuarioRepository.findByEmail(emailRemetente)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        Solicitacao solicitacao = solicitacaoRepository.findById(dto.getSolicitacaoId())
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Define destinatário automaticamente pelo contexto da solicitação
        Usuario destinatario;
        if (solicitacao.getBeneficiario().getEmail().equals(emailRemetente)) {
            destinatario = solicitacao.getItem().getDoador();
        } else if (solicitacao.getItem().getDoador().getEmail().equals(emailRemetente)) {
            destinatario = solicitacao.getBeneficiario();
        } else {
            throw new RuntimeException("Sem permissão para enviar mensagem nesta solicitação");
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setRemetente(remetente);
        mensagem.setDestinatario(destinatario);
        mensagem.setSolicitacao(solicitacao);
        mensagem.setConteudo(dto.getConteudo());
        mensagem.setLida(false);

        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> listarPorSolicitacao(Long solicitacaoId, String email) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));

        // Só doador ou beneficiário da solicitação pode ver as mensagens
        boolean isDoador = solicitacao.getItem().getDoador().getEmail().equals(email);
        boolean isBeneficiario = solicitacao.getBeneficiario().getEmail().equals(email);

        if (!isDoador && !isBeneficiario) {
            throw new RuntimeException("Sem permissão para ver estas mensagens");
        }

        return mensagemRepository.findBySolicitacaoId(solicitacaoId);
    }

    public List<Mensagem> listarNaoLidas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return mensagemRepository.findByDestinatarioIdAndLidaFalse(usuario.getId());
    }

    public Mensagem marcarComoLida(Long id, String email) {
        Mensagem mensagem = mensagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));

        if (!mensagem.getDestinatario().getEmail().equals(email)) {
            throw new RuntimeException("Sem permissão para marcar esta mensagem");
        }

        mensagem.setLida(true);
        return mensagemRepository.save(mensagem);
    }
}

