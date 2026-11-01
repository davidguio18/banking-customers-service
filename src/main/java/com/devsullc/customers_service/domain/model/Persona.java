package com.devsullc.customers_service.domain.model;

import java.io.Serial;
import java.io.Serializable;

public class Persona implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long personaId;
    private String nombre;
    private String genero;
    private Short edad;
    private String identificacion;
    private String direccion;
    private String telefono;

    public Persona() {
    }

    public Persona(Long personaId, String nombre, String genero, Short edad, String identificacion, String direccion,
                   String telefono) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.genero = genero;
        this.edad = edad;
        this.identificacion = identificacion;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

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
