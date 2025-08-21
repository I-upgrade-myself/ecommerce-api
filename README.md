🛒 E-Commerce API (Spring Boot)

A fully functional E-Commerce REST API, built with Spring Boot, featuring JWT authentication, role-based access (USER / ADMIN), a shopping cart, order management, and full CRUD operations for products.
Perfect as a backend for a modern frontend app built with React, Angular, or Vue.

🚀 Features

🔐 JWT Authentication & Role-based Access (USER, ADMIN)

👤 User Registration / Login

🛒 Shopping Cart (add, view, remove items)

📦 Order Management (create, view, statistics)

🏷️ Product Management (CRUD + search with filters)

🎛️ Admin Panel Endpoints

📄 Swagger UI for API Testing (/swagger-ui.html)

## 🛠️ Tech Stack

| Layer       | Technology                 |
|-------------|-----------------------------|
| Language    | Java 17+                    |
| Framework   | Spring Boot                 |
| Security    | Spring Security + JWT       |
| ORM         | Hibernate / JPA             |
| Database    | PostgreSQL / MySQL          |
| Mapping     | MapStruct                   |
| Boilerplate | Lombok                      |
| API Docs    | Springdoc OpenAPI / Swagger |


## 🔐 Security

JWT tokens are used for authentication and role-based access control.

### 🔓 Access Rules

- `POST /api/auth/**` – Public (registration, login)
- `GET /api/admin/**` – Admin only
- All other endpoints – require authentication

### ⚙️ Security Configuration (`SecurityConfig`)

- `@EnableMethodSecurity` – Enables `@PreAuthorize` in controllers
- CORS enabled for frontend at `http://localhost:3000`
- Stateless sessions
- CSRF disabled
- `JwtAuthenticationFilter` – intercepts all requests and sets the `SecurityContext`
- `JwtTokenProvider` – generates, validates tokens, extracts email and roles

---

## 👤 Users and Roles

### 🧍‍♂️ Entity: `User`

- Fields: `email`, `password`, `firstName`, `lastName`
- Roles: `ROLE_USER`, `ROLE_ADMIN`
- Additional: `enabled`, `createdAt`

### 🧾 Entity: `Role`

- `ROLE_USER`
- `ROLE_ADMIN`

> 🔧 `RoleInitializer` automatically creates default roles on application startup

---

## 🧩 Controllers

### 🔐 `AuthController`

- `POST /api/auth/register` – Registers a new user (returns JWT)
- `POST /api/auth/login` – Logs in the user (returns JWT + user data)

### 👤 `UserController`

- `GET /api/user/profile` – View user profile (`USER` or `ADMIN`)

### 🛠️ `AdminController`

- `GET /api/admin/dashboard` – Accessible only by `ADMIN`

### 🛒 `CartController`

- `GET /api/cart` – Get items in the shopping cart
- `POST /api/cart` – Add item to the cart
- `DELETE /api/cart/{id}` – Remove item from the cart

### 📦 `OrderController`

- `GET /api/orders/user/{userId}/status` – Get order statistics for a user
- `GET /api/orders` – Get all orders of the current user
- `POST /api/orders` – Create a new order

### 🏷️ `ProductController`

- `GET /api/products` – Get all products with pagination
- `GET /api/products/search` – Search/filter products by name, price, etc.
- `POST /api/products` – Create a new product
- `PUT /api/products/{id}` – Update product
- `DELETE /api/products/{id}` – Delete product

---

## 📦 DTOs (Data Transfer Objects)

- `AuthResponse` – JWT + user data
- `LoginRequest` / `RegisterRequest` – for login and registration
- `CartItemDTO` – `id`, `productId`, `quantity`
- `OrderDTO` – `id`, `userId`, `productIds`, `createdAt`
- `ProductDTO` – `id`, `name`, `description`, `price`
- `UserOrderStatusDTO` – order stats (`totalOrders`, `totalSpent`)

---

## 🗃️ Entities (JPA)

- `User`, `Role`, `Product`, `CartItem`, `Order`

### 🔗 Relationships

- `@ManyToOne`, `@ManyToMany`
- `CartItem` → linked to `User` and `Product`
- `Order` → linked to `User` and a list of `Products`

> 🧮 `Order.getTotalPrice()` – Calculates the total amount of an order

---

## 🔁 Mappers (MapStruct)

MapStruct mappers convert between Entities and DTOs automatically:

- `CartItemMapper`
- `OrderMapper`
- `ProductMapper`

---

## 🧩 Repositories

- `CartItemRepository`
- `OrderRepository`
- `ProductRepository`
- `RoleRepository`
- `UserRepository`

> 🔍 Supports custom filtering, search, and pagination  
> 🧰 Uses `JpaSpecificationExecutor` for advanced product queries
