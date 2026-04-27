# HotelNova System

HotelNova System is a Java 17 desktop application for hotel room, guest, user, and reservation management. The project follows a 4-layer architecture with JDBC-based persistence, service-level business rules, and a Swing entry point.

## Architecture

The application is organized into four layers:

1. `model`: Domain entities such as `Room`, `Guest`, `User`, and `Reservation`.
2. `dao`: Persistence contracts and JDBC implementations responsible for database access.
3. `service`: Business workflows such as authentication, check-in, and check-out with transaction handling.
4. `controller` and `App`: Application orchestration and the Swing user interface.

## Project Structure

```text
src/main/java/com/hotelnova
├── controller
├── dao
│   └── impl
├── database
├── exception
├── model
├── service
└── util
```

## Configuration Guide

Application properties are stored in `src/main/resources/config.properties`.

```properties
db.url=jdbc:mysql://localhost:3306/hotel_nova_db
db.user=root
db.password=1234
checkInHour=15
checkOutHour=12
vat=0.19
```

Configuration keys:

- `db.url`: MySQL JDBC URL for the HotelNova database.
- `db.user`: Database username.
- `db.password`: Database password.
- `checkInHour`: Default check-in hour.
- `checkOutHour`: Default check-out hour.
- `vat`: VAT rate used during check-out totals.

## Database Initialization

At startup, `DatabaseInitializer.initialize()`:

- Creates the database if it does not exist.
- Executes the DDL in `hotel_nova_db_ddl.sql`.
- Seeds default users when needed:
  - `admin` / seeded for compatibility with previous versions
  - `qa_admin` / `QaAdmin123!`
  - `qa_recep` / `QaRecep123!`

Required local prerequisites:

- Java 17
- Maven 3.9+
- MySQL 8+

## Running the Application

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=com.hotelnova.App
```

## Running Tests

```bash
mvn test
```

## CSV Exports

The controller export flow generates:

- `rooms_export.csv`
- `active_reservations.csv`

## Mermaid Class Diagram

```mermaid
classDiagram
    class App
    class HotelController
    class AuthService
    class ReservationService
    class DatabaseConnection
    class DatabaseInitializer
    class CSVExportUtil

    class User {
        +int id
        +String username
        +String password
        +UserRole role
        +boolean isActive
    }

    class Guest {
        +int id
        +String firstName
        +String lastName
        +String documentNumber
        +String email
        +String phoneNumber
        +boolean isActive
    }

    class Room {
        +int id
        +String roomNumber
        +String type
        +int capacity
        +BigDecimal pricePerNight
        +RoomStatus status
        +boolean isActive
    }

    class Reservation {
        +int id
        +int guestId
        +int roomId
        +int userId
        +LocalDateTime checkInDate
        +LocalDateTime checkOutDate
        +BigDecimal totalCost
        +ReservationStatus status
    }

    class UserDAO
    class GuestDAO
    class RoomDAO
    class ReservationDAO
    class UserDAOImpl
    class GuestDAOImpl
    class RoomDAOImpl
    class ReservationDAOImpl

    App --> HotelController
    HotelController --> AuthService
    HotelController --> ReservationService
    HotelController --> UserDAOImpl
    HotelController --> GuestDAOImpl
    HotelController --> RoomDAOImpl
    HotelController --> ReservationDAOImpl
    ReservationService --> ReservationDAO
    ReservationService --> RoomDAO
    ReservationService --> GuestDAO
    UserDAOImpl ..|> UserDAO
    GuestDAOImpl ..|> GuestDAO
    RoomDAOImpl ..|> RoomDAO
    ReservationDAOImpl ..|> ReservationDAO
    UserDAO --> User
    GuestDAO --> Guest
    RoomDAO --> Room
    ReservationDAO --> Reservation
    DatabaseInitializer --> DatabaseConnection
    HotelController --> CSVExportUtil
```

## Mermaid Use Case Diagram

```mermaid
flowchart LR
    Receptionist([Receptionist])
    Admin([Admin])

    UC1([Log in])
    UC2([Register guest])
    UC3([Search guest])
    UC4([List rooms])
    UC5([Create room])
    UC6([Update room price])
    UC7([Process check-in])
    UC8([Process check-out])
    UC9([Export CSV reports])

    Receptionist --> UC1
    Receptionist --> UC2
    Receptionist --> UC3
    Receptionist --> UC4
    Receptionist --> UC7
    Receptionist --> UC8
    Receptionist --> UC9

    Admin --> UC1
    Admin --> UC4
    Admin --> UC5
    Admin --> UC6
    Admin --> UC9
```
