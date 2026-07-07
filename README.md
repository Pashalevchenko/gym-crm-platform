# Gym CRM application

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-platform&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-platform)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-platform&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-platform)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-platform&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-platform)

## 1. Prerequisites

Before running the application, make sure the following tools are installed:

```text
Java 21
Maven
PostgreSQL
Redis
ActiveMQ
MongoDB
```

## 2. Clone the project

```bash
git clone https://github.com/Pashalevchenko/gym-crm-application.git
cd gym-crm-application
```

## 3. Database Setup PostgreSQL

Before the first run, create a database and user with proper privileges:

```sql
CREATE DATABASE gym_db;
CREATE USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE gym_db TO gym;
```

## MongoDB Setup

The `workload-service` uses MongoDB to store trainer workload summaries.

Default local connection:
```text
Host       localhost
Port       27017
Database   workload_service
```

## ActiveMQ Setup

The platform uses ActiveMQ Classic for asynchronous workload messages between `gym-core-service` and `workload-service`.

Local broker connection:

```yml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
```
```text
Broker URL    tcp://localhost:61616
Web console   http://localhost:8161/admin
Credentials   admin / admin
```

ActiveMQ credentials

For a local ActiveMQ installation, the default credentials are usually:

```text
admin / admin
```
Сredentials can be changed in:

```text
<ACTIVE_MQ_HOME>/conf/users.properties
<ACTIVE_MQ_HOME>/conf/groups.properties
```

## 4. Build the project

```bash
mvn clean compile
```

## 5. Run tests

Docker must be running before executing tests.

```bash
mvn test
```

## 6. Run the application from console

```bash
mvn -pl discovery-server spring-boot:run
mvn -pl workload-service spring-boot:run
mvn -pl gym-core-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl api-gateway spring-boot:run -Dspring-boot.run.profiles=local
```

If you want to run with a specific Spring profile, use:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

After startup, the application will be available at:

```text
discovery-server   http://localhost:8761
gateway-service    http://localhost:8080
gym-core-service   http://localhost:8081/gym-crm-application
workload-service   http://localhost:8082/workload-service
```

Gateway routes:
```text
/gym-crm-application/**      -> gym-core-service
/workload-service/**         -> workload-service
```

Swagger contracts
```text
gym-core-service   http://localhost:8081/gym-crm-application/swagger-ui.html
workload-service   http://localhost:8082/workload-service/swagger-ui.html
```

ActiveMQ
```text
ActiveMQ Web Console -> http://localhost:8161
```
## 7.  Actuator endpoints

The application exposes Spring Boot Actuator endpoints for health checks and Prometheus metrics.

Base local URL:

```text
http://localhost:8080/gym-crm-application
```

Available actuator endpoints:

```text
GET /actuator/health
GET /actuator/health/database
GET /actuator/health/trainee
GET /actuator/health/trainer
GET /actuator/health/trainingType
GET /actuator/health/userRepository
GET /actuator/metrics
GET /actuator/prometheus
```

### Examples

Check application health:

```bash
http://localhost:8080/gym-crm-application/actuator/health
```

Check database health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/database
```

Check trainee health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/trainee
```

Check trainer health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/trainer
```

Check Prometheus metrics:

```bash
http://localhost:8080/gym-crm-application/actuator/prometheus
```

Metric descriptions:

```text
gym_trainees_total     - total number of created trainees since application startup
gym_trainers_total     - total number of created trainers since application startup
gym_trainings_total    - total number of created trainings since application startup
gym_trainees_active    - current number of active trainees
gym_trainers_active    - current number of active trainers
```