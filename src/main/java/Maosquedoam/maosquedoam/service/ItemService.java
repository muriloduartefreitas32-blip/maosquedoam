package Maosquedoam.maosquedoam.service;

import Maosquedoam.maosquedoam.dto.ItemDto;
import Maosquedoam.maosquedoam.entity.Item;
import Maosquedoam.maosquedoam.enuns.StatusItem;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.repository.ItemRepostitory;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemRepostitory itemRepostitory;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Item cadastrar(ItemDto dto, String emailDoador){
        Usuario doador = usuarioRepository.findByEmail(emailDoador)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        Item item = new Item();
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(dto.getCategoria());
        item.setEstado(dto.getEstado());
        item.setQuantidade(dto.getQuantidade());
        item.setLocalizacao(dto.getLocalizacao());
        item.setImagemUrl(dto.getImagemUrl());
        item.setStatus(StatusItem.DISPONIVEL);
        item.setDoador(doador);

        return itemRepostitory.save(item);
    }
    public List<Item> listarTodos(){
        return itemRepostitory.findAll();
    }
    public List<Item> buscarPorCategoria(String categoria) {
        return itemRepostitory.findByCategoria(categoria);
    }
    public List<Item> buscarPorLocalizacao(String localizacao){
        return itemRepostitory.findByLocalizacaoContainingIgnoreCase(localizacao);
    }
    public List<Item> buscarPorPalavraChave(String palavra){
        return itemRepostitory.findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(palavra, palavra);
    }
    public Item editar(Long id, ItemDto dto,String emailDoador){
        Item item = itemRepostitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));

        if(!item.getDoador().getEmail().equals((emailDoador))){
            throw new RuntimeException("Sem permissão para editar esse item");
        }
        item.setTitulo(dto.getTitulo());
        item.setDescricao(dto.getDescricao());
        item.setCategoria(dto.getCategoria());
        item.setEstado(dto.getEstado());
        item.setQuantidade(dto.getQuantidade());
        item.setLocalizacao(dto.getLocalizacao());
        item.setImagemUrl(dto.getImagemUrl());

        return itemRepostitory.save(item);
    }
    public void deletar(Long id, String emailDoador){
        Item item = itemRepostitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Item não encontrado"));
        if(!item.getDoador().getEmail().equals(emailDoador)){
            throw new RuntimeException("Sem permissão para deletar esse item");
        }
        itemRepostitory.delete((item));

    }
}
