# banking-customers-service

Microservicio REST para administrar personas y clientes. Es parte de la prueba técnica Devsu LLC y utiliza Java 21, Spring Boot, Gradle, PostgreSQL y RabbitMQ. Expone su API en el puerto `8081`, bajo el context path `/api`.

## Arquitectura y estructura

El proyecto organiza sus responsabilidades en los siguientes paquetes:

| Paquete | Responsabilidad |
|---|---|
| `domain` | Modelos y excepciones del dominio; puertos de persistencia |
| `application` | Servicios y casos de uso de personas y clientes |
| `infrastructure` | Adaptadores de PostgreSQL y configuración/publicación de eventos RabbitMQ |
| `presentation` | Controladores REST, DTO y manejo de errores |

La persistencia utiliza las entidades `PersonaEntity` y `ClienteEntity` con herencia `JOINED`: el cliente comparte su identificador con la persona asociada.

## Requisitos previos

- Java 21 (toolchain requerido por Gradle).
- PostgreSQL ejecutándose en el host Windows y accesible por TCP desde la aplicación.
- La base y el esquema existentes indicados en la siguiente sección.
- RabbitMQ para ejecución local. Al usar Docker Compose, el broker se inicia junto a los dos microservicios.
- Docker Desktop con Docker Compose para el despliegue coordinado.

## PostgreSQL

Customers se conecta a la base existente `banking_customers_db` y al esquema `customers`. Deben existir las tablas `personas` y `clientes` compatibles con las entidades del servicio.

La aplicación configura `spring.jpa.hibernate.ddl-auto: validate`: valida la estructura existente y no crea ni modifica el esquema de producción. No elimine, recree ni migre las bases como parte de esta configuración.

## Configuración

Las variables del proceso se leen desde `src/main/resources/application.yaml`. La ejecución local conserva los valores predeterminados indicados. No incluya credenciales reales ni secretos en archivos versionados.

| Variable | Predeterminado local | Descripción |
|---|---|---|
| `SERVER_PORT` | `8081` | Puerto HTTP del servicio |
| `DB_URL` | `jdbc:postgresql://localhost:5432/banking_customers_db` | URL JDBC. En Docker Compose se configura para la base Customers del PostgreSQL del host |
| `DB_USERNAME` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Valor local definido en `application.yaml`; configure uno propio fuera de desarrollo local | Contraseña de PostgreSQL |
| `RABBITMQ_HOST` | `localhost` | Host del broker; en Docker Compose es `rabbitmq` |
| `RABBITMQ_PORT` | `5672` | Puerto AMQP |
| `RABBITMQ_USERNAME` | Valor local definido en `application.yaml` | Usuario del broker |
| `RABBITMQ_PASSWORD` | Valor local definido en `application.yaml`; configure uno propio fuera de desarrollo local | Contraseña del broker |
| `CUSTOMERS_EXCHANGE` | `customers.events` | Exchange durable donde se publican los eventos |

En Docker Compose, el `.env.example` de la raíz documenta las variables de despliegue: `DB_USERNAME`, `DB_PASSWORD`, `CUSTOMERS_DB_URL`, `ACCOUNTS_DB_URL`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` y los puertos publicados opcionales. Compose asigna `CUSTOMERS_DB_URL` al `DB_URL` de este contenedor. PostgreSQL sigue ejecutándose en el host Windows; desde Docker se accede mediante `host.docker.internal`.

## Ejecución local con Gradle

Con PostgreSQL y RabbitMQ disponibles, desde la raíz del repositorio:

```powershell
Set-Location .\banking-customers-service
.\gradlew.bat bootRun
```

La API queda en `http://localhost:8081/api`.

## Ejecución con Docker Compose

El único Compose está en la raíz del repositorio. Copie y complete el archivo de variables y ejecute Compose desde el directorio raíz:

```powershell
Set-Location ..
Copy-Item .env.example .env
# Edite .env con las credenciales y URL de PostgreSQL correspondientes.
docker compose up --build -d
docker compose ps
```

Compose levanta RabbitMQ y ambos microservicios; PostgreSQL permanece en el host y no se modifica. Dentro de Docker, RabbitMQ usa el hostname `rabbitmq`. Accounts se comunica con Customers mediante `http://banking-customers-service:8081/api`. La API pública queda disponible en `http://localhost:8081/api`.

Logs y detención, también desde la raíz:

```powershell
docker compose logs -f banking-customers-service
docker compose down
```

## API REST

Todas las rutas de la siguiente tabla incluyen el prefijo `/api`. Los identificadores son numéricos.

### Personas

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/personas` | Crea una persona |
| `GET` | `/api/personas` | Lista personas |
| `GET` | `/api/personas/{id}` | Consulta una persona |
| `PUT` | `/api/personas/{id}` | Actualiza una persona |
| `DELETE` | `/api/personas/{id}` | Elimina una persona |

El cuerpo de creación y actualización utiliza `PersonaRequest`:

```json
{
  "nombre": "Ana Perez",
  "genero": "FEMENINO",
  "edad": 28,
  "identificacion": "ID-PERSONA-001",
  "direccion": "Calle Principal 123",
  "telefono": "5550101"
}
```

`nombre` e `identificacion` son obligatorios y no pueden estar en blanco. La longitud máxima es 150 y 30 caracteres respectivamente. `genero` admite hasta 30 caracteres, `direccion` hasta 250 y `telefono` hasta 30; `edad` es opcional.

Una creación devuelve `201 Created` y un objeto `PersonaResponse`:

```json
{
  "personaId": 10,
  "nombre": "Ana Perez",
  "genero": "FEMENINO",
  "edad": 28,
  "identificacion": "ID-PERSONA-001",
  "direccion": "Calle Principal 123",
  "telefono": "5550101"
}
```

`GET /api/personas` devuelve un arreglo de estos objetos; `GET /api/personas/{id}` y `PUT /api/personas/{id}` devuelven un objeto. Una eliminación correcta devuelve `204 No Content`.

### Clientes

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/clientes` | Crea un cliente |
| `GET` | `/api/clientes` | Lista clientes |
| `GET` | `/api/clientes/{id}` | Consulta un cliente |
| `PUT` | `/api/clientes/{id}` | Actualiza los datos del cliente |
| `PATCH` | `/api/clientes/{id}/estado` | Cambia únicamente el estado |
| `DELETE` | `/api/clientes/{id}` | Elimina un cliente |

Crear un cliente (`POST /api/clientes`) requiere los campos de persona y además `contrasena` y `estado`:

```json
{
  "nombre": "Carlos Andres Rodriguez",
  "genero": "MASCULINO",
  "edad": 30,
  "identificacion": "ID-CLIENTE-001",
  "direccion": "Calle 15 20-30",
  "telefono": "5550102",
  "contrasena": "cambie-este-valor",
  "estado": true
}
```

El valor de `contrasena` es solo ilustrativo: use un valor apropiado en su entorno y no lo publique. `nombre`, `identificacion` y `contrasena` son obligatorios y no pueden estar en blanco. `nombre` admite hasta 150 caracteres, `identificacion` hasta 30, `genero` hasta 30, `direccion` hasta 250 y `telefono` hasta 30. `estado` es obligatorio y booleano; `edad` es opcional.

La respuesta `201 Created` es `ClienteResponse`; no incluye la contraseña:

```json
{
  "clienteId": 10,
  "personaId": 10,
  "nombre": "Carlos Andres Rodriguez",
  "genero": "MASCULINO",
  "edad": 30,
  "identificacion": "ID-CLIENTE-001",
  "direccion": "Calle 15 20-30",
  "telefono": "5550102",
  "estado": true
}
```

`GET /api/clientes` devuelve un arreglo de `ClienteResponse`; `GET /api/clientes/{id}` y `PUT /api/clientes/{id}` devuelven un objeto. `PUT` utiliza el mismo cuerpo que el alta. El estado enviado debe ser booleano. Una eliminación correcta devuelve `204 No Content`.

Cambiar estado (`PATCH /api/clientes/10/estado`):

```json
{
  "estado": false
}
```

Devuelve el `ClienteResponse` actualizado (`200 OK`). Solo publica el evento de cambio cuando el estado realmente cambia.

## Códigos HTTP y manejo de errores

| Código | Uso |
|---|---|
| `200 OK` | Consultas y actualizaciones correctas |
| `201 Created` | Creación de persona o cliente |
| `204 No Content` | Eliminación correcta |
| `400 Bad Request` | Validación de campos o identificación duplicada |
| `404 Not Found` | Persona o cliente inexistente |
| `500 Internal Server Error` | Error inesperado |

El formato común de error incluye fecha, código HTTP, descripción y lista de mensajes. Por ejemplo, la validación fallida tiene esta forma:

```json
{
  "timestamp": "2026-11-01T12:00:00",
  "status": 400,
  "error": "Validación fallida",
  "messages": [
    "El nombre es obligatorio"
  ]
}
```

Los errores de recurso inexistente responden `404`; la identificación duplicada responde `400`. Los errores inesperados se registran en el servicio y se responden con un mensaje genérico, sin devolver detalles internos.

## Reglas de negocio y validaciones

- `identificacion` debe ser única tanto entre personas como entre clientes, ya que el cliente comparte los datos de persona mediante herencia `JOINED`.
- La persona necesita `nombre` e `identificacion`; el cliente necesita además `contrasena` y `estado`.
- El cliente se crea con la persona asociada y el identificador de ambas representaciones es el mismo.
- Crear un cliente no crea cuentas automáticamente.
- Las actualizaciones de cliente publican un evento de cambio de estado solo cuando cambia el valor persistido. El endpoint PATCH también evita emitir el evento si se envía el estado actual.
- El DTO de respuesta de cliente no expone `contrasena`.
- No se documentan límites de edad adicionales: el código recibe `edad` como `Short` opcional y no le aplica una validación de rango.

## Integración RabbitMQ

Customers publica eventos JSON en el exchange durable `customers.events`. Accounts consume la cola `accounts.customers.events`, enlazada con las routing keys `customer.created` y `customer.status.changed`. Customers no declara la cola consumidora.

| Operación | Routing key |
|---|---|
| Creación de cliente | `customer.created` |
| Cambio efectivo de estado del cliente | `customer.status.changed` |

Los eventos tienen los siguientes campos reales:

```json
{
  "eventId": "c5b44f7e-8968-4fa9-8ef4-3cce4a4b2466",
  "customerId": 10,
  "nombre": "Carlos Andres Rodriguez",
  "estado": true,
  "eventType": "CUSTOMER_CREATED",
  "occurredAt": "2026-11-01T17:00:00Z"
}
```

Para un cambio de estado, la forma es la misma y `eventType` es `CUSTOMER_STATUS_CHANGED`; `estado` refleja el estado nuevo. `eventId` se genera como UUID y `occurredAt` como fecha/hora UTC. El valor de `eventType` no es la routing key: los tipos concretos son `CUSTOMER_CREATED` y `CUSTOMER_STATUS_CHANGED`.

Los eventos se envían después del commit de la transacción PostgreSQL. Si RabbitMQ no está disponible al publicar, el error se registra con el identificador del evento y no se revierte ni se reporta como fallida una operación ya confirmada en PostgreSQL. Actualmente no hay reintento automático ni patrón Outbox; por lo tanto, el evento puede perderse durante una interrupción del broker.

Cuando Accounts recibe `customer.status.changed` con `estado: false`, desactiva todas las cuentas de ese cliente en una operación transaccional. `customer.created` no crea cuentas. Reactivar un cliente no reactiva automáticamente sus cuentas.

## Pruebas

Desde el directorio del servicio:

```powershell
.\gradlew.bat test
```

La suite incluye pruebas del modelo de cliente, pruebas de integración del controlador y persistencia con H2. Las pruebas verifican la creación y consulta de clientes sin exponer la contraseña, la publicación de eventos al cambiar el estado, la publicación para activación/desactivación y que no se publique un evento si el estado no cambia. La configuración de prueba usa H2; la ejecución normal del servicio utiliza PostgreSQL.

## Postman

La colección está en `postman/banking-customers-service.postman_collection.json`. En Postman, seleccione **Import > File** y elija el archivo. Después establezca la variable de colección `baseUrl` como `http://localhost:8081/api`; la colección importada deja inicialmente ese valor vacío.

La colección contiene solicitudes para clientes y cambios de estado. No requiere una contraseña real para importarse; use datos de prueba al enviar solicitudes.
