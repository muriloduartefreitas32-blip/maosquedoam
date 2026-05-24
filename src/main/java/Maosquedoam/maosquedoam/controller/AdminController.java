package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarTodosUsuarios());
    }

    @PatchMapping("/usuarios/{id}/bloquear")
    public ResponseEntity<Map<String, String>> bloquearUsuario(@PathVariable Long id) {
        adminService.bloquearUsuario(id);
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Usuario bloqueado com sucesso");
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/usuarios/{id}/desbloquear")
    public ResponseEntity<Map<String, String>> desbloquearUsuario(@PathVariable Long id) {
        adminService.desbloquearUsuario(id);
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Usuario desbloqueado com sucesso");
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/itens/{id}")
    public ResponseEntity<Map<String, String>> deletarItem(@PathVariable Long id) {
        adminService.deletarItem(id);
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Item deletado com sucesso");
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/relatorio")
    public ResponseEntity<Map<String, Long>> relatorio() {
        return ResponseEntity.ok(adminService.gerarRelatorio());
    }
}
