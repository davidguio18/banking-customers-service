package com.devsullc.customers_service.domain.repository;

import com.devsullc.customers_service.domain.model.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaRepositoryPort {

    List<Persona> findAll();

    Optional<Persona> findById(Long id);

    Persona save(Persona persona);

    void deleteById(Long id);

    boolean existsByIdentificacion(String identificacion);
}
