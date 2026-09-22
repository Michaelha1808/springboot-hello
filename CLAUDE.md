# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 4.1.1 / Java 21 learning project (`com.michaeljava.springboot_hello`). Maven build via the wrapper (Maven 3.9.16). Lombok is on the annotation-processor path but not yet used in source. Not a git repository.

## Commands

Use `mvnw.cmd` on Windows (PowerShell) or `./mvnw` in Bash.

```
./mvnw spring-boot:run                 # run the app
./mvnw clean package                   # build fat jar -> target/springboot-hello-0.0.1-SNAPSHOT.jar
./mvnw test                            # run all tests
./mvnw test -Dtest=SpringbootHelloApplicationTests            # single test class
./mvnw test -Dtest=SpringbootHelloApplicationTests#contextLoads # single test method
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev          # run with a different profile
```

No linter/formatter is configured.

## Configuration / profiles

`application.yaml` sets `server.port: 8001` and activates the `pro` profile by default. Profile files override the port only: `dev` → 8002, `pro` → 8003, `test` → 8004. So a plain `spring-boot:run` listens on **8003**.

## Architecture

Classic three-layer layout, one feature (Product) so far. Every layer is an interface + `impl/` subpackage, wired with `@Autowired` field injection:

- `controller/ProductController` — `@RestController` under `/v1/api` (`POST /product/add`, `GET /products`). Currently binds/returns `ProductEntity` directly; the `dto/request` and `dto/response` classes exist but are not yet used.
- `service/ProductService` → `service/impl/ProductServiceImpl` — pass-through to the repository.
- `repository/ProductRepository` → `repository/impl/ProductRepositoryImpl` — **in-memory stub**, no database. Returns hard-coded data; `createProduct` returns `null`.
- `entity/ProductEntity` — setters are empty stubs (fields are never actually set), so responses serialize with default values.

`controller/PlayerController` is fully commented out (it read `player.*` properties that no longer exist in the YAML). `entity/OrderEntity` and `repository/OrderRepository` are empty placeholders.
