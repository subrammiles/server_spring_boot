# 02: Configuration (properties, YAML, profiles, secrets)

The same jar must run on your laptop, in CI and in production. Only the **configuration** changes.

## Where Boot reads configuration (highest priority first, abridged)
1. Command line: `java -jar app.jar --shop.name=X`
2. Test properties: `@SpringBootTest(properties = ...)`, `@TestPropertySource`
3. OS environment variables: `SHOP_NAME=X`
4. Profile files: `application-{profile}.yml`
5. `application.yml` / `application.properties` (in `config/` next to the jar, then the classpath)
6. `@DefaultValue` / defaults in code

So a value in the file is a default that ops can override without rebuilding.

## Concepts
- **YAML or properties**: same keys, different syntax. YAML is nicer for nested and list values.
  **Pitfall**: quote money values (`"149.00"`). YAML reads `149.00` as a float first, and the scale is lost (a test caught exactly this).
- **`@ConfigurationProperties`** (`ShopProperties`): binds `shop.*` into an immutable, type-safe record.
  - **Relaxed binding**: `max-cart-items`, `maxCartItems`, and the env var `SHOP_MAXCARTITEMS` all map to `maxCartItems`.
  - **Conversion**: `45m` to `Duration`, `10MB` to `DataSize`, lists, maps, nested records.
  - **`@Validated`**: invalid config means the app does not start (`InvalidConfigurationTest`).
  - **Configuration processor**: generates metadata for IDE autocompletion (see the `pom.xml` comment).
- **`@Value` vs `@ConfigurationProperties`**: `@Value` is fine for one or two values. Group related settings in a properties class.
- **Profiles**: `application-dev.yml`/`application-prod.yml` override the base file.
  Beans can be profile-specific too (`FakePaymentGateway` `!prod` vs `RealPaymentGateway` `prod`).
  Activate with `--spring.profiles.active=prod` or `SPRING_PROFILES_ACTIVE=prod`.
- **Secrets**: never committed. `application-prod.yml` contains `${SHOP_PAYMENT_API_KEY}`, and the value comes from the environment
  (or a secret manager / Kubernetes secret). Without it, prod refuses to start.
- **`spring.config.import`**: pulls in extra files (`optional:file:./local.yml`, which is git-ignored) or config servers / Vault.

## Run
```bash
mvn -pl 02-configuration spring-boot:run
curl localhost:8080/config

mvn -pl 02-configuration spring-boot:run -Dspring-boot.run.profiles=dev
SHOP_PAYMENT_API_KEY=sk_live_abcd1234 mvn -pl 02-configuration spring-boot:run -Dspring-boot.run.profiles=prod
SHOP_NAME="From env var" mvn -pl 02-configuration spring-boot:run
```

## Try it
- Create `02-configuration/local.yml` with `shop.name: Mine` and restart.
- Set `shop.max-cart-items: 0` and read the startup error.
- Start with `--spring.profiles.active=prod` without the env var and read the error.
