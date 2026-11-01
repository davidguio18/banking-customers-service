package com.devsullc.customers_service.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void debeCrearClienteConSusDatosYEstadoActivo() {

        Cliente cliente = new Cliente(
                1L,
                "Carlos Andres Rodriguez",
                "MASCULINO",
                (short) 30,
                "1098765432",
                "Calle 15 # 20-30",
                "3001234567",
                "Prueba123",
                true
        );

        assertEquals(1L, cliente.getClienteId());
        assertEquals(1L, cliente.getPersonaId());
        assertEquals("Carlos Andres Rodriguez", cliente.getNombre());
        assertEquals("MASCULINO", cliente.getGenero());
        assertEquals((short) 30, cliente.getEdad());
        assertEquals("1098765432", cliente.getIdentificacion());
        assertTrue(cliente.getEstado());
    }

    @Test
    void debeSincronizarClienteIdConPersonaId() {

        Cliente cliente = new Cliente();

        cliente.setClienteId(10L);

        assertEquals(10L, cliente.getClienteId());
        assertEquals(10L, cliente.getPersonaId());
    }

    @Test
    void debeActualizarEstadoDelCliente() {

        Cliente cliente = new Cliente(
                1L,
                "Carlos Andres Rodriguez",
                "MASCULINO",
                (short) 30,
                "1098765432",
                "Calle 15 # 20-30",
                "3001234567",
                "Prueba123",
                true
        );

        cliente.setEstado(false);

        assertFalse(cliente.getEstado());
    }
}
