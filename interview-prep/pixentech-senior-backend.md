# 🎮 Pixentech — Senior Backend Engineer Interview Prep

> Real-time **gaming / iGaming** platform: thousands of concurrent users, live event processing, financial transactions.
> Stack: Node.js · NestJS · TypeScript · GraphQL · MongoDB · Redis · BullMQ · Socket.io · AWS (ECS/Lambda) · Docker · CI/CD.
> Format: ❓ Question → 🎯 explanation → 🗣️ **Say this** → 🎮 gaming angle.

---

## 🏗️ NestJS Architecture & Internals

---

❓ **Question**

> Walk me through the exact order of NestJS's request lifecycle. A request comes in to place a bet — middleware, guards, pipes, interceptors, exception filters — what fires when, and where would you put JWT auth vs. wallet-balance validation vs. response shaping?

🎯 NestJS runs every request through a fixed pipeline. The order is non-negotiable, and knowing it tells you exactly where each concern belongs.

```
Incoming request
      │
      ▼
┌──────────────┐   global → module → route bound, runs on raw req/res
│ Middleware   │   (Express/Fastify level, no ExecutionContext / route metadata yet)
└──────┬───────┘
       ▼
┌──────────────┐   AuthN/AuthZ → returns boolean / throws
│ Guards       │   (has ExecutionContext: handler + class reflection)
└──────┬───────┘
       ▼
┌──────────────┐   "before" half: pre-handler logic, set up the RxJS stream
│ Interceptors │ ──┐
└──────┬───────┘   │  (wrap the handler in an RxJS pipeline)
       ▼           │
┌──────────────┐   │   validate + transform the actual params/body
│ Pipes        │   │
└──────┬───────┘   │
       ▼           │
┌──────────────┐   │
│ ROUTE HANDLER│   │   your controller method
└──────┬───────┘   │
       ▼           │
┌──────────────┐ ◄─┘  "after" half: map/transform outbound, timing
│ Interceptors │
└──────┬───────┘
       ▼
   Response
       │
   (any throw anywhere ▶ Exception Filters ▶ formatted error response)
```

Two subtleties worth stating precisely:

1. **Interceptors straddle the handler.** The same interceptor runs both before (the synchronous code before `return next.handle()`) and after (the operators you `.pipe()` onto the returned stream) via RxJS. That's why caching, timing, and response envelopes live here — one class sees both edges of the call.

2. **The "before" edge of interceptors fires *before* pipes, but pipe execution is deferred until the handler's arguments are actually resolved.** So the practical ordering the handler experiences is: guards → interceptor pre-logic → pipes → handler. The takeaway is unchanged — guards reject before any body validation or DB work — but if anyone presses you on "interceptor vs pipe order," interceptors set up first, pipes resolve the args just-in-time before the method runs.

For placing a bet:
- **Middleware** → request-id correlation, raw-body capture for signature verification, IP capture for anti-fraud.
- **Guard** → `JwtAuthGuard` (is this a valid, unexpired session?) + `RolesGuard` (is the account allowed to bet — not self-excluded, KYC-passed, region-permitted?). AuthN/AuthZ is the textbook guard job because it's a binary allow/deny on context.
- **Pipe** → `ZodValidationPipe`/`ValidationPipe` to validate the bet DTO shape (stake is a positive, bounded number; `marketId` is a valid ObjectId). Shape only — not "is the market currently open," which is live state.
- **Inside the handler/service** → wallet-balance, no-double-spend, market-still-open-at-settlement. These are *business invariants requiring atomicity*, not request-shape checks — they belong in the service as an atomic conditional update (`findOneAndUpdate` with the balance/market predicate in the filter), NOT in a pipe or guard.
- **Interceptor** → response envelope `{ data, traceId, serverTime }`, plus latency metrics.
- **Exception filter** → map `InsufficientFundsException` → HTTP 402, `MarketClosedException` → 409, with a clean body.

The classic senior mistake is doing the balance check in a guard. Guards have no transactional context and run before validation — you'd read a balance, then the handler mutates it in a separate non-atomic step, and between your read and your write a concurrent bet drains the wallet: a check-then-act race, i.e. double-spend. The balance must be enforced *in the same atomic operation that debits it*, never as a prior read.

🗣️ **Say this:** "Order is middleware, guards, then interceptors set up, pipes resolve the args, the handler runs, and interceptors finish on the way out, with exception filters catching anything thrown. Middleware is raw req/res for cross-cutting stuff like request IDs. Guards are pure allow-deny — that's JWT auth and role checks, and they run before any validation so garbage traffic is rejected cheaply. Pipes validate and coerce the DTO shape right before the handler. Interceptors wrap the handler, so they run both before and after — I use them for response envelopes and timing. The thing I'm careful about: wallet balance and double-spend do NOT go in a guard or pipe — they need atomicity, so they live in the service as a single conditional update where the balance check IS the debit. A balance read in a guard is a check-then-act race — a double-spend waiting to happen."

🎮 **Gaming angle:** At bet volume the lifecycle order is a performance and cost lever — rejecting an expired JWT in a guard skips the validation pipe and the wallet read entirely, so cheap rejections never touch Mongo. Under a credential-stuffing or bot flood, that ordering is the difference between shedding bad traffic at the edge and melting your database connection pool on requests you were always going to reject.

---

❓ **Question**

> Guards vs. interceptors vs. pipes vs. middleware — they all sound like "code that runs around a handler." Give me the precise mental model for choosing one, with a gaming example for each.

🎯 They differ on three axes: **what context they see**, **what they can do**, and **when they run**.

| Concern | Sees | Can it block? | Can it transform response? | Use it for |
|---|---|---|---|---|
| **Middleware** | raw `req`/`res`/`next` | yes (don't call `next`) | no (only raw res) | logging, CORS, raw-body, request-id |
| **Guard** | `ExecutionContext` + reflection | yes (return false/throw) | no | authN/authZ, feature flags, rate-limit gate |
| **Pipe** | the argument value + metadata | yes (throws) | transforms **input** only | validation, type coercion, parsing |
| **Interceptor** | `ExecutionContext` + RxJS stream | technically (throwing aborts the stream) | yes (wraps input call **and** output) | response shaping, caching, timing, retries, timeouts |

The decision tree:
- Need framework-level raw access, or to run before route/DI metadata exists? → **middleware**.
- Binary "is this request allowed?" using roles/tokens/context? → **guard**.
- "Is this *input* well-formed / coerce its type?" → **pipe**.
- "Wrap the call to add behavior before AND after, or shape the output?" → **interceptor**.

Gaming examples:
- **Middleware** → attach a `correlationId` to every HTTP request and websocket upgrade so a bet can be traced across services.
- **Guard** → `AntiCheatGuard` reads a Redis token-bucket and denies a client firing 500 actions/sec.
- **Pipe** → coerce and validate `{ stake, marketId }`, rejecting `stake <= 0` or a malformed market id.
- **Interceptor** → a `CacheInterceptor` on the leaderboard route that serves a Redis-cached top-100 and short-circuits the handler on a hit; or a `TimeoutInterceptor` that fails a bet-settlement call after 2s rather than hanging a connection.

Why not collapse them? Guards can't transform output. Interceptors *can* technically abort by throwing, but they run after auth and validation already passed, so using one as your gate means you've already paid for the work you're trying to reject — that's why authorization belongs in a guard, not an interceptor. Pipes only touch one argument. Mixing these up is the tell of someone who's only used NestJS superficially.

One honest nuance for a senior: guards and interceptors *both* receive `ExecutionContext`, so the difference isn't "what they see" — it's **intent and capability**. A guard is a gate (boolean, runs early, before pipes); an interceptor is a wrapper (owns the RxJS stream, runs around the handler, shapes output). Choose by what you need to *do*, not just what you can *see*.

🗣️ **Say this:** "I pick based on what the code needs to see and do. Middleware is raw req/res before routing — logging, request IDs. Guards answer one yes/no question from the execution context — auth, roles, rate-limit gating — and they run early, before pipes. Pipes validate or coerce a single input value right before the handler. Interceptors wrap the handler with an RxJS stream so they run before and after — response envelopes, caching, timeouts, timing. Gaming mapping: anti-cheat rate-limit is a guard, bet-DTO validation is a pipe, leaderboard caching is an interceptor, request correlation is middleware. The distinction people miss: guards and interceptors both see ExecutionContext, but a guard is a gate that runs early and an interceptor is a wrapper that shapes output — so authorization is a guard, never an interceptor, because an interceptor runs after you've already paid for the work."

🎮 **Gaming angle:** A leaderboard `CacheInterceptor` is the highest-leverage one — top-100 is read by every client every second, so serving it from Redis in an interceptor and short-circuiting the handler on a hit turns a hot Mongo `$sort`/`$limit` aggregation into an O(1) read off a Redis sorted set (`ZREVRANGE`). At 100k concurrent clients that's the difference between a sustainable read path and an aggregation that pins a Mongo secondary.

---

❓ **Question**

> Explain NestJS dependency injection and IoC like you're teaching it. Then: what's the difference between `useClass`, `useValue`, and `useFactory`, and when have you actually reached for each?

🎯 **IoC (Inversion of Control)** means your class doesn't construct its own dependencies — it declares what it needs in the constructor, and the framework supplies them. The **DI container** is the thing that does the supplying. NestJS builds a dependency graph at bootstrap, resolves it in dependency order, and instantiates providers as **singletons by default**, scoped to the module they're registered in (and visible to other modules only if exported).

The wiring is token-based. A provider registers a **token** (usually the class itself, sometimes a string/symbol — and for interfaces you *must* use a string/symbol or abstract class, because TypeScript interfaces don't exist at runtime) mapped to a recipe for producing the value:

```ts
// shorthand — token IS the class, recipe is "new it up"
providers: [WalletService]

// the explicit recipes:
providers: [
  // useClass: token → instantiate this class (swap implementations)
  { provide: PaymentGateway, useClass: StripeGateway },

  // useValue: token → hand back this exact object (configs, mocks, constants)
  { provide: 'WALLET_CONFIG', useValue: { maxStake: 10_000 } },

  // useFactory: token → run this fn to build the value (async/conditional/deps)
  {
    provide: 'REDIS',
    useFactory: (cfg: ConfigService) =>
      new Redis(cfg.getOrThrow('REDIS_URL')),
    inject: [ConfigService],
  },

  // useExisting: token → alias to another existing token (shared singleton)
  { provide: 'LEGACY_WALLET', useExisting: WalletService },
]
```

When I reach for each:
- **`useClass`** → swapping implementations behind an abstract token. e.g. `PaymentGateway` resolves to `StripeGateway` in prod, `FakeGateway` in tests, or per-region providers — controllers depend on the abstraction, not the concrete class.
- **`useValue`** → injecting plain config objects, feature-flag snapshots, or **mocks in tests**. No instantiation, just "here's the value." Note it's handed back by reference, so treat it as immutable shared state.
- **`useFactory`** → anything that needs **runtime logic or async** to build: a Redis/Mongo client built from `ConfigService`, choosing a provider based on an env var, or awaiting a secret from AWS Secrets Manager. The `inject` array feeds the factory its own dependencies, and the factory can be `async` so Nest awaits it before the app finishes bootstrapping. This is the most powerful and the one seniors lean on for connection clients.
- **`useExisting`** → aliasing two tokens to the *same* singleton instance (e.g. a new interface token pointing at a legacy concrete service during a migration) — distinct from `useClass`, which would create a second instance.

Why it matters: DI is what makes the code testable and swappable. Because everything is a token, in a unit test I `useValue` a mock wallet, and the service under test never knows the difference.

🗣️ **Say this:** "IoC means the framework constructs your dependencies instead of you `new`-ing them; you declare them in the constructor and the DI container resolves the whole graph at bootstrap, as singletons by default. Providers are registered by token mapped to a recipe. `useClass` swaps which implementation a token resolves to — great for hiding Stripe versus a fake gateway behind an abstract token. `useValue` hands back a literal — configs and test mocks. `useFactory` runs a function to build the value, it can be async, and it can inject other providers — that's how I build Redis or Mongo clients from ConfigService, or pull a secret from AWS Secrets Manager at startup, and Nest awaits it before the app is ready. One detail: for an interface I register a string or symbol token, because TS interfaces don't exist at runtime. The payoff is testability — because it's all tokens, I swap a real wallet for a `useValue` mock and the service under test is none the wiser."

🎮 **Gaming angle:** `useFactory` with `inject: [ConfigService]` is how I build the Redis client used for leaderboards and rate-limit buckets — one async factory reads the cluster URL from Secrets Manager at boot, opens a single pooled (or cluster) connection, and every consumer gets that same singleton instead of opening a socket per request. At thousands of bets/sec, connection-per-request would exhaust file descriptors long before it exhausted Redis.

---

❓ **Question**

> Provider scopes: DEFAULT, REQUEST, TRANSIENT. What are they, what's the cost of REQUEST-scoped providers, and would you use one in a high-throughput bet-processing service?

🎯 NestJS providers have three scopes:

```
DEFAULT (singleton) → one instance for the whole app lifetime. Created at
                      bootstrap, shared everywhere. Cheapest.

REQUEST             → a NEW instance (and its whole sub-tree of deps) is
                      created per incoming request. Can inject the REQUEST
                      object (@Inject(REQUEST)). Most expensive.

TRANSIENT           → a new instance every time it's INJECTED (each consumer
                      gets its own copy). Not shared.
```

The critical, often-missed cost: **scope bubbles up**. If a REQUEST-scoped provider is injected into an otherwise-singleton service, that consumer is promoted to REQUEST scope too, and so on up the chain. One request-scoped logger deep in the graph can silently make a whole branch of your app instantiate-per-request — allocation + GC pressure on every call, plus the per-request DI resolution cost itself. You also lose the ability to inject those providers into things that genuinely must be singletons (like a websocket gateway).

For a high-throughput bet processor: **no — default singleton.** At thousands of bets/sec, per-request instantiation of services and their dependency trees is real CPU, allocation, and GC churn, and it shows up as p99 latency. Instead of REQUEST scope to carry per-request data (current user, trace ID, tenant), I use **`AsyncLocalStorage`** (via `nestjs-cls` or a thin ALS wrapper) — that gives you request-scoped *context* without request-scoped *providers*, so everything stays a singleton. The one caveat with ALS: the context must be established inside the async chain (e.g. in middleware or an interceptor) and you have to be careful it survives across any manual `setImmediate`/event-emitter hops, but for the standard request path it's transparent.

When REQUEST scope is genuinely justified: multi-tenant per-request DB/connection selection, or when a third-party library truly needs the raw request injected into a provider and you can't thread it through method args. Even then, I keep the request-scoped subtree as small and leaf-level as possible so the bubble-up doesn't infect singletons.

🗣️ **Say this:** "DEFAULT is a singleton — one instance for the app, created at bootstrap, cheapest. REQUEST creates a fresh instance per request and can inject the request object, but it's the most expensive and, crucially, scope bubbles up — anything that injects a request-scoped provider also becomes request-scoped, which can silently turn a whole branch of the graph into per-request instantiation. TRANSIENT is a new copy per injection site. For a bet processor doing thousands of requests a second, I stay on singletons — per-request instantiation is allocation and GC pressure that shows up in p99. If I need per-request data like a user or trace ID, I use AsyncLocalStorage via nestjs-cls, which gives me request context without giving up singletons. I only reach for REQUEST scope for genuine per-tenant connection selection, and even then I keep that subtree tiny so it doesn't bubble into things that must stay singletons."

🎮 **Gaming angle:** A Socket.io gateway holding tens of thousands of live connections *must* be a singleton — it owns long-lived state (the socket registry, room membership). If you accidentally inject a REQUEST-scoped provider into it, Nest can't instantiate it as a singleton and you've broken your real-time layer outright. ALS is how you still carry the betting user's identity and trace ID through the async call chain — across the guard, the service, the BullMQ producer — without falling into that trap.

---

❓ **Question**

> How do you design module boundaries so a monolith can later be split into microservices without a rewrite? Talk about feature modules, shared modules, `forRoot`/`forRootAsync`, and circular dependencies.

🎯 The goal is **high cohesion inside a module, loose coupling between modules**, so a module can be lifted out and deployed independently.

```
AppModule
 ├─ WalletModule      ── exports: WalletService (the only public surface)
 ├─ BettingModule     ── imports: WalletModule, depends on its EXPORTS only
 ├─ LeaderboardModule
 └─ shared/
     ├─ RedisModule   ── @Global or imported; forRootAsync(config)
     └─ ConfigModule  ── @Global

Rule: a module talks to another module ONLY through its exported providers,
never by importing the other module's internal files directly.
```

Principles I apply:
- **Feature modules own a bounded context.** `WalletModule` owns wallet schemas, repo, service, controller. It `exports` only the service — its Mongo schema and internals stay private. That exported service is the seam: tomorrow it becomes a gRPC client to a Wallet microservice and nothing else changes.
- **Shared/infra modules** (Redis, Config, logging) are `forRoot`/`forRootAsync` dynamic modules so each consuming app configures them once. `forRootAsync` lets the config be built via a factory that injects `ConfigService` — essential when the connection string comes from Secrets Manager at runtime — and is the same dynamic-module pattern (`{ module, providers, exports }`) you'd reuse for a `register()`/`registerAsync()` on a per-feature queue.
- **Depend on exports, not internals.** If `BettingModule` reaches into `WalletModule`'s repository directly, you've coupled to the implementation and the split becomes a rewrite. Going through the exported `WalletService` means swapping it for a remote transport (gRPC/message broker) is a one-line provider change.
- **Circular dependencies** are a design smell. If `Wallet` needs `Betting` and `Betting` needs `Wallet`, usually a responsibility is in the wrong place — extract a third module or invert the dependency with an event (`bet.settled` → wallet reacts) instead of a direct call. `forwardRef()` patches it as a last resort, but I treat it as a flag to revisit the boundary, not a solution — and note it doesn't survive a microservice split, where a true cycle becomes a distributed deadlock risk, so event-driven decoupling is the answer that scales both ways.

The microservice payoff: because controllers/services depend on **injected abstractions** (the `WalletService` token), converting the in-process call to a `ClientProxy.send()`/`.emit()` over gRPC or NATS is localized to one provider — the bounded contexts you drew as modules become the deployable services. The honest tradeoff: that boundary swap turns a synchronous, transactional in-process call into a network call, so you inherit partial failure, retries, idempotency, and (for the wallet) the loss of a single-DB transaction — which is exactly why money flows like settlement move to an outbox/saga pattern rather than a naive remote call.

🗣️ **Say this:** "I design modules as bounded contexts with a single public surface — the module exports only its service, and internals like schemas and repositories stay private. Other modules depend on that export, never on internal files. That exported service is the seam: when I split to microservices, I swap the local provider for a gRPC or message-broker client and consumers don't change. Shared infra like Redis and Config are dynamic modules with forRootAsync so the connection can be built from a factory that reads Secrets Manager at runtime. Circular deps I treat as a boundary smell — I invert the call into an event, like a 'bet.settled' event the wallet reacts to, rather than reach for forwardRef, partly because a cycle that's just awkward in-process becomes a distributed deadlock once it's two services. The honest caveat is that the boundary swap also turns a transactional in-process call into a network call, so for money I move settlement onto an outbox or saga for idempotency and partial-failure handling — done right, the modules I draw today are the services I deploy tomorrow."

🎮 **Gaming angle:** Wallet and betting are the classic split — the wallet (money, double-spend, audit, regulatory retention) has different scaling, security, and consistency needs than the high-write, low-durability bet-ingest path. If `BettingModule` only ever knew `WalletService` through its export, peeling Wallet into its own hardened service with its own DB is a transport swap, not a rewrite — and you can scale the firehose bet-ingest path horizontally while keeping the wallet on a smaller, strongly-consistent, heavily-audited tier.

---

❓ **Question**

> How do you unit-test a service that has injected dependencies — say a `BetService` that depends on a wallet repository and Redis — without spinning up real infrastructure? And when do you reach for `Test.createTestingModule` vs. plain mocks?

🎯 DI is what makes this clean: because `BetService` receives its deps through the constructor by token, in a test I supply **fakes for those tokens** and exercise the real service logic in isolation.

Two approaches, by altitude:

**1. Plain instantiation (fastest, true unit test):**
```ts
const wallet = { debit: jest.fn(), getBalance: jest.fn() };
const redis  = { incr: jest.fn() };
const service = new BetService(wallet as any, redis as any);

it('rejects bet when balance insufficient', async () => {
  wallet.getBalance.mockResolvedValue(0);
  await expect(service.placeBet(dto)).rejects.toThrow(InsufficientFundsException);
  expect(wallet.debit).not.toHaveBeenCalled();
});
```
No NestJS machinery — I just `new` the class with mock objects. Best when the service has no decorators/scopes to resolve. The catch: I'm pinning the constructor signature manually, so this can drift if the DI graph changes.

**2. `Test.createTestingModule` (when you want the DI container):**
```ts
const moduleRef = await Test.createTestingModule({
  providers: [
    BetService,
    { provide: WalletRepository, useValue: walletMock },   // override token
    { provide: 'REDIS', useValue: redisMock },
  ],
}).compile();

const service = moduleRef.get(BetService);
```
I reach for this when I want Nest to **resolve the graph** — testing that providers wire up, exercising guards/interceptors/pipes through the container, or using `.overrideProvider(X).useValue(mock)` on a real module to swap just one dep while keeping the rest real. It's also how you test custom-provider factories, and note that for REQUEST-scoped providers you'd use `moduleRef.resolve()` rather than `.get()`.

Decision: **plain `new` for pure logic** (fast, no framework), **`TestingModule` when DI wiring or Nest constructs are part of what you're testing**. For the bet logic itself — the invariants that matter, like "never debit twice," "reject on insufficient funds" — I want fast plain-mock unit tests, then a thin layer of integration tests with `TestingModule` against an in-memory Mongo (`mongodb-memory-server`) and a real Redis to catch wiring and atomicity bugs.

The sharp caveat for money: **a mock cannot prove concurrency safety.** A unit test where `getBalance` returns 0 proves the *logic* branch, but it tells you nothing about whether two simultaneous `placeBet` calls can both pass the check and both debit. That property only emerges from the atomic operation itself, so it has to be verified at the integration level (or against a real Mongo) with concurrent calls — a green pure-mock suite can still hide a double-spend.

🗣️ **Say this:** "Because dependencies come in through the constructor by token, I just supply fakes. For pure logic I skip Nest entirely and `new` the service with jest-mock objects — fastest feedback, and I assert things like 'debit was never called when the balance was zero.' When I actually want the DI container — to test that providers wire up, to run guards or pipes through it, or to override just one provider on a real module with `.overrideProvider().useValue()` — I use `Test.createTestingModule`. So: plain mocks for business invariants, TestingModule for wiring and Nest constructs. The caveat I'm explicit about for money: a mock can't prove concurrency safety — a green pure-mock suite can still hide a double-spend — so I back the bet logic with integration tests against mongodb-memory-server that fire concurrent debits and verify the atomic update actually holds."

🎮 **Gaming angle:** The double-spend invariant is exactly what you cover at both levels — a fast unit test proves the service *logic* rejects a second debit, and an integration test against in-memory Mongo fires two concurrent settles and proves the *atomic* `findOneAndUpdate` filter condition (e.g. `{ _id, balance: { $gte: stake } }`) actually serializes them, which a pure mock fundamentally cannot verify because there's no real document and no real concurrency.

---

❓ **Question**

> Exception filters: how does Nest's exception layer work, and how would you build a global filter that gives clients clean errors while preserving observability for a financial transaction failure?

🎯 Nest funnels every unhandled throw into an **exceptions layer**. By default a global filter catches `HttpException` (and subclasses) and serializes a standard `{ statusCode, message }`; anything that isn't an `HttpException` becomes a generic 500. You override this with **exception filters** — classes decorated `@Catch(SomeException)` implementing `catch(exception, host)`, where `host` (`ArgumentsHost`) lets you grab the right transport context (HTTP res, RPC, or WS).

```ts
@Catch()                                  // catch-all
export class AllExceptionsFilter implements ExceptionFilter {
  constructor(private readonly logger: LoggerService) {}

  catch(ex: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const res = ctx.getResponse();
    const req = ctx.getRequest();

    const status = ex instanceof HttpException ? ex.getStatus() : 500;
    const traceId = req.traceId;          // from ALS / middleware

    // observability: log the FULL error + context internally
    this.logger.error({ traceId, path: req.url, userId: req.user?.id, ex });

    // client: clean, leak-free body
    res.status(status).json({
      statusCode: status,
      error: ex instanceof DomainException ? ex.code : 'INTERNAL_ERROR',
      traceId,                            // give them the trace handle
      timestamp: new Date().toISOString(),
    });
  }
}
```

Design principles for a financial failure:
- **Separate the internal record from the client payload.** Log the stack, the user, the wallet state, and the `traceId` to CloudWatch; return the client only a stable error `code` + `traceId`. Never leak balances, stack traces, or internal messages.
- **Map domain exceptions to HTTP semantics.** `InsufficientFundsException` → 402, `MarketClosedException` → 409, `DuplicateBetException` → 409 (idempotency-key collision). I throw typed domain exceptions in the service; the filter is the single place that translates them, so controllers stay clean.
- **Be transport-aware.** The same logical error arriving over a websocket bet or a gRPC call needs `host.switchToWs()`/`switchToRpc()` — branch on `host.getType()`. Watch the failure modes: over WS there's no status code (you emit an error event on the socket, and a thrown error must not tear down the connection for everyone); over RPC you don't leak an HTTP body at all.
- **Don't let the filter mask ret/safety semantics.** A 5xx the client should retry must stay a 5xx; don't collapse a transient `MongoNetworkError` into a 400. And the filter should be *side-effect-free* — it reports failure, it does not compensate. Rolling back a half-applied debit is the service/saga's job, not the filter's.
- **Filter ordering / specificity:** more specific `@Catch(InsufficientFundsException)` filters can sit alongside a catch-all; Nest applies the first matching one. Register global filters via the `APP_FILTER` provider so they can inject the logger/ALS — `app.useGlobalFilters(new ...)` can't do DI.

The observability win is the `traceId`: the client gets an opaque handle, support pastes it into CloudWatch Logs Insights, and you reconstruct the entire failed transaction without ever exposing internals.

🗣️ **Say this:** "Nest routes every throw into an exceptions layer; the default filter serializes HttpExceptions and turns everything else into a 500. I override it with a global filter that does two jobs at once: internally it logs the full error with user, context, and a traceId to CloudWatch, and externally it returns a clean, leak-free body with a stable error code and that same traceId. I throw typed domain exceptions in services — InsufficientFunds, MarketClosed — and the filter is the single place that maps them to HTTP status, so controllers stay clean. I register it via APP_FILTER so it can inject the logger and request context, and I make it transport-aware with ArgumentsHost so the same error works over HTTP, websocket, or gRPC — and over a socket I emit an error event without tearing down the connection. Two things I'm careful about: the filter reports failure, it doesn't compensate — rolling back a half-applied debit is the saga's job — and I don't collapse a retryable 5xx into a 4xx. The traceId is the key: clients get an opaque handle, support traces the whole failed transaction, and we never leak balances or stack traces."

🎮 **Gaming angle:** For a failed wallet debit you must never return the user's balance or internal reason in the error body — that's an info leak attackers actively probe to map wallet state and find timing oracles. The filter returns `{ error: 'INSUFFICIENT_FUNDS', traceId }`; the full picture (balance, attempted stake, prior bets, IP) goes only to CloudWatch keyed by that traceId. And because a single websocket carries many bets, the filter must fail *that message* cleanly without dropping the connection — otherwise one rejected bet disconnects a live player mid-session.

---

This is a self-contained editing task on a Markdown section provided inline. No file operations or tool calls are needed. Here is the polished section.

## 🔗 GraphQL (teach thoroughly — candidate gap)

### ❓ **Question 1: Walk me through what GraphQL actually is, and how a query goes from the client to a resolver. Why would we pick it over REST for our gaming platform?**

🎯 GraphQL is a **query language for your API plus a runtime that executes those queries against a type system**. Instead of many endpoints each returning a fixed shape, you expose **one endpoint** and a **schema** (written in SDL — Schema Definition Language) that describes every type and the operations available. The client asks for exactly the fields it wants; the server returns exactly that shape — no over-fetching, no under-fetching.

The schema is the contract. Three root types anchor it:

```graphql
type Query    { ... }   # reads
type Mutation { ... }   # writes
type Subscription { ... } # server-pushed streams

type Player {
  id: ID!
  username: String!
  wallet: Wallet!          # nested object -> its own resolver
  recentBets(limit: Int = 10): [Bet!]!
}
```

`!` means non-nullable, `[Bet!]!` is a non-null list of non-null Bets.

**Execution flow** of `query { player(id:"7"){ username wallet { balance } } }`:

```
HTTP POST /graphql
   │  { query, variables }
   ▼
1. Parse      -> turns the string into an AST
2. Validate   -> checks AST against the schema (fields exist? types match?)
3. Execute    -> walks the AST field-by-field, calling RESOLVERS
   player()        resolver -> returns a Player object
     username      resolver -> default: reads obj.username
     wallet()      resolver -> fetches Wallet
       balance     resolver -> default: reads wallet.balance
4. Respond    -> assembles a JSON shaped EXACTLY like the query
```

A resolver is just a function `(parent, args, context, info) => result`. The result of a parent resolver becomes the `parent` of its children — that's the **resolver chain**. If you don't write a resolver for a scalar field, GraphQL uses a *default resolver* that just reads the property off `parent`. Resolvers can return promises, and GraphQL awaits them — sibling fields resolve concurrently, which matters for latency.

**vs REST — when to use which:**

| | REST | GraphQL |
|---|---|---|
| Shape | Fixed per endpoint | Client-specified |
| Round trips | Often N (player, then wallet, then bets) | 1 |
| Caching | Trivial (HTTP/CDN per URL) | Harder — one POST URL; need field-level (DataLoader/Redis) or persisted-query GETs |
| Versioning | /v1, /v2 | Evolve schema, `@deprecated` fields, never break the contract |
| Error model | HTTP status codes | Always 200; `errors[]` + partial `data` |
| Observability | Per-route metrics free | Per-resolver/operation tracing needed (one URL hides everything) |
| File upload / simple CRUD | Great fit | Awkward (multipart spec or pre-signed S3 URLs) |

GraphQL shines when **clients are diverse and data is graph-shaped** (a player → wallet → transactions → bets). REST shines for **simple, cacheable, resource-oriented** traffic, webhooks, and high-throughput service-to-service calls (where I'd actually reach for gRPC).

🎮 **Gaming angle:** Our mobile client, web client, and an in-game overlay each need a *different slice* of the player profile. With REST we'd either build custom endpoints per client or over-fetch. One GraphQL query lets the mobile lobby pull `username + balance + activeBonuses` in a single request, while the web profile page pulls a richer shape — same endpoint, no backend churn, and we ship a new client feature by changing the query, not the API. The cost we accept: caching and observability are no longer free, so we engineer them deliberately.

🗣️ **Say this:** "GraphQL is a single endpoint backed by a typed schema where the client specifies exactly the fields it wants. A request gets parsed, validated against the schema, then executed by walking the query tree and calling a resolver per field — the parent resolver's return value feeds its children, that's the resolver chain, and sibling fields resolve concurrently. I reach for it when I have multiple clients needing different shapes of graph-connected data, like a player profile fanning out to wallet, bonuses, and bet history, so I avoid over-fetching and round trips. The tradeoff I'm explicit about is that HTTP and CDN caching aren't free anymore and one endpoint hides per-route metrics, so I invest in field-level caching and per-resolver tracing. I stay on REST for simple cacheable resource traffic and webhooks, and gRPC for hot internal service-to-service calls."

---

### ❓ **Question 2: Explain queries vs mutations vs subscriptions. How would you implement live leaderboard updates to 100k clients?**

🎯 All three are operations defined on root types, but they have different execution semantics:

- **Query** — reads. Top-level and sibling fields execute **concurrently** (they're independent). Should be side-effect free.
- **Mutation** — writes. Top-level fields execute **serially, in declared order**, so `placeBet` then `settleBet` can't interleave. Note the guarantee is only at the top level — *nested* fields under a mutation still resolve concurrently. The return type usually echoes the changed entity so the client can update its cache.
- **Subscription** — a long-lived stream. The client opens one (over WebSockets, the modern `graphql-ws` protocol — the legacy `subscriptions-transport-ws` is unmaintained), and the server **pushes** a payload every time an event fires.

```graphql
type Subscription {
  leaderboardUpdated(tournamentId: ID!): LeaderboardSnapshot!
}
```

A subscription resolver returns an **async iterator**, not a value. In NestJS/Apollo you commonly back it with a `PubSub`:

```ts
@Subscription(() => LeaderboardSnapshot, {
  filter: (payload, vars) =>
    payload.leaderboardUpdated.tournamentId === vars.tournamentId,
})
leaderboardUpdated(@Args('tournamentId') id: string) {
  return this.pubSub.asyncIterator('LEADERBOARD');
}
// elsewhere, when scores change:
pubSub.publish('LEADERBOARD', { leaderboardUpdated: snapshot });
```

One subtlety: that `filter` runs **per connected client per event**, so a single global `LEADERBOARD` channel makes every instance filter 100k times per tick. At scale I shard the channel by tournament (`LEADERBOARD:42`) so a client only wakes up for events it actually wants.

**Scaling to 100k clients** — the naive in-memory `PubSub` only works on one process. The real architecture:

```
Score event ─► Redis ZSET (ZADD)  ──► compute top-N (ZREVRANGE)
                       │
                       ▼
            Redis Pub/Sub channel "LB:tourn:42"
              │            │            │
          ECS task A   ECS task B   ECS task C   (graphql-ws subscription servers)
              │            │            │
          25k WS       40k WS       35k WS  clients
```

Key decisions for 100k:
1. **Don't push per-event to every client** — that's a fan-out storm. Throttle/coalesce: recompute the top-N on a fixed tick (~250ms–1s) and broadcast a **diff or snapshot**, not every individual score change. The score *writes* still hit Redis in real time; only the *broadcast* is ticked.
2. **Use `graphql-redis-subscriptions`** (or, better at this scale, a plain Redis Pub/Sub fan-out under the WS layer) so any ECS task can publish and all tasks fan out to their local sockets — horizontal scale behind an ALB.
3. **WebSocket connection affinity, not sticky HTTP.** A WS connection lives on one task for its lifetime, so the ALB's per-connection routing handles it; I don't rely on sticky cookies. What I *do* plan for is **reconnect storms** — if a task dies, ~35k clients reconnect at once, so I add jittered backoff on the client and ensure tasks can absorb the thundering herd (and surface current state on connect so a missed tick self-heals).
4. **Most clients only care about the top 50 + their own rank.** Broadcast the shared top-N to everyone (one payload, fanned out); compute each player's personal rank lazily via `ZREVRANK` on demand or on a slower cadence — never broadcast 100k individualized payloads.
5. **Bound memory and slow consumers.** 100k sockets is real heap and file-descriptor pressure; I cap per-connection send buffers and drop/disconnect slow clients rather than letting backpressure blow up a task.

🎮 **Gaming angle:** Leaderboards are read-heavy and update-bursty. Redis sorted sets give O(log N) rank updates (`ZADD`) and O(log N + M) top-M reads (`ZREVRANGE`); the GraphQL subscription is just the *delivery* mechanism. Coalescing the broadcast to a fixed tick (say 4/sec) is what keeps 100k sockets alive — a human can't perceive faster than that anyway, and it decouples broadcast cost from raw event rate, so a score spike during a big tournament finish doesn't melt the fleet.

🗣️ **Say this:** "Queries read with fields running concurrently; mutations write with top-level fields running serially so ordering is guaranteed; subscriptions are long-lived WebSocket streams over graphql-ws where the resolver returns an async iterator and the server pushes on events. For a 100k-client leaderboard the key insight is to decouple broadcast cost from event rate: scores still hit a Redis sorted set in real time, but I recompute the top-N on a fixed tick like 4 times a second and broadcast that one snapshot on a per-tournament Redis Pub/Sub channel, and each ECS instance fans it out to its local sockets. Personal rank I compute lazily with ZREVRANK rather than broadcasting 100k individual payloads. Then I engineer for the failure modes — reconnect storms with jittered backoff, slow-consumer backpressure, and self-healing state on connect."

---

### ❓ **Question 3: A query like `players { wallet { balance } }` for 100 players is hammering MongoDB with hundreds of queries. What's happening and how do you fix it?**

🎯 This is the **N+1 problem**, the single most important GraphQL performance issue. The resolver chain runs the `wallet` resolver **once per player**, independently:

```
players resolver         -> 1 query  (returns 100 players)
wallet resolver x100      -> 100 queries (one per player)   ← N+1
-----------------------------------------------------------
total: 101 round trips to Mongo
```

GraphQL has no built-in batching — each field resolves in isolation, so it can't see that you're about to ask for 100 wallets.

**Fix: DataLoader.** It does two things — **batching** (collect all the keys requested within one tick of the event loop, then make a single query) and **per-request memoization** (same key asked twice → resolved once). Mechanically it works by deferring: each `.load()` registers the key and returns a promise, and DataLoader schedules the batch function on the next tick (via `process.nextTick`), so every `load` issued in the current tick is collected before the DB is touched.

```ts
// one loader instance PER REQUEST (lives in GraphQL context)
const walletLoader = new DataLoader<string, Wallet | null>(async (playerIds) => {
  // playerIds = ['1','2',...,'100'] collected in one tick
  const wallets = await walletModel.find({ playerId: { $in: playerIds } });
  const byId = new Map(wallets.map(w => [w.playerId, w]));
  // MUST return results in the SAME ORDER as input keys, one slot per key
  return playerIds.map(id => byId.get(id) ?? null);
});

// resolver becomes:
@ResolveField()
wallet(@Parent() player, @Context() ctx) {
  return ctx.walletLoader.load(player.id);   // 100 .load() calls -> 1 $in query
}
```

```
100 x walletLoader.load(id)
      │  (batched within one event-loop tick)
      ▼
1 query: find({ playerId: { $in: [...100 ids] } })
```

So 101 queries collapses to **2**. Non-negotiable rules:
1. **New loader per request** — never share across requests, or stale/cross-user cached data leaks (a serious security risk in a multi-tenant gaming app). In NestJS this means the loader lives in request-scoped context, not a singleton provider.
2. **The batch function must return results in the exact order of the input keys**, one slot per key (`null` for misses), or you'll mismatch wallets to players.
3. **Cache the resolved promise, not just the value** — and be careful with errors: by default a thrown batch result is cached for that key, so for transient failures I clear the key (`loader.clear(id)`) so a retry isn't poisoned.

DataLoader fixes round-trip *count*, not query *cost* — a `$in` over 10k keys is still one heavy query, so for very wide fan-outs I also chunk the batch or push the aggregation into Mongo.

🎮 **Gaming angle:** On a live tournament page resolving 500 players each with wallet + recent bets + avatar, naive resolvers could fire 1500+ Mongo queries per request and exhaust the connection pool under concurrency — exactly when traffic peaks. DataLoader turns that into a handful of `$in` batches. For truly hot reference data (game/odds metadata, bonus configs) I'd layer a **Redis cache inside the loader's batch function** — check Redis for the batch, `$in` only the misses — so even the batched query is usually skipped.

🗣️ **Say this:** "That's the N+1 problem — the wallet resolver runs once per player because GraphQL resolves each field independently, so 100 players means 100 wallet queries plus the original. The fix is DataLoader: each request gets its own loader, the resolver calls `load(playerId)`, and DataLoader defers to the next event-loop tick to collect all the IDs, then fires a single `find` with `$in` and maps results back in key order. That collapses 101 queries to two. The rules I enforce: a fresh request-scoped loader so I never leak one user's cached data to another, returning the batch in input-key order, and clearing keys on transient errors so a poisoned cache doesn't stick. DataLoader fixes round-trip count, not query cost, so for very wide fan-outs I chunk the `$in` or aggregate in Mongo, and for hot reference data I check Redis inside the batch function first."

---

### ❓ **Question 4: How do you do auth and authorization in GraphQL? There's no per-route middleware like REST.**

🎯 Right — there's one endpoint, so you can't gate by URL. Auth happens in **layers**:

**1. Authentication (who are you) — at the transport/context level.** Validate the JWT once when the request arrives and put the user on the **context**, which every resolver receives:

```ts
// context factory (runs once per request)
context: ({ req }) => {
  const user = verifyJwt(req.headers.authorization); // throws -> UNAUTHENTICATED
  return { user, loaders: makeLoaders() };
}
```

For **subscriptions** auth happens at the **WebSocket connection** (`graphql-ws`'s `onConnect` / `connectionParams`), not per message — verify the token when the socket opens and stash the user on the connection context. One thing I watch: a WS connection can outlive its token's expiry. For long-lived sockets I either set a max connection lifetime forcing re-auth, or re-validate on sensitive operations, so a revoked/expired token can't ride an open socket forever.

**2. Authorization (what can you do) — per field/resolver.** In NestJS you use **Guards** on resolvers, exactly like controllers:

```ts
@UseGuards(GqlAuthGuard, RolesGuard)
@Roles('admin')
@Mutation(() => Boolean)
adjustWalletBalance(@Args('playerId') id, @Args('amount') amt) { ... }
```

The Gql guard pulls the request out of the GraphQL execution context (`GqlExecutionContext.create(ctx).getContext()`).

**3. Field-level / object-level authz.** Some fields are sensitive even within an allowed query — e.g., a player can see their *own* `wallet.balance` but not another player's. Enforce that **inside the resolver** by comparing `context.user.id` to the resource owner, or with a field-level authz directive/plugin. Never trust that the client "won't ask."

```
JWT verify (context)  ──► coarse: are you logged in?
Guard on resolver     ──► role: can you call adjustWalletBalance?
In-resolver check     ──► ownership: is this YOUR wallet?
```

**Pitfall — leaking via the graph:** Authz must consider the *path*, not just the field. `me { wallet { balance } }` is fine, but `tournament { players { wallet { balance } } }` could expose every player's balance through a nested path. So I anchor ownership checks to the resource and `context.user`, not to the entry point — the `wallet.balance` resolver itself must verify the caller owns that wallet regardless of how the query reached it. Pair that with **query depth/complexity limits** to stop abusive nesting.

**One senior caveat on errors:** an authz failure thrown deep in the graph can still **leak existence** — the `errors[]` path tells the attacker the node exists. For sensitive resources I prefer returning `null` (and treating the field as nullable) over throwing, so "forbidden" and "doesn't exist" look identical to a probe.

🎮 **Gaming angle:** Wallet and bet-settlement mutations are financial operations — I guard them with role + ownership checks and treat the resolver as the trust boundary, never the client. I also rate-limit at the resolver level (see anti-cheat below) because a single GraphQL endpoint makes it easy to spam expensive nested queries, and I make sure a forbidden wallet read returns null rather than confirming another player's account exists.

🗣️ **Say this:** "Because there's one endpoint, I do auth in layers. Authentication happens once in the context factory — I verify the JWT and attach the user to context, and for subscriptions I do it at the WebSocket connect handshake, with a max socket lifetime so a long-lived connection can't outlive token expiry. Authorization is per resolver: in NestJS I use Guards with role decorators just like controllers, and for ownership — a player reading their own wallet — I check `context.user.id` against the resource owner inside the resolver itself, not the entry point, because a sensitive field can be reached through a nested path like tournament-players-wallet. I add depth and complexity limits to stop abusive nesting, and for sensitive nodes I return null instead of throwing so a forbidden read doesn't even confirm the resource exists."

---

### ❓ **Question 5: How do you handle pagination in GraphQL? Why cursor-based over offset for our feeds?**

🎯 Two common styles:

**Offset/limit** — `bets(offset: 200, limit: 20)`. Simple, but on high-write data it's **unstable and slow**: if new bets land while a user pages, rows shift and they see duplicates or skips; and `skip(200)` in Mongo still scans and discards those 200 docs (the cost grows with depth — deep offsets are a classic latency cliff).

**Cursor-based (Relay Connections)** — you page relative to an opaque **cursor** (an encoded sort key like `_id` or a timestamp), not a numeric offset. This is the standard for feeds/infinite scroll. The Relay shape:

```graphql
type BetConnection {
  edges: [BetEdge!]!
  pageInfo: PageInfo!
  totalCount: Int        # optional — see caveat below
}
type BetEdge { node: Bet!  cursor: String! }
type PageInfo {
  endCursor: String
  hasNextPage: Boolean!
}

# query
bets(first: 20, after: "b3BhcXVl") { edges { node { id stake } cursor } pageInfo { endCursor hasNextPage } }
```

Server-side, `after` decodes to a sort key and you query **`_id > cursor` limit 21** (fetch one extra to compute `hasNextPage`):

```ts
const decoded = decodeCursor(after);              // e.g. an ObjectId
const docs = await betModel.find(
  decoded ? { _id: { $gt: decoded } } : {}
).sort({ _id: 1 }).limit(first + 1);

const hasNextPage = docs.length > first;
const page = docs.slice(0, first);
return {
  edges: page.map(d => ({ node: d, cursor: encode(d._id) })),
  pageInfo: { endCursor: encode(page.at(-1)?._id), hasNextPage },
};
```

```
offset:  skip(200).limit(20)   -> scans 200 docs, shifts when data grows
cursor:  find(_id > X).limit(20) -> seeks the index, stable under inserts
```

Cursor pagination is **O(log n)** to locate the start via an index range scan and **stable** because the cursor pins an absolute position in the sort order, not a count that moving data invalidates.

Two senior caveats:
- **The cursor must encode the full sort key.** If I sort by `score desc` (not `_id`), the cursor needs `(score, _id)` as a tiebreaker and a compound index `{score:-1, _id:-1}`, or rows with equal scores get skipped or duplicated. A bare `_id` cursor only works when `_id` *is* the sort order.
- **`totalCount` is the expensive part.** Relay clients often want it, but a `countDocuments` on a huge high-write collection is costly and instantly stale — I either omit it, serve an approximate count, or cache it, rather than counting on every page.

🎮 **Gaming angle:** A player's live bet feed and the global transaction ledger are extremely high-write. Offset pagination there would constantly skip/duplicate rows as new bets stream in, and deep offsets would scan huge ranges right when the collection is hottest. Cursor pagination on an indexed `_id`/timestamp keeps "load more" correct and fast even while thousands of writes per second hit the collection — and for a *leaderboard* I'd cursor on the `(score, playerId)` compound key, not `_id`, since the sort is by score.

🗣️ **Say this:** "I default to cursor-based, Relay-style connections — edges with nodes and cursors plus pageInfo with endCursor and hasNextPage. The client pages with `first` and `after`, where the cursor is an opaque encoded sort key, and the server does an index range scan — `_id` greater-than the cursor — fetching one extra row to know if there's a next page. I avoid offset/limit on high-write data because `skip` scans and discards rows so deep pages get slow, and pages shift under inserts so users see duplicates or gaps. The detail people miss is that the cursor has to encode the *whole* sort key — if I'm sorting a leaderboard by score I cursor on score plus a tiebreaker with a compound index, not just `_id` — and I treat totalCount as expensive, so I usually omit or approximate it rather than counting every page."

---

### ❓ **Question 6: How do you handle errors in GraphQL, and how do you rate-limit a single-endpoint API for anti-cheat?**

🎯 **Errors.** GraphQL transport returns HTTP 200 for a valid request (a malformed/invalid query can be 400); errors live in a top-level **`errors`** array alongside `data`, and partial success is possible — `data` can hold the fields that resolved while `errors` lists the ones that threw. One nullability gotcha: if a resolver for a **non-null** field throws, the null **propagates up to the nearest nullable parent**, so a single failed `wallet!` can null out the whole `player` — I keep financially-sensitive fields nullable on purpose so one failure doesn't blank an entire response.

```json
{
  "data": { "player": { "username": "neo", "wallet": null } },
  "errors": [{
    "message": "Insufficient permissions",
    "path": ["player","wallet"],
    "extensions": { "code": "FORBIDDEN" }
  }]
}
```

Best practices:
- Put a **machine-readable `code`** in `extensions` (`UNAUTHENTICATED`, `FORBIDDEN`, `BAD_USER_INPUT`, `INTERNAL`) — clients branch on the code, not the message string.
- **Never leak internals** (stack traces, Mongo errors) to clients; log them server-side with a correlation/trace ID, return a sanitized message. Mask in production via the Apollo `formatError`/`formatResponse` hook.
- For **expected domain outcomes** (e.g., "insufficient funds"), model them as part of the schema — a **union/result type** — rather than throwing, so they're typed and the client is forced to handle them:

```graphql
union PlaceBetResult = BetPlaced | InsufficientFunds | BetLimitExceeded
```

That keeps real "errors" (auth, bugs) in the `errors` array and **business outcomes in `data`** where they belong — and crucially, a typed result is part of the contract, so a client can't silently ignore "bet limit exceeded" the way it might overlook an `errors[]` entry.

**Rate limiting / anti-cheat.** One endpoint means per-route limits don't apply, and not all queries cost the same. Defenses, in order of leverage:
1. **Query complexity / depth limiting** — assign a cost to fields and reject queries over a budget *before execution* (`graphql-query-complexity` + a depth limit). This is the highest-leverage control: it stops a single deep nested query from doing the damage of thousands of requests, and it runs in validation so no DB work happens.
2. **Per-user/IP rate limits in a guard**, backed by Redis (sliding-window or token-bucket via an atomic Lua script so the check-and-increment can't race) — keyed off `context.user.id`, falling back to IP for unauthenticated traffic. Reuses our existing Redis rate-limiter.
3. **Operation-level limits** for sensitive mutations — cap `placeBet` to N/sec per player to blunt scripted spam, and enforce **idempotency keys** so a retried/duplicate mutation can't double-fire (the wallet path must be idempotent regardless).
4. **Persisted queries (allowlist)** — in production, only accept a known set of hashed operations, which kills arbitrary/expensive ad-hoc queries outright and also shrinks payloads.
5. **Disable introspection in production** so attackers can't trivially map the schema (defense-in-depth, not a real barrier on its own — persisted queries are the stronger control).

🎮 **Gaming angle:** Bots will hammer `placeBet`/`claimReward` and probe expensive nested queries to scrape data or exploit race conditions around settlement. Complexity limits kill the "one query = 10k DB reads" attack at validation time; an atomic Redis sliding-window guard on `context.user.id` throttles rapid-fire bets without a race; and modeling `InsufficientFunds`/`BetLimitExceeded` as typed results — plus idempotency keys on the wallet mutation so a double-submit can't double-spend — makes the financial path predictable and abuse-resistant rather than relying on thrown errors. In a real-money context, persisted queries are how I'd lock the production surface down to exactly the operations our own clients ship.

🗣️ **Say this:** "GraphQL returns 200 with errors in a top-level errors array and you can get partial data, and I'm careful that a thrown non-null field propagates null up to the nearest nullable parent — so I keep sensitive fields like wallet nullable so one failure doesn't blank the whole response. I always set a machine-readable code in extensions, never leak stack traces or DB errors — I mask them in formatError and log with a trace ID — and for expected business outcomes like insufficient funds I model a union result type so they're typed in data, not errors, and the client is forced to handle them. For rate-limiting a single endpoint the highest-leverage control is query complexity and depth limits that reject at validation before any DB work, then a Redis sliding-window guard keyed on user ID using an atomic Lua script so the increment can't race, then tighter per-operation caps and idempotency keys on placeBet so a double-submit can't double-spend. In production I'd lock the surface with persisted queries and turn off introspection."

---

### ❓ **Question 7: We're splitting into microservices — players, wallet, betting. How do you give clients one unified GraphQL API across them? Explain Apollo Federation.**

🎯 You don't want one giant monolithic schema that every team edits, and you don't want clients calling three separate GraphQL servers and stitching results. **Apollo Federation** lets each service own a **subgraph**, and a **router/gateway** composes them into one **supergraph** that clients query as a single API. (Federation 2 composition happens at build/deploy time — the supergraph schema is composed and validated by a tool like `rover`, then handed to the router; the router doesn't introspect services at runtime.)

```
                  ┌─────────────────────────┐
   client ──────► │   Apollo Router/Gateway │  (one endpoint)
                  └───────────┬─────────────┘
            ┌─────────────────┼──────────────────┐
            ▼                 ▼                  ▼
     players subgraph   wallet subgraph    betting subgraph
        owns Player       owns Wallet         owns Bet
```

Each subgraph defines its slice and uses federation directives. The killer feature is **entity resolution** — a type can be **defined in one subgraph and extended in another**, joined by a key:

```graphql
# players subgraph — owns the Player entity
type Player @key(fields: "id") {
  id: ID!
  username: String!
}

# wallet subgraph — contributes fields to the same Player by its key
type Player @key(fields: "id") {
  id: ID!                  # @key in Fed 2; @external in classic Fed 1
  wallet: Wallet!          # wallet service adds this field
}
```

When a client asks `player { username wallet { balance } }`, the **router builds a query plan**:

```
1. ask players subgraph   -> Player { id, username }
2. take that id, ask wallet subgraph via _entities/__resolveReference(id) -> wallet
3. merge into one response
```

The wallet subgraph implements a **reference resolver** (`__resolveReference`) that turns a `{ id }` representation into its part of the `Player`. The router figures out the fan-out and join automatically — clients see one coherent graph.

**Why Federation here:**
- **Team autonomy** — the wallet team ships wallet schema independently; no central schema bottleneck or shared deploy.
- **Separation of concerns** — financial wallet logic stays in its own service/repo/security/compliance boundary.
- **Composition is validated** — schema checks (`rover subgraph check`) catch breaking changes *before* they reach the supergraph, so one team can't break another's clients.

**Trade-offs I'd name:**
- The router adds a **network hop and query-planning cost**, and a cross-subgraph query is now a distributed call with **partial-failure** semantics — if the wallet subgraph is down, I need the router to return the player with a `wallet` error rather than fail the whole query.
- The **entity-fetch step is itself an N+1 risk** — the router batches representations into one `_entities` call, but inside the subgraph's reference resolver I still need **DataLoader** so 100 player IDs become one `$in`, not 100 lookups.
- It's **operational weight**: a router tier to run, scale, and observe. For a small system, schema-stitching or a modular monolith schema may be enough — I'd reach for Federation when team and domain boundaries (especially the financial one) justify it.

🎮 **Gaming angle:** The **wallet/betting services are the financial, high-compliance core** — I keep them as isolated subgraphs with their own scaling, audit, and access controls, while the players/profile service evolves fast and independently. Clients still get one query like `tournament { players { username wallet { balance } } }`, and the router fans out across subgraphs — but the money logic, audit trail, and PCI/compliance boundary never leak out of the wallet service. And because that query can fan out to thousands of entity fetches, DataLoader inside the wallet subgraph is what stops the federation join from becoming an N+1 storm on the most sensitive collection we own.

🗣️ **Say this:** "I'd use Apollo Federation. Each service owns a subgraph — players, wallet, betting — and an Apollo Router composes them at deploy time into one supergraph the client queries as a single endpoint. The key mechanism is entity resolution: a type like Player is defined in the players subgraph with an `@key` on id, and the wallet subgraph contributes a wallet field to that same Player. When a query spans both, the router builds a query plan — fetch the player, take its id, call the wallet subgraph's reference resolver, and merge. That gives each team schema autonomy and keeps the financial wallet logic in its own compliance boundary, with composition checks catching breaking changes before they ship. The tradeoffs I'd flag: an extra hop with distributed partial-failure handling, and the entity fetch is itself an N+1 risk so I run DataLoader inside each subgraph. For a smaller system I'd consider a modular monolith schema before taking on a router tier — I reach for Federation when the team and financial boundaries justify it."

---

I'll review and polish this MongoDB section. Let me work through it carefully, checking technical accuracy and strengthening the senior-level depth and gaming angle.

A few technical issues I'm fixing: the in-memory sort limit is 100 MB (not 32 MB — that was the old pre-3.4/legacy figure, and it's the same 100 MB aggregation stage limit); covered queries can't use the `_id` exclusion trick if `_id` is needed but the key correction is around multikey and the projection; and a few sharpening points on transactions, `j: true` semantics, and resharding.

## 🍃 MongoDB at Scale

---

❓ **Question:** A player's profile has core identity, a wallet, and a transaction history that grows unbounded. Walk me through how you'd model this in MongoDB — what do you embed, what do you reference, and why?

🎯 The rule of thumb is **"data that is accessed together should be stored together,"** moderated by three constraints: cardinality, unboundedness, and update contention.

```
player {
  _id, username, country, level,        // core identity — embed
  wallet: { balance, currency, version } // 1:1, read on every action — embed
}
// transactions: 1:N UNBOUNDED, high-write → SEPARATE collection, reference playerId
tx { _id, playerId, type, amount, balanceAfter, ts }
```

Decision logic:
- **Embed** when the relationship is 1:1 or 1:few, bounded, and read together. The wallet is read on every bet/payout, so embedding it avoids a join and lets you update balance atomically with a single-document write.
- **Reference** when the child set is unbounded or high-churn. Transaction history grows forever — embedding it would blow past the 16 MB document limit and rewrite the whole player doc on every insert. A separate collection keeps writes append-only and independently indexable.
- The **anti-pattern** is the "massive arrays" trap: an unbounded embedded array balloons the document, and because WiredTiger rewrites the *entire* document on any update, every insert gets more expensive as the array grows — plus the array's index becomes multikey and bloats. If a 1:N is unbounded, reference it. If it's bounded but you want recent items hot (e.g., last 20 bets for a profile UI), use the **subset pattern**: embed the recent few, reference the full history.

🗣️ **Say this:** My default is to embed data that's read together and reference data that grows unbounded. Player identity and the wallet are 1:1 and touched on every action, so I embed them — that gives me single-document atomic updates on balance without a transaction. Transaction history grows forever and is write-heavy, so it goes in its own collection keyed by player ID, append-only. The trap I avoid is unbounded embedded arrays — they hit the 16 MB doc limit and, because Mongo rewrites the whole document on any update, every insert gets slower as the array grows. When I need recent items for a UI, I use the subset pattern: embed the last N, reference the rest.

🎮 **Gaming angle:** Embedding the wallet means a bet settle is one `findOneAndUpdate` with an atomic `$inc` — no cross-document transaction on the hot path, which is what keeps p99 low under thousands of concurrent bets. The transaction-history collection then doubles as the immutable audit ledger that compliance and reconciliation read from, fully decoupled from the latency-sensitive write.

---

❓ **Question:** Your bet-history query is `{ playerId, gameId } sort by ts desc`, and it's slow. How do you design the index, and what's the rule that decides field order?

🎯 The **ESR rule** dictates compound index field order: **Equality, then Sort, then Range.**

```
Query: find({ playerId: X, gameId: Y, ts: { $gte: T } }).sort({ ts: -1 })
                  Equality      Equality     Range          Sort

Index: { playerId: 1, gameId: 1, ts: -1 }
          └── E ──┘   └── E ──┘  └─ S+R ─┘
```

- **Equality** fields first — they narrow the scan to a contiguous range in the B-tree.
- **Sort** next — if the index order matches the sort, Mongo streams results already-ordered and skips an in-memory blocking `SORT` stage (capped at 100 MB; beyond that it errors unless you `allowDiskUse`, and either way it stalls until the full set is buffered).
- **Range** last — a range scans many index entries, so anything after it in the key can't be relied on for ordering.

Here `ts` serves double duty as both sort and range. Because Mongo can scan an index **backward**, a single `{ ts: -1 }` key satisfies both `ORDER BY ts DESC` and the `ts >= T` range in one bounded scan — the range trims the scan endpoints while the index order delivers the sort for free. Verify with `.explain("executionStats")`: you want `IXSCAN`, `totalKeysExamined ≈ totalDocsExamined ≈ nReturned`, and **no `SORT` stage**.

🗣️ **Say this:** I order compound index fields by ESR — Equality, Sort, Range. Equality fields go first because they pin the scan to a contiguous slice of the B-tree. The sort field goes next so Mongo returns rows already ordered and skips the blocking in-memory sort, which caps at 100 MB. Range predicates go last because a range fans the scan out and breaks ordering for anything after it. For this query that's `{ playerId: 1, gameId: 1, ts: -1 }`, and the nice thing is `ts` doubles as sort and range — one descending key handles both since Mongo can scan the index in either direction. Then I confirm with explain that I get an index scan, keys examined roughly equals docs returned, and no blocking SORT stage.

---

❓ **Question:** Your leaderboard reads top-100 scores constantly. How do you make that query as cheap as possible, and how do covered queries fit in?

🎯 A **covered query** is one where every field the query needs — filter, sort, and projection — lives in the index, so MongoDB serves it from the index alone and never touches the documents (no `FETCH` stage). It's the cheapest possible read.

```
Index:  { season: 1, score: -1, playerId: 1, username: 1 }
Query:  find({ season: 5 }, { _id: 0, playerId: 1, username: 1, score: 1 })
            .sort({ score: -1 }).limit(100)
        → IXSCAN only, no FETCH  ✅ covered
```

Requirements for coverage:
- All filter, sort, and projected fields are in the index.
- You must **exclude `_id`** (`_id: 0`) unless `_id` is itself part of the index, since it's projected by default and would otherwise force a fetch.
- No indexed field used can be an array — a **multikey index cannot cover** a query.

For a hot leaderboard you'd pair this with a **partial index** so the index only holds active seasons:

```
{ partialFilterExpression: { season: { $gte: currentSeason } } }
```

That keeps the index small and resident in RAM. But be honest about the limit of the covered query here: it's great for *page-one* reads, but it still scans and sorts up to 100 entries per request, and a write-heavy live leaderboard would constantly invalidate the cached index pages. So for a true 100k-reads/sec leaderboard the index-covered query is your **durable source of truth and rebuild path**, and you front the actual hot path with **Redis Sorted Sets** (`ZADD`/`ZREVRANGE`) — Mongo for persistence and recovery, Redis for the per-millisecond reads and live rank.

🗣️ **Say this:** A covered query is served entirely from the index — filter, sort, and projection fields all live in the index, so there's no document fetch. For a top-100 leaderboard I build a compound index on season and score descending, include the fields I project, and exclude `_id` so coverage isn't broken — and I make sure none of those fields is an array, because a multikey index can't cover. I also make it a partial index over active seasons to keep it small and RAM-resident. But at 100k reads a second I wouldn't hammer Mongo per request — I'd serve the live leaderboard from a Redis sorted set and treat the covered Mongo query as the durable source of truth and the path I rebuild the sorted set from after a failover.

🎮 **Gaming angle:** Redis `ZINCRBY` updates a player's score in O(log N) and `ZREVRANGE 0 99` returns the top 100 in O(log N + 100); critically, `ZREVRANK` gives any single player their own rank in O(log N), which is the query that's brutal in Mongo (it needs a count of everyone ahead of them) but trivial in a sorted set. Mongo holds the authoritative history so you can rebuild the sorted set after a failover.

---

❓ **Question:** Build me a real-time aggregation: per-game total wagered, payout, and house margin over the last hour, plus a breakdown by bet type in one round trip. Which stages, and what are the performance traps?

🎯 This is `$match` early, `$group` for rollups, and `$facet` for the multi-view in one pass.

```js
db.bets.aggregate([
  // 1. MATCH FIRST — uses index, shrinks the pipeline immediately
  { $match: { ts: { $gte: oneHourAgo }, status: "settled" } },

  { $facet: {
    byGame: [
      { $group: {
          _id: "$gameId",
          wagered: { $sum: "$stake" },
          payout:  { $sum: "$payout" },
      }},
      { $addFields: { margin: { $subtract: ["$wagered", "$payout"] } } },
      { $sort: { wagered: -1 } }
    ],
    byBetType: [
      { $group: { _id: "$betType", count: { $sum: 1 }, wagered: { $sum: "$stake" } } }
    ]
  }}
])
```

Key principles and traps:
- **`$match` and `$sort` must come before `$group`** so they can use indexes — once you `$group`, the output is synthetic and no index applies. The optimizer reorders some of this, but design for it explicitly rather than relying on it.
- **`$facet` short-circuits index use after it.** Everything inside a facet runs over the in-memory post-`$match` stream — no facet sub-pipeline can use an index. That's the point (one pass, many views) but it means the `$match` *must* be tight and indexed, because the whole matched set is materialized in memory for every facet.
- **`$lookup` is a left outer join** that runs effectively as a per-document query into the other collection. On a high-write hot path, prefer denormalizing the few fields you need (e.g., store `gameName` on the bet at write time) over `$lookup`. If you must join, the foreign field must be indexed or you get an O(n·m) blow-up.
- Aggregations enforce a **100 MB per-stage memory limit**; `allowDiskUse: true` lets big rollups spill, but on a real-time path you'd rather pre-aggregate than spill to disk.
- For dashboards refreshed every few seconds, run this on a **secondary** (`readPreference: secondary` / a dedicated analytics node tag) or against pre-rolled hourly buckets, so analytics never competes for IO and cache with the betting write path on the primary.

🗣️ **Say this:** I match first so the stage uses an index and shrinks the working set immediately, then group for the rollups. To get both the per-game and per-bet-type breakdowns in a single round trip, I wrap them in a `$facet` — each sub-pipeline sees the same matched input. The big traps: keep `$match` and `$sort` before `$group` so indexes still apply; remember that nothing inside a `$facet` can use an index, so the match has to be tight because that whole set sits in memory; avoid `$lookup` on hot paths because it's a per-doc join — I'd denormalize the game name onto the bet at write time instead; and watch the 100 MB per-stage limit. For a live dashboard I'd run this against a secondary or pre-aggregated hourly buckets so analytics never steals IO from the betting writes.

🎮 **Gaming angle:** Computing house margin live lets a risk engine trip a circuit breaker on a game that's bleeding money — auto-suspend the market before the loss compounds. Running it on a secondary means that calculation never adds latency or cache pressure to the bet-settlement write path on the primary, which is exactly where your real-money latency budget lives.

---

❓ **Question:** A player must never double-spend their wallet under concurrent bets. How do you guarantee that — and when do you actually need a multi-document transaction?

🎯 For the **single-wallet case you don't need a transaction at all** — you need an atomic conditional update. Single-document writes in MongoDB are always atomic.

```js
// Atomic compare-and-decrement: only succeeds if balance is sufficient
const res = await wallets.findOneAndUpdate(
  { playerId, balance: { $gte: stake } },   // guard lives in the FILTER
  { $inc: { balance: -stake }, $push: { recent: { betId, stake, ts } } },
  { returnDocument: "after" }
);
if (!res) throw new InsufficientFundsError(); // filter didn't match → rejected
```

The guard `balance: { $gte: stake }` is inside the query filter, so two concurrent bets can't both pass — Mongo takes a document-level write lock, and the second update re-evaluates the filter against the already-decremented balance and matches nothing. This is **optimistic-style concurrency enforced by the per-document write lock**: no read-then-write gap, no race. Add an **idempotency key** (unique index on `betId`, applied via the ledger insert or an `$addToSet`/upsert) so a retried or duplicated request can't double-charge — at-least-once delivery from your queue makes this mandatory, not optional.

You need a **multi-document transaction** only when an invariant spans documents/collections and all parts must commit together:

```
// Move funds between two players (P2P transfer / escrow)
session.startTransaction({ readConcern:"snapshot", writeConcern:{ w:"majority" } })
  debit  walletA  (with $gte guard)
  credit walletB
  insert ledger entry
commit  // all-or-nothing
```

Transactions on a replica set work but carry real cost: they hold locks for their duration, **abort on write conflict** (you must wrap them in a retry loop on `TransientTransactionError`), serialize through the primary, and hit a 60s default runtime ceiling. So the rule is: **model for single-document atomicity first; reach for a transaction only for genuine cross-document invariants** like transfers or escrow. A common senior move is to avoid the transaction entirely with a **two-phase / saga pattern over an append-only ledger** — record a "pending" debit, credit the other side, then mark settled — so even a multi-party transfer reduces to a sequence of idempotent single-document writes that you can reconcile, rather than a distributed lock on the hot path.

🗣️ **Say this:** For one wallet I never use a transaction — I use an atomic conditional update. I put the sufficient-balance check inside the query filter and decrement in the same operation, so it's a single-document write, which Mongo guarantees is atomic under a per-document lock. Two concurrent bets can't both win: the second re-evaluates the filter against the already-reduced balance and matches nothing. I add a unique index on the bet ID for idempotency, because my queue is at-least-once and a retry must not double-charge. I only reach for a multi-document transaction when the invariant spans documents — a player-to-player transfer that has to debit one wallet, credit another, and write a ledger entry atomically — and I handle its cost by retrying on transient write conflicts and keeping it short. At higher scale I'd often replace even that with a saga over an append-only ledger so every step stays a single idempotent write.

🎮 **Gaming angle:** Under thousands of concurrent bets, the `findOneAndUpdate` guard is what prevents negative balances without any global lock — it serializes only *per wallet*, so two different players' bets never block each other and the system scales horizontally with the player base. That per-document granularity is the whole reason this holds p99 during a goal rush when everyone bets at once.

---

❓ **Question:** You're scaling the bets collection past what one replica set can handle on writes. How do you shard it, and how do you choose the shard key?

🎯 Sharding distributes data across multiple replica sets (shards) by a **shard key**, with a router (`mongos`) directing each query. The key choice is the single most important and least reversible decision — a bad key creates **hot shards** or **scatter-gather queries**.

Three properties of a good shard key: **high cardinality, low frequency (even distribution), and non-monotonic** (so writes spread across shards instead of all hitting the shard that owns the current max range).

```
❌ { ts: 1 }            → monotonically increasing → ALL writes hit one shard
                          (the "hot shard" — defeats the purpose)
❌ { gameId: 1 }        → low cardinality, one popular game → hot shard + jumbo chunk
✅ { playerId: "hashed" } → even write distribution, but player-scoped
                            range queries broadcast to every shard
✅ { gameId: 1, playerId: 1 } (compound) → distributes, and routes queries
                                            that include gameId to one shard
```

The tension is **write distribution vs. query routing**:
- A **hashed** shard key spreads writes perfectly but destroys locality — range queries become scatter-gather (every shard is queried, then merged), and you can't do range-based chunk targeting at all.
- A **ranged/compound** key keeps related data co-located so targeted queries hit one shard, but you must ensure the leading field has enough cardinality to avoid **jumbo chunks** (a chunk that can't be split because too many docs share one key value).

For a bets collection I'd lean toward a **compound key like `{ gameId: 1, playerId: 1 }`** if most queries filter by game, or `{ playerId: 1, ts: 1 }` if queries are player-centric — the leading field gives routing, the trailing field gives spread within it. The golden rule: **the shard key (or a prefix of it) must appear in your most common queries**, otherwise every read is a broadcast. It's effectively permanent — live resharding exists since MongoDB 5.0 but rewrites the whole collection and is operationally heavy, so model it up front and load-test the distribution before you commit.

🗣️ **Say this:** Sharding splits the collection across shards by a shard key, with mongos routing queries, and the key is the call that's hardest to undo. I pick for three things: high cardinality, even distribution, and non-monotonic writes — a raw timestamp key is the classic mistake because it sends every new write to the one shard owning the latest range. The real tension is write spread versus query routing: a hashed key spreads writes perfectly but makes range queries scatter-gather and kills locality, while a compound ranged key co-locates related data so targeted reads hit one shard — as long as the leading field has enough cardinality to avoid jumbo chunks. For bets I'd use something like `{ gameId: 1, playerId: 1 }` so the key matches my common filters. The rule I anchor on: a prefix of the shard key has to be in your most frequent queries, or every read becomes a broadcast. Resharding is possible since 5.0 but it rewrites the collection, so I model and load-test it up front.

🎮 **Gaming angle:** A monotonic key on a live-betting collection is fatal — during a big match every bet carries a near-now timestamp, so 100% of writes pile onto the shard owning the latest range while the rest sit idle. A compound key led by event/game ID spreads a single popular match's load across the player dimension, so the hottest event in your book still fans out across the cluster instead of melting one shard.

---

❓ **Question:** Walk me through read concern, write concern, and replica-set guarantees. For a financial wallet write, what do you set, and what's the latency tradeoff?

🎯 **Write concern (`w`)** = how many nodes must acknowledge a write before it's confirmed to the client. **Read concern** = what consistency/durability guarantee a read returns. They're orthogonal — you set both.

```
Replica set:  PRIMARY ──async replication──► SECONDARY, SECONDARY

w: 1            → acked by primary only. Fast, but a failover BEFORE the
                  write replicates = data loss (rolled back on recovery).
w: "majority"   → acked by a majority of voting nodes. The write is on
                  enough nodes to survive a single-node failure / failover.
                  Slower — waits for a replication round trip.
j: true         → primary has flushed it to the on-disk journal (survives
                  a process/power crash on that node, independent of w).

readConcern:
  "local"      → whatever the node has now; may later be rolled back
  "majority"   → only data acked by a majority — guaranteed not rolled back
  "snapshot"   → consistent point-in-time view (used inside transactions)
  + readPreference "primary" avoids reading stale data off a lagging secondary
```

For a **financial wallet write the answer is `w: "majority"`, `j: true`.** The invariant is: a confirmed debit must never disappear. `j: true` protects against a *crash on one node*; `w: "majority"` protects against *losing the primary entirely* — with `w: 1`, if the primary acks then dies before replicating, that write rolls back on recovery and you've told the player they're charged, then silently un-charged them. You need both together: majority for failover survival, journaling so the nodes that have it haven't lost it to a power cut. Pair with `readConcern: "majority"` (read your own writes safely) so balance reads can't observe a value that may roll back.

The **tradeoff is latency**: `majority` adds a replication round trip — typically single-digit ms within an AZ, more across AZs — so you don't use it everywhere. The pattern is **tiered durability**, matching the cost of losing a write to its latency budget:

```
Wallet debit / payout        → w:"majority", j:true   (correctness > latency)
Chat message / presence      → w:1                     (latency > durability)
Analytics event firehose     → w:0  (fire-and-forget)  (volume > everything)
```

🗣️ **Say this:** Write concern is how many nodes must acknowledge before a write is confirmed; read concern is the consistency guarantee on the way out, and they're independent. For a wallet write I use `w: majority` with `j: true`, and read with read concern majority. Journaling protects me from a crash on one node; majority protects me from losing the primary — with `w: 1`, if the primary acks then dies before replicating, the write rolls back and I've charged a player then silently refunded them, which is unacceptable for money. Majority costs a replication round trip, so I tier it: majority and journaled for anything touching the wallet, `w: 1` for chat and presence, and fire-and-forget for the analytics firehose. I match the durability level to the cost of losing that specific write.

🎮 **Gaming angle:** Bet settlement and payouts get `w:"majority", j:true` because a lost payout is a real-money liability and an audit failure. Live position and presence updates that are superseded every 100ms get `w:1` — losing one frame is invisible to the player and the latency budget there is far tighter, so paying for a replication round trip would just add jitter to something that's about to be overwritten anyway.

---

❓ **Question:** Bets are slow in production at peak. Walk me through how you diagnose it — what tools, what you look for, and how you act.

🎯 A systematic top-down pass: **profiler → explain → index/schema fix**, then capacity.

**1. Find the slow operations** — the database profiler and slow query log:
```js
db.setProfilingLevel(1, { slowms: 100 })   // log ops slower than 100ms
db.system.profile.find().sort({ ts: -1 })  // inspect captured slow ops
```
Look at `millis`, `docsExamined`, `keysExamined`, `planSummary`. The red flags are **`COLLSCAN`** (full collection scan) and a ratio where `docsExamined ≫ nReturned` — you're reading far more than you return. (On a hot prod system, profiling has overhead — set a sane `slowms` and turn it off when done.)

**2. Diagnose the specific query** — `explain("executionStats")`:
```
Healthy:  IXSCAN, totalDocsExamined ≈ nReturned, no SORT stage
Sick:     COLLSCAN, or keysExamined ≫ nReturned (bad/unselective index),
          or in-memory SORT (sort field not in the index), or FETCH you
          could avoid (projection not covered)
```
The key health metric is the **examined-to-returned ratio** — close to 1:1 is ideal. A high ratio means the index isn't selective enough, has the wrong field order, or is missing.

**3. Act — fix follows symptom:**
- `COLLSCAN` → add an index following the **ESR rule** for that query shape.
- In-memory `SORT` → extend the index so it covers the sort field.
- `keysExamined ≫ nReturned` → leading index fields aren't selective; reorder by ESR or add an equality field up front.
- High `docsExamined` on an indexed query → make it a **covered query** by adding the projected fields to the index so you skip the FETCH.
- **Working set doesn't fit in RAM** → check cache pressure and eviction in `db.serverStatus().wiredTiger.cache` (bytes read into cache, dirty bytes, eviction rate). Fix with more RAM, a smaller working set (partial indexes, TTL to expire ephemeral data), or sharding to spread it.
- **Not a query at all?** Don't tunnel-vision the index. At peak the cause is often *lock/write contention on a hot document* (every bet hitting one popular market's wallet/odds doc), replication lag pushing secondaries behind, or connection-pool exhaustion from the app tier. `mongostat`/`mongotop` and the slow-op `lockStats` separate "slow query" from "contended hot document."

```js
// TTL index: auto-expire ephemeral data (sessions, live odds) → smaller working set
db.liveOdds.createIndex({ updatedAt: 1 }, { expireAfterSeconds: 3600 })
```

🗣️ **Say this:** I go top-down. First I turn on the profiler with a slowms threshold and pull the slowest ops from `system.profile`, looking for collection scans or a docs-examined-to-returned ratio way above one. Then I run explain with execution stats on the worst offenders — I want an index scan with examined roughly equal to returned and no blocking sort. From there the fix follows the symptom: a collection scan means a missing index, which I build by ESR; an in-memory sort means I extend the index to cover the sort field; a high examined ratio means my leading fields aren't selective. If the working set doesn't fit in RAM, I check WiredTiger cache eviction and shrink the set with partial and TTL indexes, or shard. But I don't assume it's always a query — at peak it's often write contention on one hot document, replication lag, or pool exhaustion, so I check mongostat and lock stats too before I reach for an index.

🎮 **Gaming angle:** Live odds and session state churn constantly; a TTL index that auto-expires them after an hour keeps the hot working set small enough to stay in RAM, which is what holds bet-settlement latency flat during a peak-traffic match. And the classic peak failure mode here isn't a missing index at all — it's thousands of bets contending on the *single* wallet or odds document for the one match everyone's watching, which is a sharding/modeling problem, not an indexing one.

---

This is a self-contained editorial task — no task list needed. I'll produce the polished Markdown directly.

## ⚡ Redis & BullMQ

---

❓ **Question:** Walk me through how you'd build a live leaderboard for a tournament with 500k concurrent players where ranks update on every score change and clients poll their rank constantly. What Redis structure, and what are the gotchas at this scale?

🎯 The answer is a **Sorted Set (ZSET)** — every member has a score, and Redis keeps them ordered for you. Score updates and rank lookups are `O(log N)`, which is what makes this viable at 500k members.

```
ZADD tournament:42 9800 user:1001       # update score (O(log N))
ZINCRBY tournament:42 150 user:1001     # atomic delta on a score event (O(log N))
ZREVRANK tournament:42 user:1001        # this player's rank, 0-based (O(log N))
ZREVRANGE tournament:42 0 9 WITHSCORES  # top 10 (O(log N + 10))
ZREVRANGE tournament:42 <r-2> <r+2> WITHSCORES  # "players near me" window (O(log N + M))
```

Gotchas at scale:
- **Tie-breaking — and watch the float.** Two players on the same score are then ordered lexicographically by member, which is arbitrary. The instinct is to encode a timestamp into the float score, but **Redis scores are IEEE-754 doubles — only ~53 bits / 15–16 significant digits of precision.** `score - timestampMs/1e13` silently collides once the score grows. The correct move is to pack into a single integer that fits in 53 bits: `packed = score * 2^21 + (MAX_TS - tsSeconds)` (or whatever bit split your score range allows), so higher score wins and, on a tie, the earlier timestamp wins — deterministically, no precision loss. If your score range is too large to pack, keep the ZSET on score alone and resolve ties in a second structure rather than corrupting the float.
- **Don't `ZRANGE 0 -1`.** Fetching the whole board is `O(N)` and will stall the event loop. Only ever read top-N plus a window around the player.
- **Hot key.** One ZSET = one key = one slot, so it lives entirely on one node in Redis Cluster — you cannot shard a single ZSET across the cluster. 500k writes/sec hammer that one node. Mitigate by sharding boards (per region/bracket) and merging top-N at read time, or buffering score deltas in-app and flushing aggregated `ZINCRBY`s every N ms — which also collapses many events per player into one write.
- **Polling kills you.** 500k clients polling rank is a self-inflicted DoS. Push rank changes over Socket.io/pub-sub, or cache the player's own rank with a short TTL so repeat polls hit a cached value, not a fresh `ZREVRANK`. Note the tradeoff: `ZREVRANK` is exact but recomputed per call; a cached rank is cheap but can lag the live board by the TTL — fine for "your rank" UI, not for prize-line decisions.
- **Persistence.** The ZSET is the live view; the DB is the source of truth for scores. On restart, rebuild from the DB (or AOF with `appendfsync everysec`, accepting up to ~1s of loss) — never treat Redis as the ledger.

🗣️ **Say this:** "Leaderboards are the textbook use case for Redis sorted sets — `ZADD`/`ZINCRBY` to update, `ZREVRANK`/`ZREVRANGE` for rank and top-N, all `O(log N)`. At 500k players the data structure isn't the problem, the operations are. For deterministic ties I pack score and an inverted timestamp into a single 53-bit integer score — I'm careful here because Redis scores are doubles, so a naive timestamp-in-the-decimals trick silently collides. I never scan the full set, only top-N plus a window around the player. I treat the single ZSET as a hot key that can't be sharded inside the cluster, so I split boards per bracket or buffer and flush aggregated deltas. And I never let clients poll rank directly — I push over sockets and cache each player's own rank with a short TTL. Redis is the live view; the DB stays the source of truth."

🎮 **Gaming angle:** "Players near me" (`ZREVRANGE` around your rank) and the live top-10 are the only two reads a tournament UI actually needs — both are cheap on a ZSET, and you almost never need full ordering. The pack-the-timestamp tie-break also matters for money: if rank decides a prize tier, "who hit the score first" has to be deterministic and auditable, not whatever order Redis happened to store members in.

---

❓ **Question:** A player's wallet is in Redis for speed. Under high concurrency — bonus credit, bet debit, and payout all firing at once — how do you guarantee no double-spend and no negative balance? Why isn't `GET` then `SET` enough?

🎯 `GET` then `SET` is a **read-modify-write race**: two requests both read balance 100, both subtract 30, both write 70 — you've lost a debit and let through a double-spend. The fix is **atomicity**, and for conditional logic (don't debit if balance < amount) `INCRBY`/`DECRBY` alone isn't enough — they're atomic but unconditional, so they'd happily go negative.

The correct tool is a **Lua script**, which Redis executes atomically — no other command interleaves while it runs:

```lua
-- KEYS[1] = wallet key, KEYS[2] = processed-txn set
-- ARGV[1] = debit amount, ARGV[2] = idempotency txn id, ARGV[3] = txn-set TTL (s)
-- returns new balance, or -1 if insufficient, or -2 if duplicate
if redis.call('SISMEMBER', KEYS[2], ARGV[2]) == 1 then
  return -2                              -- already processed this txn (idempotency)
end
local bal = tonumber(redis.call('GET', KEYS[1]) or '0')
local amt = tonumber(ARGV[1])
if bal < amt then return -1 end          -- reject: no negative balance
redis.call('DECRBY', KEYS[1], amt)
redis.call('SADD', KEYS[2], ARGV[2])     -- record txn id so retries are no-ops
redis.call('EXPIRE', KEYS[2], ARGV[3])   -- bound the idempotency set's growth
return bal - amt
```

Key points:
- **Check-and-debit is one atomic unit** — no interleaving, so no double-spend and never negative.
- **Idempotency set makes retries safe.** A network retry of the same `txnId` returns `-2` instead of debiting twice — critical when a client or queue retries a payout. Note the real-world caveat the naive version misses: an unbounded set per wallet grows forever, so I give it a TTL longer than any plausible retry window (or store processed ids in the DB transaction and use Redis only for the recent window). Also be honest about the TTL edge: a retry arriving *after* the id expires would re-debit, so the durable ledger — not Redis — is the final dedupe authority.
- **Integer money only.** Store balances in the smallest unit (cents/credits) as integers. `DECRBY` is integer-only, and you never want floating-point cents in a wallet anyway.
- **Single-threaded command execution** means the script is effectively a critical section without you holding an explicit lock. (Redis 6+ I/O threading only parallelizes network read/write, not command execution — atomicity is unchanged.)

**Senior caveat:** Redis isn't a durable financial ledger. Real money flow should be: write the authoritative transaction to MongoDB (or an outbox) first or in the same logical step, use Redis as the fast atomic balance guard, and reconcile. If a node fails over before the AOF fsync, an in-Redis-only debit can vanish — so the only durable record of a payout must never live solely in Redis.

🗣️ **Say this:** "`GET`/`SET` is a lost-update race — concurrent debits read the same balance and overwrite each other, which is a double-spend. `INCRBY` is atomic but unconditional, so it'll go negative. So I use a Lua script: Redis runs it atomically on its single command thread, and inside it I check balance, reject if insufficient, debit, and record the txn id in an idempotency set so retries are no-ops. That's no double-spend, no negative balance, and safe retries in one shot. I keep money as integers in the smallest unit, and I put a TTL on the idempotency set so it doesn't grow unbounded. The caveat I'd raise unprompted is durability — Redis isn't a ledger and can lose a write on failover, so the authoritative record goes to Mongo via an outbox, and Redis is the fast atomic guard in front of it, not the system of record."

🎮 **Gaming angle:** Bet placement at the instant a round closes is the classic thundering herd — thousands of debits on the same wallets in the same tick, plus a payout possibly hitting the same wallet. The Lua atomic guard is what keeps the house from paying out money that was never there, and what makes a "place bet" and a "settle win" on the same wallet serialize cleanly instead of clobbering each other.

---

❓ **Question:** You're running multiple instances of a payout worker on ECS Fargate. A scheduled job credits tournament winnings. How do you make sure only one instance runs that job, and what's wrong with a naive `SETNX` lock?

🎯 You need a **distributed lock** so the job runs once across all instances. The naive version:

```
SET payout:lock <uuid> NX PX 30000   # acquire only if absent, unique token, 30s expiry
... do work ...
DEL payout:lock                       # release
```

What's wrong with the naive version:
1. **No expiry → deadlock.** If you acquire without `PX` and the holder crashes, the lock is held forever. Always set an expiry.
2. **Unsafe release.** A plain `DEL` can delete *someone else's* lock: if your work overran the TTL, the lock expired, instance B acquired it, and your late `DEL` removes B's lock. Release must be a Lua compare-and-delete on the unique token:

```lua
if redis.call('GET', KEYS[1]) == ARGV[1]
then return redis.call('DEL', KEYS[1]) else return 0 end
```

3. **TTL vs work duration.** If the job can run longer than the TTL, two instances run concurrently. Either size the TTL safely above worst-case runtime, or run a **watchdog** that extends it while work is in progress (lock renewal) — and have the worker check it still holds the lock before each side-effecting step, because a clock pause / GC stall can expire the lock without the worker noticing.
4. **Single-node failure.** A lock on one Redis master isn't fault-tolerant — if that master fails over before replicating the lock key, the lock can be granted twice. **Redlock** acquires on a majority of N independent masters to address this; it's heavier and genuinely debated (Kleppmann's "How to do distributed locking" critique vs. antirez's reply turns on clock/timing assumptions). For money-correctness I don't rely on the lock for safety at all — I make the job **idempotent** so a double-run can't double-pay.

**Senior framing:** Locks reduce contention; idempotency guarantees correctness. A lock that depends on wall-clock TTLs can never be a *safety* mechanism under GC pauses, network partitions, and failover — only a *liveness/efficiency* one. So for payouts I do both: lock to avoid wasted concurrent work, and an idempotency key (per-winner txn id, as above) so even a split-brain double-run credits each winner exactly once. A monotonic fencing token is the textbook hardening, but a per-winner idempotency key gives the same end-state guarantee more simply here.

🗣️ **Say this:** "I'd use a Redis lock — `SET key token NX PX ttl` — but the naive form has three traps: no expiry deadlocks on crash; a plain `DEL` can delete another instance's lock, so release is a Lua compare-and-delete on a unique token; and if the job outlives the TTL you get two runners, so I add a watchdog to renew and re-check ownership before each side effect. On a single Redis node the lock isn't truly fault-tolerant — it can be granted twice across a failover — and Redlock spreads it across masters but is debated. So my real answer is that the lock is an optimization, not the guarantee. Any TTL-based lock is liveness, not safety, under GC pauses and partitions. The guarantee is idempotency: each payout carries a txn id, so even a split-brain double-run pays each winner exactly once."

🎮 **Gaming angle:** End-of-tournament payout fan-out is exactly where this bites — one scheduled trigger, thousands of credits. The Fargate failure mode is real: a task gets SIGTERM'd mid-job during a deploy or scale-in while another picks the work up. Lock to avoid the double work, idempotency keys so no winner is paid twice if the lock ever slips.

---

❓ **Question:** Anti-cheat: you need to rate-limit actions — say a player can't place more than 10 bets per second, and you want to throttle a bot spamming an endpoint. How do you implement rate limiting in Redis, and what's the difference between fixed-window and sliding-window/token-bucket?

🎯 **Fixed window** is the simplest — one counter per window, atomic increment:

```lua
-- atomic INCR + first-hit EXPIRE, so the key can't leak without a TTL
local n = redis.call('INCR', KEYS[1])     -- key embeds the window timestamp
if n == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end
return n                                    -- caller rejects if n > limit
```

I do the `INCR`+`EXPIRE` in one Lua call deliberately: if you `INCR` then `EXPIRE` as two commands, a crash between them leaves a counter with **no TTL** that never resets — the user is then locked out until manual intervention. The bigger limitation is **boundary bursting**: with a 1s window a player can fire 10 at 0.99s and 10 at 1.01s — 20 in ~20ms — because the counter resets on the boundary. Fine for coarse limits, bad for anti-cheat.

**Sliding window** smooths this. The precise Redis implementation is a **sorted set per user**, scored by timestamp, done atomically in Lua:

```lua
-- KEYS[1]=user key, ARGV[1]=now(ms), ARGV[2]=window(ms), ARGV[3]=limit, ARGV[4]=unique member
redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1] - ARGV[2])  -- drop old entries
local count = redis.call('ZCARD', KEYS[1])
if count >= tonumber(ARGV[3]) then return 0 end                -- reject
redis.call('ZADD', KEYS[1], ARGV[1], ARGV[4])                  -- unique member, not ts (avoids same-ms collision)
redis.call('PEXPIRE', KEYS[1], ARGV[2])
return 1                                                        -- allow
```

One subtlety the naive version gets wrong: using the timestamp as the ZSET *member* means two actions in the same millisecond overwrite each other and undercount — use a unique member (e.g. `ts:counter` or a request id). Cost-wise this is `O(log N)` per action and stores one entry per action in the window, so it's memory-heavier than a counter.

**Token bucket** is the other common choice — store `tokens` + `lastRefill`, refill at a steady rate, each action costs a token. It allows controlled bursts (good UX) while capping sustained rate, and is O(1) and cheaper than a per-action ZSET. Also Lua-scripted for atomicity. **Leaky bucket** is the variant when you want a strictly smooth output rate with no bursts. Rule of thumb: token bucket for "burst then steady" (most player actions), sliding-window ZSET when you need a precise rolling count or the exact action timestamps (anti-cheat forensics), fixed window when cheap-and-approximate is fine.

Why Lua either way: the check-and-increment must be atomic, or two concurrent requests both read "9", both pass, and blow the limit.

🗣️ **Say this:** "Simplest is a fixed-window counter — `INCR` a per-second key with a TTL, and I set the TTL in the same Lua call so a crash can't leave a counter that never resets. But fixed windows suffer boundary bursts: you can hit double the limit across the window edge, which a bot will exploit. For anti-cheat I use a sliding window backed by a sorted set scored by timestamp — drop entries older than the window, count what's left, reject or record — all in one Lua script so the check-and-add is atomic, and I use a unique member so same-millisecond actions don't collide. If I want to allow short bursts but cap sustained rate I switch to a token bucket: tokens plus a refill timestamp, O(1), also Lua-scripted. The non-negotiable part is atomicity — without it concurrent requests both pass the check."

🎮 **Gaming angle:** The same primitive guards two layers — game-rule limits (max N bets/sec per player) and infra protection (a bot hammering a GraphQL mutation, throttled per-IP/per-token). And the sliding-window ZSET doubles as a cheat *signal*: because it stores the actual action timestamps, you can see inter-action timing — a player pinned exactly at the limit with millisecond-regular spacing is almost certainly automation, which you feed to the anti-cheat system rather than just dropping the request.

---

❓ **Question:** Explain how you'd use BullMQ for processing financial payouts. Walk me through producer/worker, retries with backoff, what happens to a job that keeps failing, and how you make the job idempotent.

🎯 BullMQ is a Redis-backed queue. A **producer** adds jobs; **workers** (separate processes/containers) pull and process them with configurable concurrency.

```ts
// Producer — enqueue a payout
await payoutQueue.add('credit-winner',
  { txnId, userId, amount },
  {
    jobId: txnId,                    // dedupe: same txnId won't enqueue twice
    attempts: 5,                     // retry up to 5 times
    backoff: { type: 'exponential', delay: 2000 }, // ~2s,4s,8s,16s (+ jitter if configured)
    removeOnComplete: 1000,          // cap completed-set growth
    removeOnFail: 5000,              // keep recent failures for inspection, but bounded
  });

// Worker
new Worker('payouts', async (job) => {
  const { txnId, userId, amount } = job.data;
  await creditWallet(userId, amount, txnId); // MUST be idempotent
}, { concurrency: 20 });
```

Key mechanics:
- **Retries + backoff.** On a thrown error BullMQ re-queues up to `attempts`, waiting per the backoff strategy. Exponential backoff avoids hammering a struggling downstream (a payment provider that's rate-limiting or degraded); add jitter so all retries from a burst don't realign and thunder together.
- **Failure terminus.** After the final attempt the job lands in the **failed set** — BullMQ's dead-letter equivalent. I bound it (`removeOnFail: <count or age>` rather than `false`) so a sustained provider outage can't grow the failed set until it OOMs Redis — but keep enough to alert, inspect, and manually/programmatically retry. Wire the worker's `failed` event to CloudWatch/alerting.
- **Idempotency is mandatory — and `jobId` dedupe has limits.** Retries mean a job *will* eventually run more than once (e.g. it credited the wallet, then the worker crashed before acking, so BullMQ's stalled-job recovery re-runs it). So the work itself must be idempotent — the `txnId` idempotency key from the wallet Lua script makes a re-run a safe no-op. Important nuance: `jobId: txnId` blocks a duplicate enqueue **only while that job id still exists in the queue** — once it completes and is removed (`removeOnComplete`), the *same* txnId can be enqueued again and won't be deduped by BullMQ. That's exactly why the durable idempotency key, not `jobId`, is the real safety net.
- **`jobId` dedup vs idempotent work are different layers:** `jobId` reduces duplicate *enqueues* (best-effort, retention-bounded); idempotent execution stops duplicate *effects* on retry (the actual guarantee). For money you want both, and you trust the second.

```
add(jobId=txn) → [waiting] → worker picks up → success → [completed]
                                   │ throws / stalls
                                   ▼
                          retry w/ backoff+jitter (attempts-1 left)
                                   │ exhausted
                                   ▼
                              [failed set]  → alert + manual/auto retry
```

🗣️ **Say this:** "Producer enqueues a payout with `attempts` and exponential backoff plus jitter; workers run with a set concurrency and pull from Redis. On failure BullMQ retries with growing delay so we don't hammer the payment provider, and after the last attempt the job goes to the failed set — that's our dead-letter — which I keep but bound so an outage can't OOM Redis, and I wire it to alerting for inspection or retry. The critical part for money is idempotency: retries and stalled-job recovery mean a job can execute more than once, so the worker must be safe to re-run — I use the txn id as the wallet idempotency key so a re-run is a no-op. I also set it as the BullMQ `jobId`, but I'm clear that `jobId` only dedupes while the job is still retained, so it's a nice-to-have on enqueue, not the guarantee. The guarantee is the idempotent effect, and for payouts I want both layers."

🎮 **Gaming angle:** Tournament settlement is one trigger fanning out thousands of payout jobs. Concurrency drains them fast, backoff-with-jitter protects the payment provider when it starts rate-limiting mid-settlement, and a bounded failed set means a provider blip parks a winner's payout — alerted and retryable — instead of silently losing it. The thing that would actually get you fired is a stalled-job re-run double-crediting; the idempotency key is what makes that re-run a no-op.

---

❓ **Question:** When would you use Redis Pub/Sub versus a BullMQ queue versus Redis Streams? Concretely — broadcasting a live score to 100k connected clients, vs. processing a deposit. Why not just use one for everything?

🎯 They solve different delivery problems:

| | Redis Pub/Sub | BullMQ (queue) | Redis Streams |
|---|---|---|---|
| Delivery | fire-and-forget, **no persistence** | durable, **at-least-once** | durable, **persistent log** |
| If consumer offline | message **lost** | job waits in Redis | message retained, replayable |
| Consumption | every subscriber gets it (fan-out) | one worker per job (work queue) | consumer groups (fan-out + scaling, with acks) |
| Ordering | per-publisher, best-effort | not guaranteed across workers | ordered per stream; per-consumer within a group |
| Use for | live broadcast, cache invalidation | jobs/tasks that must complete | event sourcing, replayable/auditable streams |

- **Broadcasting a live score to 100k clients → Pub/Sub** (or Socket.io's Redis adapter, which uses Pub/Sub under the hood). Fire-and-forget is *correct* here: a missed score is superseded by the next tick in ~100ms, and durably queuing+retrying a now-stale score would be actively wrong. It's also how you fan a message across multiple Socket.io instances behind an ALB — each instance subscribes and pushes to its locally-connected sockets. Caveat to state: Pub/Sub delivery isn't acked and a slow subscriber can hit its client-output-buffer limit and get dropped, so it's "best-effort at the transport too," which is fine for ephemeral scores.
- **Processing a deposit → BullMQ.** Money must not be lost. You need durability, retries, backoff, idempotency, a dead-letter set, and visibility. Pub/Sub would silently drop the work if no consumer was up at that instant.
- **Streams** sit in between: durable like a queue but with a replayable log, consumer groups, and explicit `XACK` / pending-entries-list so you can see and reclaim un-acked messages. Reach for it for an event-sourced/auditable feed — e.g. an immutable bet-event log you may need to reprocess, or that multiple independent consumer groups (anti-cheat, reconciliation, analytics) each read at their own pace. BullMQ is higher-level (retries, backoff, scheduling, priorities built in) for *task execution*; Streams give you the raw replayable log for *event distribution and reprocessing*. They overlap, so the deciding question is "do I need to replay history and fan to multiple independent groups?" → Streams; "do I need rich per-job retry/scheduling semantics?" → BullMQ.

**Why not one tool:** durability has a cost — storage, acking, retention — you don't want on a 100k-fanout hot path, and fire-and-forget is unacceptable for money. Matching the delivery guarantee to the data is the senior call.

🗣️ **Say this:** "It comes down to delivery guarantees. Live score to 100k clients is Pub/Sub — fire-and-forget is the *right* semantics because a missed score is superseded in 100ms and you'd never retry a stale one; it's also how Socket.io fans across instances behind the load balancer, with the honest caveat that a slow subscriber can be dropped, which is fine for ephemeral data. A deposit is BullMQ — money can't be dropped, so I want durability, retries, backoff, idempotency, and a dead-letter set, none of which Pub/Sub gives you. Streams are the middle ground: a durable, replayable log with consumer groups and acks, which I reach for when I need to reprocess events or have multiple independent consumers — like a bet-event log that anti-cheat, reconciliation, and analytics each read separately. Using one tool for everything either pays durability cost on a hot broadcast path or loses money on a queue, so I match the guarantee to the data."

🎮 **Gaming angle:** A single live round touches all three: Pub/Sub pushes the ticking scoreboard to every client, BullMQ runs the settlement payouts, and a Stream carries the immutable bet-event log that anti-cheat and reconciliation replay later — same round, three different delivery guarantees, which is the whole point.

---

❓ **Question:** Your cache-aside layer for player profiles is causing problems — stale data after profile updates, and a stampede that crushed Mongo when a hot key expired. Walk me through cache-aside, invalidation, and how you'd fix both issues.

🎯 **Cache-aside** (lazy loading): the app reads cache first; on a miss it loads from Mongo, populates the cache with a TTL, and returns.

```
read:  GET profile:42 → hit? return
                      → miss? load from Mongo → SET profile:42 <json> EX 300 → return
write: update Mongo → DEL profile:42   (invalidate, don't blindly overwrite)
```

**Stale data fix — invalidate, don't update.** On a write, the safe move is to **delete** the key, not `SET` the new value: two concurrent writers can `SET` in the wrong order and leave a stale value cached, whereas deleting forces the next read to repopulate from the source of truth. Order it **DB-write-then-invalidate** — if you invalidate first, a concurrent read can repopulate the *old* value before the DB commit lands. Be honest that even DB-then-DEL has a small race (a read that missed just before the write can `SET` stale right after your `DEL`); the bound on that staleness is the TTL, and the standard hardenings are a short TTL, or delayed/double-delete, or for strict cases a versioned key. For strong consistency you'd move off cache-aside toward write-through or a CDC-driven invalidation (e.g. Mongo change streams → cache delete) so invalidation isn't tied to app-write timing at all.

**Stampede fix — the hot key expiring.** When a popular key (a streamer's profile, the tournament-config key) expires, thousands of concurrent misses all hit Mongo at once:

```
key expires → 5000 requests miss simultaneously → 5000 Mongo queries (thundering herd)
```

Fixes, in order of preference:
1. **Single-flight / lock on repopulation.** First miss acquires a short Redis lock (`SET k:lock NX PX`) and is the only one to query Mongo; the rest briefly wait and re-read, or serve stale. Collapses 5000 queries into 1.
2. **Early/probabilistic expiry (XFetch).** Recompute slightly *before* the TTL, randomized per request, so a hot key is refreshed by one request before it ever expires under live load.
3. **Stale-while-revalidate.** Serve the old cached value while one background task refreshes it — ideal when slightly-stale profile data is acceptable, which it usually is.
4. **Jitter the TTL** (`EX 300 + rand(0..60)`) so a batch of keys populated together don't all expire on the same tick — that's the related **cache avalanche**, distinct from a single hot-key stampede.

State the distinction explicitly: **stampede** = one hot key, many simultaneous misses → fix with single-flight / early refresh; **avalanche** = many keys expiring at once → fix with TTL jitter. They're different failure modes with different fixes.

🗣️ **Say this:** "Cache-aside is read-through-the-app: check Redis, on a miss load from Mongo, set with a TTL, return. The staleness bug usually comes from *updating* the cache on writes — I switch to *invalidating* by deleting the key so the next read repopulates from source, and I order it DB-write-then-invalidate so a concurrent read can't cache the old value before the commit lands. I'll call out that even that has a small race bounded by the TTL, and if I need strong consistency I move to write-through or change-stream-driven invalidation. The stampede is a hot key expiring with thousands of misses hitting Mongo at once — I fix it with single-flight: the first miss takes a short Redis lock and is the only one to query the DB while the rest wait or serve stale. I add early probabilistic refresh and stale-while-revalidate so the hot key refreshes before it expires, and TTL jitter to prevent the avalanche where many keys expire together. The theme: invalidate rather than overwrite, and never let one expiry become a thundering herd."

🎮 **Gaming angle:** During a big match the tournament-config key and a handful of streamer profiles get hammered by 100k clients — those are exactly the keys whose expiry triggers a stampede, and the config key is the worst case because *every* request reads it. Single-flight repopulation plus early refresh keeps one expiry from cascading into a Mongo outage mid-event, and stale-while-revalidate is acceptable here because a 200ms-old profile or config is invisible to players but a Mongo meltdown mid-tournament is not.

---

Both key facts are confirmed: Socket.io defaults are `pingInterval` 25s / `pingTimeout` 20s (the draft had pingTimeout "~20s" — correct, but said "~45s ping" loosely; I'll tighten), and ALB default idle timeout is 60s. 

One important nuance to correct: the client considers the connection dead if it receives no ping within `pingInterval + pingTimeout`, and the proxy-idle rule should be stated against the actual gap between server writes, which is `pingInterval + pingTimeout` (45s). The draft's "idle > pingInterval+pingTimeout" is correct. I'll refine the loose "~45s ping" phrasing. Now producing the polished section.

## 🔌 Real-time at Scale (WebSockets / Socket.io)

---

❓ **Question**
"We run Socket.io on ECS Fargate behind an ALB. We scale from 2 to 12 tasks during a live event and clients start randomly disconnecting and failing to reconnect. What's wrong, and how do you make WebSockets work across a fleet?"

🎯
Two separate problems collide here: **connection affinity** and **cross-server fan-out**.

1. **Sticky sessions.** Socket.io's default transport does an HTTP long-poll handshake *before* upgrading to WebSocket. The polling phase is multiple separate HTTP requests that must all land on the *same* task, or the upgrade fails with `Session ID unknown`. Behind an ALB you need stickiness, OR force `transports: ['websocket']` to skip polling entirely (one connection, one upgrade, no affinity needed — my preferred approach for native/mobile apps and other known clients where I control the SDK). The tradeoff: dropping polling sacrifices the fallback path for restrictive corporate proxies that block raw WS, so for an open web audience I keep polling + stickiness; for a controlled game client I go websocket-only.

2. **Backplane.** Each task only knows about *its own* sockets. If user A is on task-3 and you `io.to(room).emit()` from task-7, A never gets it. You need the **Redis adapter** so emits propagate across all tasks via Redis pub/sub.

```
            ┌── ECS task 1 ──┐
client ──▶ ALB ─┤   socket.io    │──┐
 (sticky)   ├── ECS task 2 ──┤  ├─▶ Redis (pub/sub backplane)
            └── ECS task N ──┘──┘
   emit on task 2 → PUBLISH → all tasks SUBSCRIBE → deliver to local sockets
```

```ts
// NestJS gateway adapter
import { createAdapter } from '@socket.io/redis-adapter';
const pub = new Redis(process.env.REDIS_URL);
const sub = pub.duplicate();
io.adapter(createAdapter(pub, sub));
```

On ALB specifically: enable `stickiness` (app-based or duration cookie) on the target group, and set the ALB **idle timeout above the heartbeat gap**. The ALB default idle is 60s; Socket.io's defaults are `pingInterval` 25s and `pingTimeout` 20s, so a healthy idle socket can go up to ~45s between server writes. Keep ALB idle > `pingInterval + pingTimeout` or the ALB silently kills idle WS connections between pings. One more Fargate-specific gotcha: set ECS task **deregistration delay (connection draining)** sensibly and have clients reconnect on deploys — a scale-in event tears down long-lived sockets, so graceful drain plus client backoff-with-jitter prevents a reconnect storm.

🗣️ **Say this:**
"Two issues. First, ALB stickiness — Socket.io's polling handshake is several HTTP requests that must hit the same task, so I either enable target-group stickiness or, for a controlled game client, force the websocket-only transport to skip polling. Second, and more important, a single task only sees its own sockets, so a broadcast from one task never reaches users on another. I add the Socket.io Redis adapter, which uses Redis pub/sub as a backplane so every emit fans out to all tasks. I also raise the ALB idle timeout above the heartbeat gap — pingInterval plus pingTimeout, about 45 seconds on defaults — so it doesn't silently drop idle sockets, and I make sure scale-in drains connections gracefully while clients reconnect with jittered backoff. With those, the fleet behaves like one logical server."

🎮 **Gaming angle:** During a live tournament you scale tasks up mid-event; without the Redis adapter, players who reconnected onto new tasks stop receiving game-state and bet-settlement events — looks like the game froze even though the server is healthy. And without graceful drain + jittered reconnect, the scale-up itself triggers a thundering-herd reconnect that can knock the fleet over right when load peaks.

---

❓ **Question**
"Rooms vs namespaces — when do you use each? How would you model 'all players in table #42', 'everyone watching the world championship', and 'this one user's private notifications'?"

🎯
**Namespaces** = a separate communication *channel/endpoint* (`/game`, `/chat`, `/admin`), each with its own middleware, auth, and connection handlers. **Rooms** = arbitrary, dynamic groupings of sockets *inside* a namespace — cheap, ephemeral, created on `join`.

Rule of thumb: **namespaces are few and static (logical product surfaces); rooms are many and dynamic (entities/sessions).**

| Need | Use | Why |
|------|-----|-----|
| Separate auth/middleware for game vs admin | **Namespace** | per-namespace middleware & handlers |
| All players at table #42 | **Room** `table:42` | dynamic, thousands of them, auto-cleaned |
| Everyone watching the championship | **Room** `stream:wc-final` | one broadcast target, 100k members |
| One user across their 3 devices | **Room** `user:<id>` | join every socket to a per-user room |

```ts
socket.join(`table:${tableId}`);
socket.join(`user:${socket.data.userId}`);  // all devices → private channel
io.to(`table:42`).emit('state', payload);    // table broadcast
io.to(`user:abc`).emit('balance', wallet);   // every device of one user
```

Anti-pattern: creating a namespace *per table* (`/table/42`). Namespaces are matched against a fixed set (or a regex) and aren't designed for high cardinality — you lose the cheap dynamic semantics and connection management gets messy. Use rooms. One thing to know with the Redis adapter: room membership is **per-task local state**, but `io.to(room).emit()` still reaches members on every task because the emit itself is published over the backplane — so you get cross-fleet delivery without each task needing the full membership list.

🗣️ **Say this:**
"Namespaces are static product surfaces — separate endpoints with their own auth and middleware, like `/game` versus `/admin`. Rooms are dynamic groupings inside a namespace, and they're cheap, so I lean on them heavily. A poker table is a room `table:42`, the championship stream is one big room with 100k members, and a user's private channel is a `user:<id>` room that all their devices join — so I can push a wallet update to every device with one emit. The mistake I avoid is a namespace per table; namespaces aren't meant for high cardinality, rooms are. And with the Redis adapter the emit fans out across the fleet even though each task only holds its own slice of the room."

🎮 **Gaming angle:** The `user:<id>` room pattern is how you push an authoritative wallet balance to all of a player's open tabs/devices simultaneously after a bet settles — no device shows a stale balance.

---

❓ **Question**
"You need to broadcast a leaderboard update or a live game event to 100k concurrent clients with sub-second latency. Walk me through what actually happens and where it falls over."

🎯
The dangerous instinct is "just `io.emit()`." At 100k clients that's 100k individual frame serializations + socket writes, and if you emit on every score change you melt the event loop.

What actually happens with the Redis adapter on a broadcast:
```
emit to room (100k members, 12 tasks)
  → 1 PUBLISH to Redis  (NOT 100k)
  → each of 12 tasks receives it once
  → each task writes only to ITS ~8.3k local sockets
```
So Redis sees **one** message; the per-socket write fan-out is parallelized across the fleet. Redis is rarely the bottleneck for the *broadcast* path — **per-task socket writes and serialization are.** (The Redis caveat is different: if you do high-cardinality *targeted* emits — `io.to(user:x)` for thousands of distinct users — each one is its own publish, and *that* can saturate the pub/sub channel. Broadcast = one publish; per-user fan-out = many.)

Levers that actually move the needle:
- **Don't broadcast per-event.** Coalesce. Diff + batch on a tick (e.g. flush leaderboard deltas every 250–500ms), not on every score change. 4 emits/sec ≫ 4000 emits/sec.
- **Send deltas, not the full board.** Top-100 + the player's own rank slice (computed cheaply from a Redis Sorted Set via `ZREVRANGE` / `ZREVRANK`), not 10k rows.
- **Pre-serialize once.** Compute the JSON string a single time per tick; avoid re-stringifying per socket. With the Redis adapter the payload is serialized for the wire anyway, so feed it a value that's cheap to encode.
- **Use `volatile` emits** for state that's superseded next tick (drop if the socket is behind rather than queue).
- **Shard the work.** More tasks = fewer sockets each = more parallel write throughput. It's CPU/event-loop bound, so scale horizontally, and watch p99 event-loop lag (not just CPU%) as your real saturation signal.
- **Compression off for tiny frequent frames** (`perMessageDeflate: false`) — the CPU cost of deflate per frame outweighs bandwidth savings for small hot payloads; keep it on only for large, infrequent ones.

🗣️ **Say this:**
"First I separate two costs: cross-server propagation and per-socket writes. With the Redis adapter a *room broadcast* is a single Redis publish, then each task writes only to its local sockets — so Redis isn't the bottleneck for broadcasts, the per-task serialization and writes are. The biggest win is *not* emitting per score change: I coalesce on a 250-to-500ms tick and send deltas — top-100 plus the player's own rank, which I pull straight from a Redis sorted set — not the full board. I pre-serialize the payload once per tick, use volatile emits for state that's about to be superseded, turn off per-message deflate for tiny hot frames, and scale tasks horizontally since it's event-loop bound — I watch p99 event-loop lag as the real saturation signal. That's how you hold sub-second at 100k."

🎮 **Gaming angle:** A leaderboard during a live event changes thousands of times a second, but humans only perceive ~4–10 updates/sec. Coalescing to a tick cuts emit volume by ~1000x with zero perceived latency loss — and volatile emits mean a slow client just skips to the latest standings instead of replaying stale ones.

---

❓ **Question**
"One client is on a slow mobile connection. Your server keeps emitting fast game events. What happens to memory, and how do you handle backpressure and slow consumers?"

🎯
This is the silent killer. When you `emit()` faster than a socket can drain, Node buffers the unsent data in the socket's **write buffer in your process heap**. One slow client × high emit rate = unbounded memory growth → eventually OOM-kills the task → drops *everyone* on it. A slow consumer becomes a fleet-wide outage.

WebSocket has no built-in app-level flow control, so you implement it:

1. **`volatile` emits** for superseded state — Socket.io won't buffer the frame if the underlying connection isn't ready, so a behind client simply skips it. Perfect for game state, positions, leaderboards (next tick fixes it anyway).
```ts
socket.volatile.emit('tick', state); // dropped if client is behind
```
2. **Watch the buffer.** Monitor the engine's outgoing buffer; if it crosses a threshold, stop sending non-critical data to that socket (or disconnect it). The exact accessor varies by version, so I treat it as "read the engine write-buffer length" rather than hardcoding a field — and I also cap it server-side with `maxHttpBufferSize`.
```ts
if (socket.conn.writeBuffer.length > MAX) socket.disconnect(true);
```
3. **Separate critical from droppable.** Wallet settlement = reliable, acked, sourced from a durable store. Position/leaderboard ticks = volatile, droppable. Never put financial events on the droppable path.
4. **Rate-limit outbound per client.** Cap events/sec per socket; let the tick coalescer (above) bound the firehose at the source — backpressure is cheapest to solve *before* the data is ever queued.

```
fast producer ──▶ [ socket write buffer grows ] ──▶ slow client
                         │
                  unbounded heap growth → OOM → whole task dies
   fix: volatile drop ▲ + buffer threshold disconnect + coalesce at source
```

🗣️ **Say this:**
"The danger is that emitting faster than a slow client can drain buffers data in the process heap, and one slow client can OOM the task and take down everyone on it. WebSocket has no app-level flow control, so I add it. For superseded state — positions, leaderboards — I use volatile emits, which skip the frame if the client is behind, because the next tick corrects it anyway. I monitor each socket's outgoing buffer and stop non-critical sends or disconnect past a threshold, and I cap buffer size server-side. Critically, I separate droppable state from reliable financial events — wallet settlements are acked and sourced from a durable store, never on the volatile path. And I coalesce at the source so the firehose is bounded before it ever reaches a socket."

🎮 **Gaming angle:** A laggy player must never be able to balloon server memory and crash the table for everyone else. Volatile game-state + reliable acked wallet events is exactly the split: the laggy player just sees a few skipped frames, but their bet settlement still arrives exactly once.

---

❓ **Question**
"How do heartbeats and reconnection actually work in Socket.io, and what breaks them in production? A client's connection drops for 5 seconds during a bet — what's the correct behavior?"

🎯
**Heartbeat:** Since Socket.io v3 the *server* drives the heartbeat — it sends a ping every `pingInterval` (default 25s) and considers the connection dead if it gets no pong within `pingTimeout` (default 20s). The client independently declares the connection dead if it receives no ping within `pingInterval + pingTimeout`. This detects half-open connections that TCP alone won't (NAT timeouts, dead WiFi). (Note the v2→v3 reversal: in v2 the client sent pings; if you read old answers that say "client pings," that's stale.)

**What breaks it in prod:** any intermediary idle timeout *shorter* than the heartbeat gap. ALB default idle = 60s, some proxies 30s. The longest a healthy idle socket can go without a server write is `pingInterval + pingTimeout` (~45s on defaults); if that exceeds the proxy idle timeout, the proxy silently closes the socket *between* pings → mass phantom disconnects. **Rule: proxy/ALB idle timeout MUST exceed `pingInterval + pingTimeout`.**

**Reconnection:** The client auto-reconnects with exponential backoff + jitter (jitter matters — without it, 100k clients reconnect in lockstep and you get a thundering-herd that DDoSes your own ALB after a deploy). On reconnect the client gets a *new* socket id by default; the old one is gone.

**The 5-second-drop / bet scenario — the key insight:** WebSocket reconnection does **not** replay missed messages or preserve in-flight operations. The socket is a *transport*, not a transaction. So:
- The bet's correctness must **not** depend on the socket staying up. Use a **client-generated idempotency key**; the bet is an acked request → processed server-side exactly once regardless of disconnects.
- On reconnect, the client **re-syncs authoritative state** (fetch current wallet + bet status by idempotency key), it doesn't "resume" the socket.
- Socket.io **Connection State Recovery** can transparently restore the session id + rooms and replay *buffered* missed events across a short gap — but it's best-effort (events are buffered in the adapter with a TTL and can be lost), so use it only for droppable UX continuity, **never** as the integrity mechanism for money.

🗣️ **Say this:**
"Since v3 the server drives the heartbeat — it pings every pingInterval and declares the connection dead if it misses a pong within pingTimeout — and that catches half-open connections TCP won't. The number-one production break is a proxy or ALB idle timeout shorter than the heartbeat gap, which silently kills sockets between pings, so the idle timeout must exceed pingInterval plus pingTimeout, about 45 seconds on defaults. The client reconnects with exponential backoff *and jitter* so 100k clients don't reconnect in lockstep after a deploy. For the bet: the critical thing is that reconnection doesn't replay missed messages — the socket is a transport, not a transaction. So the bet carries a client idempotency key and is processed exactly once server-side regardless of the drop, and on reconnect the client re-fetches authoritative wallet and bet state rather than 'resuming.' Connection State Recovery can smooth the UX gap, but I'd never trust it for money."

🎮 **Gaming angle:** Players on mobile drop constantly. Money correctness can never ride on socket liveness — an idempotency key + state re-sync on reconnect means a 5-second tunnel/elevator drop mid-bet results in exactly one settled bet, and the client just re-syncs the real balance when it comes back.

---

❓ **Question**
"When would you choose WebSockets vs Server-Sent Events vs long-polling? Give me a case where SSE is the *better* engineering choice for a gaming feature."

🎯
| | WebSocket | SSE | Long-polling |
|---|---|---|---|
| Direction | **Bi-directional** | Server→client only | Client-pull (hack) |
| Transport | TCP upgrade | Plain HTTP stream | Repeated HTTP |
| Auto-reconnect | Manual / lib | **Built-in** (`Last-Event-ID`) | N/A |
| Infra friendliness | Needs WS-aware LB | Just HTTP — proxies/CDN love it | Universal |
| Overhead | Lowest per-msg | Low | **Highest** |
| Binary | Yes | No (text/UTF-8) | Yes |

- **WebSocket** when you need **low-latency bidirectional**: player actions in, game state out — placing bets, moving, chat. This is the default for actual gameplay.
- **SSE** when traffic is **server→client only** and you want HTTP simplicity + free reconnection with event replay via `Last-Event-ID`. It rides plain HTTP, so ALBs, CDNs, and corporate proxies handle it without special config — and it auto-reconnects with a built-in resume cursor.
- **Long-polling** only as a **fallback** transport (Socket.io uses it under the hood for handshake/degraded networks). Rarely a deliberate primary choice today.

**SSE-is-better gaming case:** a **read-only live odds / score ticker / spectator leaderboard** where the viewer only *consumes*. No client→server messages needed → a WebSocket's bidirectional machinery and LB special-casing are pure overhead. SSE gives you push + automatic reconnection-with-replay over boring, CDN-cacheable HTTP. **Two caveats worth naming:** (1) over HTTP/1.1 a browser caps ~6 connections per domain, so a user with several tabs starves — run SSE over **HTTP/2+** to multiplex many streams on one connection; (2) `Last-Event-ID` only replays if *your server* retains recent events to resume from, so back it with a short Redis ring/stream buffer if gap-free replay matters.

🗣️ **Say this:**
"WebSocket is the default for actual gameplay because it's bidirectional and low-latency — actions in, state out. SSE is server-to-client only but it's plain HTTP, so it gets free reconnection with event replay via Last-Event-ID and it sails through ALBs, CDNs, and proxies with no special config. Long-polling I only use as a degraded fallback. The case where SSE is genuinely better is a read-only feature — a live odds ticker or spectator leaderboard where the viewer only consumes. There's no client-to-server traffic, so a WebSocket's bidirectional machinery is just overhead; SSE gives me push plus automatic resume over boring CDN-friendly HTTP. Two caveats: I run it over HTTP/2 so the six-connection-per-domain limit doesn't bite, and I keep a short server-side event buffer so Last-Event-ID can actually replay the gap."

🎮 **Gaming angle:** Spectators watching a tournament leaderboard vastly outnumber active players. Serving the read-only spectator feed over SSE (CDN-friendly, auto-reconnecting) offloads it from your WebSocket fleet, so your WS capacity is reserved for players who actually need bidirectional gameplay.

---

❓ **Question**
"WebSocket delivery is 'fire and forget' at the protocol level. How do you build at-least-once or exactly-once delivery guarantees for something like a bet confirmation on top of it?"

🎯
WebSocket/Socket.io gives you **TCP ordering within a single live connection, but no guarantee across disconnects** — a frame in flight when the socket dies is simply lost. For money you need application-level guarantees layered on top. (And "exactly-once *delivery*" is a myth on any network — what you actually engineer is at-least-once delivery + idempotency, which yields exactly-once *effect*.)

**The ladder of guarantees:**

1. **Acks** (at-least-once, server↔client). Socket.io supports per-message acknowledgements with a callback/timeout. Sender retries until acked.
```ts
socket.timeout(5000).emit('placeBet', bet, (err, ack) => {
  if (err) retryWithSameKey(bet);   // no ack → retry with the SAME key
});
```
2. **Idempotency key** (turns at-least-once into **effectively exactly-once**). Client generates a UUID per bet; server dedupes (unique index in Mongo / atomic `SET key val NX` in Redis). Retries are safe — the operation applies once.
```
placeBet{ key: "uuid-1", ... }  ──▶  server: SET bet:uuid-1 … NX
   retry (same key)             ──▶  already exists → return prior result
```
3. **Outbox / durable queue for the critical path.** The bet doesn't "live" on the socket. The socket just *carries* the request → it's enqueued (BullMQ) → processed by a worker → result is **authoritative state in Mongo, written in a single atomic operation that both debits the wallet and records the bet** (e.g. one document update guarded by the idempotency key, or a Mongo multi-doc transaction). Delivery of the *confirmation* back to the client is best-effort over WS, but the client can always re-fetch truth by key. Never let WS delivery be the system of record.
4. **Re-sync on reconnect**, not replay. On reconnect, client asks "status of bet uuid-1?" and "current wallet?" → authoritative answer. The socket never has to have delivered the original confirmation.

```
Client ──placeBet(key)──▶ Gateway ──▶ BullMQ ──▶ Worker ──▶ Mongo (truth, atomic debit+record)
   ▲ ack/confirm (best-effort WS)                    │
   └──────────── reconnect: GET status by key ◀──────┘ (authoritative)
```

So: **acks + idempotency key = exactly-once *effect*; durable queue + atomic write + state re-sync = the socket is never the source of truth.** That's how you do money over an unreliable transport.

🗣️ **Say this:**
"WebSocket gives ordering within a live connection but nothing across disconnects, and frankly exactly-once *delivery* doesn't exist on a network — so what I build is at-least-once plus idempotency, which gives exactly-once *effect*. Socket.io acks with a timeout give me the at-least-once retry — no ack, I retry with the same key. The client attaches an idempotency key and the server dedupes with a unique index or an atomic Redis SET-NX, so retries apply once. Crucially, the bet doesn't live on the socket: the gateway enqueues it to BullMQ, a worker processes it, and the debit and the bet record land in Mongo in one atomic operation keyed by that idempotency key. The WebSocket confirmation is best-effort, because on reconnect the client re-syncs by asking 'status of this key' and 'current wallet' rather than replaying. So acks plus idempotency give exactly-once effect, and the durable queue plus atomic write plus re-sync mean the socket is never the system of record."

🎮 **Gaming angle:** This is the core wallet-integrity / no-double-spend pattern: a player taps "place bet," their phone drops before the ack, the client retries the same idempotency key — server dedupes, exactly one bet is placed, exactly one debit happens. No double-spend, no lost bet, even though the transport itself guaranteed nothing.

---

## ⚡ Event-Driven & Distributed Systems

---

❓ **Question: A player wins a bet. The "settle bet" event flows through your queue and credits their wallet. The consumer crashes after crediting but before acking the message, so it redelivers and credits again — double payout. How do you guarantee a bet is settled exactly once?**

🎯 The honest framing: **exactly-once *delivery* is a myth** over an unreliable network. The two-generals problem means a producer can never know its message arrived without an ack, and that ack can itself be lost — so you either risk losing the message or risk duplicating it. Every real broker (Kafka, BullMQ/Redis, SQS) gives you **at-least-once** delivery. You convert that into **exactly-once *effect*** by making the consumer **idempotent**.

The mechanism is an **idempotency key** plus an atomic write. The key must be deterministic and tied to the business event, not generated per-attempt — e.g. `settle:bet:{betId}` (NOT a random UUID per delivery, or retries get different keys and double-credit anyway).

```
                  at-least-once delivery (may redeliver)
   [settle event] ──────────────────────────────────────▶ [consumer]
        key = settle:bet:{betId}                               │
                                                               ▼
   ┌──────────────────── single atomic transaction ──────────────────┐
   │  1. insert idempotency record { key } UNIQUE  → dup? abort+ack   │
   │  2. wallet.balance += payout                                     │
   │  3. write ledger entry                                           │
   └─────────────────────────────────────────────────────────────────┘
                          commit, THEN ack
```

In Mongo, the cleanest version collapses the dedup and the mutation into one atomic op: a unique index on the idempotency key makes the *second* credit fail at the storage layer, even under concurrent redelivery.

```ts
// Unique index: { idempotencyKey: 1 }, unique: true
try {
  await session.withTransaction(async () => {
    // throws E11000 on duplicate key -> already settled
    await ledger.create(
      [{ idempotencyKey: `settle:bet:${betId}`, betId, amount }],
      { session },
    );
    await wallet.updateOne({ _id: userId }, { $inc: { balance: amount } }, { session });
  });
} catch (e) {
  if (e.code === 11000) return; // already settled -> ack, no-op
  throw e; // real failure -> don't ack, let it redeliver
}
```

Three rules carry this: (1) **commit the effect before you ack** — if you ack first and crash, the effect is lost (at-most-once). (2) The whole thing — dedup insert + balance change — must be **one transaction**, or you can insert the dedup row, crash, and never credit (silent loss). (3) **Catch the duplicate-key error and treat it as success** — a redelivery hitting E11000 is the *expected* path, so it acks and moves on; only a genuine failure should be left to redeliver.

One senior caveat: a Mongo transaction itself can fail at commit with a `TransientTransactionError` or `UnknownTransactionCommitResult`, where the commit may or may not have landed. The driver's `withTransaction` retries those safely *because* the operation is idempotent on the unique key — which is exactly why the key has to guard the commit, not just the application logic.

🎮 **Gaming angle:** Settlements, deposits, withdrawals, and bonus credits are all financial and all run through at-least-once queues. Every one needs a stable idempotency key. We key deposits on the PSP transaction id, settlements on `betId`, so a redelivered webhook or a retried job is a no-op, never a double-spend. And we never delete idempotency records on a hot path — they're the audit proof a payout happened exactly once, so they're retained (or archived) for the regulatory window.

🗣️ **Say this:** "Exactly-once delivery doesn't exist over a network — brokers give you at-least-once, so I design every consumer assuming redelivery. I make the *effect* idempotent: a deterministic key tied to the business event, like `settle:bet:{betId}`, with a unique index so the second attempt fails at the database. The dedup write and the balance change go in one transaction, and I commit before I ack. I catch the duplicate-key error and treat it as a successful no-op, so a redelivered settlement just acks. Ack-first loses messages; effect-then-ack with a unique key means a redelivered payout is harmless. That's how you turn at-least-once into exactly-once payout without ever double-crediting."

---

❓ **Question: A bet write needs to (1) persist the bet to Mongo and (2) publish a `bet.placed` event so the leaderboard, risk engine, and stats consumers update. If you write to the DB and then publish, what can go wrong, and how do you fix it?**

🎯 This is the **dual-write problem**: two independent systems (your DB and your broker) with no shared transaction. Any interleaving fails:

```
  DB write OK  ─┐
                ├─ crash before publish  → bet exists, NO event (leaderboard/risk never see it)
  publish OK   ─┘
  publish first, DB write fails           → event exists, NO bet (phantom — consumers act on nothing)
```

You cannot wrap a Mongo write and a Kafka publish in one atomic transaction — they're separate infrastructure. The fix is the **transactional outbox pattern**: write the event into an `outbox` collection **in the same DB transaction** as the bet. Now the bet and the intent-to-publish commit atomically. A separate **relay** (a poller, or Mongo change stream / CDC) reads the outbox and publishes to the broker, marking rows sent.

```
  ┌─ one Mongo txn ─────────────┐
  │  bets.insert(bet)           │
  │  outbox.insert(bet.placed)  │   ← atomic: both or neither
  └─────────────────────────────┘
            │
            ▼  change stream / poller (relay)
        publish to Kafka ──▶ leaderboard / risk / stats
            │
            ▼  mark outbox row as published
```

The relay is **at-least-once** (it can publish, then crash before marking the row sent, and re-publish) — which is exactly why the downstream consumers must be idempotent, closing the loop with the previous answer. **Outbox guarantees "no lost events"; idempotent consumers guarantee "no duplicate effects."**

Two senior refinements worth raising:
- **Relay choice is a real tradeoff.** A **change stream / CDC** reader is near-real-time (low latency, ideal for live odds and risk) but you must persist the resume token so a relay restart doesn't replay or skip. A **poller** is simpler and dead-easy to reason about but adds latency and DB load, and needs an index on `{ published: 1, createdAt: 1 }` to stay cheap. On this stack I'd use a change stream for latency, with a poller as a backstop sweep for any row the stream missed.
- **Ordering.** A naive relay can publish events out of order, which breaks consumers that assume causal order (e.g. `bet.placed` before `bet.settled`). If a consumer needs order, partition the broker by a stable key (`betId` / `userId`) so all events for one entity land on one partition and stay ordered.

🎮 **Gaming angle:** A bet that's persisted but never reaches the risk engine is an unmonitored exposure; a leaderboard that silently drops wins erodes trust instantly. Outbox guarantees every placed bet eventually reaches every consumer, even across a deploy or a pod kill mid-publish — and partitioning by `betId` keeps a bet's place→settle sequence in order for the consumers that care.

🗣️ **Say this:** "That's the dual-write problem — DB and broker have no shared transaction, so a crash between them either loses the event or creates a phantom one. I use the transactional outbox: I write the event row into an outbox collection in the *same* Mongo transaction as the bet, so they commit atomically. A relay — a change stream or poller — publishes from the outbox and marks rows sent. I'd lean on a change stream for low latency, persisting the resume token, with a poller as a backstop. The relay is at-least-once, so consumers stay idempotent, and where order matters I partition by `betId` so place-then-settle stays ordered. Net result: the bet and its event are never out of sync, and every downstream system eventually sees every bet."

---

❓ **Question: Walk me through settling a multi-leg workflow: a player cashes out, which must debit the house ledger, credit the player wallet, deduct any bonus rollover, and call an external PSP for withdrawal. Some steps are in different services. How do you keep this consistent without a distributed 2PC lock?**

🎯 Two-phase commit doesn't scale here: it holds locks across services for the duration, and a coordinator failure leaves participants **blocked** holding those locks — unacceptable at gaming write volumes and latency budgets. The pattern is a **saga**: break the transaction into a sequence of **local** transactions, each with a **compensating action** that semantically undoes it. You give up atomic isolation and accept **eventual consistency** with explicit rollback.

```
 Saga: cash-out
   T1 debit house ledger      ⟶  C1 credit house ledger back
   T2 credit player wallet    ⟶  C2 debit player wallet
   T3 deduct bonus rollover   ⟶  C3 restore bonus
   T4 PSP withdrawal call ✗   ⟶  run C3, C2, C1 (reverse order)
```

Two flavours: **choreography** (each service emits an event the next one listens to — decentralized, but the flow is implicit and hard to trace) vs **orchestration** (a central coordinator drives each step and triggers compensations — explicit, observable, my default for money flows). Given the stack, I'd run the orchestrator on **Temporal**, which persists workflow state and makes compensation/retry first-class; otherwise a BullMQ-driven state machine with a `saga_state` document.

Critical correctness rules:
- **Every step and compensation is idempotent** — sagas retry, and a compensation can itself be retried, so "undo twice" must equal "undo once" (the wallet's idempotency-key trick applies here too).
- **Lost-update / out-of-order guards.** Because there's no isolation, a compensation can race a concurrent operation on the same wallet. Use a conditional/`$inc` update or an optimistic version so a compensation can't, say, restore funds a later debit already touched.
- **Order for irreversibility.** Some actions can't be undone — you can't un-send a real bank payout. So order the saga to do **reversible work first, the irreversible PSP call last**, and use a **pivot/confirm step**: reserve/hold funds, do everything reversible, then commit the irreversible PSP call. A failure *before* the pivot compensates cleanly; after the pivot, the saga can only roll *forward* (retry to completion), never back.

🎮 **Gaming angle:** Withdrawals touch real money across the wallet service, ledger, bonus engine, and an external PSP — no single DB transaction spans them. A saga with the PSP call ordered last means if the bank rejects *before* the pivot, we compensate the internal debits and the player's balance is exactly restored, with a full ledger trail for audit/regulatory. And because the PSP call is the irreversible pivot, that outbound request itself carries an idempotency key (most PSPs support one) so a Temporal retry never fires two real payouts.

🗣️ **Say this:** "I avoid 2PC — it locks across services and blocks participants on coordinator failure. I use a saga: a sequence of local transactions, each with a compensating action, accepting eventual consistency. For money I prefer an orchestrated saga on Temporal so state, retries, and compensations are explicit and auditable. Every step and compensation is idempotent because they retry, and I guard wallet updates conditionally so a compensation can't race a concurrent debit. The key trick is ordering: do reversible work first and the irreversible PSP call last, behind a pivot step — fail before the pivot and I compensate cleanly; after it, I can only roll forward. The PSP call carries its own idempotency key so a retry never double-pays. If the bank rejects early, compensations restore the player's balance exactly, with a ledger trail."

---

❓ **Question: We broadcast live event updates — odds changes, goals, a settled round — to 100k concurrently connected clients. Design the fan-out. What breaks if you do it naively, and how does pub/sub + consumer groups fit in?**

🎯 The naive version — one process holding all sockets and looping `for (client of clients) client.emit()` — breaks on three axes: a single Node process can't hold 100k sockets or sustain that CPU; if it dies, everyone disconnects at once; and you can't scale horizontally because sockets are pinned to one box. You need to **decouple producers from connections** and **scale the socket tier independently**.

```
                          ┌─ pub/sub fan-out (Redis / Kafka topic) ─┐
   [odds engine] ─publish─▶  channel: match:{id}                    │
                          └──────────────┬────────────┬─────────────┘
                                         ▼            ▼            ▼
                                   [ws node 1]  [ws node 2]  [ws node 3]   ← scale out
                                    25k socks    40k socks    35k socks
                                         │            │            │
                                       clients      clients      clients
```

Each WebSocket node **subscribes** to the channels its connected clients care about and pushes only to its local sockets. With Socket.io that's the **Redis adapter**, which uses Redis pub/sub to fan a broadcast across all nodes. Behind an ALB you need **sticky sessions** (or pure WebSocket transport, no long-polling upgrade) so a client stays on the node holding its socket.

Key distinction for the interview: **pub/sub fan-out ≠ consumer groups**. Pub/sub is *every subscriber gets every message* — correct for broadcasting odds to all UI nodes. **Consumer groups** (Kafka groups, BullMQ concurrency) are *load-balanced, each message processed once* — correct for *work* like settling bets, where you want N workers sharing the load, not all doing the same job. Live broadcast = fan-out; settlement processing = consumer group. Don't mix them up.

Three senior details that separate "it works" from "it works at 100k":
- **Shard channels** (`match:{id}`, not one global channel) so a node only receives traffic for matches its clients watch, keeping each node's load proportional to its connections.
- **Latest-wins, fire-and-forget** for live odds — drop stale updates rather than queue them, because a 3-second-late odd is worse than a dropped one. Coalesce: if a node is behind, send only the newest odds, not the backlog.
- **Redis pub/sub is itself fan-out-on-one-node** — every WS node's broadcast round-trips through Redis, so at very high broadcast rates Redis pub/sub (or a single match channel) becomes the bottleneck. The scaling answers are sharding channels across Redis, or moving to a dedicated streaming tier; I'd name that ceiling rather than pretend the Redis adapter scales infinitely. Also: **reconnect storms** — if a WS node dies, its 40k clients reconnect at once, so I need backoff-with-jitter on the client and connection-rate headroom on the surviving nodes.

🎮 **Gaming angle:** 100k clients on a big match, odds changing several times a second. The socket tier scales out on ECS Fargate independently of the odds engine; Redis pub/sub does the cross-node fan-out; per-match channels keep each node's load proportional to its clients. Settlement, by contrast, runs on a BullMQ consumer group so the payout work is shared once across workers — fan-out for delivery, consumer group for work.

🗣️ **Say this:** "Naively, one process can't hold 100k sockets, is a single point of failure, and can't scale out. I decouple the producer from the connections: the odds engine publishes to a pub/sub channel per match, and a horizontally-scaled WebSocket tier subscribes and pushes to its local sockets — Socket.io's Redis adapter does the cross-node fan-out, with sticky sessions behind the ALB. The key distinction is pub/sub fan-out, where every node gets every message for broadcast, versus consumer groups, where work is load-balanced and processed once — fan-out for odds, consumer groups for settlement. Live odds are latest-wins fire-and-forget, so I coalesce and drop stale updates rather than queue them. And I'd call out the ceiling: every broadcast round-trips through Redis, so at extreme rates I shard channels across Redis or move to a dedicated streaming tier, and I plan for reconnect storms with client backoff-and-jitter."

---

❓ **Question: A live leaderboard for a tournament must rank 50k players updating constantly. Where does CAP bite you, and how do you choose between strong and eventual consistency here versus for the wallet?**

🎯 CAP says under a **network partition** you choose **C**onsistency or **A**vailability — you can't have both, and partitions are not optional. The senior move is recognizing **different data has different consistency needs**, even in the same product. (And the everyday axis is really **PACELC**: even with no partition, you're trading latency vs consistency — which is exactly why the leaderboard reads from Redis and not the primary.)

**Leaderboard → choose availability + eventual consistency.** Players tolerate a rank that's a few hundred ms stale; what they won't tolerate is the board freezing. Implement with a **Redis sorted set** (`ZADD`/`ZINCRBY` + `ZREVRANGE`) — O(log N) updates and range reads, served from memory, eventually consistent with the source of truth. A momentarily-off rank is fine.

```
  Leaderboard:  AP — Redis ZSET, eventual,   "approximately right, always up"
  Wallet:       CP — Mongo txn, strong+linearizable, "exactly right, may reject"
```

**Wallet → choose consistency.** A balance must be **linearizable**: a read must reflect all prior credits/debits, and two concurrent debits must not both succeed past zero. Here I'd rather **reject or stall a bet during a partition than allow a double-spend or a negative balance**. Concretely: Mongo with `majority` write concern and `majority`/`linearizable` read concern, balance changes in a transaction, and a guard like `updateOne({ _id, balance: { $gte: stake } }, { $inc: { balance: -stake } })` so an insufficient-balance debit fails atomically (the conditional match is what makes it safe under concurrency — two debits can't both pass the `$gte` check).

One nuance worth naming: the leaderboard being eventual doesn't mean *sloppy*. The score it ranks on is **derived from the wallet/ledger, which is the strong source of truth** — so the leaderboard can lag, but it can't invent points. Eventual consistency on a projection of strongly-consistent data, not eventual consistency on the money itself.

The framing that lands: **strong consistency where being wrong costs money; eventual consistency where being slightly stale costs nothing.** Wallet is CP, leaderboard is AP — a deliberate, per-dataset decision, not a global one.

🎮 **Gaming angle:** Leaderboards are read-heavy, latency-sensitive, and forgiving — Redis ZSET, eventual, always available, but ranking a value that ultimately comes from the strongly-consistent ledger. Wallets are correctness-critical — strong consistency, reject rather than risk a negative balance. Same platform, opposite CAP choices, chosen per the cost of being wrong.

🗣️ **Say this:** "CAP forces a choice during partitions, and the senior insight is that it's per-dataset, not global — and even without a partition, PACELC says I'm trading latency for consistency, which is why the leaderboard reads from Redis. The leaderboard is read-heavy and forgiving — a rank that's 200ms stale is fine but a frozen board isn't — so it's AP: a Redis sorted set, in-memory, eventually consistent, though the score it ranks comes from the strongly-consistent ledger, so it can lag but never invent points. The wallet is the opposite: a balance must be linearizable and never go negative, so it's CP — Mongo majority read/write concern, transactional debits with a `balance >= stake` conditional guard so two concurrent debits can't both pass. I'd rather reject a bet during a partition than allow a double-spend. Strong consistency where being wrong costs money, eventual where staleness is free."

---

❓ **Question: An anti-cheat / rate-limiting layer must block a client spamming 10k bet requests/sec across multiple ECS tasks. A naive in-memory counter per task fails. How do you build a correct distributed rate limiter, and what's the failure mode if Redis goes down?**

🎯 The in-memory counter fails because each ECS task has its **own** counter — behind the ALB a client spread across 4 tasks gets 4× the intended limit, and the limit resets on every deploy. You need **shared, atomic** state — Redis — with an algorithm that's correct under concurrency.

Fixed windows have a **boundary burst** flaw (up to 2× the limit straddling the window edge). I use a **sliding-window** or **token-bucket** limiter, executed as a **Lua script** so the read-decide-write is **atomic** on the Redis server — otherwise two concurrent requests both read "under limit" and both pass (a check-then-act race, the same class of bug as double-spend). Lua here is doing the same job the unique index does for the wallet: collapsing check-and-act into one atomic step.

```
  ALB ─┬─▶ task1 ┐
       ├─▶ task2 ├──▶  Redis (atomic Lua: token-bucket per userId) ──▶ allow / 429
       ├─▶ task3 ┘     key = rl:{userId}, refill rate + burst cap
       └─▶ task4
```

Token bucket also models gaming nicely: a **burst** allowance (legit rapid betting) plus a sustained **refill rate** (anti-spam ceiling). Key per identity (`userId`/`sessionId`), not IP alone (NAT and carrier-grade NAT mean many real users share an IP; key on IP only as a coarse pre-auth layer). Set a TTL on the key so idle buckets self-evict and Redis memory stays bounded.

**Redis-down failure mode** is a real design decision: **fail-open vs fail-closed.** Fail-open (allow when the limiter is unreachable) keeps the platform usable but removes the cheat ceiling; fail-closed (deny) protects correctness but can take you fully offline on a Redis blip. For *rate limiting* I fail-open with a tight per-task local fallback cap and loud alerts — better degraded than down. For the **wallet debit**, by contrast, I **fail-closed** — never process money without the consistency guarantee. Same incident, opposite choice, again driven by the cost of being wrong.

Two more senior notes: keep the limiter **off the hot data path** — a slow or overloaded Redis must not add latency to every bet, so the call is time-boxed (short timeout → fall back) rather than allowed to block. And anti-cheat is **defense in depth**: the rate limiter is one layer; behavioural detection (impossible click cadence, win-rate anomalies) runs async off the `bet.placed` stream, because a sophisticated bot stays *under* any per-user rate limit.

🎮 **Gaming angle:** Bot/scripted clients hammer bet and spin endpoints to exploit odds latency or bonus abuse. A per-user token bucket in Redis, evaluated atomically across all Fargate tasks, gives a true global ceiling with a legitimate burst allowance — and the fail-open/fail-closed split keeps gameplay flowing while money operations stay strict. The rate limiter blocks the brute-force spammer; the async anomaly detector on the bet stream catches the slow, "legitimate-looking" bot.

🗣️ **Say this:** "Per-task in-memory counters multiply the limit by the number of tasks and reset on deploy, so I centralize state in Redis. I use token-bucket or sliding-window, run as a Lua script so the check-and-decrement is atomic on the server — otherwise concurrent requests race past the limit, the same bug class as double-spend. Token bucket gives a burst allowance plus a sustained refill, which matches real betting, and I key per user with a TTL, not IP, because of NAT. I time-box the Redis call so a slow limiter never adds latency to every bet. And I decide Redis-down behaviour deliberately: fail-open with a local fallback for rate limiting so the platform stays up, but fail-closed for wallet debits — never move money without the guarantee. The limiter is just one layer; the slow, smart bot that stays under the limit gets caught by async anomaly detection on the bet stream."

---

❓ **Question: Your BullMQ settlement worker pulls a job, starts crediting wallets, but the job takes longer than its lock/visibility timeout. BullMQ assumes it's dead and gives the job to a second worker. Now two workers settle the same round. What's happening and how do you prevent it?**

🎯 This is the **visibility-timeout / lock-renewal** failure — the dark side of at-least-once. The broker can't tell "slow worker" from "dead worker," so once the lock expires it redelivers for liveness. Result: **concurrent duplicate processing**, not just sequential redelivery.

```
  worker A: ──takes job──[ crediting... still crediting... ]──lock EXPIRES──ack(too late, rejected)
  worker B:                         ──lock free, takes SAME job──[ credits AGAIN ]
                                                       ▲ both ran → double settle
```

Three layers of defense, in order:

1. **Idempotency is the real safety net** (per the wallet answer): a unique idempotency key per settlement so even concurrent double-processing produces one effect. This must exist regardless — it's the only thing correct under *all* races, including the case where worker A is still running when B starts. Lock tuning reduces frequency; idempotency guarantees correctness.
2. **Keep the lock alive** — BullMQ renews the lock via `lockDuration`/`lockRenewTime` heartbeats; size `lockDuration` well above realistic job time, and let the renewer heartbeat for long jobs. Don't set it so tight that a GC pause or event-loop stall expires it. (Note the renewer runs on the same event loop, so a truly blocked worker stops renewing — which is the correct behaviour, since you *want* a wedged worker to lose its lock, and idempotency covers the overlap.)
3. **Make jobs short / chunked** — don't settle 50k wallets in one job; fan out one job per wallet (or small batches) so each unit finishes well inside the lock, partial failure only retries a slice, and you get parallelism across workers instead of one long serial job.

The mindset to voice: **never rely on the lock for correctness — only for efficiency.** Locks reduce wasted duplicate work; the idempotency key is what makes the outcome correct. Treat every job as "may run more than once, possibly concurrently." A useful tell that you've got it right: if you imagine two copies of the worker running the same job in parallel, the wallet still ends up credited exactly once.

🎮 **Gaming angle:** Settling a big match's bet pool is a long job; under load a worker can blow its lock and a second picks it up. Per-bet idempotency keys mean the overlap is harmless — the second run is a no-op — while chunking per bet keeps each unit fast, lets retries hit only the failed slice rather than re-crediting the whole pool, and spreads settlement across the worker fleet so a 100k-bet round drains fast.

🗣️ **Say this:** "That's the visibility-timeout problem — the broker can't distinguish a slow worker from a dead one, so it redelivers and now two workers run the same job concurrently. The real fix is idempotency: a unique settlement key so even concurrent double-processing yields one effect — locks are for efficiency, never correctness. On top of that I tune `lockDuration` and let BullMQ heartbeat the lock for long jobs, mindful that an event-loop stall stops renewal — which is fine, because idempotency covers the overlap. And I chunk work — one job per bet instead of settling the whole pool — so each unit finishes inside its lock, retries only touch the failed slice, and settlement parallelizes across the fleet. I always assume a job can run more than once, even in parallel, and design so two concurrent copies still credit exactly once."

---

I'll review this section as a Staff Backend Engineer, then return the polished Markdown. Let me think through the technical accuracy carefully before rewriting.

Key technical issues I'm checking:
1. The claim that `crypto.createHash` has async threadpool-backed variants — this is **inaccurate**. `crypto.createHash` is synchronous. The threadpool-backed crypto is `crypto.pbkdf2`, `crypto.randomFill`, `crypto.scrypt`, etc. SHA-256 hashing via `createHash` runs on the main thread. This is a real correctness bug to fix.
2. Socket.io heartbeat direction and reconnect behavior — generally fine.
3. `findOneAndUpdate` atomicity — correct.
4. cluster shared socket / stickiness — correct.

Let me produce the polished section.

## ⚡ Node.js Async Model & Concurrency

---

❓ **Question:** Walk me through the event loop phases. A player reports that during peak hours our live-odds WebSocket feed "freezes" for ~200ms every few seconds. How do the event loop and microtask queue explain what could be happening?

🎯 Node runs your JS on a **single thread** driven by libuv's event loop, which cycles through ordered phases. Between every callback (and at each phase boundary), Node drains the **microtask queues** to empty.

```
   ┌─> timers          (setTimeout / setInterval callbacks)
   │   pending callbacks (deferred I/O errors, e.g. some TCP ECONNREFUSED)
   │   idle/prepare    (internal)
   │   poll            ← retrieve I/O events, run I/O callbacks, may BLOCK here
   │   check           (setImmediate callbacks)
   └─< close callbacks (socket 'close', etc.)

  After EACH callback:  drain process.nextTick queue → then Promise microtasks
```

Key ordering facts a senior should nail:
- `process.nextTick` queue drains **before** the Promise microtask queue, and **both** fully drain before the loop advances to the next phase.
- `setImmediate` fires in **check**; `setTimeout(fn, 0)` fires in **timers** on the next loop iteration. Inside an I/O callback, `setImmediate` deterministically beats `setTimeout(0)`; at the top level the order isn't guaranteed.
- A flood of recursive `nextTick`/microtasks can **starve** the loop — I/O never gets serviced because the microtask queue never empties, so the loop never reaches **poll**.

The 200ms freeze is the loop being **blocked synchronously**, almost always inside a **poll-phase callback** (where your I/O handlers run): a big `JSON.parse`/serialize, sync crypto, a heavy in-process `.sort()`/`reduce` on a leaderboard, a sync transform of a Mongo/Redis result, or a runaway microtask chain. While that runs, the loop can't return to **poll** to pick up new socket data, so every WebSocket client stalls in lockstep — exactly the periodic, synchronized freeze described. The "every few seconds" rhythm is the tell: it lines up with a recurring tick (a broadcast interval, a metrics flush, a per-batch recompute).

🗣️ **Say this:** "Node is single-threaded with libuv running ordered phases — timers, poll for I/O, check for setImmediate, close — and after every callback it drains microtasks, nextTick before promises. A periodic freeze across *all* sockets at once is the signature of the loop blocking synchronously inside a callback: heavy parse/serialize, sync crypto, a big in-memory sort on the hot path, or a runaway microtask chain. While JS runs, the loop can't get back to poll to read sockets, so every client stalls together. The few-second rhythm usually maps to a recurring tick — a broadcast or recompute. I'd confirm with `--prof`, `clinic flame`, or event-loop-delay metrics (`perf_hooks.monitorEventLoopDelay`), then move the hot spot off the loop — chunk it, cache it, or push it to a worker thread or Redis."

🎮 **Gaming angle:** Rebuilding a 100k-entry leaderboard in-process with `Array.sort` on every score update will freeze the odds feed for everyone — and it scales O(n log n) per update, so it gets worse as you grow. Keep it in a Redis **sorted set**: `ZADD` is O(log n) per update and `ZREVRANGE`/`ZRANGEBYSCORE` reads top-N off your event loop entirely. For "rank around me," `ZREVRANK` is O(log n) too — no full scan.

---

❓ **Question:** Why is "don't block the event loop" existentially important for a real-time server specifically, versus a normal REST API where it's "just slow"?

🎯 In a request/response REST API, blocking degrades **throughput and tail latency** — bad, but bounded and usually masked by horizontal scaling. In a real-time server, the same single thread is **simultaneously** responsible for *thousands of persistent connections*. Blocking has fan-out:

```
REST:        block 50ms  → this one request is 50ms slower
Real-time:   block 50ms  → ALL 40k sockets miss 50ms of frames,
                           heartbeats, and writes — at once
```

Concrete consequences on a live platform:
- **Heartbeats stall** → Socket.io/WS ping-timeout fires → clients believe they disconnected and **reconnect-storm**, dumping *more* CPU (auth, handshake, room re-join) onto the already-busy loop — a cascading failure.
- **Backpressure builds** — outbound `socket.write()` buffers grow in memory while the loop is stuck, spiking RSS; if you don't watch buffer state you can OOM under load.
- **Ordering/fairness breaks** — one expensive handler delays event delivery to everyone, so the "live" feed isn't live, and head-of-line blocking means a slow message for one client can delay the broadcast for all.

The fix posture: keep per-event work O(small) and predictable, push CPU-bound work to **worker threads**, fan out heavy broadcasts via a **Redis pub/sub adapter** so no single process serializes for every client, run **multiple processes** (cluster/Fargate tasks) behind a sticky load balancer, and shard rooms so a hot match isn't all on one process.

🗣️ **Say this:** "On a REST API a blocked loop makes one request slow, and autoscaling hides a lot of sins. On a real-time server the one thread is multiplexing tens of thousands of live sockets, so a 50ms block costs *every* connected player 50ms at once — missed frames, stalled heartbeats. And it's self-amplifying: ping timeouts trigger mass reconnects that pile auth and room-join work back onto the loop, while outbound buffers balloon RSS. So 'don't block the loop' isn't a style rule, it's the line between 'a bit slow' and a platform-wide brownout. I keep handlers tiny, offload anything CPU-heavy, and I watch event-loop lag as a first-class SLO, not just request latency."

🎮 **Gaming angle:** A reconnect storm during a marquee match is the classic real-time death spiral — one slow handler trips heartbeats, 40k clients reconnect simultaneously, auth + room-join floods the loop, and the shard browns out. Defenses: sticky ALB routing, a Redis Socket.io adapter, tiny handlers, plus **jittered client reconnect backoff** and a **connection rate-limit/circuit breaker** at the edge so the herd doesn't return all at once.

---

❓ **Question:** We need to validate provably-fair game outcomes — SHA-256 over a server seed + client seed + nonce, hashed thousands of times per second, plus occasional batch reconciliation that's genuinely CPU-bound. How do you keep that off the event loop?

🎯 First, kill a common myth: **`crypto.createHash('sha256')` is synchronous and runs on the JS thread** — there is no async, threadpool-backed SHA-256 in core. The libuv threadpool only backs specific async crypto APIs like `crypto.pbkdf2`, `crypto.scrypt`, `crypto.randomBytes`/`randomFill`, and the key-generation functions. So for hashing you have to be deliberate:

1. **Lean on how cheap SHA-256 actually is.** A single SHA-256 over a few hundred bytes is microseconds. "Thousands per second" of *individual* hashes interleaved between I/O is usually fine on the main thread — the danger is doing them in a **tight synchronous batch** (e.g. verifying 50k rounds in one loop), which blocks the loop for that whole batch. Measure before you optimize.

2. **Worker threads for sustained or batched CPU work** — batch reconciliation, large verification sweeps, anti-cheat replay analysis, and any bulk-hashing loop. A `Worker` runs real JS on its **own thread, with its own event loop and V8 isolate**; you pass data over a `MessagePort` (structured clone by default, or zero-copy by **transferring** an `ArrayBuffer`, or shared via `SharedArrayBuffer`).

```
Main loop ──postMessage(job)──▶ [ Worker Pool ]   (Piscina)
   ▲                              cpu-bound JS
   └──────── result ◀─────────────┘   (main thread stays free)
```

Use a **pool** (e.g. **Piscina**) rather than spawning a worker per request — worker startup is expensive (a fresh V8 isolate, ~tens of ms + memory). Rule of thumb: pool size ≈ number of CPU cores (minus one for the main thread on a busy box). And know the boundary: worker threads are for **CPU-bound** work only; they do *nothing* for I/O-bound work (Mongo/Redis are already async and off-thread), and the message-passing/serialization cost means small jobs can be slower in a worker than inline — so don't reach for them by default.

🗣️ **Say this:** "First, a correctness point a lot of people get wrong: `crypto.createHash` is synchronous — SHA-256 isn't on the libuv threadpool, only things like pbkdf2, scrypt, and randomBytes are. But a single SHA-256 is microseconds, so thousands of *interleaved* hashes on the main thread are fine; the killer is a tight synchronous batch. So I split by shape: individual provably-fair checks stay inline, and genuinely CPU-bound or batched work — reconciliation, anti-cheat replay, bulk verification sweeps — goes to a Piscina worker-thread pool sized to core count, passing big buffers as transferables to avoid copying. The boundary I keep clear: worker threads help only CPU-bound work; for I/O the async APIs already get you off the loop and a worker would just add serialization overhead."

🎮 **Gaming angle:** Real-time per-round provably-fair verification is cheap enough to stay inline. The thing that hurts is a **batch reconciliation or anti-cheat replay sweep** — those are pure, sustained CPU. Run them in a worker pool, or better, in a **separate Fargate service / scheduled job / Lambda** so a reconciliation run is fully isolated from — and can't steal CPU from — the live game socket serving active tables.

---

❓ **Question:** ECS Fargate gives our task, say, 4 vCPUs. Node uses one thread for JS. How do you actually use all 4 cores, and how does that interact with the cluster module vs. just running more containers?

🎯 One Node process ≈ one JS thread ≈ ~one core of *JS* work (the libuv threadpool and GC use a bit more in the background). To use 4 cores of JS you need **4 processes**. Two ways:

- **`cluster` module (or PM2):** a primary forks N workers that **share one listening socket**; the OS/Node distributes incoming *connections* across them (round-robin by default on Linux, with caveats). One container, multiple processes.
- **More containers/tasks:** run 1 process per task and let the **ALB** spread load across tasks; scale by task count and let ECS autoscaling drive it.

```
   ALB ──► Fargate task (4 vCPU)
            ├─ node worker 1 ┐
            ├─ node worker 2 ├─ share :3000 (cluster)  OR  4 small tasks
            ├─ node worker 3 │
            └─ node worker 4 ┘
```

Trade-offs the senior view cares about:
- Processes **don't share memory** — in-process caches, rate-limit counters, and Socket.io rooms are **per-process**. Anything stateful must be externalized to **Redis** (rate limits, sessions, pub/sub fan-out) — true whether you cluster or scale tasks.
- WebSockets need **sticky sessions** (ALB stickiness) *and* a Redis adapter: a client's socket lives on exactly one process, but events that should reach it can originate anywhere. Stickiness routes the connection consistently; the adapter routes the *events*.
- cluster load-balances at **connection** granularity, so with long-lived WebSockets one worker can end up hot while others idle — connections don't rebalance after they're established. Task-level scaling plus the ALB gives you cleaner, observable distribution.
- In containers, the cleaner cloud-native pattern is usually **one process per task, scale horizontally** — simpler CPU/memory limits, per-process isolation (one crash ≠ all workers gone), straightforward autoscaling, and a clean mapping to ECS task metrics. cluster shines when you want to fill a big multi-core box without orchestration, but on Fargate you're already paying for orchestration, so let it do the work.

🗣️ **Say this:** "Node does JS on one thread, so on a 4-vCPU task you need four processes to use four cores — either the cluster module forking workers that share the listen socket, or four separate Fargate tasks behind the ALB. Either way the hard part is identical: processes share no memory, so anything stateful — rate-limit counters, sessions, Socket.io rooms — moves to Redis, and WebSockets need sticky routing *plus* a Redis pub/sub adapter, because stickiness pins the connection but the adapter delivers the events. One gotcha: cluster balances per-connection, so with long-lived sockets a worker can get hot and never rebalance. In a container world I default to one process per task and scale task count — cleaner limits, isolation, and autoscaling that maps straight to ECS metrics."

🎮 **Gaming angle:** With multiple processes/tasks, a player's socket lives on one process but a "goal scored" or "round settled" event might be produced by another (or by a background worker). The **Redis Socket.io adapter** publishes that event so it reaches the right rooms across *all* processes — without it, only the players on the originating process see the update and the rest silently fall behind.

---

❓ **Question:** We stream large data — match replays to S3, CSV exports of transaction history, ingest of a high-volume event firehose. Explain streams and backpressure, and what breaks if you ignore them.

🎯 A stream processes data in **chunks** instead of buffering the whole payload in memory. **Backpressure** is the flow-control signal that stops a fast producer from overwhelming a slow consumer.

`writable.write()` returns `false` when its internal buffer exceeds `highWaterMark`. The contract: **stop writing and wait for the `'drain'` event** before continuing. `pipe()` / `pipeline()` honor this automatically; if you write manually, you must respect the return value.

```
fast producer ──▶ [ writable buffer ]  ──▶ slow consumer (S3 / disk / net)
                   ▲ returns false when full
                   └─ pause until 'drain'   ← backpressure
```

What breaks if you ignore it:
- **Unbounded memory growth → OOM.** Keep calling `write()` while it returns `false` and chunks pile up in process memory. A firehose into a slow S3 upload will OOM-kill the Fargate task.
- **Whole-payload loads defeat the point.** `fs.readFile`, `.toArray()` on a huge Mongo cursor, or building one giant string — same OOM, just sooner and more certain.
- **Leaked descriptors / half-written objects on error.** If you wire streams by hand and one errors, the others aren't torn down — you leak file descriptors/sockets and can leave a truncated S3 object.

Do it right with `pipeline` (propagates errors and destroys every stream on failure — no leaks):

```ts
import { pipeline } from 'node:stream/promises';
await pipeline(
  mongoCursor.stream(),         // Readable: results in chunks
  csvTransform,                 // Transform
  s3Upload.writableStream(),    // Writable: backpressure-aware
);                              // auto pause/resume, auto cleanup on error
```

For Mongo, use a **cursor stream**, not `.toArray()`. For multipart S3 uploads, prefer the SDK's managed `Upload` (lib-storage) so part buffering is bounded. And for high-volume ingest, the queue/log itself (Kafka/BullMQ/Redis Streams) *is* your backpressure boundary — consumers pull at their own rate and lag is your visible signal, rather than buffering unboundedly in process memory.

🗣️ **Say this:** "Streams move data chunk-by-chunk so you never hold the whole payload in memory; backpressure is the feedback loop — `write` returns false when the buffer's past its highWaterMark and you wait for `drain` before continuing. Ignore it and a fast producer feeding a slow sink — a firehose into S3, a giant CSV export — buffers unboundedly and OOM-kills the task. I use `stream.pipeline`, which wires pause/resume *and* propagates errors and destroys every stream on failure, so I don't leak descriptors or leave a half-written S3 object. I never `.toArray()` a huge Mongo result — I stream the cursor. And for ingest I let the queue or log be the backpressure boundary so consumers pull at their own rate and I can watch lag."

🎮 **Gaming angle:** Exporting a season of wallet transactions for compliance, or shipping multi-GB match replays to S3 — stream the Mongo cursor straight through a transform into a multipart S3 upload. `.toArray()` on millions of rows OOMs the task mid-export and, because it's the *same* process serving live sockets, it takes live traffic down with it. For the inbound event firehose, treat consumer **lag** (Kafka/Redis Streams) as the health metric — rising lag means apply backpressure or scale consumers, never grow an in-memory buffer.

---

❓ **Question:** Our long-running gaming service slowly climbs in memory over days until the Fargate task gets OOM-killed and recycled. How do you reason about GC and hunt a Node memory leak?

🎯 V8 GC is **generational**: short-lived objects live in the **young generation** (cheap, frequent *scavenge*); survivors get promoted to the **old generation** (expensive *mark-sweep-compact*). A "leak" in Node usually isn't GC failing — it's objects still being **reachable**, so GC correctly *can't* collect them. (Distinguish this from healthy sawtooth growth and from RSS staying high after a spike because V8 doesn't always return freed pages to the OS — that's fragmentation/retention, not necessarily a leak.)

Classic Node leak sources:
- **Unbounded module-level `Map`/array caches** — e.g. a per-player object map never evicted. Use an **LRU with a max size/TTL**.
- **Listeners not removed** — `emitter.on(...)` per connection without `off`/`removeListener`; watch for `MaxListenersExceededWarning`.
- **Closures capturing big objects** held alive by long-lived timers/intervals (a `setInterval` that closes over a request payload pins it forever).
- **Globals / unbounded queues** that only grow.
- **Socket/room/state maps not cleaned on `disconnect`.**

How to hunt it:
```
1. Trend RSS / heapUsed in CloudWatch — sawtooth (healthy) or staircase (leak)?
2. Take heap snapshots over time (--inspect, or node --heapsnapshot-signal=SIGUSR2).
3. Diff snapshots → object counts that only GROW between captures.
4. Follow the "retainer" chain to the GC root holding them → that's your leak.
```

Also tune and guard: set `--max-old-space-size` *below* the container's memory limit so V8 runs GC and ideally crashes cleanly before the kernel OOM-killer hits (a V8 heap OOM gives you a stack/heap dump; a kernel SIGKILL gives you nothing). Alert on the RSS/heap trend, capture `--heapsnapshot-near-heap-limit` automatically, and treat steady old-gen growth across snapshots as the smoking gun.

🗣️ **Say this:** "V8 GC is generational — cheap scavenges for young objects, costly mark-sweep for the old generation. A Node leak is almost never GC breaking; it's objects staying *reachable*, so GC correctly keeps them. Usual suspects: unbounded caches, listeners added per-connection and never removed, and closures pinned by long-lived timers. I confirm it's a leak when heapUsed trends as a staircase, not a sawtooth, then take heap snapshots over time and diff them — whatever object count only grows is the leak, and I follow the retainer chain to the GC root. Fixes are usually an LRU bound, cleaning up listeners and state on disconnect, and setting `--max-old-space-size` below the container limit so V8 OOMs cleanly with a heap dump instead of getting SIGKILL'd with no evidence."

🎮 **Gaming angle:** The #1 real-world culprit: per-connection state — the socket→player map, room membership, in-process rate-limit counters — added on `connect` but never torn down on `disconnect`. Over multi-day uptime with constant player churn, that map grows forever. Always clean up on `disconnect` (and on the `error`/`close` paths too, since a hard drop may skip your tidy handler), and bound every in-process cache.

---

❓ **Question:** A wallet debit `await`s a Mongo update, but a rejected promise somewhere crashes the process — or worse, *doesn't*. Walk me through unhandled rejections and the async pitfalls that bite a financial workflow.

🎯 An **unhandled rejection** is a rejected promise with no `.catch`/`try-catch` in its async chain. Since Node 15 the default mode is `throw` — it **crashes the process** (`unhandledRejection` → non-zero exit). That's *safer* than silently continuing on corrupt state — but only if you've designed your writes to be recoverable on restart.

The pitfalls that bite financial code:

- **Floating promises** — calling an async function without `await`ing or attaching `.catch`. The work runs detached; if it rejects you get an unhandled rejection, and worse, your handler may have **returned success before the debit actually committed**.
  ```ts
  wallet.debit(userId, amount);       // ❌ floating — not awaited
  await wallet.debit(userId, amount); // ✅
  ```
  (Turn on `@typescript-eslint/no-floating-promises` — this is a lint-catchable class of bug, and on a wallet you want it as a hard error.)
- **Sequential awaits in a loop** when calls are independent — slow; use `Promise.all`. But for *dependent financial steps*, sequential is **correct** — don't parallelize a debit and the credit that depends on it.
- **`Promise.all` fails fast** — one rejection rejects the whole thing but **doesn't cancel the others**; the already-started operations still run, so partially-applied side effects can leave inconsistent state. Use `Promise.allSettled` when you need to know exactly what succeeded — and for true all-or-nothing across multiple writes, that's a **transaction**, not a Promise combinator.
- **`await` doesn't make it atomic.** Two concurrent debits can both read balance 100, both write 90 — a **double-spend**. Atomicity needs a DB-level guarantee: a conditional update (`findOneAndUpdate({ _id, balance: { $gte: amount } }, { $inc: { balance: -amount } })`), a Mongo **multi-document transaction** when several docs must move together (debit + ledger entry), or an **idempotency key** to make retries safe — *not* just `await`.

Operationally: keep the crash-on-rejection default, but add a top-level `process.on('unhandledRejection')` that **logs with full context and exits non-zero** so the orchestrator (ECS/Temporal) restarts a clean task — never swallow it to "stay up." The real safety net for money isn't the crash handler, it's that every write is **idempotent and reconcilable**, so a crash mid-flow plus a restart converges to a correct balance.

🗣️ **Say this:** "An unhandled rejection is a rejected promise with nothing catching it; since Node 15 it crashes the process by default, and I keep that — better to die and restart than run on corrupt wallet state. The financial pitfalls: floating promises, where you forget to await a debit and return success before it commits — I make `no-floating-promises` a hard lint error on wallet code. `Promise.all` fails fast but doesn't cancel the siblings, so already-started writes can leave inconsistent state — I use `allSettled` to know what happened, and a real transaction when I need all-or-nothing. And the big one: `await` doesn't mean atomic. Two concurrent debits can both read 100 and write 90 — a double-spend. That needs a conditional `$inc`, a transaction, or an idempotency key at the DB level. The deeper principle is that every money write should be idempotent and reconcilable, so a crash-and-restart converges to the right balance."

🎮 **Gaming angle:** No-double-spend is the wallet's whole job. Enforce it with an atomic conditional update — `findOneAndUpdate({ _id, balance: { $gte: amount } }, { $inc: { balance: -amount } })` returns `null` if the balance was insufficient, so concurrent bet placements can never overdraw, in a single round-trip with no read-modify-write race. Layer an **idempotency key** per transaction (unique index on it) so a client retry, a reconnect replay, or an at-least-once queue redelivery can't debit twice — the second insert hits a duplicate-key error and you return the original result. For multi-doc moves (debit + append to the ledger), wrap them in a Mongo transaction so the balance and the audit trail can't diverge.

---

I'll review and polish this section. The draft is strong, but there are a few technical inaccuracies and depth gaps a senior interviewer would catch. Let me return the improved version.

Key fixes I'm making:
- **`minimumHealthyPercent=100%` + `maxPercent=200%`** is correct for zero-downtime, but I'll clarify the actual rolling mechanics and add the deployment-circuit-breaker / auto-rollback point (a real senior-level miss in the draft).
- **Socket.io stickiness:** sticky cookies don't survive the WS upgrade cleanly and the Redis adapter is about pub/sub fan-out, not connection state — tightening that.
- **Secrets injection:** ECS resolves secrets at task launch only — I'll make the "rotation needs a redeploy/restart" tradeoff explicit (the draft hints but undersells it).
- **Lambda + Mongo:** RDS Proxy doesn't apply to MongoDB — that's an inaccuracy. Fixing to Mongo-appropriate pooling guidance.
- Sharpening the gaming/financial angle and a couple of "Say this" lines.

---

## ☁️ AWS & DevOps

---

❓ **Question:** Walk me through how a code merge to `main` actually becomes a running container serving live traffic on ECS Fargate. Hit every stage — build, push, deploy, health check, drain.

🎯 The end-to-end flow, with each AWS primitive doing one job:

```
git push main
   │
   ▼
GitHub Actions runner
   ├─ npm ci && npm test          (fail fast — no broken image)
   ├─ docker build (multi-stage)  → small prod image
   ├─ OIDC → assume AWS role      (no long-lived CI keys)
   ├─ aws ecr get-login-password  → docker push :gitsha (+ :latest)
   ├─ register NEW task-def revision (points at :gitsha)
   └─ aws ecs update-service --task-definition <rev>
         │
         ▼
ECS scheduler reads the Task Definition (image, CPU/mem, env, secrets, IAM role)
   ├─ launches NEW tasks on Fargate (no EC2 to manage)
   ├─ registers them with the ALB Target Group
   ├─ ALB health checks each task: GET /health → 200 ?
   │     • healthy  → starts routing traffic
   │     • unhealthy after N tries → ECS kills, retries
   ├─ rolling update: minimumHealthyPercent=100%, maximumPercent=200%
   │     (spins up new before draining old → zero downtime)
   ├─ deployment circuit breaker → auto-rollback if new tasks
   │     never stabilize (don't sit broken at 2am)
   └─ OLD tasks → "draining": ALB stops new conns, lets
         in-flight requests finish (deregistration delay), then stops
```

Key points a senior should name explicitly:
- **Task Definition is the unit of deploy** — it's an immutable, versioned spec (image tag, CPU/mem, env vars, secret ARNs, IAM task role). You don't "edit a server," you register a new revision and point the service at it. Note `--force-new-deployment` only re-pulls the *same* task-def (useful for `:latest` or config picked up at boot); a real code deploy registers a *new* revision.
- **The image tag should be the git SHA**, not just `:latest`. `:latest` is mutable and makes rollbacks ambiguous — two deploys can resolve to different images behind the same tag. SHA tags give you a deterministic "redeploy revision N" rollback and a clean audit trail.
- **Health check gating is what makes it safe** — traffic only shifts once new tasks pass the ALB target-group health check. A bad deploy never receives traffic if `/health` fails, and the **deployment circuit breaker** rolls back automatically instead of leaving you degraded.
- **Rollback = point the service back at the previous task-def revision.** It's one API call because revisions are immutable — no rebuild, no race.

🗣️ **Say this:** "A merge to main triggers GitHub Actions. It runs tests, builds a multi-stage Docker image, tags it with the git SHA, and pushes to ECR — and the runner authenticates to AWS via OIDC, so there are no long-lived CI keys. It registers a new task-definition revision pointing at that SHA and calls `ecs update-service`. ECS spins up new Fargate tasks, registers them with the ALB target group, and the ALB only routes traffic once they pass `/health`. It's a rolling deploy with minimum-healthy 100% and max 200%, so new tasks come up before old ones drain — zero downtime. Old tasks go into draining: the ALB stops new connections but lets in-flight requests finish over the deregistration delay. I turn on the deployment circuit breaker so a bad deploy auto-rolls-back instead of sitting broken. And because revisions are immutable, a manual rollback is one API call — point the service at the previous revision, deterministic, no rebuild."

🎮 **Gaming angle:** For a wallet/settlement service you want `minimumHealthyPercent=100%` so you never drop capacity mid-deploy during peak betting, plus a deregistration delay long enough that an in-flight bet-settlement isn't cut off when a task is replaced. And you deploy *between* events, not during a live match — the safest rollout window is the one where money isn't moving.

---

❓ **Question:** We run Socket.io for live game events over an ALB. After a deploy, clients keep disconnecting and some can't reconnect at all. What's wrong and how do you fix it at the load-balancer level?

🎯 Two distinct problems, both ALB-related:

**1. WebSocket session affinity during the handshake.** Socket.io opens with HTTP long-polling and *then* upgrades to WebSocket. During that polling phase the multiple HTTP requests **must hit the same backend task** — the handshake/session state lives on one task. Without stickiness, poll 2 lands on a different task → "session ID unknown" → reconnect loop.

```
Client ──polling handshake──▶ ALB ──▶ Task A   (creates session)
Client ──polling poll 2─────▶ ALB ──▶ Task B   ✗ unknown session
```

Fix: enable **ALB target-group stickiness** (the ALB-generated `lb_cookie`) so a client pins to one task through the handshake. **Better fix for real-time:** force the pure WebSocket transport (`transports: ['websocket']`) so there's no polling phase at all — it's a single upgraded TCP connection, stickiness becomes largely irrelevant, and you scale horizontally cleanly. (Caveat: some corporate proxies block raw WS, so keep polling as a fallback only if your audience needs it.)

Then — separately — use the **Redis adapter** (`@socket.io/redis-adapter`) so a broadcast on one task reaches clients connected to other tasks. To be precise: the adapter is **not** about connection routing or shared session state — each socket still lives on exactly one task. It's a **pub/sub backplane**: `io.emit('odds:update', …)` on Task A publishes to Redis, every task subscribes and re-emits to its own locally-connected sockets.

**2. Deploys kill long-lived connections.** WebSockets are long-lived; a rolling deploy drains old tasks and those sockets die. You can't avoid that — you make it graceful:
- ALB **idle timeout** must exceed your heartbeat/ping interval, or the ALB silently closes "idle" sockets between pings.
- On `SIGTERM`, stop accepting new sockets, optionally emit a `reconnect` hint, and rely on the client reconnecting with **jittered backoff** so 50k clients don't stampede the new tasks simultaneously (a thundering-herd reconnect can take down the fresh fleet you just deployed).

```
sticky (lb_cookie)  →  fixes handshake routing
websocket transport →  removes the polling phase entirely
redis-adapter       →  cross-task broadcast fan-out (THE scaling fix)
idle timeout        →  stop silent ALB disconnects
graceful drain      →  controlled, jittered reconnect on deploy
```

🗣️ **Say this:** "Two things. First, affinity — Socket.io starts on long-polling before upgrading, and those polling requests must hit the same task, so without ALB stickiness you get 'unknown session' loops. I'd enable the ALB cookie, but the real fix is forcing the WebSocket transport so there's no polling phase to route. Separately I run the Redis adapter — and I'm precise that it's a pub/sub backplane for broadcasts, not shared connection state; each socket still lives on one task, Redis just fans an emit out to every task. Second, the ALB idle timeout has to exceed the heartbeat or the LB silently drops sockets. Rolling deploys inevitably kill long-lived connections, so on SIGTERM I stop new connections and let clients reconnect with jittered backoff — otherwise 50k simultaneous reconnects stampede the fresh tasks."

🎮 **Gaming angle:** The Redis adapter is non-negotiable for a live leaderboard or in-play odds feed — when you emit `odds:update` to 100k clients spread across 20 Fargate tasks, the backplane fans it out so every task pushes to its local sockets. Without it, only the ~5k clients on the emitting task ever see the price move — and in-play, a stale odds display is a mispricing/liability problem, not a cosmetic one.

---

❓ **Question:** How do you get secrets — Mongo URI, JWT signing key, payment-provider API key — into a Fargate container *without* baking them into the image or committing them? And how is that locked down?

🎯 Use **AWS Secrets Manager** referenced directly from the task definition. ECS resolves them at task launch and injects them as env vars — they never touch the image or your repo.

```jsonc
// task definition
"secrets": [
  { "name": "MONGO_URI",   "valueFrom": "arn:aws:secretsmanager:...:secret:prod/mongo-uri"   },
  { "name": "JWT_SECRET",  "valueFrom": "arn:aws:secretsmanager:...:secret:prod/jwt-signing" }
]
```

The locking-down is **two IAM roles, doing different jobs** — a senior must distinguish them:

- **Execution role** — used by the *ECS agent* to pull the image from ECR and **read the secret at launch**. Needs `secretsmanager:GetSecretValue` (and the KMS `kms:Decrypt` on the secret's CMK) scoped to those exact secret ARNs.
- **Task role** — the identity your *application code* assumes at runtime for AWS SDK calls (S3 put, SQS send, etc.). This is what your app uses; it should NOT have blanket secrets access.

**Least privilege** means:
- Scope `GetSecretValue` to specific secret ARNs, not `*` — and lock the KMS decrypt grant too.
- Separate secrets per environment (`prod/…`, `staging/…`) so staging credentials can't read prod.
- No long-lived IAM *user* keys in the container — the task role gives short-lived, auto-rotated credentials via the container credentials endpoint.

**The honest tradeoff a senior names:** ECS injects secrets **only at task start**. So when Secrets Manager rotation runs (a Lambda rotates the DB password and updates the secret), **already-running tasks keep the old value** until they're replaced — you need a rolling restart / `--force-new-deployment` to pick up the new secret. The "tasks pick it up automatically" claim is only true for *newly launched* tasks. If you need true hot reload without a restart, you fetch from Secrets Manager via the SDK at runtime (using the task role) and cache with a TTL — at the cost of more moving parts.

The app itself never sees an AWS access key for the env-var path — it just reads `process.env.MONGO_URI`, and AWS handled injection.

🗣️ **Say this:** "Secrets live in Secrets Manager and are referenced by ARN in the task definition under `secrets`, so ECS injects them as env vars at launch — nothing's in the image or the repo. Access is gated by two IAM roles: the execution role lets the ECS agent read the secret and KMS-decrypt it at launch, scoped to those exact ARNs; the task role is the runtime identity my code uses for AWS calls and deliberately has no broad secrets access. Least privilege means scoping to specific ARNs not wildcards, splitting prod and staging, and never putting static keys in the container. The one tradeoff I call out: the env-var injection happens only at task start, so rotation needs a rolling restart for running tasks to pick up the new value — if I need true hot reload I read from Secrets Manager via the SDK at runtime with a cached TTL instead."

🎮 **Gaming angle:** The payment-provider key is the crown jewel — scope its `GetSecretValue` to only the wallet/settlement service's execution role, so a compromised chat or matchmaking service has no IAM path to read it. And because the key rotates on a schedule, the wallet service's deploy/restart strategy has to tolerate rotation without dropping in-flight settlements — that's exactly why rotation is a deploy concern, not just a security checkbox.

---

❓ **Question:** Show me a production Docker setup for a NestJS service. Why multi-stage, and what concretely does it buy you?

🎯 Multi-stage = build with the full toolchain, ship only the runtime artifact.

```dockerfile
# ---- stage 1: build ----
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci                      # full deps incl. devDeps (typescript, nest cli)
COPY . .
RUN npm run build               # tsc → dist/
RUN npm prune --omit=dev        # drop devDeps from node_modules

# ---- stage 2: runtime ----
FROM node:20-alpine AS runtime
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/package.json ./
USER node                       # don't run as root
EXPOSE 3000
# init process so SIGTERM reaches Node for graceful WS drain
ENTRYPOINT ["dumb-init", "--"]
CMD ["node", "dist/main.js"]
```

What it concretely buys you:
- **Smaller image** — the final image has no TypeScript compiler, no `@nestjs/cli`, no source `.ts`. Often 5–10x smaller → faster ECR pulls → faster Fargate cold start and scale-out.
- **Smaller attack surface** — no build tools or source maps shipped to prod; fewer CVEs to patch and a cleaner image scan.
- **No dev dependencies at runtime** — `npm prune --omit=dev` strips them.
- **Layer caching** — `COPY package*.json` then `npm ci` *before* `COPY . .` means the dependency layer is cached and only rebuilt when `package.json` changes, not on every code edit. Big CI speedup.

Other production must-dos: `USER node` (never run as root), **pin the base image by digest** (not just a moving tag) for reproducible builds, a proper **init/PID-1** (`dumb-init` or `--init`) so `SIGTERM` actually reaches Node — Node as PID 1 doesn't get default signal handling, which silently breaks graceful WebSocket drain — and a `.dockerignore` to keep `node_modules`/`.git`/`.env` out of the build context.

🗣️ **Say this:** "Multi-stage means a builder stage with the full toolchain — installs all deps, runs `nest build` to `dist`, prunes devDeps — and a slim runtime stage that copies only `node_modules` and `dist`. The final image has no TypeScript compiler or source, so it's several times smaller: faster ECR pulls, faster Fargate scale-out, smaller attack surface. I order the Dockerfile so `package.json` is copied and `npm ci` runs before the source copy, which caches the dependency layer so it only rebuilds when deps change. I run as non-root `node`, pin the base image by digest, and add an init process like dumb-init so SIGTERM actually reaches Node — without it, Node as PID 1 ignores the signal and my graceful WebSocket drain on deploy silently never fires."

🎮 **Gaming angle:** Faster cold-start matters when auto-scaling reacts to a spike at match kickoff — a 70MB image scales out in seconds; a 700MB image with the full build chain makes you slow to add capacity exactly when you need it. And the PID-1 detail is the difference between a clean deploy and one where every WebSocket client gets a hard-killed connection instead of a graceful reconnect hint.

---

❓ **Question:** Traffic is spiky — quiet for hours, then 50k users hit at a tournament start. How do you configure ECS auto-scaling and health checks so you add capacity fast but don't thrash or pay for idle?

🎯 ECS **Service Auto Scaling** via Application Auto Scaling, driven by CloudWatch metrics. Pick the policy type deliberately:

```
Target Tracking:   "keep avg CPU at 60%"  → AWS adds/removes tasks to hold target
Step Scaling:      "if CPU>80% add 4 tasks, if >90% add 8"  → reacts in tiers
Scheduled:         "at 19:55, set min=40 tasks"  → for KNOWN events (kickoff)
```

For a known tournament start, **scheduled scaling is the winner** — pre-warm before the spike rather than chasing it reactively, because reactive scaling has compounding lag (metric aggregation period + alarm evaluation + task launch + image pull + health-check pass = tens of seconds to minutes). Combine: scheduled floor for known events + target tracking for the unknown tail.

Tuning to add fast without thrashing:
- **Aggressive scale-out, conservative scale-in.** Short scale-out cooldown; long scale-in cooldown so you don't kill tasks the moment CPU dips and then immediately need them again (flapping).
- **Scale on the right metric.** CPU is fine for compute-bound work; for a WS/event service the real pressure is **connection count or a custom metric** (events/sec, BullMQ queue depth) — publish it and target-track on that, not CPU. A WebSocket task can be near-idle on CPU while pinned on connection count or memory.
- **Mind the real ceilings.** Fargate task launch isn't instant and you can hit account/service quotas or downstream limits — pre-warming sidesteps both. Your scaling is only as fast as your slowest dependency: if Mongo connections or a Redis instance saturate first, adding tasks just moves the bottleneck.
- **Health checks: separate liveness from readiness.** The ALB target-group check hits `/health` — keep it cheap (process is up, event loop responsive) and **don't fail it on transient downstream blips**, or ECS kills healthy tasks and you cascade. Set a **health-check grace period** so a slow-booting Nest app isn't killed before it finishes starting.

```
min=2 (always warm)  ───┐
target conn-count ──────┤→ Application Auto Scaling → CloudWatch alarms → add/remove tasks
scheduled min=40 @ event┘
grace period 60s        → don't kill tasks still booting
```

🗣️ **Say this:** "Two layers. For known events like a tournament start I use scheduled scaling to pre-warm — raise the min task count before kickoff, because reactive scaling has compounding lag: metric period, alarm eval, task launch, image pull, health check. You can't chase a 50k spike, you get ahead of it. For the unknown tail, target tracking on the metric that actually reflects load — for a real-time service that's connection count or events/sec via a custom metric, not raw CPU, because a WS task can be CPU-idle but connection-saturated. I scale out aggressively with a short cooldown, scale in slowly to avoid flapping, and I keep `/health` cheap so a transient downstream blip doesn't trigger a kill cascade, with a grace period so booting tasks survive. And I remember scaling tasks only helps if Mongo and Redis can take the load too — otherwise I've just moved the bottleneck."

🎮 **Gaming angle:** Pre-warming on a schedule is how you survive kickoff — by the time CPU-based reactive scaling notices the spike and launches tasks, the first wave of 50k players has already hit a saturated service and seen errors. You scale *ahead* of the event. And for a betting platform that error window maps directly to lost wagers and refunds during the highest-revenue minute of the night — the cost of being slow to scale is measured in money, not just latency.

---

❓ **Question:** A user reports a bet that "disappeared." How do you use CloudWatch to investigate, and how would you have set up logs, metrics, and alarms so you'd have known before the user told you?

🎯 Three distinct CloudWatch capabilities — a senior keeps them straight:

**Logs (forensics).** Fargate's `awslogs` driver ships container stdout to CloudWatch Logs. Investigate with **Logs Insights**:
```sql
fields @timestamp, @message, level, betId, userId, traceId
| filter betId = "abc123"
| sort @timestamp asc
```
This only works if you log **structured JSON with a correlation ID** (traceId/requestId) threaded through every service — a Nest logger plus an interceptor (or AsyncLocalStorage) that stamps the trace ID on every log line. Then you can reconstruct the bet's entire lifecycle across services from one query. (Caveat: scope queries by log group and time window — Logs Insights bills per GB scanned, and an unbounded query over weeks of logs is slow and expensive.)

**Metrics (trends & symptoms).** Emit custom metrics — `bets.placed`, `bets.settled`, `wallet.debit.failed`, BullMQ queue depth. A divergence between `bets.placed` and `bets.settled` is the disappeared bet showing up as a *number* before any user complains. Prefer **EMF (Embedded Metric Format)** — log a structured line and CloudWatch extracts the metric — so you're not making a synchronous `PutMetricData` call on the hot path of a bet.

**Alarms (proactive).** CloudWatch Alarms on those metrics → SNS → PagerDuty/Slack:
```
ALARM if  wallet.debit.failed > 0 for 1 min            → page (financial)
ALARM if  bets.placed - bets.settled  > threshold       → settlement lag (metric math)
ALARM if  bullmq.queue.depth > 1000 for 5 min           → worker stuck / backed up
ALARM if  ALB 5xx rate > 1%                             → service degraded
ALARM if  p99 latency > 500ms                           → degradation
```

The principle: **you should learn about the disappeared bet from an alarm, not a user.** Logs tell you *what happened* to one bet; metrics tell you *something is wrong* across all bets; alarms *tell you first*. The placed-vs-settled alarm uses **CloudWatch metric math** to alert on the divergence directly.

🗣️ **Say this:** "First, forensics: every service logs structured JSON with a correlation ID threaded through the request via an interceptor or AsyncLocalStorage, shipped by the awslogs driver. I'd run a Logs Insights query filtering on that bet ID, scoped to its log group and time window, and reconstruct its lifecycle across services in one timeline. But the real answer is I shouldn't hear it from the user. I emit custom metrics — placed, settled, wallet-debit failures, queue depth — using EMF so I'm not calling PutMetricData on the hot path, and I alarm on any wallet-debit failure and on a placed-versus-settled divergence using metric math. Logs tell me what happened to one bet; metrics and alarms tell me something's wrong across all of them, before a customer notices."

🎮 **Gaming angle:** For wallets, any non-zero `wallet.debit.failed` or a placed-vs-settled divergence is a **money-correctness signal, not a perf signal** — it pages immediately, because a "disappeared bet" is a financial discrepancy and a trust/regulatory issue, not just a bug. And the structured-log + traceId discipline isn't optional here: when a regulator or a dispute asks you to reconstruct exactly what happened to one customer's stake, that immutable, queryable trail *is* your answer.

---

❓ **Question:** When would you reach for Lambda instead of running something on ECS Fargate? Give concrete examples from a gaming platform, and name the traps.

🎯 Rule of thumb: **Fargate for long-running, stateful, connection-heavy services; Lambda for short, event-driven, bursty, stateless work.**

```
Fargate (always-on container)        Lambda (event → run → die)
─────────────────────────────        ──────────────────────────
WebSocket / Socket.io gateway        S3 upload → generate thumbnail
GraphQL API, game session server     Scheduled cron (daily report)
BullMQ workers (steady load)         React to an S3 / SQS / EventBridge event
anything holding live connections    sporadic webhook from payment provider
```

Concrete gaming uses for Lambda:
- **Asset processing** — player uploads an avatar to S3 → S3 event triggers Lambda → resize/virus-scan → write back. No server sits idle waiting.
- **Scheduled jobs** — EventBridge cron → Lambda generates the daily settlement report or expires stale sessions.
- **Webhook fan-in** — payment provider posts a settlement callback; Lambda validates the signature and drops it on a queue for the wallet service.
- **Glue/ops** — react to a CloudWatch alarm, rotate a secret, prune logs.

The traps a senior names:
- **Cold starts** — a cold Lambda (especially in a VPC, fetching a connection) adds latency. Never put it on the hot path of a live bet or a real-time event where users feel 100–800ms. That's Fargate's job. (Provisioned concurrency mitigates it but you're now paying to keep it warm — at which point ask why it isn't just Fargate.)
- **15-minute max** and stateless — no good for long jobs or anything holding a connection.
- **Concurrency limits + downstream blast radius** — a spike of S3 events can fan out to thousands of concurrent Lambdas and **hammer Mongo's connection pool**. Each cold Lambda is its own execution environment with its own connections, so 1,000 concurrent invocations can mean 1,000+ Mongo connections — the pool tops out and the *Fargate* services sharing that cluster start getting connection errors too. Use **reserved concurrency** to cap it, or front it with **SQS** to smooth the burst into a steady drain rate.
- **Mongo connection reuse** — instantiate the client **outside** the handler so it's reused across warm invocations on the same execution environment, never per-invocation. Note RDS Proxy is a relational/RDS feature — it doesn't apply to MongoDB; for Mongo you lean on Atlas's own pooling, capping concurrency, and SQS buffering instead.

🗣️ **Say this:** "Fargate for anything long-running, stateful, or holding connections — the WebSocket gateway, the GraphQL API, the BullMQ workers. Lambda for short, bursty, event-driven work: an S3 upload triggering avatar processing, an EventBridge cron generating the daily settlement report, a payment webhook that validates the signature and just enqueues. The traps: cold starts, so never Lambda on the hot path of a live bet; the 15-minute limit; and concurrency blast radius — a burst of events fans out to thousands of Lambdas, each with its own Mongo connections, so it exhausts the pool and takes the Fargate services sharing that cluster down with it. I cap with reserved concurrency or buffer through SQS, and I instantiate the Mongo client outside the handler so warm invocations reuse it — and I'm aware RDS Proxy doesn't help here because it's relational-only, so for Mongo it's Atlas pooling plus concurrency caps."

🎮 **Gaming angle:** Live odds delivery and bet placement stay on always-warm Fargate — you can't afford a cold start when a user taps "place bet," and you can't risk a Lambda burst starving the wallet service's Mongo pool. Lambda handles the off-path work around it: post-match settlement reports, avatar processing, expiring idle sessions. Right tool, right latency budget, and the financial path stays insulated from the bursty one.

---

I'll review and polish this section. Let me work through it carefully, applying senior-level technical scrutiny and strengthening the gaming/real-time angle throughout.

## 🛡️ REST API Design & High Availability

---

❓ **Question**

*"We expose a public REST API for our game-integration partners. How do you version it so that next year's breaking changes don't take down a live operator mid-tournament?"*

🎯
Versioning is a contract-stability problem. The key rule: **never break an existing consumer; add, don't mutate.**

Strategies, in order of preference for a partner-facing API:

```
URI versioning      GET /v1/wallets/123      ← explicit, cache-friendly, dead simple
Header versioning   Accept: application/vnd.pixen.v2+json   ← "purer" REST, but invisible in logs/CDN rules
Query param         GET /wallets/123?version=2   ← avoid: breaks cache keys & gateway routing
```

For a gaming platform with external operators I default to **URI versioning** (`/v1`, `/v2`) because it's visible in logs, trivial to route at the ALB/API gateway, easy to put a CDN/cache rule on, and partners can read it off the URL. The tradeoff is purists call it "unRESTful" (the URI should identify the resource, not its representation) — I take that hit because operational clarity beats theoretical purity for a partner-facing contract. Internally I treat the version as a routing concern, not a code-duplication concern: one service layer, version-specific DTOs/serializers at the edge, so business logic isn't forked.

Discipline that actually matters more than the scheme:
- **Additive changes are non-breaking** (new optional field, new endpoint, new enum value *only if* clients are told to tolerate unknowns) → no version bump.
- **Breaking changes** (removed/renamed field, narrowed type, changed semantics, new *required* request field, tightened validation) → new version.
- Run **N and N-1 concurrently**, announce a deprecation window with `Deprecation` / `Sunset` headers, and track per-version traffic *per partner* in CloudWatch so you know not just *when* v1 is quiet but *who* is still on it to chase.

```
Deprecation: true
Sunset: Sat, 31 Jan 2026 23:59:59 GMT
Link: <https://docs.pixen.io/v2/migration>; rel="deprecation"
```

🗣️ **Say this:**
"For partner-facing APIs I use URI versioning — `/v1`, `/v2` — because it's explicit, cacheable, and routable at the gateway. It's technically less 'pure' than header-based content negotiation, but for an external contract operational visibility wins. The scheme matters less than the discipline: additive changes never bump the version, only genuine breaking changes do. I run old and new side by side, emit `Sunset` and `Deprecation` headers, and watch per-version traffic *per partner* in CloudWatch — so I know exactly who to chase before retiring v1, and no operator gets surprised mid-tournament. Internally I keep one service layer with version-specific DTOs at the edge so I'm not maintaining two copies of the business logic."

🎮 Gaming angle: An operator integrated against `/v1` during a live tournament can't tolerate a schema change. Concurrent versions + Sunset headers let them migrate on *their* maintenance window, not yours — and per-partner traffic metrics mean you never blind-kill a version that one high-GGR operator is still calling.

---

❓ **Question**

*"A payment partner complains our errors are unparseable — every endpoint returns errors in a different shape. How should a REST API return errors?"*

🎯
Standardize on **RFC 9457 `application/problem+json`** (which obsoletes RFC 7807). One machine-readable envelope across every endpoint:

```jsonc
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/problem+json

{
  "type": "https://docs.pixen.io/errors/insufficient-funds",
  "title": "Insufficient funds",
  "status": 422,
  "detail": "Wallet 8f3a balance is below the requested stake",
  "instance": "/v1/wallets/8f3a/debit",
  "traceId": "req-01HZX...",        // extension member
  "balance": 4.50,                  // extension member, machine-usable
  "required": 10.00                 // amounts as machine fields, not prose
}
```

Principles:
- **`type` is a stable URI**, not prose — clients branch on it, not on the human `title`/`detail`. The URI need not resolve, but it must be stable forever.
- HTTP status carries transport semantics; the `type` carries domain semantics. `422` alone isn't enough — the partner needs to know *which* of 15 business failures it was.
- Put machine-usable values in **typed extension members** (`balance`, `required`), not interpolated into `detail` — clients should never regex the prose.
- Include a **`traceId`** that maps to your logs/X-Ray so support can correlate a partner's complaint to a request in seconds.
- **Never leak internals** (stack traces, Mongo error text, internal IDs) in `detail`.
- Distinguish **4xx (client must change the request) from 5xx (server's fault, retry may help)** clearly — clients use this to decide whether to retry. Be deliberate with the validation code: `400` for malformed syntax, `422` for syntactically-valid-but-semantically-rejected, `409` for state conflicts.

In NestJS this is **one global `ExceptionFilter`** that maps domain exceptions → problem+json, so the shape is guaranteed everywhere and individual handlers never hand-roll error bodies. Domain errors are thrown as typed exception classes carrying their `type` URI and extension fields; the filter is the single serialization point.

🗣️ **Say this:**
"I standardize on `application/problem+json` from RFC 9457 — a single error envelope with a stable `type` URI, a status, a human-readable detail, a traceId, and typed extension fields. The `type` URI is the contract: clients branch on that, never on the message text, and machine values like balance go in extension fields, not interpolated into prose. In NestJS I enforce it with one global exception filter that maps typed domain exceptions to problem+json, so every endpoint is consistent and I never leak stack traces or Mongo errors. And I keep 4xx-versus-5xx honest — 422 for a business rejection, 5xx for our fault — because partners use exactly that to decide whether retrying is even worth it."

🎮 Gaming angle: A wallet debit can fail for *insufficient funds*, *self-exclusion / responsible-gaming lock*, *velocity/AML limit*, *market already settled*, or *duplicate idempotency key* — all logically "422", but a stable `type` URI lets the operator's client react differently to each (block the user, show a cooldown, silently treat the duplicate as success) without string-matching our messages.

---

❓ **Question**

*"Our leaderboard endpoint returns the top players and our `/transactions` history endpoint is paginated. Walk me through cursor vs offset pagination — when do you use which?"*

🎯
**Offset pagination** (`?page=3&limit=20` → `skip(40).limit(20)`) is simple but has two fatal flaws at scale:

```
1. Drift: rows inserted/deleted between page loads cause
   items to repeat or vanish ("page 2 shows what page 1 just showed").
2. Cost: skip(N) makes Mongo *walk and discard* N docs even with
   a perfect index — skip(1_000_000) is O(N). Kills high-write collections.
```

**Cursor (keyset) pagination** encodes a pointer to the last item and queries *past* it:

```
GET /v1/transactions?limit=20
→ { data:[...], nextCursor: "eyJjcmVhdGVkQXQiOiIuLi4iLCJfaWQiOiIuLi4ifQ" }

// cursor decodes to {createdAt, _id} of last row, then:
db.tx.find({
  $or: [
    { createdAt: { $lt: cur.createdAt } },
    { createdAt: cur.createdAt, _id: { $lt: cur._id } }  // tiebreaker for equal timestamps
  ]
}).sort({ createdAt: -1, _id: -1 }).limit(20)
// compound index on {createdAt:-1, _id:-1}; scans only what it returns — O(limit), drift-free
```

Decision:

| Use offset | Use cursor |
|---|---|
| Small, bounded, stable data | Large or high-write collections |
| Need "jump to page 7" / total count | Infinite scroll / feeds / history |
| Admin tables | Transaction logs, activity feeds |

The cursor **must be opaque** (base64 of the sort tuple — so you can change the encoding later without breaking clients), **must include a unique tiebreaker** (`_id`) so non-unique sort keys don't skip or duplicate rows, and the **exact sort tuple must be covered by a compound index** in the same order/direction or the `$or` keyset scan degrades. One caveat to state honestly: keyset can't random-access page N, so if a screen genuinely needs total counts or jump-to-page, that's where offset (or a separately-maintained counter) still earns its place.

🗣️ **Say this:**
"Offset is fine for small, stable, admin-style data where you need a total count or jump-to-page. But it breaks at scale two ways: `skip(N)` makes Mongo walk and throw away N documents even with an index, and concurrent inserts make rows repeat or disappear between pages. For transaction history and feeds I use cursor — keyset — pagination: an opaque base64 cursor encoding the last row's sort key plus `_id` as a tiebreaker, querying past it on a compound index in the same sort order. That's O(limit), index-backed, and drift-free regardless of write volume. The honest tradeoff is you give up random page access and cheap total counts — so I only reach for it where infinite-scroll semantics fit."

🎮 Gaming angle: A leaderboard mutates every second — offset pagination would show duplicate or skipped players as ranks shift mid-scroll. For the *top-N* leaderboard itself I wouldn't page Mongo at all; I'd serve it from a **Redis sorted set** — `ZREVRANGE` for the top slice, `ZREVRANK` for a single player's rank, `ZINCRBY` to update score in O(log N) on every event. Cursor pagination is for the deep transaction/bet history behind it, where the data is append-mostly and immutable.

---

❓ **Question**

*"A user double-clicks 'Place Bet', or their mobile retries on a flaky network. We charged the wallet twice. How do you make a POST safe to retry?"*

🎯
You can't make POST naturally idempotent — so you add an **idempotency key**: the client sends a unique key per logical operation, and the server guarantees the side effect happens **at most once**.

```
POST /v1/bets
Idempotency-Key: 7f1c-…(client-generated UUID, stable across retries of the SAME logical op)

Server flow:
┌──────────────────────────────────────────────────────┐
│ 1. Atomically claim the key (Redis SET NX, or a        │
│    unique index on Mongo {key} inserted as PENDING)    │
│ 2a. Claim won  → process bet, then persist RESULT+key  │
│ 2b. Claim lost → key already PENDING/DONE:             │
│       - DONE     → return the STORED response verbatim │
│       - PENDING  → 409 (in flight, ask client to retry)│
└──────────────────────────────────────────────────────┘
```

Critical details:
- **The claim must be atomic.** `SET key val NX EX 86400` in Redis, or a unique index in Mongo — the datastore, not application code, enforces uniqueness under concurrency. Two simultaneous requests race for the *write*, and exactly one wins.
- **Store and replay the original response** (status + body), so retries get the identical answer, not a fresh execution.
- **Scope the key + fingerprint the body**: store a hash of the request body against the key. A *reused key with a different body* is a client bug → reject with `422`, never silently serve the old result for a new request.
- **TTL** the key (e.g. 24h) — it's for retry windows, not permanent dedup.
- **Mind the failure window**: if you crash *after* charging the wallet but *before* writing the stored result, a retry must not double-charge. The robust shape is to make the key-claim and the financial write part of the **same atomic unit** (one Mongo transaction writing both the ledger entry and the idempotency record, or the key keyed off the ledger entry's own unique id) so "charged" and "recorded the key" can never diverge.

🗣️ **Say this:**
"POST isn't idempotent, so for anything that moves money I require an `Idempotency-Key` header — a client-generated UUID stable across retries of the same logical operation. On arrival I atomically claim the key with Redis `SET NX` or a Mongo unique index, so the datastore enforces uniqueness under concurrency, not my code. If the key's new I process the bet and persist the result against it; if it's already done I replay the stored response verbatim; if it's still in flight I return 409. I also fingerprint the body so a reused key with a different payload gets rejected, not silently mismatched. The subtle part is the crash window — if I charge the wallet but die before recording the key, a retry could double-charge — so I make the key record and the ledger write one atomic unit. That turns a double-click or a network retry into exactly one debit."

🎮 Gaming angle: Mobile clients on patchy connections retry constantly, and a 'cash out' tapped during a dropout is the dangerous one — without an idempotency key it becomes two payouts. The key plus an atomic ledger write is the difference between a balanced ledger and a finance incident with a regulator.

---

❓ **Question**

*"Our bet-settlement service calls a downstream odds provider. It got slow, our threads piled up waiting, and the whole API fell over. Walk me through the resilience patterns that prevent this."*

🎯
This is a **cascading failure** from an unbounded dependency. (Worth naming the Node nuance: there's no thread-per-request pool to exhaust like in a JVM — what piles up is in-flight promises, open sockets, and event-loop pressure, and the libuv/HTTP-agent socket pool *does* cap out. The failure mode is the same; the resource is different.) Five patterns work together:

```
        ┌── Timeout ──┐   ┌─ Circuit Breaker ─┐
caller ─┤  fail fast  ├──►│ stop hammering a   │──► downstream
        └─────────────┘   │ known-dead dep     │
        ┌── Retry ────┐   └───────────────────┘
        │ backoff+    │   ┌──── Bulkhead ─────┐
        │ jitter      │   │ isolate pools so   │
        └─────────────┘   │ one dep can't eat  │
        ┌─ Load shed ─┐   │ all capacity       │
        │ reject early│   └───────────────────┘
        └─────────────┘
```

1. **Timeouts** — *every* network call needs one (connect + overall request/socket). No timeout = unbounded socket hold = the pileup you described. Set it below the caller's own deadline budget, and propagate a **deadline** downstream so the whole chain stops working on a request the client already gave up on.

2. **Retries with exponential backoff + jitter** — retry only **idempotent / safe** ops, only on transient failures (timeout, 503, connection reset), never on 4xx.
   ```
   delay = min(cap, base * 2^attempt) ± random_jitter
   ```
   **Jitter is non-negotiable** — without it, 10k clients that failed at the same instant retry in synchronized waves (thundering herd) and re-knock the service over. Cap attempts (2–3), use a **retry budget** (cap retries as a % of total traffic so retries can't amplify load during an outage), and respect `Retry-After`.

3. **Circuit breaker** — track the failure rate; when it crosses a threshold, **open** the circuit and fail fast for a cooldown instead of waiting on every call. Then **half-open**: let a trial request through; success → close, failure → re-open. (This is also what stops retries from making things worse: an open breaker short-circuits the retry loop.)
   ```
   CLOSED ──fail rate > X%──► OPEN ──cooldown──► HALF-OPEN ──ok──► CLOSED
                                                      └──fail──► OPEN
   ```

4. **Bulkheads** — isolate resource pools per dependency (separate connection pool / concurrency limit per downstream). The odds provider's pool exhausting doesn't starve the wallet or auth path. Plus a **fallback**: serve last-known odds, queue the settlement for later, or degrade the market gracefully.

5. **Load shedding** — when *you* are the bottleneck, reject early (429) and protect core paths rather than collapsing globally. Shed low-value traffic first so wallets and auth survive while a cosmetic endpoint sheds.

🗣️ **Say this:**
"What you're describing is a cascading failure — an unbounded downstream call holding sockets and event-loop capacity until the whole service starves; in Node it's open connections and pending promises rather than threads, but the effect's identical. I layer four patterns plus load shedding. First, a timeout on every network call set under my own deadline budget, propagated downstream so nobody works on a request the client abandoned. Second, retries with exponential backoff *and* jitter — only on idempotent, transient failures — with a retry budget so retries can't amplify an outage. Third, a circuit breaker that trips open after a failure-rate threshold so I stop hammering a dead dependency and short-circuit the retry loop, then half-opens to probe recovery. Fourth, bulkheads — a separate pool per downstream — so one slow provider can't consume all my capacity, plus a fallback like last-known odds. And when I'm the bottleneck, I shed low-value traffic early to keep wallets and auth alive. Together they contain the blast radius to one dependency."

🎮 Gaming angle: During a live match the odds feed spikes hardest exactly when load is highest. A breaker + bulkhead means a flaky odds provider degrades *one* market to last-known prices instead of taking down wallets, auth, and every other market on the platform — and load shedding means that if anything has to give, it's a cosmetic endpoint, never the bet-placement or cash-out path.

---

❓ **Question**

*"We deploy to ECS Fargate behind an ALB. During deploys we sometimes drop in-flight bets and 502 users. Explain health checks and graceful shutdown so a deploy is zero-impact."*

🎯
Two separate concerns that people conflate: **health checks** (is this task fit to receive traffic?) and **graceful shutdown** (drain in-flight work before dying).

**Liveness vs readiness:**
```
Liveness  → "am I alive, or wedged/deadlocked?"  → fail ⇒ orchestrator RESTARTS me
Readiness → "can I serve traffic right now?"     → fail ⇒ load balancer STOPS routing to me
```
A task can be *live* but *not ready* — still warming caches, or **draining during shutdown**. Conflating them causes needless restarts or routing to a cold/dying task. Readiness should check real dependencies (Mongo, Redis reachable); liveness should be cheap and *not* fail on a downstream blip — if liveness depends on Mongo, a brief Mongo hiccup restarts every task at once and turns a blip into an outage.

> Note on ALB specifics: a classic ALB target group has **one** health check, which gates routing (readiness-style). True liveness/readiness separation is a Kubernetes concept; on ECS you approximate it — the ALB health check plays readiness, and ECS's own `essential` container + `stopTimeout` handles the restart/kill side. Say "readiness vs liveness" as the model, but know that on ALB the single health check is doing the readiness job.

**Graceful shutdown / connection draining on SIGTERM:**
```
1. ECS sends SIGTERM (then SIGKILL after stopTimeout).
2. Flip the health endpoint → 503  ── ALB sees unhealthy, stops sending NEW requests
3. ALB deregistration delay (connection draining) lets in-flight requests finish
4. Stop accepting new conns; let active HTTP requests drain
5. Drain workers: stop pulling new BullMQ jobs, finish/return in-flight ones
6. Close Mongo/Redis/sockets cleanly, then exit 0
```

The 502s come from the gap between "ECS started killing the task" and "ALB stopped routing to it." The fix is **ordering and timing**: mark unhealthy first, let the ALB deregister (set **deregistration delay > your worst-case drain time**), drain, then exit — and set ECS **`stopTimeout` longer than deregistration delay + worst-case in-flight request** so SIGKILL never lands mid-drain. In NestJS, `enableShutdownHooks()` + an `OnApplicationShutdown` hook flips readiness and drains BullMQ before exit. One Node gotcha: install the SIGTERM handler so it doesn't just `process.exit()` immediately — that's exactly what drops the in-flight bet.

🗣️ **Say this:**
"The 502s are a draining problem, not a health problem. Conceptually I separate liveness — am I wedged, restart me — from readiness — can I take traffic, stop routing to me; on ECS the ALB's single health check is effectively doing the readiness job. On deploy ECS sends SIGTERM, and my handler first flips the health endpoint to 503 so the ALB deregisters and stops sending new requests, then I let the deregistration delay and in-flight requests drain, stop pulling new BullMQ jobs while finishing the ones I've already pulled, close Mongo and Redis cleanly, and exit zero. The whole game is ordering and timing: mark unhealthy before you die, set the deregistration delay above your worst-case request and the ECS stopTimeout above *that*, so SIGKILL never lands mid-drain. In NestJS that's `enableShutdownHooks` with an `OnApplicationShutdown` handler — and critically, a SIGTERM handler that drains instead of calling `process.exit` immediately, which is the usual cause of dropped requests."

🎮 Gaming angle: An in-flight bet settlement killed mid-write is a financial inconsistency, not just a 502. Draining BullMQ — finish the jobs you've pulled, stop pulling new ones — is what guarantees a deploy never leaves a wallet half-debited; and because the worker isn't behind the ALB, its drain is governed by `stopTimeout`, not the health check, which is why the timing on both has to line up.

---

❓ **Question**

*"How do you roll out a risky change — say a new settlement engine — without a big-bang cutover? Compare blue-green and canary."*

🎯
Both avoid in-place mutation; they differ in *how* traffic shifts.

**Blue-green** — two full environments, instant switch:
```
        ┌─ BLUE  (v1, live) ─┐
ALB ───►│                    │   flip target group ⇒ 100% to GREEN
        └─ GREEN (v2, idle) ─┘   rollback = flip back (seconds)
```
- Pro: instant cutover, instant rollback, smoke-test green in isolation first.
- Con: 100% of users hit v2 the moment you flip — a bug hits everyone at once. Double the infra during the window. DB migrations must be backward-compatible (both versions may briefly run; in-flight v1 connections drain onto v1).

**Canary** — shift a *small %* first, watch, then ramp:
```
ALB ──► 95% v1 ─┐
        5%  v2 ─┘ → watch error rate / latency / business KPIs
                  → 5% → 25% → 50% → 100%, auto-rollback on regression
```
- Pro: blast radius is the canary %; real production traffic validates v2 before full exposure.
- Con: slower; *requires* solid observability + automated rollback to be safe; both versions serve concurrently, so contracts/DB must be compatible. Watch for state that doesn't honor the traffic split — sticky sessions, shared caches, a user landing on v2 then v1 on the next request.

For a **settlement engine touching money** I wouldn't bet only on traffic-percentage canarying. I'd go in order: **(1) shadow mode** first — the new engine consumes real events, writes to a *side* ledger, and a diff job compares it to v1 with zero financial exposure; **(2)** only once shadow diffs are clean, a **canary gated on business metrics** — settlement success rate, ledger-balance invariants, payout latency — not just HTTP 5xx, with **automated rollback** on any ledger discrepancy. The non-negotiable underneath both: **expand/contract (parallel-change) migrations** — add the new schema, write both, backfill, switch reads, *then* drop the old — so old and new run side by side throughout.

🗣️ **Say this:**
"Blue-green is two full environments with an instant target-group flip — great for fast rollback, but everyone hits the new version the moment you switch. Canary shifts a small slice first, say 5%, watches error rate and business KPIs, then ramps with automated rollback on regression, so the blast radius is just the canary. The canary gotcha is shared state — sticky sessions or a shared cache can make a user hop versions between requests, so the split has to be honest. For something touching money like a settlement engine, traffic percentage isn't enough: I shadow-run it first — process real events into a side ledger and diff against the live engine — to prove correctness with zero exposure, *then* canary gated on ledger invariants and settlement success, not just 5xx, with rollback on any ledger discrepancy. Both ride on expand-contract migrations so the two versions run side by side safely."

🎮 Gaming angle: A settlement bug doesn't 500 — it silently pays out the wrong amount, and you find out from finance, not your dashboards. That's why money-path rollouts are gated on *ledger* correctness and proven in shadow mode first; HTTP status is exactly the wrong signal for a bug whose symptom is a balanced-looking-but-wrong payout.

---

❓ **Question**

*"You're senior now — others build on your branches. What does a clean Git workflow look like on this team?"*

🎯
The goal is a **readable, bisectable history** and **small, reviewable PRs** — not Git trivia.

**Branching** — short-lived feature branches off `main` (trunk-based), merged via PR. Long-lived branches drift and create merge hell; keep them hours-to-days, not weeks. Pair this with **feature flags** so you can merge incomplete work to `main` dark and decouple deploy from release — which is what makes trunk-based actually work at speed.
```
main ──●────●────●────●──►   (always deployable)
        \        /
         feat/idempotency-keys   ← short-lived, one concern
```

**Commits** — atomic and conventional:
```
feat(wallet): add idempotency key claim on debit
fix(settlement): treat 409 from odds provider as transient
^type ^scope        ^imperative, present tense, explains WHY in the body
```
Each commit builds and passes tests (so `git bisect` lands on a real culprit, not a broken intermediate). Don't mix a refactor + a feature + a formatting sweep in one commit.

**PRs** — small (a few hundred lines), one logical change, clear description of *what and why*, linked issue, green CI required. Reviewable in one sitting. A 2,000-line PR gets rubber-stamped, which is where bugs hide.

**Integration hygiene** — rebase your *own unshared* feature branch on `main` to stay current and keep history linear; **squash-merge** trivial PRs so `main` stays clean; **never force-push or rebase a shared branch** others have based work on (rewrite their base and you've corrupted their history). Protect `main`: required reviews, required CI, no direct pushes.

🗣️ **Say this:**
"I run trunk-based: short-lived feature branches off main, main always deployable and protected with required reviews and green CI, and feature flags so incomplete work can merge dark and deploy is decoupled from release. Commits are atomic and conventional — type, scope, imperative subject, the *why* in the body — and each one builds and passes tests so `git bisect` lands on a real culprit, never a broken intermediate; I don't blend a refactor with a feature. PRs are small and single-purpose, because a giant PR just gets rubber-stamped and that's where bugs hide. I rebase my own branch on main to keep history linear, squash-merge the noise, and never rebase or force-push a branch teammates have built on. As a senior the point isn't Git cleverness — it's leaving a history the next person can read and bisect when settlement breaks at 2 a.m. during a live event."

🎮 Gaming angle: When a settlement bug surfaces during a live tournament, a clean bisectable history plus commits that each pass tests is how you `git bisect` to the offending commit in minutes instead of hours — and small PRs plus a feature flag are how you both catch it in review *and* kill it instantly without a redeploy if it does slip through.

---

## 🎮 Gaming Real-Time System-Design Scenarios

These are full mini system-designs. The bar for "senior" is not reciting components — it's voicing **tradeoffs, consistency boundaries, failure modes, and back-of-envelope numbers** without being asked. Drive every answer back to the gaming reality: thousands of concurrent players, money on the line, and cheaters probing your hot paths.

---

❓ **Question:** Design real-time live-event delivery — e.g. a live match or in-play betting feed — pushed to 100k+ concurrent players with sub-second latency. Walk me through the architecture.

🎯 The instinct to "just use Socket.io" is right but incomplete. The hard part isn't the socket — it's **fan-out** and **horizontal scale of stateful connections**.

```
                 ┌─────────────┐   pub/sub    ┌──────────────┐
  Event source → │ Event/Match  │ ───────────► │   Redis      │
  (game engine)  │  Service     │  (channel)   │  Pub/Sub or  │
                 └─────────────┘               │  Streams     │
                                               └──────┬───────┘
                                                      │ each WS node subscribes
                    ┌───────────────┬─────────────────┼─────────────────┐
                    ▼               ▼                  ▼                 ▼
              ┌──────────┐    ┌──────────┐       ┌──────────┐     ┌──────────┐
              │ WS node 1│    │ WS node 2│  ...  │ WS node N│     │ WS node N│
              │ ~10k conn│    │ ~10k conn│       │ ~10k conn│     └──────────┘
              └────┬─────┘    └────┬─────┘       └────┬─────┘
                   ▼ local fan-out to subscribed sockets in this room
              100k players
```

Key decisions:
- **Connection layer = ECS Fargate tasks behind an ALB** with WebSocket support and connection draining on deploy. ~10k connections per node, so 100k ≈ 10–12 nodes plus headroom. Note: with a Redis backplane you do **not** need ALB sticky sessions for message delivery — any node can serve any player. Stickiness only matters for Socket.io's HTTP long-polling fallback handshake; if you force the native WebSocket transport, you can drop stickiness entirely. Scale on **connection count and event-loop lag (p99)**, not CPU alone — a Node WS node is usually memory- and FD-bound long before CPU.
- **Fan-out = Redis Pub/Sub (or Redis Streams) as the backplane.** The event service publishes once; every WS node is subscribed and does the local fan-out to the sockets in that room. This is the `@socket.io/redis-adapter` pattern. One publish → N nodes → 100k frames. Without a backplane, a player on node 3 never sees an event emitted on node 7. Watch the cost model: with the adapter, every node receives every published message and filters to its local room members, so cross-node chatter grows with node count. At very high node counts you'd shard channels by match (`match:1234`) rather than one firehose, or move to Streams with consumer groups.
- **Rooms** scope delivery: `match:1234`. You only push to subscribers of that match, not all 100k.
- **Pub/Sub vs Streams tradeoff** — voice this: plain Pub/Sub is fire-and-forget with **at-most-once** delivery (a node that's briefly disconnected, or a slow consumer, silently misses messages — Redis does not buffer for Pub/Sub subscribers). For an in-play **betting/odds** feed where a missed update is a financial/fairness problem, use **Redis Streams** (persisted log, consumer groups, replay via `XRANGE`/`XREAD` from a known ID) so a reconnecting client can catch up. For pure cosmetic feeds (chat, kill feed), Pub/Sub is fine and cheaper. Either way, attach a monotonic **sequence number** to every event so a client can detect a gap regardless of transport.
- **Backpressure & slow clients:** never let a slow socket buffer unboundedly — that's how one bad mobile connection OOMs a node holding 10k sockets. Cap per-connection outbound buffer (`ws` exposes `socket.bufferedAmount`); on overflow, drop the client (they reconnect and resync from latest state). For high-frequency feeds, **coalesce/conflate** — for an odds feed you only care about the *latest* price, so send the latest snapshot every 100–250ms rather than every micro-update. This bounds bandwidth and CPU independent of how fast the source ticks.
- **Auth:** validate the JWT once on the WebSocket upgrade/handshake, not per frame. The subtlety: a long-lived socket can outlive a short-TTL token, so for money-touching sessions either re-validate on a timer / re-auth on reconnect, or carry a server-side revocation check (a Redis "banned/loggedout" set) so you can kill a compromised session mid-match without waiting for token expiry.
- **Failure handling:** WS node dies → ALB drops those connections → clients auto-reconnect (exponential backoff **+ jitter** to avoid a synchronized thundering herd of 10k reconnects) to a healthy node, resubscribe, and replay from their last seq. The Redis backplane is the critical dependency, so run **ElastiCache in cluster or replica mode, Multi-AZ**, and treat a Redis blip as "clients resync on recovery," not data loss — for ephemeral feeds the current state is re-derivable.

🗣️ **Say this:** "I'd terminate WebSockets on a fleet of stateless Fargate nodes behind an ALB, roughly ten thousand connections each, so a hundred K is about ten to twelve nodes. With a Redis backplane I don't need sticky sessions — any node can serve any player. The real work is fan-out across nodes: the event service publishes each update once to Redis, every WS node subscribes and does local fan-out to the sockets in that match room. For a cosmetic feed I'd use plain Pub/Sub, which is at-most-once; for an in-play odds or betting feed I'd use Redis Streams with sequence numbers so a reconnecting client can replay what it missed — silently dropping a financial update isn't acceptable. I'd cap per-socket buffers and conflate high-frequency odds to a snapshot every couple hundred milliseconds, scale on connection count and event-loop lag rather than CPU, and handle node loss with client reconnect-with-jitter plus resync from last sequence."

🎮 Gaming angle: The "publish once, fan out at the edge" backplane is the single most important pattern in real-time gaming — leaderboards, match state, and live odds all ride it.

---

❓ **Question:** Design a real-time leaderboard — top 100 plus "your rank" — for a game with millions of players, updated as scores change. Why Redis and not Mongo?

🎯 This is the canonical Redis **Sorted Set** problem, and saying so fast signals seniority.

```
ZADD leaderboard:season42 <score> <playerId>      # O(log N) write
ZREVRANGE leaderboard:season42 0 99 WITHSCORES     # top 100, O(log N + 100)
ZREVRANK leaderboard:season42 <playerId>           # my rank, O(log N)
ZINCRBY leaderboard:season42 <delta> <playerId>    # bump score
```

- **Why not Mongo:** computing rank in Mongo means `countDocuments({score: {$gt: mine}})` or a sort over the whole collection per request — O(N) and brutal at millions of players × high read rate. A Redis sorted set keeps members ordered by score (skiplist + hash) and gives **rank, range, and top-N in O(log N)**. Mongo is the **source of truth / durable store**; Redis is the **serving layer**.
- **Data flow & the write path tradeoff:** score change → update Mongo (durable) → `ZADD`/`ZINCRBY` to Redis. Voice the consistency gap between the two stores: writing to two systems is not atomic, so a crash between them drifts the view. The robust pattern is to make Mongo the write, emit a change event, and let a consumer (BullMQ worker / change stream) project into Redis — that gives you one ordered write path and a rebuildable view. Redis-first is lower latency but risks losing the last few updates on a Redis failure unless AOF + replication is on; Mongo-first is safer but adds latency to the hot path. For a leaderboard, slightly stale ranks are acceptable, so I lean Mongo-write + async projection.
- **Use ZINCRBY, not read-modify-ZADD:** if scores are deltas (you earned +50), `ZINCRBY` is atomic server-side. Reading the current score into the app and writing back a new total reintroduces a lost-update race under concurrent score events — the same TOCTOU trap as the wallet.
- **Ties:** sorted sets break score ties by lexical member order, which is arbitrary. For deterministic tie-breaks (earlier achiever ranks higher), **encode a composite score**: since Redis scores are IEEE-754 doubles (only ~53 bits of integer precision), pack `score` in the high bits and an inverted timestamp in the low bits within that budget, so a higher score wins and, on a tie, the earlier timestamp wins. Validate the bit budget — naive `score * 1e13 - timestamp` silently loses precision past 2^53.
- **Scale / sharding:** millions of members in one sorted set is fine memory-wise (~tens of bytes/member), but a single hot key lives on **one Redis Cluster shard**, so all writes for that board hit one core — Redis is single-threaded per shard. Shard by season/region/segment (`leaderboard:{region}:{season}`), which spreads write load **and** matches how players actually view boards. A global "all regions" board is then a periodic merge job, not a hot path.
- **"Your rank" at scale:** exact rank via `ZREVRANK` is cheap. If you ever need approximate rank for tens of millions cheaply, bucket scores into ranges (a coarse histogram) and interpolate — but start exact.
- **Failure handling:** Redis is a cache/view — if it's lost, replay from Mongo (or the event log) to rebuild. Enable AOF + replica for faster recovery. Never treat the leaderboard Redis as the only copy of score data.

🗣️ **Say this:** "A leaderboard is a Redis sorted set, full stop — ZADD to write, ZREVRANGE for the top 100, ZREVRANK for a player's rank, all O(log N) on a skiplist. Mongo can't do rank without an O(N) count or full sort per request, so Mongo stays the durable source of truth and Redis is the serving view. For score deltas I use ZINCRBY so the increment is atomic and I don't reintroduce a read-modify-write race. I encode a composite score to break ties toward the earlier achiever, but I'm careful about double precision — Redis scores are 53-bit floats, so I pack score and timestamp within that budget. I shard boards by season and region, which spreads write load off a single hot shard and matches how players view them, and because the sorted set is a materialized view I can always rebuild it from Mongo — so a Redis failure is a recovery problem, not data loss. I keep AOF and a replica on for fast rebuilds, and I lean Mongo-write plus async projection into Redis since slightly stale ranks are fine for a leaderboard."

🎮 Gaming angle: Composite-score tie-breaking, ZINCRBY atomicity, and per-season/region sharding (one key = one shard) are exactly the details that separate a real game leaderboard from a tutorial one.

---

❓ **Question:** Design a player wallet — deposits, bets, payouts — that **never double-spends** even when the same player fires concurrent requests. This is real money.

🎯 The whole question is **concurrency + correctness on balance**. Two bets debiting the same balance at the same instant must not both succeed if only one is affordable.

Two layers of defense:

**1) Atomic, conditional balance updates (no read-then-write race).** Never `read balance → check → write` in app code; that's a TOCTOU bug. Make the debit a single atomic conditional operation:

```js
// MongoDB: debit ONLY if sufficient funds — atomic, condition in the filter
const res = await wallets.updateOne(
  { _id: playerId, balance: { $gte: amount } },   // guard in the query
  { $inc: { balance: -amount },
    $push: { ledger: { txId, type: 'BET', amount: -amount, ts: new Date() } } }
);
if (res.modifiedCount === 0) throw new InsufficientFundsError();
```

The `balance: { $gte: amount }` filter means the DB itself rejects the debit if funds dropped between requests. A single-document update in MongoDB is atomic and the matched write is conflict-checked, so concurrent debits serialize at the document — no lost update, no negative balance. (Note: `modifiedCount === 0` is ambiguous — it can mean "insufficient funds" *or* "no such player" *or* "idempotent replay that changed nothing." Re-read or branch on which, so you return the right error.)

**2) Idempotency (no double-apply on retries).** Network retries and at-least-once queues mean the same bet can arrive twice. Every transaction carries a client-generated **idempotency key (`txId`)**. Enforce it with a **unique index** so a replay is rejected, not re-applied:

```
db.transactions.createIndex({ txId: 1 }, { unique: true })
// duplicate txId → E11000 → treat as "already processed", return the prior result
```

- **Order matters under concurrency:** insert the `transactions` record (unique `txId`) **first**, then apply the wallet `$inc`. If you debit first and the idempotency insert is what dedupes, two in-flight copies of the same retry can both pass the `$gte` guard before either records the txId. Inserting the unique key first makes the *second* copy fail fast with E11000 before it touches the balance. For true all-or-nothing across the txn record + wallet + ledger, wrap them in one multi-document transaction (below).
- **Append-only ledger is the source of truth.** Balance is derived from (or a cached projection of) the sum of immutable ledger entries. This gives you auditability — non-negotiable for iGaming/financial and for regulator reconciliation — and lets you recompute balance if anything ever drifts. Keep a denormalized `balance` field for the fast `$gte` guard, but treat the ledger as canonical.
- **Multi-document atomicity:** if a single bet touches multiple docs (player wallet + house/jackpot pool + ledger), wrap them in a **MongoDB multi-document transaction** (replica set required; uses snapshot isolation, and a write conflict aborts and must be retried with backoff). For the single-doc wallet+ledger shown above, the document-level atomic update already gives ACID-per-document without the transaction overhead — prefer that on the hot path and reserve multi-doc transactions for genuinely cross-document moves.
- **Redis for speed, Mongo for truth — voice the tradeoff:** you *can* use a Redis Lua script (`DECRBY` guarded by a balance check, atomic because Redis runs the whole script single-threaded) as a fast pre-check or low-stakes session balance, but the **system of record for money must be the durable, transactional store (Mongo)**. Redis persistence is best-effort (AOF `everysec` can lose ~1s on crash; failover can lose acknowledged writes), so real-money correctness must never live *only* in a cache. Common pattern: Redis holds a fast-moving play balance for sub-millisecond checks, reconciled to and settled against the Mongo ledger.
- **Distributed locking** (Redlock / a per-player lock) is a fallback if you can't express the guard atomically — but prefer the conditional update; a lock is slower, serializes the player, and adds failure modes (lock expiry mid-operation → two holders; Redlock's safety is itself debated). Mention it to show you know it, then say why you'd avoid it here.
- **Failure handling:** queue payout side-effects via **BullMQ with the txId as the job id** so retries are deduped and idempotent; if the Mongo write succeeds but a downstream step (e.g. a payment provider call) fails, the ledger entry is the recovery anchor — replay forward from it, never silently re-debit. Reconcile asynchronously by re-summing the ledger against the cached balance and alerting on any drift.

🗣️ **Say this:** "Two rules. One, never read-then-write a balance — that's a race. I make the debit a single atomic conditional update: in Mongo the filter says balance greater-than-or-equal-to the amount, so the database itself refuses to debit if the funds aren't there, and concurrent debits serialize at the document. No negative balance, no lost update. Two, idempotency: every transaction has a client-generated txId with a unique index, and I insert that txId record *before* I touch the balance, so a retried or duplicated bet fails fast on a duplicate-key error and I return the prior result instead of double-spending. The append-only ledger is the source of truth and balance is derived from it, which gives the auditability iGaming and regulators require. If a bet spans the wallet, the house pool, and the ledger I wrap it in a Mongo multi-doc transaction and retry on write conflict. Redis can serve a fast session balance, but money settles against the durable Mongo ledger — correctness never lives only in a cache, because cache persistence is best-effort."

🎮 Gaming angle: Under a jackpot rush, the same player will hammer "bet" — the `$gte` guard plus a unique `txId` index inserted *before* the debit is what stops a double-spend without serializing every request behind a lock.

---

❓ **Question:** A hot endpoint — say `placeBet` or `spin` — is being hammered, partly by legit traffic and partly by bots/scripted clients trying to exploit timing. Design rate-limiting and anti-cheat for it.

🎯 Two distinct problems wearing the same hat: **protect the system (rate limit)** and **detect cheating (behavioral)**. Solve them in layers.

**Rate limiting — distributed, not per-instance.** With N Fargate nodes, an in-memory counter per node is useless (a player hits a different node each request, so the effective limit is N× what you set). The counter must be **centralized in Redis**, keyed by `playerId` (and/or IP).

```
-- token-bucket / sliding-window in Redis via atomic Lua (whole script runs atomically):
key = rl:placeBet:{playerId}
INCR key ; if first hit, EXPIRE key <window>     -- (fixed window — simplest, but allows boundary bursts)
if count > limit  → 429 + Retry-After
```

- Use a **Lua script** (or a battle-tested limiter like `rate-limiter-flexible`) so check-and-increment is atomic — otherwise two concurrent requests both read "under limit" and slip through. Prefer **sliding-window-log or token-bucket** over a fixed window: a fixed window lets an attacker fire a full quota at the end of one window and another at the start of the next — a 2× burst across the boundary. Token-bucket also lets you allow short legitimate bursts while bounding sustained rate, which fits real play better.
- **Layered keys:** per-player (fairness/abuse), per-IP (botnets/NAT — but be careful, mobile carriers and offices share IPs, so don't block IP hard), per-device, and a **global circuit breaker** to protect the backend. Stricter limits on money-moving and auth endpoints than on cosmetic ones.
- **Edge first:** also rate-limit at the ALB/WAF/CloudFront layer (AWS WAF rate-based rules) so obvious volumetric floods never reach Node and never cost you a Redis round-trip. Cheap, coarse defense at the edge; precise per-player defense in Redis. The two layers are complementary, not redundant.

**Anti-cheat — behavioral, asynchronous.** Rate-limiting stops volume; it doesn't catch a cheater playing at humanly-impossible *consistency*. Don't do this on the hot path:

- Emit every action as an **event** (to Kafka / Redis Streams / a BullMQ queue). A separate **anti-cheat worker** consumes the stream and scores behavior: inter-action timing below human reaction (sub-~100ms reliably), impossible or perfectly-flat win rates, perfectly periodic timing (a scripted bot's tell), input entropy that's too low, geo/device anomalies, and multi-account collusion (shared device fingerprints / coordinated timing across "different" players).
- Keep the synchronous path fast: cheap checks inline (rate limit, **server-authoritative validation** that the action is even legal and that the *server* — not the client — computed the outcome), expensive correlation offline. Server-authoritative outcome is itself anti-cheat: never trust the client's claimed result; the server computes the spin/roll from a server-side seed (use a CSPRNG, and for fairness disputes consider provably-fair commit-reveal hashing).
- Actions on detection: shadow-limit, flag for manual review, step-up auth / CAPTCHA, or freeze the wallet — **tiered**, because a false positive that freezes a paying player's funds is a support escalation and lost revenue. Bias automated actions toward reversible ones; reserve hard freezes for high-confidence signals.

```
client → [WAF rate rule] → ALB → Node: [Redis rate limit] → [server-authoritative validate] → respond
                                              └── emit action event ──► anti-cheat worker (async scoring) ──► flag/limit
```

- **Failure handling / fail-open vs fail-closed — voice it:** if Redis (the limiter) is down, do you fail open (let traffic through, risk abuse) or fail closed (block, hurt legit players)? For a money-moving endpoint I'd **fail closed, or degrade to a conservative in-process static limit** so I'm not wide open during the outage; for a cosmetic endpoint, fail open so a Redis blip doesn't break gameplay. State the choice and the blast radius either way.

🗣️ **Say this:** "I split it into protecting the system and catching cheaters. Rate-limiting has to be centralized in Redis keyed by player, device, and IP — with many Fargate nodes a per-instance counter just multiplies the real limit by the node count — and the check-and-increment runs as a Lua script so it's atomic, with a token-bucket or sliding window so attackers can't double up at the window boundary. I push a coarse rate rule to AWS WAF at the edge so volumetric floods never even reach Node. For anti-cheat, the synchronous path only does cheap, server-authoritative validation — the server computes the outcome from its own seed, I never trust the client's claimed result. Every action is emitted as an event to a separate worker that scores behavior offline: sub-human reaction times, perfectly periodic bot timing, impossible win rates, collusion via shared device fingerprints. Detection is tiered and biased toward reversible actions because falsely freezing a paying player's wallet costs real revenue. And I'd state the fail-mode explicitly: for a money endpoint, fail closed to a conservative static limit if Redis is down."

🎮 Gaming angle: "Server-authoritative outcome + Redis-atomic limiter + async behavioral scoring" is the standard anti-cheat spine — the client proposes, the server decides the result from its own seed, and a worker watches for inhuman patterns and collusion.

---

❓ **Question:** Design live match state synchronization — every player in a match sees a consistent, current game state with minimal latency, and players join/reconnect mid-match. How do you keep them in sync?

🎯 The core tension: **broadcasting every delta is fast but a late joiner has no context; sending full state every time is consistent but heavy.** You need both.

- **Authoritative server state.** One source of truth per match, not peer-to-peer — anything else is a cheating vector. Hold the **live, mutable match state in Redis** (a hash per match, `match:1234`), because it's read/written constantly and must be shared across all WS nodes. Periodically and at key moments, **persist snapshots to Mongo** for durability, history, and replay.

```
match:1234 (Redis hash)  →  authoritative state, seq number bumped on each change
   on change: HSET fields + INCR seq atomically (MULTI/Lua), then publish delta {seq, changes} to backplane
WS nodes subscribe → fan out delta to that room's sockets   (see live-event design)
```

- **Sequenced deltas + periodic snapshots.** Make the state mutation and the `seq` bump **atomic** (a Lua script or `MULTI`), so the sequence number can never disagree with the state it labels. Steady state pushes **deltas** with a monotonically increasing **seq** (low bandwidth). A **late joiner or reconnecting player fetches the latest full snapshot once** (current Redis state at seq = K), then **applies deltas with seq > K** — caught up and in order. A gap in seq (received K → K+2) is detectable → client requests a fresh snapshot/resync.

```
Join/reconnect:  GET snapshot (state @ seq=K)  →  apply deltas seq=K+1, K+2, …
Detect gap:      received seq jumps K → K+2     →  request fresh snapshot
```

  Subtlety to voice: there's a race between "read snapshot" and "start receiving deltas." The clean fix is to **subscribe to the delta stream first, buffer**, then fetch the snapshot at seq K, then drop buffered deltas ≤ K and apply the rest — otherwise a delta that fires during the snapshot read is lost.
- **Server-authoritative ticks:** the server advances state on a fixed tick and is the only authority on outcomes — clients send *intents* ("I moved / I acted"), the server validates against current state and applies. This is both correctness and anti-cheat. Where latency hurts feel, clients can do **prediction + server reconciliation** (apply the intent locally, snap to the server's authoritative state when it arrives) — call this out as the standard way to hide latency without giving up authority.
- **Consistency model — voice it:** this is **eventual consistency with a total order**, not strong/linearizable global consistency. You can't make 100k clients linearizable in real time, and you wouldn't want to pay that latency; instead the server's copy is the single linearization point and every client converges to the same state in the same order via the seq number. Clients are momentarily behind by network latency — acceptable for a match. Explicitly contrast: the wallet needs stronger guarantees (no lost update on money); match state tolerates a client being 80ms stale.
- **Scaling:** fan-out reuses the Redis backplane from the live-event design (publish once, WS nodes fan out per room). State shards naturally by match — `match:1234` is one key on one shard; matches are independent, so you scale horizontally by adding shards/nodes and hashing matches across them. A single match's write throughput is bounded by one shard, which is fine — a match has a handful of actors, not 100k writers.
- **Failure handling:** WS node dies → client reconnects to another node, pulls a snapshot, resumes from latest seq — the per-node connection is disposable because state lives in Redis, not on the node. Redis is HA (replica/cluster, Multi-AZ); on failover the last Mongo snapshot + the delta log bound any loss, and clients re-snapshot. If the delta stream is lost entirely, clients fall back to short-interval snapshot polling until it recovers.

🗣️ **Say this:** "The match has one authoritative server-side state — never peer-to-peer, or you've built a cheating vector. I keep the live mutable state in Redis as a hash per match because every WS node needs to read and write it, and I snapshot to Mongo periodically for durability and replay. Steady state, I broadcast deltas with a monotonic sequence number for low bandwidth, and I bump that seq atomically with the state change so they never disagree. A joiner or reconnecting player subscribes to the delta stream first, then pulls one snapshot at seq K, drops buffered deltas at or below K, and applies the rest — that ordering avoids losing a delta during the snapshot read. If they ever see a gap in the sequence, they request a fresh snapshot. The model is eventual consistency with a total order — I can't make a hundred K clients linearizable and wouldn't pay that latency, but the server is the single linearization point and the seq guarantees everyone converges to the same state in the same order. Clients send intents, the server is authoritative on outcomes, and clients can predict-and-reconcile to hide latency. Connections are disposable because state lives in Redis, so a node death is just reconnect-and-resync. This is the right tradeoff for match state but not for the wallet — money needs stronger guarantees."

🎮 Gaming angle: "Snapshot + sequenced deltas + server-authoritative tick, with client prediction and reconciliation" is how real multiplayer stays in sync; subscribe-then-snapshot and seq-gap-triggers-resync are what make reconnect-mid-match feel seamless.

---

## 🎤 Day-of Cheat Sheet

# 🎤 Day-of Cheat Sheet — Senior Backend Engineer @ Pixentech (Real-Time Gaming)

## NestJS Architecture & Internals
- **NestJS DI & modules** — Nest is a DI-first framework where modules group providers and the IoC container resolves singleton dependencies via constructor injection, keeping services decoupled and testable.
- **Request lifecycle** — A request flows middleware → guards → interceptors → pipes → handler → interceptors (response) → exception filters, so I hook cross-cutting concerns at the exact right layer.
- **Guards vs interceptors vs pipes** — Guards authorize (return boolean), pipes validate/transform input, and interceptors wrap the handler for logging, caching, and response shaping.

## GraphQL (deep dive — my growth area)
- **GraphQL core** — GraphQL exposes a single typed endpoint where clients request exactly the fields they need, eliminating over- and under-fetching versus fixed REST payloads.
- **Resolvers & schema** — The schema is the contract and each field has a resolver function `(parent, args, context, info)`, with `context` carrying auth and per-request DataLoaders.
- **N+1 & DataLoader** — Naive nested resolvers fire one query per parent, so I batch and cache them per-request with DataLoader to collapse N+1 into a single round trip.
- **Mutations & subscriptions** — Mutations mutate state and return the updated entity, while subscriptions push live updates over WebSockets — perfect for game state and live odds.

## MongoDB at Scale
- **Mongo ESR indexing** — I order compound indexes Equality → Sort → Range so the index satisfies the filter and avoids an in-memory sort on hot queries.
- **Sharding & scale** — Mongo scales horizontally by sharding on a high-cardinality key, and I pick the shard key to avoid hot partitions and scatter-gather fan-out.
- **Document modeling** — I embed for data read together and reference for unbounded or independently-updated data, designing around access patterns not normalization.

## Redis & BullMQ
- **Redis as primitive** — Redis is a single-threaded in-memory data-structure server I lean on for caching, rate limiting, distributed locks, and pub/sub fan-out.
- **Leaderboards via sorted sets** — Live leaderboards are O(log N) ZADD plus O(log N + M) ZREVRANGE on a sorted set, giving real-time ranks without scanning a table.
- **BullMQ retry/DLQ** — BullMQ runs jobs on Redis with exponential-backoff retries, and exhausted jobs land in a dead-letter queue for inspection instead of silently vanishing.

## Real-Time at Scale (WebSockets / Socket.io)
- **WebSocket fan-out via Redis adapter** — Socket.io's Redis adapter pub/subs across nodes so a message on one instance reaches clients connected to any instance, enabling horizontal scale.
- **Rooms & namespaces** — I scope broadcasts with rooms (per-table, per-match) so I push to exactly the right players instead of flooding every socket.

## Event-Driven & Distributed Systems
- **Event-driven design** — Services emit events to a broker (Kafka/SQS) and consumers react asynchronously, decoupling producers from consumers and absorbing traffic spikes.
- **Idempotent wallets** — Every wallet transaction carries an idempotency key so retries and duplicate events never double-credit, the non-negotiable invariant for real-money systems.

## Node.js Async Model & Concurrency
- **Event loop never blocks** — Node runs one JS thread on a libuv event loop, so I keep the loop free by offloading CPU-heavy work to worker threads and never doing sync I/O on the hot path.
- **Concurrency model** — Node is concurrent, not parallel, on the main thread — async I/O is non-blocking, but a tight CPU loop stalls every connection, so I profile and offload it.

## AWS & DevOps
- **ECS Fargate + ALB sticky sessions** — I run stateless containers on Fargate behind an ALB with sticky sessions so a player's WebSocket stays pinned to its instance while the fleet autoscales.
- **Observability & CI/CD** — I ship structured logs, metrics, and traces (CloudWatch/OpenTelemetry) through an automated CI/CD pipeline so I can deploy fast and debug production blind spots.

## REST API Design & High Availability
- **REST design** — I design resource-oriented, versioned APIs with correct status codes, pagination, and idempotent PUT/DELETE so clients can retry safely.
- **Circuit breakers** — I wrap flaky downstream calls in a circuit breaker that trips on sustained failure, shedding load and failing fast instead of cascading the outage.

## Gaming Real-Time System-Design Scenarios
- **Real-time game architecture** — Stateless WebSocket gateways fan out via Redis, authoritative game state lives in memory backed by Redis, and durable writes go async to Mongo to keep latency sub-100ms.
- **Consistency vs latency** — I serve hot game state from Redis for speed and reconcile to Mongo as the source of truth, trading eventual consistency everywhere except the wallet, which stays strongly consistent.
