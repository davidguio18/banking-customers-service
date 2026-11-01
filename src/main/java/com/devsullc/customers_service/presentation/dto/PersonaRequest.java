package com.devsullc.customers_service.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PersonaRequest {

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Short getEdad() {
        return edad;
    }

    public void setEdad(Short edad) {
        this.edad = edad;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
