# Product Vision

## Product Name

Inventory Management System

## Vision Statement

Provide a simple internal platform that helps small or mid-sized operations teams track products, monitor stock levels, manage supporting master data, and control sensitive changes through approval workflows and reporting.

The long-term vision is to replace fragmented inventory handling with a single, role-aware system that gives the organization better visibility, stronger operational discipline, and faster decision-making. The product is intended to support daily inventory work, reduce manual coordination, and create a reliable source of truth for stock, users, and supporting records.

## Product Background

Many growing businesses begin inventory tracking with spreadsheets, paper logs, or informal communication between store staff and managers. That approach may work at low scale, but it becomes increasingly difficult to maintain when product counts rise, stock levels change frequently, and multiple users need to access the same information.

This project addresses that gap by introducing a centralized web application built on Spring Boot and MySQL. It allows the team to manage products, categories, suppliers, users, and reports in one place, while also enforcing access control and approval rules for sensitive changes.

## Problem Statement

Inventory operations often depend on manual spreadsheets or disconnected tools. That leads to:

- Poor visibility into current stock
- Delayed response to low-stock situations
- Weak control over who can change inventory data
- Limited reporting for stock, sales, and user activity
- Errors when categories, suppliers, and users are managed informally

These issues create business risk in several ways. Teams may overstock slow-moving products, run out of fast-moving items, or make decisions based on incomplete or outdated records. Managers also lose accountability when changes are not tied to a specific user or approval step. Over time, this reduces trust in the data and increases the cost of day-to-day operations.

## Opportunity Statement

The opportunity is to deliver a focused internal product that improves operational control without requiring a highly complex enterprise system. By combining inventory tracking, low-stock alerts, approval workflows, reporting, and user access control, the organization can improve stock accuracy and reduce manual overhead with a manageable implementation footprint.

## Product Value Proposition

The Inventory Management System provides:

- One central place to manage inventory-related operations
- Faster visibility into stock health and low-stock risk
- Better governance through role-based access and approval flows
- Cleaner operational reporting for managers and auditors
- Reduced dependence on manual spreadsheet maintenance

In practical terms, the product helps the business move from reactive inventory handling to controlled and traceable inventory management.

## Target Users

### Admin

- Manages products, categories, suppliers, and users
- Reviews and approves operational requests
- Uses dashboards and exports to monitor the business
- Needs traceability through user audit and stock movement history

Admin users are typically supervisors, store managers, inventory heads, or system operators. They are responsible for data quality and operational oversight. Their primary concern is not just entering data, but maintaining control over who changes it, monitoring stock health, and generating reports for internal review.

### Admin Needs

- Quick access to a complete operational dashboard
- Control over product, user, category, and supplier records
- Visibility into low-stock products and recent activity
- Approval tools for sensitive requests
- Exportable reports for management review or external sharing

### Standard User

- Views inventory and low-stock data
- Requests stock changes and new categories
- Uses a limited dashboard and account controls
- Needs a fast workflow without direct access to admin-only operations

Standard users are typically store staff, warehouse operators, or inventory assistants. They interact with the system more frequently for day-to-day tasks, but should not have unrestricted authority to change master records or bypass review.

### Standard User Needs

- Fast access to current product and stock information
- A clear view of low-stock items requiring attention
- A simple way to request updates without admin access
- Reliable account management and password changes

## Stakeholders

- Business owner or operations head
- Inventory manager
- Store or warehouse staff
- System administrator
- Audit or compliance reviewer
- Management team consuming reports

## Business Goals

- Centralize inventory records in one application
- Reduce stock-out risk through alerts and low-stock views
- Introduce controlled approval for sensitive updates
- Improve operational reporting and export capability
- Track user access for accountability

These business goals support both operational efficiency and governance. The system should help the organization spend less time reconciling records, respond faster to stock issues, and maintain an auditable trail of important changes.

## Product Goals

- Make daily inventory status visible within one dashboard
- Ensure stock changes are captured consistently
- Support search, filtering, and quick navigation across records
- Provide role-based authorization with minimal complexity
- Keep the system deployable on a standard Spring Boot plus MySQL setup

The product goals are intentionally practical. This system is not trying to be a full enterprise resource planning platform. Instead, it focuses on solving the most important inventory management problems with a maintainable architecture and a straightforward user experience.

## Strategic Objectives

- Create a reliable single source of truth for operational inventory data
- Minimize unauthorized or accidental changes through permission control
- Increase visibility into product movement and stock health
- Improve managerial insight through dashboards and exportable reports
- Establish a foundation for future enhancements such as purchase orders or multi-location inventory

## Key User Journeys

### Admin Journey

1. Log in to the system with admin credentials.
2. Access the admin dashboard to review KPIs, charts, and low-stock warnings.
3. Create or update products, categories, suppliers, and users.
4. Review pending stock or category requests raised by standard users.
5. Approve or reject requests based on business need.
6. Export reports for products, stock, sales, stock movement, or user audit.

### Standard User Journey

1. Log in to the system with user credentials.
2. Review dashboard and inventory data.
3. Check low-stock items or search for products.
4. Submit a request for stock adjustment or category creation.
5. Manage account security through password change.
6. Wait for admin approval before changes take effect.

## In-Scope Features

- Authentication with role-based access control
- Admin and user dashboards
- Product CRUD and image upload
- Inventory listing, search, and category filtering
- Low-stock detection and stock quantity updates
- Category CRUD
- Supplier CRUD
- User management and profile updates
- Change password flow
- Approval requests for stock updates and category creation
- Sales, stock movement, and user audit reporting
- Excel exports
- Email notification for low-stock products

### Functional Scope Details

#### Authentication and Authorization

- Custom login page
- Role-based route protection
- Separate admin and user dashboards
- Password change support for users

#### Product and Inventory Management

- Product creation, update, listing, and deletion
- Product image upload
- Product search by keyword
- Product filtering by category
- Quantity updates and low-stock monitoring
- Tracking of inventory movement history

#### Master Data Management

- Category management
- Supplier management
- User management for admins
- User profile update support

#### Approval Workflow

- User-submitted stock update requests
- User-submitted category creation requests
- Admin approval and rejection actions
- Bulk approval and rejection of pending requests

#### Reporting and Monitoring

- Dashboard KPIs and summary charts
- Sales reporting
- Stock reporting
- Product reporting
- Stock movement reporting
- User session audit reporting
- Spreadsheet export capabilities

#### Notifications

- Email alerts for low-stock conditions
- Configurable low-stock recipient through properties

## Out-of-Scope for Current Release

- Purchase order lifecycle
- Barcode scanning
- Multi-warehouse support
- API-first external integrations
- Mobile application
- Advanced analytics or forecasting
- Fine-grained workflow configuration

These items are excluded to keep the initial release focused and achievable. They may be revisited in later iterations once the core inventory workflows are stable and well adopted.

## Non-Functional Expectations

- The system should be easy to run in a local or small office environment.
- Pages should remain simple and understandable for non-technical staff.
- Security rules should clearly separate admin and user privileges.
- Data should persist reliably in MySQL.
- Exports should be usable in common spreadsheet tools.
- The application should remain maintainable with standard Spring Boot patterns.

## Success Metrics

- Stock records can be created, updated, searched, and exported without manual DB edits
- Low-stock items are visible and trigger notification logic
- Admin-only functions remain inaccessible to standard users
- Approval requests can be created and resolved end to end
- Reports are downloadable in spreadsheet form

### Operational Success Indicators

- Reduction in spreadsheet-based inventory maintenance
- Faster turnaround time for identifying low-stock products
- Lower number of unauthorized or uncontrolled inventory changes
- Better traceability for login sessions and stock movements
- Improved management visibility through dashboard and export usage

### Adoption Indicators

- Admin users use the dashboard and reports regularly
- Standard users submit requests through the system instead of informal channels
- Core records such as products, categories, and suppliers remain current in the application

## Constraints and Assumptions

- Requires a MySQL database named `inventory_db`
- Runs on port `9090` by default
- Uses local filesystem image storage at `C:/inventory-images/`
- Current mail delivery depends on configured SMTP credentials
- Current seeded users create one admin and one standard user on startup

### Technical Assumptions

- The application is deployed in an environment where Java 17 is available.
- The target users access the system through a browser on an internal network or controlled environment.
- SMTP configuration is valid if email notifications are required.
- Local file storage is acceptable for the current deployment model.

### Delivery Constraints

- The current implementation favors simplicity over enterprise-scale extensibility.
- Upload handling is tied to a fixed local path and may require environment-specific setup.
- Some operational insights depend on the availability of sales and stock movement data.

## Risks

- Secrets are stored in `application.properties`, which is not production-safe
- Image storage path is hardcoded and Windows-specific
- There is limited visible automated test coverage
- Report and dashboard quality depend on underlying sales and movement data integrity

### Additional Risks

- Demo or seeded credentials may be left unchanged in a non-development environment.
- Manual deployment configuration may introduce environment drift.
- Approval workflows may become bottlenecks if request volume grows.
- Lack of pagination or scalability improvements may affect usability on larger datasets.

## Future Vision

After the current release is stable, the product can evolve toward a broader inventory operations platform. Potential future directions include:

- Purchase and replenishment workflows
- Supplier order tracking
- Barcode or QR-based product handling
- Multi-branch or multi-warehouse inventory support
- Advanced dashboards and forecasting
- API integration with billing or ERP systems
- More granular permissions and configurable approval policies

## Summary

The Inventory Management System is designed as a practical, role-based web platform for improving inventory control, operational visibility, and accountability. It addresses real problems found in spreadsheet-driven environments by centralizing product data, enabling approval workflows, surfacing low-stock risks, and generating usable reports. Its near-term goal is to deliver a dependable operational tool, while its longer-term value lies in creating a scalable foundation for broader inventory digitization.
