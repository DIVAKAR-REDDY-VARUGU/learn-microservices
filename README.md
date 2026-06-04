# 🧩 Learn Microservices — NestJS + Spring Boot

A hands-on learning journal where I build microservices **twice — once in NestJS, once in Spring Boot** — to truly understand the patterns (not just one framework). The end goal is a small **polyglot system** that demonstrates the hard parts: gRPC, Kafka, Redis pub/sub, and the **Saga pattern** for transactions across services.

> This README *is* the course. Every lesson is logged here: the concept, **my doubts**, the explanations, **my mistakes**, and the **optimized approaches** — so it doubles as my study notes and a portfolio of how I learned.

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
| 5 | Async / pub-sub: Kafka + Redis | ⚪ Planned |
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
