# StockGuard — Inventory Management API

A backend-focused inventory management system built with Java, Spring Boot, and PostgreSQL. StockGuard demonstrates secure REST APIs, JWT authentication, transactional stock updates, and concurrency-safe order processing.

## Features

- **Product management:** Create, view, update, and delete inventory products.
- **Order management:** Place orders, view order history, and cancel orders.
- **Atomic stock deduction:** Orders are accepted only when sufficient stock is available.
- **Concurrency protection:** Prevents multiple simultaneous orders from overselling available inventory.
- **Safe cancellation:** Restores stock when an order is cancelled and prevents duplicate stock restoration.
- **JWT authentication:** Protects administrative operations.
- **Public product catalogue:** Allows visitors to browse products without logging in.
- **Public test ordering:** Supports placing test orders through the public API.
- **API documentation:** Swagger UI and OpenAPI endpoints.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | REST API and application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA / Hibernate | Database access and ORM |
| PostgreSQL | Relational database |
| Maven | Dependency and build management |
| Swagger / OpenAPI | API documentation |

## Architecture

```text
Client / React Frontend
          |
          v
     REST API
   Spring Boot
          |
          v
 Spring Security + JWT
          |
          v
 Services + Transactions
          |
          v
 Spring Data JPA
          |
          v
    PostgreSQL
```

## API Endpoints

All endpoints use the `/api` prefix.

### Authentication

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/login` | Public |

Login returns a JWT token used to authenticate protected requests.

### Products

| Method | Endpoint | Access |
|---|---|---|
| GET | `/api/products` | Public |
| GET | `/api/products/{id}` | Public |
| POST | `/api/products` | Admin |
| PUT | `/api/products/{id}` | Admin |
| DELETE | `/api/products/{id}` | Admin |

### Orders

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/orders?productId={id}&quantity={quantity}` | Public test ordering |
| GET | `/api/orders` | Admin |
| PUT | `/api/orders/{id}/cancel` | Admin |

Public test ordering changes inventory in the database. The public order endpoint should be used only for demonstration and testing.

### API Documentation

When the backend is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Concurrency and Transaction Safety

StockGuard addresses a common inventory management problem: two customers attempting to purchase the same remaining stock at the same time.

The stock deduction operation checks availability and updates stock atomically. If insufficient stock remains, the order is rejected rather than allowing the inventory to become negative.

Order cancellation also uses transaction-safe processing to restore stock and prevent repeated cancellations from restoring stock more than once.

### Example

Suppose a product has 5 units in stock, and two requests simultaneously attempt to order 4 units each.

- One order can succeed.
- The other order is rejected because sufficient stock is no longer available.
- Stock does not become negative.

This behavior was verified using concurrent requests during local testing.

## Local Setup

### Prerequisites

- Java JDK compatible with the project's `pom.xml`
- Git
- PostgreSQL database (local or hosted)
- Maven Wrapper included in this repository

### 1. Clone the repository

```bash
git clone https://github.com/avisha191/StockGuard-Backend.git
cd StockGuard-Backend
```

### 2. Configure environment variables

Set the following environment variables before starting the application:

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL of your PostgreSQL database |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret used to sign JWTs |

Example PowerShell configuration:

```powershell
$env:DB_URL="jdbc:postgresql://HOST/DATABASE?sslmode=require"
$env:DB_USERNAME="YOUR_DATABASE_USERNAME"
$env:DB_PASSWORD="YOUR_DATABASE_PASSWORD"
$env:JWT_SECRET="YOUR_LONG_RANDOM_SECRET"
```

Replace the example values with your own credentials. Never commit real credentials or secrets to GitHub.

### 3. Run the backend

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API should start at:

`http://localhost:8080`

### 4. Build the application

```powershell
.\mvnw.cmd clean package
```

## Security Notes

- Administrative endpoints require an authenticated admin token.
- Passwords are stored as password hashes rather than plaintext.
- Database credentials and JWT secrets are supplied through environment variables.
- Public ordering is intended for demonstration; additional rate limiting can be added for production use.
- Database schema migrations and stricter production configuration are potential future improvements.

## Future Improvements

- Automated integration and concurrency tests.
- Request rate limiting for public order placement.
- Database migration management.
- Pagination and filtering for larger catalogues.
- More detailed audit logging and monitoring.

## Author

**Avisha Sahu**

GitHub: [@avisha191](https://github.com/avisha191)
