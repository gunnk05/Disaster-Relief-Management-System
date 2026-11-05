### Tech Stack

| Layer      | Technology                                                            |
|------------|-----------------------------------------------------------------------|
| Frontend   | React 19, React Router 7, Vite 6, Vanilla CSS                        |
| Backend    | Java 17, Spring Boot 3.3.2, Spring Data JPA, Spring Security         |
| Auth       | JWT (jjwt 0.12.6) with BCrypt password hashing                       |
| Database   | MySQL 8+ with triggers for audit logging                              |
| Testing    | JUnit 5, Mockito, MockMvc, H2 (in-memory)                            |

Backend commands
cd backend
mvn spring-boot:run

Frontend commands
cd frontend
npm install
npm run dev
