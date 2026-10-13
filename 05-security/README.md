# 05: Security (authentication, authorization, JWT)

Adding `spring-boot-starter-security` alone locks every endpoint and creates one user with a random password
printed at startup. This module replaces that default with a realistic setup: users in a database,
hashed passwords, login that issues a **JWT**, and **role-based** rules on URLs and on methods.

## Flow
```
1. POST /auth/register {username, password}   -> user saved with a bcrypt hash, role USER
2. POST /auth/token   (HTTP Basic user:pass)   -> {"accessToken": "eyJ...", "expiresIn": 3600}
3. GET  /api/me       Authorization: Bearer eyJ...   -> the API trusts the signed token (no session, no DB lookup)
```

## Concepts
- **Authentication** (who are you? failure gives **401**) vs **authorization** (may you do this? failure gives **403**).
- **Filter chain**: every request passes through security filters before the `DispatcherServlet`.
  Two `SecurityFilterChain` beans: HTTP Basic for `/auth/token`, JWT for everything else (`@Order` decides which matches first).
- **UserDetailsService** loads users from the database (`DatabaseUserDetailsService`).
- **Password hashing**: `DelegatingPasswordEncoder`, which uses bcrypt and stores `{bcrypt}` in front of the hash so the algorithm can be upgraded later.
  Never store or log plain passwords.
- **JWT** (JSON Web Token): signed claims (`sub`, `roles`, `exp`). Stateless and short-lived. Anyone can **read** the payload,
  but only the key holder can **sign** it, so tampering is detected (`tamperedTokenIsRejected`).
  This demo signs with an HMAC secret. With several services you'd use an **authorization server** (Keycloak, Auth0, or
  Spring Authorization Server) and RSA keys, and the API would only point `spring.security.oauth2.resourceserver.jwt.issuer-uri` at it.
- **URL rules** (`authorizeHttpRequests`, first match wins), **method security** (`@EnableMethodSecurity`,
  `@PreAuthorize("hasRole('ADMIN')")`), **ownership rules** (`#username == authentication.name`).
- **CSRF**: needed for cookie/session-based browser apps. It is disabled here because the API is stateless and uses Bearer tokens.
- **CORS**: which browser origins may call the API (`corsConfigurationSource`).
- **Secrets**: the JWT key comes from `APP_JWT_SECRET` in real deployments. The default in `application.yml` is for the demo only.

## Testing security
- `jwt()` / `httpBasic()` request post-processors (`spring-security-test`): fake an authenticated user, or log in for real.
- `@WithMockUser(roles = "ADMIN")` does the same for method-level tests.
- Test the **negative** cases: 401, 403, tampered token, wrong password, CORS from a foreign origin.

## Run
```bash
mvn -pl 05-security spring-boot:run

curl localhost:8080/api/courses                                              # public
TOKEN=$(curl -s -X POST -u admin:admin123 localhost:8080/auth/token | jq -r .accessToken)
curl -H "Authorization: Bearer $TOKEN" localhost:8080/api/me
curl -i -X POST -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
     -d '{"title":"OAuth2"}' localhost:8080/api/courses                     # 201 for admin
STUDENT=$(curl -s -X POST -u student:student123 localhost:8080/auth/token | jq -r .accessToken)
curl -i -X DELETE -H "Authorization: Bearer $STUDENT" localhost:8080/api/courses/1   # 403
```
Demo accounts: `student` / `student123`, `admin` / `admin123`. They're seeded by `DemoUsers`, which a real app would not do.

## Try it
- Paste a token into https://jwt.io and read its claims.
- Set `app.jwt.ttl: 30s`, get a token, wait, and call `/api/me` again.
- Add a refresh-token endpoint, or replace HTTP Basic with a JSON login endpoint.
