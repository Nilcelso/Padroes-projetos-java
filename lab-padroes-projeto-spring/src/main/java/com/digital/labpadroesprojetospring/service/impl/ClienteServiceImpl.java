package com.digital.labpadroesprojetospring.service.impl;

import com.digital.labpadroesprojetospring.model.Cliente;
import com.digital.labpadroesprojetospring.model.ClienteRepository;
import com.digital.labpadroesprojetospring.model.Endereco;
import com.digital.labpadroesprojetospring.model.EnderecoRepository;
import com.digital.labpadroesprojetospring.service.ClienteService;
import com.digital.labpadroesprojetospring.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {
    //Singleton1: Injetar os componentes do Spring com @Autowired
    @Autowired
    public ClienteRepository clienteRepository;
    @Autowired
    public EnderecoRepository enderecoRepository;
    @Autowired
    public ViaCepService viaCepService;

    //Strategy: Implementar os metodos defindos na interface
    //Facade: Abstrair integraçóes com subsistemas, provendo uma interface simples.


    @Override
    public Iterable<Cliente> buscarTodos(){
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
       Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente){
        //Buscar Cliente por ID, caso exista:
        Optional<Cliente> clienteBd = clienteRepository.findById(id);
        if (clienteBd.isPresent()) {
            //Verificar se o endereço do cliente ja existe(pelo cep)
            //Caso nao exista intregrar com o o via cep e persistir o retorno 
            //Altera o Cliente, vinculando o entedeço (novo ou existente)
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    private void salvarClienteComCep(Cliente cliente) {
        //Verificar se o endereço do cliente já existe.
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
            // Casso não exista, integrar com o viacep e persiste o retorno
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecoRepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        //Inserir cliente, vinculado o endereço (novo ou existente)
        clienteRepository.save(cliente);
    }

}
