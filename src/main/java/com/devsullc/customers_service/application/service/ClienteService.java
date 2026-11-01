package com.devsullc.customers_service.application.service;

import com.devsullc.customers_service.domain.exception.ResourceNotFoundException;
import com.devsullc.customers_service.domain.model.Cliente;
import com.devsullc.customers_service.domain.repository.ClienteRepositoryPort;
import com.devsullc.customers_service.infrastructure.messaging.publisher.ClienteEventPublisher;
import com.devsullc.customers_service.presentation.dto.ClienteRequest;
import com.devsullc.customers_service.presentation.dto.ClienteResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepositoryPort clienteRepository;
    private final ClienteEventPublisher clienteEventPublisher;

    public List<ClienteResponse> findAll() {
        return clienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ClienteResponse findById(Long id) {
        return clienteRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    @Transactional
    public ClienteResponse create(ClienteRequest request) {
        validateUniqueIdentificacion(request.getIdentificacion());

        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(request.getContrasena());
        cliente.setEstado(request.getEstado() == null ? Boolean.TRUE : request.getEstado());

        Cliente created = clienteRepository.save(cliente);
        ClienteResponse response = toResponse(created);
        clienteEventPublisher.publishCustomerCreated(created);
        return response;
    }

    @Transactional
    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente existing = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (!existing.getIdentificacion().equals(request.getIdentificacion()) && clienteRepository.existsByIdentificacion(request.getIdentificacion())) {
            throw new IllegalArgumentException("La identificación ya existe");
        }

        boolean statusChanged = !Objects.equals(existing.getEstado(), request.getEstado());

        existing.setNombre(request.getNombre());
        existing.setGenero(request.getGenero());
        existing.setEdad(request.getEdad());
        existing.setIdentificacion(request.getIdentificacion());
        existing.setDireccion(request.getDireccion());
        existing.setTelefono(request.getTelefono());
        existing.setContrasena(request.getContrasena());
        existing.setEstado(request.getEstado() == null ? existing.getEstado() : request.getEstado());

        Cliente updated = clienteRepository.save(existing);
        if (statusChanged) {
            clienteEventPublisher.publishCustomerStatusChanged(updated);
        }
        return toResponse(updated);
    }

    @Transactional
    public ClienteResponse updateStatus(Long id, Boolean estado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        if (!Objects.equals(cliente.getEstado(), estado)) {
            cliente.setEstado(estado);
            cliente = clienteRepository.save(cliente);
            clienteEventPublisher.publishCustomerStatusChanged(cliente);
        }

        return toResponse(cliente);
    }

    @Transactional
    public void deleteById(Long id) {
        if (clienteRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private void validateUniqueIdentificacion(String identificacion) {
        if (clienteRepository.existsByIdentificacion(identificacion)) {
            throw new IllegalArgumentException("La identificación ya existe");
        }
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getClienteId(),
                cliente.getPersonaId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getEstado());
    }
}
