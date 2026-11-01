package com.devsullc.customers_service.application.service;

import com.devsullc.customers_service.domain.exception.ResourceNotFoundException;
import com.devsullc.customers_service.domain.model.Persona;
import com.devsullc.customers_service.domain.repository.PersonaRepositoryPort;
import com.devsullc.customers_service.presentation.dto.PersonaRequest;
import com.devsullc.customers_service.presentation.dto.PersonaResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaService {

    private final PersonaRepositoryPort personaRepository;

    public PersonaService(PersonaRepositoryPort personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<PersonaResponse> findAll() {
        return personaRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PersonaResponse findById(Long id) {
        return personaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id: " + id));
    }

    @Transactional
    public PersonaResponse create(PersonaRequest request) {
        validateUniqueIdentificacion(request.getIdentificacion());
        Persona persona = new Persona();
        persona.setNombre(request.getNombre());
        persona.setGenero(request.getGenero());
        persona.setEdad(request.getEdad());
        persona.setIdentificacion(request.getIdentificacion());
        persona.setDireccion(request.getDireccion());
        persona.setTelefono(request.getTelefono());
        return toResponse(personaRepository.save(persona));
    }

    @Transactional
    public PersonaResponse update(Long id, PersonaRequest request) {
        Persona existing = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con id: " + id));

        if (!existing.getIdentificacion().equals(request.getIdentificacion()) && personaRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new IllegalArgumentException("La identificación ya existe");
        }

        existing.setNombre(request.getNombre());
        existing.setGenero(request.getGenero());
        existing.setEdad(request.getEdad());
        existing.setIdentificacion(request.getIdentificacion());
        existing.setDireccion(request.getDireccion());
        existing.setTelefono(request.getTelefono());
        return toResponse(personaRepository.save(existing));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!personaRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Persona no encontrada con id: " + id);
        }
        personaRepository.deleteById(id);
    }

    private void validateUniqueIdentificacion(String identificacion) {
        if (personaRepository.existsByIdentificacion(identificacion)) {
            throw new IllegalArgumentException("La identificación ya existe");
        }
    }

    private PersonaResponse toResponse(Persona persona) {
        return new PersonaResponse(
                persona.getPersonaId(),
                persona.getNombre(),
                persona.getGenero(),
                persona.getEdad(),
                persona.getIdentificacion(),
                persona.getDireccion(),
                persona.getTelefono());
    }
}
