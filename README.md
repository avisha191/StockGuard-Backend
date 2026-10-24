# StockGuard - Inventory Management API



StockGuard is a backend-focused inventory management system built with Java and Spring Boot. It manages products, stock levels, and orders while preventing overselling during simultaneous order requests.



\## Features



\- \*\*Product management:\*\* Create, view, update, and delete inventory products.

\- \*\*Order management:\*\* Place orders, view order details, and cancel orders.

\- \*\*Concurrency protection:\*\* Uses an atomic conditional database update to prevent stock from becoming negative during simultaneous orders.

\- \*\*Transactional order placement:\*\* Keeps inventory deduction and order creation consistent.

\- \*\*Cancellation safeguards:\*\* Prevents an already-cancelled order from restoring stock multiple times.

\- \*\*JWT authentication:\*\* Secures authenticated API requests.

\- \*\*PostgreSQL database:\*\* Stores product and order data persistently.



\## Tech Stack



\- Java

\- Spring Boot

\- Spring Data JPA / Hibernate

\- PostgreSQL

\- Maven

\- Spring Security

\- JWT authentication



\## Architecture



The application follows a layered backend architecture:



\- \*\*Controller layer:\*\* Receives HTTP requests and returns responses.

\- \*\*Service layer:\*\* Implements business logic for inventory and orders.

\- \*\*Repository layer:\*\* Communicates with PostgreSQL through JPA.

\- \*\*Database layer:\*\* Stores products, orders, and related data.



\## Configuration



The application reads sensitive configuration from environment variables.



| Variable | Purpose |

|---|---|

| `DB\_URL` | PostgreSQL JDBC connection URL |

| `DB\_USERNAME` | Database username |

| `DB\_PASSWORD` | Database password |

| `JWT\_SECRET` | Secret used to sign JWTs |



Never commit real credentials or JWT secrets to version control.



\## Run Locally



\### Prerequisites



\- A compatible Java JDK

\- PostgreSQL database access

\- Git



\### 1. Clone the repository



```bash

git clone YOUR\_BACKEND\_REPOSITORY\_URL

cd stockguard

```



Replace `YOUR\_BACKEND\_REPOSITORY\_URL` with your GitHub repository URL after publishing.



\### 2. Set environment variables



In PowerShell, set the variables for your current terminal session:



```powershell

$env:DB\_URL="YOUR\_POSTGRESQL\_JDBC\_URL"

$env:DB\_USERNAME="YOUR\_DATABASE\_USERNAME"

$env:DB\_PASSWORD="YOUR\_DATABASE\_PASSWORD"

$env:JWT\_SECRET="YOUR\_BASE64\_ENCODED\_SECRET"

```



Use your own database credentials and a strong secret. Do not commit them.



\### 3. Start the backend



```powershell

.\\mvnw.cmd spring-boot:run

```



The backend should start at `http://localhost:8080` if the configured port is available.



\## API Overview



The API includes endpoints for authentication, product management, and order management.



| Method | Endpoint | Purpose |

|---|---|---|

| POST | `/api/auth/login` | Authenticate and obtain a token |

| GET | `/api/products` | Retrieve products |

| POST | `/api/orders?productId={id}\&quantity={quantity}` | Place an order |

| GET | `/api/orders` | Retrieve orders |

| GET | `/api/orders/{id}` | Retrieve an order |

| PUT | `/api/orders/{id}/cancel` | Cancel an order |



Consult the controller classes for the complete endpoint list and request/response formats.



\## Concurrency and Overselling Protection



StockGuard deducts inventory using a conditional database update that only succeeds when sufficient stock is available.



Conceptually:



```sql

UPDATE product

SET stock = stock - :quantity

WHERE id = :productId

&#x20; AND stock >= :quantity;

```



The operation is atomic at the database level. If two requests attempt to purchase more stock than is available, a request that cannot satisfy the condition is rejected.



Order placement


