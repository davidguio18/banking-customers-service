package com.devsullc.customers_service.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long clienteId;
    private Long personaId;
    private String nombre;
    private String genero;
    private Short edad;
    private String identificacion;
    private String direccion;
    private String telefono;
    private Boolean estado;
}
