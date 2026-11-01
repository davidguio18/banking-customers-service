package com.devsullc.customers_service.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.devsullc.customers_service.infrastructure.messaging.publisher.CustomerCreatedEvent;
import com.devsullc.customers_service.infrastructure.messaging.publisher.CustomerStatusChangedEvent;
import com.devsullc.customers_service.presentation.dto.ClienteRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void resetRabbitTemplate() {
        reset(rabbitTemplate);
    }

    @Test
    void shouldCreateAndRetrieveCustomerWithoutExposingPassword() throws Exception {
        ClienteRequest request = requestWithStatus(true);
        MvcResult createResult = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.estado").value(true))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andReturn();

        long customerId = customerId(createResult);
        var eventCaptor = forClass(CustomerCreatedEvent.class);
        verify(rabbitTemplate).convertAndSend(
                eq("customers.events"), eq("customer.created"), eventCaptor.capture());
        CustomerCreatedEvent event = eventCaptor.getValue();
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
        org.junit.jupiter.api.Assertions.assertEquals(customerId, event.customerId());
        org.junit.jupiter.api.Assertions.assertEquals("Ana María", event.nombre());
        org.junit.jupiter.api.Assertions.assertEquals(true, event.estado());
        org.junit.jupiter.api.Assertions.assertEquals("CUSTOMER_CREATED", event.eventType());

        mockMvc.perform(get("/clientes/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(customerId))
                .andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        MvcResult getAllResult = mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andReturn();
        for (JsonNode customer : objectMapper.readTree(getAllResult.getResponse().getContentAsString())) {
            assertFalse(customer.has("contrasena"));
        }

        clearInvocations(rabbitTemplate);
        mockMvc.perform(put("/clientes/{id}", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contrasena").doesNotExist());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void shouldUpdateCustomerToInactiveAndPublishStatusChangedEvent() throws Exception {
        long customerId = createCustomer(true);
        clearInvocations(rabbitTemplate);

        mockMvc.perform(patch("/clientes/{id}/estado", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(false))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        var eventCaptor = forClass(CustomerStatusChangedEvent.class);
        verify(rabbitTemplate).convertAndSend(
                eq("customers.events"), eq("customer.status.changed"), eventCaptor.capture());
        CustomerStatusChangedEvent event = eventCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(customerId, event.customerId());
        org.junit.jupiter.api.Assertions.assertEquals(false, event.estado());
        org.junit.jupiter.api.Assertions.assertEquals("CUSTOMER_STATUS_CHANGED", event.eventType());
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldUpdateCustomerToActiveAndPublishStatusChangedEvent() throws Exception {
        long customerId = createCustomer(false);
        clearInvocations(rabbitTemplate);

        mockMvc.perform(patch("/clientes/{id}/estado", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(true));

        var eventCaptor = forClass(CustomerStatusChangedEvent.class);
        verify(rabbitTemplate).convertAndSend(
                eq("customers.events"), eq("customer.status.changed"), eventCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(true, eventCaptor.getValue().estado());
    }

    @Test
    void shouldNotPublishEventWhenCustomerStatusDoesNotChange() throws Exception {
        long customerId = createCustomer(true);
        clearInvocations(rabbitTemplate);

        mockMvc.perform(patch("/clientes/{id}/estado", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value(true));

        verifyNoInteractions(rabbitTemplate);
    }

    private long createCustomer(boolean estado) throws Exception {
        MvcResult result = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithStatus(estado))))
                .andExpect(status().isCreated())
                .andReturn();
        return customerId(result);
    }

    private ClienteRequest requestWithStatus(boolean estado) {
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Ana María");
        request.setGenero("FEMENINO");
        request.setEdad((short) 30);
        request.setIdentificacion(UUID.randomUUID().toString().substring(0, 24));
        request.setDireccion("Calle 123");
        request.setTelefono("3001234567");
        request.setContrasena("secreto123");
        request.setEstado(estado);
        return request;
    }

    private long customerId(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("clienteId")
                .asLong();
    }
}
