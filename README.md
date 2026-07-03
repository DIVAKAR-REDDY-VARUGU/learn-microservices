# 🧩 Learn Microservices — NestJS + Spring Boot

A hands-on learning journal where I build microservices **twice — once in NestJS, once in Spring Boot** — to truly understand the patterns (not just one framework). The end goal is a small **polyglot system** that demonstrates the hard parts: gRPC, Kafka, Redis pub/sub, and the **Saga pattern** for transactions across services.

> This README *is* the course. Every lesson is logged here: the concept, **my doubts**, the explanations, **my mistakes**, and the **optimized approaches** — so it doubles as my study notes and a portfolio of how I learned.
>
> 📂 **Interview prep:** [`interview-prep/`](interview-prep/) — [Wells Fargo R2](interview-prep/wells-fargo-round2.md) · [Kafka from zero](interview-prep/kafka-from-scratch.md) · [GraphQL from zero](interview-prep/graphql-from-zero.md) · [REST↔GraphQL flows](interview-prep/rest-vs-graphql-flows.md) · [WebSockets from zero](interview-prep/websockets-from-zero.md) · [Frontend/MERN master](interview-prep/frontend-master-prep.md) · [Pixentech Senior Backend](interview-prep/pixentech-senior-backend.md)

---

## 📚 How this journal works

Each lesson is recorded with the same shape:

- **🎯 Concept** — what we're learning and why
- **💬 My doubts → answers** — the questions I asked and how they were resolved
- **✅ Checkpoint Q&A** — quick self-tests
- **🐞 Mistakes → optimized approach** — what I got wrong and the better way
- **🔑 Key takeaways** — the one-liners worth remembering

Style: **small, bite-sized lessons**, with **hands-on practice** (especially for Docker, Redis, Kafka).

---

## 🗺️ Progress

| Module | Topic | Status |
|---|---|---|
| 0 | Setup & foundations (mental model, tooling, Docker infra) | ✅ Complete |
| 1 | REST fundamentals (DI, controllers, DTOs, validation) | 🟡 In progress |
| 2 | Persistence on PostgreSQL (TypeORM · Spring Data JPA) | ⚪ Planned |
| 3 | Security (JWT + roles) | ⚪ Planned |
| 4 | Sync comms: gRPC + Protocol Buffers + TCP | ⚪ Planned |
| 5 | Async / pub-sub: Kafka + Redis | 🟡 In progress (Kafka deep dive + edge cases) |
| 6 | **Saga pattern** (distributed transactions) | ⚪ Planned |
| 7 | Polyglot microservices capstone | ⚪ Planned |
| F | React + Redux Toolkit frontend (optional) | ⚪ Planned |

---

## 🎯 Target architecture

The system we're building toward:

```
                       ┌────────────────────┐
     Browser / App ───►│    API GATEWAY     │   the ONE public front door
        REST/JSON      │  • routes requests │
                       │  • checks JWT token│
                       └─────────┬──────────┘
            ┌────────────────────┼─────────────────────┐
            │ "log me in"        │ business calls        │
            ▼                    ▼  gRPC / TCP           ▼
      ┌──────────┐         ┌──────────┐           ┌───────────┐
      │  AUTH    │         │  ORDER   │ ──gRPC──► │ INVENTORY │
      │ issues   │         │ creates  │ "in stock?"│  tracks   │
      │  JWT     │         │  orders  │ ◄─reply──  │  stock    │
      └────┬─────┘         └────┬─────┘           └─────┬─────┘
        own DB               own DB                  own DB
                                │ publishes "OrderPlaced"
                                ▼
                          ┌──────────┐
                          │  KAFKA   │   event log
                          └────┬─────┘
                               ▼ consumes the event
                       ┌────────────────┐
                       │  NOTIFICATION  │  sends email / SMS / push
                       └────────────────┘

  Shared infra (all in Docker):  🐘 Postgres   🟥 Redis   🟧 Kafka
```

| Service | Its one job |
|---|---|
| **Gateway** | Single entry point — routes requests, verifies the login token |
| **Auth** | Logs users in, hands out JWT tokens |
| **Order** | Creates & manages orders |
| **Inventory** | Tracks stock; Order *asks* it "is this in stock?" |
| **Notification** | Listens for events, sends emails/SMS/push |

## 🧰 Tech stack

**Languages/Frameworks:** TypeScript + NestJS · Java 17 + Spring Boot
**Data:** PostgreSQL · Redis · (MongoDB optional) **Messaging:** Apache Kafka · Redis pub/sub · (RabbitMQ concepts)
**Comms:** REST · gRPC + Protocol Buffers · TCP **Infra:** Docker Compose **Tests:** Jest · JUnit + Mockito

---

# Module 0 — Setup & Foundations

## Lesson 1 — The mental map 🗺️

### 🎯 Concept

**Monolith vs Microservices.** A *monolith* is one big app with **one database** — simple to start, but every deploy risks the whole thing and you can't scale one feature alone. *Microservices* are many small apps; each owns **one business capability and its own database**, deployed independently.

> 🍔 Analogy: a monolith is one giant shared kitchen; microservices are a **food court** where each stall is independent and they coordinate when an order spans stalls.

**The golden rule:** a service **never** reaches into another service's database. If Order needs stock data, it *asks* Inventory.

**How services talk — two families:**

- **Synchronous** (caller waits — like a 📞 phone call): **REST** (HTTP+JSON, universal), **gRPC** (HTTP/2 + Protobuf, fast & typed), **TCP** (Nest's lightweight transport).
- **Asynchronous** (fire & move on — like 📬 posting a letter): **Kafka** (durable, replayable event log), **Redis pub/sub** (instant fan-out, not stored).

**The gateway** is the single front door; the browser only talks to it, and it checks auth at the edge.

### 💬 My doubts → answers

> **Q: So we'll have gateway, auth, order, inventory, notification services — right? Show me visually.**

Yes — exactly those five. See the [Target architecture](#-target-architecture) diagram above. Each owns its own DB; they talk over gRPC/TCP (sync) and Kafka (async); Notification reacts to events.

### ✅ Checkpoint Q&A

> **Q: Why can't `order-service` just run one database transaction that also updates the inventory table?**
>
> **My answer (correct ✔):** Because we keep databases **separate per service** — order-service must not touch inventory's tables.
>
> **The deeper consequence:** since inventory's data lives in a different service's DB you're not allowed to touch, you **can't wrap both writes in a single database transaction**. That's the exact problem the **Saga pattern** (Module 6) exists to solve — a chain of local transactions with **compensating** steps to undo earlier ones if a later step fails.

### 🐞 Mistakes → optimized approach

_None yet — checkpoint answered correctly._ (This section will capture wrong turns and the better way as we go.)

### 🔑 Key takeaways

- One service = one capability **+ its own database**.
- Never reach into another service's DB — **ask** the owning service.
- **Sync** = wait for a reply (REST/gRPC/TCP); **async** = fire an event (Kafka/Redis).
- No shared transaction across services → that's why **Saga** exists.
- Learn each concept in **both** Nest and Spring so it sticks as a *pattern*, not a framework trick.

---

## Lesson 2 — Docker 🐳

### 🎯 Concept
Docker packages software + everything it needs into a portable **image**, which you run as an isolated **container**.

**Image vs Container (= Class vs Object):**

| Image | Container |
|---|---|
| Read-only **blueprint/snapshot** (mini-OS + software + libs + start command) | A **running instance** of an image |
| Downloaded from Docker Hub (`redis:7`) or built from a `Dockerfile` | Created with `docker run` |
| Frozen 🧊 | Alive 🔥 |

```
Dockerfile ──build──► IMAGE ──run──► CONTAINER
 (recipe)           (blueprint)     (running, isolated)
```

**One image → MANY containers** (like one class → many objects):
```
              ┌─► myredis (6379)
 redis:7 ─────┼─► cacheA  (6380)
 (1 image)    └─► cacheB  (6381)
```

**Containers talk by NAME on a shared network; your laptop reaches them via published ports:**
```
        Docker network "app-net"
   ┌───────────────────────────────────────────┐
   │ [order-service] ─"postgres:5432"─► [postgres]│
   │       └─────────"redis:6379"────► [redis]    │
   │       └─────────"kafka:9092"────► [kafka]    │
   └───────────────────────────────────────────┘
              ▲ laptop → localhost:6379 (via -p)
```

### 💬 My doubts → answers
- **What's in an image / do I write it?** A mini-OS + the software + deps + start command. For redis/postgres/kafka you **don't** write it — pull official images. For your **own** app, write a `Dockerfile` and `docker build` it.
- **redis + postgres + kafka — what do I do?** One service = one container, each from its official image; declare all in one `docker-compose.yml`.
- **Multiple terminals in one container?** Yes. The main process (e.g. `redis-server`) starts with `docker run`; open extra sessions with `docker exec -it <name> <cmd>` (cli, shell, logs) — all in the same container.
- **Can containers talk to each other?** Yes — on a shared Docker network they reach each other **by name** (`postgres:5432`). Laptop → container via published ports (`-p`).

### 🔑 Takeaways
- Image = blueprint (frozen); Container = running instance (alive); **1 image → many containers**.
- Pull official images for infra; write a Dockerfile only for your **own** services.
- `docker exec` = extra sessions into a running container; containers talk **by name** on a network.

---

## Lesson 3 — Redis (hands-on) 🟥

### 🎯 Concept
In-memory **key → value** store. Keys are strings; values can be **String / List / Hash / Set / Sorted Set**. Optional auto-expiry (**TTL**). Great for **cache + sessions**; not a query engine (lookups are by exact key).

### 🛠️ What I actually ran (and saw)
```bash
docker run -d --name myredis -p 6379:6379 redis:7      # start Redis in background

redis-cli SET product:42 "Bolt"     # → OK
redis-cli GET product:42            # → Bolt

# Hash = an object in one key:
HSET product:99 name Bolt price 10
HGETALL product:99                  # → name Bolt price 10

# Sorted Set = auto-sorted by score:
ZADD leaderboard 100 alice 80 bob 120 carol
ZRANGE leaderboard 0 -1 WITHSCORES  # → bob 80, alice 100, carol 120  (sorted!)

# Expiry:
SET session:abc user123 EX 10
TTL session:abc                     # → 10  (key self-deletes after 10s)
```

### 💬 My doubts → answers
- **Is `product:42` the key?** Yes — the *whole string* is one key; `:` is just a naming convention (like folders), `42` = an example id.
- **If it auto-expires, how is it useful?** Expiry is **optional** and you pick the time (cache 5 min, session 30 min, OTP 5 min); **no `EX` = forever**. Auto-cleanup is the feature.
- **Data types & operations?** String (SET/GET/INCR), Hash (HSET/HGET), List (LPUSH/LRANGE), Set (SADD), Sorted Set (ZADD/ZRANGE). **Update** = SET again, or a type command. **Search** = by exact key only (not SQL-like filtering).

### 🔑 Takeaways
- Redis = fast key→value; values can be objects (Hash), lists, sets, sorted sets.
- Update = overwrite; lookups are by exact key (blazing fast), not querying.
- **TTL auto-expires keys** → perfect for caches & sessions.

---

## Lesson 4 — Kafka (concept) 🟧

### 🎯 Concept
A durable, replayable **event log**. Producers write events to a **topic**; consumers read them. Unlike Redis pub/sub, Kafka **stores** events so consumers can catch up / replay.

```
ORDER ──produce──► [ topic "orders": e1 │ e2 │ e3 ] ──consume──► NOTIFICATION
                       (stored & replayable)
```

**Consumer groups — the key idea:**
- **Different groups** → each gets a FULL copy of every event (📢 broadcast)
- **Same group** → events split across members (⚖️ load-balanced, via partitions)
```
topic "orders": e1 e2 e3 e4
  group "notification": consumer-1 ──► all (own copy)
  group "analytics":    consumer-1 ──► all (separate copy)
  inside one group (2 consumers, 2 partitions):  c1►e1,e3   c2►e2,e4  (split)
```

### 💬 My doubts → answers
- **Isn't Kafka just pub/sub?** Yes — *durable* pub/sub **+ consumer groups**.
- **Do all consumers get the event?** Different groups = yes (each a full copy); same group = split among members.
- **One consumer, many topics?** Yes.
- **Redis pub/sub vs Kafka?** Redis = live-only (miss = gone); Kafka = stored + replayable.

### 🔑 Takeaways
- Topic = the log; producer writes, consumer reads; **offset** = a consumer's bookmark.
- Different groups **broadcast**; same group **load-balances** (via partitions).

---

## Lesson 5 — docker-compose & the image/container lifecycle 🧩

### 🎯 Concept
Declare several containers in ONE YAML file; `docker compose up -d` pulls images (if missing), creates + starts them, all on one auto-network (services reach each other by name).

```
 ONE recipe file          ONE command             MANY containers
 docker-compose.yml  ──►  docker compose up -d ──► 🐘 postgres 🟥 redis 🟧 kafka
```

### 🧱 YAML / file structure
- Indentation = nesting (spaces, never tabs). `key: value`, `key:` + block, `- item` = list.
- **Top-level keys** = a FIXED vocabulary: `services`, `volumes`, `networks`, `configs`, `secrets`, `name`. (We use `services` + `volumes`.)
- **Service names** (`postgres`/`redis`/`kafka`) = **free text** you choose → they become the **hostname** on the network. (≠ `image: postgres:16`, which is a real published image name.)
- **Inner keys** (`image`, `ports`, `volumes`, `environment`, `restart`, `depends_on`…) = the **same Compose menu for every service**; you use what you need. Only the **values inside `environment:`** are image-specific (`POSTGRES_*` vs `KAFKA_*`).

### 🔁 Lifecycle — when is an image vs a container created?
| Step | What happens |
|---|---|
| write the compose file | **nothing** (just a recipe) |
| `docker compose up` | **pull** images (if missing) → **create** containers → **start** |
| `docker compose down` | remove containers + network (images & volumes stay) |
| `docker rmi <img>` / `down -v` | delete images / named volumes |

- **Images** appear at **pull** time; **containers** at **`up`** time.
- We never *built* an image — official images are **downloaded** from Docker Hub. (You build your own only with a `Dockerfile` + `docker build`, later.)

### 🔌 ports & 💾 volumes
- **ports**: `"HOST:CONTAINER"` (your laptop : inside container). No `ports:` = only other containers can reach it.
- **volumes**: `SOURCE:TARGET[:opts]`. **Named** (`pgdata` → Docker-managed, **persists**; `down -v` deletes) vs **bind** (`./file` on your PC → into container, `:ro` = read-only).

### 💬 My doubts → answers
- **More top-level keys?** Yes, a fixed set: services/volumes/networks/configs/secrets/name.
- **Service names = keywords?** No — free text; they become hostnames.
- **Inner keys change per service?** No — same Compose menu; only `environment` values are image-specific.

### 🐞 My mistake → correction
- **Q:** Would the `products` data survive `docker compose down`?
- **My answer:** *"No — Redis will clear the data."*
- **Correction:** `products` is a **Postgres** table (not Redis). Postgres has a **named volume `pgdata`**, so the data **SURVIVES** `docker compose down` — only `docker compose down -v` wipes it. The *"no volume → data lost"* intuition is correct, but it applies to **Redis** here (we gave Redis no volume), not Postgres.

### 🔑 Takeaways
- The compose file is an inert **recipe**; `up` pulls images + creates containers.
- **Images** created at pull/build; **containers** at `up`.
- **Named volume** persists (DB 🐘); **no volume** = disposable (cache 🟥).

---

## Lesson 6 — Postgres hands-on (SQL) 🐘

### 🎯 Concept
A **relational database**: data lives in **tables** (rows × columns, each column typed). You talk to it with **SQL**. Unlike Redis (key→value, exact-key lookups only), Postgres can **filter, sort, and join**.

```
 TABLE: products                     Redis 🟥           vs   Postgres 🐘
 ┌─────┬────────┬───────┐            key → value             tables (rows × cols)
 │ id  │ name   │ price │            GET product:42          SELECT … WHERE price > 4
 ├─────┼────────┼───────┤            (exact key only)        (filter · sort · join)
 │ 1   │ Bolt   │ 10    │            in-memory, fast         on-disk, durable, queryable
 │ 2   │ Nut    │ 5     │
 │ 3   │ Washer │ 2     │
 └─────┴────────┴───────┘
```

### 🛠️ What I ran (in psql)
```sql
-- connect INTO the postgres container (psql = the Postgres CLI, like redis-cli)
docker exec -it learn-postgres psql -U dev -d learn

CREATE TABLE products (id SERIAL PRIMARY KEY, name TEXT NOT NULL, price INT NOT NULL);
INSERT INTO products (name, price) VALUES ('Bolt',10),('Nut',5),('Washer',2);
SELECT * FROM products;
SELECT name, price FROM products WHERE price > 4 ORDER BY price DESC;  -- filter + sort
ALTER TABLE old_name RENAME TO new_name;   -- rename a table WITHOUT losing data
\dt    -- list tables      \q    -- quit psql
```

### ✅ Check Q&A
**Q:** Why can Postgres answer `WHERE price > 4 ORDER BY price DESC` but Redis can't?
**A (correct ✔):** Postgres is *relational*, built for querying/filtering/sorting. Redis is *key-value* — it only fetches by an **exact key you already know** (`GET product:42`); it can't scan and compare values.

### 🐞 Mistakes → fixes
1. **Group name vs container name.** Ran `docker exec -it learn-microservices …` → *"No such container"*. `learn-microservices` is the compose **group/project**, not a container. The containers are `learn-postgres` / `learn-redis` / `learn-kafka`. ➜ Find names with `docker compose ps` (the **NAME** column).
2. **Typo'd table name.** Created `procucts` (missing 'd'); then `INSERT INTO products` → *ERROR: relation "products" does not exist* ("relation" = Postgres's word for a table). ➜ Fixed without losing data: `ALTER TABLE procucts RENAME TO products;`.

### 🔑 Takeaways
- Postgres = tables + SQL → rich queries (`WHERE` / `ORDER BY` / `JOIN`); Redis = exact-key only.
- `psql` = the Postgres CLI (like `redis-cli`); **"relation" = table**.
- `ALTER TABLE … RENAME TO …` changes the schema **without losing data**.
- Compose **group** name ≠ **container** name.

---

## ✅ Module 0 — Complete!

```
 MODULE 0  ▕████████████████████▏ 100%
   ✅ mental map   ✅ Docker   ✅ Redis   ✅ Kafka
   ✅ docker-compose + lifecycle   ✅ Postgres + SQL
```

**What I can now do by hand:**
- Explain a microservices architecture (5 services · sync vs async · the saga seed).
- Use Docker: **image vs container**, the **pull → create → start** lifecycle, networks, `exec`.
- Run & use **Redis** (key→value · Hash · Sorted Set · TTL).
- Reason about **Kafka** (topics · producers/consumers · consumer groups).
- Author a **`docker-compose.yml`** and boot the whole stack with one command.
- Run **Postgres** and write **SQL** (CREATE / INSERT / SELECT / WHERE / ORDER BY / ALTER).

### 🧰 Infra cheat-sheet (the stack I built)
```bash
cd ~/Documents/learn-microservices
docker compose up -d         # start Postgres + Redis + Kafka
docker compose ps            # what's running (NAME column = container names)
docker compose logs -f kafka # follow a service's logs
docker compose down          # stop & remove containers (volume data SURVIVES)
docker compose down -v       # also delete volumes (wipes DB data)

# talk to a datastore (CLI inside the container):
docker exec -it learn-postgres psql -U dev -d learn   # Postgres CLI
docker exec -it learn-redis redis-cli                 # Redis CLI
```
| Service | Container | Reach it at |
|---|---|---|
| Postgres 🐘 | `learn-postgres` | `localhost:5432` (user `dev` / pass `dev` / db `learn`) |
| Redis 🟥 | `learn-redis` | `localhost:6379` |
| Kafka 🟧 | `learn-kafka` | `localhost:9092` |

### ⚠️ Setup gotchas I hit (and fixes)
- **`docker` not recognized** in a terminal → that terminal opened *before* install; open a **fresh** one so PATH refreshes.
- **`nest.ps1 cannot be loaded` (PowerShell)** → script execution is disabled. Fix: `Set-ExecutionPolicy -Scope CurrentUser RemoteSigned`, **or** just use **Git Bash** (what I did).
- **"No such container: learn-microservices"** → that's the compose **group/project**, not a container. Use `learn-postgres` etc.; `docker compose ps` shows the real names.

---

# Module 1 — REST Fundamentals (NestJS + Spring Boot)

## Lesson 1 — app bootstrap + the request lifecycle (Nest ↔ Spring) 🧩

### 🎯 Concept — building blocks
A request flows through a **pipeline** of optional layers before reaching your handler:
```
 REQUEST → middleware → guards → interceptor(pre) → pipes → CONTROLLER → SERVICE
                                                              → interceptor(post) → RESPONSE
         (any throw → EXCEPTION FILTER)
```
- **Controller** = routes (`@Get/@Post`) · **Service** = logic (`@Injectable`) · **Module** = wiring (`@Module`).
- **Decorators** (`@Controller`, `@Get`, `@Injectable`) = tags that attach metadata so the framework wires/routes. **DI** = the framework creates & injects instances (never `new Service()`).

### 🏭 `NestFactory` — all options
| Method | Listens on | Use when | Analogy |
|---|---|---|---|
| `create()` | HTTP (Express/Fastify) | REST/web APIs | 🏪 shop w/ front door |
| `createMicroservice()` | TCP/Redis/Kafka/gRPC/NATS/RMQ/MQTT | message-driven, no HTTP | 📮 mail-slot only |
| `createApplicationContext()` | nothing | CLI/cron/scripts (just DI) | 🛠️ workshop, no customers |
- Hybrid: `create()` + `app.connectMicroservice()` → HTTP **and** messaging in one process.
- We used `create()` because tasks-service serves HTTP REST.

### ☕ Spring equivalent — `SpringApplication`
| Option | ≈ Nest |
|---|---|
| `SpringApplication.run(App.class, args)` | default boot |
| `SpringApplicationBuilder` | fluent options |
| `WebApplicationType.SERVLET` (MVC + Tomcat) | `create()` |
| `WebApplicationType.REACTIVE` (WebFlux + Netty) | `create()` reactive |
| `WebApplicationType.NONE` | `createApplicationContext()` |
- No single `createMicroservice()` → use a Boot app + starter (`spring-kafka`/`spring-amqp`/gRPC) + `@KafkaListener` etc.

### 🔀 Nest → Spring layer map (same flow, different names + extras)
```
 Middleware            → Servlet Filter
 Guard                 → Security filter chain + @PreAuthorize
 Interceptor(pre/post) → HandlerInterceptor + AOP @Around
 Pipe                  → ArgumentResolver + @Valid (Bean Validation) + Converter
 Controller            → @RestController
 Service               → @Service
 Exception Filter      → @ControllerAdvice / @ExceptionHandler
 (hidden router)       → ★ DispatcherServlet  (Spring's central front controller — the new piece)
 (—)                   → AOP @Around (cross-cutting: @Transactional, caching)
```

### 🔑 Takeaways
- Same pipeline stages in both; Spring renames them, adds the **DispatcherServlet** front controller + **AOP**, and **splits** guards (filter + @PreAuthorize) and pipes (binding + @Valid).
- `NestFactory.create` ↔ `SpringApplication.run` (SERVLET); `createApplicationContext` ↔ `WebApplicationType.NONE`; `createMicroservice` ↔ Boot app + messaging starter + listeners.

---

## Lesson 2 — IoC & Dependency Injection 🔌

### 🎯 Concept — "who is in control?"
```
 NORMAL control:   YOUR CODE ──creates & calls──► objects     (you are the boss)
 INVERTED control: FRAMEWORK ──creates & calls──► YOUR CODE    (framework is the boss)
                                                               ↑ this flip = the "Inversion"
```
- **IoC (Inversion of Control)** = hand control of *object creation, wiring & flow* over to a framework/container. Motto: *"Don't call us, we'll call you."* → **loose coupling is the RESULT**, not the definition.
- **DI (Dependency Injection)** = a *form* of IoC: the container **creates each dependency and injects it** into whoever declares a need — instead of the object doing `new` itself.

🍽️ **Analogy:** the IoC **container = a restaurant manager** who hires (creates) the chef & waiter and assigns (injects) them where needed. You just declare *"I need a chef."*

### 🔀 Other ways to achieve IoC (DI isn't the only one)
- **For getting dependencies:** **DI** (dependency is *pushed* into you) ✅ vs **Service Locator** (you *pull* it from a central registry — works, but hides dependencies → messier).
- **For inverting flow:** Events/Callbacks/Observer, Template Method, Factory.
```
 DI:              container ──pushes dep──►  you
 Service Locator: you ──"give me dep"──► registry ──returns──► you
```

### ☕ Nest ↔ Spring (same idea)
| Nest | Spring |
|---|---|
| `@Injectable()` + `providers:[]` | `@Service` + component-scan |
| injector (IoC container) | `ApplicationContext` |
| "provider" instance | "bean" |
| singleton by default | singleton by default |
| constructor injection | constructor injection |

### 🔑 Takeaways
- **IoC** = give up control of creation/flow (→ loose coupling). **DI** = a technique to do it (inject deps).
- The container makes **singletons**, resolves the dependency graph, and injects **by type**.
- DI vs Service Locator = **push vs pull** — same goal, DI is cleaner.

---

## Lesson 3 — a full CRUD REST resource (`/tasks`) 🧱

Built a complete `tasks` resource with the Nest CLI generators (`nest g module|service|controller tasks` → auto-wired into the module), then the 5 REST endpoints:

```
 GET    /tasks       → list all
 POST   /tasks       → create        (@Body + DTO + global ValidationPipe → 400 on bad input)
 GET    /tasks/:id   → get one       (@Param + ParseIntPipe → 400; NotFoundException → 404)
 PATCH  /tasks/:id   → partial update
 DELETE /tasks/:id   → delete
```

### 🧩 Pieces used (with their decorator kind)
- **Routing (method):** `@Get @Post @Patch @Delete`
- **Params (param):** `@Param('id', ParseIntPipe)` · `@Body()`
- **Validation (property, in DTOs):** `@IsString @IsNotEmpty @IsOptional @IsBoolean`
- **Pipes:** global `ValidationPipe` (in `main.ts`, checks DTOs) + param-level `ParseIntPipe` (string→number)
- **Exceptions:** `throw new NotFoundException(...)` → Nest's exception layer returns a clean 404
- **DTO rule:** a DTO is a **class** (survives to runtime for validation); fields use `!`/`?` for strict mode.
- **PUT vs PATCH:** PUT replaces the whole resource; PATCH updates only sent fields.

### 🐞 Mistakes → fixes (from my code review)
1. **`@Body('body')`** extracted a *field* named `body` (→ `undefined` → PATCH crashed). Fix: **`@Body()`** = the whole body. (`@Body('x')` = just field `x`.)
2. **Missing `return`** in controller `create`/`update`/`delete` (and `service.create`) → empty responses. Fix: `return` the service call / the created task.
3. **`@Get('all')`** put the list at `/tasks/all`. Fix: **`@Get()`** → `/tasks` (REST convention). Also: declare **static routes before dynamic `:id`** routes.
4. `==` → `===` for clean equality.

### 🔑 Takeaways
- A resource = **module + controller + service + DTOs**; the CLI auto-wires it.
- `@Body()` whole vs `@Body('field')` one; always **`return`** the service result.
- Pipes can be **global** (ValidationPipe) or **param-level** (ParseIntPipe); exceptions become HTTP responses automatically.

---

## 📑 Nest Decorator Reference (we go deep on each in its module)

Targets: **class · method · parameter · property** (there is no "file-level"; "module-level" = `@Module` on a class).

- **① Structure & DI** *(class/param)*: `@Module` `@Global` `@Injectable` `@Controller` · `@Inject(TOKEN)` `@Optional`
- **② HTTP routing** *(method)*: `@Get @Post @Put @Patch @Delete @Options @Head @All` · `@HttpCode` `@Header` `@Redirect` `@Render` `@Version` `@Sse`
- **③ Request data** *(param)*: `@Body` `@Param` `@Query` `@Headers` `@Req/@Res` `@Ip` `@Session` `@HostParam` `@UploadedFile`
- **④ Bind pipeline** *(class/method)*: `@UseGuards` `@UseInterceptors` `@UsePipes` `@UseFilters`
- **⑤ Exceptions** *(class)*: `@Catch`
- **⑥ Metadata & custom**: `@SetMetadata` · `createParamDecorator()` → your own `@Roles`/`@Public`/`@User` are built on these
- **⑦ Validation — class-validator** *(property)*: `@IsString @IsNotEmpty @IsOptional @IsInt @IsBoolean @IsEmail @IsEnum @Min @Max @Length @Matches @IsArray @ValidateNested @IsUUID …`
- **⑧ Transform — class-transformer** *(property)*: `@Expose @Exclude @Transform @Type`
- **⑨ Persistence — TypeORM** *(class/property) → M2*: `@Entity @Column @PrimaryGeneratedColumn @CreateDateColumn @OneToMany @ManyToOne @ManyToMany @JoinColumn @Index @Unique` · `@InjectRepository`
- **⑩ Swagger — @nestjs/swagger** *(docs)*: `@ApiTags @ApiOperation @ApiProperty @ApiPropertyOptional @ApiQuery @ApiParam @ApiBody @ApiBearerAuth @ApiExcludeEndpoint @ApiExcludeController` + response family `@ApiOkResponse @ApiCreatedResponse @ApiBadRequestResponse @ApiNotFoundResponse @ApiUnauthorizedResponse @ApiForbiddenResponse @ApiConflictResponse …`

**Deep-dive map:** M1 → routing/DI/validation · M2 → TypeORM · M3 → guards/metadata/filters/interceptors · add-on → Swagger.
**☕ Spring twin** for each = a Java annotation: `@RestController @GetMapping @RequestBody @Valid @NotBlank @ResponseStatus @ControllerAdvice @PreAuthorize @Entity @Column`, Swagger `@Operation/@Schema`.

> ⚠️ Note: `@ApiHideEndpoint`/`@ApiHideController` aren't real — use `@ApiExcludeEndpoint`/`@ApiExcludeController`. `@Public`/`@Roles`/`@Permissions` are **custom** (built on `@SetMetadata`), not built-in.

---

## 📖 Glossary (building as we go)

| Term | Plain meaning |
|---|---|
| **Microservice** | Small independent app owning one capability + its own DB |
| **API Gateway** | Single public entry point that routes to services & checks auth |
| **REST** | Calling a service over HTTP with JSON |
| **gRPC** | Fast, typed service-to-service calls using Protocol Buffers over HTTP/2 |
| **TCP transport** | NestJS's lightweight binary message channel between services |
| **Kafka** | Durable, replayable log of events that many services can consume |
| **Redis pub/sub** | Instant broadcast of messages to listeners (not stored) |
| **Saga** | Pattern for multi-service transactions using compensating steps |
| **Image** | Read-only blueprint to run software (downloaded or built) |
| **Container** | A running instance of an image (1 image → many containers) |
| **Volume** | Docker-managed disk that persists a container's data across restarts |
| **docker-compose** | One YAML file declaring several containers, started together |
| **Port mapping** | `HOST:CONTAINER` — bridges your laptop's port into a container |
| **psql** | The PostgreSQL command-line client (like `redis-cli` for Redis) |
| **Relation** | Postgres's word for a table |
| **TTL** | Time-to-live — a key's auto-expiry timer (Redis) |

---

---

# 🎤 Interview Prep — Wells Fargo (Java / J2EE Microservices)

> Interview: **Wed 3 Jun 2026, 3:00 PM with Jalal.** Role: Software Engineer — 2+ yrs Java/J2EE, microservice architecture, scripting + dev + testing + DevOps exposure, **secure web apps**.
> Format below = **🎯 Concept** (understand it) → **🗣️ Say this** (the crisp 30–60s answer to speak out loud). Spring Boot is covered elsewhere; this is *everything else*.
>
> 👉 **Round 2 (technical + managerial) deep-dive:** [`interview-prep/wells-fargo-round2.md`](interview-prep/wells-fargo-round2.md) — gRPC/resilience/Kafka, the **Spring MVC flow**, Docker→K8s deploy, caching, JWT logout, WebSocket scaling, **SQL & MongoDB query refreshers**, and the managerial round.

---

## Part A — Core Java & Concurrency ☕

### A1 — OOP: the 4 pillars
🎯 Encapsulation (hide state behind methods), Inheritance (reuse via `extends`), Polymorphism (one interface, many forms — overriding/overloading), Abstraction (expose *what*, hide *how*).
🗣️ *"The four pillars are encapsulation, inheritance, polymorphism and abstraction. In practice: I keep fields private and expose behaviour through methods (encapsulation), program to interfaces so I can swap implementations (abstraction + polymorphism), and use inheritance sparingly — I prefer composition over inheritance because deep hierarchies get rigid."*

### A2 — Abstract class vs Interface
🎯 Interface = a contract; since Java 8 it can have `default`/`static` methods; a class can implement many. Abstract class = partial implementation + state; single inheritance.
🗣️ *"Use an interface when you're defining a capability that unrelated classes can have — and you can implement many. Use an abstract class when classes share common state and code. Rule of thumb: interface for 'can-do', abstract class for 'is-a' with shared implementation. Java 8 blurred it with default methods, but interfaces still can't hold instance state."*

### A3 — `==` vs `.equals()`, and the equals/hashCode contract
🎯 `==` compares references (or primitive values); `.equals()` compares logical equality. If you override `equals`, you **must** override `hashCode` — equal objects must return equal hashcodes, or they break in `HashMap`/`HashSet`.
🗣️ *"`==` checks if two references point to the same object; `.equals()` checks logical equality. The key gotcha: if I override equals I must override hashCode too, because hash-based collections first bucket by hashCode then confirm with equals — if they're inconsistent, lookups silently fail."*

### A4 — String immutability, pool, StringBuilder
🎯 `String` is immutable (thread-safe, cacheable, safe as map keys). The string pool interns literals. Concatenation in loops creates garbage → use `StringBuilder` (not thread-safe, fast) or `StringBuffer` (synchronized).
🗣️ *"Strings are immutable, which makes them thread-safe and lets the JVM pool literals. The downside is concatenation in a loop creates a new object each time, so for heavy building I use StringBuilder — or StringBuffer if it must be thread-safe."*

### A5 — Collections framework
🎯 Know the map:
- `ArrayList` (array-backed, fast random access, slow mid-insert) vs `LinkedList` (fast insert/delete, slow access).
- `HashMap` (O(1), unordered, allows one null key) vs `LinkedHashMap` (insertion order) vs `TreeMap` (sorted, O(log n)).
- `HashSet`/`TreeSet`. `HashMap` internals: array of buckets; a bucket's collision chain **converts to a red-black tree only when it has 8+ nodes AND the table capacity ≥ 64** (`MIN_TREEIFY_CAPACITY`); below capacity 64 it **resizes instead of treeifying** (Java 8+).
- **Fail-fast** iterators throw `ConcurrentModificationException` if the collection mutates during iteration.
🗣️ *"I pick by access pattern: ArrayList for read-heavy indexed access, LinkedList for frequent insert/delete at ends. HashMap for O(1) keyed lookup — internally it's a bucket array, collisions chain and turn into a balanced tree past a threshold in Java 8. If I need ordering I use LinkedHashMap or TreeMap. And for concurrent access I never synchronize a HashMap manually — I use ConcurrentHashMap."*

### A6 — Checked vs unchecked exceptions, try-with-resources
🎯 Checked = compiler-enforced (`IOException`) — recoverable. Unchecked = `RuntimeException` (`NullPointer`, `IllegalArgument`) — programming bugs. `finally` always runs; **try-with-resources** auto-closes `AutoCloseable`.
🗣️ *"Checked exceptions are for recoverable conditions the caller should handle, like IO. Unchecked are programming errors — I don't catch NullPointer, I fix it. I use try-with-resources for anything Closeable so connections and streams close even on exceptions, which is cleaner than a finally block."*

### A7 — `final` / `finally` / `finalize`
🗣️ *"`final` makes a variable constant, a method un-overridable, or a class un-extendable. `finally` is the block that always runs after try/catch. `finalize` was a GC hook before cleanup — it's deprecated, I'd never use it; I use try-with-resources or Cleaner instead."*

### A8 — Java 8 functional features
🎯 Lambdas, functional interfaces (`Function`, `Predicate`, `Consumer`, `Supplier`), the **Stream API** (`map`/`filter`/`reduce`/`collect`), `Optional` (avoids null), method references.
🗣️ *"Java 8 brought lambdas and the Stream API, so I can express data pipelines declaratively — filter, map, collect — instead of manual loops. Streams can also go parallel with one call. And Optional lets me model 'maybe a value' explicitly instead of returning null and risking NPEs. One caveat: streams are single-use and parallel isn't always faster, so I benchmark."*

### A9 — JVM memory model & GC
🎯 **Heap** (objects, shared, GC'd — Young/Eden+Survivor + Old gen) vs **Stack** (per-thread, frames, primitives & references). Metaspace = class metadata. GC reclaims unreachable objects; modern collectors: G1 (default), ZGC (low-pause). `OutOfMemoryError` vs `StackOverflowError`.
🗣️ *"Each thread has its own stack for frames and local variables; objects live on the shared heap, which is split into young and old generations. Most objects die young, so the young-gen GC is cheap; survivors get promoted to old gen. The GC — G1 by default now — frees unreachable objects, so I don't free memory manually, but I can still leak by holding references, like an ever-growing static collection."*

### A10 — Concurrency (their stated focus 🔴)
🎯 Must-knows:
- **Thread vs Runnable** — implement `Runnable` (favour composition); thread lifecycle: New→Runnable→Running→Blocked/Waiting→Terminated.
- **`synchronized`** — mutual exclusion on a monitor; **`volatile`** — visibility only (no atomicity).
- **`ExecutorService` / thread pools** — don't `new Thread()` per task; reuse a pool.
- **`CompletableFuture`** — async composition without blocking.
- **`ConcurrentHashMap`**, **atomic classes** (`AtomicInteger` — CAS, lock-free), **`ThreadLocal`** (per-thread state).
- **Deadlock** (4 conditions: mutual exclusion, hold-and-wait, no preemption, circular wait) → avoid by consistent lock ordering.
- **`wait/notify`**, race conditions, `Callable` (returns a value vs `Runnable`).
🗣️ *"For concurrency I lean on `java.util.concurrent` rather than raw threads. I use an ExecutorService thread pool instead of creating threads per task, ConcurrentHashMap instead of a synchronized map, and atomic classes like AtomicInteger for lock-free counters using compare-and-swap. `synchronized` gives mutual exclusion; `volatile` only guarantees visibility, not atomicity — so `volatile count++` is still a race. For async flows I compose CompletableFutures. Deadlock I prevent by always acquiring locks in the same order."*

**🐞 Classic trap:** *"Is `volatile` enough for a counter?"* → **No** — `volatile` guarantees visibility but `count++` is read-modify-write (3 ops), not atomic. Use `AtomicInteger` or `synchronized`.

---

## 🧪 OOP Trick-Question Bank (machine-verified — 2 independent tracers agreed on every output)

> Predict the output before reading the answer. These are the ones interviewers actually use to separate "knows syntax" from "knows the JVM."

**T1 — 3-way overload resolution.** `go(long)`, `go(Integer)`, `go(int...)` all defined; call `go(5)` with `int i=5`. → **`long (widening)`**. Phases: **widening → boxing → varargs**, stops at first match. Widening `int→long` wins; never reaches boxing or varargs.

**T2 — `f(null)` ambiguous.** `f(String)` and `f(StringBuilder)` both defined; `f(null)`. → **Compile error: reference is ambiguous.** "Most specific" needs a subtype relationship; String & StringBuilder are unrelated siblings. (`f(String)` vs `f(Object)` *would* pick String — only because String ⊂ Object.) Fix: `f((String) null)`.

**T3 — `@Override` on a static.** Subclass writes `@Override static String tag(){...}`. → **Compile error: "static methods cannot be annotated with @Override."** Statics are *hidden*, not overridden. (Remove `@Override` → compiles, and an unqualified `tag()` inside an inherited instance method binds to the *declaring* class statically — prints the base version.)

**T4 — "Class wins" rule.** `class Q extends Base implements Greet`, where `Base` has concrete `hi()` and `Greet` has `default hi()`. → **prints `class`.** A superclass concrete method *always* beats an interface default — no diamond error. (Interface-vs-interface defaults *would* error until you override.)

**T5 — `final` parameter.** `static x(final StringBuilder sb){ sb.append("-WF"); sb = new StringBuilder("X"); }`. → **Compile error: "final parameter sb may not be assigned"** (the reassignment). `.append()` is fine — `final` freezes the *binding*, not the object. (Without the reassignment: mutation is visible to the caller — references are passed by value, aliasing still bites.)

**T6 — Interface static method via instance.** Interface has `static hello()` + `private static prefix()` (Java 9+) + `default greet()` calling `hello()`. `g.greet()` and `Greeter.hello()` print fine, but `g.hello()` → **hard compile error** (unlike classes, you can't call an interface static via an instance — must qualify `Greeter.hello()`).

**T7 — `this()` + `super()` together.** Constructor writes `super(); this(10);`. → **Compile error: "call to this must be first statement."** A constructor gets *exactly one* explicit `this()`/`super()`, and it must be first. (Swap order → "call to super must be first" — still fails.)

**T8 — Mutate a key after `put()`.** `Key` has correct `equals`+`hashCode` on `id`; `map.put(k,"found")`; then `k.id=99`. All of `map.get(k)`, `map.get(new Key(99))`, `map.get(new Key(1))`, `map.containsKey(k)` → **`null/null/null/false`.** Entry sits in bucket for hash=1 but the live key now hashes to 99 → permanently orphaned. **Hash keys must be immutable** in their equals/hashCode fields. (This is why `String`/`Integer` are immutable.)

**T9 — Fragile base class.** `CountingHashSet extends HashSet`, overrides `add` (++) *and* `addAll` (+= size, then `super.addAll`). `addAll(List.of("a","b","c"))` → **`6`, not 3.** `HashSet.addAll` internally calls `add`, which dynamic-dispatches to your override → double-count. *Effective Java* Item 18: **favour composition (forwarding wrapper) over inheritance.**

---

## 📦 Collections Framework — reference + machine-verified trick bank

**Hierarchy:** `Iterable → Collection → {List, Set, Queue}`; **`Map` is NOT a Collection** (deals in pairs).

| Type | Backing | Order | get | add | null | Thread-safe |
|---|---|---|---|---|---|---|
| **ArrayList** | array | insertion | O(1) | O(1)* tail | ✅ | ❌ |
| **LinkedList** | doubly-linked (also Deque) | insertion | O(n) | O(1) ends | ✅ | ❌ |
| **HashSet / HashMap** | bucket array | none | O(1) | O(1) | ✅ 1 null key | ❌ |
| **LinkedHashMap** | buckets + linked list | insertion/access | O(1) | O(1) | ✅ | ❌ |
| **TreeSet / TreeMap** | red-black tree | sorted | O(log n) | O(log n) | ❌ key (NPE) | ❌ |
| **ConcurrentHashMap** | buckets + CAS/bucket-lock | none | O(1) | O(1) | ❌ | ✅ |
| **PriorityQueue** | binary heap | heap (poll order) | — | O(log n) | ❌ | ❌ |
| **ArrayDeque** | resizable array | insertion | — | O(1) ends | ❌ | ✅ use for stack/queue |

**Key internals (interview gold):**
- **HashMap resize:** default cap 16, load factor 0.75 → threshold 12. Check is `if (++size > threshold) resize()` *after* insert → the **13th** put doubles capacity to 32, not the 12th.
- **Treeify:** needs **8+ nodes in a bucket AND table capacity ≥ 64**; below 64 it resizes instead.
- **LinkedHashMap** with `accessOrder=true` + `removeEldestEntry()` → instant **LRU cache**.
- **ConcurrentHashMap** beats `Collections.synchronizedMap` (whole-map lock) and legacy `Hashtable`.

**🧪 Verified trick questions (predict the output):**

1. **for-each remove escapes CME.** `["a","b","c"]`, remove `"b"` in a for-each → **`[a, c]`, NO exception.** `hasNext()` is `cursor != size` (no modCount check); removing the **penultimate** element ends the loop before `next()` re-checks. Same code on `["a","b","c","d"]` → **throws `ConcurrentModificationException`.** ⇒ *fail-fast is best-effort; never mutate while iterating — use `Iterator.remove()` or `removeIf()`.*
2. **`it.remove()` after `list.remove()` still throws CME** — from `Itr.remove()` itself. Use ONE mechanism only.
3. **Index remove in a counting loop skips elements.** `for(i...) if(even) list.remove(i)` on `[1,2,4,3]` → leaves `4` (shift moves it into the slot you just passed). Iterate backwards or `removeIf`.
4. **`TreeSet.add(null)` throws NPE even on an empty set** (natural ordering calls `compareTo`). Fix: `new TreeSet<>(Comparator.nullsFirst(naturalOrder()))`.
5. **Comparator inconsistent with equals breaks the Set contract.** Length-only comparator: `add("dog")` after `"cat"` is dropped (both len 3); `contains("fox")` → **true**; `remove("rat")` removes `"cat"`. Sorted collections define identity by `compareTo()==0`, not `equals()`.
6. **Null policy differs across "thread-safe" maps:** `ConcurrentHashMap` → no null key/value (NPE); `Collections.synchronizedMap(new HashMap<>())` → **allows** null key & values (keeps HashMap's policy); `Hashtable` → no null key/value.
7. **`Arrays.asList(1,2,3)`** → fixed-size: `set()` ✅, `add()` → `UnsupportedOperationException`. **`List.of(...)`** → fully immutable (`set()` throws too). **`Collections.unmodifiableList(src)`** → live *view*: editing `src` shows through; only edits *via the wrapper* throw. Snapshot = `List.copyOf(src)`.

---

## 🛠️ Collections — operations cheat (coding-round mechanics)

### 1. `list.remove(...)` — by index or by value? ⚠️
There is **no no-arg `remove()`** on `ArrayList`. Two overloads; the **argument type** decides:
```java
List<Integer> list = new ArrayList<>(List.of(10, 20, 30));
list.remove(1);                    // remove(int index) → removes INDEX 1 → [10, 30]
list.remove(Integer.valueOf(20));  // remove(Object)    → removes VALUE 20 → [10, 30]
```
> For a `List<Integer>`, `list.remove(2)` removes **index 2**, not the value 2. To delete a value, wrap it: `Integer.valueOf(2)`. (No ambiguity for `List<String>`.)

### 2. Using `ArrayList` as a stack / deque
```java
int lastIndex = list.size() - 1;   // last index (−1 if empty → guard!)

// PUSH
list.add(value);                   // append at END   → O(1) amortized ✅
list.add(0, value);                // push at FRONT   → O(n) (shifts all)
list.add(list.size(), value);      // also appends (index == size is allowed)

// PEEK
list.get(0);                       // first
list.get(list.size() - 1);         // last

// DELETE
list.remove(list.size() - 1);      // delete LAST → O(1) (returns removed element)
list.remove(0);                    // delete FIRST → O(n)
```
> ⚠️ To append, use `add(value)` or `add(size(), value)` — **not** `add(size()-1, value)` (that inserts *before* the last). Guard empty lists: `size()-1 == -1` → `IndexOutOfBoundsException`.
> 💡 Need fast push/pop at **both ends**? Use **`ArrayDeque`** (`addFirst/addLast/pollFirst/pollLast/peekFirst/peekLast`, all **O(1)**).

### 3. Conversions
```java
// List <-> TreeSet
TreeSet<Integer> ts = new TreeSet<>(list);      // List → TreeSet (sorts + dedups)
List<Integer> back  = new ArrayList<>(ts);      // TreeSet → List (sorted order)

// Object[] array <-> ArrayList
List<Integer> l   = new ArrayList<>(Arrays.asList(arr));  // Integer[] → List (mutable)
Integer[] arr2    = l.toArray(new Integer[0]);            // List → array

// Object[] array <-> TreeSet
TreeSet<Integer> t = new TreeSet<>(Arrays.asList(arr));   // Integer[] → TreeSet (sorted+dedup)
Integer[] arr3     = t.toArray(new Integer[0]);           // TreeSet → array
```
> ⚠️ **Primitive `int[]` gotcha:** `Arrays.asList(intArray)` makes a `List<int[]>` of size 1 — wrong! Use streams:
```java
List<Integer> li = Arrays.stream(prim).boxed().collect(Collectors.toList());            // int[] → List
TreeSet<Integer> tt = Arrays.stream(prim).boxed().collect(Collectors.toCollection(TreeSet::new));
int[] backArr = li.stream().mapToInt(Integer::intValue).toArray();                      // List → int[]
```

### 4. `HashMap` — add / get / delete / check key
```java
Map<String,Integer> map = new HashMap<>();
map.put("a", 1);                       // ADD (or update if key exists)
Integer v   = map.get("a");            // GET → value, or null if absent (no exception)
int safe    = map.getOrDefault("z", 0);// GET with default → 0
boolean has = map.containsKey("a");    // CHECK key exists → true
map.remove("a");                       // DELETE → returns old value
// idioms:
map.putIfAbsent("a", 1);               // add only if absent
map.merge("a", 1, Integer::sum);       // counting: a = (a ?? 0) + 1
map.computeIfAbsent("k", x -> new ArrayList<>()).add(5);  // multimap pattern
```
> Prefer `containsKey` over `get` when a key may legitimately map to `null` (a `null` from `get` is ambiguous: absent vs present-but-null). Otherwise `getOrDefault` is the clean one-liner.

---

## Part B — Microservices Patterns 🧩

### B1 — Monolith vs Microservices (lead with trade-offs, not hype)
🗣️ *"A monolith is one deployable — simpler to build, test and deploy early, but it scales as a unit and a bug can take everything down. Microservices split by business capability, each with its own database and deploy cycle, so teams ship independently and scale hot paths alone. The cost is distributed-systems complexity: network failures, eventual consistency, harder debugging. So I don't start with microservices — I start with a well-modularised monolith and extract services when a clear boundary and scaling need appears."*

### B2 — API Gateway
🎯 Single entry point: routing, auth/JWT validation, rate limiting, SSL termination, aggregation. (Spring Cloud Gateway.)
🗣️ *"The gateway is the one public front door. It routes to internal services, validates the JWT once so services don't each re-auth, and handles cross-cutting concerns like rate limiting and SSL termination. It stops clients from needing to know the internal topology."*

### B3 — Service Discovery
🎯 Services register with a registry (Eureka/Consul); callers look up live instances instead of hardcoding hosts. Client-side vs server-side discovery.
🗣️ *"In a dynamic environment instances come and go and IPs change, so I don't hardcode addresses. Services register with a registry like Eureka, and a caller asks the registry for healthy instances — then a client-side load balancer picks one. Kubernetes does the same thing natively through its Service DNS."*

### B4 — Circuit Breaker & resilience
🎯 Prevents cascading failure. States: **Closed → Open → Half-Open**. (Resilience4j.) Plus retries, timeouts, bulkheads, fallbacks.
🗣️ *"If a downstream service is failing, hammering it makes things worse and ties up my threads. A circuit breaker — I use Resilience4j — trips to Open after a failure threshold and fails fast with a fallback, then goes Half-Open to test recovery. I pair it with timeouts, bounded retries with backoff, and bulkheads to isolate thread pools so one slow dependency can't sink the whole service."*

### B5 — Inter-service communication: sync vs async
🎯 **Sync** = REST/gRPC (request-reply, temporal coupling). **Async** = messaging/Kafka (decoupled, resilient, eventual consistency).
🗣️ *"Synchronous REST or gRPC is fine for a query where I need an answer now, but it couples availability — if the callee is down, I'm down. For anything that can be eventual, I prefer async messaging over Kafka: the producer fires an event and moves on, consumers process at their pace, and a slow consumer doesn't block the producer. gRPC I reach for when I need fast, typed, high-volume internal calls."*

### B6 — Database per service & the consistency problem
🎯 Each service owns its DB (no shared tables → loose coupling). But you lose cross-service ACID transactions → **eventual consistency**.
🗣️ *"Each service owns its own database so no one reaches into another's tables — that's what keeps them independently deployable. The trade-off is you can't do a single ACID transaction across services, so I design for eventual consistency and use the Saga pattern for multi-service workflows."*

### B7 — Saga pattern (distributed transactions) 🔴
🎯 Sequence of local transactions; each step publishes an event triggering the next. On failure, run **compensating transactions** to undo. Two styles: **Choreography** (services react to events, no central brain) vs **Orchestration** (a central orchestrator directs steps).
🗣️ *"Since I can't span a transaction across services, a Saga breaks it into local transactions, one per service, chained by events. If a later step fails, I run compensating actions to undo the earlier ones — like cancelling a reservation instead of rolling back. Choreography means each service listens and reacts, which is decoupled but hard to trace; orchestration uses a central coordinator, which is easier to reason about and monitor. I pick orchestration when the flow is complex."*

### B8 — CQRS & Event Sourcing (know the gist)
🗣️ *"CQRS separates the write model from the read model so each scales and is optimised independently — useful when reads vastly outnumber writes. Event sourcing stores the sequence of state-changing events as the source of truth instead of just the current state, so I get a full audit log and can rebuild state. They're powerful but add complexity, so I use them only where the domain justifies it — not by default."*

### B9 — Distributed tracing & observability
🎯 A request spans many services → correlation IDs + tracing (Sleuth/Micrometer + Zipkin/Jaeger). The three pillars: **logs, metrics, traces**.
🗣️ *"In a distributed system one user action touches many services, so I propagate a correlation/trace ID across calls and use Zipkin or Jaeger to see the whole request path and where latency is. Observability is three pillars — centralised logs, metrics like Prometheus, and traces — so I can actually debug production."*

### B10 — Idempotency (banking-relevant 💰)
🗣️ *"For a bank, retries are inevitable, so non-query operations must be idempotent — processing the same request twice mustn't charge a customer twice. I enforce it with an idempotency key the client sends; the server records processed keys and returns the original result on a duplicate. It's the safety net that makes retries and at-least-once messaging safe."*

---

## Part C — DevOps, Testing & Security 🔧

### C1 — CI/CD pipeline
🎯 CI = automatically build + test every commit. CD = automated release. Pipeline: commit → build → unit tests → static analysis (SonarQube) → package → deploy to staging → integration tests → deploy to prod. Tools: Jenkins, GitLab CI, GitHub Actions.
🗣️ *"CI means every push triggers a build and the full test suite, so integration problems surface in minutes, not at release. CD extends that to automated deployment through environments. A typical Jenkins pipeline for me: build, unit tests, Sonar quality gate, build a Docker image, deploy to staging, run integration tests, then promote to prod — ideally with blue-green or canary so rollback is instant."*

### C2 — Docker (image vs container, layers)
🎯 **Image** = read-only blueprint (layered). **Container** = running instance. Dockerfile builds layers (cached). **Multi-stage builds** keep the final image small. (Already in your Glossary — reuse it.)
🗣️ *"An image is an immutable, layered blueprint; a container is a running instance of it — one image, many containers. Layers are cached, so I order my Dockerfile to put rarely-changing steps first. For Java I use a multi-stage build: build the jar in a Maven stage, then copy just the jar into a slim JRE image, so the shipped image is small and has less attack surface."*

### C3 — Kubernetes (the why + core objects)
🎯 Container orchestration: self-healing, scaling, rolling updates, service discovery. Objects: **Pod** (smallest unit, 1+ containers), **Deployment** (declarative replicas + rolling updates), **Service** (stable network endpoint + load balancing), **ConfigMap/Secret**, **Ingress**.
🗣️ *"Once you have many containers you need something to schedule, heal and scale them — that's Kubernetes. I describe the desired state declaratively: a Deployment says 'run 3 replicas of this image', and K8s keeps it true, restarting crashed pods and doing rolling updates with zero downtime. A Service gives a stable address and load-balances across pods, and ConfigMaps/Secrets externalise config so the same image runs in every environment."*

### C4 — Git workflow
🗣️ *"I work on feature branches off main, keep commits small and meaningful, open a pull request for review and CI, and merge once it's green and approved. I rebase to keep history clean and use trunk-based or GitFlow depending on the team. I resolve merge conflicts by understanding both sides, never blind-accepting."*

### C5 — Testing: pyramid, JUnit, Mockito, TDD
🎯 **Test pyramid**: many fast unit tests, fewer integration, fewest E2E. **JUnit 5** (`@Test`, `@BeforeEach`, `@ParameterizedTest`), **Mockito** (`mock`, `when().thenReturn()`, `verify()`) to isolate the unit. **TDD** = red→green→refactor.
🗣️ *"I follow the test pyramid — lots of fast unit tests, fewer integration tests, very few end-to-end, because E2E is slow and brittle. Unit tests use JUnit 5 with Mockito to stub collaborators so I'm testing one class in isolation. For integration I spin up real dependencies — often Testcontainers for a real Postgres. I practise TDD where the design is unclear: write a failing test, make it pass, refactor. Good tests are also living documentation."*

### C6 — Security: OAuth2 + JWT 🔴 (JD stresses "secure web apps")
🎯 **AuthN** (who you are) vs **AuthZ** (what you can do). **JWT** = signed token with 3 parts (header.payload.signature), base64; stateless — server verifies the signature, no session lookup. **OAuth2** = delegated authorization (access token + refresh token); OpenID Connect adds identity.
🗣️ *"Authentication is proving who you are; authorization is what you're allowed to do. I keep services stateless with JWTs: on login the auth service issues a signed token, the client sends it as a Bearer header, and each service verifies the signature locally — no shared session store, which scales horizontally. The token carries claims like roles for authorization. I keep tokens short-lived with refresh tokens, always over HTTPS, and never put secrets in the payload since it's only signed, not encrypted. OAuth2 is the framework for delegated access — issuing and scoping those tokens."*

### C7 — Secure coding (OWASP essentials)
🎯 **SQL injection** → parameterised queries / prepared statements (never string-concat SQL). **XSS** → output encoding. **CSRF** → tokens. Plus: validate all input, least privilege, encrypt in transit (TLS) and at rest, no secrets in code (use a vault), dependency scanning.
🗣️ *"For a bank security is non-negotiable. The basics I always apply: parameterised queries to kill SQL injection, output encoding against XSS, validate and never trust input, principle of least privilege on every service and DB account, TLS in transit and encryption at rest, secrets in a vault not in code, and dependency scanning in the pipeline to catch vulnerable libraries. I think in terms of the OWASP Top 10."*

---

## 🔑 Day-of cheat lines (memorise these openers)
- **Why microservices?** → *"Independent deploy + scale per capability — at the cost of distributed-systems complexity, so I extract them from a monolith only when justified."*
- **volatile vs synchronized** → *"volatile = visibility; synchronized = visibility + atomicity."*
- **Saga** → *"Local transactions chained by events, with compensating actions to undo on failure."*
- **JWT** → *"Stateless, signed, self-contained — each service verifies locally."*
- **Circuit breaker** → *"Fail fast when a dependency is sick; Closed→Open→Half-Open."*
- **equals/hashCode** → *"Override both together or hash collections break."*
- **Idempotency** → *"Same request twice = same effect once — essential for safe retries in payments."*

## 🗣️ STAR stories to prep (behavioural — they will ask)
Pick 2–3 real ones from your work and frame each as **Situation → Task → Action → Result**:
1. A production bug / incident you debugged (shows ownership).
2. A performance or design improvement you drove (shows the "service quality & availability" line in the JD).
3. A time you disagreed technically and resolved it (shows collaboration with product owners — also in the JD).

---

_Journal started during Module 0. Pushed periodically by me (DIVAKAR-REDDY-VARUGU)._

---

## 🟧 Module 5 · Lesson 1 — Kafka deep dive: architecture + the book analogy 📚

### 🎯 Concept
Kafka is a **cluster of servers (brokers)** storing **topics** — each split into ordered logs called **partitions**, replicated across brokers for safety. **Producers** write events; **consumer groups** read them, each consumer owning some partitions, tracking progress via **offsets** stored in Kafka itself.

### 📚 The whole thing as a LIBRARY
The analogy that made it click — every Kafka part has a book-world twin:

| 📚 Library world | ⚙️ Kafka | what it DOES |
|---|---|---|
| Library | **Cluster** | the whole system |
| Shelf | **Broker** | a server; physically stores partitions on disk |
| Book series (title) | **Topic** | a logical named feed (`orders`) — NOT on one shelf |
| Volume of the series | **Partition** | a physical, ordered append-only log; unit of parallelism + ordering |
| Page | **Message / Record** | one event (key + value + headers + timestamp) |
| Page number | **Offset** | position of a message within its volume |
| Storyline tag | **Key** | hashes to a volume → same key stays in one volume, in order |
| Master copy / photocopy | **Leader / Follower** | leader serves reads/writes; followers (ISR) are backups |
| Author | **Producer** | writes pages; the key picks the volume |
| Reader | **Consumer** | reads pages from assigned volumes |
| Reading club | **Consumer group** | splits volumes among members (1 volume → 1 reader) |
| Library's bookmark ledger | **`__consumer_offsets`** | stores each club's bookmark per volume (survives restarts) |
| Keep books N days, recycle | **Retention** | log kept for replay; old pages deleted |
| Re-divide volumes on join/leave | **Rebalance** | redistributes partitions when membership changes |
| Librarian's catalog | **KRaft / ZooKeeper** | tracks masters; runs leader election on failure |

**The flow:** an **author** (producer) adds a **page** (message) to a **series** (topic) tagged with a **storyline** (key) → the key picks a **volume** (partition); the librarian stamps a **page number** (offset) and stores it on a **shelf** (broker) with **photocopies** (replicas). A **reading club** (consumer group) divides the volumes among **readers** (consumers) — one volume per reader — and each reader saves its **bookmark** (offset) in the **library ledger** (`__consumer_offsets`), so progress survives restarts and old pages can be replayed. Different clubs (groups) each read the whole series independently.

### 🎥 Happy-flow diagram
```
 Producer-A ─send(topic="orders", key=custId)─┐   (key → which volume)
 Producer-B ─send(topic="payments")───────────┼─┐
                                              ▼ ▼
 Topic "orders"   → [P0][P1][P2]      Topic "payments" → [P0][P1]    (partitions = volumes)
        │  partitions spread + replicated across brokers ↓
 Broker-1: orders-P0(L)  orders-P2(f)  payments-P0(L)
 Broker-2: orders-P1(L)  orders-P0(f)  payments-P1(L)
 Broker-3: orders-P2(L)  orders-P1(f)  payments-P0(f) payments-P1(f)     (L=leader f=follower)
        │  consumers subscribe by TOPIC; Kafka assigns partitions ↓
 Group "ship"      : P0→C1  P1→C2  P2→C3      (split work, each msg once)
 Group "analytics" : P0,P1→Cx  P2→Cy          (DIFFERENT group → gets ALL msgs again)
```

### 💬 My doubts → answers
- **Is a topic inside a broker?** No — a topic is *logical* (a name). Its **partitions** are physical and **spread across brokers**. (Book title vs its volumes on shelves.)
- **Does the producer send via the topic name?** Yes — to the **topic** + a **key**; the partitioner then picks the **partition** (`hash(key) % #partitions`; no key → round-robin). Same action.
- **Does the consumer connect to the topic or the partitions?** It **subscribes to the topic**; Kafka **assigns it partitions** (one partition → one consumer per group).
- **Where is "how far I've read" stored?** NOT in the consumer — in Kafka's **`__consumer_offsets`** topic, keyed by (group, topic, partition). That's why progress survives a restart.
- **Can two groups share one consumer?** No — a consumer instance has one `group.id`. Same group = split work; different groups = each replays everything.
- **What if a broker dies?** A follower **photocopy** is promoted to **leader** (if replication ≥ 2) → no data lost, stays available.
- **Who hosts / pays?** You run brokers yourself (Docker/VMs — you pay infra) OR a managed provider (Confluent Cloud / AWS MSK — you pay them). Data lives on broker disks.

### 🔑 Key takeaways
- **Topic = logical name; partition = physical ordered log on a broker.**
- **Key → partition** (ordering is guaranteed *within* a partition only).
- **Consumer group splits partitions; offsets live in Kafka, not the consumer.**
- **Same group = load-balance; different groups = broadcast.**
- **Replication = failover; retention = replay.**

---

## 🧩 Module 5 · Lesson 2 — Kafka edge cases, failures & costs (library analogy)
> The corner cases distributed-systems interviews probe: consumer downs, broker/partition failures, lost logs, missing bookmarks, shrinking resources mid-process, delivery/ordering guarantees, costs, and multiple simultaneous failures — each explained with the shelves/volumes/photocopies/readers/club/ledger analogy.

### Consumer (reader) failures & lag

A reading club splits the series' volumes among its members. Each member reads pages in order and writes their progress into the library's central bookmark ledger (`__consumer_offsets`). Almost every failure below comes down to one question: **did the bookmark move before or after the work was actually done?**

---

#### ⚠️ Scenario 1 — Reader crashes mid-read (uncommitted work)

- **⚠️ Scenario** — A consumer fetches a batch, processes some of it, then dies before committing its offset.
- **⚙️ What Kafka does** — On the next poll the replacement consumer reads the *committed* offset from `__consumer_offsets`. Anything processed-but-not-committed is **re-delivered** → at-least-once delivery, possible duplicates. With `enable.auto.commit=true` the opposite risk exists: the auto-commit timer (`auto.commit.interval.ms`) can advance the offset *past* records you haven't finished, causing silent **data loss**.
- **📚 In the library** — A reader finishes pages 50–55 but is yanked away before updating the club's bookmark, which still says 49. The next member opens to page 50 and re-reads 50–55. If instead the bookmark auto-advances to 55 the moment the book is opened, and the reader is interrupted at page 52, pages 53–55 are skipped forever.
- **🛡️ Mitigation** — Disable auto-commit (`enable.auto.commit=false`); commit **after** processing. Make handlers idempotent (dedupe by key/offset) so re-delivery is harmless. For true exactly-once, use transactions + `isolation.level=read_committed`.
- **💰 Cost/trade-off** — Manual/transactional commits add latency and dedupe bookkeeping; the price of not losing or double-counting pages.

---

#### ⚠️ Scenario 2 — Slow reader / consumer lag (falling behind the author)

- **⚠️ Scenario** — The producer appends faster than the consumer drains. Lag (log-end-offset − committed-offset) grows without bound.
- **⚙️ What Kafka does** — Nothing breaks immediately — partitions just buffer. But if lag exceeds what **retention** (`retention.ms` / `retention.bytes`) keeps, unread records are deleted before they're read. The consumer's next fetch lands on a gone offset and `auto.offset.reset` decides the jump: `latest` skips the backlog, `earliest` replays from the oldest surviving page.
- **📚 In the library** — The author writes new pages faster than the reader turns them. The reader keeps falling behind until retention ("keep N days, recycle old pages") shreds pages the bookmark hasn't reached yet. The reader returns to a torn-out page number and is bounced to either the newest page (skip the gap) or the oldest surviving one (start over).
- **🛡️ Mitigation** — Add consumers up to the partition count to parallelize; speed up the handler (batch, async I/O); raise `max.poll.records` throughput; lengthen retention as a safety margin. Alert on lag via `kafka-consumer-groups --describe`.

```
volume head ───────────────────────────────► author writes here
         ^committed (bookmark)        ^log-end
         └──────── LAG (unread) ───────┘
```

- **💰 Cost/trade-off** — More consumers/partitions and longer retention = more CPU and disk; lag monitoring needs tooling.

---

#### ⚠️ Scenario 3 — More readers than volumes (idle readers)

- **⚠️ Scenario** — A consumer group has more members than the topic has partitions.
- **⚙️ What Kafka does** — A partition is assigned to **at most one consumer per group**. Surplus consumers get **zero** partitions and sit idle — they're hot standbys, not extra throughput.
- **📚 In the library** — The series has 4 volumes but the club shows up with 6 readers. Each volume can be held by only one reader at a time, so 4 read and 2 sit empty-handed. Adding readers past 4 buys you nothing but faster failover.
- **🛡️ Mitigation** — Size the group ≤ partition count. If you genuinely need more parallelism, **add partitions** (note: this changes key→volume hashing for new records and is one-way — you can't easily shrink). Keep a couple of idle members deliberately as warm standbys.
- **💰 Cost/trade-off** — Idle consumers waste compute/licensing; over-partitioning adds per-partition overhead (memory, open files, rebalance cost).

---

#### ⚠️ Scenario 4 — Rebalance storms (members joining/leaving constantly)

- **⚠️ Scenario** — Members flap in and out, triggering back-to-back rebalances; the group spends more time re-dividing work than processing.
- **⚙️ What Kafka does** — Each join/leave fires a **rebalance** that re-divides partitions. With the classic eager protocol, a **stop-the-world** pause halts *all* consumers until reassignment completes. Causes: handlers slower than `max.poll.interval.ms` (consumer deemed dead, ejected, rejoins on next poll → loop), or missed heartbeats past `session.timeout.ms`.
- **📚 In the library** — Every time a reader walks in or out, the whole club stops and re-deals all volumes from scratch. If members keep flickering, the club is perpetually reshuffling volumes instead of reading pages — the bookmark ledger churns while no story gets read.
- **🛡️ Mitigation** — Tune `max.poll.interval.ms` up (or shrink `max.poll.records`) so slow batches don't look dead. Use **cooperative-sticky** assignor (`partition.assignment.strategy`) to keep most assignments and rebalance incrementally. Use **static membership** (`group.instance.id`) so restarts don't trigger reassignment. Tune `session.timeout.ms` / `heartbeat.interval.ms` to ride out blips.

```
eager:        ⏸ ALL stop → re-deal every volume → ▶ resume
cooperative:  keep what you hold → only move the few that must change
```

- **💰 Cost/trade-off** — Higher timeouts slow detection of genuinely dead readers; static membership needs stable, unique instance IDs.

---

#### ⚠️ Scenario 5 — Poison page that crashes every reader (dead-letter handling)

- **⚠️ Scenario** — One malformed record throws on every attempt; the consumer never commits past it and crashes/retries forever — head-of-line blocking.
- **⚙️ What Kafka does** — Kafka has no built-in skip. The bad offset never commits, so on restart the same record is re-fetched and re-poisons the consumer. The whole partition behind it is **stuck** — a single page jams the entire volume.
- **📚 In the library** — Page 88 is gibberish that makes every reader faint. Because the bookmark can't advance past 88, each new reader opens to 88, faints, and the rest of the volume is never read. The single bad page blocks the whole volume indefinitely.
- **🛡️ Mitigation** — Wrap processing in try/catch; on repeated failure, route the record to a **dead-letter topic (DLT)** and commit past it so the partition flows again. Cap retries (e.g. retry topic with backoff), then DLT. Inspect/replay the DLT out of band.

```
volume ──[86][87][💀88][89][90]──►
              │ N retries fail
              ▼
         dead-letter topic   then commit→89, volume flows
```

- **💰 Cost/trade-off** — Extra topic + alerting/replay pipeline to run, and you accept that poison records are deferred rather than processed in line.

---

#### ⚠️ Scenario 6 — Reader stuck/hung, not committing

- **⚠️ Scenario** — A consumer is alive (heartbeating) but wedged inside processing — a hung downstream call, deadlock, infinite loop — so it polls slowly or not at all and stops committing.
- **⚙️ What Kafka does** — Heartbeats run on a background thread, so the broker may still think the consumer is healthy while no progress is made. If `poll()` isn't called within `max.poll.interval.ms`, the broker finally considers it dead, **ejects** it, and rebalances its partitions to others. Until then, lag on its partitions climbs and offsets stall.
- **📚 In the library** — The reader is still nodding at the desk (raising a hand each minute = heartbeat) but hasn't turned a page in an hour — frozen on one paragraph. The club thinks they're fine, so their volumes sit unread and the bookmark doesn't move, until the "no page turned in X minutes" rule (`max.poll.interval.ms`) finally reassigns those volumes.
- **🛡️ Mitigation** — Put **timeouts** on all downstream calls inside the handler so it can't hang. Keep work per poll bounded (small `max.poll.records`). Set `max.poll.interval.ms` to a realistic ceiling so a genuine hang gets ejected promptly. Monitor per-partition lag for stalls that heartbeats alone won't reveal.
- **💰 Cost/trade-off** — Aggressive timeouts/intervals risk evicting merely-slow readers (extra rebalances); lenient ones let a hang stall the partition longer.

### Broker / partition (shelf / volume) failures

A shelf is just a server. Shelves collapse: power dies, disk fills, the rack catches fire. Kafka survives because every volume is photocopied onto *other* shelves — but the details of *how cleanly* it survives are where the edge cases live.

```
Topic "orders" (book series)
 └─ Partition 2 (volume)  RF=3
      ├─ Shelf A : MASTER COPY  (leader)   ← reads+writes go here
      ├─ Shelf B : photocopy    (follower, in-sync)  ┐ ISR = {A,B,C}
      └─ Shelf C : photocopy    (follower, in-sync)  ┘
```

---

- **⚠️ Scenario — A shelf collapses (broker goes down).**
- **⚙️ What Kafka does** — The broker stops sending heartbeats; the controller (KRaft/ZooKeeper) detects it via `zookeeper.session.timeout.ms` / KRaft broker session expiry and marks it dead. Every partition whose **leader** lived on that broker is now leaderless, so the controller triggers **leader election** for those partitions. Followers on surviving brokers keep their data; only leadership needs reassigning.
- **📚 In the library** — A shelf collapses. The librarian's catalog notices the shelf stopped answering roll-call. For every volume whose **master copy** sat on that shelf, the catalog must crown a new master from the surviving **photocopies** on other shelves. Volumes whose photocopies (not masters) were on the dead shelf are unaffected — readers/authors don't even notice those.
- **🛡️ Mitigation** — Replication factor ≥ 3 so a master loss still leaves living photocopies; spread replicas across racks/AZs with `broker.rack` so one rack outage ≠ one volume gone; keep `unclean.leader.election.enable=false`.
- **💰 Cost/trade-off** — RF=3 means 3× the disk and 3× the cross-broker write traffic for one volume's safety.

---

- **⚠️ Scenario — The master copy was on the dead shelf → promote a photocopy (leader election).**
- **⚙️ What Kafka does** — The controller picks the **new leader from the ISR** (in-sync replica set), preferring the first live ISR member. With clean election it only ever promotes a replica that had caught up to the old leader's high-watermark, so **no committed message is lost**. Clients holding stale metadata get `NOT_LEADER_FOR_PARTITION` / `LEADER_NOT_AVAILABLE`, refresh metadata, and reconnect to the new leader.
- **📚 In the library** — The master copy burned with the shelf, so the catalog promotes one of the **in-sync photocopies** to be the new master. Because that photocopy was fully caught up (every committed page it had matched the old master's), no committed page is lost. Authors and readers who still think the old shelf holds the master get told "wrong shelf — check the catalog again," look it up, and walk to the new shelf.
- **🛡️ Mitigation** — Ensure at least 2 replicas stay in ISR so a clean promotion is always possible; tune `replica.lag.time.max.ms` so healthy followers don't drop out spuriously; enable `enable.auto.commit` carefully and rely on producer retries to ride out the metadata refresh.
- **💰 Cost/trade-off** — Election + metadata propagation costs a short unavailability window (typically sub-second to a few seconds) per affected volume.

---

- **⚠️ Scenario — Under-replication (fewer in-sync photocopies than you want).**
- **⚙️ What Kafka does** — When a replica falls behind or its broker dies, the partition becomes **under-replicated**: live replicas < replication factor (surfaced as `UnderReplicatedPartitions > 0`). The partition is still readable/writable, but durability is reduced — you're one more failure away from data loss. If ISR drops below `min.insync.replicas`, producers using `acks=all` start getting `NotEnoughReplicasException` and writes are rejected.
- **📚 In the library** — You *wanted* 3 copies of each volume but a shelf is gone, so some volumes now have only 2 living copies — **under-replicated**. The series still circulates, but you've lost a safety net. If you'd promised authors "I won't confirm a page until N copies exist" (`min.insync.replicas`) and fewer than N copies remain, the librarian refuses new pages until copies are rebuilt.
- **🛡️ Mitigation** — Replace/restart the dead broker so Kafka re-replicates and ISR refills; set `min.insync.replicas=2` with RF=3 (tolerates one loss, still safe); alert on `UnderReplicatedPartitions`.
- **💰 Cost/trade-off** — Setting `min.insync.replicas` high buys durability but means a single broker loss can *block writes* — availability traded for safety.

---

- **⚠️ Scenario — ISR shrinks because a photocopy lags.**
- **⚙️ What Kafka does** — A follower that stops fetching, or falls more than `replica.lag.time.max.ms` (default 30s) behind the leader's log end, is **ejected from the ISR** by the leader. The partition keeps serving from the remaining ISR members. When the slow replica catches back up to the high-watermark, the leader re-admits it to the ISR automatically.
- **📚 In the library** — One photocopy machine is slow and its copy is missing the last hundred pages. The master stops counting that photocopy as "in-sync" and removes it from the **in-sync set** — it's still a copy, just not a trusted-current one, so it's no longer eligible to be promoted to master. Once it transcribes the missing pages and catches up, it rejoins the in-sync set.
- **🛡️ Mitigation** — Fix the root cause of lag (slow disk, network, GC pauses, hot partition); size `num.replica.fetchers` adequately; don't set `replica.lag.time.max.ms` so low that healthy followers flap in and out.
- **💰 Cost/trade-off** — A shrunk ISR silently lowers your effective redundancy — you may *think* you have RF=3 protection while really running on 2.

---

- **⚠️ Scenario — UNCLEAN leader election (promote an out-of-date photocopy → pages lost).**
- **⚙️ What Kafka does** — If **all** ISR replicas are dead and `unclean.leader.election.enable=true`, the controller promotes a *non-ISR*, stale replica to leader to restore availability. That replica is missing messages the old leader had committed, so those records are **permanently lost** and the log is effectively truncated — availability is bought at the price of correctness. With the (default, recommended) `false`, the partition instead stays **offline** until an ISR member returns.
- **📚 In the library** — Every in-sync copy is gone; the only surviving photocopy is an old one missing the last fifty pages. With unclean election *on*, the catalog crowns that stale copy as the new master anyway — the series is back in circulation, but those fifty committed pages are **gone forever**, and the volume's later page numbers will be re-used for different content. With it *off*, the librarian declares that volume **closed** until a current copy resurfaces.
- **📚 diagram**
```
Old master pages:  ...48 49 50 51 52   (50,51,52 committed)
Stale photocopy :  ...48 49           (never got 50-52)
 unclean=true  → promote stale → pages 50,51,52 LOST, offsets reused
 unclean=false → volume OFFLINE until an in-sync copy comes back
```
- **🛡️ Mitigation** — Keep `unclean.leader.election.enable=false` (default since Kafka 0.11) for any data you can't afford to lose; combine RF=3 + `min.insync.replicas=2` + `acks=all` so a clean leader almost always exists; spread replicas across failure domains.
- **💰 Cost/trade-off** — `false` = possible downtime (correctness first); `true` = stay up but silently lose data — pick which one hurts your business less.

---

- **⚠️ Scenario — What readers and authors experience during failover.**
- **⚙️ What Kafka does** — **Authors (producers)** see transient errors (`NOT_LEADER_FOR_PARTITION`, `NotEnoughReplicasException`, timeouts); with `retries` (default high) + `acks=all` + idempotence (`enable.idempotence=true`) they retry transparently once the new leader is elected — no duplicates, no loss for committed writes. **Readers (consumers)** pause briefly, refetch metadata, and resume **from their committed offset** in `__consumer_offsets`; the broker death may also trip a **consumer-group rebalance** if a consumer was co-located. They re-read from their last committed bookmark, so at-least-once consumers may reprocess a few records.
- **📚 In the library** — During the hand-over, **authors** trying to add pages get "not the master, try again," and their retry logic keeps knocking until the new master is crowned — committed pages survive, and idempotence stops the same page being filed twice. **Readers** briefly can't borrow the volume, re-check the catalog, then resume from the bookmark recorded in the **central bookmark-ledger** — so a club picks up exactly where it left off (occasionally re-reading the last page or two). If the failure also shuffled club membership, the club **re-divides the volumes** among its members before reading on.
- **🛡️ Mitigation** — Producers: `acks=all`, `enable.idempotence=true`, generous `delivery.timeout.ms`/`retries`. Consumers: commit offsets carefully and design handlers to be idempotent (because reprocessing can happen); tune `session.timeout.ms` / `max.poll.interval.ms` so brief failover doesn't trigger needless rebalances.
- **💰 Cost/trade-off** — Strong guarantees (`acks=all` + idempotence) add write latency and a touch of throughput overhead; the failover itself costs a few seconds of partition unavailability.

> **One-line takeaway:** Replication keeps the series alive when a shelf falls, but **ISR + `min.insync.replicas` + `acks=all` + `unclean.leader.election.enable=false`** are the four knobs that decide whether failover loses *no pages* (and maybe pauses) or stays up *while quietly losing pages*.

### Log / page loss & retention

**⚠️ Scenario** — A volume's physical pages are destroyed (disk failure on the broker holding that partition).

**⚙️ What Kafka does** — Depends on **replication factor** and the ISR. With `replication.factor=1` there is no follower, so the partition (and its data) is simply gone — unrecoverable. With `replication.factor>=2` and healthy ISR, the controller (KRaft/ZK) runs **leader election** and promotes an in-sync replica to leader; producers/consumers transparently fail over with at most a brief unavailability. Any record acknowledged under `acks=all` with `min.insync.replicas>=2` survives because it existed on >=2 replicas before being acked.

**📚 In the library** — The shelf holding a volume's **master copy** burns. If no **photocopies** exist, that volume is lost forever. If in-sync photocopies exist on other shelves, the **librarian's catalog** elects one photocopy to become the new master; readers keep reading. Because the author waited for photocopies before declaring success, no acked page is lost.

```
RF=1:  [master:gone]                      -> data lost
RF=3:  [master X] [photocopy✓] [photocopy✓] -> promote photocopy -> keep going
```

**🛡️ Mitigation** — `replication.factor>=3`, `acks=all`, `min.insync.replicas=2`, and spread replicas across racks/AZs (`broker.rack`). Disable/avoid unclean leader election (`unclean.leader.election.enable=false`) so a stale out-of-sync copy is never promoted.

**💰 Cost/trade-off** — RF=3 triples storage + replication network; durability bought with 3x disk.

---

**⚠️ Scenario** — Retention recycles old pages a SLOW reader hasn't reached yet — the reader "falls off the log."

**⚙️ What Kafka does** — Retention (`retention.ms` / `retention.bytes`) deletes log segments whose data is older/bigger than the limit, regardless of whether any consumer read them. If a consumer's committed offset points at a record that's been deleted, its next fetch gets an **OFFSET_OUT_OF_RANGE** error. The consumer then resets per **`auto.offset.reset`**: `latest` (skip the gap, jump to the end — silent data loss), `earliest` (jump to the oldest surviving page — reprocessing), or `none` (throw, stop).

**📚 In the library** — The library "keeps books N days then recycles old pages." A slow **reader** is still on page 100 in the **central bookmark ledger**, but pages 1–500 were already pulped. When the reader returns, page 100 doesn't exist. The reading-club's reset policy decides: jump to the newest page (miss everything in between), restart from the oldest surviving page (re-read a lot), or refuse to continue.

```
ledger bookmark: 100
volume now holds: [501 .. 900]   (100 recycled)
-> OffsetOutOfRange -> reset (latest=900 | earliest=501)
```

**🛡️ Mitigation** — Size retention longer than the slowest consumer's worst-case lag; alert on **consumer lag** (lag approaching retention edge). Keep readers fast enough to stay inside the window.

**💰 Cost/trade-off** — Longer retention = more disk held continuously to protect slow readers.

---

**⚠️ Scenario** — Disk full on a shelf (broker's log volume hits 100%).

**⚙️ What Kafka does** — A broker with a full log dir can't write; the affected log dir is marked offline (or the broker shuts down). Partitions whose **leaders** lived there go offline and leadership moves to in-sync replicas elsewhere (if any). The broker drops out of the ISR for partitions it can no longer replicate; if too many replicas fall out, partitions under `min.insync.replicas` reject `acks=all` writes with **NotEnoughReplicas**.

**📚 In the library** — A **shelf** runs out of physical space — no new pages can be filed there. The **librarian's catalog** moves any **master copies** on that shelf to other shelves that still have room. Photocopies that lived on the full shelf go stale and drop out of the in-sync set; if too few in-sync photocopies remain, the author's "wait for photocopies" guarantee can't be met and new pages are refused.

**🛡️ Mitigation** — Monitor disk and alert well before full; enforce retention by size (`retention.bytes`) and `log.retention.check.interval.ms`; spread partitions across brokers/dirs; add capacity or rebalance before saturation.

**💰 Cost/trade-off** — Headroom = paying for disk you don't fill, to avoid outages.

---

**⚠️ Scenario** — Log compaction: keep only the latest page per storyline tag.

**⚙️ What Kafka does** — With `cleanup.policy=compact`, the log cleaner retains the **latest record per key** rather than deleting by time. Older records for the same key are eventually garbage-collected; a record with a `null` value (a **tombstone**) marks that key for deletion and is itself removed after `delete.retention.ms`. The log is no longer a full history — it's the current state per key. Recent (active segment / not-yet-cleaned) records may still show duplicate keys; compaction is a background, eventual process. `cleanup.policy=compact,delete` combines both.

**📚 In the library** — Instead of recycling by age, the library keeps only the **most recent page for each storyline tag** (key). When a new page arrives for tag "ACCOUNT-42," the older "ACCOUNT-42" page is pulped. A page with an empty body is a "remove this tag" note (tombstone). The volume becomes a snapshot of the latest state per tag, not the full saga — perfect for changelog/state topics.

```
key A: 1, key B: 1, key A: 2, key C: 1, key A: 3
-> compacted -> key A:3, key B:1, key C:1   (latest per tag)
```

**🛡️ Mitigation** — Use compaction only for keyed state/changelog topics (every record MUST have a key); set `min.compaction.lag.ms` so consumers can read recent updates before they're collapsed; never assume full event history on a compacted topic.

**💰 Cost/trade-off** — Saves disk by dropping history, but spends background CPU/IO on cleaning — and you lose the audit trail.

### Offset / bookmark problems

The bookmark ledger (`__consumer_offsets`) is the single source of truth for "where did my reading club stop?". Every failure below is a story about that bookmark being missing, wrong, too eager, too lazy, stale, or pointing at a volume that no longer belongs to you.

#### 1. Bookmark missing / never committed

- **⚠️ Scenario** — A brand-new consumer group (or one that crashed before its first commit) has no stored offset for a partition.
- **⚙️ What Kafka does** — There is no entry in `__consumer_offsets` for that `(group, topic, partition)`. The broker cannot tell the consumer where to start, so it falls back to the `auto.offset.reset` policy (`earliest` / `latest` / `none`). With `none`, the consumer throws `NoOffsetForPartitionException` and refuses to start.
- **📚 In the library** — The reading club walks up to the central bookmark ledger and asks "where were we in Volume 3?" The librarian finds no bookmark slip with the club's name on it. Either the club re-reads from page 1, jumps to the newest page, or is told to go home and figure it out itself.
- **🛡️ Mitigation** — Decide `auto.offset.reset` deliberately per use case; for critical pipelines set it to `none` and seed the offset explicitly (`seekToBeginning`/`seekToEnd`/`seek`) so a missing bookmark is a loud failure, not a silent guess.
- **💰 Cost/trade-off** — `none` trades convenience for safety: you must own offset bootstrapping, but you never accidentally reprocess or skip a backlog.

#### 2. `auto.offset.reset = earliest` (re-read everything → duplicates)

- **⚠️ Scenario** — No valid committed offset, so the consumer rewinds to the oldest retained record.
- **⚙️ What Kafka does** — `auto.offset.reset=earliest` resets the position to the **log-start offset** of the partition. Every record still within retention is delivered again — potentially millions of records.
- **📚 In the library** — Bookmark missing, so the club flips back to the very first surviving page of the volume and reads the whole thing front-to-back again. Everything that wasn't yet recycled gets read a second time.

```
[p0]......................[p500 newest]
 ^ start here (earliest)              -> re-reads 0..500 = duplicates downstream
```

- **🛡️ Mitigation** — Make downstream processing idempotent (dedupe by key/offset, upserts keyed on message id); only choose `earliest` when full replay is genuinely desired (e.g. rebuilding a projection).
- **💰 Cost/trade-off** — Burns CPU, network, and downstream write capacity reprocessing data you already handled, in exchange for "no record is ever missed."

#### 3. `auto.offset.reset = latest` (skip to new → missed pages)

- **⚠️ Scenario** — No valid committed offset, so the consumer jumps to the end and ignores the backlog.
- **⚙️ What Kafka does** — `auto.offset.reset=latest` sets the position to the **high-water mark** (next offset to be produced). Records already in the partition are never delivered to this group.
- **📚 In the library** — Bookmark missing, so the club shrugs and opens the volume at the blank page right after the last written one. Every page the author already wrote before the club showed up is skipped forever.

```
[p0 .... p500 written][p501 next]
                        ^ start here (latest) -> pages 0..500 never read = data loss
```

- **🛡️ Mitigation** — Use `latest` only for "live tail" cases where history is irrelevant; for anything stateful, commit an offset before the group first goes live so the reset policy never triggers on real data.
- **💰 Cost/trade-off** — Cheap and fast (no backlog drain) but silently loses unprocessed history — the worst kind of loss because nothing errors.

#### 4. Commit BEFORE processing (at-most-once → loss on crash)

- **⚠️ Scenario** — The consumer advances and commits the offset, *then* processes the record. It crashes between the two.
- **⚙️ What Kafka does** — The new offset is durably written to `__consumer_offsets` before the side effect happens. On restart the consumer resumes *after* the uncommitted-but-already-bookmarked record, so that record is never processed. This is **at-most-once** delivery.
- **📚 In the library** — The reader moves the bookmark slip to page 251 in the ledger, *then* sits down to actually read page 250 — and faints. When the club returns, the ledger says "next is 251," so page 250 is never read by anyone.

```
poll(250) -> commit(251) -> [CRASH] -> process(250)?  NO -> page lost
```

- **🛡️ Mitigation** — Commit **after** processing for at-least-once, or use Kafka transactions (`isolation.level=read_committed`, transactional producer) to tie processing+commit into one atomic unit for effectively-once.
- **💰 Cost/trade-off** — Lowest latency and no duplicates, paid for with permanent data loss on any crash — only acceptable for lossy telemetry/metrics.

#### 5. Commit AFTER processing (at-least-once → duplicates on crash)

- **⚠️ Scenario** — The consumer processes the record, *then* commits the offset. It crashes between the two.
- **⚙️ What Kafka does** — The side effect lands but the offset commit to `__consumer_offsets` never happens. On restart the consumer re-fetches from the last committed offset and reprocesses the record. This is **at-least-once** delivery (the safe default).
- **📚 In the library** — The reader finishes page 250, acts on it, but faints before updating the bookmark slip. The ledger still says "next is 250," so the returning club reads page 250 again — duplicate work.

```
poll(250) -> process(250) -> [CRASH] -> commit(251)?  NO -> re-read 250 = duplicate
```

- **🛡️ Mitigation** — Make consumers idempotent (dedupe on a unique key/offset, upserts, dedup store), or adopt Kafka transactions for exactly-once semantics across read-process-write.
- **💰 Cost/trade-off** — No data loss, but you pay for idempotency machinery (a dedupe store / extra writes) and occasional reprocessing.

#### 6. Stale bookmark pointing past retention

- **⚠️ Scenario** — A consumer was offline long enough that its committed offset now points to records already deleted by retention.
- **⚙️ What Kafka does** — The committed offset is below the partition's current **log-start offset** (records aged out via `retention.ms`/`retention.bytes` or compaction). The fetch is out of range, raising `OffsetOutOfRangeException`, and the consumer again falls back to `auto.offset.reset` — silently jumping to earliest or latest.
- **📚 In the library** — The club's bookmark says "resume at page 90," but the library's "keep N days then recycle old pages" policy already shredded pages 1–150. The bookmark now points into empty space, so the club is bounced to either the oldest surviving page or the newest one.

```
recycled |#####| live window [p151 .... p900]
bookmark -> p90 (gone)  => OffsetOutOfRange => reset (earliest=p151 or latest=p900)
```

- **🛡️ Mitigation** — Monitor consumer lag and offset-vs-log-start; size retention to exceed worst-case downtime; alert before a group's bookmark falls off the cliff. Set `auto.offset.reset=none` if a silent jump is unacceptable.
- **💰 Cost/trade-off** — Longer retention costs disk on every broker; shorter retention saves storage but risks losing slow consumers' backlog.

#### 7. Bookmark for a volume that was reassigned

- **⚠️ Scenario** — A rebalance moves a partition to a different consumer instance, but stale local state or a manually pinned offset still references the old assignment.
- **⚙️ What Kafka does** — During a **rebalance**, partition ownership is reshuffled across the group. Offsets live centrally in `__consumer_offsets` keyed by `(group, topic, partition)` — *not* by instance — so the new owner correctly resumes from the committed offset. Trouble appears when code commits for a partition it no longer owns (commit rejected / `CommitFailedException`), or holds in-memory state tied to the old partition, or uses manual `assign()` and ignores reassignment. Leader changes (master copy moving shelves) are separate and don't move the bookmark.
- **📚 In the library** — A member joins the club, so volumes get re-divided ("re-divide volumes when a club member joins/leaves"). Reader A used to own Volume 3 and keeps trying to update Volume 3's bookmark slip — but the ledger has already handed Volume 3 to Reader B. A's late scribble is refused; B reads correctly from the ledger's bookmark, but any notes A kept on a sticky pad (in-memory state) are now stale.

```
before:  ReaderA -> Vol3 (offset 420 in ledger)
rebalance (new member joins)
after:   ReaderB -> Vol3  (reads ledger offset 420  ✅)
         ReaderA -> commit(Vol3) -> CommitFailedException ❌ (no longer owner)
```

- **🛡️ Mitigation** — Commit before `onPartitionsRevoked` in a `ConsumerRebalanceListener`, flush/clear per-partition local state on revoke, prefer `subscribe()` (managed assignment) over manual `assign()`, and use cooperative-sticky assignment to minimize churn. Always store state keyed by partition, never by instance.
- **💰 Cost/trade-off** — Rebalance handling adds commit/flush latency and code complexity, but prevents duplicate processing and corrupted per-partition state when membership changes.

### Delivery guarantees & ordering

The three guarantees differ only in **when the bookmark moves vs. when the work is done**:

```
at-most-once  : move bookmark FIRST, then process  -> crash = page LOST
at-least-once : process FIRST, then move bookmark   -> crash = page RE-READ (dup)
exactly-once  : process + move bookmark ATOMICALLY  -> no loss, no dup
```

---

- **⚠️ Scenario — At-most-once** — You commit the offset before you finish processing; a crash loses the record.
- **⚙️ What Kafka does** — With `enable.auto.commit=true` the consumer commits offsets on a timer (`auto.commit.interval.ms`) regardless of whether your handler succeeded. If you commit (or auto-commit fires) *before* processing and then die, that offset is already in `__consumer_offsets`, so on restart `auto.offset.reset` / the stored offset skips the unprocessed record. No redelivery → possible **data loss**.
- **📚 In the library** — The reader writes the new page number into the **central bookmark ledger** the instant they pick the page up, *then* starts reading. If they faint mid-page, the club resumes from the bookmark — that page is never read again. Fast, but a page can be silently skipped.
- **🛡️ Mitigation** — Only choose this when loss is acceptable (metrics, logs). To avoid it, commit *after* processing (see at-least-once).
- **💰 Cost/trade-off** — Cheapest & lowest-latency (no reprocessing), but you pay in lost data.

---

- **⚠️ Scenario — At-least-once (the default) & WHY duplicates happen** — You process first, commit after; a crash or rebalance between the two replays records.
- **⚙️ What Kafka does** — Two independent duplicate sources:
  - **Consumer side:** process record → crash *before* the offset commit lands → on restart the committed offset is still the old one, so records are re-delivered. A rebalance (member join/leave, exceeded `max.poll.interval.ms`) also reassigns the partition and replays uncommitted work.
  - **Producer side:** producer sends, broker writes it and the ack is lost on the network; the producer’s `retries` kick in and send the *same* record again → two physical copies in the log.
  Because of both, **the consumer MUST be idempotent** — re-applying the same record yields the same state (e.g. dedupe on a business key / `INSERT ... ON CONFLICT`).
- **📚 In the library** — The reader reads the page, *then* updates the ledger. Die in between and the next reader starts at the old bookmark and reads that page twice. Separately, the **author**, unsure their page reached the **master copy** (the ack got lost), re-sends it — now the volume holds the same page twice. The club must be able to read a duplicate page without double-counting.
- **🛡️ Mitigation** — Manual commit after success (`enable.auto.commit=false`); design idempotent handlers keyed on a stable id; keep `max.poll.interval.ms` comfortably above your processing time so you don’t get kicked mid-batch.
- **💰 Cost/trade-off** — Safe & simple, but you pay CPU/IO reprocessing dups and must build dedupe storage.

---

- **⚠️ Scenario — Exactly-once (EOS): idempotent producer + transactions** — End-to-end no-loss, no-dup across a consume→process→produce pipeline.
- **⚙️ What Kafka does** — Two layers:
  - **Idempotent producer** (`enable.idempotence=true`, implies `acks=all`, `retries>0`, `max.in.flight.requests.per.connection<=5`): the broker tags each batch with a Producer ID + per-partition sequence number and drops re-sends, killing producer-side duplicates **within a partition**.
  - **Transactions** (`transactional.id` set; `initTransactions/beginTransaction/sendOffsetsToTransaction/commitTransaction`): the produced records *and* the consumer offset commit to `__consumer_offsets` are written **atomically**. Downstream consumers set `isolation.level=read_committed` so they never see records from aborted/in-flight transactions.
  - In Kafka Streams this is one switch: `processing.guarantee=exactly_once_v2`.
- **📚 In the library** — The author stamps every page with an ID + running sequence number, so a re-sent page is recognised and discarded by the master copy. And the reader’s “I read these pages” **and** the bookmark-ledger update are sealed in one envelope — either both happen or neither. Other clubs with a `read_committed` rule ignore any pages still inside an unsealed envelope.
- **🛡️ Mitigation** — Use EOS only for the consume-transform-produce path; keep `transactional.id` stable per logical producer instance so zombie producers get fenced.
- **💰 Cost/trade-off** — Highest correctness, highest cost: extra round-trips, transaction markers in the log, and added latency from `read_committed` buffering.

---

- **⚠️ Scenario — Ordering is guaranteed only WITHIN a partition, never across the topic** — Records are ordered per volume, not topic-wide.
- **⚙️ What Kafka does** — Kafka assigns a monotonically increasing **offset** only inside a single partition; a partition is an append-only log. Across partitions there is **no global order** — they’re consumed in parallel by different consumers. Same-key records land in the same partition (`partition = hash(key) % numPartitions`), so per-key order holds; keyless/round-robin records have no cross-partition order at all. (With a non-idempotent producer and `max.in.flight.requests.per.connection>1`, retries can even reorder *within* a partition — idempotence prevents this.)
- **📚 In the library** —

```
Topic "Saga"  (Storyline tag = Key decides the volume)
 ┌Volume 0─────────┐   ┌Volume 1─────────┐
 │ p0  p1  p2  p3  │   │ p0  p1  p2      │   page#=offset, ordered inside a volume
 └─────────────────┘   └─────────────────┘
  ↑ "userA" pages here   ↑ "userB" pages here
  Within a volume: strict page order.  Across volumes: no shared timeline.
```

  All pages tagged `userA` go to Volume 0 in order; `userB` to Volume 1. There is no library-wide page sequence spanning both volumes.
- **🛡️ Mitigation** — Pick a **key** that groups everything needing order (e.g. `accountId`); if you truly need total order, use a single-partition topic (kills parallelism).
- **💰 Cost/trade-off** — More partitions = more throughput/parallelism; one partition = total order but zero scale-out.

---

- **⚠️ Scenario — Adding partitions later re-maps key→partition and breaks per-key ordering / causes temporary dups** — Scaling out a live topic silently scrambles routing for existing keys.
- **⚙️ What Kafka does** — The default partitioner is `hash(key) % numPartitions`. Increasing the partition count changes the divisor, so the *same* key now hashes to a **different** partition. In-flight and historical records for that key still sit in the **old** partition while new ones go to the **new** one → ordering for that key is split across two logs. During the rebalance/repartition window, consumers can also briefly re-read around the moved boundary (temporary duplicates). Kafka **cannot decrease** partition count, so this is one-way.

```
before: hash("userA") % 2 = 0  -> Volume 0   (history lives here)
add a volume (2 -> 3):
after:  hash("userA") % 3 = 2  -> Volume 2   (new pages go here)
=> userA's story is now spread across Volume 0 (old) and Volume 2 (new)
```

- **📚 In the library** — You decide the popular series needs a third **volume**. The hashing rule that maps **storyline tags** to volumes now divides by 3 instead of 2, so `userA`’s tag suddenly points at Volume 2 — but all their earlier pages are still bound into Volume 0. Their story is now split across two volumes with no single timeline, and during the re-division readers may re-skim a few boundary pages.
- **🛡️ Mitigation** — Size partitions generously up front; if you must grow, drain/quiesce affected keys first, or use a sticky/custom partitioner (or key-hashing scheme) that keeps a key’s mapping stable. For a hard cutover, create a *new* topic with the target partition count and migrate, rather than expanding in place.
- **💰 Cost/trade-off** — Over-provisioning partitions wastes broker file handles/memory and slows rebalances; under-provisioning forces this disruptive, order-breaking expansion later.

### Scaling up & shrinking resources mid-process

> **Rule of thumb:** Kafka tolerates *adding* capacity gracefully but *shrinking* it is where data and ordering get hurt. Every change below triggers a **rebalance** (re-divide volumes when a club member joins/leaves) and/or a **partition reassignment** (physically copy a volume's pages to another shelf).

---

**⚠️ Scenario** — Adding shelves (brokers): scaling the library out.
- **⚙️ What Kafka does** — A new broker joins the cluster but holds **no partitions** until you run a **partition reassignment** (`kafka-reassign-partitions.sh`). The controller (KRaft) moves replicas onto the new broker; while moving, the new follower copies the full log from the leader and stays **out of ISR** until caught up. This generates heavy inter-broker **replication traffic**. Leadership only shifts after the new replica is in-sync.
- **📚 In the library** — You bolt a new empty shelf into the room. It's useless until the librarian's catalog reassigns some master copies and photocopies onto it. To make a photocopy, the new shelf must duplicate every existing page from the master — that's a lot of photocopier traffic. The new photocopy doesn't count as an "in-sync photocopy" until it has copied all the way up to the latest page.
- **🛡️ Mitigation** — Throttle the move with `--throttle` so replication doesn't starve live producers/consumers; reassign during low traffic; move a few partitions at a time.
- **💰 Cost/trade-off** — You pay network + disk I/O up front (and a slower cluster during the copy) to gain durability and headroom later.

---

**⚠️ Scenario** — Adding volumes (partitions): more parallelism on an existing topic.
- **⚙️ What Kafka does** — `kafka-topics.sh --alter --partitions N` only ever *increases* the count. The key→partition mapping is `hash(key) % numPartitions`, so changing `N` **remaps existing keys**: a storyline that used to land in volume 2 may now hash into volume 5. Past records are **not** moved — only new records follow the new mapping. Ordering is guaranteed *within a partition*, so a key split across old+new volumes loses its global order.
- **📚 In the library** — You split the series into more volumes for more readers to work in parallel. But the storyline-tag rule (`hash(tag) % volumes`) now points some tags at different volumes. Old pages stay where they were written; new pages with the same tag may go to a brand-new volume — so one storyline is now scattered across two volumes and can be read out of order.
- **🛡️ Mitigation** — Size partitions correctly **up front**; if you must add, accept the remap or republish into a fresh topic with the final partition count. Never rely on cross-partition ordering.
- **💰 Cost/trade-off** — More partitions = more parallelism, but more open file handles, more controller metadata, and broken per-key order across the boundary.

```
hash("alice") % 4 = 2   ->  volume 2   (old pages)
add partitions: 4 -> 8
hash("alice") % 8 = 5   ->  volume 5   (new pages)   ⚠ same tag, two volumes
```

---

**⚠️ Scenario** — Removing readers: scaling the reading club down (consumer leaves mid-read).
- **⚙️ What Kafka does** — When a consumer leaves (graceful `close()` or a missed heartbeat past `session.timeout.ms` / `max.poll.interval.ms`), the group coordinator triggers a **rebalance** and reassigns that consumer's partitions to surviving members. The new owner resumes from the **committed offset** in `__consumer_offsets`. Anything processed but **not yet committed** is re-read → **at-least-once duplicates**.
- **📚 In the library** — A club member walks out. The club re-divides that person's volumes among the remaining readers. A new reader picks up at the last bookmark saved in the central bookmark ledger — so any pages the departed reader finished but never bookmarked get read again by someone else.
- **🛡️ Mitigation** — Commit offsets frequently / after processing; make processing **idempotent**; use cooperative-sticky assignor (`partition.assignment.strategy`) so only the moved partitions pause, not all of them.
- **💰 Cost/trade-off** — Fewer readers = lower throughput and a brief reprocessing blip, but no data loss.

---

**⚠️ Scenario** — The "stop-the-world" pause during a rebalance.
- **⚙️ What Kafka does** — With the classic **eager** rebalance protocol, *every* consumer in the group revokes **all** its partitions, then the coordinator reassigns and everyone resumes — so **the whole group stops consuming** for the duration. The **cooperative** (incremental) protocol revokes only the partitions that actually change owners, shrinking the freeze.
- **📚 In the library** — Eager mode: the instant anyone joins or leaves, *all* readers slap their books shut, the club re-deals every volume from scratch, and only then does everyone reopen — nobody reads during the re-deal. Cooperative mode: only the few volumes changing hands are set down; everyone else keeps reading.
- **🛡️ Mitigation** — Use `cooperative-sticky`; enable **static membership** (`group.instance.id`) so a quick restart doesn't trigger a reassignment at all; keep `max.poll.interval.ms` generous so slow processing isn't mistaken for a dead member.
- **💰 Cost/trade-off** — A short latency/throughput stall (seconds) is the price of elastic, self-healing consumer groups.

```
EAGER:        join/leave -> ALL stop -> redeal ALL -> ALL resume   (whole club frozen)
COOPERATIVE:  join/leave -> only moved volumes pause -> rest keep reading
```

---

**⚠️ Scenario** — Removing a shelf (broker) WHILE reading is in progress.
- **⚙️ What Kafka does** — If you stop a broker that holds **partition leaders**, the controller runs **leader election**: an in-sync follower (a member of the **ISR**) is promoted to leader. Consumers/producers transparently follow the metadata to the new leader after a brief retry. **No data loss — as long as a live in-sync replica exists.** Consumers don't lose their place; bookmarks live in `__consumer_offsets`, not on the dying broker. But if you remove a broker without first **reassigning its partitions away**, those partitions become **under-replicated** (fewer photocopies than the replication factor).
- **📚 In the library** — You yank a shelf out while readers are mid-page. The librarian's catalog instantly promotes an in-sync photocopy on another shelf to be the new master copy. Readers glance at the catalog, walk to the new shelf, and continue from their bookmark — barely a hiccup. But if no other shelf had an up-to-date photocopy of a volume that lived only on the yanked shelf, that volume is **gone or stuck**.
- **🛡️ Mitigation** — **Drain first:** reassign the broker's partitions onto other brokers and wait for ISR to be full *before* shutting it down; use **controlled shutdown** (`controlled.shutdown.enable=true`) so the broker hands off leadership cleanly; verify `UnderReplicatedPartitions == 0` before and after.
- **💰 Cost/trade-off** — Draining costs reassignment time + replication traffic; skipping it risks under-replication or a stuck partition.

```
shelf-3 leaves (held master of volume V)
  ISR for V = {shelf-3 master, shelf-1 photocopy*}   (* in-sync)
  catalog elects shelf-1 photocopy -> new master
  readers re-point to shelf-1, resume from bookmark   ✅ no loss
```

---

**⚠️ Scenario** — Shrinking below the replication factor.
- **⚙️ What Kafka does** — Replication factor is **fixed per topic** and does **not** auto-shrink. If surviving brokers < replication factor, partitions can't keep enough replicas → **under-replicated partitions**, and ISR shrinks. Once ISR drops below `min.insync.replicas`, any producer using `acks=all` gets **`NotEnoughReplicasException`** and writes are **rejected** (the topic effectively goes read-only for safe producers). If you lose the **last** in-sync replica and have `unclean.leader.election.enable=false`, the partition goes **offline** (no leader) until a replica returns; setting it `true` promotes a stale follower → **data loss**.
- **📚 In the library** — The series demands, say, 3 copies of each volume (1 master + 2 photocopies), and the author insists on "all in-sync photocopies confirm before I declare a page written" (`acks=all`, `min.insync.replicas=2`). Pull shelves until fewer than 2 copies survive: the author is now refused — "not enough photocopies to safely write," so no new pages get added. Lose the very last good copy and either the volume goes dark (no master, with clean election) or the librarian promotes a stale photocopy and silently loses the newest pages.
- **🛡️ Mitigation** — Never run fewer brokers than the replication factor; keep `min.insync.replicas = RF − 1` for headroom; keep `unclean.leader.election.enable=false` to favor consistency over availability; **reassign replicas down** deliberately instead of yanking shelves.
- **💰 Cost/trade-off** — Holding `RF` copies costs `RF×` storage and write amplification, but it's exactly what protects you from a yanked shelf — shrink past it and you trade availability (writes rejected) or durability (lost pages) for the saved disk.

```
RF=3, min.insync.replicas=2, acks=all
brokers: 3 -> 2   ISR={master, photocopy}  ✅ still writable (2 ≥ 2)
brokers: 2 -> 1   ISR={master}             ❌ acks=all rejected: NotEnoughReplicas
last copy lost + unclean=false             ⛔ partition OFFLINE (no master)
last copy lost + unclean=true              ☠ stale photocopy promoted -> data loss
```

### Cost & resource trade-offs

Every Kafka knob that buys you speed, safety, or replay-ability is also spending something: CPU, RAM, disk, network, or money. This section walks the classic trade-offs. The rule of thumb: **there is no free durability and no free parallelism — you pay for both in resources.**

---

- **⚠️ Scenario** — More partitions sounds like "more parallelism, always better" — but each partition has a fixed overhead, so over-partitioning quietly taxes the whole cluster.
- **⚙️ What Kafka does** — Consumer parallelism inside one group is capped at the partition count: a group can have **at most one consumer per partition** (extras sit idle). But each partition is a real on-disk log with open file handles, an index/timeindex file, a leader + follower set to replicate, and per-partition memory in the producer buffer (`batch.size` is *per partition*). Brokers track every partition in the controller metadata, so leader election and rebalances scale with total partition count. Rough guidance: keep partitions per broker in the low thousands; tens of thousands cluster-wide slows controller failover and lengthens rebalances.
- **📚 In the library** — Splitting a series into more volumes lets more readers in the club read at once (one reader per volume). But every extra volume is a physical book that needs its own master copy, its own photocopies on other shelves, its own slot in the bookmark ledger, and a line in the librarian's catalog. Order 50,000 thin volumes and the librarian spends all day re-cataloging and re-dividing volumes whenever a club member joins or leaves — and you can't even use the parallelism if your club only has 6 readers.
- **🛡️ Mitigation** — Size partitions to *target throughput / per-partition throughput*, not to a guess. A common heuristic: `partitions = max(target_MBps / producer_MBps, target_MBps / consumer_MBps)`. Leave headroom for future consumers but don't 10x "just in case" — you can add partitions later (note: adding them changes key→partition mapping and breaks ordering for in-flight keys).
- **💰 Cost/trade-off** — Parallelism is bounded by partitions; idle consumers above the partition count are wasted compute, and excess partitions waste broker memory/handles and slow failover.

```
Group A (3 consumers)        Topic: 6 volumes
 C1 ── reads V0, V1          [V0][V1][V2][V3][V4][V5]
 C2 ── reads V2, V3
 C3 ── reads V4, V5
Add C4..C7? → idle (no volume left to own)
```

---

- **⚠️ Scenario** — Replication factor (RF) protects you from broker loss, but RF=3 literally stores three full copies of every byte.
- **⚙️ What Kafka does** — `replication.factor=N` keeps N copies of each partition across N brokers: 1 leader + (N−1) followers. Producers/consumers only talk to the leader; followers fetch to stay in the **ISR** (in-sync replicas). On leader failure, the controller elects a new leader from the ISR — so durability and availability scale with RF, but so does disk and replication network traffic. RF=3 ≈ **3× the raw storage** of the data, plus 2× the write data crossing the inter-broker network.
- **📚 In the library** — Every volume has a master copy plus photocopies on other shelves; the in-sync photocopies are the ones kept current. If a shelf burns down, the librarian promotes an in-sync photocopy to master and the club barely notices. But three copies of a series means three shelves' worth of paper for the same content, and every page the author writes must be photocopied twice before it's safe.
- **🛡️ Mitigation** — RF=3 is the standard production default (survives 1 broker loss with `min.insync.replicas=2` still writable). Use RF=2 only for low-value/replayable data, RF=1 only for throwaway/dev. Don't crank RF=5 unless you genuinely need to survive 2 simultaneous broker losses.
- **💰 Cost/trade-off** — Each +1 to RF adds 1× full storage and inter-broker network for the same data — you're buying fewer-9s-of-data-loss with disk.

---

- **⚠️ Scenario** — Long retention is great for replay and late consumers, but Kafka keeps every retained byte on disk (times RF).
- **⚙️ What Kafka does** — `retention.ms` (or `retention.bytes`) controls how long/large the log stays before old segments are deleted (or compacted, with `cleanup.policy=compact`). Data within the window is fully replayable: a consumer can reset with `auto.offset.reset=earliest` or seek to any offset still on disk. Steady-state disk ≈ `ingest_rate × retention × replication_factor`. Double the retention → double the disk. Past the window, those offsets are gone and `earliest` jumps forward to the oldest surviving page.
- **📚 In the library** — Retention is "keep books N days then recycle old pages." A long retention lets a reader who joins late, or a club that wants to re-read from the start, page back through old history. But every kept page is paper on the shelf — and because of photocopies, it's paper times the copy count. Recycle too aggressively and a slow reader arrives to find the early pages already pulped; their bookmark in the ledger now points before the oldest page, so they're force-skipped to the new oldest page.
- **🛡️ Mitigation** — Set retention to the real replay/recovery need (e.g. 7 days for ops replay), not "forever by default." For unbounded history use compacted topics (keep latest value per key) or tier to cheap object storage (Tiered Storage / KIP-405, MSK/Confluent tiered) so hot disk stays small.
- **💰 Cost/trade-off** — Replay-ability and tolerance for slow/late consumers cost disk linearly in `retention × RF`; shrink retention and you save disk but lose history.

---

- **⚠️ Scenario** — `acks` decides how many copies must confirm a write before the producer calls it "done" — stronger acks mean higher latency.
- **⚙️ What Kafka does** — `acks=0`: fire-and-forget, no broker confirmation (fastest, can silently lose data). `acks=1`: leader writes to its log and confirms — but if the leader dies before a follower fetches, that write is lost. `acks=all` (a.k.a. `-1`): leader waits until all **ISR** members have the record; combined with `min.insync.replicas=2`, the write isn't acknowledged unless at least 2 copies hold it, and if too few replicas are in sync the producer gets `NotEnoughReplicas` and must retry. Stronger acks add a network round-trip (or several) to every produce.
- **📚 In the library** — This is "author waits for photocopies before declaring success." `acks=0`: the author writes a page and walks away without looking — fast, but if the page blows off the desk, no one knows. `acks=1`: the author waits only for the master copy to be filed. `acks=all`: the author waits until every in-sync photocopy also has the page — slowest pen-down-to-confirmed time, but if any one shelf is lost the page survives on another.
- **🛡️ Mitigation** — For data you can't lose: `acks=all` + `min.insync.replicas=2` + RF=3 + `enable.idempotence=true` (which also guards against duplicate-on-retry). For high-volume metrics/logs where a few drops are fine, `acks=1` (or even `0`) to cut latency. Tune `linger.ms`/`batch.size` so batching hides some of the round-trip cost.
- **💰 Cost/trade-off** — `acks=all` buys near-zero data loss at the price of per-message latency and throughput; `acks=0/1` buys speed by risking lost writes.

```
acks=0   author → (no wait)              fastest │ least safe
acks=1   author → master filed ──ack     medium  │ loses data if leader dies pre-copy
acks=all author → master + ISR copies ──ack  slowest │ safest
```

---

- **⚠️ Scenario** — One skewed key floods a single partition while the rest sit nearly empty — a HOT volume that wastes the cluster's capacity.
- **⚙️ What Kafka does** — The default partitioner sends a record to `hash(key) % numPartitions`, guaranteeing **same key → same partition → ordering**. But if one key dominates the traffic (e.g. a single huge tenant), that partition's leader broker carries disproportionate write/read load, its log grows faster, and one consumer in the group is pinned to it as a bottleneck — while the other partitions' consumers idle. Total cluster CPU/disk looks underused even as that one partition saturates.
- **📚 In the library** — The storyline tag (key) hashes to pick the volume; same tag always lands in the same volume so its pages stay ordered. But if 80% of pages carry one popular tag, one volume balloons, the shelf holding its master copy runs hot, and the single reader assigned to that volume falls behind — while readers on the other near-empty volumes twiddle their thumbs. You bought 6 volumes of parallelism but get the throughput of barely 1.
- **🛡️ Mitigation** — If strict per-key ordering isn't required, salt the key (`key + "#" + bucket`) or use a custom/round-robin partitioner to spread the hot key across N sub-partitions. If ordering *is* required, isolate the whale into its own topic/partition with dedicated capacity, or split that tenant's stream by a secondary attribute. Monitor per-partition bytes-in to detect skew early.
- **💰 Cost/trade-off** — Key skew wastes paid-for parallelism: you provision (and pay for) N partitions/consumers but effectively get the throughput of the one hot partition.

```
       V0   V1   V2   V3   V4   V5
load: ████  ▏    ▏    ▏    ▏    ▏     ← one tag floods V0
       hot  idle idle idle idle idle
```

---

- **⚠️ Scenario** — Self-host Kafka on your own boxes vs. pay a managed service (Confluent Cloud / Amazon MSK) — cheaper sticker price vs. cheaper total cost.
- **⚙️ What Kafka does** — Kafka itself is the same engine either way: brokers, partitions, replication, the KRaft/ZooKeeper controller doing leader election and failover, `__consumer_offsets` tracking group progress. **Self-hosted**: you run and patch the brokers and the controller (KRaft/ZK), size disks, handle rebalances/upgrades, build monitoring, and carry the on-call pager. **Managed** (Confluent/MSK): the provider operates brokers, controller, patching, and often tiered storage; you pay per broker-hour, per GB ingress/egress/stored, and sometimes per partition — trading ops labor for a metered bill.
- **📚 In the library** — Self-hosting is owning the building: you buy the shelves, hire the librarian (the catalog/election service), restock paper, and get the call when a shelf collapses at 3am. Managed is renting a serviced library wing: the provider supplies shelves, runs the librarian, refills paper, and handles photocopier jams — you just bring authors and reading clubs and pay per shelf-hour and per page shipped in/out.
- **🛡️ Mitigation** — Choose by where your scarce resource is: if you have strong platform/SRE staff and steady high volume, self-hosting (or a control plane like Strimzi on k8s) is cheaper at scale. If engineer time is the bottleneck or load is spiky/early-stage, managed removes the ops burden. Watch managed **egress/cross-AZ network** charges — they often dominate the bill more than storage.
- **💰 Cost/trade-off** — Self-hosted = lower cloud bill but real engineering/on-call cost; managed = higher metered spend (broker-hours + per-GB network/storage) but near-zero ops labor.

### Multiple simultaneous failures (probabilities with multiples)

Single failures are routine — Kafka is built to shrug them off. The danger is **correlated, simultaneous** failures, where the math of "rare × rare" stops protecting you because the failures aren't independent (same rack, same switch, same deploy, same bad config).

---

- **⚠️ Scenario** — Master AND all photocopies for a partition go down at once (total volume loss).
- **⚙️ What Kafka does** — If the leader and *every* in-sync replica (ISR) for a partition are offline, that partition becomes **unavailable**. Producers get `NOT_ENOUGH_REPLICAS` / `LEADER_NOT_AVAILABLE`; consumers stall on it. Kafka will NOT promote a stale follower unless `unclean.leader.election.enable=true` — and that promotion **loses** any records the dead leader had that the follower never replicated. Other partitions keep serving; only this one is dark.
- **📚 In the library** — A book series has its master volume on one shelf and photocopies on others. If the master shelf and *all* shelves holding photocopies of that volume are pulled at the same moment, no one can read or write *that volume*. Other series and other volumes are fine. The catalog can only crown a new master from a surviving photocopy — and a stale photocopy is missing the latest pages.
- **🛡️ Mitigation** — Spread replicas across failure domains with **rack awareness** (`broker.rack` + `replica.selector`), so RF=3 means 3 *different* racks/AZs, not 3 servers on one switch. Keep `unclean.leader.election.enable=false` to refuse lossy promotion.
- **💰 Cost/trade-off** — Multi-AZ replication triples storage + inter-AZ network egress; the alternative is a permanent data hole.

---

- **⚠️ Scenario** — Several readers crash at once (rebalance + temporary processing gap).
- **⚙️ What Kafka does** — Each dead consumer misses its heartbeat; after `session.timeout.ms` the group coordinator declares it dead and triggers a **rebalance** to reassign its partitions to survivors. During the rebalance, with the classic eager protocol, **all** members pause consuming ("stop-the-world") until partitions are reassigned. Progress resumes from the last committed offset in `__consumer_offsets`.
- **📚 In the library** — Several members of a reading club collapse simultaneously. The club must **re-divide the volumes** among the remaining readers. While they re-divide, *everyone* stops reading for a moment. Each survivor opens their newly-assigned volume at the page recorded in the central bookmark ledger and reads on.
- **🛡️ Mitigation** — Use **cooperative-sticky** assignor (incremental rebalance — only moved partitions pause) and **static membership** (`group.instance.id`) so a quick restart doesn't trigger reassignment at all. Tune `session.timeout.ms` / `heartbeat.interval.ms` sensibly.
- **💰 Cost/trade-off** — Longer session timeouts mean fewer needless rebalances but slower detection of genuinely dead consumers (lag spikes).

---

- **⚠️ Scenario** — N shelves (brokers) down vs replication factor — does the cluster survive?
- **⚙️ What Kafka does** — A partition stays available as long as ≥1 replica is alive **and** `min.insync.replicas` is satisfiable for writes. With **RF=3** and `min.insync.replicas=2`, the partition survives **2 broker losses for reads/leadership**, but `acks=all` **writes** start failing once fewer than 2 replicas remain in-sync (you keep durability but lose write availability for that partition).

```
RF=3, min.insync.replicas=2, acks=all
brokers down: 0 → reads OK, writes OK
brokers down: 1 → reads OK, writes OK (2 in ISR)
brokers down: 2 → reads OK, writes REJECTED (only 1 in ISR)
brokers down: 3 → partition OFFLINE (total volume loss)
```

- **📚 In the library** — One master + two photocopies (RF=3). Lose 2 shelves and a surviving copy can still be read and crowned master. But the author's rule "wait for 2 copies before declaring success" can no longer be met, so **new pages are refused** even though old pages are still readable. Lose all 3 shelves and the volume is gone.
- **🛡️ Mitigation** — Size RF and `min.insync.replicas` to your tolerance: RF=3/`min.insync=2` tolerates 1 failure with full writes and 2 with reads. Spread replicas across racks so "N down" can't be one rack.
- **💰 Cost/trade-off** — Higher RF = more disk, more replication bandwidth, more brokers to pay for — bought to push the "all copies gone" probability toward zero.

---

- **⚠️ Scenario** — Network split / "split brain" (cluster partitioned into two halves).
- **⚙️ What Kafka does** — Kafka avoids true split-brain by requiring a **single source of truth** for metadata: only the side holding the controller **quorum majority** (KRaft) can elect leaders. The minority side **cannot** elect new leaders — its partitions go read-only/offline rather than diverging. Replicas cut off from the leader fall out of ISR; if `acks=all` they can't be acknowledged, so no conflicting "winner" timeline forms.

```
        ┌── network split ──┐
 Side A (majority)     Side B (minority)
 controller quorum OK  no quorum
 elects leaders        cannot elect → partitions frozen
 keeps serving         clients time out / fail over
```

- **📚 In the library** — A wall splits the library in two. Only the half that holds the **majority of the catalog committee** can appoint new master volumes; the other half is frozen — it may neither crown masters nor accept new pages, so the two halves never write conflicting page-15s. When the wall comes down, the frozen half re-syncs from the side that kept the truth.
- **🛡️ Mitigation** — Run an **odd** controller count (3 or 5) so a majority always exists on exactly one side; place voters across AZs but keep the quorum reachable. Keep `unclean.leader.election=false`.
- **💰 Cost/trade-off** — Choosing consistency means the minority side is *unavailable* during the split (CP over AP) — you sacrifice availability to never merge-conflict data.

---

- **⚠️ Scenario** — Cascading rebalances (one rebalance triggers the next, in a storm).
- **⚙️ What Kafka does** — A flapping consumer (slow processing exceeds `max.poll.interval.ms`, GC pauses, or restart loops) repeatedly leaves and rejoins. Each join/leave fires a rebalance; each rebalance pauses consumption, which makes processing slower, which trips more timeouts — a feedback loop where the group spends more time rebalancing than consuming. Lag climbs unboundedly.
- **📚 In the library** — A reader keeps stepping out and back in. Every time, the club **re-divides the volumes** and everyone stops reading. The stoppages make the backlog grow, the reader gets even more overwhelmed and steps out again — the club thrashes, re-dividing forever and reading almost nothing.
- **🛡️ Mitigation** — Raise `max.poll.interval.ms` and **lower `max.poll.records`** so a poll batch finishes in time; use **cooperative-sticky** + **static membership** to stop reassigning the same reader; fix the slow downstream work. Watch the `rebalance-rate` metric.
- **💰 Cost/trade-off** — Bigger poll intervals / smaller batches add a little latency and throughput overhead, traded for a group that actually makes forward progress.

---

- **⚠️ Scenario** — Quorum loss in the librarian's catalog (KRaft controller quorum lost) — leadership changes stop.
- **⚙️ What Kafka does** — KRaft runs a **Raft quorum** of controllers; with 3 controllers it tolerates 1 loss, with 5 it tolerates 2. Lose the **majority** and the metadata log can't commit: **no new leader elections, no ISR changes, no topic/partition admin**. Existing leaders keep serving their partitions (the data plane runs on cached metadata), but any partition that *needs* a new leader after this point stays leaderless until quorum returns.

```
5 controllers → survive 2 down (3 ≥ majority)
                lose 3 → NO majority → control plane frozen
data plane: current leaders still serve; failover is paused
```

- **📚 In the library** — The catalog committee that appoints masters loses its majority. Volumes that already have a master keep being read and written — but the moment any master shelf fails, **no replacement master can be crowned**, because the committee can't agree on anything. The library limps on existing masters until the committee regains a majority.
- **🛡️ Mitigation** — Run **3 or 5** dedicated controllers across separate AZs (never 2 or 4 — even counts buy no extra tolerance). Monitor quorum health and don't co-locate all voters on one rack/host; restore a downed controller fast.
- **💰 Cost/trade-off** — Dedicated, spread-out controllers cost extra nodes and cross-AZ traffic — the price of keeping failover alive when shelves die.

