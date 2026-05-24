package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.AvaliacaoDto;
import Maosquedoam.maosquedoam.entity.Avaliacao;
import Maosquedoam.maosquedoam.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<Avaliacao> avaliar(@RequestBody @Valid AvaliacaoDto dto,
                                             Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(avaliacaoService.avaliar(dto, email));
    }

    @GetMapping("/recebidas")
    public ResponseEntity<List<Avaliacao>> avaliacoesRecebidas(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(avaliacaoService.listarAvaliacoesRecebidas(email));
    }
}
