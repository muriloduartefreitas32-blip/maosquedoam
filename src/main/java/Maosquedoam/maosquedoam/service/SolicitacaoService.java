package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.dto.SolicitacaoDto;
import Maosquedoam.maosquedoam.entity.*;
import Maosquedoam.maosquedoam.enuns.StatusItem;
import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import Maosquedoam.maosquedoam.repository.ItemRepostitory;
import Maosquedoam.maosquedoam.repository.SolicitacaoRepository;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private ItemRepostitory itemRepostitory;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Solicitacao solicitar(SolicitacaoDto dto, String emailBeneficiario){
        Usuario beneficiario = usuarioRepository.findByEmail(emailBeneficiario)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        Item item = itemRepostitory.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        if(item.getStatus() != StatusItem.DISPONIVEL){
            throw new RuntimeException("Item não esta disponivel");
        }
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setItem(item);
        solicitacao.setBeneficiario((beneficiario));
        solicitacao.setMensagem(dto.getMensagem());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        item.setStatus(StatusItem.RESERVADO);;
        itemRepostitory.save(item);

        return solicitacaoRepository.save((solicitacao));
    }
     public List<Solicitacao> listarMinhaSolicitacoes(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return solicitacaoRepository.findByBeneficiarioId(usuario.getId());

     }
    public List<Solicitacao> listarSolicitacoesRecebidas(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return solicitacaoRepository.findByItemDoadorId(usuario.getId());
    }
     public Solicitacao atualizarStatus(Long id,StatusSolicitacao novoStatus, String email){
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        if(!solicitacao.getItem().getDoador().getEmail().equals(email)){
            throw new RuntimeException("Sem permissão para atualizar essa solicitação");
        }
        solicitacao.setStatus((novoStatus));
        solicitacao.setDataAtualizacao((LocalDateTime.now()) );

        if(novoStatus == StatusSolicitacao.CONCLUIDA){
            solicitacao.getItem().setStatus((StatusItem.DOADO) );
            itemRepostitory.save(solicitacao.getItem());
        }
        if( novoStatus == StatusSolicitacao.RECUSADA || novoStatus == StatusSolicitacao.CANCELADA){
            solicitacao.getItem().setStatus(StatusItem.DISPONIVEL);
        }
        return solicitacaoRepository.save(solicitacao);
     }
}
