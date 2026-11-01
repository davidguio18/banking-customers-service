package com.devsullc.customers_service.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EstadoRequest {

    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
}
