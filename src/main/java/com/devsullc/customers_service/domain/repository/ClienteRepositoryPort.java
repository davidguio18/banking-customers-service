package com.devsullc.customers_service.domain.repository;

import com.devsullc.customers_service.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    List<Cliente> findAll();

    Optional<Cliente> findById(Long id);

    Cliente save(Cliente cliente);

    void deleteById(Long id);

    boolean existsByIdentificacion(String identificacion);
}
