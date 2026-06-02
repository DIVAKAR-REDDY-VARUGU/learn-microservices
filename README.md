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

_Journal started during Module 0. Pushed periodically by me (DIVAKAR-REDDY-VARUGU)._
