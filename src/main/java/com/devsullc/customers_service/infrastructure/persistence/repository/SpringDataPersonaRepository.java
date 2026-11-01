package com.devsullc.customers_service.infrastructure.persistence.repository;

import com.devsullc.customers_service.infrastructure.persistence.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataPersonaRepository extends JpaRepository<PersonaEntity, Long> {

    @Query("select case when count(p) > 0 then true else false end from PersonaEntity p where p.identificacion = :identificacion")
    boolean existsByIdentificacion(@Param("identificacion") String identificacion);
}
