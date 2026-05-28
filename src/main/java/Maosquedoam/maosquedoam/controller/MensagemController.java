package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.MensagemDto;
import Maosquedoam.maosquedoam.entity.Mensagem;
import Maosquedoam.maosquedoam.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensagens")
public class MensagemController {

    @Autowired
    private MensagemService mensagemService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void enviarWs(@Payload MensagemDto dto,
                         SimpMessageHeaderAccessor accessor) {
        String email = accessor.getUser().getName();
        Mensagem mensagem = mensagemService.enviar(dto, email);
        messagingTemplate.convertAndSend(
                "/topic/solicitacao/" + dto.getSolicitacaoId(), mensagem);
    }

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
