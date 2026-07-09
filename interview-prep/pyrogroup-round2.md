# Pyrogroup — Round 2 (Face-to-Face) Prep

Selected after the SwiftPay hackathon. This round is **senior backend fundamentals**. Golden rule:
**answer the concept, then tie it to SwiftPay** (I built a system that uses Redis, Kafka, indexing,
locks, Docker, and had to reason about scale). That's what wins.

---

# 1. Redis

### Is Redis single-threaded or multi-threaded?
**Command execution is single-threaded** — one event loop processes every command, one at a time.
- Since **Redis 6.0** there is **multi-threaded I/O** (reading/writing bytes on sockets is parallelised),
  and there are **background threads** for slow jobs (async delete `UNLINK`, RDB/AOF persistence via `fork`).
- But the **core — running your commands — stays single-threaded on purpose.**

🎤 *"Redis executes commands on a single thread. Redis 6 added multi-threaded network I/O, but command
execution is still single-threaded — that's deliberate: it makes every command atomic with no locks."*

### Why is single-threaded actually FAST (not a limitation)?
1. **In-memory** — data lives in RAM, no disk seeks on reads.
2. **No locks / no context switching** — one thread → no mutex contention, no race conditions;
   every command is **atomic** for free (that's why `SET key val NX` is a reliable lock primitive).
3. **I/O multiplexing (epoll/kqueue)** — one thread juggles thousands of connections via an event loop,
   instead of one thread per connection.
4. **Optimised C data structures** + a **simple wire protocol (RESP)** → tiny per-command overhead.

🎤 *"It's fast because it's in-memory, and single-threaded means no lock contention and atomic ops.
It uses epoll to handle thousands of connections on one thread."*

### Data types + the structures underneath (they love this)
| Type | Under the hood | Use it for |
|---|---|---|
| **String** | SDS (simple dynamic string) | cache, counters (`INCR`), **idempotency keys** |
| **List** | quicklist (linked listpacks) | queues (`LPUSH`/`RPOP`) |
| **Hash** | listpack (small) / hashtable | objects with fields |
| **Set** | intset (all ints) / hashtable | unique members, tags |
| **Sorted Set (ZSet)** | **skiplist + hashtable** | leaderboards, ranking, priority queues — O(log n) by score |
| **Stream** | radix tree | append-only log (mini-Kafka) |
| Bitmap / HyperLogLog / Geo | strings / ZSet | flags, cardinality estimates, geo |

### SwiftPay tie
- **Idempotency:** `SET idem:payment:<id> 1 NX EX 86400` — the `NX` (set-if-not-exists) is atomic
  *because* Redis is single-threaded → a perfect distributed "claim."
- **Balance cache:** `String` value + 30s TTL (cache-aside); evicted after each settlement.

**Follow-ups:** *How does Redis persist?* RDB (point-in-time snapshot via fork) + AOF (append-only log of
writes). *Redis vs Memcached?* Redis has rich types + persistence + replication; Memcached is a plain KV cache.

---

# 2. Java — Threads, Streams, Kafka

### Threads
- **Thread** = smallest unit of execution. Define work via `Runnable`/`Callable`.
- **Don't `new Thread()` in real code — use a thread pool** (`ExecutorService`): reuses threads, bounds
  concurrency, avoids the cost of creating/destroying threads.
- **Concurrency hazards:** race conditions (two threads write shared state), **visibility** (a thread
  doesn't see another's write → `volatile`), **atomicity** (`i++` isn't atomic → `synchronized` /
  `AtomicInteger` / locks).
- **`synchronized` vs `ReentrantLock`:** `synchronized` = simple intrinsic lock; `ReentrantLock` adds
  `tryLock()`, timeouts, fairness, interruptibility.
- **Virtual threads (Java 21, Project Loom):** ultra-light threads — you can have **millions**; great for
  I/O-bound work (each blocked call parks cheaply). Platform threads = 1:1 OS threads (heavy).

🎤 *"I use ExecutorService/thread pools, not raw threads. For shared state I reason about visibility and
atomicity — volatile for visibility, locks or atomics for atomicity. Java 21 virtual threads make
I/O-bound concurrency cheap."*

### Thread vs Async vs Process (people mix these up)
- **Thread** = a **worker** — a unit of execution inside a process. More threads = more work at the same
  time → **parallelism** (on multiple CPU cores).
- **Async** = a **non-blocking style**, *not* a worker — it stops a thread from sitting **idle while it
  waits** (for I/O) and lets it do other work meanwhile → **concurrency without parallelism**.
- **Process** = an **independent program with its own memory** (heavy, isolated). Threads live inside a process.

**Restaurant analogy:** more **threads** = hire more chefs (cook in parallel). **Async** = one chef who
chops veg while the pasta boils instead of watching it (one worker, never idle). **Process** = a whole
separate kitchen.

| | Thread | Async function | Process |
|---|---|---|---|
| What | a worker | a non-blocking *style* | isolated program |
| Memory | shared within the process | runs on a thread | its own, isolated |
| Gives | parallelism (cores) | concurrency, no blocking | isolation + parallelism |
| Weight | medium | free | heavy |
| Best for | **CPU-bound** | **I/O-bound** (waiting) | separate services |

- **CPU-bound** (compute): async does nothing → use **threads** (parallel cores).
- **I/O-bound** (DB/network/disk waits — most web backends): **async wins** → one thread serves thousands
  because each is mostly *waiting*.
- **Node** = 1 thread + async event loop (offload CPU to worker threads/processes). **Java** = real threads;
  **Java 21 virtual threads** = write blocking code, park cheaply → async-like scale with simple code.
- **SwiftPay:** gateway = thread-per-request pool (250 req/s in parallel); ledger consumer = 12 threads.
  If it were NestJS: one thread + `await` on Redis/Postgres/Kafka.

🎤 *"A thread is a worker; async is a style that stops a worker blocking while it waits. Threads →
parallelism (CPU-bound); async → one thread switching instead of waiting (I/O-bound); a process is an
isolated program. Node is single-thread + async; Java uses threads, and virtual threads give async scale
with blocking-style code."*

### Streams (java.util.stream) — NOT I/O streams, NOT Kafka Streams
- A **functional pipeline** over a collection: `source → intermediate ops (filter/map/sorted) → terminal
  op (collect/reduce/count)`.
- **Lazy** — nothing runs until the terminal op. Can go **parallel** with `.parallelStream()`.
```java
List<Tx> completed = txns.stream()
    .filter(t -> t.getStatus() == COMPLETED)
    .sorted(comparing(Tx::getCreatedAt).reversed())
    .toList();
```
🎤 *"Streams are a lazy functional pipeline — map/filter/reduce. Different from java.io streams and from
Kafka Streams, which are unrelated."*

### Kafka
- **Distributed, append-only log.** `Topic → Partitions → Offsets`. **Partition = the unit of parallelism
  and of ordering** (order is guaranteed only *within* a partition).
- **Consumer group:** partitions are shared across consumers in a group; add consumers → Kafka rebalances
  → parallel consumption. More than #partitions consumers = idle ones.
- **At-least-once delivery** → the consumer must be **idempotent** (a message may arrive twice).
- **Key → partition:** same key always lands on the same partition → per-key ordering.

### SwiftPay tie
- Gateway **produces** `PaymentInitiated` to `payments.initiated`, **keyed by transactionId** (per-payment
  ordering). Ledger **consumes** with group `ledger-service`; I ran **12 partitions × 12 threads**.
- **At-least-once + idempotent consumer**: `settle()` skips if the tx isn't `PENDING`; a redelivery is a
  no-op. Failures → retry with back-off → **dead-letter topic**.

---

# 3. Databases — Indexing, Partitioning, Sharding

### Indexing — what, structure, where stored, why fast
- **What:** a separate data structure that maps **column value → row location**, so the DB finds rows
  *without scanning the whole table*.
- **Structure: B-tree / B+tree** — balanced, sorted; lookups and **range scans** in **O(log n)**.
  (Hash index = equality only; LSM-tree = write-heavy stores like Cassandra/RocksDB.)
- **Where it's stored:** on **disk**, as its **own structure** next to the table. In Postgres the index is
  a separate relation; its **leaf nodes hold the indexed value + a pointer (`ctid`) to the row** in the
  heap. In MySQL/InnoDB the **clustered (primary) index leaf holds the actual row data**.
- **Why fast:** B+tree on 1,000,000 rows ≈ **~20 comparisons** (log₂ 1M) vs **1,000,000** for a full scan.
  Leaves are linked → range queries (`WHERE created_at BETWEEN …`) are efficient too.

```
Full table scan:   read all N rows        → O(N)
B+tree index:      root → branch → leaf   → O(log N)   (1M rows ≈ 20 steps)
```

- **Clustered vs non-clustered:** clustered = table stored *in* index order (one per table, the PK,
  InnoDB); non-clustered/secondary = separate structure pointing at rows (all Postgres indexes).
- **Composite index** (multi-column, left-to-right), **covering index** (index has every column the query
  needs → *index-only scan*, never touches the table).
- **Trade-off:** indexes **speed reads but slow writes** (every insert/update must maintain the index) and
  use disk. So index what you query, don't over-index.

### SwiftPay tie
- `idx_tx_sender`, `idx_tx_receiver` on `transactions` → `GET /users/{id}/transactions` is an index lookup,
  not a full scan. Primary key on `transaction_id` → O(log n) idempotency check + settlement lookup.

### Partitioning — split one table *within one DB*
- Break a big table into **partitions** by a key: **range** (by date), **hash** (by id), or **list**.
- The engine does **partition pruning** — only scans the relevant partition. Smaller indexes, easier
  maintenance (drop last month's partition instantly).
- e.g. partition `transactions` by `created_at` monthly, or by `hash(sender_id)`.

### Sharding — split data *across many DB servers* (horizontal scale)
- Each **shard** is a **separate database** holding a subset of the data, chosen by a **shard key**
  (e.g. `account_id`). Scales **writes + storage beyond one machine.**
- **Partitioning = within one DB; Sharding = across many DBs.**
- **Hard parts:** cross-shard queries/transactions (a transfer between accounts on different shards needs a
  distributed transaction / saga), and rebalancing when you add shards.

🎤 *"An index is a B+tree that turns a full scan into an O(log n) lookup, stored separately with pointers
to the rows. Partitioning splits a table within one database; sharding splits it across many databases by a
shard key to scale writes horizontally."*

---

# 4. Design Patterns & Concurrency

### Factory pattern (creational)
- **Encapsulates object creation** so callers depend on an **interface**, not on `new ConcreteClass()` →
  decoupling, and you add new types without touching callers.
- **Factory Method:** a method returns objects of a common interface; subclasses pick the concrete type.
- **Abstract Factory:** a factory that creates **families** of related objects.
- **Everywhere in Java/Spring:** `LoggerFactory.getLogger()`, `Calendar.getInstance()`,
  and **Spring's `ApplicationContext`/`BeanFactory` *is* a factory** — it creates and wires your beans.
```java
interface PaymentProcessor { void process(Payment p); }
class ProcessorFactory {
    PaymentProcessor create(String type) {
        return switch (type) {
            case "UPI"  -> new UpiProcessor();
            case "CARD" -> new CardProcessor();
            default -> throw new IllegalArgumentException(type);
        };
    }
}
```
🎤 *"A factory centralises object creation behind an interface so callers don't hard-code concrete classes.
Spring's container is itself a factory — it builds and injects beans."*

### Mutex / Locks
- **Mutex = mutual exclusion:** only **one** thread holds it at a time → protects a **critical section**.
- **Semaphore:** allows **N** concurrent holders (mutex = semaphore of 1).
- **Java:** `synchronized` (intrinsic monitor lock), `ReentrantLock` (explicit — `tryLock`, timeout,
  fairness), `ReadWriteLock` (many readers OR one writer).
- **Deadlock:** two threads grab two locks in opposite order → both wait forever. Avoid by **consistent
  lock ordering** / `tryLock` with timeout.
- **Distributed lock** (across processes/machines): **Redis** `SET NX EX` (+ Redlock), Zookeeper, or a
  **DB row lock**.

### SwiftPay tie (you have THREE kinds of "locking" — great talking point)
1. **Pessimistic DB lock:** `SELECT … FOR UPDATE` (`findByIdForUpdate`) locks the transaction row so two
   deliveries of the same payment serialise → settlement is idempotent under concurrency.
2. **Atomic/optimistic (no app lock):** `UPDATE accounts SET balance = balance - :amt WHERE id=:id AND
   balance >= :amt` — the DB row lock during the UPDATE makes it race-safe; no explicit mutex needed.
3. **Distributed "claim":** Redis `SET idem NX` acts as a cross-service lock for idempotency.

🎤 *"For race safety I didn't reach for an app-level mutex — I used the database: a conditional atomic
UPDATE for the debit, and SELECT FOR UPDATE to serialise duplicate settlements. Redis SET NX is my
distributed idempotency lock."*

---

# 5. Docker & Kubernetes

### Docker
- **Container** = an isolated process with its own filesystem + network, **sharing the host kernel** →
  far lighter than a **VM** (which ships a whole guest OS).
- **Image** = immutable, layered template; **container** = a running instance of it.
- **Why:** kills "works on my machine," starts in ms, packs densely, portable.
- **SwiftPay:** multi-stage `Dockerfile` per service (build stage with Maven+JDK21 → slim JRE runtime
  stage), whole stack via `docker-compose` (Postgres, Kafka, Redis, 3 services).

```
VM:        Hardware → Host OS → Hypervisor → [Guest OS + App] × N     (heavy, GBs, slow boot)
Container: Hardware → Host OS → Docker Engine → [App] × N              (light, MBs, ms boot)
```

### Kubernetes (K8s)
- **Container orchestration:** runs, scales, heals containers across a cluster.
- **Pod** (smallest unit, 1+ containers) · **Deployment** (declares replicas + rolling updates) ·
  **Service** (stable IP + load-balancing to pods) · **ConfigMap/Secret** (config) ·
  **HPA** (Horizontal Pod Autoscaler — scale replicas on CPU/metrics).
- **Self-healing** (restarts dead pods), **rolling deploys** (zero downtime), **service discovery**.
- **SwiftPay on K8s:** gateway = `Deployment` with N replicas + `HPA` (scale on CPU/req-rate) behind a
  `Service`; ledger = `Deployment`; Kafka/Postgres/Redis as `StatefulSet`s or managed cloud services.

---

# 6. ⭐ Scaling SwiftPay: 1M → 2M / 5M (the flagship question)

**First, clarify the metric** (do this out loud in the interview): *"5M — over what window? 5M/day is
~58 TPS, trivial. If you mean 5× the throughput, ~1,250 TPS sustained, that's the real question — here's
how I'd scale each layer."*

**The key insight:** SwiftPay is **already built to scale** — it's **stateless at the edge, event-driven,
and idempotent**, so I scale each layer independently and Kafka absorbs spikes as a buffer.

```
Client → [Gateway ×N]  → Kafka (P partitions) → [Ledger consumers ×N] → Postgres (sharded)
          stateless,       buffer / backpressure    row-lock safe,          the real bottleneck
          autoscale                                 scale with partitions
```

**Layer by layer:**
1. **Gateway — trivial.** It's **stateless** (no session), returns `202` fast. Run N replicas behind a load
   balancer; K8s **HPA** autoscales on CPU/req-rate. Near-linear.
2. **Kafka — add partitions + brokers.** Throughput ≈ partitions × per-consumer rate. For 5×, go from 12 →
   ~60 partitions and add brokers. Keying by `transactionId` keeps per-payment ordering.
3. **Ledger — scale the consumer group.** Add ledger instances into group `ledger-service`; Kafka
   rebalances partitions across them → parallel settlement. The `SELECT FOR UPDATE` row lock keeps it
   correct no matter how many threads.
4. **Postgres — the hardest part (writes). In order of escalation:**
   - **Connection pooling (PgBouncer)** — many app instances, bounded DB connections.
   - **Read replicas** — send history/balance/analytics reads to replicas; writes to primary.
   - **Partitioning** — partition `transactions` by time or `hash(account_id)` → smaller indexes.
   - **Sharding** — the big lever: shard **accounts + transactions by `account_id`** across multiple DB
     clusters; route by shard key → scales writes horizontally.
     - *Cross-shard transfer* (sender & receiver on different shards) needs a **distributed
       transaction / saga** — which my event-driven design already leans toward.
5. **Redis — Redis Cluster** (sharded) for idempotency keys + cache at high volume.
6. **Backpressure is free:** gateway returns `202` + Kafka queues the work, so a spike **doesn't crush the
   DB** — it drains at the ledger's pace. That's the whole point of the async design.
7. **Analytics already scales separately** — ClickHouse (CQRS read model) absorbs the reporting load.
8. **Observability + autoscaling:** Prometheus/Grafana metrics, K8s HPA per service, tracing to find the
   next bottleneck.

🎤 *"Because the gateway is stateless and settlement is async and idempotent, scaling is mostly horizontal:
more gateway replicas, more Kafka partitions and consumers. The real bottleneck is Postgres writes — I'd
add pooling and read replicas first, then partition, then shard by account_id. Kafka acts as a buffer so a
traffic spike queues instead of overwhelming the database. I load-tested the current setup at 250 TPS for
1M transactions with zero failures; scaling to 1,250 TPS is adding partitions/consumers/replicas and
sharding the DB."*

---

## Rapid-fire soundbites to memorise
- **Redis fast?** In-memory + single-threaded (no locks, atomic) + epoll multiplexing.
- **Redis threading?** Single-threaded command execution; multi-threaded I/O since v6.
- **Index?** B+tree, stored separately with row pointers, turns O(N) scan into O(log N).
- **Partition vs shard?** Partition = split within one DB; shard = split across many DBs by a key.
- **Mutex?** One holder at a time; in SwiftPay it's DB row locks (`SELECT FOR UPDATE` + atomic UPDATE), not app mutexes.
- **Factory?** Create objects behind an interface; Spring's container is a factory.
- **Container vs VM?** Container shares the host kernel (light); VM ships a guest OS (heavy).
- **Scale to 5×?** Stateless gateway replicas + more Kafka partitions/consumers + DB pooling→replicas→partition→shard; Kafka buffers the spike.
