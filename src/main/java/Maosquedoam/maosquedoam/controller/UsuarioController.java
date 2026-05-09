package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.UsuarioDto;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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




}
