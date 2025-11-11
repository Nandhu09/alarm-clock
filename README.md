# Java Alarm Clock Application

A full-stack alarm clock application built with Java Spring Boot, H2 Database, and JavaScript.

## Tech Stack

### Backend
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (in-memory)
- Lombok

### Frontend
- HTML5
- CSS3
- JavaScript
- Web Audio API
- Web Notifications API

## Features

- Digital clock displaying current time and date
- Add alarms with custom messages
- Multiple alarm sound options
- Snooze functionality
- Daily repeat option
- Persistent storage with H2 database
- RESTful API for alarm management
- Responsive design for desktop and mobile

## Prerequisites

- Java Development Kit (JDK) 17 or later
- Maven 3.8+ or compatible build tool

## Installation and Setup

### Clone the repository
```bash
git clone https://github.com/yourusername/spring-boot-alarm-clock.git
cd spring-boot-alarm-clock
```

### Build and run the application
```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at:
- Frontend: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:alarmdb, Username: sa, Password: empty)

## API Endpoints

The application provides the following RESTful API endpoints:

- `GET /api/alarms` - Get all alarms
- `GET /api/alarms/{id}` - Get alarm by ID
- `POST /api/alarms` - Create a new alarm
- `PUT /api/alarms/{id}` - Update an existing alarm
- `DELETE /api/alarms/{id}` - Delete an alarm by ID

## Usage

1. Set a new alarm using the form in the "Add New Alarm" section
2. View active alarms in the "Active Alarms" section
3. When an alarm triggers:
   - A sound will play
   - A notification will appear (if permission was granted)
   - You can choose to snooze or dismiss the alarm

## Development Notes

- The frontend uses JavaScript to communicate with the backend API
- Alarms are stored in an H2 in-memory database (data will be lost on application restart)
- The application includes fallback mechanisms if the backend is unreachable

To modify the port or other settings, edit the `application.properties` file located in `src/main/resources/`.

## License


MIT License - Feel free to use and modify as needed. 
