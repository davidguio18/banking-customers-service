package com.devsullc.customers_service.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe exceder 150 caracteres")
    private String nombre;

    @Size(max = 30, message = "El género no debe exceder 30 caracteres")
    private String genero;

    private Short edad;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 30, message = "La identificación no debe exceder 30 caracteres")
    private String identificacion;

    @Size(max = 250, message = "La dirección no debe exceder 250 caracteres")
    private String direccion;

    @Size(max = 30, message = "El teléfono no debe exceder 30 caracteres")
    private String telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;

}
