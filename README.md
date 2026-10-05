# 📦 Enterprise Inventory Management System (IMS)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-blue.svg)](https://spring.io/projects/spring-security)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20H2-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED.svg)](https://www.docker.com/)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-2088FF.svg)](https://github.com/features/actions)

A production-grade, enterprise web application designed for streamlined inventory tracking, role-based access governance, stock movement auditing, dual-approval workflows, and low-stock automated alerts. Built with modern **Spring Boot 4**, **Spring Security**, **Spring Data JPA / Hibernate**, **Thymeleaf**, **Apache POI**, and containerized for turnkey deployment.

---

## 🏗️ Architecture & Component Overview

```mermaid
flowchart TD
    Client["Browser Client / Mobile Browser"] --> Security["Spring Security Filter Chain\n(Form Login, Role Authorization, Session Audit)"]

    subgraph Presentation ["Presentation Layer"]
        Security --> Controllers["Spring MVC Controllers\n(Product, Category, Supplier, Admin,\nReport, User, Request, Account)"]
        Controllers --> Thymeleaf["Thymeleaf Template Engine\n(Dark/Light Themes, Dynamic Fragments)"]
    end

    subgraph Business ["Service Layer"]
        Controllers --> ProductService["ProductService\n(Low Stock Monitoring, Alert Triggers)"]
        Controllers --> CategoryService["CategoryService"]
        Controllers --> SupplierService["SupplierService"]
        Controllers --> UserService["UserService & CustomUserDetailsService"]
        Controllers --> ApprovalService["ApprovalRequestService\n(Two-person Rule for Stock & Catalog)"]
        Controllers --> StockService["StockMovementService\n(Audit Trail: Added / Reduced)"]
        Controllers --> EmailService["EmailService\n(SMTP Mail Notification)"]
        Controllers --> AuditService["UserSessionAuditService\n(Login/Logout/Session Tracking)"]
    end

    subgraph Data ["Data Access Layer (Spring Data JPA)"]
        ProductService --> Repos["JPA Repositories & HikariCP Pool"]
        CategoryService --> Repos
        SupplierService --> Repos
        UserService --> Repos
        ApprovalService --> Repos
        StockService --> Repos
        AuditService --> Repos
    end

    subgraph Persistence ["Persistence Layer"]
        Repos --> MySQL[("Production: MySQL 8.0")]
        Repos --> H2[("Test/Dev: In-Memory H2 DB")]
    end
```

---

## 🗄️ Entity-Relationship Model

```mermaid
erDiagram
    USER ||--o{ USER_SESSION_AUDIT : logs
    USER ||--o{ APPROVAL_REQUEST : requests
    PRODUCT ||--o{ STOCK_MOVEMENT : tracks
    CATEGORY ||--o{ PRODUCT : categorizes
    SUPPLIER ||--o{ PRODUCT : supplies

    USER {
        Long id PK
        string userId UK "e.g. ADMIN001, USER001"
        string username
        string password "BCrypt encoded"
        string role "ADMIN / USER"
        string email
        string phone
        string address
    }

    PRODUCT {
        Long id PK
        string name
        string category
        double price
        int quantity
        int minimumStock
        string image "Relative upload filename"
    }

    CATEGORY {
        Long id PK
        string name
        string description
    }

    SUPPLIER {
        Long id PK
        string name
        string contact
        string email
        string address
    }

    STOCK_MOVEMENT {
        Long id PK
        Long productId FK
        string productName
        string movementType "ADDED / REDUCED"
        int quantity
        datetime timestamp
    }

    APPROVAL_REQUEST {
        Long id PK
        string requesterUserId
        string requesterName
        string requestType "STOCK_UPDATE / CATEGORY_ADD"
        string status "PENDING / APPROVED / REJECTED"
        Long productId
        string productName
        int currentQuantity
        int requestedQuantity
        string categoryName
        string categoryDescription
        datetime createdAt
    }

    USER_SESSION_AUDIT {
        Long id PK
        string userId
        string ipAddress
        datetime loginTime
        datetime logoutTime
        string sessionStatus
    }
```

---

## 🚀 Key Improvements & Enterprise Refactoring

| Area | Before | Enterprise Refactored State |
| :--- | :--- | :--- |
| **Directory Structure** | Cluttered nested directories (`inventry/inventory-management-system/`) with typos and 23MB PPTX/XLSX dumped in root | Clean root-level Maven layout; project assets and agile tracking sheets organized in `docs/` |
| **Security & Secrets** | Leaked Gmail App password & plaintext MySQL credentials in version control | Purged all hardcoded secrets; replaced with 12-factor environment variable interpolation with safe defaults; created `.env.example` |
| **Application Entrypoint** | Duplicate `@SpringBootApplication` classes causing packaging ambiguity | Unified into single canonical `InventoryManagementApplication` |
| **Cross-Platform Storage** | Hardcoded Windows path (`C:/inventory-images/`) breaking Linux/Mac/Docker | OS-agnostic configurable `app.upload.dir` with dynamic URI resolution in `WebConfig` |
| **Database & Testing** | Rigid local MySQL dependency causing tests to fail when MySQL is offline | Integrated in-memory H2 database test profile (`application-test.properties`) allowing instant, zero-dependency CI runs |
| **Automated Test Suite** | Single empty test class | 26 unit and integration tests covering Products, Categories, Users, Approval workflows, and Spring Security authorization |
| **Containerization** | None | Production multi-stage `Dockerfile` with non-root security user & `docker-compose.yml` with MySQL health checks |
| **CI/CD** | None | GitHub Actions CI workflow (`.github/workflows/ci.yml`) validating builds and test passes on every push and PR |

---

## 🔑 Default Credentials

The application seeds default users upon startup via `UserDataInitializer`:

| Role | User ID | Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Admin** | `ADMIN001` | `admin123` | Full administrative controls: product CRUD, category CRUD, supplier CRUD, user management, approval workflows, Excel exports, audit logs |
| **Standard User** | `USER001` | `user123` | Operational view: inventory catalog, low-stock alerts, stock update requests, category creation requests, password management |

---

## ⚙️ Configuration & Environment Variables

Copy `.env.example` to `.env` or set the following environment variables:

| Variable | Description | Default Value |
| :--- | :--- | :--- |
| `SERVER_PORT` | HTTP port the application listens on | `9090` |
| `SPRING_DATASOURCE_URL` | JDBC URL for MySQL database | `jdbc:mysql://localhost:3306/inventory_db?...` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | *(empty)* |
| `APP_UPLOAD_DIR` | Image uploads filesystem directory | `./uploads/images/` |
| `SPRING_MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `SPRING_MAIL_PORT` | SMTP server port | `587` |
| `SPRING_MAIL_USERNAME` | SMTP authentication username | *(empty)* |
| `SPRING_MAIL_PASSWORD` | SMTP authentication password / app password | *(empty)* |
| `LOW_STOCK_RECIPIENT_EMAIL` | Destination email for automated low-stock warnings | `admin@inventory.local` |

---

## 🛠️ Local Development & Testing

### Prerequisites
- **JDK 17** or higher (`java -version`)
- **MySQL 8.0+** (for local runs) or **Docker**

### 1. Run Automated Test Suite
The test suite utilizes an in-memory H2 database with zero external dependencies:
```bash
./mvnw clean test
```

### 2. Run Application Locally
```bash
./mvnw spring-boot:run
```
Access the application at [http://localhost:9090](http://localhost:9090).

### 3. Build Executable Jar
```bash
./mvnw clean package
java -jar target/inventory-system-0.0.1-SNAPSHOT.jar
```

---

## 🐳 Docker & Turnkey Deployment

### Run with Docker Compose
Spin up the Spring Boot application and a MySQL 8.0 container with persistent volumes:
```bash
docker compose up --build -d
```

### Check Logs & Status
```bash
docker compose ps
docker compose logs -f app
```

To shut down:
```bash
docker compose down
```

---

## 📂 Repository Organization

```text
├── .github/
│   └── workflows/
│       └── ci.yml                 # Automated Maven test & build pipeline
├── docs/
│   ├── presentation/              # System architecture & slide deck
│   └── project_management/        # Agile tracking & sprint backlog
├── src/
│   ├── main/
│   │   ├── java/com/inventory/
│   │   │   ├── config/            # Security, Password, MVC, Audit handlers & Data seeders
│   │   │   ├── controller/        # Web MVC Controllers (Thymeleaf endpoints & REST actions)
│   │   │   ├── entity/            # JPA Data Entities (Product, User, Category, StockMovement...)
│   │   │   ├── repository/        # Spring Data JPA Repositories
│   │   │   └── service/           # Business Logic, Email alerts, and Workflow services
│   │   └── resources/
│   │       ├── static/            # CSS, JavaScript, theme togglers, and static assets
│   │       ├── templates/         # Thymeleaf HTML views (dashboards, products, approvals...)
│   │       └── application.properties # Production properties (env-var interpolated)
│   └── test/
│       ├── java/com/inventory/    # Unit & Integration test suites (26 tests)
│       └── resources/             # In-memory H2 test profile & Mockito configuration
├── .dockerignore
├── .env.example                   # Sample environment configuration
├── .gitignore
├── Dockerfile                     # Multi-stage production container build
├── docker-compose.yml             # Orchestration for App + MySQL
├── pom.xml                        # Maven dependencies & build plugins
└── README.md                      # Comprehensive documentation
```

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
