package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.MensagemDto;
import Maosquedoam.maosquedoam.entity.Mensagem;
import Maosquedoam.maosquedoam.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensagens ")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;

    @PostMapping("/enviar")
    public ResponseEntity<Mensagem> enviar(@RequestBody @Valid MensagemDto dto,
    Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(mensagemService.enviar(dto, email));
    }
    @GetMapping("/solicitacao/{solicitacaoId}")
    public ResponseEntity<List<Mensagem>> listarPorSolicitacao(@PathVariable Long solicitacaoId,
                                                               Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(mensagemService.listarPorSolicitacao(solicitacaoId, email));
    }
    @PatchMapping("/{id}/lida")
    public ResponseEntity<Mensagem> marcarComoLida(@PathVariable Long id,
                                                   Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(mensagemService.marcarComoLida(id, email));
    }
}
