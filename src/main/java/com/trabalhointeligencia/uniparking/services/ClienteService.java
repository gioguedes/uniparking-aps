package com.trabalhointeligencia.uniparking.services;

import com.trabalhointeligencia.uniparking.models.Cliente;
import com.trabalhointeligencia.uniparking.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente salvarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientesPorUsuario(String username) {
        return clienteRepository.findByAtivoTrueAndUsuario_Login(username);
    }

    public Page<Cliente> findPaginated(String username, Pageable pageable) {
        List<Cliente> clientesAtivos = listarClientesPorUsuario(username);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), clientesAtivos.size());
        List<Cliente> sublist = clientesAtivos.subList(start, end);
        return new PageImpl<>(sublist, pageable, clientesAtivos.size());
    }

    public Optional<Cliente> buscarClientePorId(Integer id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if(clienteOpt.isPresent() && clienteOpt.get().getAtivo()){
            return clienteOpt;
        }
        return Optional.empty();
    }

    public void excluirCliente(Integer id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if(clienteOpt.isPresent()){
            Cliente cliente = clienteOpt.get();
            cliente.setAtivo(false);
            clienteRepository.save(cliente);
        }
    }
}
