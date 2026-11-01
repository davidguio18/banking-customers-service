package com.devsullc.customers_service.infrastructure.messaging.publisher;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID eventId,
        Long customerId,
        String nombre,
        Boolean estado,
        String eventType,
        Instant occurredAt) implements CustomerEvent {
}
