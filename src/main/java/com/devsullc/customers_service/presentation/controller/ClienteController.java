package com.devsullc.customers_service.presentation.controller;

import com.devsullc.customers_service.application.service.ClienteService;
import com.devsullc.customers_service.presentation.dto.ClienteRequest;
import com.devsullc.customers_service.presentation.dto.ClienteResponse;
import com.devsullc.customers_service.presentation.dto.EstadoRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public List<ClienteResponse> findAll() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    public ClienteResponse findById(@PathVariable("id") Long id) {
        return clienteService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ClienteResponse update(@PathVariable("id") Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.update(id, request);
    }

    @PatchMapping("/{id}/estado")
    public ClienteResponse updateStatus(@PathVariable("id") Long id, @Valid @RequestBody EstadoRequest request) {
        return clienteService.updateStatus(id, request.getEstado());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        clienteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
