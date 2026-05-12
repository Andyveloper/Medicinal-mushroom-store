# Setas Backend

REST API para una tienda en línea de hongos medicinales construida con **Spring Boot** siguiendo arquitectura hexagonal (Ports & Adapters). Incluye integración con **Stripe** para pagos en línea.

## Tech Stack

- Java 17
- Spring Boot 4.0.5 (Spring 6 / Jakarta EE)
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Stripe Java SDK 26.3.0
- Spring Boot Actuator
- Lombok
- JUnit 5

## Requisitos previos

- Java 17+
- PostgreSQL corriendo en `localhost:5432`
- Base de datos `setas_db` creada
- Cuenta de Stripe (claves de API)

## Configuración

Edita `src/main/resources/application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/setas_db
spring.datasource.username=postgres
spring.datasource.password=tu_password

stripe.secret.key=sk_test_...
stripe.webhook.secret=whsec_...
```

> El esquema se gestiona automáticamente con `ddl-auto=update`.

## Ejecutar

```bash
# Levantar la aplicación
./mvnw spring-boot:run

# Compilar y empaquetar (sin tests)
./mvnw clean package -DskipTests

# Ejecutar tests
./mvnw test
```

La API queda disponible en `http://localhost:8080`.

## Endpoints

### Autenticación (`/api/auth`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registrar usuario → retorna JWT |
| POST | `/api/auth/login` | Iniciar sesión → retorna JWT |
| GET | `/api/auth/{email}` | Buscar usuario por email |
| DELETE | `/api/auth/{id}` | Eliminar usuario |

### Productos (`/api/products`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/products` | Crear producto |
| GET | `/api/products` | Listar todos los productos |
| GET | `/api/products/active` | Listar productos activos |
| GET | `/api/products/{id}` | Obtener producto por id |
| DELETE | `/api/products/{id}` | Eliminar producto (soft delete) |

### Órdenes (`/api/orders`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/orders` | Crear orden |
| GET | `/api/orders/{id}` | Obtener orden por id |
| GET | `/api/orders/user/{userId}` | Listar órdenes de un usuario |
| PUT | `/api/orders/{orderId}/status` | Actualizar estado (`?status=PENDING\|PAID\|CANCELLED`) |

### Pagos (`/api/payments`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/payments/{orderId}` | CLIENT | Crear PaymentIntent de Stripe para la orden |

Respuesta:
```json
{
  "clientSecret": "pi_xxx_secret_xxx",
  "orderId": 1
}
```

### Webhooks (`/api/webhooks`)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/webhooks/stripe` | Recibir eventos de Stripe (público, validado por firma) |

Eventos manejados:
- `payment_intent.succeeded` → actualiza la orden a `PAID` y reduce el stock de los productos.

## Flujo de pago

1. El cliente crea una orden (`POST /api/orders`).
2. El cliente solicita un PaymentIntent (`POST /api/payments/{orderId}`) → recibe un `clientSecret`.
3. El frontend completa el pago con Stripe usando el `clientSecret`.
4. Stripe notifica al backend vía webhook → la orden pasa a `PAID` y se descuenta el stock.

## Autenticación

El registro y login devuelven un token JWT. Inclúyelo en las peticiones protegidas:

```
Authorization: Bearer <token>
```

Los roles disponibles son `ADMIN` y `CLIENT`.

## Arquitectura

```
domain/           ← Lógica de negocio pura (sin dependencias de framework)
  model/          ← Entidades: Product, User, Order, OrderItem
  port/in/        ← Interfaces de casos de uso
  port/out/       ← Interfaces de repositorios

application/
  usecase/        ← Implementación de los casos de uso

infrastructure/
  persistence/    ← Adaptadores JPA (implementan los puertos de salida)
  web/            ← Controllers REST (adaptadores de entrada)
  web/dto/        ← DTOs de respuesta

config/           ← SecurityConfig, JwtService, JwtAuthFilter, StripeService
```
