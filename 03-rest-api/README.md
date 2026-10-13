# 03: Building REST APIs

A course catalog API covering what a production REST API needs: CRUD, paging, validation, consistent errors,
versioning and documentation. `basics/` keeps the request-handling examples from the original version of this repo.

## Endpoints
| Method | URL | Notes |
|---|---|---|
| GET | `/api/courses?category=backend&q=spring&page=0&size=10&sort=price` | search, paging, sorting |
| GET | `/api/courses/{id}` | header `API-Version: 2.0` returns the v2 shape |
| POST | `/api/courses` | 201 + `Location` header. 409 if the title exists |
| PUT | `/api/courses/{id}` | full replace |
| PATCH | `/api/courses/{id}` | partial update (only the fields sent) |
| DELETE | `/api/courses/{id}` | 204 |
| GET | `/basics/...` | query params, body, headers, cookies, `ResponseEntity` |
| GET | `/swagger-ui.html`, `/v3/api-docs` | interactive docs (springdoc-openapi) |

## Concepts
- **DTOs**: `CourseRequest` (input) and `CourseResponse` (output) are separate from the domain `Course`.
  Clients can't set server-owned fields (id, createdAt), and the internals can change without breaking the API.
- **HTTP semantics**: correct verbs and status codes (200, 201 + `Location`, 204, 400, 404, 409), and PUT vs PATCH.
- **Validation**: `@Valid @RequestBody` for bodies. Constraints directly on `@RequestParam` (`@Max(100)`)
  are validated by Spring MVC itself. Don't add `@Validated` to the controller (see the comment in `CourseController`).
- **Centralised errors**: `@RestControllerAdvice` extending `ResponseEntityExceptionHandler` makes every error,
  built-in or custom, an RFC 9457 `application/problem+json` body with field-level `errors`.
- **Paging**: never return unbounded lists. Cap `size` and return totals.
- **API versioning** (new in Spring Framework 7): `@GetMapping(path = "/{id}", version = "2.0")` plus
  `spring.mvc.apiversion.use.header=API-Version` in `application.yml`. Path segments, query params and media types work too.
- **OpenAPI / Swagger UI** generated from the code, with `@Tag`/`@Operation` for descriptions.
- `@Controller` vs `@RestController` (`LegacyController`).

## Testing styles (compare them)
| Test | What starts | Speed | Use for |
|---|---|---|---|
| `CourseControllerWebMvcTest` | web layer only (`@WebMvcTest`), service mocked with `@MockitoBean` | fast | status codes, JSON shape, validation, errors |
| `CourseApiIntegrationTest` | whole app on a random port (`@SpringBootTest` + `RestTestClient`) | slower | end-to-end flows |

`MockMvcTester` is the AssertJ flavour of MockMvc. `RestTestClient` (Spring 7) sends real HTTP.

## Run
```bash
mvn -pl 03-rest-api spring-boot:run
curl localhost:8080/api/courses
curl -H 'API-Version: 2.0' localhost:8080/api/courses/1
curl -i -X POST localhost:8080/api/courses -H 'Content-Type: application/json' \
     -d '{"title":"Docker","category":"devops","durationHours":6,"price":799}'
curl -i -X POST localhost:8080/api/courses -H 'Content-Type: application/json' -d '{"title":""}'
open http://localhost:8080/swagger-ui.html
```

## Try it
- Add `DELETE` protection: return 409 when a course "has students" (fake it with a flag).
- Switch versioning to a path segment (`/api/v2/courses/{id}`) with `spring.mvc.apiversion.use.path-segment`.
