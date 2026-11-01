package com.devsullc.customers_service.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Entity
@Table(name = "clientes", schema = "customers")
@PrimaryKeyJoinColumn(name = "cliente_id", referencedColumnName = "persona_id")
public class ClienteEntity extends PersonaEntity {

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    public ClienteEntity() {
    }

    public ClienteEntity(Long personaId, String nombre, String genero, Short edad, String identificacion, String direccion,
                        String telefono, String contrasena, Boolean estado) {
        super(personaId, nombre, genero, edad, identificacion, direccion, telefono);
        this.contrasena = contrasena;
        this.estado = estado;
    }

}
