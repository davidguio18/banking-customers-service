package com.devsullc.customers_service.presentation.controller;

import com.devsullc.customers_service.application.service.PersonaService;
import com.devsullc.customers_service.presentation.dto.PersonaRequest;
import com.devsullc.customers_service.presentation.dto.PersonaResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<PersonaResponse> findAll() {
        return personaService.findAll();
    }

    @GetMapping("/{id}")
    public PersonaResponse findById(@PathVariable("id") Long id) {
        return personaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<PersonaResponse> create(@Valid @RequestBody PersonaRequest request) {
        PersonaResponse response = personaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public PersonaResponse update(@PathVariable("id") Long id, @Valid @RequestBody PersonaRequest request) {
        return personaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
