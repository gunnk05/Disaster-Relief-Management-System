### Tech Stack

| Layer      | Technology                                                            |
|------------|-----------------------------------------------------------------------|
| Frontend   | React 19, React Router 7, Vite 6, Vanilla CSS                        |
| Backend    | Java 17, Spring Boot 3.3.2, Spring Data JPA, Spring Security         |
| Auth       | JWT (jjwt 0.12.6) with BCrypt password hashing                       |
| Database   | MySQL 8+ with triggers for audit logging                              |
| Testing    | JUnit 5, Mockito, MockMvc, H2 (in-memory)                            |


### 2. Backend

```bash
cd backend

# Set environment variables (or use defaults in application.properties)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=drrms_db
export DB_USER=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-secret-key-at-least-32-characters-long

# Build and run (Windows)
mvn spring-boot:run

# Or (Linux / macOS)
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080/api`.

### 3. Frontend

```bash
cd frontend
npm install
npm run dev

---

## 📄 License

This project is for educational and interview demonstration purposes.
