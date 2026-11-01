package com.devsullc.customers_service.infrastructure.persistence.repository;

import com.devsullc.customers_service.domain.model.Persona;
import com.devsullc.customers_service.domain.repository.PersonaRepositoryPort;
import com.devsullc.customers_service.infrastructure.persistence.entity.PersonaEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonaRepository implements PersonaRepositoryPort {

    private final SpringDataPersonaRepository delegate;

    public JpaPersonaRepository(SpringDataPersonaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Persona> findAll() {
        return delegate.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Persona> findById(Long id) {
        return delegate.findById(id).map(this::toDomain);
    }

    @Override
    public Persona save(Persona persona) {
        PersonaEntity entity = toEntity(persona);
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

    private Persona toDomain(PersonaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Persona(
                entity.getPersonaId(),
                entity.getNombre(),
                entity.getGenero(),
                entity.getEdad(),
                entity.getIdentificacion(),
                entity.getDireccion(),
                entity.getTelefono());
    }

    private PersonaEntity toEntity(Persona persona) {
        if (persona == null) {
            return null;
        }
        PersonaEntity entity = new PersonaEntity();
        entity.setPersonaId(persona.getPersonaId());
        entity.setNombre(persona.getNombre());
        entity.setGenero(persona.getGenero());
        entity.setEdad(persona.getEdad());
        entity.setIdentificacion(persona.getIdentificacion());
        entity.setDireccion(persona.getDireccion());
        entity.setTelefono(persona.getTelefono());
        return entity;
    }
}
