# banking-customers-service

Microservicio de personas y clientes para la prueba de desarrollador Java de Devsu LLC.

## Eventos RabbitMQ

Este servicio publica eventos JSON en el exchange durable `customers.events`; las colas y
bindings de consumo son responsabilidad de cada microservicio consumidor. Las routing keys
son `customer.created` y `customer.status.changed`. Ambos eventos comparten los campos
`eventId`, `customerId`, `nombre`, `estado`, `eventType` y `occurredAt`.

Los eventos se envían después del commit de la transacción de base de datos. Si RabbitMQ no
está disponible, el servicio registra el error con el identificador del evento y no revierte
ni reporta como fallida una operación que ya quedó confirmada en PostgreSQL. No hay reintento
automático ni outbox; por tanto, un evento puede perderse durante una interrupción del broker.

## Estado del cliente

`PATCH /api/clientes/{id}/estado` acepta un JSON como `{"estado": false}`. El evento de cambio
de estado solo se publica si el valor persistido realmente cambia.
