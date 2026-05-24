package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import Maosquedoam.maosquedoam.repository.ItemRepostitory;
import Maosquedoam.maosquedoam.repository.SolicitacaoRepository;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ItemRepostitory itemRepostitory;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public void desbloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encotrado"));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public void deletarItem(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        itemRepostitory.deleteById(id);
    }

    public void bloquearUsuario(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        usuarioRepository.delete(usuario);
    }

    public Map<String, Long> gerarRelatorio() {
        Map<String, Long> relatorio = new HashMap<>();
        relatorio.put("totalUsuarios", usuarioRepository.count());
        relatorio.put("totalItens", itemRepostitory.count());
        relatorio.put("totalSolicitacoes", solicitacaoRepository.count());
        relatorio.put("solicitacoesConcluidas", solicitacaoRepository
                .countByStatus(StatusSolicitacao.CONCLUIDA));
        return relatorio;

    }
}
