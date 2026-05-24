package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.LoginDto;
import Maosquedoam.maosquedoam.dto.NovaSenhaDto;
import Maosquedoam.maosquedoam.dto.RecuperacaoDto;
import Maosquedoam.maosquedoam.dto.UsuarioDto;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(
            @RequestBody UsuarioDto dto
            ){
        Usuario usuario = service.cadastrar(dto);
        return ResponseEntity.ok(usuario);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto dto){
        String resposta = service.login(dto);

        return ResponseEntity.ok(resposta);
    }
    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(
            @RequestBody @Valid RecuperacaoDto dto) {
        service.solicitarRecuperacao(dto.getEmail());
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Código enviado para o email!");
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(
            @RequestBody @Valid NovaSenhaDto dto) {
        service.redefinirSenha(dto);
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Senha redefinida com sucesso!");
        return ResponseEntity.ok(resposta);
    }




}
