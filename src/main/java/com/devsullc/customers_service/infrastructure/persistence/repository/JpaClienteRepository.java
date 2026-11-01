package com.devsullc.customers_service.infrastructure.persistence.repository;

import com.devsullc.customers_service.domain.model.Cliente;
import com.devsullc.customers_service.domain.repository.ClienteRepositoryPort;
import com.devsullc.customers_service.infrastructure.persistence.entity.ClienteEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaClienteRepository implements ClienteRepositoryPort {

    private final SpringDataClienteRepository delegate;

    public JpaClienteRepository(SpringDataClienteRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Cliente> findAll() {
        return delegate.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return delegate.findById(id).map(this::toDomain);
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        return toDomain(delegate.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        delegate.deleteById(id);
    }

    @Override
    public boolean existsByIdentificacion(String identificacion) {
        return delegate.existsByIdentificacion(identificacion);
    }

    private Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setPersonaId(entity.getPersonaId());
        cliente.setClienteId(entity.getPersonaId());
        cliente.setNombre(entity.getNombre());
        cliente.setGenero(entity.getGenero());
        cliente.setEdad(entity.getEdad());
        cliente.setIdentificacion(entity.getIdentificacion());
        cliente.setDireccion(entity.getDireccion());
        cliente.setTelefono(entity.getTelefono());
        cliente.setContrasena(entity.getContrasena());
        cliente.setEstado(entity.getEstado());
        return cliente;
    }

    private ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        ClienteEntity entity = new ClienteEntity();
        entity.setPersonaId(cliente.getPersonaId());
        entity.setNombre(cliente.getNombre());
        entity.setGenero(cliente.getGenero());
        entity.setEdad(cliente.getEdad());
        entity.setIdentificacion(cliente.getIdentificacion());
        entity.setDireccion(cliente.getDireccion());
        entity.setTelefono(cliente.getTelefono());
        entity.setContrasena(cliente.getContrasena());
        entity.setEstado(cliente.getEstado() == null ? Boolean.TRUE : cliente.getEstado());
        return entity;
    }
}
