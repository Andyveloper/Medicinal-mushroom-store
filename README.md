# Setas Backend

REST API para una tienda en línea construida con **Spring Boot** siguiendo arquitectura hexagonal (Ports & Adapters).

## Tech Stack

- Java 17
- Spring Boot 4.0.5 (Spring 6 / Jakarta EE)
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Lombok
- JUnit 5

## Requisitos previos

- Java 17+
- PostgreSQL corriendo en `localhost:5432`
- Base de datos `setas_db` creada

## Configuración

Edita `src/main/resources/application.properties` con tus credenciales de base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/setas_db
spring.datasource.username=postgres
spring.datasource.password=tu_password
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

config/           ← SecurityConfig, JwtService, JwtAuthFilter
```
