# DocAssist Database Setup

This directory contains the Docker Compose configuration for the DocAssist application's PostgreSQL database with PGVector extension.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)

## Starting the Database

1. Open a terminal in the project root directory:
   ```cmd
   cd c:\Users\marsk\Documents\Java\docassist
   ```

2. Start the PostgreSQL database:
   ```cmd
   docker-compose up -d
   ```

3. Verify the database is running:
   ```cmd
   docker-compose ps
   ```

## Database Configuration

- **Host:** localhost
- **Port:** 5432
- **Database:** docassist
- **Username:** postgres
- **Password:** postgres

## Extensions Installed

The database is automatically configured with:
- **vector** - For vector similarity search capabilities
- **uuid-ossp** - For UUID generation
- **pg_trgm** - For text similarity searches
- **btree_gin** - For better indexing performance

## Connecting to the Database

### Using psql (if PostgreSQL client is installed)
```cmd
psql -h localhost -p 5432 -U postgres -d docassist
```

### Using Docker exec
```cmd
docker exec -it docassist-postgres psql -U postgres -d docassist
```

## Useful Commands

### View logs
```cmd
docker-compose logs postgres
```

### Stop the database
```cmd
docker-compose down
```

### Stop and remove volumes (WARNING: This will delete all data)
```cmd
docker-compose down -v
```

### Restart the database
```cmd
docker-compose restart postgres
```

## Health Check

The PostgreSQL container includes a health check that verifies the database is ready:
- **Interval:** 10 seconds
- **Timeout:** 5 seconds
- **Retries:** 5

You can check the health status with:
```cmd
docker-compose ps
```

## Data Persistence

Database data is persisted using Docker volumes:
- **Volume name:** `docassist_postgres_data`
- **Mount point:** `/var/lib/postgresql/data`

## Troubleshooting

### Port already in use
If port 5432 is already in use, you can change it in `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Use port 5433 instead
```

Don't forget to update `application.properties` accordingly.

### Connection refused
1. Ensure Docker is running
2. Check if the container is healthy: `docker-compose ps`
3. View logs: `docker-compose logs postgres`

### Reset database
To completely reset the database:
```cmd
docker-compose down -v
docker-compose up -d
```

## Application Configuration

To configure the application to use PostgreSQL, update your `src/main/resources/application.properties` file with the following settings:

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/docassist
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Comment out or remove H2 configuration if present
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=password
# spring.h2.console.enabled=true
```

For development purposes, you can switch between H2 and PostgreSQL by commenting/uncommenting the respective sections.
