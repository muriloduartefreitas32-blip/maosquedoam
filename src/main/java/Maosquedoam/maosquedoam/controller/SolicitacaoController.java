package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.SolicitacaoDto;
import Maosquedoam.maosquedoam.entity.Solicitacao;
import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import Maosquedoam.maosquedoam.service.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping("/solicitar")
    public ResponseEntity<Solicitacao> solicitar(@RequestBody @Valid SolicitacaoDto dto,
    Authentication authentication){
       String email = authentication.getName();
       return ResponseEntity.ok(solicitacaoService.solicitar(dto, email));
    }
    @GetMapping("/minhas")
    public ResponseEntity<List<Solicitacao>> minhasSolicitacoes(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(solicitacaoService.listarMinhaSolicitacoes(email));
    }
    @GetMapping("/recebidas")
    public ResponseEntity<List<Solicitacao>> solicitacoesRecebidas(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(solicitacaoService.listarSolicitacoesRecebidas(email));
    }
   @PatchMapping("/{id}/status")
    public ResponseEntity<Solicitacao> atualizarStatus(@PathVariable Long id,
                                                       @RequestParam StatusSolicitacao status,
                                                       Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(solicitacaoService.atualizarStatus(id,status,email));
   }
}
