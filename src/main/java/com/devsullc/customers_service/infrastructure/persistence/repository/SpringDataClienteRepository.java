package com.devsullc.customers_service.infrastructure.persistence.repository;

import com.devsullc.customers_service.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, Long> {

    @Query("select case when count(c) > 0 then true else false end from ClienteEntity c where c.identificacion = :identificacion")
    boolean existsByIdentificacion(@Param("identificacion") String identificacion);
}
