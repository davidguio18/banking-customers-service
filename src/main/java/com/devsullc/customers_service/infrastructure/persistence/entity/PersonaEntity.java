package com.devsullc.customers_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter 
@Setter
@Entity
@Table(name = "personas", schema = "customers")
@Inheritance(strategy = InheritanceType.JOINED)
public class PersonaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Long personaId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "genero", length = 30)
    private String genero;

    @Column(name = "edad")
    private Short edad;

    @Column(name = "identificacion", nullable = false, unique = true, length = 30)
    private String identificacion;

    @Column(name = "direccion", length = 250)
    private String direccion;

    @Column(name = "telefono", length = 30)
    private String telefono;

    public PersonaEntity() {
    }

    public PersonaEntity(Long personaId, String nombre, String genero, Short edad, String identificacion, String direccion,
                        String telefono) {
        this.personaId = personaId;
        this.nombre = nombre;
        this.genero = genero;
        this.edad = edad;
        this.identificacion = identificacion;
        this.direccion = direccion;
        this.telefono = telefono;
    }

}
