package Maosquedoam.maosquedoam.controller;

import Maosquedoam.maosquedoam.dto.ItemDto;
import Maosquedoam.maosquedoam.entity.Item;
import Maosquedoam.maosquedoam.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

@RestController
@RequestMapping("/itens")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/cadastrar")
    public ResponseEntity<Item> cadastrar(@RequestBody @Valid ItemDto dto, Authentication authentication){
        String email = authentication.getName();
        Item item = itemService.cadastrar(dto, email);
        return ResponseEntity.ok(item);
    }
    @GetMapping
    public ResponseEntity<List<Item>> listarTodos() {
        return ResponseEntity.ok(itemService.listarTodos());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Item>> buscarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(itemService.buscarPorCategoria(categoria));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Item>> buscarPorPalavraChave(@RequestParam String palavra) {
        return ResponseEntity.ok(itemService.buscarPorPalavraChave(palavra));
    }

    @GetMapping("/localizacao")
    public ResponseEntity<List<Item>> buscarPorLocalizacao(@RequestParam String localizacao) {
        return ResponseEntity.ok(itemService.buscarPorLocalizacao(localizacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> editar(@PathVariable Long id,
                                       @RequestBody @Valid ItemDto dto,
                                       Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(itemService.editar(id, dto, email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
                                        Authentication authentication) {
        String email = authentication.getName();
        itemService.deletar(id, email);
        return ResponseEntity.noContent().build();
    }
}
