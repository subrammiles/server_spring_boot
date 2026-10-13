# 04: Data access with Spring Data JPA, Flyway and Testcontainers

The domain from the original version of this repo, rebuilt properly: courses with reviews, students with
passports and enrolments, and an employee inheritance hierarchy.

```
Course 1──* Review            (one-to-many / many-to-one)
Student 1──1 Passport         (one-to-one)
Student *──* Course           (many-to-many via student_course)
Employee <|── FullTimeEmployee, PartTimeEmployee   (inheritance, SINGLE_TABLE)
```

## What Boot does for you
Compare with `server_spring` lessons 15-18: the DataSource, EntityManagerFactory, transaction manager, repositories,
`JdbcClient` and Flyway are all **auto-configured** from the starters plus `application.yml`.

## Concepts
- **Flyway migrations** (`db/migration/V1__...sql`, `V2__...`): versioned, repeatable schema changes, checked into git.
  `ddl-auto: validate` makes Hibernate verify that the entities match the schema, and never change it.
- **Entity relationships** with helper methods keeping both sides in sync (`Course.addReview`, `Student.enroll`).
- **`@MappedSuperclass`** (`BaseEntity`): shared id, `@Version` (optimistic locking), auditing timestamps.
- **Auditing**: `@EnableJpaAuditing` + `@CreatedDate`/`@LastModifiedDate`.
- **Inheritance mapping**: `SINGLE_TABLE` + discriminator column, polymorphic queries, `TYPE()` in JPQL.
  The original code forgot `@Entity` on the subclasses, which this version fixes.
- **Fetching**: everything LAZY, plus **`@EntityGraph`** to load what a use case needs in one query
  (the `RepositoryTest` counts the SQL statements).
- **`spring.jpa.open-in-view: false`**: lazy loading must happen in the service transaction. Don't rely on it during JSON rendering.
- **Service layer**: owns `@Transactional` and maps **entities to DTOs**. Controllers never return entities.
- **Paging**: `Pageable` parameters straight from the URL (`?page=0&size=5&sort=name,desc`).
- **Projections**: `CourseStats` record filled by a JPQL constructor expression.
- **JdbcClient** for reporting SQL (`ReportRepository`), next to JPA on the same DataSource.
- **Constraint violations**: a database unique key becomes `DataIntegrityViolationException`, which the API returns as 409.
- **H2 console** at `/h2-console` while developing.

## Testing
| Test | Starts | Notes |
|---|---|---|
| `RepositoryTest` | `@DataJpaTest`: JPA + DB + Flyway only | rolled back after each test, `TestEntityManager` |
| `StudentServiceTest` | full context, no web | `@Transactional` rollback |
| `ApiTest` | full context + `MockMvcTester` | HTTP behaviour on a real DB |
| `PostgresContainerTest` | full context + **PostgreSQL in Docker** | `@ServiceConnection`. Skipped without Docker |

## Run
```bash
mvn -pl 04-data-jpa spring-boot:run
curl 'localhost:8080/api/courses?sort=name,desc'
curl localhost:8080/api/courses/1
curl -X POST localhost:8080/api/courses/3/reviews -H 'Content-Type: application/json' -d '{"rating":5,"description":"Clear"}'
curl -X PUT localhost:8080/api/students/2/courses/3
curl 'localhost:8080/api/students?course=Spring Boot in Depth'
curl localhost:8080/api/employees
curl localhost:8080/api/reports/top-courses
# H2 console: http://localhost:8080/h2-console  (JDBC URL jdbc:h2:mem:coursesdb, user sa, empty password)
```

## Running on PostgreSQL
```bash
docker run -d --name pg -e POSTGRES_PASSWORD=secret -p 5432:5432 postgres:17-alpine
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres \
SPRING_DATASOURCE_USERNAME=postgres SPRING_DATASOURCE_PASSWORD=secret \
  mvn -pl 04-data-jpa spring-boot:run
```

## Try it
- Add `V3__add_course_level.sql` (a `level` column) and map it in `Course`. Run, then break the mapping and read the `validate` error.
- Change `@EntityGraph` off `findWithReviewsById` and watch the SQL log in `RepositoryTest`.
- Switch inheritance to `JOINED`. Which migration would you need?
