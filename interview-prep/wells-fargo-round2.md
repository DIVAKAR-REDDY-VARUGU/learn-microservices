# 🎯 Wells Fargo — Round 2 Prep (Technical + Managerial)

> **Format of every topic:** ❓ *the question* → 🎯 *step-by-step* → 🖼️ *visual* → 🗣️ *say this* (the 30–60s spoken answer).
> **Lens:** Java/**Spring**-primary (WF is a Java shop), with a **Nest bridge** so it maps to what you already do.

---

## ⚠️ Round-1 stumbles → the correct answer (read first)
1. **"What is Spring / how does the flow happen?"** — you answered with the **NestJS** pipeline (middleware → guard → …). WF wanted the **Spring MVC** flow: *Filter chain → DispatcherServlet → HandlerMapping → HandlerInterceptor → Controller → Service → Repository → HttpMessageConverter → response.* (Full detail in §6.)
2. When unsure, **say the mental model first, then name the components** — don't free-associate.

---

# PART 1 — DISTRIBUTED SYSTEMS & COMMUNICATION

## §1 — Why gRPC / TCP between microservices, not HTTP?
❓ *"Why gRPC/TCP for service-to-service, why not plain HTTP?"*

🎯 **The point:** public/browser traffic = REST over HTTP/JSON. **Internal** service-to-service = you control both ends, so you optimize for speed + a strict contract → **gRPC**.

| | REST (HTTP/1.1 + JSON) | gRPC (HTTP/2 + Protobuf) |
|---|---|---|
| Payload | Text JSON (bigger, slower parse) | **Binary Protobuf** (small, fast) |
| Transport | HTTP/1.1 — one req/response, head-of-line blocking | **HTTP/2** — multiplexed streams on one connection |
| Contract | Loose (docs/Swagger) | **Strict `.proto`** → code-gen, type-safe, versioned |
| Streaming | No (req→res) | **Bi-directional streaming** |
| Best for | Public APIs, browsers | **Internal high-throughput calls** |

🖼️
```
Browser ──REST/JSON──▶ [API Gateway] ──gRPC/Protobuf (HTTP/2)──▶ internal services
   (human-readable, universal)            (fast, typed, multiplexed)
```

🗣️ **Say this:** *"For public or browser-facing endpoints I use REST over HTTP — it's universal and human-readable. But between internal services I control both ends, so I use gRPC: it's binary Protobuf over HTTP/2, which means smaller payloads, multiplexed streams instead of head-of-line blocking, native streaming, and a strict typed contract from the .proto file. So it's faster and safer for high-volume internal calls. HTTP/REST is still the right default at the edge."* *(Nest bridge: NestJS's TCP transport is the same idea — a lightweight binary channel between services instead of HTTP.)*

---

## §2 — "Systems fail" — designing for failure (resilience) ⭐
❓ *"How do microservices communicate reliably?" / "Service A calls Service B and B is down — what do you do?"*

🎯 **Never assume the network or a dependency is up.** Layer these patterns:
1. **Timeout** — never wait forever; fail fast (e.g., 1–2s).
2. **Retry with exponential backoff + jitter** — only for *transient* failures and *idempotent* calls (retry after 100ms, 200ms, 400ms… + randomness to avoid a thundering herd).
3. **Circuit breaker** — stop hammering a sick service. **Closed → Open → Half-Open.**
4. **Fallback / graceful degradation** — return a cached value, a default, or a "try later" instead of crashing.
5. **Bulkhead** — isolate thread/connection pools so one slow dependency can't sink the whole service.
6. **Async decoupling** — if it can be eventual, push to a **queue** and let B process when it's back.
7. **Idempotency + health checks + DLQ** underpin all of it.

🖼️ Circuit breaker states:
```
        failures < threshold
   ┌──────────────────────────┐
   ▼                          │
[CLOSED] ──failures exceed──▶ [OPEN] ──after cooldown──▶ [HALF-OPEN]
  (calls pass)              (fail fast,            (let a few test calls
                            return fallback)        through)
   ▲                                                   │
   └──────────────── test calls succeed ───────────────┘
```

🗣️ **Say this:** *"I assume every call can fail. So: a tight timeout so I fail fast, bounded retries with exponential backoff and jitter for transient errors on idempotent calls, and a circuit breaker — Resilience4j — that trips to Open after a failure threshold and fails fast with a fallback, then goes Half-Open to test recovery. The fallback might be a cached response or a degraded feature. If the work can be eventual, I don't call synchronously at all — I publish to a queue so B processes when it recovers. And I isolate pools with bulkheads so one slow dependency can't exhaust all my threads. The goal is: a downstream failure degrades one feature, it doesn't cascade."*

---

## §3 — Kafka deep-dive (flow, consumer failure, debugging)
❓ *"Explain the Kafka flow in detail. What if the consumer fails? How do you debug Kafka?"*

> 🧑‍🏫 **New to Kafka? Learn it from absolute zero first:** [`kafka-from-scratch.md`](kafka-from-scratch.md) — every term explained with a restaurant analogy. This §3 is the *refresher*; that file is the *lesson*.

🎯 **The flow:**
1. **Producer** sends a message to a **topic**. A topic is split into **partitions**. The **key** decides the partition (`hash(key) % partitions`) → same key = same partition = **ordering guaranteed per partition**.
2. Message is **appended** to the partition log and gets a monotonic **offset**.
3. **Broker** stores partitions; each partition has a **leader** + **follower replicas**. `acks=all` waits for the **ISR** (in-sync replicas) → durability.
4. **Consumer group**: each partition is read by **exactly one consumer** in the group (parallelism = #partitions). Consumers **commit offsets** to mark progress.
5. Add/remove a consumer → **rebalance** (partitions reassigned).

🖼️
```
Producer ─key→partition─▶  TOPIC "orders"
                          ┌─ Partition 0 ── off0,off1,off2… ─┐
                          ├─ Partition 1 ── off0,off1…       │   replicated across brokers
                          └─ Partition 2 ── off0…            ┘
                                   │  (pull)
                       Consumer Group "billing"
                       C1→P0   C2→P1   C3→P2   (1 consumer per partition)
                                   │ commits offset after processing
```

🎯 **Consumer fails mid-processing:** if the **offset wasn't committed**, on restart/rebalance Kafka redelivers from the **last committed offset** → **at-least-once** delivery → so your consumer **must be idempotent**. A repeatedly-failing message ("poison") goes to a **retry topic** then a **dead-letter topic (DLQ)** so it doesn't block the partition.

🎯 **Debugging Kafka:**
- **Consumer lag** = `latest offset − committed offset`. Growing lag = consumer too slow / stuck. → `kafka-consumer-groups.sh --describe --group billing`.
- Check **rebalance storms** (consumers flapping), slow processing, a poison message blocking a partition, or partition skew.
- Monitor lag dashboards (Burrow/Prometheus); inspect consumer logs; replay from an offset if needed.

🗣️ **Say this:** *"A producer writes to a topic that's partitioned; the key picks the partition so ordering holds per partition, and each message gets an offset. Brokers replicate partitions and acks=all waits for the in-sync replicas. Consumers in a group each own some partitions and commit offsets as they progress. If a consumer crashes before committing, Kafka redelivers from the last committed offset — that's at-least-once, so I make processing idempotent. Poison messages go to a retry topic then a DLQ. To debug I look at consumer lag with kafka-consumer-groups --describe; growing lag means the consumer is slow or stuck, and I check for rebalances or a blocking poison message."*

---

## §4 — Kafka / RabbitMQ vs Redis Pub/Sub
❓ *"How is a message queue (Kafka/RabbitMQ) different from Redis Pub/Sub?"*

🎯
| | Kafka | RabbitMQ | Redis Pub/Sub |
|---|---|---|---|
| Persistence | **Durable log**, retained | Durable queues | **None** — in-memory, fire-and-forget |
| Offline subscriber | Reads later (replay) | Message waits in queue | **Message is LOST** |
| Replay | ✅ by offset | ❌ (once consumed) | ❌ |
| Model | Pull, consumer groups, ordered per partition | Push, exchanges/routing, ack/DLQ | Broadcast to live subscribers |
| Use | Event streaming, high throughput, audit | Task queues, complex routing | Instant fan-out where loss is OK |

🗣️ **Say this:** *"Redis Pub/Sub is fire-and-forget — if no subscriber is connected, the message is gone, and there's no replay. Kafka is a durable, replayable log with consumer groups and ordering per partition, so it's right for event streaming and anything I might need to reprocess. RabbitMQ is a smart broker with queues, routing and acks — great for task distribution. Rule of thumb: Redis Pub/Sub for instant broadcast where loss is acceptable; Kafka/RabbitMQ when I need delivery guarantees and durability."* *(Note: Redis **Streams** is the durable option — different from Pub/Sub.)*

---

## §5 — RabbitMQ: consumer crashes before processing
❓ *"What happens if your RabbitMQ consumer crashes before processing a message?"*

🎯 Depends on **acknowledgement mode**:
- **Manual ack (correct way):** broker marks the message **unacked** while the consumer holds it. If the consumer dies before `ack`, the channel closes, the message is **requeued** and **redelivered** to another consumer → **not lost** (at-least-once) → must be **idempotent**.
- **Auto-ack (risky):** message is acked on *delivery*, so if you crash mid-processing it's **lost**.
- **prefetch** limits in-flight unacked messages (fair dispatch). Repeated failures → `nack` with `requeue=false` → **Dead-Letter Queue**.

🖼️
```
Queue ──deliver──▶ Consumer (processing… not yet acked)
                       │  💥 crash before ack
                       ▼
Broker sees unacked ──▶ requeue ──▶ redeliver to another consumer
   (after N fails) ─────────────────▶ Dead-Letter Queue
```

🗣️ **Say this:** *"With manual acknowledgements the message is held as unacked while I process it; if my consumer crashes before acking, RabbitMQ requeues and redelivers it to another consumer, so it isn't lost — that's at-least-once, so my handler is idempotent. I avoid auto-ack because it acks on delivery and would drop the message on a crash. Messages that keep failing get nacked without requeue and routed to a dead-letter queue so they don't block the others."*

---

# PART 2 — SPRING (THE JAVA FLOW THEY WANTED)

## §6 — What is Spring + the REAL request flow ⭐ (your R1 miss)
❓ *"What is Spring, and how does a request flow through it?"*

🎯 **What is Spring:** a framework built around an **IoC/DI container** — you declare **beans** (`@Component/@Service/@Repository/@Controller`) and Spring wires their dependencies. **Spring Boot** adds **auto-configuration**, **starters**, and an **embedded server** so it "just runs."

🎯 **Spring MVC request flow (say it in this order):**
```
1. HTTP request → embedded Tomcat (Servlet container)
2. Servlet FILTER chain        ── CORS, Spring Security (authN/authZ), logging
3. DispatcherServlet           ── the front controller, entry to Spring MVC
4. HandlerMapping              ── find which @Controller method handles the URL
5. HandlerInterceptor.preHandle()
6. Argument resolution + @Valid ── bind @RequestBody/@PathVariable, validate DTO
7. @RestController method  →  @Service (business)  →  @Repository (DB, JPA)
8. Return value → HttpMessageConverter (Jackson) → JSON
   (or ViewResolver → HTML for server-rendered MVC)
9. HandlerInterceptor.postHandle() / afterCompletion()
10. Response back out through the Filter chain → client

   (AOP @Aspect wraps service methods — e.g., @Transactional, logging;
    @ControllerAdvice + @ExceptionHandler turn exceptions into responses)
```

🖼️ **Nest ↔ Spring map (so you never mix them up again):**
```
NestJS                         Spring
──────                         ──────
Middleware            ≈        Servlet Filter
Guard (canActivate)   ≈        Spring Security / HandlerInterceptor
Interceptor (RxJS)    ≈        AOP @Aspect / HandlerInterceptor
Pipe (ValidationPipe) ≈        ArgumentResolver + @Valid
Exception Filter      ≈        @ControllerAdvice / @ExceptionHandler
Controller→Service    ≈        @RestController→@Service→@Repository
```

🗣️ **Say this:** *"Spring is a dependency-injection container — I declare beans and it wires them; Spring Boot adds auto-config, starters and an embedded server. For a web request: it hits Tomcat, goes through the servlet filter chain where Spring Security handles auth, then the DispatcherServlet — the front controller — uses HandlerMapping to find the controller method. Interceptors run pre-handle, arguments are bound and the DTO validated with @Valid, then my @RestController calls a @Service which calls a @Repository for the database. The return value is serialized to JSON by Jackson via an HttpMessageConverter, interceptors run post-handle, and the response goes back out through the filters. Cross-cutting concerns are AOP aspects like @Transactional, and exceptions are handled centrally with @ControllerAdvice."*

---

## §7 — Building REST APIs in Spring Boot (the approach)
❓ *"How do you create REST APIs in Spring Boot? What's the approach?"*

🎯 **Layered, annotation-driven:**
```java
@RestController
@RequestMapping("/api/v1/tasks")          // versioned base path
class TaskController {
  private final TaskService service;       // constructor injection (DI)
  TaskController(TaskService service){ this.service = service; }

  @GetMapping                              // GET /api/v1/tasks?page=0&size=20
  Page<TaskDto> list(Pageable pageable){ return service.list(pageable); }

  @GetMapping("/{id}")
  ResponseEntity<TaskDto> get(@PathVariable Long id){
    return ResponseEntity.ok(service.get(id));
  }

  @PostMapping                             // 201 Created
  @ResponseStatus(HttpStatus.CREATED)
  TaskDto create(@Valid @RequestBody CreateTaskDto dto){ return service.create(dto); }
}

@Service                                   // business logic
class TaskService { /* uses repository */ }

interface TaskRepository extends JpaRepository<Task, Long> { }   // Spring Data JPA = free CRUD

@RestControllerAdvice                      // global error handling
class ApiExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  ResponseEntity<?> notFound(){ return ResponseEntity.status(404).body(...); }
}
```

🎯 **Checklist:** `@RestController` + mapping annotations · DTOs + `@Valid` (Bean Validation: `@NotBlank`, `@Email`) · `@Service` for logic · `@Repository`/`JpaRepository` for data · `ResponseEntity` + correct **status codes** · `@ControllerAdvice` for errors · `Pageable`/`Page` for pagination · API **versioning** in the path.

🗣️ **Say this:** *"I build it in layers. A @RestController maps the HTTP endpoints under a versioned path; it takes a DTO validated with @Valid and delegates to a @Service that holds business logic, which uses a @Repository — Spring Data JPA gives me CRUD for free by extending JpaRepository. I return ResponseEntity with proper status codes — 201 on create, 404 on not found — handle errors centrally with @ControllerAdvice, and use Pageable for pagination. It's the same controller-service-repository split I use in NestJS, just with Spring annotations."*

---

# PART 3 — SCALE, DEPLOY, OPERATE

## §8 — Handling high traffic
❓ *"If there's a lot of traffic, how do you handle it?"*

🎯 **First: find the bottleneck** (CPU? DB? a slow downstream?). Then:
1. **Horizontal scaling** behind a **load balancer** (add instances, not bigger boxes).
2. **Stateless services** (session in Redis / JWT) so any instance serves any request.
3. **Cache** hot reads in **Redis** → fewer DB hits.
4. **Database**: read replicas, connection pooling, **indexing**, query tuning, sharding if needed.
5. **Async**: offload heavy work (emails, PDFs, reports) to a **queue** (Kafka/Bull) — don't do it in the request.
6. **Rate limiting** to protect the system; **CDN** for static assets.
7. **Autoscaling** (Kubernetes **HPA**) on CPU/metrics; **circuit breakers** so a slow dependency doesn't cascade.

🖼️
```
            ┌── Load Balancer ──┐
  Traffic ─▶│  (round-robin)    │─▶ app#1  app#2  app#3   ← scale out (stateless)
            └───────────────────┘        │       │
                                      Redis cache │   ← absorb hot reads
                                         DB primary ──▶ read replicas
                                      heavy work → Queue → workers (async)
```

🗣️ **Say this:** *"I scale horizontally behind a load balancer and keep services stateless so any instance can handle any request. I put a Redis cache in front of hot reads to take pressure off the database, and I tune the DB with indexing, connection pooling and read replicas. Anything heavy or non-immediate I push to a queue and process async instead of blocking the request. I add rate limiting, a CDN for static content, and autoscaling on CPU — plus circuit breakers so a slow dependency degrades one feature instead of cascading. But first I'd measure to find the actual bottleneck rather than scaling blindly."*

---

## §9 — Docker → Kubernetes: local code to production (full flow) ⭐
❓ *"Explain Docker and Kubernetes — how do you deploy, from local code to running in production?"*

🎯 **Step by step:**
```
LOCAL                                                   PRODUCTION (Kubernetes)
─────                                                   ───────────────────────
1. Write code
2. Write a Dockerfile (multi-stage:                     7. Write K8s manifests:
   build stage compiles, slim runtime stage)               - Deployment (image, replicas, probes)
3. docker build -t myapp:1.0 .   → an IMAGE              - Service (stable internal address + LB)
4. docker run / docker-compose up                         - Ingress (external HTTP routing)
   (run locally with db+redis containers)                 - ConfigMap / Secret (config & secrets)
5. Test locally                                         8. kubectl apply -f k8s/
6. docker push to a REGISTRY                            9. API server stores DESIRED STATE
   (Docker Hub / AWS ECR / ACR)                         10. Scheduler places Pods on Nodes;
                                                            kubelet pulls the image & starts containers
                                                         11. ReplicaSet keeps N replicas alive
                                                         12. Service + Ingress route traffic in
                                                         13. New version → ROLLING UPDATE (zero downtime);
                                                             kubectl rollout undo to roll back
```

🖼️ **Mental model:** *Image = the frozen app. Container = a running copy. Kubernetes = the manager that runs many containers, heals them, scales them, and routes traffic.*
```
 Dockerfile ─build─▶ Image ─push─▶ Registry ─pull─▶ ┌─ Kubernetes Cluster ─────────────┐
                                                    │ Deployment → ReplicaSet → Pods   │
                                  Ingress ─▶ Service ─▶  Pod  Pod  Pod (your container)│
                                                    │ (self-heals, scales, rolls out)  │
                                                    └──────────────────────────────────┘
```

🗣️ **Say this:** *"I containerize the app with a multi-stage Dockerfile so the final image is small — build the jar in one stage, copy it into a slim runtime. `docker build` makes an image; I run it locally with docker-compose alongside its dependencies. Then I push the image to a registry. For production I describe the desired state in Kubernetes manifests — a Deployment with the image, replica count and health probes, a Service for a stable address and load-balancing, an Ingress for external routing, and ConfigMaps/Secrets for config. `kubectl apply` stores that desired state; the scheduler places Pods on nodes, the kubelet pulls the image and runs containers, and the ReplicaSet keeps the right number alive. New versions go out as a rolling update with zero downtime, and I can roll back instantly."*

---

## §10 — Auto-restart / self-healing in production
❓ *"How do you ensure containers/pods automatically restart in production?"*

🎯
- **`restartPolicy: Always`** — Kubernetes restarts a crashed container.
- **Liveness probe** — "is it alive?" If it fails, K8s **kills and restarts** the container (recovers from deadlock/hang).
- **Readiness probe** — "ready for traffic?" If it fails, K8s **removes the pod from the Service** (no traffic) but doesn't restart it — used during warm-up or temporary overload.
- **Startup probe** — for slow-starting apps, before liveness kicks in.
- **ReplicaSet** — maintains the **desired replica count**; if a pod or whole node dies, it **reschedules** a new one elsewhere.
- **HPA** — scales replica count up/down on CPU/metrics.

🖼️
```
Liveness fail  → restart the container        (it's stuck)
Readiness fail → stop sending traffic         (not ready, but alive)
Pod/Node dies  → ReplicaSet reschedules a new Pod   (desired state restored)
```

🗣️ **Say this:** *"Kubernetes is declarative — I tell it I want N healthy replicas and it keeps that true. A liveness probe detects a hung container and restarts it; a readiness probe stops routing traffic to a pod that isn't ready without killing it; and the ReplicaSet reschedules pods if a node dies. So failures self-heal without anyone paging me, and the HPA scales replicas on load."*

---

# PART 4 — CACHING, GATEWAY, SECURITY, REAL-TIME

## §11 — Redis caching & invalidation
❓ *"How do you handle cache invalidation in Redis when data updates?"*

🎯 **Patterns:**
- **Cache-aside (lazy)** — default: read checks cache; on **miss**, load from DB and populate. On **write**, update the DB then **invalidate (delete) the key** (or update it). Next read re-warms it.
- **Write-through** — write to cache + DB together (cache always fresh, slower writes).
- **Write-behind** — write cache now, DB async (fast, risk on crash).
- **Invalidation tools:** **TTL** expiry, explicit **delete-on-write**, **versioned keys** (`user:42:v3`), **pub/sub** to bust caches across instances.
- **Stampede protection:** when a hot key expires, many requests hit the DB at once → use a short **lock/single-flight**, **stale-while-revalidate**, or **jittered TTL**.

🖼️
```
READ:  app → Redis? ──hit──▶ return
                   └─miss──▶ DB → set key (TTL) → return
WRITE: app → DB update → DELETE Redis key   (next read re-loads fresh)
```

🗣️ **Say this:** *"My default is cache-aside: reads check Redis, a miss loads from the DB and populates the cache with a TTL, and on a write I update the DB and then invalidate the key so the next read re-warms it with fresh data. For data that must always be fresh I'd use write-through. I guard against cache stampede on hot keys with a short lock or jittered TTLs so a popular key expiring doesn't slam the database. And in a multi-instance setup I bust caches across nodes via Redis pub/sub or versioned keys."*

---

## §12 — What does an API Gateway solve?
❓ *"What exactly does an API Gateway solve in a system?"*

🎯 It's the **single front door** that handles cross-cutting concerns so services don't each repeat them:
- **Routing** to the right service · **load balancing**
- **Authentication** — validate the **JWT once** at the edge
- **Rate limiting / throttling** · **SSL termination**
- **Request aggregation** (combine several services for one client call)
- **Hides internal topology** — clients don't need to know service addresses
- Central place for **logging, metrics, CORS**

🖼️
```
            ┌──────── API GATEWAY ────────┐
 Client ───▶│ route · authN(JWT) · rate-  │──▶ Auth svc
  (1 URL)   │ limit · SSL · aggregate     │──▶ Order svc
            └─────────────────────────────┘──▶ Inventory svc
```

🗣️ **Say this:** *"The gateway is the one public entry point. It routes requests to the right service, validates the JWT once so each service doesn't re-implement auth, and centralizes rate limiting, SSL termination, and request aggregation. It also hides the internal topology — clients hit one URL and never need to know how many services are behind it or where they live."*

---

## §13 — Secure API design + JWT logout
❓ *"How do you design secure APIs (auth, rate limiting, validation)?" + "How do you invalidate JWT tokens on logout?"*

🎯 **Secure API checklist:**
- **AuthN** (who) — JWT / OAuth2; **AuthZ** (what) — roles/scopes (RBAC).
- **Validate all input** — whitelist, Bean Validation, **parameterized queries** (no SQL injection).
- **Rate limiting** per user/IP; **HTTPS/TLS** everywhere; security headers; least privilege; secrets in a vault.

🎯 **JWT logout — the hard part:** a JWT is **stateless and self-contained**, so you can't just "delete" it server-side. Options:
1. **Short-lived access token + refresh token** — access token lives 5–15 min; on logout you **revoke the refresh token** (stored server-side), so it can't be renewed and the access token expires fast.
2. **Server-side denylist** in **Redis**, keyed by the token's **`jti`** (id), with TTL = the token's remaining life — check it on each request.
3. **`tokenVersion`** per user in the DB — bump it on logout-all-devices; tokens carry the version, mismatch = invalid.

🖼️
```
Login   → issue access(15m) + refresh(7d, stored)
Logout  → revoke refresh (delete from store) + add access jti to Redis denylist (TTL=remaining)
Request → verify signature → check jti NOT in denylist → allow
```

🗣️ **Say this:** *"Authentication with JWT or OAuth2, authorization with roles, validate every input and use parameterized queries, rate-limit per client, and everything over TLS. The tricky bit is JWT logout: a JWT is stateless, so I keep access tokens short-lived with a refresh token, and on logout I revoke the refresh token so it can't be renewed. If I need immediate invalidation I add the token's jti to a Redis denylist with a TTL equal to its remaining lifetime, or bump a per-user tokenVersion to log out all devices at once."*

---

## §14 — Scaling WebSockets + rooms vs namespaces
❓ *"How do you scale WebSocket connections across multiple servers?" + "What are rooms and namespaces?"*

🎯 **The problem:** a WebSocket is a **stateful, persistent connection to ONE server instance**. With many instances behind a load balancer, an event created on instance A won't reach a client connected to instance B.

🎯 **The fixes:**
1. **Sticky sessions** — the load balancer pins a client to the same instance for its connection.
2. **Pub/sub backplane / adapter** — instances share events through **Redis** (the **Socket.io Redis adapter**): when A emits to a room, it publishes to Redis, every instance receives it and forwards to its local clients. This also syncs room membership across instances.

🖼️
```
        Client1 ── inst A ─┐                 ┌─ inst B ── Client2
                           ▼   Redis Pub/Sub ▼
                       (broadcast fans out to ALL instances → all clients)
```

🎯 **Rooms vs Namespaces:**
- **Namespace** = a separate **endpoint/channel of concern** (e.g., `/chat`, `/notifications`) — its own handlers/middleware. Clients choose to connect.
- **Room** = an arbitrary **group of sockets within a namespace** that you broadcast to (e.g., `order:123`, `user:42`). Server-side only; sockets join/leave. Use rooms to target a subset (one chat group, one user's devices).

🗣️ **Say this:** *"A WebSocket is a stateful connection to a single instance, so when I scale out, a message emitted on one instance won't reach clients on another. I solve that with sticky sessions plus a Redis pub/sub backplane — the Socket.io Redis adapter — so a broadcast publishes to Redis and every instance delivers it to its local clients, and room membership stays in sync. Namespaces are separate endpoints for different concerns like /chat versus /notifications; rooms are arbitrary groups of sockets within a namespace that I broadcast to — like a room per chat group or per user — so I can target exactly the right clients."*

---

# PART 5 — DATA: MONGODB, SQL, NODE

## §15 — MongoDB indexing strategy + TTL
❓ *"How do you decide indexing strategy by query pattern?" + "If multiple TTL indexes/expirations are involved, how do you handle incoming requests?"*

🎯 **Index by your actual queries — the ESR rule** for compound indexes: **Equality → Sort → Range.**
- Put **equality** fields first (`status = 'active'`), then the **sort** field, then **range** fields (`createdAt > x`).
- A compound index `{a,b,c}` also serves queries on its **prefixes** `{a}` and `{a,b}`.
- Prefer **high-cardinality** fields (more selective). Aim for **covered queries** (all needed fields in the index → no document fetch). Index slows writes + costs space, so index for real patterns, not everything. Verify with **`explain()`** — want `IXSCAN`, not `COLLSCAN`.

🎯 **TTL indexes:** a single-field index on a **Date** with `expireAfterSeconds` auto-deletes expired docs. **A background monitor runs ~every 60s**, so deletion is **eventual, not instant** — expired docs can briefly still be present. With multiple expirations, **don't trust instant deletion**: on read, **filter logically** (`expiresAt > now`) so users never see expired data while TTL cleans up in the background. (TTL must be a single date field — no compound TTL.)

🖼️
```
Query: status='A' sort by createdAt where age>18
Index:  { status:1 ,  createdAt:1 ,  age:1 }
            Equality      Sort         Range      ← ESR
TTL: { expiresAt:1 } expireAfterSeconds:0  → background sweep ~60s (eventual)
     → on read, also filter expiresAt > now
```

🗣️ **Say this:** *"I index for the queries I actually run, following ESR — equality fields first, then the sort field, then ranges — and I lean on compound-index prefixes so one index serves several queries. I check explain() to confirm it's an index scan, not a collection scan, and I don't over-index because indexes slow writes. For TTL, the expiry is handled by a background sweep that runs about once a minute, so deletion isn't instant — I never rely on it for correctness; I also filter expired documents logically on read so the user never sees stale data while TTL cleans up."*

---

## §16 — Node.js event loop
❓ *"Explain the event loop phases and how they impact execution."*

🎯 Node is **single-threaded for your JS**, using **libuv** for async I/O. The loop runs in **phases**:
```
   ┌──────────────────────────────────────────────┐
   │ timers        → setTimeout / setInterval cbs   │
   │ pending cbs   → some system callbacks          │
   │ poll          → I/O (the loop waits here)      │
   │ check         → setImmediate cbs               │
   │ close         → 'close' events                 │
   └──────────────────────────────────────────────┘
   Between EVERY callback: drain microtasks →
        process.nextTick queue  (highest priority)
        then Promise .then/await jobs
```
🎯 **Impact:** a **blocking, CPU-heavy synchronous** task (big loop, sync crypto, JSON of a huge object) **freezes the whole loop** — no I/O, no other requests progress → latency spikes. Fix: keep handlers async/non-blocking, break up work, or offload CPU-bound work to **worker threads** / a queue. `process.nextTick` and Promises run **before** the loop continues, so misuse can starve I/O.

🗣️ **Say this:** *"Node runs my JavaScript on a single thread and uses libuv for async I/O. The event loop cycles through phases — timers, poll for I/O, check for setImmediate, close — and between every callback it drains microtasks: process.nextTick first, then promise jobs. The key implication is that any blocking, CPU-heavy synchronous work stalls the entire loop, so all other requests wait. So I keep handlers non-blocking and push CPU-bound work to worker threads or a queue."*

---

## §17 — SQL refresher (brush-up)
❓ *Expect: joins, grouping, and one or two "write a query" questions.*

🎯 **Core syntax**
```sql
SELECT col, COUNT(*) AS c
FROM orders
WHERE status = 'PAID' AND created_at >= '2026-01-01'
GROUP BY col
HAVING COUNT(*) > 5            -- HAVING filters groups; WHERE filters rows
ORDER BY c DESC
LIMIT 10;
```

🖼️ **JOINs**
```
INNER JOIN  → only matching rows in both       A ∩ B
LEFT JOIN   → all of A + matches from B (NULL)  A (+B)
RIGHT JOIN  → all of B + matches from A          (A+) B
FULL JOIN   → everything, matched or not        A ∪ B
```
```sql
SELECT u.name, o.id
FROM users u
LEFT JOIN orders o ON o.user_id = u.id;   -- users even with no orders
```

🎯 **Classic interview queries**
```sql
-- 2nd highest salary
SELECT MAX(salary) FROM emp WHERE salary < (SELECT MAX(salary) FROM emp);
-- or: SELECT DISTINCT salary FROM emp ORDER BY salary DESC LIMIT 1 OFFSET 1;

-- Top-N per group (window function)
SELECT * FROM (
  SELECT e.*, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY salary DESC) rn
  FROM emp e
) t WHERE rn <= 3;

-- Find duplicates
SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1;
```

🎯 **Know cold:**
- **Indexes** speed reads (B-tree), slow writes; check with `EXPLAIN`.
- **ACID** = Atomicity, Consistency, Isolation, Durability.
- **Isolation levels** (weak→strong): Read Uncommitted → Read Committed → Repeatable Read → Serializable; anomalies they prevent: dirty read, non-repeatable read, phantom read.
- `WHERE` filters rows **before** grouping; `HAVING` filters **after**. Aggregates: COUNT/SUM/AVG/MIN/MAX. Subqueries & CTEs (`WITH x AS (...)`).

🗣️ **Say this (joins):** *"INNER JOIN returns only rows matching in both tables; LEFT JOIN keeps all rows from the left table and fills NULLs where the right has no match — I use that to find, say, users with no orders. WHERE filters individual rows before grouping; HAVING filters the groups after a GROUP BY. For 'second highest' I'd use a correlated subquery or a window function with DENSE_RANK."*

---

## §18 — MongoDB queries refresher (brush-up)
❓ *"You've used Mongo — write a query / aggregation."*

🎯 **CRUD + operators**
```js
db.users.find({ age: { $gte: 18 }, role: { $in: ['admin','mod'] } },
              { name: 1, _id: 0 })          // filter + projection
        .sort({ age: -1 }).limit(10).skip(20);

db.users.findOne({ email: /@gmail\.com$/ }); // $regex
// operators: $gt $gte $lt $lte $ne $in $nin $exists $and $or $regex

db.users.insertOne({ name: 'A', age: 30 });
db.users.updateOne({ _id: id }, { $set:{ age: 31 }, $inc:{ logins: 1 },
                                  $push:{ roles: 'editor' } }, { upsert: true });
db.users.deleteMany({ active: false });
```

🎯 **Aggregation pipeline** (data flows stage → stage)
```js
db.orders.aggregate([
  { $match:  { status: 'PAID' } },                       // WHERE
  { $group:  { _id: '$userId', total: { $sum: '$amount' } } }, // GROUP BY + SUM
  { $sort:   { total: -1 } },                            // ORDER BY
  { $lookup: { from:'users', localField:'_id',
               foreignField:'_id', as:'user' } },         // JOIN
  { $unwind: '$user' },                                   // flatten array
  { $project:{ name:'$user.name', total: 1, _id: 0 } }   // SELECT
]);
```

🖼️ **SQL ↔ Mongo map**
```
WHERE     → $match        GROUP BY → $group         JOIN   → $lookup
ORDER BY  → $sort         SELECT   → $project        LIMIT  → $limit
SUM/AVG   → $sum/$avg     unnest   → $unwind         OFFSET → $skip
```

🗣️ **Say this:** *"Reads use find with operators like $gte, $in and $regex, plus a projection to return only the fields I need, and sort/limit/skip for paging. Writes use update operators — $set to change fields, $inc to bump counters, $push to add to arrays, with upsert when I want insert-or-update. For analytics I use the aggregation pipeline, where documents flow through stages: $match filters like WHERE, $group with $sum is GROUP BY, $lookup is a join, $unwind flattens the joined array, and $project shapes the output like SELECT."*

---

# PART 6 — MANAGERIAL / BEHAVIORAL ROUND

## §19 — How to win the managerial round
🎯 **Use STAR for every story:** **S**ituation → **T**ask → **A**ction → **R**esult (lead with the result's impact, use "I" not "we", keep it ~90 seconds).

🎯 **Prep these 6 stories from YOUR real work** (Procurex + State Street) — write 2–3 bullets each:
1. **Ownership / impact** — a feature you drove end-to-end across the 12+ microservices (e.g., the RFQ-to-payment flow) and the business result.
2. **A production incident you debugged** — symptom → how you diagnosed (logs/metrics/traces) → fix → prevention. *(Shows the JD's "service quality & availability".)*
3. **A technical disagreement** you resolved — two options, how you weighed trade-offs, how you aligned the team. *(Shows collaboration with product owners — also in the JD.)*
4. **Delivering under a tight deadline** — how you prioritized/scoped (MVP first), what you cut.
5. **Mentoring / code review** — raising quality, helping a teammate.
6. **A failure / mistake** — what went wrong, what you learned, what you changed. *(Be honest; show growth.)*

🎯 **Common WF managerial questions → angle:**
| Question | Angle |
|---|---|
| "Why Wells Fargo?" | Scale + impact of financial systems; reliability/compliance matters to you; growth. |
| "Tell me about yourself" | 30s: backend engineer, distributed systems, what you own now, why this role. |
| "A conflict with a teammate?" | Disagree-and-commit; focus on the problem, data over ego. |
| "How do you handle ambiguity?" | Clarify with stakeholders, break it down, ship the smallest valuable slice. |
| "How do you prioritize?" | Impact vs effort; business value; communicate trade-offs. |
| "Time you failed?" | Own it, the fix, the lesson, the process change so it can't recur. |
| "How do you ensure quality?" | Tests (pyramid), code review, monitoring, on-call ownership. |

🗣️ **Opening "tell me about yourself" (adapt):** *"I'm a backend engineer with ~3 years building production microservices on a distributed architecture — I currently own several services in a procurement platform, working across REST and gRPC, Kafka, Redis and SQL databases, all the way from data model to deployment. I care about reliability and clean service boundaries, and I'm drawn to Wells Fargo because financial systems are where correctness, scale and availability really matter — which is the kind of engineering I want to grow in."*

> ✅ **Managerial golden rules:** be concrete (numbers), own your decisions ("I"), show you collaborate with product owners, and end every story on a **result + a lesson**.

---

# 🎤 DAY-OF CHEAT SHEET (one-line openers)
- **gRPC vs HTTP** → *"REST/JSON at the edge; gRPC — binary Protobuf over HTTP/2 — between internal services for speed and a typed contract."*
- **Systems fail** → *"Timeout, retry with backoff, circuit breaker, fallback, bulkhead — degrade one feature, don't cascade."*
- **Service B down** → *"Fail fast, retry transient errors, trip the breaker, serve a fallback, or queue for later."*
- **Kafka consumer fails** → *"Offset wasn't committed → redelivered → so I make processing idempotent; poison → DLQ."*
- **Kafka vs Redis pub/sub** → *"Durable replayable log vs fire-and-forget broadcast that's lost if no one's listening."*
- **RabbitMQ crash** → *"Manual ack → unacked message is requeued and redelivered; not lost."*
- **Spring flow** → *"Filter chain → DispatcherServlet → HandlerMapping → interceptor → Controller → Service → Repository → JSON."*
- **REST in Spring** → *"@RestController → @Service → @Repository (JpaRepository), DTO + @Valid, ResponseEntity, @ControllerAdvice."*
- **High traffic** → *"Find the bottleneck, then scale out stateless behind an LB, cache in Redis, async the heavy work, autoscale."*
- **Docker→K8s** → *"Image → registry → Deployment/Service/Ingress → kubectl apply → ReplicaSet runs & heals Pods → rolling update."*
- **Auto-restart** → *"Liveness restarts a hung container; readiness pulls it from traffic; ReplicaSet reschedules on node loss."*
- **Cache invalidation** → *"Cache-aside: update DB then delete the key; TTL + stampede protection on hot keys."*
- **API Gateway** → *"One front door: routing, auth once, rate limiting, SSL, aggregation."*
- **JWT logout** → *"Stateless → short-lived access + revoke refresh; or a Redis jti denylist; or bump tokenVersion."*
- **Scale WebSockets** → *"Sticky sessions + Redis adapter backplane so broadcasts fan out across instances."*
- **Rooms vs namespaces** → *"Namespace = separate endpoint/concern; room = a group of sockets to broadcast to."*
- **Mongo indexing** → *"ESR — Equality, Sort, Range; verify with explain(); don't over-index."*
- **TTL** → *"Background sweep ~60s, eventual delete → also filter expired on read."*
- **Event loop** → *"Single-threaded JS + libuv; phases + microtasks; never block it — offload CPU work."*
- **SQL joins** → *"INNER = matches both; LEFT = all left + NULLs; WHERE before grouping, HAVING after."*
- **Mongo aggregation** → *"$match→$group→$sort→$lookup→$unwind→$project ≈ WHERE/GROUP BY/ORDER/JOIN/SELECT."*

---

_Good luck, Divakar. Read the **🗣️ Say this** lines out loud twice — speaking them is what makes them stick._
