package com.devsullc.customers_service.infrastructure.messaging.publisher;

import com.devsullc.customers_service.domain.model.Cliente;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ClienteEventPublisher {

    private static final String CUSTOMER_CREATED_ROUTING_KEY = "customer.created";
    private static final String CUSTOMER_STATUS_CHANGED_ROUTING_KEY = "customer.status.changed";
    private static final String CUSTOMER_CREATED_EVENT_TYPE = "CUSTOMER_CREATED";
    private static final String CUSTOMER_STATUS_CHANGED_EVENT_TYPE = "CUSTOMER_STATUS_CHANGED";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;

    public ClienteEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            RabbitTemplate rabbitTemplate,
            @Value("${customers.messaging.exchange:customers.events}") String exchangeName) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
    }

    public void publishCustomerCreated(Cliente cliente) {
        applicationEventPublisher.publishEvent(new CustomerCreatedEvent(
                UUID.randomUUID(),
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getEstado(),
                CUSTOMER_CREATED_EVENT_TYPE,
                Instant.now()));
    }

    public void publishCustomerStatusChanged(Cliente cliente) {
        applicationEventPublisher.publishEvent(new CustomerStatusChangedEvent(
                UUID.randomUUID(),
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getEstado(),
                CUSTOMER_STATUS_CHANGED_EVENT_TYPE,
                Instant.now()));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCustomerCreated(CustomerCreatedEvent event) {
        send(CUSTOMER_CREATED_ROUTING_KEY, event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCustomerStatusChanged(CustomerStatusChangedEvent event) {
        send(CUSTOMER_STATUS_CHANGED_ROUTING_KEY, event);
    }

    private void send(String routingKey, CustomerEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, event);
        } catch (AmqpException exception) {
            log.error(
                    "RabbitMQ no pudo aceptar el evento {} con routing key {}. "
                            + "El cambio de base de datos ya fue confirmado; no habrá reintento automático.",
                    event.eventId(),
                    routingKey,
                    exception);
        }
    }
}
