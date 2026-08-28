# Words — Backend

Spring Boot microservices powering **Words**, a vocabulary-learning app with a dictionary and a spaced-repetition-style learning flow.

This repo is the **backend half** of the Words project (the SPA frontend lives in the companion [Words-Frontend](https://github.com/DanilProggg/Words-Frontend) repo). It contains two independently deployable microservices that live side by side in this monorepo.

## What the app does

Words lets a signed-up user build a personal dictionary of foreign words (word, transcription, translation, notes) and drill them with a "knowledge level" learning loop: each word carries a `knowledgeLevel` (NONE → BAD → GOOD → EXCELLENT) and a `lastSeen` timestamp, and the learning endpoint hands back the word that's next due for review.

## Architecture: two services talking over RabbitMQ RPC

- **`gateway`** (port `8090`) — the public-facing API. Owns user accounts, sign-up/sign-in and JWT issuance (Spring Security + `jjwt`), and exposes the `/api/v1/*` word/learning endpoints. It never touches word data directly.
- **`memorization`** (port `9091`) — the domain service. Owns the `Word` entity, `KnowledgeLevel` logic and all CRUD/learning persistence against its own Postgres database. It has no HTTP surface for clients — it is driven entirely by messages.

The interesting bit: instead of a REST call or a shared database between the two services, `gateway` talks to `memorization` **asynchronously over RabbitMQ using an RPC pattern built on top of pub/sub**. `gateway` publishes a command (e.g. `crud.create`, `learn.get.word`) to a direct exchange with a generated `correlationId` and a `replyTo` queue, then parks the HTTP request on a `CompletableFuture` (with a timeout) while it waits. `memorization` consumes the command via `@RabbitListener`, executes it, and publishes the result back to the reply queue; a dedicated listener on the `gateway` side (`RabbitMQMemorizationResponseListener`) resolves the matching future by correlation id, which unblocks the original HTTP response. This decouples the two services completely (no shared JDBC connection, no synchronous HTTP client between them) while still giving the caller a synchronous-looking HTTP response.

Each service has its own Postgres database (`users-db` for gateway, `memorization` for the domain service) — no shared schema.

## Tech stack

- Java 21, Spring Boot 3.3.5
- Spring Security + JJWT (stateless JWT auth, role-based access with `ROLE_USER`/`ROLE_ADMIN`)
- Spring Data JPA + PostgreSQL (one database per service)
- Spring AMQP / RabbitMQ (command + RPC-style messaging between services)
- springdoc-openapi (Swagger UI on each service)
- Maven, Lombok
- Prometheus/Grafana chart values included for metrics scraping

## Key features

- User sign-up / sign-in issuing a signed JWT (`AuthController`), with a stateless `JwtAuthenticationFilter` securing everything except `/auth/**` and Swagger routes
- Per-user word CRUD proxied through the gateway to the memorization service: add, paginated list, update, delete (`MemorizationController` → `CrudController`)
- Spaced-learning endpoint that returns the next word due for review and stamps it with the current time (`LearnController` / `LearnServiceImpl.getWord`)
- Word knowledge tracking via a 4-level enum (`NONE`, `BAD`, `GOOD`, `EXCELLENT`) with validation on update, plus lookup of words by knowledge level for review sessions
- Correlation-id based RPC over RabbitMQ so the gateway can present synchronous HTTP semantics over an async transport, including request timeouts on the caller side

## Running locally

Both services expect Postgres and RabbitMQ to be available. A ready-to-use `docker-compose.yml` in this folder spins up `rabbitmq` (management UI included), two Postgres instances (`54321` for users, `54322` for memorization) and both Spring Boot services (built from the included `gateway.Dockerfile` / `memorization.Dockerfile`, since the source repo ships without Dockerfiles):

```bash
docker compose up --build
```

Gateway comes up on `:8090` (Swagger at `/swagger-ui/index.html`), memorization on `:9091` internally (no public HTTP API — it only listens on RabbitMQ queues).
