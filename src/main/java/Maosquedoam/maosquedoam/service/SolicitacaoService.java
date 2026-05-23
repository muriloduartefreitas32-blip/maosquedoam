package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.dto.SolicitacaoDto;
import Maosquedoam.maosquedoam.entity.*;
import Maosquedoam.maosquedoam.repository.ItemRepostitory;
import Maosquedoam.maosquedoam.repository.SolicitacaoRepository;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
