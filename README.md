# News & Ads Portal - Backend API

A comprehensive backend system for a multi-lingual (UZ/RU/EN) news and advertising portal built with Spring Boot 3.

## Features

- **Multi-language Support**: UZ, RU, EN with per-language slugs
- **Admin API**: Complete CRUD operations for news, categories, tags, and ads
- **Public API**: Optimized endpoints for displaying published content
- **JWT Authentication**: Secure token-based authentication with refresh tokens
- **Role-Based Access Control**: ADMIN, EDITOR, AUTHOR roles
- **Media Management**: S3/MinIO integration for file storage
- **Scheduled Jobs**: Auto-publish/unpublish news articles
- **Redis Caching**: Optional caching for public endpoints
- **Swagger Documentation**: Interactive API documentation
- **Database Migrations**: Flyway for schema management

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.9**
- **PostgreSQL 15+**
- **Redis** (optional, for caching)
- **MinIO/S3** (for media storage)
- **Flyway** (database migrations)
- **JWT** (authentication)
- **Swagger/OpenAPI** (API documentation)

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 15+
- Redis (optional)
- MinIO or S3-compatible storage

## Quick Start with Docker

1. Clone the repository
2. Create `.env` file from `.env.example` and configure:
   ```bash
   cp .env.example .env
   ```

3. Start all services:
   ```bash
   docker-compose up -d
   ```

4. Wait for services to be healthy, then access:
   - Application: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - MinIO Console: http://localhost:9001 (minioadmin/minioadmin)
   - Health Check: http://localhost:8080/actuator/health

## Local Development Setup

### 1. Database Setup

```bash
# Create database
createdb newsportal

# Or using PostgreSQL client
psql -U postgres -c "CREATE DATABASE newsportal;"
```

### 2. Redis (Optional)

```bash
# Start Redis
redis-server

# Or using Docker
docker run -d -p 6379:6379 redis:7-alpine
```

### 3. MinIO Setup

```bash
# Using Docker
docker run -d -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  minio/minio server /data --console-address ":9001"
```

Then create a bucket named "media" via MinIO console (http://localhost:9001)

### 4. Configure Application

Update `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/newsportal
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 5. Run Application

```bash
mvn clean install
mvn spring-boot:run
```

## Default Admin User

The database migration creates a default admin user:
- **Username**: admin
- **Password**: admin123 (CHANGE IN PRODUCTION!)

**Important**: Update the password hash in the migration file or change it immediately after first login.

## API Endpoints

### Authentication (Public)
- `POST /api/v1/admin/auth/login` - Login and get JWT tokens
- `POST /api/v1/admin/auth/refresh` - Refresh access token
- `POST /api/v1/admin/auth/logout` - Logout (client-side)

### Admin News Management
- `GET /api/v1/admin/news` - List all news (with filters)
- `GET /api/v1/admin/news/{id}` - Get news by ID
- `POST /api/v1/admin/news` - Create news article
- `PATCH /api/v1/admin/news/{id}/status` - Update news status
- `DELETE /api/v1/admin/news/{id}` - Soft delete news
- `POST /api/v1/admin/news/{id}/restore` - Restore soft-deleted news
- `DELETE /api/v1/admin/news/{id}/hard` - Hard delete (ADMIN only)
- `GET /api/v1/admin/news/{id}/history` - Get news change history

### Public News API
- `GET /api/v1/public/news` - Get published news (paginated)
- `GET /api/v1/public/news/{slug}` - Get news by slug

### Admin Categories
- `GET /api/v1/admin/categories` - List categories
- `POST /api/v1/admin/categories` - Create category
- `PATCH /api/v1/admin/categories/{id}` - Update category
- `DELETE /api/v1/admin/categories/{id}` - Delete category

### Admin Tags
- `GET /api/v1/admin/tags` - List tags
- `POST /api/v1/admin/tags` - Create tag
- `PATCH /api/v1/admin/tags/{id}` - Update tag
- `DELETE /api/v1/admin/tags/{id}` - Delete tag

### Admin Media
- `POST /api/v1/admin/media` - Upload media file
- `DELETE /api/v1/admin/media/{id}` - Delete media

### Public Ads
- `GET /api/v1/public/ads/{placementCode}` - Get ad for placement

## API Documentation

Swagger UI is available at: http://localhost:8080/swagger-ui.html

## Authentication

All admin endpoints (except `/auth/**`) require JWT authentication:

```bash
# Login
curl -X POST http://localhost:8080/api/v1/admin/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use the access_token in subsequent requests
curl -X GET http://localhost:8080/api/v1/admin/news \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Database Migrations

Migrations are managed by Flyway and run automatically on application startup. Migration files are located in:
```
src/main/resources/db/migration/
```

## Scheduled Jobs

The application includes scheduled jobs that run every minute:
- **Auto-publish**: Automatically publishes news with `publish_at <= now` and status `REVIEW`
- **Auto-unpublish**: Automatically unpublishes news with `unpublish_at <= now` and status `PUBLISHED`

## Security

- **Password Hashing**: Argon2
- **JWT Tokens**: 
  - Access token: 1 hour (configurable)
  - Refresh token: 30 days (configurable)
- **Role-Based Access**: 
  - `ADMIN`: Full access including hard delete
  - `EDITOR`: Can publish/unpublish, edit all news
  - `AUTHOR`: Can create/edit own drafts

## Caching (Optional)

Redis caching can be enabled for public endpoints. Configure in `application.properties`:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Environment Variables

Key environment variables (see `.env.example`):
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - Database connection
- `REDIS_HOST`, `REDIS_PORT` - Redis connection
- `S3_ENDPOINT`, `S3_ACCESS_KEY`, `S3_SECRET_KEY`, `S3_BUCKET` - S3/MinIO config
- `JWT_SECRET` - JWT signing secret (minimum 32 characters)

## Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/news-ads-app-0.0.1-SNAPSHOT.jar
```

## Health Check

Health check endpoint: `GET /actuator/health`

## License

This project is proprietary.

## Support

For issues and questions, please contact the development team.
