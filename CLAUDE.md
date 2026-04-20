# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=SetasBackendApplicationTests

# Compile only
./mvnw compile
```

## Architecture: Hexagonal (Ports & Adapters)

The project enforces a strict layered structure under `src/main/java/com/setas/setas_backend/`:

```
domain/          ← Core business logic; no Spring dependencies
  model/         ← JPA entities (Product, User, Order, OrderItem, Role, OrderStatus)
  port/
    in/          ← Input ports: use-case interfaces (ICreateProduct, IRegisterUser, ICreateOrder, …)
    out/         ← Output ports: repository contracts (IProductRepository, IUserRepository, IOrderRepository)

application/
  usecase/
    product/     ← CreateProduct, DeleteProduct, GetProducts
    user/        ← RegisterUser, LoginUser, DeleteUser, FindByEmailUser
    order/       ← CreateOrder, GetOrders, UpdateOrder

infrastructure/
  persistence/
    product/     ← JpaProductRepository, ProductRepositoryImpl
    user/        ← JpaUserRepository, UserRepositoryImpl
    order/       ← JpaOrderRepository, OrderRepositoryImpl
  web/           ← REST controllers (ProductController, AuthController, OrderController)
    dto/         ← Response DTOs (AuthResponse, OrderResponse, OrderItemResponse)

config/          ← SecurityConfig, JwtService, JwtAuthFilter
```

**Key rule:** The `domain/` layer must remain free of framework coupling. Spring and JPA annotations belong only in `infrastructure/` and `config/`.

## Domain Models

### Product (`domain/model/Product.java`)
- `id` (Long), `name`, `description` (max 1000 chars), `price` (BigDecimal), `stock` (Integer), `imageUrl`, `active` (Boolean, default `true` — soft delete)

### User (`domain/model/User.java`)
- `id` (Long), `name`, `lastname`, `email` (unique), `password` (write-only), `phoneNumber` (Long), `imageUrl`, `role` (enum: `ADMIN`, `CLIENT`), `active` (Boolean, default `true`)

### Order (`domain/model/Order.java`)
- `id` (Long), `user` (ManyToOne), `totalPrice` (BigDecimal), `status` (enum: `PENDING`, `PAID`, `CANCELLED`), `createdAt` (LocalDateTime), `orderItems` (OneToMany)

### OrderItem (`domain/model/OrderItem.java`)
- Belongs to an Order; references a Product with quantity and unit price.

All models use Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`.

## Output Port Interfaces

| Interface | Methods |
|---|---|
| `IProductRepository` | `save`, `deleteById`, `findById`, `findAll`, `findAllActive` |
| `IUserRepository` | `save`, `deleteById`, `findById`, `findByEmail` |
| `IOrderRepository` | `save`, `findById`, `findByUserId` / `findAll`, `updateStatus` |

## REST API Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new user → returns JWT |
| POST | `/api/auth/login` | Login → returns JWT |
| GET | `/api/auth/{email}` | Find user by email |
| DELETE | `/api/auth/{id}` | Delete user |
| POST | `/api/products` | Create product |
| GET | `/api/products` | List all products |
| GET | `/api/products/active` | List active products |
| GET | `/api/products/{id}` | Get product by id |
| DELETE | `/api/products/{id}` | Soft-delete product |
| POST | `/api/orders` | Create order |
| GET | `/api/orders/{id}` | Get order by id |
| GET | `/api/orders/user/{userId}` | Get orders by user |
| PUT | `/api/orders/{orderId}/status` | Update order status (`?status=PENDING\|PAID\|CANCELLED`) |

## Security

JWT-based authentication via `JwtService` and `JwtAuthFilter`. Tokens include email and role claims. Configuration in `config/SecurityConfig.java`.

## Tech Stack

- **Java 17**, **Spring Boot 4.0.5** (Spring 6 / Jakarta EE)
- **Spring Data JPA** + **PostgreSQL** (`localhost:5432/setas_db`)
- **Spring Security** + **JWT**
- **Lombok**
- **JUnit 5** for tests

## Database

Config in `src/main/resources/application.properties`. `spring.jpa.hibernate.ddl-auto=update` — schema is auto-managed. Credentials are currently hardcoded; migrate to environment variables or Spring profiles before going to production.
