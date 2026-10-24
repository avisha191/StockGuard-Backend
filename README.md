# StockGuard — Inventory Management API

A backend-focused inventory management system built with **Java, Spring Boot, and PostgreSQL**. StockGuard manages products, inventory, and orders while protecting against overselling during concurrent requests.

## Features

- **Inventory Management:** Create, view, update, and delete products.
- **Order Management:** Place orders, view order details, and cancel orders.
- **Concurrency Control:** Atomic conditional stock updates prevent inventory from becoming negative.
- **Transactional Operations:** Keep stock deductions and order creation consistent.
- **Cancellation Protection:** Prevent duplicate cancellations from restoring stock more than once.
- **JWT Authentication:** Token-based authentication for protected API endpoints.
- **Persistent Storage:** PostgreSQL database integration using Spring Data JPA and Hibernate.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | REST API and application framework |
| Spring Data JPA | Database access |
| Hibernate | ORM and entity management |
| PostgreSQL | Relational database |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| Maven | Dependency management and build |

## Architecture

The backend follows a layered architecture:

- **Controller:** Handles HTTP requests and responses.
- **Service:** Implements inventory, order, and authentication logic.
- **Repository:** Performs database operations.
- **Entity:** Represents products, users, orders, and order statuses.
- **Security:** Validates JWTs and protects API endpoints.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Authenticate and obtain a JWT |

### Products

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | Retrieve products |
| POST | `/api/products` | Create a product |
| PUT | `/api/products/{id}` | Update a product |
| DELETE | `/api/products/{id}` | Delete a product |

### Orders

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders?productId={id}&quantity={quantity}` | Place an order |
| GET | `/api/orders` | Retrieve orders |
| GET | `/api/orders/{id}` | Retrieve an order |
| PUT | `/api/orders/{id}/cancel` | Cancel an order |

Protected endpoints require a valid JWT in the `Authorization` header:

`Authorization: Bearer <token>`

## Concurrency and Overselling Prevention

StockGuard uses a conditional database update to deduct inventory only when sufficient stock is available.

```sql
UPDATE product
SET stock = stock - :quantity
WHERE id = :productId
  AND stock >= :quantity;
```

The database executes this update atomically. When concurrent requests compete for limited inventory, a request that cannot satisfy the stock condition is rejected rather than allowing inventory to become negative.

Order placement is transactional, helping maintain consistency between stock deductions and order creation.

## Cancellation Safety

When an order is cancelled, StockGuard restores the ordered quantity to inventory. The cancellation logic locks the order record and checks its status, preventing repeated cancellation from restoring stock multiple times.

## Local Setup

### Prerequisites

- Java JDK compatible with the project
- PostgreSQL database
- Git

### 1. Clone the repository

```bash
git clone https://github.com/avisha191/StockGuard-Backend.git
cd StockGuard-Backend
```

### 2. Configure environment variables

In PowerShell, set the following variables for your terminal session:

```powershell
$env:DB_URL="YOUR_POSTGRESQL_JDBC_URL"
$env:DB_USERNAME="YOUR_DATABASE_USERNAME"
$env:DB_PASSWORD="YOUR_DATABASE_PASSWORD"
$env:JWT_SECRET="YOUR_STRONG_JWT_SECRET"
```

Use your own credentials and a strong JWT signing secret. **Never commit real credentials or secrets to GitHub.**

### 3. Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080` by default.

## Testing

The following scenarios have been manually tested:

- Two simultaneous orders competing for limited stock.
- Rejection of orders exceeding available inventory.
- Successful orders for the exact available quantity.
- Rejection of orders when stock reaches zero.
- Stock restoration after cancellation.
- Rejection of duplicate cancellation attempts.

In the concurrent test, two requests attempted to order four units each when stock was five. One order succeeded, the other was rejected with HTTP `409 Conflict`, and the remaining stock was one unit.

## Future Improvements

- Expanded automated unit and integration test coverage.
- API documentation with Swagger/OpenAPI.
- Production deployment and monitoring.
- Additional validation and error-handling improvements.

