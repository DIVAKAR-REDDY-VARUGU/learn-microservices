# 🛒 REST ↔ GraphQL — Every API Pattern, Full Flow

> One **Buyer/Seller/Product/Order** story. For each API pattern: **REST** request/response · **type contract** · **GraphQL** equivalent · the full **NestJS flow** (gateway → middleware → guard → interceptor → pipe → controller → service → repository → DB → response).

---

## 📦 Shared Foundation (domain, types, the flow legend)

> **This document is the single source of truth.** Every later section reuses the domain, the IDs/values, the schema, the TypeScript types, and the request-flow legend defined here. Do not redefine these elsewhere — link back to this file instead.

---

## 1. The Domain Story

We are building **MarketHub**, a marketplace where **Buyers** purchase **Products** from **Sellers**. When a buyer commits to a purchase, the system creates an **Order** made up of one or more **OrderItems** (line items). **Discounts** may apply to a product (or be carried onto a line item), and once an order is placed an **Invoice** is generated and moves through its own lifecycle.

### Entities at a glance

| Entity        | What it represents                          | Key fields                                                        |
|---------------|---------------------------------------------|------------------------------------------------------------------|
| **Seller**    | A merchant who lists products               | `id`, `name`, products[]                                          |
| **Buyer**     | A customer who places orders                | `id`, `name`, orders[]                                            |
| **Product**   | A listed item for sale                      | `id`, `name`, `price`, `cost`, `stock`, `discount?`, `status`, `sellerId` |
| **Order**     | A buyer's purchase from a seller            | `id`, `buyerId`, `sellerId`, items[], `status`, `total`          |
| **OrderItem** | One line in an order                        | `id`, `orderId`, `productId`, `qty`, `unitPrice`                  |
| **Discount**  | A price reduction applied to a product      | `id`, `code`, `percent`, `productId`                             |
| **Invoice**   | The billing document for an order           | `id`, `orderId`, `amount`, `status`, `issuedAt?`                 |

### Status enums

| Enum             | Values                                                       |
|------------------|-------------------------------------------------------------|
| `ProductStatus`  | `ACTIVE` · `OUT_OF_STOCK` · `ARCHIVED`                       |
| `OrderStatus`    | `PLACED` · `PAID` · `SHIPPED` · `DELIVERED` · `CANCELLED`    |
| `InvoiceStatus`  | `DRAFT` · `ISSUED` · `PAID` · `VOID`                         |

### Entity-Relationship diagram (ASCII)

```
                                  ┌──────────────┐
                                  │    SELLER    │
                                  │  id   name   │
                                  └──────┬───────┘
                           1            │           1
            ┌───────────────────────────┼───────────────────────────┐
            │ sells (1..*)              │ receives (1..*)            │
            ▼                           ▼                            │
     ┌──────────────┐            ┌──────────────┐                    │
     │   PRODUCT    │            │    ORDER     │◄───────────────────┘
     │ id  name     │            │ id  status   │
     │ price cost   │            │ total        │
     │ stock status │            └───┬──────┬───┘
     └──────┬───────┘                │      │
            │ 0..1                    │ 1    │ 1
            │ has                     │      │ generates
            ▼                  places │      ▼
     ┌──────────────┐    (1..*)│   ┌──────────────┐
     │   DISCOUNT   │          │   │   INVOICE    │
     │ id  code     │          │   │ id  amount   │
     │ percent      │          │   │ status       │
     └──────────────┘          │   └──────────────┘
            ▲                   │
            │ buys (1..*)       │ contains (1..*)
     ┌──────┴───────┐           ▼
     │    BUYER     │    ┌──────────────┐        refers to    ┌──────────┐
     │  id   name   │    │  ORDER_ITEM  │───────────────────► │ PRODUCT  │
     └──────────────┘    │ id  qty      │       (many..1)     │  (p1…)   │
                         │ unitPrice    │                     └──────────┘
                         └──────────────┘
```

**Relationship summary (read it as sentences):**

- A **Seller** *sells* many **Products** and *receives* many **Orders**. `Seller 1 ──< Product` · `Seller 1 ──< Order`
- A **Buyer** *places* many **Orders**. `Buyer 1 ──< Order`
- An **Order** *belongs to* one **Buyer** and one **Seller**, and *contains* many **OrderItems**. `Order 1 ──< OrderItem`
- An **OrderItem** *refers to* exactly one **Product** (many line items can point at the same product). `OrderItem >── 1 Product`
- A **Product** *has* zero-or-one **Discount**. `Product 1 ──0..1 Discount`
- An **Order** *generates* exactly one **Invoice**. `Order 1 ──1 Invoice`

---

## 2. Canonical Sample Data

**These exact IDs and values are reused in every example, in every later section.** When you see `s1`, `b1`, `p1`, `p2`, `o1`, it always means *this*.

### Sellers

| id   | name   |
|------|--------|
| `s1` | Acme   |

### Buyers

| id   | name |
|------|------|
| `b1` | Riya |

### Products

| id   | name           | price | cost | stock | discount | status   | sellerId |
|------|----------------|-------|------|-------|----------|----------|----------|
| `p1` | Wireless Mouse | 1200  | 700  | 50    | —        | `ACTIVE` | `s1`     |
| `p2` | Keyboard       | 2500  | 1500 | 30    | —        | `ACTIVE` | `s1`     |

### The canonical Order `o1`

> **Riya (`b1`) buys 2× Wireless Mouse (`p1`) from Acme (`s1`).**

| Order field | value     |
|-------------|-----------|
| `id`        | `o1`      |
| `buyerId`   | `b1`      |
| `sellerId`  | `s1`      |
| `status`    | `PLACED`  |
| `total`     | `2400`    |

**OrderItem of `o1`:**

| id    | orderId | productId | qty | unitPrice | line total |
|-------|---------|-----------|-----|-----------|------------|
| `oi1` | `o1`    | `p1`      | 2   | 1200      | 2400       |

**Invoice of `o1`:**

| id    | orderId | amount | status  |
|-------|---------|--------|---------|
| `inv1`| `o1`    | 2400   | `DRAFT` |

> Mnemonic: **s1 = Acme, b1 = Riya, p1 = Mouse @1200, p2 = Keyboard @2500, o1 = 2× p1 = 2400.**

---

## 3. The GraphQL Schema (SDL)

```graphql
# ─── Enums ─────────────────────────────────────────────
enum ProductStatus { ACTIVE OUT_OF_STOCK ARCHIVED }
enum OrderStatus   { PLACED PAID SHIPPED DELIVERED CANCELLED }
enum InvoiceStatus { DRAFT ISSUED PAID VOID }

# ─── Entity types ──────────────────────────────────────
type Seller {
  id: ID!
  name: String!
  products: [Product!]!          # a seller's catalogue
  orders: [Order!]!              # orders they received
}

type Buyer {
  id: ID!
  name: String!
  orders: [Order!]!              # orders this buyer placed
}

type Product {
  id: ID!
  name: String!
  price: Float!
  cost: Float!
  stock: Int!
  status: ProductStatus!
  discount: Discount             # nullable: 0..1
  seller: Seller!
}

type Discount {
  id: ID!
  code: String!
  percent: Float!
  product: Product!
}

type Order {
  id: ID!
  buyer: Buyer!
  seller: Seller!
  items: [OrderItem!]!           # 1..* line items
  status: OrderStatus!
  total: Float!
  invoice: Invoice               # nullable until generated
}

type OrderItem {
  id: ID!
  order: Order!
  product: Product!
  qty: Int!
  unitPrice: Float!
}

type Invoice {
  id: ID!
  order: Order!
  amount: Float!
  status: InvoiceStatus!
  issuedAt: String               # ISO-8601, null while DRAFT
}

# ─── Inputs ────────────────────────────────────────────
input OrderItemInput {
  productId: ID!
  qty: Int!
}

input PlaceOrderInput {
  buyerId: ID!
  sellerId: ID!
  items: [OrderItemInput!]!
}

# ─── Root operations ───────────────────────────────────
type Query {
  product(id: ID!): Product
  products(status: ProductStatus): [Product!]!
  order(id: ID!): Order
  buyer(id: ID!): Buyer
  seller(id: ID!): Seller
}

type Mutation {
  placeOrder(input: PlaceOrderInput!): Order!
  cancelOrder(orderId: ID!): Order!
  issueInvoice(orderId: ID!): Invoice!
  updateProductStock(productId: ID!, stock: Int!): Product!
}

type Subscription {
  orderStatusChanged(orderId: ID!): Order!
  invoiceIssued(sellerId: ID!): Invoice!
}
```

---

## 4. TypeScript Types / DTOs

```typescript
// ─── Enums ──────────────────────────────────────────────
export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  OUT_OF_STOCK = 'OUT_OF_STOCK',
  ARCHIVED = 'ARCHIVED',
}

export enum OrderStatus {
  PLACED = 'PLACED',
  PAID = 'PAID',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export enum InvoiceStatus {
  DRAFT = 'DRAFT',
  ISSUED = 'ISSUED',
  PAID = 'PAID',
  VOID = 'VOID',
}

// ─── Entity interfaces ──────────────────────────────────
// Stored shape: relations held as *Id foreign keys, not nested objects.
export interface Seller {
  id: string;
  name: string;
}

export interface Buyer {
  id: string;
  name: string;
}

export interface Product {
  id: string;
  name: string;
  price: number;
  cost: number;
  stock: number;
  status: ProductStatus;
  discountId?: string | null;   // 0..1
  sellerId: string;
}

export interface Discount {
  id: string;
  code: string;
  percent: number;
  productId: string;
}

export interface Order {
  id: string;
  buyerId: string;
  sellerId: string;
  status: OrderStatus;
  total: number;
}

export interface OrderItem {
  id: string;
  orderId: string;
  productId: string;
  qty: number;
  unitPrice: number;
}

export interface Invoice {
  id: string;
  orderId: string;
  amount: number;
  status: InvoiceStatus;
  issuedAt?: string | null;
}

// ─── Example DTOs (the wire/input shapes) ───────────────

// Input DTO: what a client sends to place an order.
export interface PlaceOrderDto {
  buyerId: string;               // 'b1'
  sellerId: string;              // 's1'
  items: Array<{
    productId: string;           // 'p1'
    qty: number;                 // 2
  }>;
}

// Output DTO: enriched order returned to the client,
// with relations resolved (nested objects instead of bare ids).
export interface OrderResponseDto {
  id: string;                    // 'o1'
  buyer: Buyer;                  // { id:'b1', name:'Riya' }
  seller: Seller;                // { id:'s1', name:'Acme' }
  status: OrderStatus;           // PLACED
  total: number;                 // 2400
  items: Array<{
    productId: string;           // 'p1'
    name: string;                // 'Wireless Mouse'
    qty: number;                 // 2
    unitPrice: number;           // 1200
    lineTotal: number;           // 2400
  }>;
  invoice?: Invoice | null;      // { id:'inv1', amount:2400, status:'DRAFT' }
}
```

---

## 5. THE BACKEND REQUEST-FLOW LEGEND

Every endpoint in MarketHub — REST or GraphQL — passes through the **same NestJS pipeline**. Memorize this once; later sections only point to which layer changes.

### Master pipeline diagram

```
                         ┌─────────┐
                         │ CLIENT  │  sends HTTP request
                         └────┬────┘  Headers: Authorization: Bearer <JWT>, Cookie: sid=…
                              │
        ╔═════════════════════▼═════════════════════════════════════════╗
        ║                   API GATEWAY                                   ║
        ║  routing · TLS termination · global rate-limit · auth pre-check║
        ╚═════════════════════╤═════════════════════════════════════════╝
                              │ (request enters the Nest app)
   ┌──────────────────────────▼────────────────────────────────────────┐
   │                       NEST APPLICATION                             │
   │                                                                    │
   │   ┌──────────────┐                                                 │
   │   │ MIDDLEWARE   │  raw req/res: logging, correlation-id,          │
   │   │              │  cookie parsing, CORS                           │
   │   └──────┬───────┘  ◄── READS Authorization header & Cookie,      │
   │          │              PARSES them onto the request               │
   │   ┌──────▼───────┐                                                 │
   │   │   GUARD      │  authN/authZ: "is this caller allowed?"         │
   │   └──────┬───────┘  ◄── VERIFIES the JWT signature/expiry,        │
   │          │              ATTACHES req.user → request context        │
   │   ┌──────▼───────┐                                                 │
   │   │ INTERCEPTOR  │  (pre) wrap: start timer, check cache           │
   │   │   (pre)      │                                                 │
   │   └──────┬───────┘                                                 │
   │   ┌──────▼───────┐                                                 │
   │   │    PIPE      │  validate + transform the input DTO             │
   │   └──────┬───────┘  (PlaceOrderDto: qty is Int>0, ids present)     │
   │   ┌──────▼───────┐                                                 │
   │   │ CONTROLLER   │  HTTP↔method mapping ONLY, no business logic    │
   │   │  (Resolver)  │  reads req.user from context                    │
   │   └──────┬───────┘                                                 │
   │   ┌──────▼───────┐                                                 │
   │   │   SERVICE    │  business logic: price, stock, build order      │
   │   └──────┬───────┘                                                 │
   │   ┌──────▼───────┐                                                 │
   │   │ REPOSITORY / │  data access (TypeORM/Prisma): queries, tx      │
   │   │     ORM      │                                                 │
   │   └──────┬───────┘                                                 │
   └──────────┼─────────────────────────────────────────────────────────┘
              │
        ┌─────▼─────┐
        │ DATABASE  │  rows in / rows out
        └─────┬─────┘
              │  ░░░░░░░░░░░░  response travels back OUT  ░░░░░░░░░░░░
   ┌──────────▼─────────────────────────────────────────────────────────┐
   │   ┌──────────────┐                                                  │
   │   │ INTERCEPTOR  │  (post) shape/serialize response, stop timer,    │
   │   │   (post)     │  populate cache                                  │
   │   └──────┬───────┘                                                  │
   │   ┌──────▼───────┐                                                  │
   │   │  EXCEPTION   │  ◄── only if ANY layer threw: map error →        │
   │   │   FILTER     │      HTTP status / GraphQL error envelope        │
   │   └──────┬───────┘                                                  │
   └──────────┼─────────────────────────────────────────────────────────┘
              │
         ┌────▼────┐
         │ CLIENT  │  receives response (or error)
         └─────────┘
```

### One-line responsibility per layer (in order)

| # | Layer | One-line responsibility |
|---|-------|-------------------------|
| 0 | **API Gateway** | Edge: routing, TLS termination, global rate-limiting, coarse auth pre-check before traffic reaches the app. |
| 1 | **Middleware** | Touches raw req/res: logging, correlation-id, **cookie parsing**, CORS — and *parses* the `Authorization` header & cookies onto the request. |
| 2 | **Guard** | AuthN/AuthZ decision — "is this caller allowed?": **verifies the JWT** (signature/expiry) and attaches `req.user` to the request context. |
| 3 | **Interceptor (pre)** | Wraps the call on the way in: starts timing, checks cache, can short-circuit. |
| 4 | **Pipe** | Validates and transforms the **input DTO** (e.g. `PlaceOrderDto`: `qty` is a positive Int, ids present). |
| 5 | **Controller / Resolver** | Thin HTTP↔method (or GraphQL field) mapping, **no business logic**; reads the authenticated user from request context. |
| 6 | **Service** | The business logic: pricing, stock checks, building the order, orchestrating repositories. |
| 7 | **Repository / ORM** | Data access only: builds queries, manages transactions (TypeORM/Prisma). |
| 8 | **Database** | Persists and returns rows. |
| 9 | **Interceptor (post)** | On the way out: shapes/serializes the response, stops the timer, populates cache. |
| 10 | **Exception Filter** | Fires only when a layer **throws**: maps the error to an HTTP status / GraphQL error envelope. |

### Where headers / cookies / JWT are handled (the critical hand-off)

```
Authorization: Bearer <JWT>   Cookie: sid=…
        │                          │
        ▼                          ▼
   ┌─────────────┐  PARSE   The MIDDLEWARE reads the raw header & cookie
   │ MIDDLEWARE  │ ───────► and parses them (no trust decision yet).
   └─────────────┘
        │
        ▼
   ┌─────────────┐  VERIFY  The GUARD verifies the JWT (signature + expiry),
   │   GUARD     │ ───────► rejects if invalid, and sets req.user.
   └─────────────┘
        │
        ▼
   ┌─────────────┐  CARRY   The REQUEST CONTEXT carries req.user downstream;
   │  CONTROLLER │ ───────► controller/service read the user from context,
   │  → SERVICE  │          never re-parsing the header.
   └─────────────┘
```

**Rule of thumb:** *Middleware **parses**, Guard **verifies**, request context **carries** the user.* No layer below the guard should re-read the raw `Authorization` header.

---

---

## 🔍 GET one (by id, path param)

**Story:** Riya (`b1`) taps the "Wireless Mouse" card on Acme's storefront, and her browser fetches the full detail for product `p1` to render the product page.

---

### REST

```http
GET /products/p1 HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Accept: application/json
```

- **Method + path:** `GET /products/:id`
- **Path param:** `id = "p1"` (the product to fetch)
- **Query params:** none
- **Body:** none (GET is safe & has no request body)

**Response — `200 OK`** (product found):

```json
{
  "id": "p1",
  "name": "Wireless Mouse",
  "price": 1200,
  "cost": 700,
  "stock": 50,
  "status": "ACTIVE",
  "discountId": null,
  "sellerId": "s1"
}
```

**Response — `404 Not Found`** (no product with that id, e.g. `GET /products/p999`):

```json
{
  "statusCode": 404,
  "message": "Product p999 not found",
  "error": "Not Found"
}
```

> `GET` is **safe and idempotent** — no state change, and repeating it yields the same result. A missing resource is `404`, never `200` with an empty body. (Optional: return an `ETag` so the client can revalidate with `If-None-Match` and get a cheap `304 Not Modified`.)

---

### Type contract

```typescript
// Request: there is no body DTO — the only input is the path param.
export interface GetProductParams {
  id: string;                    // 'p1'
}

// Response (success 200): the stored Product shape (see Foundation §4).
export type GetProductResponseDto = Product;
// {
//   id: 'p1', name: 'Wireless Mouse', price: 1200, cost: 700,
//   stock: 50, status: ProductStatus.ACTIVE, discountId: null, sellerId: 's1'
// }

// Response (error 404): standard Nest error envelope.
export interface NotFoundErrorDto {
  statusCode: 404;
  message: string;               // 'Product p999 not found'
  error: 'Not Found';
}
```

---

### GraphQL

**Operation + variables:**

```graphql
query GetProduct($id: ID!) {
  product(id: $id) {
    id
    name
    price
    cost
    stock
    status
    discount { id code percent }
    seller { id name }
  }
}
```

```json
{ "id": "p1" }
```

**Response JSON** (found):

```json
{
  "data": {
    "product": {
      "id": "p1",
      "name": "Wireless Mouse",
      "price": 1200,
      "cost": 700,
      "stock": 50,
      "status": "ACTIVE",
      "discount": null,
      "seller": { "id": "s1", "name": "Acme" }
    }
  }
}
```

**Not found** — GraphQL returns `200 OK` at the HTTP layer with `product: null` (nullable field), *not* an error envelope:

```json
{ "data": { "product": null } }
```

**SDL operation signature** (from Foundation §3):

```graphql
product(id: ID!): Product        # nullable return → null when absent, no 404
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => Product)
export class ProductResolver {
  constructor(
    private readonly products: ProductService,
    private readonly sellers: SellerService,
  ) {}

  // Root field: returns null when absent — does NOT throw (nullable return).
  @Query(() => Product, { nullable: true })
  product(@Args('id', { type: () => ID }) id: string): Promise<Product | null> {
    return this.products.findById(id);   // null if missing — no throw
  }

  // 'seller' is a relation, resolved lazily from the row's sellerId.
  // In production, batch these per request with DataLoader to avoid N+1.
  @ResolveField('seller', () => Seller)
  seller(@Parent() product: Product): Promise<Seller> {
    return this.sellers.findById(product.sellerId);
  }
}
```

> Note the contract divergence: **REST signals absence with `404`; GraphQL signals it with `null`** on a nullable field (HTTP stays `200`). The `product` query maps to `ProductService.findById` (returns `null`), whereas the REST controller calls `findByIdOrThrow` (throws → `404`) — same service, two absence policies.

---

### Backend flow (this endpoint)

```
GET /products/p1
   │
   ▼
[Gateway] route /products/* ──► Nest app
   │
   ▼
[Middleware] log + correlation-id, parse Authorization header (no trust yet)
   │
   ▼
[Guard] verify JWT (signature/expiry) ─ valid? ─► set req.user
   │
   ▼
[Interceptor pre] cache lookup key="product:p1" ─ miss ─► continue
   │
   ▼
[Pipe] validate path param id:"p1" (non-empty string; format check)
   │
   ▼
[Controller] @Get(':id') → productService.findByIdOrThrow('p1')
   │
   ▼
[Service] await repo.findOne(); if null → throw NotFoundException
   │
   ▼
[Repository] productRepo.findOne({ where:{ id:'p1' } })
   │
   ▼
╔═══════════ DB HIT ═══════════╗
║ SELECT * FROM products       ║
║ WHERE id = 'p1' LIMIT 1;     ║   ← Mongo: db.products.findOne({_id:'p1'})
╚══════════════════════════════╝
   │  returns 1 row  ┌──────────────────────────┐
   └────────────────►│ {id:p1, Wireless Mouse…} │
                     └──────────────────────────┘
   │
   ▼
[Interceptor post] populate cache "product:p1", serialize → 200 OK
   (if Service threw NotFoundException → [Exception Filter] → 404)
```

1. **Gateway** — matches `/products/*`, terminates TLS, applies rate-limit, forwards into the Nest app.
2. **Middleware** — attaches a correlation-id, logs `GET /products/p1`, and *parses* the `Authorization` header onto the request (no trust decision yet).
3. **Guard** — *verifies* the JWT signature/expiry; on success sets `req.user`, on failure throws → `401`. (A public catalogue could omit the guard; we assume authenticated browse here.)
4. **Interceptor (pre)** — checks cache key `product:p1`; on a hit it short-circuits and returns immediately, never touching the service or DB. On a miss, it proceeds.
5. **Pipe** — the only input is the path param `id`. The pipe *validates/transforms input shape only* — non-empty string, and a format check (e.g. `ParseUUIDPipe`) rejects a malformed id early with `400`. It does **not** check existence (that's a DB concern → the service).
6. **Controller** — `@Get(':id')` reads `@Param('id')` and delegates to `productService.findByIdOrThrow('p1')`. No logic of its own; it never re-reads the raw header (it reads `req.user` from context if needed).
7. **Service** — owns the absence policy: `await`s the repository for one row and, **if the result is `null`, throws `NotFoundException('Product p1 not found')`** (this is what becomes the `404`). Otherwise it returns the row. "Not found" is a business decision, so it lives here — not in the pipe or controller.
8. **Repository / ORM** — issues a single primary-key lookup: `productRepo.findOne({ where: { id: 'p1' } })`. This is the **only** layer that touches the DB.
9. **DB** — `SELECT * FROM products WHERE id = 'p1' LIMIT 1;` (Mongo: `db.products.findOne({ _id: 'p1' })`). A primary-key/index lookup returns **one row** (or zero).
10. **Response** — on a row: **Interceptor (post)** caches it under `product:p1`, serializes, and returns `200 OK`. On a thrown `NotFoundException`: the **Exception Filter** maps it to `404 Not Found` (the post-interceptor's response shaping is skipped on the throw path).

> ⚠️ **Gotcha:** look up by **primary key**, not a scan — `findOne({ where:{ id } })` hits the PK index (O(log n)), whereas `find().filter(...)` would table-scan. Keep absence handling in the **service** (throw `NotFoundException`), not the pipe or controller — and let that thrown exception become the `404`. Returning `200` with `null`/`{}` is a REST anti-pattern that breaks client error handling.

---

## 📋 List a Seller's Products
**Story:** Riya is browsing Acme's storefront — she opens the seller's catalogue, so the UI fetches **all of Acme's (`s1`) products** to render the product grid.

---

### REST

```http
GET /sellers/s1/products?status=ACTIVE&limit=20&offset=0 HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Cookie: sid=…
Accept: application/json
```

| Part | Value | Meaning |
|------|-------|---------|
| Path param | `sellerId = s1` | which seller's catalogue (collection scope) |
| Query param | `status=ACTIVE` | optional filter (omit → all statuses) |
| Query param | `limit=20` · `offset=0` | optional pagination |

`GET` is **safe** (read-only, no side effects) and **idempotent** — repeating it changes nothing, so it is freely cacheable and retriable.

**Response — `200 OK`** (a collection is always an array, even if empty → `[]`, never `404`):

```json
[
  { "id": "p1", "name": "Wireless Mouse", "price": 1200, "stock": 50, "status": "ACTIVE", "sellerId": "s1" },
  { "id": "p2", "name": "Keyboard",       "price": 2500, "stock": 30, "status": "ACTIVE", "sellerId": "s1" }
]
```

> Optional: surface total count via header `X-Total-Count: 2` (or wrap in an envelope `{ data, total }` if you prefer pagination metadata in the body). Send `Cache-Control` (e.g. `public, max-age=30`) since the read is safe and cacheable.

---

### Type contract

```typescript
// Request — path + query (no request body for GET)
export interface ListSellerProductsParams {
  sellerId: string;                 // 's1'  (path)
}
export interface ListSellerProductsQuery {
  status?: ProductStatus;           // optional filter
  limit?: number;                   // default 20
  offset?: number;                  // default 0
}

// Response — array of product summaries
export interface ProductSummaryDto {
  id: string;                       // 'p1'
  name: string;                     // 'Wireless Mouse'
  price: number;                    // 1200
  stock: number;                    // 50
  status: ProductStatus;            // ACTIVE
  sellerId: string;                 // 's1'
}
export type ListSellerProductsResponse = ProductSummaryDto[];
```

---

### GraphQL

Same intent expressed as a **query** — the seller's `products` field returns the array. Either query `products(status)` at the root, or traverse from `seller(id)` into `products`:

```graphql
query SellerProducts($id: ID!, $status: ProductStatus) {
  seller(id: $id) {
    id
    name
    products(status: $status) {     # filtered collection
      id
      name
      price
      stock
      status
    }
  }
}
```

**Variables:**

```json
{ "id": "s1", "status": "ACTIVE" }
```

**Response JSON:**

```json
{
  "data": {
    "seller": {
      "id": "s1",
      "name": "Acme",
      "products": [
        { "id": "p1", "name": "Wireless Mouse", "price": 1200, "stock": 50, "status": "ACTIVE" },
        { "id": "p2", "name": "Keyboard",       "price": 2500, "stock": 30, "status": "ACTIVE" }
      ]
    }
  }
}
```

**SDL operation signature** (a `Seller.products` field resolver; note the added `status` filter arg vs. the base schema):

```graphql
type Seller {
  products(status: ProductStatus): [Product!]!   # non-null list of non-null Products
}
```

**NestJS resolver sketch** — `@ResolveField` so the list is fetched lazily only when the query selects it:

```typescript
@Resolver(() => Seller)
export class SellerResolver {
  constructor(
    private readonly sellers: SellerService,
    private readonly products: ProductService,
  ) {}

  @Query(() => Seller, { nullable: true })
  seller(@Args('id', { type: () => ID }) id: string) {
    return this.sellers.findById(id);          // returns null → GraphQL `seller: null`
  }

  // Resolved only if the query body selects `products`.
  // Batch with a DataLoader to avoid N+1 across a seller list.
  @ResolveField(() => [Product])
  products(
    @Parent() seller: Seller,
    @Args('status', { type: () => ProductStatus, nullable: true }) status?: ProductStatus,
  ) {
    return this.products.listBySeller(seller.id, { status });
  }
}
```

---

### Backend flow (this endpoint)

```
GET /sellers/s1/products?status=ACTIVE
        │
   ┌────▼─────┐
   │ GATEWAY  │  routes /sellers/** → product-service; global rate-limit
   └────┬─────┘
   ┌────▼─────────┐
   │ MIDDLEWARE   │  parses Authorization + sid cookie, stamps correlation-id
   └────┬─────────┘
   ┌────▼─────────┐
   │ GUARD        │  JwtAuthGuard: valid token? (READ scope — any logged-in user may browse)
   └────┬─────────┘
   ┌────▼─────────┐
   │ INTERCEPTOR  │  CacheInterceptor: key = `seller:s1:products:ACTIVE:20:0` → MISS, continue
   └────┬─────────┘
   ┌────▼─────────┐
   │ PIPE         │  ParseEnumPipe(status), ParseIntPipe(limit/offset) → defaults 20/0
   └────┬─────────┘
   ┌────▼─────────┐
   │ CONTROLLER   │  ProductController.listBySeller('s1', { status, limit, offset })
   └────┬─────────┘
   ┌────▼─────────┐
   │ SERVICE      │  asserts seller s1 exists (→404 if not); builds filter; no pricing logic
   └────┬─────────┘
   ┌────▼─────────┐
   │ REPOSITORY   │  productRepo.findAndCount({ where, take, skip, order })
   └────┬─────────┘
   ┌────▼─────────┐
   │   DB         │  SELECT * FROM products WHERE seller_id='s1' AND status='ACTIVE'
   │              │  ORDER BY id LIMIT 20 OFFSET 0;   ── returns 2 rows + count ──►
   └────┬─────────┘
   ┌────▼─────────┐
   │ INTERCEPTOR  │  (post) maps rows → ProductSummaryDto[], fills cache, sets X-Total-Count
   └────┬─────────┘
        ▼  200 OK  [ {p1…}, {p2…} ]
```

1. **Gateway** — matches the `/sellers/**` route and forwards to the product-service; applies global rate-limiting.
2. **Middleware** — parses the `Authorization` header and `sid` cookie onto the request and stamps a correlation-id (read-only, no trust decision yet).
3. **Guard** — `JwtAuthGuard` *verifies* the JWT (signature + expiry) and sets `req.user`; this is a **read** of a public catalogue, so any authenticated user passes (no seller-ownership check — Riya can view Acme's listings).
4. **Interceptor (pre)** — `CacheInterceptor` computes a key over **all** inputs (`seller:s1:products:ACTIVE:20:0`) and looks it up; on a hit it short-circuits and never reaches the controller. Here: MISS.
5. **Pipe** — `ParseEnumPipe` validates `status ∈ ProductStatus`; `ParseIntPipe` (with `DefaultValuePipe`) coerces `limit`/`offset` to numbers and applies defaults `20`/`0`. No body DTO — `GET` carries no body.
6. **Controller** — thin mapping only: reads `req.user` from context and calls `productService.listBySeller('s1', { status, limit, offset })`. No business logic.
7. **Service** — first asserts the **parent** `s1` exists (throws `NotFoundException` → `404` if not — this is the *only* 404 here); then assembles the `where` filter `{ sellerId:'s1', status:'ACTIVE' }` plus `take/skip` and orders the page. **No pricing or stock mutation** — a list read is side-effect-free.
8. **Repository** — `productRepo.findAndCount({ where:{ sellerId, status }, take:20, skip:0, order:{ id:'ASC' } })` (one round-trip returning both the page **and** the total for `X-Total-Count`).
9. **DB** — `SELECT * FROM products WHERE seller_id='s1' AND status='ACTIVE' ORDER BY id LIMIT 20 OFFSET 0;` → returns the **2 rows** `p1`, `p2` plus the count (Mongo equivalent: `db.products.find({ sellerId:'s1', status:'ACTIVE' }).sort({ _id:1 }).limit(20).skip(0)`).
10. **Interceptor (post)** — maps rows to `ProductSummaryDto[]`, populates the cache under the computed key, and sets `X-Total-Count: 2`; the controller's value is serialized as **`200 OK`** with the array. (If any layer threw, the **exception filter** maps it instead — e.g. the service's `NotFoundException` → `404`.)

---

> **⚠️ Gotcha:** An empty catalogue is **`200 OK` with `[]`, not `404`** — a collection endpoint always succeeds when the *parent* (`s1`) exists; `404` is reserved for a missing seller, not zero children. (If `s1` itself doesn't exist, *then* the service throws `404`.) Also watch the **N+1 trap** in GraphQL: resolving `products` per-seller across a seller list needs a **DataLoader** to batch the lookups, or each seller fires its own `SELECT`.

---

## 🔎 Browse the catalogue (filter + sort)

> **Story:** Riya (`b1`) opens Acme's storefront and narrows the catalogue: she wants only **peripherals priced ₹1000–3000**, **in stock**, sorted **most expensive first**. Both p1 (Mouse @1200) and p2 (Keyboard @2500) qualify; she sees Keyboard, then Mouse.

---

### REST

A filtered/sorted listing is a **read of a collection** → `GET /products` with everything expressed as **query params** (filters never belong in the resource path; the path is always the collection). `GET` is **safe and idempotent** — repeating it changes nothing and the same query always yields the same result, so it's freely cacheable.

```
GET /products?category=peripherals&minPrice=1000&maxPrice=3000&inStock=true&sort=price:desc
Host: api.markethub.dev
Authorization: Bearer <JWT>
Cookie: sid=…
Accept: application/json
```

**Query-param contract**

| Param | Type | Meaning | Maps to (DB) |
|-------|------|---------|--------------|
| `category` | string | single-value filter | `category = 'peripherals'` |
| `minPrice` | number | range lower bound (incl.) | `price >= 1000` |
| `maxPrice` | number | range upper bound (incl.) | `price <= 3000` |
| `inStock` | boolean | derived filter | `stock > 0` |
| `sort` | `field:dir` | ordering | `ORDER BY price DESC` |

> **Single-param example** (just one filter): `GET /products?category=peripherals` → every peripheral, default order.
> **Multi-param example**: the full query above — filters AND-combined, then sorted.

There is **no request body** (GET). The response is `200 OK` with a JSON array — an empty match is still `200 OK` with `[]`, **not** `404` (the collection exists; it just has no members matching the filter):

```jsonc
HTTP/1.1 200 OK
Content-Type: application/json
X-Total-Count: 2

[
  { "id": "p2", "name": "Keyboard",       "price": 2500, "stock": 30, "status": "ACTIVE", "sellerId": "s1" },
  { "id": "p1", "name": "Wireless Mouse",  "price": 1200, "stock": 50, "status": "ACTIVE", "sellerId": "s1" }
]
```

Note the order: **p2 before p1** because `sort=price:desc`. A malformed param (e.g. `minPrice=abc` or `sort=price:sideways`) → `400 Bad Request` from the validation pipe **before** any service or DB work happens.

---

### Type contract

```typescript
// ─── Request: parsed & validated query DTO ──────────────
export type SortDir = 'asc' | 'desc';

export interface ProductFilterQueryDto {
  category?: string;        // 'peripherals'
  minPrice?: number;        // 1000   (coerced from string)
  maxPrice?: number;        // 3000
  inStock?: boolean;        // true    (coerced from 'true')
  sort?: `${'price' | 'name' | 'stock'}:${SortDir}`; // 'price:desc'
}

// ─── Response: a list of product views ──────────────────
export interface ProductListItemDto {
  id: string;               // 'p2'
  name: string;             // 'Keyboard'
  price: number;            // 2500
  stock: number;            // 30
  status: ProductStatus;    // ACTIVE   (reuses shared enum)
  sellerId: string;         // 's1'
}

export type ProductListResponseDto = ProductListItemDto[];
```

---

### GraphQL

Filtering is expressed as **arguments** — bundled into one `input` object so the filter set stays cohesive and extensible. This is a pure read, so it's a `Query` field (no mutation, no subscription).

**SDL operation signature** (extends the shared schema's `Query`)

```graphql
enum ProductSortField { price name stock }
enum SortDir { ASC DESC }

input ProductFilter {
  category: String
  minPrice: Float
  maxPrice: Float
  inStock: Boolean
}

input ProductSort {
  field: ProductSortField! = price
  dir: SortDir! = ASC
}

extend type Query {
  products(filter: ProductFilter, sort: ProductSort): [Product!]!
}
```

> The shared schema already declares `products(status: ProductStatus): [Product!]!`. This pattern **supersedes** that signature with the richer `filter`/`sort` arguments — treat this as the canonical product-list query going forward (the old `status`-only form folds into `ProductFilter`).

**Operation + variables**

```graphql
query BrowseProducts($filter: ProductFilter, $sort: ProductSort) {
  products(filter: $filter, sort: $sort) {
    id
    name
    price
    stock
    status
  }
}
```
```jsonc
// variables
{
  "filter": { "category": "peripherals", "minPrice": 1000, "maxPrice": 3000, "inStock": true },
  "sort":   { "field": "price", "dir": "DESC" }
}
```

**Response JSON**

```jsonc
{
  "data": {
    "products": [
      { "id": "p2", "name": "Keyboard",      "price": 2500, "stock": 30, "status": "ACTIVE" },
      { "id": "p1", "name": "Wireless Mouse", "price": 1200, "stock": 50, "status": "ACTIVE" }
    ]
  }
}
```

> **Single vs many filters** is free in GraphQL: omit fields from `$filter` and they're simply not applied — `{ "filter": { "category": "peripherals" } }` is the single-param case; the object above is multi-param. No new endpoint needed.

**NestJS resolver sketch**

```typescript
@Resolver(() => Product)
export class ProductsResolver {
  constructor(private readonly products: ProductsService) {}

  @Query(() => [Product])
  products(
    @Args('filter', { nullable: true }) filter?: ProductFilterInput,
    @Args('sort',   { nullable: true }) sort?: ProductSortInput,
  ): Promise<Product[]> {
    return this.products.findMany(filter, sort); // SAME service method the REST controller calls
  }

  // `seller` is a nested object on Product → resolved per-row (watch N+1, see gotcha)
  @ResolveField(() => Seller)
  seller(
    @Parent() p: Product,
    @Context('sellerLoader') sellerLoader: DataLoader<string, Seller>,
  ): Promise<Seller> {
    return sellerLoader.load(p.sellerId); // batched per-request, not one query per row
  }
}
```

> Validation/coercion of `filter`/`sort` is handled by Nest's **GraphQL input types + global `ValidationPipe`** (class-validator on `ProductFilterInput`/`ProductSortInput`) — the resolver itself stays thin, exactly like the REST controller.

---

### Backend flow (this endpoint)

```
GET /products?category=peripherals&minPrice=1000&maxPrice=3000&inStock=true&sort=price:desc
      │
      ▼
┌──────────┐   route GET /products → ProductsController.list
│ GATEWAY  │   (safe/idempotent read ⇒ generous rate-limit bucket; edge-cacheable)
└────┬─────┘
     ▼
┌────────────┐  parse Authorization + Cookie; log "list products" + the raw query string
│ MIDDLEWARE │  (no trust decision yet — just parse + attach onto the request)
└────┬───────┘
     ▼
┌──────────┐   verify JWT → req.user = Riya(b1). Browsing the catalogue is read-only,
│  GUARD   │   so authZ is loose: any authenticated buyer may list (no ownership/role gate).
└────┬─────┘
     ▼
┌──────────────┐  (pre) cache key = normalized querystring (params sorted); on HIT short-circuit,
│ INTERCEPTOR  │  on MISS continue down the pipeline
└────┬─────────┘
     ▼
┌──────────┐   ValidationPipe + @Query() → ProductFilterQueryDto:
│   PIPE   │   coerce minPrice/maxPrice→number, inStock→boolean,
│          │   whitelist sort ∈ {price|name|stock}:{asc|desc}; else 400.
│          │   NO DB access here — purely shape/validate the input.
└────┬─────┘
     ▼
┌────────────┐  thin: forwards (filterDto, sortDto) to ProductsService.findMany()
│ CONTROLLER │  (reads req.user from context; no business logic, no query building)
└────┬───────┘
     ▼
┌──────────┐   translate the DTO into a where-spec + order-spec:
│ SERVICE  │   { category:'peripherals', price:{ gte:1000, lte:3000 }, stock:{ gt:0 } }
│          │   order: [['price','DESC']]. inStock=true ⇒ stock>0 (DERIVED, not a column).
│          │   Owns the business mapping; delegates the actual query to the repository.
└────┬─────┘
     ▼
┌──────────────┐  build ONE parameterized query from the spec (values bound, never concatenated)
│ REPOSITORY   │  — this is the layer that talks to the DB
└────┬─────────┘
     ▼
┌──────────┐   ── the DB hit (single round-trip) ──
│ DATABASE │   SQL (Postgres/TypeORM):
│          │     SELECT id, name, price, stock, status, "sellerId"
│          │     FROM products
│          │     WHERE category = $1            -- 'peripherals'
│          │       AND price   >= $2            -- 1000
│          │       AND price   <= $3            -- 3000
│          │       AND stock    > 0
│          │     ORDER BY price DESC;
│          │   Mongo equivalent:
│          │     db.products.find({ category:'peripherals',
│          │       price:{ $gte:1000, $lte:3000 }, stock:{ $gt:0 } })
│          │       .sort({ price:-1 })
│          │   ⇒ returns 2 rows: [p2(2500), p1(1200)]
└────┬─────┘
     ▼
┌──────────────┐  (post) map rows → ProductListItemDto[], set X-Total-Count: 2,
│ INTERCEPTOR  │  populate cache for this querystring → 200 OK
└────┬─────────┘
     ▼
   CLIENT  ◄── [ {p2…}, {p1…} ]
```

**What each layer does, specifically here:**

1. **Gateway** — matches `GET /products`; because the verb is safe/idempotent it gets a generous rate-limit bucket and may be edge-cached.
2. **Middleware** — parses the JWT/cookie and logs the request **with its query string** (so the filter set is auditable). It only *parses*; it makes no auth decision.
3. **Guard** — *verifies* the JWT and sets `req.user=b1`; listing is permitted for any authenticated buyer (no ownership/role gate). Authorization lives here, **not** in the service.
4. **Interceptor (pre)** — computes a cache key from the **normalized querystring** (params sorted), so identical filter+sort combos are served from cache without touching the service or DB.
5. **Pipe** — binds `@Query()` into `ProductFilterQueryDto`, **coerces** `minPrice/maxPrice` to numbers and `inStock` to boolean, and **whitelists** `sort` against allowed `field:dir` pairs; anything off → `400`. This is pure input validation — **no DB access, no business rules**.
6. **Controller** — no logic; reads `req.user` from context and hands `(filterDto, sortDto)` to `ProductsService.findMany`.
7. **Service** — converts the DTO into a where-spec + order-spec; notably `inStock=true` becomes the **derived** predicate `stock > 0` (there is no `inStock` column), and `sort=price:desc` becomes `ORDER BY price DESC`. This is the only layer that knows those *business* mappings — but it does **not** issue SQL itself.
8. **Repository / ORM** — builds **one parameterized query** from the spec (placeholders, never string-concatenated values) and is the sole layer that actually hits the DB.
9. **DB** — runs the `WHERE … ORDER BY` shown above; returns the 2 matching rows already ordered `[p2, p1]`.
10. **Interceptor (post)** — serializes rows to `ProductListItemDto[]`, sets `X-Total-Count`, populates the cache, returns `200 OK`. (If any layer had thrown, the **exception filter** would instead map it — e.g. a bad param to `400`.)

> ⚠️ **Gotcha:** In GraphQL, the list query is one DB hit, but selecting `seller { … }` per product fires `@ResolveField` **once per row** → classic **N+1**. Batch with a per-request **DataLoader** (collect `sellerId`s → one `WHERE id IN (…)`), as in the resolver sketch above. Two REST footguns: (a) a no-match result is `200 []`, **never** `404`; (b) always **whitelist** the `sort` field/direction and **bind filters as query parameters** — interpolating raw `sort`/`minPrice` strings into the query is an injection hole. Note this whitelisting happens in the **pipe** (input is well-formed) while parameter binding happens in the **repository** (values are safely bound) — two different layers, two different defenses.

---

I'll polish this section, focusing on REST accuracy, GraphQL accuracy, and especially the NestJS backend flow correctness. Let me work through the technical details carefully.

Key issues I'm spotting to fix:
1. **DataLoader lifecycle**: The draft says the interceptor (pre) "instantiates a fresh DataLoader map" — but in NestJS GraphQL the correct, idiomatic place is per-request context creation (`context` factory in `GraphQLModule`), not an interceptor. Interceptors run per-resolver-field in GraphQL, so this is misleading/incorrect.
2. **`recent` in the DataLoader batch key**: batching on `{productId, recent}` with `In(ids)` then `.slice(0, recent)` is subtly wrong — a global `ORDER BY ... DESC` with a flat `IN` + per-key slice doesn't give you the N-most-recent *per product* (it slices from a globally-ordered list). This is a real correctness bug worth flagging.
3. **`Product.orders` typing** — `orders(recent: Int = 5): [Order!]!` returning an `Order` with an injected `qty` field that isn't on the canonical `Order` — should be honest about that being a denormalized projection.
4. REST: `?include=products` is fine, but worth noting it's non-idempotent-safe... actually GET is safe/idempotent, that's correct. The N+1 framing is good.

Let me produce the polished section.

## 🪆 GET nested / list-of-list (relations) + N+1

> **Story:** A MarketHub admin opens **Acme's (`s1`) seller dashboard** and wants the full tree in one glance: Acme → every product in the catalogue (`p1` Wireless Mouse, `p2` Keyboard) → and for *each* product, its recent orders. It's a **list of lists** — and it's exactly where the N+1 trap springs.

---

### REST

REST has no nested field selection, so the client (or a BFF) walks the tree with **multiple round-trips**: first the seller, then its products, then orders per product. Every call here is a **GET** — safe and idempotent (no state change, repeatable without side effects), so all of it is freely cacheable.

**Round-trip 1 — the seller + its products**

```http
GET /sellers/s1?include=products HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Cookie: sid=…
Accept: application/json
```

| param | in | value |
|-------|-----|-------|
| `s1` | path | seller id |
| `include` | query | `products` — embed the catalogue (saves one trip) |

**`200 OK`**

```json
{
  "id": "s1",
  "name": "Acme",
  "products": [
    { "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE" },
    { "id": "p2", "name": "Keyboard",       "price": 2500, "status": "ACTIVE" }
  ]
}
```

> If `s1` doesn't exist, this is `404 Not Found` — not an empty `200`.

**Round-trip 2..N — orders for *each* product** (this is the **client-side N+1**: one call per product id)

```http
GET /products/p1/orders?recent=5 HTTP/1.1
Authorization: Bearer <JWT>
```
```http
GET /products/p2/orders?recent=5 HTTP/1.1
Authorization: Bearer <JWT>
```

| param | in | value |
|-------|-----|-------|
| `p1` / `p2` | path | product id |
| `recent` | query | cap the list to the N most recent orders |

**`200 OK`** for `/products/p1/orders`:

```json
{
  "productId": "p1",
  "orders": [
    { "id": "o1", "buyerId": "b1", "status": "PLACED", "total": 2400, "qty": 2 }
  ]
}
```

**`200 OK`** for `/products/p2/orders` — Keyboard has no recent sales (empty list is still `200`, not `404`: the *collection* exists, it's just empty):

```json
{ "productId": "p2", "orders": [] }
```

> To assemble Acme's tree the client makes **1 + 2 = 3 HTTP calls**, growing to **1 + N** as the catalogue grows. It also **over-fetches**: REST can't express "give me only `order.id` and `order.total`" — the server's fixed representation comes back whole.

---

### Type contract

```typescript
// ── Round-trip 1: seller with embedded products ──────────
export interface SellerWithProductsDto {
  id: string;                    // 's1'
  name: string;                  // 'Acme'
  products: Array<{
    id: string;                  // 'p1'
    name: string;                // 'Wireless Mouse'
    price: number;               // 1200
    status: ProductStatus;       // ACTIVE
  }>;
}

// ── Round-trip 2..N: one product's recent orders ─────────
export interface ProductOrdersDto {
  productId: string;             // 'p1'
  orders: Array<{
    id: string;                  // 'o1'
    buyerId: string;             // 'b1'
    status: OrderStatus;         // PLACED
    total: number;               // 2400
    qty: number;                 // 2  (qty of THIS product in that order — a projection, not Order.qty)
  }>;
}

// ── The fully-assembled tree (what the client ends up with) ──
export interface SellerTreeDto {
  id: string;
  name: string;
  products: Array<{
    id: string;
    name: string;
    price: number;
    status: ProductStatus;
    orders: ProductOrdersDto['orders'];   // list-of-list: each product carries its own orders
  }>;
}
```

---

### GraphQL

One query, nested selection, **exact fields** — the whole tree comes back in a single round-trip.

**Operation + variables**

```graphql
query SellerTree($id: ID!, $recent: Int = 5) {
  seller(id: $id) {
    id
    name
    products {                   # Seller.products  → list
      id
      name
      price
      status
      orders(recent: $recent) {  # Product.orders → list-of-list (the N+1 hotspot)
        id
        status
        total
        qty
      }
    }
  }
}
```
```json
{ "id": "s1", "recent": 5 }
```

**Response**

```json
{
  "data": {
    "seller": {
      "id": "s1",
      "name": "Acme",
      "products": [
        {
          "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE",
          "orders": [
            { "id": "o1", "status": "PLACED", "total": 2400, "qty": 2 }
          ]
        },
        {
          "id": "p2", "name": "Keyboard", "price": 2500, "status": "ACTIVE",
          "orders": []
        }
      ]
    }
  }
}
```

**SDL operation signature** — adds the `Product.orders` connection field to the shared schema. The per-product `qty` lives on a dedicated projection type (`ProductOrderLine`), **not** on the canonical `Order`, since `qty` is a line-item attribute, not an order attribute:

```graphql
type Query {
  seller(id: ID!): Seller
}

type Product {
  # …shared fields…
  orders(recent: Int = 5): [ProductOrderLine!]!   # recent orders containing this product
}

# Projection: an Order as seen *through* a given product (carries that product's line qty).
type ProductOrderLine {
  id: ID!                 # the order id (o1)
  status: OrderStatus!
  total: Float!
  qty: Int!               # qty of the resolving product in this order
}
```

**NestJS resolver sketch** — three resolver levels, with the per-request **DataLoader** that kills the N+1. Note the DataLoader is built in the **GraphQL context factory** (per request), not in an interceptor:

```typescript
@Resolver(() => Seller)
export class SellerResolver {
  constructor(private readonly sellers: SellerService) {}

  @Query(() => Seller, { nullable: true })
  seller(@Args('id', { type: () => ID }) id: string) {
    return this.sellers.findById(id);            // 1 query → seller row (or null → GraphQL null)
  }

  // Seller.products — batched across ALL sellers resolved this request
  @ResolveField(() => [Product])
  products(@Parent() seller: Seller, @Context('loaders') l: Loaders) {
    return l.productsBySeller.load(seller.id);   // batched: WHERE sellerId IN (…)
  }
}

@Resolver(() => Product)
export class ProductResolver {
  // Product.orders — THE N+1 hotspot, batched via DataLoader
  @ResolveField(() => [ProductOrderLine])
  orders(
    @Parent() product: Product,
    @Args('recent', { type: () => Int, defaultValue: 5 }) recent: number,
    @Context('loaders') l: Loaders,
  ) {
    return l.recentOrdersByProduct.load({ productId: product.id, recent });
  }
}
```

```typescript
// ── The DataLoader: collects p1, p2, … then fires ONE query ──
// Built fresh per request in the GraphQL context factory:
//   GraphQLModule.forRoot({ context: () => ({ loaders: buildLoaders() }) })
const recentOrdersByProduct = new DataLoader<ProductKey, ProductOrderLine[]>(
  async (keys) => {
    const ids = keys.map(k => k.productId);              // ['p1','p2']
    const maxRecent = Math.max(...keys.map(k => k.recent)); // cap fetched per product

    // Fetch enough rows per product to satisfy the largest `recent` in the batch.
    // A flat IN(...) + global ORDER BY would NOT give the N-most-recent *per product*
    // (a global slice mixes products), so we rank within each product.
    const rows = await orderItemRepo
      .createQueryBuilder('oi')
      .innerJoinAndSelect('oi.order', 'o')
      .where('oi.productId IN (:...ids)', { ids })        // ← ONE query
      .andWhere(qb => {
        // keep only the top-N orders per productId by recency (window function)
        return `oi.id IN ${qb.subQuery()
          .select('ranked.id')
          .from(sub => sub
            .select('oi2.id', 'id')
            .addSelect('ROW_NUMBER() OVER (PARTITION BY oi2.productId ORDER BY o2.createdAt DESC)', 'rn')
            .from('order_items', 'oi2')
            .innerJoin('orders', 'o2', 'o2.id = oi2.orderId')
            .where('oi2.productId IN (:...ids)', { ids }), 'ranked')
          .where('ranked.rn <= :maxRecent', { maxRecent })
          .getQuery()}`;
      })
      .orderBy('o.createdAt', 'DESC')
      .getMany();

    // Re-group rows back to the EXACT order of incoming keys (key-order contract).
    return keys.map(k =>
      rows
        .filter(r => r.productId === k.productId)
        .slice(0, k.recent)                                // honour each key's own `recent`
        .map(r => ({ id: r.order.id, status: r.order.status, total: r.order.total, qty: r.qty })),
    );
  },
);
```

> **Why the window function?** The naïve version — flat `WHERE productId IN (...) ORDER BY createdAt DESC` then `.slice(0, recent)` per key — silently returns the wrong rows: the global slice can be filled entirely by one hot product, starving the others. Ranking **within each `productId`** (`PARTITION BY`) is what makes "N most recent **per product**" correct in a single batched query.

---

### Backend flow (this endpoint)

```
CLIENT  query SellerTree($id:"s1")
  │  POST /graphql   Authorization: Bearer <JWT>   Cookie: sid=…
  ▼
GATEWAY ── routes /graphql · TLS · rate-limit   (ONE request enters, vs REST's 1+N)
  ▼
MIDDLEWARE ── parses JWT header + sid cookie onto req (no trust decision yet)
  ▼
GUARD ── verifies JWT (sig/expiry), sets req.user; authorizes "may view s1's dashboard"
  ▼
CONTEXT FACTORY ── builds a FRESH per-request DataLoader map  ◄── (not an interceptor!)
  ▼
INTERCEPTOR(pre) ── starts the request timer
  ▼
PIPE ── coerces/validates args: $id→string 's1', $recent→Int (default 5, must be >0)
  ▼
RESOLVER seller(id) ─────────────► SERVICE.findById('s1')
  │                                      │
  │                                   REPO ── DB query #1
  │                                      ▼
  │                            ┌──────────────────────────────┐
  │                            │ SELECT * FROM sellers         │
  │                            │ WHERE id = 's1'      → 1 row  │
  │                            └──────────────────────────────┘
  ▼
@ResolveField products(seller) ──► loaders.productsBySeller.load('s1')
  │                                      │  (enqueue → batch)
  │                                   DB query #2
  │                            ┌──────────────────────────────┐
  │                            │ SELECT * FROM products        │
  │                            │ WHERE sellerId IN ('s1')      │
  │                            │            → [p1, p2]         │
  │                            └──────────────────────────────┘
  ▼
@ResolveField orders(product)  ←─ invoked ONCE PER product (p1, then p2)
  │   ┌─ p1 ─┐  ┌─ p2 ─┐   each .load() only ENQUEUES a key…
  │   load() load()       …DataLoader coalesces → fires ONE query
  ▼
  loaders.recentOrdersByProduct
                                   DB query #3  (the batch — NOT 1-per-product)
                            ┌──────────────────────────────────────────────┐
                            │ SELECT oi.*, o.*  FROM order_items oi         │
                            │ JOIN orders o ON o.id = oi.orderId            │
                            │ WHERE oi.productId IN ('p1','p2')             │
                            │   AND oi.id IN (top-N per product by recency) │
                            │   → p1:[o1], p2:[]                            │
                            └──────────────────────────────────────────────┘
  ▼
INTERCEPTOR(post) ── stops timer (the tree is assembled by GraphQL's own field resolution)
  ▼
EXCEPTION FILTER ── only if a layer threw (e.g. seller null is fine; auth fail → error envelope)
  ▼
CLIENT ── { seller:{ products:[ {p1, orders:[o1]}, {p2, orders:[]} ] } }
```

**What each layer concretely does for *this* endpoint:**

1. **Gateway** — routes the single `POST /graphql`, terminates TLS, applies the rate-limit. Exactly **one** HTTP request enters (vs REST's 1+N).
2. **Middleware** — parses `Authorization: Bearer <JWT>` and `Cookie: sid=…` onto `req`; tags a correlation-id so all three DB queries share one trace. No auth decision here.
3. **Guard** — verifies the JWT signature/expiry, sets `req.user`, and authorizes that this caller may view seller `s1`'s dashboard. Reads from request context, never re-parses the raw header.
4. **GraphQL context factory** — **instantiates a fresh DataLoader map for this request.** This is the correct seam for loaders in NestJS GraphQL — *not* an interceptor. (In GraphQL, an interceptor fires per resolved field, so a loader created there would be rebuilt mid-request and couldn't batch.) Per-request lifetime is mandatory: a loader shared across requests would serve stale or cross-tenant data from its cache.
5. **Interceptor (pre)** — starts the request timer (and could check a response cache). Wraps the whole operation, not individual fields.
6. **Pipe** — coerces and validates the args: `$id` → string `'s1'`, `$recent` → positive `Int` (defaults to `5`). Pure input shaping; **no DB access**.
7. **Resolver `seller(id)` → Service → Repo → DB query #1** — `SELECT * FROM sellers WHERE id='s1'` → the Acme row. The resolver is thin; the **service** owns the logic and the **repo** owns the query. Relations remain bare ids at this point. A missing seller returns `null` (valid GraphQL), not an exception.
8. **`@ResolveField products` → DB query #2** — `loaders.productsBySeller.load('s1')` enqueues, then fires `SELECT * FROM products WHERE sellerId IN ('s1')` → `[p1, p2]`. Batched: 10 sellers in play would still be **one** `IN (...)`.
9. **`@ResolveField orders` → DB query #3 (the N+1 kill)** — GraphQL invokes this field **once per product** (`p1`, then `p2`). Naïvely that's N queries. Instead each call does `loaders.recentOrdersByProduct.load({productId, recent})`, which only **enqueues** the key; the DataLoader coalesces `['p1','p2']` into **one** `WHERE oi.productId IN (...)` join (with per-product top-N ranking), then re-maps rows back to each product **in key order** (`p1:[o1]`, `p2:[]`).
10. **Interceptor (post) / Exception filter** — the post-interceptor stops the timer; GraphQL itself stitches the resolved fields into the nested tree. The exception filter fires only if a layer **threw** (a `null` seller is not an error; a failed auth or DB error is, and maps to the GraphQL error envelope).

> **Total DB hits: 3** (seller + products-batch + orders-batch) regardless of catalogue size — *not* `1 + 1 + N`.

---

> **⚠️ Gotcha — N+1 on `Product.orders`:** without DataLoader, GraphQL's per-field resolution runs the `orders` resolver **once for every product** the `products` field returned — 2 products → 2 order queries, 500 products → **500** queries, each identical except for `productId`. DataLoader fixes it by deferring every `.load(key)` to the next tick, collecting the keys, and issuing **one** `WHERE productId IN (…)` batch. Three things must all hold or it breaks:
> 1. **Per-request loader** — built in the **context factory** (step 4), never shared across requests (stale/cross-tenant cache) and never in an interceptor (rebuilds mid-request, defeating batching).
> 2. **Key-order re-mapping** — return one result array **per input key, in order**; emit `[]` for `p2`, never skip it, or every product's orders shift by one.
> 3. **Correct per-key slicing** — "N most recent" must be ranked **per `productId`** (window function / `PARTITION BY`), not a global `ORDER BY` + flat slice, which would let one busy product starve the rest.

---

This is a documentation-polishing task, not a code-diff review. There's no git repo here (the env says "Is directory a git repo: No"), and my actual job is to return polished Markdown for the pagination section — not to run a code-review harness over a diff. The code-review skill isn't the right fit, so I'll proceed with the editorial review directly.

Let me analyze the section against the shared foundation for accuracy issues:

**REST issues found:**
1. Offset response says `"page": 1` but the request used `page=1` with `limit=2` returning both products (`totalItems: 2, totalPages: 1`) — with limit 2 and 2 total items, page 1 returns both, so `hasNext: false` is correct. But the story says "Page 2, 20 per page" while the example uses page 1 / limit 2. Minor narrative mismatch; the example is internally consistent. I'll align the prose.
2. Cursor example: `cursor=eyJpZCI6InAxIn0` (`{"id":"p1"}`) returns rows **after** p1 → `[p2]`, `nextCursor: eyJpZCI6InAyIn0`, `hasNext: false`. Consistent.
3. Status code `200 OK` correct; GET is safe/idempotent — good. Should note `400` for malformed cursor.

**GraphQL issues:**
1. Resolver `productsPage` declares `status?` param but the SDL has `status` as first arg — ordering with required-after-optional in TS args is fine since all are `@Args`. However, `findConnection` is called but `after`/`first` mapping is okay.
2. Relay `hasPreviousPage` with no `before`/`last` is typically `false` on forward pagination — fine.

**Backend flow issues (the important part):**
1. Flow step 6 "Controller — detects cursor-mode vs offset-mode" — but in GraphQL there are two **separate** resolver methods/queries, and in REST it's arguably better to have separate endpoints or let the pipe/service decide. Putting mode-detection branching in the controller is borderline "logic in controller." The canonical rule says controller is thin. Mode detection (which is just reading whether `cursor` is present) is acceptable routing, but I'll tighten the wording so it's clearly mapping, not logic.
2. Step 5 Pipe: "base64-decode cursor" — decoding/validating the opaque token is correctly a pipe/transform concern. Good. Clamping limit in the pipe is correct.
3. Interceptor caching keyed on full query string including JWT-derived scope — for a per-user catalog this is fine since catalog is public-ish; acceptable.
4. The DB "hit here" annotation correct at Database layer.
5. Guard reading `scope` — fine.

Now I'll write the polished section, keeping the same structure, fixing the narrative/example alignment, tightening the controller-vs-pipe-vs-service responsibilities, adding the `400` malformed-cursor path, and clarifying idempotency/safety of GET.

## 📑 GET with Pagination (offset AND cursor)

**Story:** Riya (`b1`) opens Acme's catalog and scrolls — first as numbered pages ("Page 1, 2 per page"), then as an infinite-scroll feed ("give me the next 2 after the last mouse I saw"). Same products, two pagination strategies, one safe & idempotent `GET`.

---

### REST

> `GET` is **safe** (no state change) and **idempotent** (same query → same page). Both strategies return `200 OK`; a malformed cursor or out-of-range param is a client error → `400 Bad Request`. Never `404` an empty page — an empty result set is still `200` with `data: []`.

#### A) Offset pagination — `page` / `limit`

```http
GET /products?status=ACTIVE&page=1&limit=2&sort=price:asc HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Cookie: sid=…
```

| Param    | In    | Example     | Meaning                                              |
|----------|-------|-------------|------------------------------------------------------|
| `status` | query | `ACTIVE`    | filter                                               |
| `page`   | query | `1`         | 1-based page number                                  |
| `limit`  | query | `2`         | page size (clamp server-side, e.g. ≤100)             |
| `sort`   | query | `price:asc` | deterministic ordering (required for stable paging)  |

**Response — `200 OK`**

```json
{
  "data": [
    { "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE" },
    { "id": "p2", "name": "Keyboard",       "price": 2500, "status": "ACTIVE" }
  ],
  "pagination": {
    "page": 1,
    "limit": 2,
    "totalItems": 2,
    "totalPages": 1,
    "hasNext": false,
    "hasPrev": false
  }
}
```

> `Link` header alternative (RFC 8288): `Link: <…?page=2&limit=2>; rel="next"`. With only 2 ACTIVE products there is no next page, so the `Link` header is omitted here.

#### B) Cursor pagination — `cursor` / `limit`

```http
GET /products?status=ACTIVE&limit=2&cursor=eyJpZCI6InAxIn0 HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
```

| Param    | In    | Example            | Meaning                                                                       |
|----------|-------|--------------------|-------------------------------------------------------------------------------|
| `limit`  | query | `2`                | page size                                                                     |
| `cursor` | query | `eyJpZCI6InAxIn0`  | opaque base64 token = `{"id":"p1"}`; returns rows **after** it. Omit on first call. |

**Response — `200 OK`**

```json
{
  "data": [
    { "id": "p2", "name": "Keyboard", "price": 2500, "status": "ACTIVE" }
  ],
  "pagination": {
    "nextCursor": null,
    "hasNext": false,
    "limit": 2
  }
}
```

> Cursor `{"id":"p1"}` means "rows after p1", so `p2` is the only remaining row. As it's the last row, `hasNext: false` and `nextCursor: null` ⇒ end of list. The cursor is **opaque** — clients must never decode or construct it; a tampered/garbage token returns `400`, not `500`.

---

### Type contract

```typescript
// ─── Offset ─────────────────────────────────────────────
export interface OffsetPageQueryDto {
  status?: ProductStatus;
  page?: number;     // default 1, min 1
  limit?: number;    // default 20, min 1, max 100
  sort?: string;     // 'price:asc'
}

export interface OffsetPageMeta {
  page: number;
  limit: number;
  totalItems: number;
  totalPages: number;
  hasNext: boolean;
  hasPrev: boolean;
}

// ─── Cursor ─────────────────────────────────────────────
export interface CursorPageQueryDto {
  status?: ProductStatus;
  limit?: number;        // default 20, max 100
  cursor?: string;       // opaque base64 of { id: string }
}

export interface CursorPageMeta {
  nextCursor: string | null;
  hasNext: boolean;
  limit: number;
}

// ─── Shared envelope ────────────────────────────────────
export interface PaginatedResponse<T, M> {
  data: T[];
  pagination: M;
}

// Concrete uses:
type OffsetProductsResponse = PaginatedResponse<Product, OffsetPageMeta>;
type CursorProductsResponse = PaginatedResponse<Product, CursorPageMeta>;
```

---

### GraphQL

#### A) Offset — `productsPage(offset, limit)`

```graphql
query OffsetProducts($status: ProductStatus, $offset: Int!, $limit: Int!) {
  productsPage(status: $status, offset: $offset, limit: $limit) {
    items { id name price status }
    totalCount
    hasNext
  }
}
```
Variables: `{ "status": "ACTIVE", "offset": 0, "limit": 2 }`

```json
{
  "data": {
    "productsPage": {
      "items": [
        { "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE" },
        { "id": "p2", "name": "Keyboard",       "price": 2500, "status": "ACTIVE" }
      ],
      "totalCount": 2,
      "hasNext": false
    }
  }
}
```

#### B) Cursor — Relay connection (`edges` / `node` / `pageInfo`)

```graphql
query CursorProducts($status: ProductStatus, $first: Int!, $after: String) {
  productsConnection(status: $status, first: $first, after: $after) {
    edges {
      cursor
      node { id name price status }
    }
    pageInfo {
      hasNextPage
      hasPreviousPage
      startCursor
      endCursor
    }
    totalCount
  }
}
```
Variables: `{ "status": "ACTIVE", "first": 2, "after": null }`

```json
{
  "data": {
    "productsConnection": {
      "edges": [
        { "cursor": "eyJpZCI6InAxIn0", "node": { "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE" } },
        { "cursor": "eyJpZCI6InAyIn0", "node": { "id": "p2", "name": "Keyboard",       "price": 2500, "status": "ACTIVE" } }
      ],
      "pageInfo": {
        "hasNextPage": false,
        "hasPreviousPage": false,
        "startCursor": "eyJpZCI6InAxIn0",
        "endCursor": "eyJpZCI6InAyIn0"
      },
      "totalCount": 2
    }
  }
}
```

> Relay convention: `after` takes the **`endCursor`** of the previous page, not a row id. With `after: null` this is the first page, so `hasPreviousPage` is `false`. Each edge's `cursor` is opaque and positional — the same encode-the-key rule as the REST cursor.

#### SDL operation signatures (extends the shared schema)

```graphql
type ProductPage {                 # offset style
  items: [Product!]!
  totalCount: Int!
  hasNext: Boolean!
}

type ProductEdge {                 # cursor / Relay style
  cursor: String!
  node: Product!
}
type PageInfo {
  hasNextPage: Boolean!
  hasPreviousPage: Boolean!
  startCursor: String
  endCursor: String
}
type ProductConnection {
  edges: [ProductEdge!]!
  pageInfo: PageInfo!
  totalCount: Int!
}

extend type Query {
  productsPage(status: ProductStatus, offset: Int! = 0, limit: Int! = 20): ProductPage!
  productsConnection(status: ProductStatus, first: Int! = 20, after: String): ProductConnection!
}
```

#### NestJS resolver sketch

```typescript
@Resolver(() => Product)
export class ProductsResolver {
  constructor(private readonly products: ProductsService) {}

  // ── Offset ── thin: hand args straight to the service, no paging logic here
  @Query(() => ProductPage)
  productsPage(
    @Args('status', { nullable: true }) status: ProductStatus | undefined,
    @Args('offset', { type: () => Int, defaultValue: 0 }) offset: number,
    @Args('limit',  { type: () => Int, defaultValue: 20 }) limit: number,
  ): Promise<ProductPage> {
    return this.products.findPage({ status, offset, limit });
  }

  // ── Cursor / Relay ── service decodes `after`, runs keyset, builds the connection
  @Query(() => ProductConnection)
  productsConnection(
    @Args('status', { nullable: true }) status: ProductStatus | undefined,
    @Args('first', { type: () => Int, defaultValue: 20 }) first: number,
    @Args('after', { nullable: true }) after: string | undefined,
  ): Promise<ProductConnection> {
    return this.products.findConnection({ status, first, after });
  }
}
```

> In GraphQL the two strategies are **two distinct root fields** (`productsPage` vs `productsConnection`), so there is no runtime mode-detection — the schema picks the path. The resolver stays thin; `limit`/`first` clamping and `after`/cursor decoding live in the service (or a dedicated args pipe), never in the resolver body.

---

### Backend flow (this endpoint)

> Traced for the **REST cursor** call `GET /products?status=ACTIVE&limit=2&cursor=eyJpZCI6InAxIn0`. Offset-mode differs only at the Pipe (decode page/limit instead of a cursor) and the Repository (`OFFSET … + COUNT(*)` instead of keyset).

```
GET /products?status=ACTIVE&limit=2&cursor=eyJpZCI6InAxIn0
        │
        ▼
 ┌──────────────┐
 │ API GATEWAY  │  route /products → products-svc; coarse per-API-key rate-limit
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │ MIDDLEWARE   │  parse Bearer JWT + sid cookie onto req; stamp correlation-id
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │   GUARD      │  VERIFY JWT → req.user = Riya (b1); authenticated read is enough
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │ INTERCEPTOR  │  (pre) cache-key = full query string; MISS → continue
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │    PIPE      │  validate CursorPageQueryDto: clamp limit→[1,100];
 │              │  base64-decode cursor → {id:"p1"}; malformed ⇒ 400
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │ CONTROLLER   │  ProductsController.list(query) → calls the service; no logic
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │   SERVICE    │  keyset: ask repo for limit+1 (=3) rows to derive hasNext;
 │              │  encode last returned id as nextCursor
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │ REPOSITORY   │  WHERE status='ACTIVE' AND id > 'p1' ORDER BY id ASC LIMIT 3
 └──────┬───────┘
        ▼
 ┌──────────────┐
 │  DATABASE    │  ◄── DB HIT. returns [p2] (1 row < limit+1 → no further page)
 └──────┬───────┘
        ▼ (response walks back out)
 ┌──────────────┐
 │ INTERCEPTOR  │  (post) wrap → { data:[p2], pagination:{ nextCursor:null, hasNext:false, limit:2 } };
 │              │  populate cache under the query-string key
 └──────────────┘
```

**What each layer concretely does HERE:**

1. **Gateway** — routes `/products` to the products service and applies a coarse per-API-key rate limit (paginated list endpoints get hammered by infinite scroll).
2. **Middleware** — parses `Authorization: Bearer <JWT>` and the `sid` cookie onto the request and stamps a correlation-id so the whole scroll session is traceable. No trust decision yet.
3. **Guard** — verifies the JWT (signature + expiry) and sets `req.user = b1`. Catalog read needs only an authenticated user — there is **no** seller-ownership check (this is a public-ish list, not a per-seller admin view).
4. **Interceptor (pre)** — builds a cache key from the **full query string** (`status+limit+cursor`); on a hit it returns the cached page immediately and the DB is never touched.
5. **Pipe** — validates `CursorPageQueryDto`: **clamps `limit` into `[1,100]`** and **base64-decodes `cursor`** to `{ id: "p1" }`. A malformed/tampered token fails here → `400 Bad Request` (this is input shape, so it belongs in the pipe, not the service).
6. **Controller** — genuinely thin: binds the validated query DTO and calls `productsService` for that page. (Offset vs cursor is a *route/DTO* distinction the framework already resolved; the controller does not branch on business rules.)
7. **Service** — owns the paging algorithm: the keyset trick. It asks the repo for **`limit + 1` rows** (3) so it can tell whether a further page exists, then takes the last *returned* row's `id` and base64-encodes it as `nextCursor` (only if an extra row came back).
8. **Repository** — builds the keyset query: `WHERE status='ACTIVE' AND id > 'p1' ORDER BY id ASC LIMIT 3`. (Offset-mode instead issues `… ORDER BY price ASC LIMIT 2 OFFSET 0` **plus a `COUNT(*)`** for `totalItems`/`totalPages`.)
9. **Database** — **the single DB hit.** Returns only `[p2]` — fewer than `limit+1`, so the service concludes there is no further page → `hasNext = false` and `nextCursor = null`.
10. **Interceptor (post)** — wraps the rows into the `{ data, pagination }` envelope and **populates the cache** under the query-string key for the next identical request.

**SQL contrast at the DB (the whole point):**

```sql
-- OFFSET  (page 1, size 2): random-access, but O(offset) scan + a separate count
SELECT * FROM products WHERE status='ACTIVE' ORDER BY price ASC LIMIT 2 OFFSET 0;
SELECT COUNT(*) FROM products WHERE status='ACTIVE';          -- for totalItems/totalPages

-- CURSOR  (keyset): seeks straight to the spot, no count, stable under inserts
SELECT * FROM products WHERE status='ACTIVE' AND id > 'p1' ORDER BY id ASC LIMIT 3;
```

---

⚠️ **Gotcha:** **Offset drifts, keyset doesn't.** With `OFFSET`, if a new ACTIVE product is inserted before the current page between requests, every row shifts down one — the user sees a duplicate at the page boundary (or skips one), and a deep `OFFSET 100000` forces the DB to scan and discard 100k rows. Cursor/keyset (`id > lastId`) seeks directly to the position, so it is stable under concurrent inserts and O(limit) regardless of depth — but it **only supports next/prev, not random "jump to page 50."** Pick offset for small admin tables that need page numbers; pick cursor for large, live, infinite-scroll feeds.

---

## 📝 POST Create One (Body)

> **Story:** Riya (`b1`) commits to her cart — 2× Wireless Mouse (`p1`) from Acme (`s1`) — and hits **Place Order**. The client POSTs a new Order to the server, which mints `o1`, reserves stock, computes the `2400` total, and replies **201 Created** with a `Location` pointing at the fresh resource. An **idempotency key** guarantees a double-click (or a retry after a flaky network) never charges Riya twice.

---

### REST

```http
POST /orders HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Cookie: sid=…
Content-Type: application/json
Idempotency-Key: 4f1a9c2e-7b3d-4a51-9e0c-2f6a8b1d3e44
```

- **Method + path:** `POST /orders` — create a *new* member of the `/orders` collection. No id in the path (the server assigns it). `POST` is the correct verb precisely because it is **not idempotent** by default — the `Idempotency-Key` header is what *adds* safe-retry semantics on top.
- **Headers:** `Idempotency-Key` (client-generated UUID) dedupes retries; `Authorization` carries Riya's JWT; `Content-Type: application/json`.
- **Body** (the `PlaceOrderDto`):

```json
{
  "buyerId": "b1",
  "sellerId": "s1",
  "items": [
    { "productId": "p1", "qty": 2 }
  ]
}
```

**Response — `201 Created`:**

```http
HTTP/1.1 201 Created
Location: /orders/o1
Content-Type: application/json
Idempotency-Key: 4f1a9c2e-7b3d-4a51-9e0c-2f6a8b1d3e44
```

```json
{
  "id": "o1",
  "buyer":  { "id": "b1", "name": "Riya" },
  "seller": { "id": "s1", "name": "Acme" },
  "status": "PLACED",
  "total": 2400,
  "items": [
    { "productId": "p1", "name": "Wireless Mouse", "qty": 2, "unitPrice": 1200, "lineTotal": 2400 }
  ],
  "invoice": null
}
```

> On a **replayed** request (same `Idempotency-Key`, same body), the server returns the *same* `o1` body — but `200 OK` (not a second `201`), and no new order/charge is created. If the same key arrives with a *different* body, that's a misuse: reject with `422 Unprocessable Entity` rather than silently replaying the wrong order.

Status-code map: `201` success · `200` idempotent replay · `400` malformed/invalid body (qty ≤ 0, missing field) · `401` no/invalid JWT · `403` `buyerId` ≠ authenticated user · `404` unknown `productId`/`sellerId` · `409` insufficient stock or duplicate key still in-flight · `422` semantic conflict (e.g. product `ARCHIVED`, or key reused with a different body).

---

### Type contract

```typescript
// ─── REQUEST ─────────────────────────────────────────────
// Reuses PlaceOrderDto from the shared foundation (§4).
export interface PlaceOrderDto {
  buyerId: string;                 // 'b1'
  sellerId: string;                // 's1'
  items: Array<{
    productId: string;             // 'p1'
    qty: number;                   // 2  (validated: Int > 0)
  }>;
}

// The idempotency key rides in a header, not the body:
//   Idempotency-Key: <uuid>

// ─── RESPONSE ────────────────────────────────────────────
// Reuses OrderResponseDto from the shared foundation (§4).
export interface OrderResponseDto {
  id: string;                      // 'o1'
  buyer: Buyer;                    // { id:'b1', name:'Riya' }
  seller: Seller;                  // { id:'s1', name:'Acme' }
  status: OrderStatus;             // OrderStatus.PLACED
  total: number;                   // 2400
  items: Array<{
    productId: string;             // 'p1'
    name: string;                  // 'Wireless Mouse'
    qty: number;                   // 2
    unitPrice: number;             // 1200
    lineTotal: number;             // 2400
  }>;
  invoice?: Invoice | null;        // null until issued
}
```

---

### GraphQL

In GraphQL there is no `201`/`Location` — creation is a **mutation** that returns the created object directly (transport status stays `200`; errors surface in the `errors[]` envelope, not the HTTP status). Idempotency rides as an HTTP header on the transport (preferred — it stays orthogonal to the schema) rather than as a schema argument.

**Operation + variables:**

```graphql
mutation PlaceOrder($input: PlaceOrderInput!) {
  placeOrder(input: $input) {
    id
    status
    total
    buyer  { id name }
    seller { id name }
    items  { productId qty unitPrice }
    invoice { id status }
  }
}
```

```json
{
  "input": {
    "buyerId": "b1",
    "sellerId": "s1",
    "items": [ { "productId": "p1", "qty": 2 } ]
  }
}
```

**Response JSON:**

```json
{
  "data": {
    "placeOrder": {
      "id": "o1",
      "status": "PLACED",
      "total": 2400,
      "buyer":  { "id": "b1", "name": "Riya" },
      "seller": { "id": "s1", "name": "Acme" },
      "items":  [ { "productId": "p1", "qty": 2, "unitPrice": 1200 } ],
      "invoice": null
    }
  }
}
```

**SDL operation signature** (from the shared schema, §3):

```graphql
placeOrder(input: PlaceOrderInput!): Order!
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => Order)
export class OrderResolver {
  constructor(
    private readonly orders: OrdersService,
    private readonly buyers: BuyersService,
  ) {}

  @Mutation(() => Order)
  async placeOrder(
    @Args('input') input: PlaceOrderInput,   // validated by the global ValidationPipe
    @Context() ctx: GqlContext,              // carries req.user (the verified buyer) + raw headers
  ): Promise<Order> {
    const idemKey = ctx.req.headers['idempotency-key'] as string | undefined;
    // Resolver stays thin: no business logic, just hand off to the service.
    return this.orders.placeOrder(input, ctx.req.user.id, idemKey);
  }

  // Relations resolved lazily per field, avoiding over-fetch on the create path:
  @ResolveField('buyer', () => Buyer)
  buyer(@Parent() order: Order): Promise<Buyer> {
    return this.buyers.byId(order.buyerId);
  }
}
```

---

### Backend flow (this endpoint)

```
CLIENT  POST /orders  +Idempotency-Key: 4f1a…  body{ b1, s1, [p1×2] }
   │
   ▼
┌──────────────┐
│  GATEWAY     │  routes POST /orders → Nest; per-buyer write rate-limit
└──────┬───────┘
       ▼
┌──────────────┐
│ MIDDLEWARE   │  parses Authorization + Idempotency-Key headers onto req (no trust yet)
└──────┬───────┘
       ▼
┌──────────────┐
│   GUARD      │  verifies JWT → req.user = Riya(b1); 401 if invalid
└──────┬───────┘
       ▼
┌──────────────┐
│ INTERCEPTOR  │  (pre) looks up Idempotency-Key in store ──► HIT? short-circuit, replay 200
└──────┬───────┘
       ▼
┌──────────────┐
│    PIPE      │  ValidationPipe on PlaceOrderDto: qty=2 (Int>0), ids present
└──────┬───────┘
       ▼
┌──────────────┐
│ CONTROLLER   │  @Post() create() → reads req.user + key, calls service. No logic.
└──────┬───────┘
       ▼
┌──────────────┐
│   SERVICE    │  authZ: body.buyerId === req.user.id (403 else)
│              │  load p1, assert ACTIVE + stock≥2, freeze unitPrice=1200, total=2400
└──────┬───────┘  open TX → repo writes → store key (UNIQUE) → COMMIT
       ▼
┌──────────────┐
│ REPOSITORY   │  INSERT order, INSERT order_item, conditional UPDATE stock, INSERT key
└──────┬───────┘
       ▼
┌──────────────┐
│   DATABASE   │  ◄── rows committed atomically in one TX; returns new o1 row
└──────┬───────┘
       ▼
┌──────────────┐
│ INTERCEPTOR  │  (post) cache {key → o1}, serialize OrderResponseDto
└──────┬───────┘
       ▼
┌──────────────┐
│  CONTROLLER  │  sets 201 + Location: /orders/o1 (via @HttpCode/@Header or res)
└──────┬───────┘
       ▼
CLIENT  201 Created  Location: /orders/o1  { o1, total 2400, PLACED }
```

**What each layer concretely does here:**

1. **Gateway** — routes `POST /orders` into the Nest app; applies a per-buyer write rate-limit (placing orders is a mutating, chargeable action).
2. **Middleware** — parses the `Authorization` header **and** the `Idempotency-Key` header onto `req` (no trust decision yet — *parses*, doesn't verify).
3. **Guard** — verifies Riya's JWT (signature + expiry), sets `req.user = b1`; rejects with `401` if absent/invalid. The guard answers only *"is this caller authenticated?"* — it does **not** read the body, so the `buyerId`-matches-user check belongs downstream (it needs the validated DTO).
4. **Interceptor (pre)** — looks up `Idempotency-Key 4f1a…` in the idempotency store. **On a hit**, it short-circuits and replays the stored `o1` response with **`200 OK`** — the pipe/controller/service/DB are never touched, so no double-charge. (On a miss it falls through; the *atomic* key write happens later in the service's transaction.)
5. **Pipe** — `ValidationPipe` runs class-validator on `PlaceOrderDto`: `items` non-empty, each `qty` is an integer `> 0`, `buyerId`/`sellerId`/`productId` present non-empty strings. Pure **shape** validation against the body — no DB, no cross-entity checks. Rejects with `400` otherwise.
6. **Controller** — thin `@Post()` handler; pulls the `Idempotency-Key` from headers, the validated DTO from the body, and the user from `req.user`, then calls `ordersService.placeOrder(dto, user.id, idemKey)`. No logic.
7. **Service** — the business core. First the **ownership authZ** the guard couldn't do: assert `dto.buyerId === user.id` (else `403` — a buyer can't place an order *as* someone else). Then loads `p1`, asserts `status === ACTIVE` (`422` if `ARCHIVED`) and `stock ≥ 2` (`409` otherwise); freezes `unitPrice = 1200`; computes `lineTotal = 2400` and `total = 2400`; opens **one transaction** so the order, its line item, the stock decrement `50 → 48`, and the idempotency-key row commit atomically.
8. **Repository / ORM** — issues the writes inside that single transaction (see DB below); no business decisions, just parameterized queries and the conditional stock guard.
9. **Database (SQL)** — committed atomically:
   ```sql
   BEGIN;
   INSERT INTO orders (id, buyer_id, seller_id, status, total)
     VALUES ('o1','b1','s1','PLACED',2400);
   INSERT INTO order_items (id, order_id, product_id, qty, unit_price)
     VALUES ('oi1','o1','p1',2,1200);
   UPDATE products SET stock = stock - 2 WHERE id = 'p1' AND stock >= 2;  -- 50→48; 0 rows ⇒ rollback ⇒ 409
   INSERT INTO idempotency_keys (key, order_id) VALUES ('4f1a…','o1');     -- UNIQUE(key)
   COMMIT;
   ```
   The conditional `WHERE … stock >= 2` prevents overselling under concurrency: if it matches 0 rows, the service rolls back and returns `409`. Returns the freshly inserted `o1` row (+ its item).
10. **Interceptor (post)** — once the TX commits, stores `{idemKey → o1 response}` for future replays and serializes the enriched `OrderResponseDto`. The **`201 Created`** status and **`Location: /orders/o1`** header are set by the controller (e.g. `@HttpCode(201)` + `@Header`, or the injected `res`), since status/headers are an HTTP-layer concern, not the interceptor's transform job.

---

> ⚠️ **Gotcha — idempotency must be atomic with the write.** The `INSERT INTO idempotency_keys` and the order INSERT live in the **same transaction**. If you store the key *before* committing the order, a crash mid-flight leaves a key with no order (future retries falsely "succeed" with a `200` and Riya's order is lost). Put a **UNIQUE constraint on `idempotency_keys.key`** so two concurrent double-clicks race on the insert — the loser hits the constraint violation, rolls back, and is served the winner's `o1` (replay `200`) instead of creating a duplicate order. Note the division of labor: the **pre-interceptor** catches *sequential* retries cheaply (already-committed key → replay `200`), while the **UNIQUE constraint** is the backstop for *concurrent* double-clicks that both miss the pre-check and race to the DB.

---

```markdown
## 📦 Bulk Create Products

> **Story:** Seller Acme (`s1`) just onboarded and bulk-uploads a CSV of its catalogue. The app POSTs all rows at once — some are valid (Wireless Mouse, Keyboard), one is junk (negative price) — and the server tells Acme **exactly which rows landed and which bounced**.

---

### REST

`POST /products/bulk` creates many products in one request. Because some rows can succeed while others fail, the correct status is **`207 Multi-Status`** (partial success), not `201`.

`POST` (not `PUT`) because the server mints the ids — the request isn't naturally idempotent on its own. Safe retries are made idempotent **explicitly**, via the `Idempotency-Key` header (replay the stored response instead of re-inserting).

```
POST /products/bulk
Authorization: Bearer <JWT for s1>
Content-Type: application/json
Idempotency-Key: 8f3a-acme-upload-2026-06-04
```

**Request body** — an `items` array of products to create (sellerId comes from the JWT, not the body):

```json
{
  "items": [
    { "name": "Wireless Mouse", "price": 1200, "cost": 700,  "stock": 50 },
    { "name": "Keyboard",       "price": 2500, "cost": 1500, "stock": 30 },
    { "name": "Broken Item",    "price": -10,  "cost": 5,    "stock": 5  }
  ]
}
```

**Response — `207 Multi-Status`** (2 created, 1 rejected; the `results` array is index-aligned with the request):

```json
{
  "summary": { "requested": 3, "created": 2, "failed": 1 },
  "results": [
    { "index": 0, "status": "CREATED",  "id": "p1",
      "product": { "id": "p1", "name": "Wireless Mouse", "price": 1200, "cost": 700,  "stock": 50, "status": "ACTIVE", "sellerId": "s1" } },
    { "index": 1, "status": "CREATED",  "id": "p2",
      "product": { "id": "p2", "name": "Keyboard", "price": 2500, "cost": 1500, "stock": 30, "status": "ACTIVE", "sellerId": "s1" } },
    { "index": 2, "status": "REJECTED", "error": { "field": "price", "message": "price must be >= 0" } }
  ]
}
```

> **Status-code rule:** every row succeeds → **`201 Created`**; mixed outcome → **`207 Multi-Status`**; every row fails the per-row business rule → **`422 Unprocessable Entity`** (request well-formed, nothing persisted). A malformed *shape* (e.g. `items` missing or not an array, a non-numeric `price`) is rejected wholesale by the pipe as **`400 Bad Request`** before the service runs.

---

### Type contract

```typescript
// ─── Request ────────────────────────────────────────────
// One product to create. No id/status/sellerId — server assigns them.
export interface CreateProductInput {
  name: string;
  price: number;   // must be >= 0
  cost: number;    // must be >= 0
  stock: number;   // integer >= 0
}

export interface BulkCreateProductsDto {
  items: CreateProductInput[];   // 1..N rows
}

// ─── Response ───────────────────────────────────────────
export type BulkItemStatus = 'CREATED' | 'REJECTED';

export interface BulkItemError {
  field: string;     // 'price'
  message: string;   // 'price must be >= 0'
}

// Discriminated union, index-aligned with the request array.
export interface BulkCreatedResult {
  index: number;
  status: 'CREATED';
  id: string;            // 'p1'
  product: Product;      // full stored shape (see Foundation §4)
}

export interface BulkRejectedResult {
  index: number;
  status: 'REJECTED';
  error: BulkItemError;
}

export type BulkProductResult = BulkCreatedResult | BulkRejectedResult;

export interface BulkCreateProductsResponseDto {
  summary: { requested: number; created: number; failed: number };
  results: BulkProductResult[];
}
```

---

### GraphQL

GraphQL always returns HTTP `200`; the partial-success signal lives **in the payload** (`status` per item), not in a transport status code.

**SDL operation signature** (extends Foundation §3):

```graphql
input CreateProductInput {
  name: String!
  price: Float!
  cost: Float!
  stock: Int!
}

enum BulkItemStatus { CREATED REJECTED }

type BulkItemError {
  field: String!
  message: String!
}

type BulkProductResult {
  index: Int!
  status: BulkItemStatus!
  product: Product        # non-null only when CREATED
  error: BulkItemError    # non-null only when REJECTED
}

type BulkCreateSummary {
  requested: Int!
  created: Int!
  failed: Int!
}

type BulkCreateProductsPayload {
  summary: BulkCreateSummary!
  results: [BulkProductResult!]!
}

extend type Mutation {
  bulkCreateProducts(input: [CreateProductInput!]!): BulkCreateProductsPayload!
}
```

**Mutation + variables:**

```graphql
mutation BulkCreate($input: [CreateProductInput!]!) {
  bulkCreateProducts(input: $input) {
    summary { requested created failed }
    results {
      index
      status
      product { id name price status sellerId }
      error   { field message }
    }
  }
}
```

```json
{
  "input": [
    { "name": "Wireless Mouse", "price": 1200, "cost": 700,  "stock": 50 },
    { "name": "Keyboard",       "price": 2500, "cost": 1500, "stock": 30 },
    { "name": "Broken Item",    "price": -10,  "cost": 5,    "stock": 5  }
  ]
}
```

**Response JSON:**

```json
{
  "data": {
    "bulkCreateProducts": {
      "summary": { "requested": 3, "created": 2, "failed": 1 },
      "results": [
        { "index": 0, "status": "CREATED", "product": { "id": "p1", "name": "Wireless Mouse", "price": 1200, "status": "ACTIVE", "sellerId": "s1" }, "error": null },
        { "index": 1, "status": "CREATED", "product": { "id": "p2", "name": "Keyboard", "price": 2500, "status": "ACTIVE", "sellerId": "s1" }, "error": null },
        { "index": 2, "status": "REJECTED", "product": null, "error": { "field": "price", "message": "price must be >= 0" } }
      ]
    }
  }
}
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => BulkProductResult)
export class ProductResolver {
  constructor(private readonly products: ProductService) {}

  @Mutation(() => BulkCreateProductsPayload)
  @UseGuards(JwtAuthGuard)   // role=SELLER enforced inside the guard
  async bulkCreateProducts(
    @Args('input', { type: () => [CreateProductInput] }) input: CreateProductInput[],
    @CurrentUser() user: AuthUser,           // user.sellerId === 's1'
  ): Promise<BulkCreateProductsPayload> {
    // Resolver is thin: hand off to the service, which owns per-row
    // business validation, the transaction, and the index-aligned result.
    return this.products.bulkCreate(user.sellerId, input);
  }

  // seller resolved lazily, and only on the CREATED rows that carry a product.
  // Use a DataLoader to batch these and avoid N+1 reads across many rows.
  @ResolveField('seller', () => Seller)
  async seller(@Parent() result: BulkProductResult): Promise<Seller | null> {
    if (result.status !== 'CREATED') return null;
    return this.products.loadSeller(result.product.sellerId);
  }
}
```

---

### Backend flow (this endpoint)

```
CLIENT  POST /products/bulk  { items:[Mouse, Keyboard, Broken] }   Bearer s1  +  Idempotency-Key
   │
   ▼
GATEWAY ── routes /products/bulk; larger payload-size cap (bulk bodies are big)
   │
   ▼
MIDDLEWARE ── parses Authorization + Idempotency-Key headers onto the request (no trust decision)
   │
   ▼
GUARD ── verifies JWT, asserts role=SELLER → req.user = { sellerId:'s1', role:SELLER }   ✅ may create products
   │
   ▼
INTERCEPTOR(pre) ── reads Idempotency-Key; cache hit? → replay stored 207, short-circuit (skip pipe→DB)
   │
   ▼
PIPE ── validates BulkCreateProductsDto SHAPE only: items non-empty array,
   │      each row's name/price/cost/stock present & correctly typed.
   │      Does NOT enforce price>=0 (that's a per-row business rule → service)
   ▼
CONTROLLER ── ProductController.bulkCreate(dto, user.sellerId) — thin mapping, no logic
   │
   ▼
SERVICE ── ProductService.bulkCreate('s1', items):
   │        1. loop each row → apply business rule price/cost/stock >= 0
   │             • valid   → stage row (status=ACTIVE, sellerId='s1')
   │             • invalid → record REJECTED result at its index, do NOT touch DB
   │        2. if ANY valid rows → insert them in ONE transaction (atomic together)
   │        3. if ZERO valid rows → no DB call at all
   ▼
REPOSITORY ── ONE batched INSERT for the 2 valid rows (never a per-row loop)
   │
   ▼
DB (SQL) ──  BEGIN;
   │         INSERT INTO products (name,price,cost,stock,status,seller_id)
   │         VALUES ('Wireless Mouse',1200,700,50,'ACTIVE','s1'),
   │                ('Keyboard',2500,1500,30,'ACTIVE','s1')
   │         RETURNING id, ... ;   COMMIT;     ──►  returns p1, p2
   ▼
SERVICE(stitch) ── map p1,p2 back to request indices 0,1; merge REJECTED row at index 2;
   │               build summary {requested:3, created:2, failed:1}
   ▼
INTERCEPTOR(post) ── store final body under Idempotency-Key; set HTTP status 207
   │
   ▼
CLIENT  207 Multi-Status  { summary:{requested:3,created:2,failed:1}, results:[…] }
```

**What each layer concretely does here:**

1. **Gateway** — routes `/products/bulk`; enforces a larger payload-size limit because bulk bodies are big (guards against a 10 MB upload DoS).
2. **Middleware** — parses the `Authorization` and **`Idempotency-Key`** headers onto the request. It only *parses* — no trust or replay decision yet.
3. **Guard** — verifies the JWT (signature/expiry) and asserts `role=SELLER`; only a seller may create products. Sets `req.user.sellerId='s1'`.
4. **Interceptor (pre)** — looks up the `Idempotency-Key`; on a hit it **replays the previously stored `207`** and short-circuits, so the pipe, service, and DB never run and a retried upload can't double-insert.
5. **Pipe** — validates `BulkCreateProductsDto` at the **shape** level only: `items` is a non-empty array, and every row has correctly-typed `name/price/cost/stock`. It does **not** reject the negative-price row — that's a per-row business rule, and rejecting it here would `400` the *entire* batch instead of yielding a per-row `207`.
6. **Controller** — `ProductController.bulkCreate` maps the HTTP call to the service, passing `user.sellerId`. No logic.
7. **Service** — the core. Iterates rows and applies the business rule `price/cost/stock >= 0`. Valid rows are staged with `status=ACTIVE` and `sellerId='s1'`; the invalid row (index 2) is recorded as `REJECTED` and **never sent to the DB**. The valid rows are inserted inside **one transaction** (all-or-nothing for the rows actually attempted). If *no* row is valid, the service skips the DB entirely.
8. **Repository** — issues **one batched `INSERT … VALUES (…),(…)`** for the 2 valid rows — not a loop of single inserts (avoids the N+1 write trap).
9. **DB (SQL)** — inside the transaction, inserts the 2 rows and `RETURNING`s the generated ids `p1`, `p2`.
10. **Service (stitch)** — maps returned rows back to their original request indices, merges the rejected row, computes `summary {requested:3, created:2, failed:1}`.
11. **Interceptor (post)** — stores the full response body under the `Idempotency-Key` (so a later retry replays it) and sets the HTTP status to **`207`**. The exception filter is *not* involved here: a rejected row is normal data, not a thrown error.

> ⚠️ **Gotcha — partial failure + idempotency.** Three traps stack here. **(1) Never fail the whole batch for one bad row** — validate per-row in the *service* (not the pipe) and return a `207` with index-aligned results, never a `400`. **(2) Transaction scope** — the valid rows go in **one transaction** so they commit all-or-nothing; the rejected row sits *outside* it and never reaches the DB. **(3) Idempotency** — cache the *final* response under the `Idempotency-Key` so a network-retry **replays** it instead of inserting `p1`/`p2` a second time. And avoid the **N+1 write** trap: one batched `INSERT`, not one query per row.
```

---

The task gives me everything inline — I don't need to read any file. I'll return the polished Markdown directly.

## 🔄 PUT full replace

> **Story:** Acme (`s1`) edited the entire listing for **Wireless Mouse** (`p1`) in the seller dashboard and hit "Save". The client sends the *whole* product object — so any field Acme left out (like `discountId`) is reset to its default, not preserved.

---

### REST

`PUT` replaces the **entire resource** at a known URI. The client must send every writable field; the server treats the body as the new complete state. Omitting a field means "this field should now be absent/default" — **not** "leave it alone" (that's `PATCH`).

```
PUT /products/p1
```

| Part | Value |
|------|-------|
| Path param | `id = p1` (the product being replaced) |
| Headers | `Authorization: Bearer <JWT>` · `Content-Type: application/json` · `If-Match: "v7"` (optional optimistic-lock) |

**Request body** — the *full* replacement (note: no `discountId`, but `status` is explicit because every writable field is mandatory):

```json
{
  "name": "Wireless Mouse",
  "price": 1100,
  "cost": 650,
  "stock": 80,
  "status": "ACTIVE",
  "sellerId": "s1"
}
```

**Response — `200 OK`** (resource replaced; returns the new canonical state). `discountId` is now `null` because it was absent from the body:

```json
{
  "id": "p1",
  "name": "Wireless Mouse",
  "price": 1100,
  "cost": 650,
  "stock": 80,
  "status": "ACTIVE",
  "discountId": null,
  "sellerId": "s1"
}
```

> **Status notes:** `200 OK` (replaced an existing resource) — or `201 Created` *only* if you support upsert and `PUT` created a resource that did not exist before. `404 Not Found` if the resource is absent and you do **not** upsert. `400 Bad Request` if a required field is missing or an enum is invalid. `412 Precondition Failed` if `If-Match` doesn't match the current version (`428 Precondition Required` if you mandate `If-Match` and it's absent).

---

### Type contract

```typescript
// Request: the FULL replacement body. Every writable field is required
// (except genuinely optional relations). No `id` — it comes from the path.
export interface ReplaceProductDto {
  name: string;          // required
  price: number;         // required
  cost: number;          // required
  stock: number;         // required
  status: ProductStatus; // required — caller must state it explicitly
  sellerId: string;      // required
  discountId?: string | null; // optional → absent means "no discount"
}

// Response: the full, replaced Product (see Shared Foundation §4).
export type ReplaceProductResponse = Product;
```

> Contrast with `PATCH`'s DTO, where **every** field would be `?` optional (`Partial<…>`) and absence means "don't touch".

---

### GraphQL

GraphQL has no HTTP verbs, so "full replace" is modeled as an explicit `replaceProduct` mutation whose input mirrors `ReplaceProductDto` — required fields are required, so the client is forced to send the whole object.

**Mutation + variables:**

```graphql
mutation ReplaceProduct($id: ID!, $input: ReplaceProductInput!) {
  replaceProduct(id: $id, input: $input) {
    id
    name
    price
    cost
    stock
    status
    discount { id code percent }
    seller { id }
  }
}
```

```json
{
  "id": "p1",
  "input": {
    "name": "Wireless Mouse",
    "price": 1100,
    "cost": 650,
    "stock": 80,
    "status": "ACTIVE",
    "sellerId": "s1"
  }
}
```

**Response JSON** (`discount` resolves to `null` — it was not supplied):

```json
{
  "data": {
    "replaceProduct": {
      "id": "p1",
      "name": "Wireless Mouse",
      "price": 1100,
      "cost": 650,
      "stock": 80,
      "status": "ACTIVE",
      "discount": null,
      "seller": { "id": "s1" }
    }
  }
}
```

> Note: the SDL `Product` type exposes the relation `seller: Seller!` (not a scalar `sellerId`), so the selection set requests `seller { id }`. The *input* still carries the flat `sellerId: ID!`.

**SDL operation signature** (extends the Shared Foundation schema):

```graphql
input ReplaceProductInput {
  name: String!          # all required → forces a FULL object
  price: Float!
  cost: Float!
  stock: Int!
  status: ProductStatus!
  sellerId: ID!
  discountId: ID         # nullable → omit to clear the discount
}

extend type Mutation {
  replaceProduct(id: ID!, input: ReplaceProductInput!): Product!
}
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => Product)
export class ProductResolver {
  constructor(private readonly products: ProductService) {}

  @Mutation(() => Product)
  replaceProduct(
    @Args('id', { type: () => ID }) id: string,
    @Args('input') input: ReplaceProductInput, // required scalars enforce "full"
  ): Promise<Product> {
    return this.products.replace(id, input); // full overwrite
  }

  // Resolved lazily: `discount` is null here because the row's
  // discountId was cleared by replace().
  @ResolveField('discount', () => Discount, { nullable: true })
  discount(@Parent() p: Product) {
    return p.discountId ? this.products.findDiscount(p.discountId) : null;
  }

  @ResolveField('seller', () => Seller)
  seller(@Parent() p: Product) {
    return this.products.findSeller(p.sellerId);
  }
}
```

---

### Backend flow (this endpoint)

```
PUT /products/p1   { name, price, cost, stock, status, sellerId }   Authorization: Bearer <JWT>
        │
        ▼
┌──────────────┐
│ API GATEWAY  │  routes PUT /products/:id → Product service; TLS + rate-limit
└──────┬───────┘
       ▼
┌──────────────┐
│ MIDDLEWARE   │  parses Authorization header + cookies; sets correlation-id;
│              │  logs "PUT /products/p1"  (no trust decision yet)
└──────┬───────┘
       ▼
┌──────────────┐
│   GUARD      │  VERIFIES JWT, sets req.user; RolesGuard: caller is a SELLER.
│              │  (Cross-row ownership "owns p1?" is deferred to the service —
│              │   the guard has no DB row yet.)
└──────┬───────┘
       ▼
┌──────────────┐
│ INTERCEPTOR  │  (pre) start timer; no cache read (this is a write)
└──────┬───────┘
       ▼
┌──────────────┐
│    PIPE      │  ValidationPipe(whitelist, forbidNonWhitelisted) → ReplaceProductDto:
│              │  ALL of name/price/cost/stock/status/sellerId present & typed;
│              │  shape-only — NO DB access here
└──────┬───────┘
       ▼
┌──────────────┐
│ CONTROLLER   │  @Put(':id') → service.replace('p1', dto, req.user)
└──────┬───────┘
       ▼
┌──────────────┐
│   SERVICE    │  1) load p1 → 404 if missing
│              │  2) authorize: p1.sellerId === req.user.sellerId else 403
│              │  3) (optimistic lock) compare If-Match version else 412
│              │  4) build complete record from dto; unspecified discountId → null
└──────┬───────┘
       ▼
┌──────────────┐
│ REPOSITORY   │  one full-row UPDATE (or replaceOne) for p1, RETURNING *
└──────┬───────┘
       ▼
┌──────────────┐
│  DATABASE    │  SQL : UPDATE products SET name=$1, price=$2, cost=$3,
│              │        stock=$4, status=$5, seller_id=$6, discount_id=NULL
│              │        WHERE id='p1';   → 1 row affected, RETURNING *
│              │  Mongo: db.products.replaceOne({_id:'p1'}, {…full doc…})
└──────┬───────┘
       ▼  returns the fully-replaced row
┌──────────────┐
│ INTERCEPTOR  │  (post) serialize Product → 200 OK; stop timer
└──────────────┘
       │  (Exception filter fires only if a layer threw: 400/403/404/412)
       ▼
     CLIENT
```

1. **Gateway** — matches `PUT /products/:id` and forwards to the Product service; TLS + rate-limit at the edge.
2. **Middleware** — *parses* the `Authorization` header and cookies onto the request and attaches a correlation-id; logs the write. No trust decision yet.
3. **Guard** — *verifies* the JWT and sets `req.user`; `RolesGuard` confirms the caller's **role** is `SELLER`, else `403`. Row-level ownership ("does this seller own `p1`?") is **not** done here — the guard hasn't loaded the row. That check belongs in the service, where the row is fetched.
4. **Interceptor (pre)** — starts the timer; no cache read (this is a mutation).
5. **Pipe** — `ValidationPipe({ whitelist: true, forbidNonWhitelisted: true })` binds the body to `ReplaceProductDto` and **requires** every field; a missing `price` or bad `status` enum → `400` before any business logic runs. The pipe validates **shape only** — it never touches the DB.
6. **Controller** — `@Put(':id')` handler; pure mapping, calls `productService.replace('p1', dto, req.user)`.
7. **Service** — the business core, and the **only** layer that hits the DB for decisions: (a) fetches `p1` to confirm it exists (else `404`); (b) authorizes ownership — `p1.sellerId === req.user.sellerId` else `403`; (c) if `If-Match` was sent, compares the version and throws `412` on mismatch; (d) builds the **complete** new record from the DTO and sets `discountId = null` because the body omitted it — that's the defining "full replace" behavior.
8. **Repository** — issues **one** full-row write for `p1` (TypeORM `update` / Mongo `replaceOne`); manages the transaction. No business logic here.
9. **DB hit** — `UPDATE products SET …all columns…, discount_id=NULL WHERE id='p1'` (SQL, `RETURNING *`) or `replaceOne({_id:'p1'}, fullDoc)` (Mongo). Comes back: the single replaced row with `discountId = null`.
10. **Interceptor (post)** — serializes the row to the response DTO and returns `200 OK`. If any layer threw, the **exception filter** maps it to the right status (`400`/`403`/`404`/`412`) instead.

---

> ⚠️ **Gotcha — PUT is idempotent, and "missing = reset" bites silently.** Sending the same body twice yields the identical final state (unlike `POST`). But because omitted fields are *reset to default/null*, a client that sends a partial body thinking it's a tweak will silently wipe `discountId`, `stock`, etc. If you want "only change what I send", that's `PATCH` — not `PUT`. To avoid lost updates under concurrent edits, gate the write with `If-Match`/version and return `412` on mismatch (`428` if you require the header and it's missing).

---

I'll polish this PATCH section, fixing technical inaccuracies while keeping the structure. Let me work through it carefully.

Key issues I'm spotting to fix:
- **REST**: PATCH idempotency framing, `If-Match` semantics, status code accuracy, `405` for unsupported methods
- **Backend flow**: The `SellerOwnsProductGuard` loading `p1.sellerId` is itself a DB hit *before* the labeled "DB hit" step — this is a real layering inaccuracy worth being honest about. Also the empty-patch rejection ideally belongs partly in the pipe.
- **GraphQL**: idempotency note, resolver consistency
- **null-vs-absent**: ensure DTO/validation guidance is precise

## 🏷️ PATCH partial update

**Story:** Acme (`s1`) is running a flash sale — Riya keeps eyeing the **Wireless Mouse** (`p1`), so the seller drops its price from `1200` to `999` and nothing else. One field changes; everything else on `p1` stays exactly as it was.

---

### REST

`PATCH /products/p1` — send **only** the fields that change. (Contrast `PUT /products/p1`, which would require the *entire* product representation and overwrite omitted fields.)

```http
PATCH /products/p1 HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>        # must resolve to the owning seller s1
Cookie: sid=…
Content-Type: application/json
If-Match: "v7"                     # optional: optimistic-lock guard; server compares to the resource's current ETag
```

Request body (partial — just the discounted price):

```json
{
  "price": 999
}
```

Response — `200 OK` (the full updated resource is echoed back). The response SHOULD carry the new validator so the next conditional request can use it:

```http
HTTP/1.1 200 OK
Content-Type: application/json
ETag: "v8"
```
```json
{
  "id": "p1",
  "name": "Wireless Mouse",
  "price": 999,
  "cost": 700,
  "stock": 50,
  "status": "ACTIVE",
  "discountId": null,
  "sellerId": "s1"
}
```

**Status-code map for this endpoint**

| Situation | Status |
|-----------|--------|
| Patch applied, resource returned | `200 OK` |
| Patch applied, nothing returned in body | `204 No Content` |
| Malformed JSON / wrong type / `price < 0` / unknown fields | `400 Bad Request` |
| Empty patch (no updatable fields sent) | `400 Bad Request` |
| JWT missing/invalid/expired | `401 Unauthorized` |
| Caller is authenticated but not the owning seller of `p1` | `403 Forbidden` |
| `p1` does not exist | `404 Not Found` |
| Resource exists but route doesn't allow PATCH | `405 Method Not Allowed` |
| Body media type isn't `application/json` | `415 Unsupported Media Type` |
| Semantically invalid (e.g. `status` transition not allowed) | `422 Unprocessable Entity` |
| `If-Match` present but ETag is stale | `412 Precondition Failed` |

> **PATCH vs PUT:** `PATCH` = *partial* — omitted fields are left untouched. `PUT` = *full replace* — omitted fields are reset to default/null. Sending `{ "price": 999 }` via PATCH changes one column; via PUT it would blank out `name`, `stock`, `status`, etc.
>
> **Idempotency:** `PUT` is idempotent by definition (replaying the same full body yields the same state). `PATCH` is **not** idempotent in general — it depends on the patch's *form*. This absolute-set body (`price: 999`) happens to be idempotent (re-applying lands on the same value), but a relative body like `{ "stock": { "decrement": 5 } }` is not. Don't assume a retry is safe.

---

### Type contract

```typescript
// Request DTO — every field OPTIONAL; that optionality IS the PATCH semantics.
// (Compare: a PUT/replace DTO would make these REQUIRED.)
export interface UpdateProductDto {
  name?: string;
  price?: number;        // > 0
  cost?: number;         // > 0
  stock?: number;        // >= 0, integer
  status?: ProductStatus;
  discountId?: string | null;   // explicit null clears the discount
}

// Response — the full, post-update Product (see Shared Foundation §4).
export type UpdateProductResponse = Product;
```

> A subtle PATCH detail the type alone can't express: `discountId: null` (clear it) must be distinguishable from the key being **absent** (leave it alone). `Partial<Product>` collapses both to `undefined`, so the service must inspect *which keys are present* (`hasOwnProperty` / `key in dto`), not just their values. In NestJS this means **not** relying on a bare spread `{ ...existing, ...dto }` — a `class-validator` DTO preserves an explicit `null` but `ValidationPipe`'s default transform must not silently drop it.

---

### GraphQL

GraphQL mutations are partial by nature: only the input fields you supply are touched. `updateProduct` takes a partial `UpdateProductInput`.

Operation + variables:

```graphql
mutation UpdateProduct($id: ID!, $input: UpdateProductInput!) {
  updateProduct(id: $id, input: $input) {
    id
    name
    price
    status
  }
}
```

```json
{
  "id": "p1",
  "input": { "price": 999 }
}
```

Response JSON:

```json
{
  "data": {
    "updateProduct": {
      "id": "p1",
      "name": "Wireless Mouse",
      "price": 999,
      "status": "ACTIVE"
    }
  }
}
```

SDL operation signature (extends the Shared Foundation's `Mutation` + `input` blocks):

```graphql
input UpdateProductInput {
  name: String
  price: Float
  cost: Float
  stock: Int
  status: ProductStatus
  discountId: ID            # pass null to clear the discount; omit to leave untouched
}

type Mutation {
  updateProduct(id: ID!, input: UpdateProductInput!): Product!
}
```

> **GraphQL null-vs-absent:** GraphQL distinguishes a field set to `null` from one **not present in the variables** at the schema level — the resolver can read this difference, which is exactly what PATCH needs. (REST has to reconstruct it by inspecting the raw JSON keys.) There's no separate HTTP status here: a not-found `p1` or ownership failure surfaces as an entry in the top-level `errors` array (commonly with an `extensions.code` like `NOT_FOUND` / `FORBIDDEN`), not a `404`/`403`.

NestJS resolver sketch:

```typescript
@Resolver(() => Product)
export class ProductResolver {
  constructor(private readonly products: ProductService) {}

  @Mutation(() => Product)
  updateProduct(
    @Args('id', { type: () => ID }) id: string,
    @Args('input') input: UpdateProductInput,   // partial; only set keys present
    @CurrentUser() user: AuthUser,               // from req.user (guard-attached)
  ): Promise<Product> {
    // Ownership lives in the SERVICE (or a field/method guard) — the resolver
    // only wires args → service, exactly like the REST controller.
    return this.products.patch(id, input, user.sellerId);
  }

  // price/status live on the row already — no @ResolveField round-trips needed here.
}
```

---

### Backend flow (this endpoint)

```
PATCH /products/p1   { price: 999 }   Authorization: Bearer <JWT>
        │
        ▼
 ┌──────────────┐  Gateway: route PATCH /products/:id → ProductController
 │   GATEWAY    │  (PATCH is a distinct verb/handler from PUT)
 └──────┬───────┘
        ▼
 ┌──────────────┐  Middleware: parse Bearer token + sid cookie onto req
 │ MIDDLEWARE   │  (no trust decision yet)
 └──────┬───────┘
        ▼
 ┌──────────────┐  Guard: JwtAuthGuard verifies JWT → req.user (401 if bad).
 │   GUARD      │  Then SellerOwnsProductGuard loads p1 (← 1st DB read) and
 │              │  asserts p1.sellerId === user.sellerId, else 403 / 404.
 └──────┬───────┘
        ▼
 ┌──────────────┐  Interceptor(pre): start timer; NO cache read (this is a write)
 │ INTERCEPTOR  │
 └──────┬───────┘
        ▼
 ┌──────────────┐  Pipe: ValidationPipe(whitelist,forbidNonWhitelisted) on
 │    PIPE      │  UpdateProductDto — strip unknown keys, enforce price>0,
 │              │  keep only provided keys (preserving explicit null)
 └──────┬───────┘
        ▼
 ┌──────────────┐  Controller: maps id='p1' + validated body →
 │ CONTROLLER   │  productService.patch('p1', dto, user.sellerId)
 └──────┬───────┘
        ▼
 ┌──────────────┐  Service: reject empty patch (400); merge ONLY present keys
 │   SERVICE    │  onto p1; recompute status if stock crosses 0; If-Match→ETag
 └──────┬───────┘
        ▼
 ┌──────────────┐  Repository: partial UPDATE … SET <changed cols> WHERE id=p1
 │ REPOSITORY   │  (optionally AND version = 'v7' for optimistic lock)
 └──────┬───────┘
        ▼
 ┌──────────────┐  DB hit ↓↓↓  (the write)
 │     DB       │  UPDATE products SET price=999, version='v8'
 │              │  WHERE id='p1' [AND version='v7'] RETURNING *;
 └──────┬───────┘  → 1 row updated (0 rows ⇒ 412 stale)
        ▼  ░░ response back out ░░
 ┌──────────────┐  Interceptor(post): serialize row → JSON, set ETag, stop timer
 │ INTERCEPTOR  │
 └──────┬───────┘
        ▼
   200 OK  { id:'p1', name:'Wireless Mouse', price:999, … }   ETag: "v8"
```

What each layer concretely does **for this PATCH**:

1. **Gateway** — matches `PATCH /products/:id`, forwards to the Nest app. PATCH maps to a different handler than PUT; a route that only registered GET/PUT would yield **405** here.
2. **Middleware** — parses `Authorization: Bearer <JWT>` and the `sid` cookie onto the request. No trust decision yet (*middleware parses, guard verifies*).
3. **Guard** — `JwtAuthGuard` verifies signature/expiry → `req.user` (**401** if missing/invalid). Then `SellerOwnsProductGuard` **loads `p1`** (this is the **first DB read** — be honest that ownership checks touch the database *before* the service) and asserts `p1.sellerId === user.sellerId`, else **403**; if `p1` doesn't exist the guard surfaces **404** (don't leak existence as a 403). *Design note:* loading `p1` here means the service can be handed the already-fetched row to avoid a second read.
4. **Interceptor (pre)** — starts the timer. Deliberately **no cache lookup** — this is a mutation, not a read (and on the way out it must **invalidate** any cached `p1`).
5. **Pipe** — `ValidationPipe({ whitelist: true, forbidNonWhitelisted: true })` validates `UpdateProductDto`: `price` must be a positive number; unknown keys → **400**. It **strips** fields the client didn't send so the patch stays partial — while **preserving an explicit `discountId: null`** (clear) versus its absence (leave alone). It does *not* decide ownership or hit the DB.
6. **Controller** — pure mapping: pulls `id = 'p1'` from the path and the validated body, calls `productService.patch('p1', { price: 999 }, user.sellerId)`. No business logic.
7. **Service** — the business logic: rejects an **empty patch** (no updatable keys → **400**); applies **only the keys actually present** in the DTO (here just `price`), leaving `name/cost/stock/status/sellerId` untouched; if a `stock` change had crossed `0` it would flip `status` to `OUT_OF_STOCK`. It owns the **If-Match → ETag/version** comparison logic. (Existence/ownership were already settled by the guard; if the guard handed down the row, no re-fetch is needed.)
8. **Repository** — issues a **partial** update, setting only the changed columns (plus a bumped `version`), optionally scoping the `WHERE` by the expected version for optimistic locking.
9. **DB** — **the hit (a write):** `UPDATE products SET price = 999, version = 'v8' WHERE id = 'p1' AND version = 'v7' RETURNING *;` (Mongo equivalent: `db.products.findOneAndUpdate({ _id:'p1', version:'v7' }, { $set:{ price:999 }, $inc:{ version:1 } }, { returnDocument:'after' })`). One row affected returns the fresh row; **zero rows** means the version was stale → **412**.
10. **Interceptor (post) → response** — serializes the returned row to the full Product JSON, sets the new `ETag`, and returns **200 OK**.

> ⚠️ **Gotcha — partial vs full + null-vs-absent:** PATCH must update *only* the keys the client sent. The trap is `discountId: null` (intentionally clear the discount) vs the key being absent (leave it alone) — both collapse to `undefined` after a naive `Partial<>` mapping, so silently dropping `null` would fail to clear a discount the seller meant to remove. Inspect *which keys are present* (`key in dto` / `Object.prototype.hasOwnProperty`), don't just spread truthy values. And remember PATCH is **not idempotent in general** — this absolute-set form is safe to retry, but a relative/delta body (e.g. `{ "stock": { "decrement": 5 } }`) is not, so a blind client retry can double-apply.

---

This confirms my key concerns: DB query logic belongs in the repository/service layer (not handlers), no N+1 loops, and the entity-ID comparison rule (seller_id ownership). These map directly to the inaccuracies I found in the draft's backend flow.

The central correctness fix: the draft claims the **service** distinguishes `NOT_FOUND` / `FORBIDDEN` / `ILLEGAL_TRANSITION`, but the **single set-based `WHERE id IN (…) AND seller_id='s1' AND status IN ('PLACED','PAID')`** collapses all three rejection reasons into "absent from `RETURNING`" — indistinguishable. You can't have both per-reason reporting AND a single ownership-and-status-filtered write. The honest resolution: do a **read pass** to classify each id (exists? owned? legal transition?), then a **set-based write** for only the eligible ids, and reconcile `RETURNING` against the eligible set. Here is the polished section.

## 🔄 Bulk update (PATCH/PUT many)

**Story:** Acme (`s1`) just dropped a pallet at the courier — from the seller dashboard Acme selects orders `o1` and `o42` and hits **"Mark as Shipped"** in one click. (Sibling case: Acme bulk-applies a 10% discount to `p1` + `p2` before a sale.)

---

### REST

A **bulk partial update** is `PATCH` on a **collection action** sub-resource. `PATCH` (not `PUT`) because we send *only the field(s) to change*, not a full replacement of each order. The payload carries the list of ids + the new value. Because some items may fail (not found, not yours, illegal transition) while others succeed, the correct status is **`200 OK`** with a **per-item result report** — *not* a blanket `204` (the caller could never tell that `o99` failed). Pair it with an **`Idempotency-Key`** so a retried pallet-scan doesn't double-apply.

```http
PATCH /orders/bulk-status HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Content-Type: application/json
Idempotency-Key: 7c1f-ship-2026-06-04-acme-pallet-9

{
  "ids": ["o1", "o42", "o99"],
  "status": "SHIPPED"
}
```

**Response — `200 OK` (partial success):**

```http
HTTP/1.1 200 OK
Content-Type: application/json
```
```json
{
  "requested": 3,
  "succeeded": 2,
  "failed": 1,
  "results": [
    { "id": "o1",  "ok": true,  "status": "SHIPPED" },
    { "id": "o42", "ok": true,  "status": "SHIPPED" },
    { "id": "o99", "ok": false, "error": "NOT_FOUND" }
  ]
}
```

> Sibling endpoint: `PATCH /products/bulk-discount` with body `{ "ids": ["p1","p2"], "percent": 10 }` → same per-item report shape.

**Status-code choices:** `200` (mixed or all success, report in body) · `207 Multi-Status` (acceptable WebDAV-style alternative that signals "look inside for per-item codes") · `422 Unprocessable Entity` only if the *whole envelope* is malformed (e.g. unknown `status` value, empty `ids`) · `404` is **never** returned for individual missing ids — those are reported inside the body. Note `PATCH` is **not** required to be idempotent by HTTP, which is exactly why we add an explicit `Idempotency-Key` to make *this* one safe to retry.

---

### Type contract

```typescript
// ─── Request DTO ────────────────────────────────────────
export interface BulkOrderStatusDto {
  ids: string[];                 // ['o1','o42','o99'] — non-empty, unique
  status: OrderStatus;           // 'SHIPPED' (reuses shared enum)
}

// Sibling: bulk discount
export interface BulkProductDiscountDto {
  ids: string[];                 // ['p1','p2']
  percent: number;               // 10 — 0 < percent <= 100
}

// ─── Per-item result + envelope ─────────────────────────
export interface BulkItemResult {
  id: string;                    // 'o1'
  ok: boolean;                   // true | false
  status?: OrderStatus;          // present when ok
  error?: 'NOT_FOUND' | 'FORBIDDEN' | 'ILLEGAL_TRANSITION';
}

export interface BulkUpdateResponseDto {
  requested: number;             // 3
  succeeded: number;             // 2
  failed: number;                // 1
  results: BulkItemResult[];
}
```

---

### GraphQL

One **bulk mutation** carries the whole list in a single round-trip and returns the same per-item envelope. GraphQL has no HTTP status, so the **report shape *is* the contract** for partial failure — the mutation resolves successfully (top-level `errors` stays absent) and each id's fate lives in `results[]`.

**SDL operation signature** (extends the shared schema; `OrderStatus` is the shared enum):

```graphql
type BulkItemResult {
  id: ID!
  ok: Boolean!
  status: OrderStatus
  error: String
}

type BulkUpdateResult {
  requested: Int!
  succeeded: Int!
  failed: Int!
  results: [BulkItemResult!]!
}

extend type Mutation {
  bulkUpdateOrderStatus(ids: [ID!]!, status: OrderStatus!): BulkUpdateResult!
  bulkApplyDiscount(ids: [ID!]!, percent: Float!): BulkUpdateResult!
}
```

**Operation + variables:**

```graphql
mutation ShipBatch($ids: [ID!]!, $status: OrderStatus!) {
  bulkUpdateOrderStatus(ids: $ids, status: $status) {
    requested
    succeeded
    failed
    results { id ok status error }
  }
}
```
```json
{ "ids": ["o1", "o42", "o99"], "status": "SHIPPED" }
```

**Response JSON:**

```json
{
  "data": {
    "bulkUpdateOrderStatus": {
      "requested": 3,
      "succeeded": 2,
      "failed": 1,
      "results": [
        { "id": "o1",  "ok": true,  "status": "SHIPPED", "error": null },
        { "id": "o42", "ok": true,  "status": "SHIPPED", "error": null },
        { "id": "o99", "ok": false, "status": null,      "error": "NOT_FOUND" }
      ]
    }
  }
}
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => BulkUpdateResult)
export class OrderBulkResolver {
  constructor(private readonly orders: OrderService) {}

  @Mutation(() => BulkUpdateResult)
  bulkUpdateOrderStatus(
    @Args('ids', { type: () => [GraphQLID] }) ids: string[],
    @Args('status', { type: () => OrderStatus }) status: OrderStatus, // enum registered via registerEnumType
    @CurrentUser() user: AuthUser,           // from request context, set by the guard
  ): Promise<BulkUpdateResult> {
    // thin: delegates to the service, which returns the per-item envelope.
    // No field throws on partial failure — failures are data, not errors.
    return this.orders.bulkUpdateStatus(ids, status, user.sellerId);
  }
}
```

> `OrderStatus` must be exposed to the schema once via `registerEnumType(OrderStatus, { name: 'OrderStatus' })`, and `GraphQLID` is imported from `graphql` for the `[ID!]!` arg.

---

### Backend flow (this endpoint)

```
CLIENT  PATCH /orders/bulk-status  { ids:[o1,o42,o99], status:SHIPPED }
        Header: Idempotency-Key: 7c1f-ship-…-9
   │
   ▼
GATEWAY ──► routes /orders/* to Order service; rate-limit (bulk = 1 call, not 3)
   │
   ▼
MIDDLEWARE ─ parses JWT header + Idempotency-Key header onto req (no trust yet)
   │
   ▼
GUARD ────── verifies JWT; role must be SELLER (only a seller ships their orders).
   │          Coarse "is this caller a seller at all?" — NOT per-row ownership.
   ▼
INTERCEPTOR ─ (pre) IdempotencyInterceptor: key 7c1f… seen before?
   │            ├─ HIT  → replay stored 200 body, SKIP everything below  ┐
   │            └─ MISS → reserve key (in-flight), continue              │
   ▼                                                                     │
PIPE ─────── validates BulkOrderStatusDto SHAPE only: ids non-empty +    │
   │          unique, status ∈ OrderStatus enum. Bad envelope → 422.     │
   │          (Pipe does NOT touch the DB — existence/ownership is data, │
   │           not shape, so it belongs in the service.)                 │
   ▼                                                                     │
CONTROLLER ─ OrderController.bulkStatus(dto, user) — pure mapping;       │
   │          reads user.sellerId from context, no business logic.       │
   ▼                                                                     │
SERVICE ──── bulkUpdateStatus(ids, SHIPPED, sellerId):                   │
   │   (A) READ pass — load the orders for these ids (one query):        │
   │        SELECT id, seller_id, status FROM orders WHERE id IN (ids)   │
   │       then classify each requested id IN MEMORY:                    │
   │        ▸ row missing entirely        → NOT_FOUND                    │
   │        ▸ row.seller_id ≠ sellerId     → FORBIDDEN                    │
   │        ▸ status not in {PLACED,PAID}  → ILLEGAL_TRANSITION          │
   │        ▸ else                          → eligible                   │
   │   (B) collect the eligible ids → [o1, o42]                          │
   ▼                                                                     │
REPOSITORY ─ ONE transaction, ONE set-based write over eligible ids      │
   │          (no per-id loop, no N+1):                                  │
   ▼                                                                     │
   DB  ── SQL:  UPDATE orders                                            │
   │            SET status='SHIPPED'                                     │
   │            WHERE id IN ('o1','o42')        -- only the eligible set │
   │              AND seller_id='s1'            -- ownership re-asserted  │
   │              AND status IN ('PLACED','PAID') -- guards a race       │
   │            RETURNING id;        → [o1, o42]                         │
   │      (Mongo: updateMany({_id:{$in:eligible}, sellerId,             │
   │              status:{$in:['PLACED','PAID']}},                       │
   │              {$set:{status:'SHIPPED'}}) → {modifiedCount:2})         │
   ▼                                                                     │
SERVICE ──── reconcile RETURNING against the eligible set, merge with    │
   │          the classified rejects → build {requested, succeeded,      │
   │          failed, results[]}. (An eligible id NOT in RETURNING moved │
   │          since the read → report ILLEGAL_TRANSITION, the race case.)│
   ▼                                                                     │
INTERCEPTOR ─ (post) store {key → 200 body} so a retry replays the ◄─────┘
   │          SAME report; serialize the envelope.
   ▼
CLIENT  200 OK  { requested:3, succeeded:2, failed:1, results:[…] }
```

1. **Gateway** — routes `/orders/*` to the Order service; the whole batch counts as **one** rate-limit hit, not three.
2. **Middleware** — parses the `Authorization` and `Idempotency-Key` headers onto `req` (no trust decision yet).
3. **Guard** — verifies the JWT and enforces the **coarse** rule `role === SELLER`; a buyer can't ship anything. The guard does **not** check per-order ownership (it has no DB access and shouldn't classify data) — that's the service's job in step 7.
4. **Interceptor (pre)** — `IdempotencyInterceptor` looks up `7c1f-ship-…-9`: on a **HIT** it replays the previously stored `200` body and nothing below runs (the retried pallet-scan is a true no-op); on a **MISS** it reserves the key (so concurrent retries don't both execute) and proceeds.
5. **Pipe** — validates the `BulkOrderStatusDto` **shape**: `ids` is non-empty and de-duplicated, `status` is a real `OrderStatus`. A malformed *envelope* fails the whole call with `422`; the pipe never hits the DB, so individual bad ids are *not* its concern.
6. **Controller** — `OrderController.bulkStatus(dto, user)`; pure mapping, reads `user.sellerId` from context.
7. **Service** — owns all the business classification, because deciding *NOT_FOUND vs FORBIDDEN vs ILLEGAL_TRANSITION* requires reading rows, which a guard/pipe must not do. It runs a **read pass** to label every requested id, then hands only the **eligible** ids to the write. This is what lets the report distinguish "someone else's order" (`FORBIDDEN`) from "doesn't exist" (`NOT_FOUND`) — a single ownership-filtered `WHERE` alone could not tell them apart.
8. **Repository → DB** — **one** transaction, **one set-based `UPDATE … WHERE id IN (eligible) AND seller_id='s1' AND status IN ('PLACED','PAID') RETURNING id`** (Mongo: `updateMany` with `$in` → `modifiedCount`). The extra `seller_id` and `status` predicates re-assert the service's decision at write time and **guard against a concurrent change between the read and the write**; `RETURNING` tells the service exactly which rows actually moved.
9. **Service (reconcile)** — merges `RETURNING` with the rejects from the read pass into the final `{requested, succeeded, failed, results}` envelope. Any eligible id missing from `RETURNING` was changed by a concurrent request after the read and is reported `ILLEGAL_TRANSITION` rather than silently lost.
10. **Interceptor (post)** — stores `{Idempotency-Key → 200 body}` so any retry replays the identical report, then serializes the envelope back to the client.

---

> ⚠️ **Gotcha — partial failure ≠ all-or-nothing, but each row's write must still be atomic.** Return **`200` with a per-item report**, never a bare `204` (the caller can't tell `o99` failed). Do the write as **one set-based query over the eligible ids**, never a `for`-loop of N updates (classic N+1 → 3 round-trips collapse to 1). Crucially, a single ownership-and-status-filtered `WHERE` **cannot** distinguish *not-found* from *not-yours* from *illegal-transition* — they all just vanish from `RETURNING`. So classify in a **read pass** in the service first, then write only the eligible set; that's the only way the report can name the real reason per id. Finally, make it **idempotent** via `Idempotency-Key`: re-scanning the same pallet must mark `o1`/`o42` `SHIPPED` once. The post-interceptor replay handles the *retry*; the `WHERE status IN ('PLACED','PAID')` clause handles the *race*, making a second concurrent run a safe no-op for rows that already moved.

---

## 🗑️ DELETE one

> **Story:** Acme (`s1`) is discontinuing the **Keyboard** (`p2`). The seller hits "Remove product" in their catalogue dashboard, and MarketHub takes `p2` off the shelf.

---

### REST

```http
DELETE /products/p2 HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>          # must resolve to a user who owns s1
Cookie: sid=…
```

- **Path param:** `id = p2` (the product to remove)
- **Query param (optional):** `?hard=true` — opt into a true physical delete; default is soft-delete
- **Request body:** none (DELETE carries no body)

**Response — soft-delete (default):**

```http
HTTP/1.1 204 No Content
```

(empty body — the row still exists with `status: ARCHIVED`, but the client is told "gone, nothing to return")

**Response — second identical call (already archived):**

```http
HTTP/1.1 204 No Content
```

(idempotent — a repeat DELETE on an already-archived `p2` is still a success, still `204`; see the idempotency gotcha below)

**Response — if `p2` never existed:**

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{ "statusCode": 404, "message": "Product p2 not found", "error": "Not Found" }
```

**Response — if the caller does not own `s1`:**

```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{ "statusCode": 403, "message": "You do not own this product", "error": "Forbidden" }
```

> Why **204** and not 200? A successful delete has nothing meaningful to return — no body. `204 No Content` is the canonical REST answer. (Return `200` only if you choose to echo back the archived resource.)

---

### Type contract

```typescript
// ─── Request ─────────────────────────────────────────────
// No request body. The contract is entirely in the URL:
//   path:  { id: string }          // 'p2'
//   query: { hard?: boolean }      // ?hard=true → physical delete
export interface DeleteProductParams {
  id: string;                       // 'p2'
}
export interface DeleteProductQuery {
  hard?: boolean;                   // default false → soft-delete (ARCHIVED)
}

// ─── Response ────────────────────────────────────────────
// 204 No Content → the body type is literally `void`.
export type DeleteProductResponse = void;

// (Internally, soft-delete just flips the stored Product's status:)
//   Product.status: ProductStatus.ACTIVE → ProductStatus.ARCHIVED
```

---

### GraphQL

GraphQL has no "204"; a delete is a **mutation** that returns *something* — here a boolean success flag (you could alternatively return the deleted `ID!`).

**SDL operation signature** (extends the shared `Mutation` root):

```graphql
extend type Mutation {
  # returns true if the product was archived/removed
  deleteProduct(id: ID!, hard: Boolean = false): Boolean!
  # alt style:  deleteProduct(id: ID!): ID!   # echoes the removed id
}
```

**Mutation + variables:**

```graphql
mutation DeleteProduct($id: ID!, $hard: Boolean) {
  deleteProduct(id: $id, hard: $hard)
}
```

```json
{ "id": "p2", "hard": false }
```

**Response JSON:**

```json
{ "data": { "deleteProduct": true } }
```

**NestJS resolver sketch:**

```typescript
@Resolver(() => Product)
export class ProductResolver {
  constructor(private readonly products: ProductService) {}

  @Mutation(() => Boolean)
  async deleteProduct(
    @Args('id', { type: () => ID }) id: string,
    @Args('hard', { type: () => Boolean, nullable: true, defaultValue: false }) hard: boolean,
    @CurrentUser() user: AuthUser,           // from req.user (guard)
  ): Promise<boolean> {
    return this.products.remove(id, user.sellerId, hard);  // throws NotFound/Forbidden → GraphQL error
  }
}
```

> Ownership is enforced the same way as REST — a `@UseGuards(GqlAuthGuard, OwnershipGuard)` on the resolver. A failed check surfaces as a GraphQL `errors[]` entry (with `extensions.code` `FORBIDDEN` / `NOT_FOUND`), not an HTTP status.

---

### Backend flow (this endpoint)

```
DELETE /products/p2?hard=false
   │
   ▼
┌──────────┐  route DELETE /products/:id → ProductController.remove
│ GATEWAY  │
└────┬─────┘
     ▼
┌────────────┐  parse Authorization + Cookie (no trust yet)
│ MIDDLEWARE │
└────┬───────┘
     ▼
┌────────────┐  AuthGuard: JWT valid? ─► RolesGuard: caller is a SELLER?
│  GUARD     │  (ownership of p2 needs a DB lookup → deferred to the service)
└────┬───────┘
     ▼
┌────────────┐  (pre) start timer — nothing to cache on a delete
│INTERCEPTOR │
└────┬───────┘
     ▼
┌────────────┐  ParseUUIDPipe on :id (well-formed?); ParseBoolPipe coerces hard → boolean
│   PIPE     │
└────┬───────┘
     ▼
┌────────────┐  ProductController.remove(id, user, hard) — maps args, no logic
│ CONTROLLER │
└────┬───────┘
     ▼
┌────────────┐  ProductService.remove(): load p2 → exists? owned by user.sellerId? →
│  SERVICE   │  soft: set status=ARCHIVED   |   hard: physical delete
└────┬───────┘
     ▼
┌────────────┐  ProductRepository: SELECT (load+own check) → UPDATE (soft) or DELETE (hard)
│ REPOSITORY │
└────┬───────┘
     ▼
┌────────────┐  ░ DB HIT ░
│    DB      │  load:  SELECT id, seller_id, status FROM products WHERE id='p2'
│            │  soft:  UPDATE products SET status='ARCHIVED' WHERE id='p2'  → 1 row
│            │  hard:  DELETE FROM products WHERE id='p2'                   → 1 row
└────┬───────┘
     ▼
┌────────────┐  (post) map service result → HTTP 204 No Content (strip any body)
│INTERCEPTOR │
└────┬───────┘
     ▼
  204 No Content
```

**What each layer concretely does for `DELETE /products/p2`:**

1. **Gateway** — matches `DELETE /products/:id` and routes to `ProductController.remove`; applies the global rate-limit.
2. **Middleware** — parses `Authorization: Bearer <JWT>` and the `sid` cookie onto the request; no decision yet.
3. **Guard** — `AuthGuard` verifies the JWT and sets `req.user`; `RolesGuard` confirms the caller has the `SELLER` role. **Per-resource ownership (`p2.sellerId === user.sellerId`) is *not* decided here** — it needs the product row from the DB, so it belongs in the service. A guard answers "is this caller allowed *in principle*?" using only the request + token; it does not hit the DB to load the target entity.
4. **Interceptor (pre)** — starts the timer. Nothing to look up in cache; a delete is never cache-served.
5. **Pipe** — validates that the path `id` is a well-formed identifier (`ParseUUIDPipe`) and coerces the `hard` query string (`"true"`/`"false"`) into a real `boolean` (`ParseBoolPipe`, defaulting to `false`). No request-body DTO to validate.
6. **Controller** — thin: calls `productService.remove('p2', user.sellerId, hard)`. No business logic.
7. **Service** — loads `p2`. If missing → throws `NotFoundException` (→ 404). If found but `p2.sellerId !== user.sellerId` → throws `ForbiddenException` (→ 403). Otherwise **branches**: *soft* (default) flips `status` `ACTIVE → ARCHIVED`; *hard* (`?hard=true`) requests a physical delete. (Already `ARCHIVED`? Treat as a no-op success — see idempotency gotcha.)
8. **Repository / ORM** — issues the reads/writes: `SELECT` to load p2 (for the existence + ownership check), then soft = `UPDATE`, hard = `DELETE`.
9. **DB hit** —
   - **Load:** `SELECT id, seller_id, status FROM products WHERE id='p2'` — drives the 404 (no row) and 403 (wrong owner) decisions.
   - **Soft (default):** `UPDATE products SET status='ARCHIVED' WHERE id='p2'` → **1 row affected**. The row survives (audit trail, existing OrderItems still resolve `p2`).
   - **Hard (`?hard=true`):** `DELETE FROM products WHERE id='p2'` → **1 row deleted**. Gone forever.
10. **Interceptor (post) → response** — there is no entity to serialize; the post-interceptor maps the successful result to **`204 No Content`** and strips any body. (Errors instead flow to the exception filter → 404 / 403.)

> **Soft vs hard — the design call:** Prefer **soft-delete** (`status: ARCHIVED`) for anything referenced elsewhere. `p2` may sit inside historical `OrderItem`s and invoices; a hard `DELETE` would orphan those foreign keys (or trip an FK constraint), breaking order history. Soft-delete keeps referential integrity and gives you an audit trail — just remember to **filter `status != 'ARCHIVED'` in all catalogue/list queries** so archived products stop showing up. Reserve hard-delete for genuinely unreferenced rows or GDPR-style "must be erased" mandates.

> ⚠️ **Gotcha — idempotency:** DELETE must be **idempotent**. The *first* call archives `p2` and returns `204`; a *second* identical call should also return `204` (already gone = success), not error. Don't let a re-delete throw `404` just because the row is already `ARCHIVED` — decide your contract: treat "already archived" as a no-op `204`, and reserve `404` for an id that **never existed** at all. (Note: a *hard* `DELETE` is idempotent too — once the row is gone, repeat calls find no row; map that to `204`, not `404`, to honor the same contract.) Pick one and be consistent.

---

I'll review this Bulk delete section and polish it. Let me think through the technical correctness across REST, GraphQL, and especially the NestJS backend flow before rewriting.

Key issues I'm checking: the 207 serialization placement (post-interceptor vs exception filter vs controller), the idempotency claim, the pipe-vs-service concern split, and consistency with the shared foundation (e.g., `BulkDeleteFailureReason` enum must align, the post-interceptor can't set HTTP status reliably).

Let me produce the polished section.

The draft is strong but has a few real inaccuracies I'll fix:

1. **HTTP status in a post-interceptor is wrong.** A NestJS interceptor's `map()` can shape the body but the *status code* is set by the controller/handler (`@HttpCode`) or `res.status()`, not by the post-interceptor "mapping it to 207." The 207 decision depends on the body's `failed` array, so it must be set by the controller (after the service returns) — e.g. via a `@Res({ passthrough:true })` or a small status-deciding interceptor that reads the resolved value. I'll correct the layer attribution.
2. **Idempotency wording.** Re-running shouldn't flip `p1`/`p2` into the `failed` array as `NOT_FOUND` if we want "same 207 shape" — that's actually a *different* body. I'll make the idempotency point precise: the operation is idempotent in *effect* (end state identical), and a re-run yields a still-2xx response, but the body legitimately differs. That's the honest framing.
3. **Pipe doing dedupe/normalize is fine**, but "non-empty" is validation (pipe) — good. I'll keep concerns split cleanly.

---

## 🗑️ Bulk delete

> **Story:** Acme (`s1`) is clearing out its catalogue and asks to remove **both** the Wireless Mouse (`p1`) and the Keyboard (`p2`) in a single call — but one of the IDs in the batch might be stale.

---

### REST

`DELETE /products` — delete many products in one request. IDs can be sent either in the **body** or as a `?ids=` query list. Because the batch can partially succeed (some IDs gone, some never existed), this returns **`207 Multi-Status`** rather than `204`.

```
DELETE /products?ids=p1,p2,p999      ← (alt) ids as CSV query param
Authorization: Bearer <JWT>
Content-Type: application/json
```

Request body (preferred form):

```json
{ "ids": ["p1", "p2", "p999"] }
```

Response — **`207 Multi-Status`** (p1 & p2 gone, p999 never existed):

```json
{
  "deleted": ["p1", "p2"],
  "failed": [
    { "id": "p999", "reason": "NOT_FOUND" }
  ],
  "requested": 3,
  "deletedCount": 2
}
```

> **Status policy.** `207` is returned only when the outcome is *mixed* (≥1 deleted **and** ≥1 failed). If **every** id succeeds, return `200 OK` with `failed: []`. Reserve `204 No Content` for single-resource deletes (`DELETE /products/p1`) where there is no body to report. Because the status depends on the *result body*, the **controller** decides it after the service returns — it is not a fixed `@HttpCode`.

---

### Type contract

```typescript
// Request DTO — ids accepted in body; the query form (?ids=p1,p2)
// is parsed into this same shape by a pipe.
export interface BulkDeleteProductsDto {
  ids: string[];                 // ['p1', 'p2', 'p999'] — non-empty, unique
}

// One failed entry in the partial-success report.
export interface BulkDeleteFailure {
  id: string;                    // 'p999'
  reason: 'NOT_FOUND' | 'FORBIDDEN' | 'IN_USE';
}

// Response DTO — the 207 partial-success envelope.
export interface BulkDeleteProductsResponseDto {
  deleted: string[];             // ['p1', 'p2']
  failed: BulkDeleteFailure[];   // [{ id:'p999', reason:'NOT_FOUND' }]
  requested: number;             // 3
  deletedCount: number;          // 2
}
```

---

### GraphQL

GraphQL has no HTTP status codes, so partial success is expressed **in the payload** — the mutation always resolves (transport stays HTTP 200, `errors` absent) and the caller inspects `deleted` / `failed`.

Operation + variables:

```graphql
mutation BulkDeleteProducts($ids: [ID!]!) {
  bulkDeleteProducts(ids: $ids) {
    deleted
    failed { id reason }
    requested
    deletedCount
  }
}
```

```json
{ "ids": ["p1", "p2", "p999"] }
```

Response JSON:

```json
{
  "data": {
    "bulkDeleteProducts": {
      "deleted": ["p1", "p2"],
      "failed": [{ "id": "p999", "reason": "NOT_FOUND" }],
      "requested": 3,
      "deletedCount": 2
    }
  }
}
```

SDL operation signature (extends the shared `Mutation` + adds a result type):

```graphql
enum BulkDeleteFailureReason { NOT_FOUND FORBIDDEN IN_USE }

type BulkDeleteFailure {
  id: ID!
  reason: BulkDeleteFailureReason!
}

type BulkDeleteProductsResult {
  deleted: [ID!]!
  failed: [BulkDeleteFailure!]!
  requested: Int!
  deletedCount: Int!
}

extend type Mutation {
  bulkDeleteProducts(ids: [ID!]!): BulkDeleteProductsResult!
}
```

NestJS resolver sketch:

```typescript
@Resolver(() => Product)
export class ProductResolver {
  constructor(private readonly products: ProductService) {}

  @Mutation(() => BulkDeleteProductsResult)
  bulkDeleteProducts(
    @Args('ids', { type: () => [ID] }) ids: string[],
    @CurrentUser() user: AuthUser,        // seller scope for the IN_USE/FORBIDDEN checks
  ): Promise<BulkDeleteProductsResponseDto> {
    return this.products.bulkDelete(ids, user.sellerId);
  }
}
```

---

### Backend flow (this endpoint)

```
DELETE /products  { ids: ["p1","p2","p999"] }
   │
   ▼
┌──────────────┐
│ API GATEWAY  │ routes DELETE /products → MarketHub
└──────┬───────┘
┌──────▼───────┐
│ MIDDLEWARE   │ parses Authorization: Bearer <JWT> + cookies onto req (no trust yet)
└──────┬───────┘
┌──────▼───────┐
│ GUARD        │ verifies JWT · role must be SELLER · sets req.user (incl. sellerId)
└──────┬───────┘
┌──────▼───────┐
│ INTERCEPTOR  │ (pre) start timer — NO cache read (this is a write/delete)
└──────┬───────┘
┌──────▼───────┐
│ PIPE         │ shape-only: body OR ?ids= CSV → string[]; non-empty, deduped
└──────┬───────┘
┌──────▼───────┐
│ CONTROLLER   │ calls products.bulkDelete(ids, user.sellerId); sets 200/207 from result
└──────┬───────┘
┌──────▼───────┐
│ SERVICE      │ partition requested → deleted[] vs failed[]; build totals
└──────┬───────┘
┌──────▼───────┐
│ REPOSITORY   │ ONE bulk DELETE scoped by sellerId, RETURNING id (no N+1)
└──────┬───────┘
┌──────▼───────┐
│ DATABASE     │ deletes p1,p2; p999 matches nothing → absent from RETURNING
└──────┬───────┘
   ░░░ back out ░░░
┌──────▼───────┐
│ INTERCEPTOR  │ (post) serialize envelope, stop timer (body only, NOT the status)
└──────────────┘
```

1. **Gateway** — routes `DELETE /products` into the MarketHub app; global rate-limit applies.
2. **Middleware** — parses the `Authorization` header and cookies onto the request (no trust decision yet).
3. **Guard** — verifies the JWT and enforces the **SELLER** role; attaches `req.user` (with `sellerId`) so downstream layers can scope by tenant. A non-seller is rejected here with `403` — it never reaches the service.
4. **Interceptor (pre)** — starts the timer; deliberately **skips cache** since this mutates state.
5. **Pipe** — *structural concern only*: coalesces the two input forms (split `?ids=` CSV, else take `body.ids`), asserts the array is **non-empty** and **de-duplicates** → `['p1','p2','p999']`. It does **not** check existence or ownership — those need the DB and belong in the service.
6. **Controller** — thin mapping: calls `products.bulkDelete(['p1','p2','p999'], 's1')`, then **chooses the status from the result** (`207` if `failed.length && deletedCount`, else `200`). No business logic.
7. **Service** — the business logic and the **single source of the partition**: it asks the repository to delete the requested ids *scoped to `s1`*, treats the returned-id set as `deleted` (`p1`,`p2`), and derives `failed` for every requested id **not** returned (`p999` → `NOT_FOUND`). It computes `requested`/`deletedCount`. (Ownership/usage nuances — a foreign id or one referenced by an order — surface here as `FORBIDDEN` / `IN_USE`.)
8. **Repository** — issues **one** scoped bulk delete (no per-id round-trips), returning the ids actually removed so the service can diff:
   - **SQL:** `DELETE FROM products WHERE id = ANY($1) AND seller_id = $2 RETURNING id;` → `['p1','p2']`.
   - **Mongo:** the equivalent — fetch-then-`deleteMany` (or a pipeline) to know *which* ids were removed, since `deleteMany({ _id: { $in: [...] }, sellerId: 's1' })` only yields a `deletedCount`, not the id list the diff needs.
9. **Database** — removes the rows for `p1` and `p2`; `p999` matches nothing (absent, or owned by another seller), so it is simply not in the returned set, and the service maps that gap → `failed`.
10. **Interceptor (post)** — serializes the partial-success **body** and stops the timer. It does **not** set the HTTP status — that was already fixed by the controller in step 6 (an interceptor can reshape the payload but should not own the 207 decision, which depends on the resolved result).

---

> ⚠️ **Gotcha — partial failure + idempotency.** Never let one stale id (`p999`) `404` the **whole** batch; report it inside `failed` and keep the batch `2xx`. Delete is **idempotent in effect**: re-running the call leaves the catalogue in the *same end state* (p1/p2 already gone) and still returns `2xx` — but the body legitimately changes (a second run reports `p1`/`p2` under `failed` as `NOT_FOUND` and a `200`/`207` with `deletedCount: 0`). So callers must treat `NOT_FOUND` on a delete as **success-equivalent**, not a hard error. And always scope the `DELETE` by `seller_id` so one tenant can't wipe another's products by guessing ids — a foreign id comes back as `NOT_FOUND` (don't leak `FORBIDDEN`/existence across tenants).

---

## 🪪 Auth context: JWT header + cookies

> **Story:** Riya (`b1`) hits **"Place Order"** on her cart (2× Wireless Mouse from Acme). The browser sends her access token in `Authorization: Bearer …` *and* a `sid` session cookie. MarketHub must figure out *who* she is from the JWT, validate her session, and only then let the service place the order **as `b1`** — the client never says "I am b1" in the body; the server derives it from the token.

---

### REST

The buyer identity is **not** in the request body — it comes from the token. The body carries only *what* she's buying.

```http
POST /orders  HTTP/1.1
Host: api.markethub.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiMSIsIm5hbWUiOiJSaXlhIiwicm9sZSI6IkJVWUVSIiwic2lkIjoic2Vzc184ZjNhMmMiLCJpYXQiOjE3MDAwLCJleHAiOjk5OTk5OTk5OTl9.sig
Cookie: sid=sess_8f3a2c…; theme=dark
X-CSRF-Token: c7f1e9a4-bcsrf
Content-Type: application/json
Idempotency-Key: ord-2026-06-04-b1-001
```

```json
{
  "sellerId": "s1",
  "items": [{ "productId": "p1", "qty": 2 }]
}
```

**Response — `201 Created`** (`Location: /orders/o1`):

```json
{
  "id": "o1",
  "buyer":  { "id": "b1", "name": "Riya" },
  "seller": { "id": "s1", "name": "Acme" },
  "status": "PLACED",
  "total": 2400,
  "items": [
    { "productId": "p1", "name": "Wireless Mouse", "qty": 2, "unitPrice": 1200, "lineTotal": 2400 }
  ],
  "invoice": null
}
```

**`POST /orders` is intentionally non-idempotent** — each successful call creates a *new* order. The `Idempotency-Key` header makes a *retry* safe: the server stores the key → first response, so a network-retried POST returns the original `201` (or a `409 Conflict`) instead of placing a duplicate order. (Don't confuse this with HTTP idempotency: `POST` is neither safe nor idempotent by method; the key is what restores at-most-once semantics.)

Status semantics:

| Status | When |
|--------|------|
| **`201 Created`** | Order placed; `Location: /orders/o1` set. |
| **`401 Unauthorized`** | JWT missing / expired / bad-signature. |
| **`403 Forbidden`** | JWT valid but `role !== BUYER`, **or** the CSRF / session-cookie check fails. |
| **`409 Conflict`** | Same `Idempotency-Key` replayed concurrently, or insufficient stock. |
| **`422 Unprocessable Entity`** | Body well-formed but invalid (e.g. `qty ≤ 0`, unknown `productId`). |

> Prefer standard `403` over a non-standard `419` for CSRF/session failures — `419` is a framework-ism, not an IANA status.

---

### Type contract

```typescript
// What the GUARD extracts from the verified JWT and attaches to the request.
export interface AuthUser {
  sub: string;                   // 'b1'  — the buyer id (JWT subject)
  name: string;                  // 'Riya'
  role: 'BUYER' | 'SELLER' | 'ADMIN';
  sessionId: string;             // 'sess_8f3a2c…' — JWT `sid` claim, matched against the sid cookie
}

// Augment Express's Request so downstream layers read a typed user.
declare module 'express' {
  interface Request {
    user?: AuthUser;             // set by JwtAuthGuard, carried in request context
  }
}

// Request body DTO — note: NO buyerId. Identity comes from the token, not the body.
export interface CheckoutBodyDto {
  sellerId: string;             // 's1'
  items: Array<{ productId: string; qty: number }>;
}

// Response — reuses OrderResponseDto from the shared foundation.
// (buyer is filled from req.user.sub, never from the request body)
```

---

### GraphQL

In GraphQL there's a *single* endpoint, so auth rides the same way: `Authorization` header + cookie on the one `POST /graphql` call. The token is parsed/verified **once per request** and dropped into the **GraphQL context**, which every resolver reads.

```graphql
mutation PlaceOrder($input: CheckoutInput!) {
  placeOrder(input: $input) {
    id
    buyer { id name }
    seller { id name }
    status
    total
    items { productId qty unitPrice }
  }
}
```

Variables — again, **no `buyerId`**; the server injects it from context:

```json
{
  "input": {
    "sellerId": "s1",
    "items": [{ "productId": "p1", "qty": 2 }]
  }
}
```

> Note: the shared SDL's `PlaceOrderInput` declares `buyerId: ID!`. For an authenticated checkout we use an **auth-derived variant** (`CheckoutInput`, with `buyerId` omitted and taken from `ctx.user.sub`) so a client can't spoof another buyer.

**Response:**

```json
{
  "data": {
    "placeOrder": {
      "id": "o1",
      "buyer":  { "id": "b1", "name": "Riya" },
      "seller": { "id": "s1", "name": "Acme" },
      "status": "PLACED",
      "total": 2400,
      "items": [{ "productId": "p1", "qty": 2, "unitPrice": 1200 }]
    }
  }
}
```

> GraphQL transports auth failures *inside* a `200 OK` body: a missing/expired JWT or failed CSRF check returns `{ "data": null, "errors": [{ "extensions": { "code": "UNAUTHENTICATED" | "FORBIDDEN" } }] }`, mapped by the exception filter. (The HTTP `401`/`403` distinction from REST collapses into the `errors[].extensions.code`.)

**SDL operation signature** (auth-derived input variant):

```graphql
input CheckoutInput {            # PlaceOrderInput minus buyerId
  sellerId: ID!
  items: [OrderItemInput!]!
}

type Mutation {
  placeOrder(input: CheckoutInput!): Order!   # buyerId resolved from ctx.user
}
```

**NestJS resolver sketch** — context is built per request from the header/cookie:

```typescript
// app.module.ts / GraphQLModule — build context ONCE per request:
GraphQLModule.forRoot<ApolloDriverConfig>({
  driver: ApolloDriver,
  context: ({ req }) => ({ req }),   // req.user is set later, by the guard
});

@Resolver(() => Order)
export class OrderResolver {
  constructor(private readonly orders: OrderService) {}

  @Mutation(() => Order)
  @UseGuards(GqlJwtAuthGuard)                    // verifies JWT, sets ctx.req.user
  placeOrder(
    @Args('input') input: CheckoutInput,
    @CurrentUser() user: AuthUser,              // custom decorator → ctx.req.user
  ): Promise<Order> {
    // buyerId is taken from the token, NOT from input
    return this.orders.placeOrder(user.sub, input);
  }
}

// @CurrentUser() decorator pulls user out of the GraphQL execution context.
// NOTE: the GqlJwtAuthGuard runs first and sets ctx.req.user; the guard —
// not the context factory — is what verifies the JWT.
export const CurrentUser = createParamDecorator(
  (_data, ctx: ExecutionContext) =>
    GqlExecutionContext.create(ctx).getContext().req.user,
);
```

---

### Backend flow (this endpoint)

```
Authorization: Bearer <JWT>   Cookie: sid=sess_8f3a2c   X-CSRF-Token: c7f1…
        │
        ▼
┌─────────────┐
│   GATEWAY   │  routes POST /orders; coarse "is there a Bearer token at all?" pre-check
└──────┬──────┘
       ▼
┌─────────────┐  PARSE   cookie-parser splits Cookie → req.cookies.sid;
│ MIDDLEWARE  │ ───────► raw Authorization header left on req.headers. (NO verify yet)
└──────┬──────┘
       ▼
┌─────────────┐  VERIFY  jwt.verify(token, secret): signature + exp OK?  (fail → 401)
│    GUARD    │ ───────► sid cookie maps to a live session & matches the JWT `sid` claim;
│ JwtAuthGuard│          X-CSRF-Token matches the session (double-submit);  (fail → 403)
└──────┬──────┘          role === BUYER?  (fail → 403)  →  set req.user = { sub:'b1', … }
       ▼
┌─────────────┐
│ INTERCEPTOR │  (pre) start timer; tag logs with correlation-id + user.sub
└──────┬──────┘
       ▼
┌─────────────┐  VALIDATE  CheckoutBodyDto: sellerId present, items non-empty,
│    PIPE     │ ─────────► each qty is Int > 0.  (buyerId is NOT in the DTO → silently dropped)
└──────┬──────┘
       ▼
┌─────────────┐  reads req.user.sub ('b1') from context — never re-parses the header.
│ CONTROLLER  │  calls orders.placeOrder(req.user.sub, body)
└──────┬──────┘
       ▼
┌─────────────┐  business logic: load p1, check stock≥2, price 2×1200=2400,
│   SERVICE   │  build Order{ buyerId:'b1', sellerId:'s1', status:PLACED };
└──────┬──────┘  (idempotency-key lookup also lives here / in a dedicated guard)
       ▼
┌─────────────┐  open ONE transaction; INSERT order + order_item; decrement stock
│ REPOSITORY  │
└──────┬──────┘
       ▼
┌─────────────┐  SQL:  INSERT INTO orders(buyer_id, seller_id, status, total)
│  DATABASE   │        VALUES ('b1','s1','PLACED',2400) RETURNING id;  → 'o1'
└──────┬──────┘        INSERT INTO order_items(order_id, product_id, qty, unit_price)
       │               VALUES ('o1','p1',2,1200);  COMMIT;
       ▼
┌─────────────┐
│ INTERCEPTOR │  (post) shape rows → OrderResponseDto (buyer from req.user, not body)
└──────┬──────┘
       ▼
   201 Created + Location: /orders/o1
```

**What each layer concretely does here:**

1. **Gateway** — routes `POST /orders`; does a coarse pre-check that *some* `Authorization: Bearer` header exists (rejects obviously anonymous traffic) before it reaches Nest. No signature check here.
2. **Middleware** — `cookie-parser` splits the `Cookie` header into `req.cookies` (so `req.cookies.sid = 'sess_8f3a2c…'`); the raw `Authorization` header is left on `req.headers` for the guard. **Parses, does not trust.**
3. **Guard (`JwtAuthGuard`)** — the trust decision: `jwt.verify()` checks **signature + expiry** (fail → `401`); confirms the `sid` cookie maps to a live server-side session **and** equals the JWT's `sid` claim, and that `X-CSRF-Token` matches the session (fail → `403`); checks `role === 'BUYER'` (fail → `403`). On success it sets `req.user = { sub:'b1', name:'Riya', role:'BUYER', sessionId:'sess_8f3a2c…' }`. This is authN/authZ only — **no DB writes, no business logic**.
4. **Interceptor (pre)** — starts the latency timer and tags logs with the correlation-id and `user.sub`.
5. **Pipe** — validates `CheckoutBodyDto`: `sellerId` present, `items` non-empty, every `qty` is an `Int > 0`. With `whitelist: true`, a stray `buyerId` in the body is **stripped**, not bound — so a malicious client can't smuggle in an identity. The pipe does **not** hit the DB (e.g. it doesn't verify the product exists — that's the service's job).
6. **Controller** — reads the authenticated id from **context** (`req.user.sub === 'b1'`), never re-reading the header, and calls `ordersService.placeOrder('b1', body)`. No business logic.
7. **Service** — the only layer with business rules: checks/records the `Idempotency-Key`, loads `p1`, verifies `stock ≥ 2` (else throws → `409`/`422`), computes `2 × 1200 = 2400`, and builds the `Order` stamped with `buyerId: 'b1'` from the token. It orchestrates the repository but issues no raw SQL itself.
8. **Repository / ORM** — opens **one transaction** and issues the two inserts plus the stock decrement, so a partial failure rolls back.
9. **Database** — `INSERT … RETURNING id` yields `o1`; the order-item row is written with `unit_price = 1200`; `COMMIT`.
10. **Interceptor (post) → Response** — the post-interceptor serializes `OrderResponseDto` (`buyer` filled from `req.user`, not the body); the controller returns **`201 Created`** with `Location: /orders/o1`. Any throw from layers 3–9 is instead mapped to a status by the **exception filter**.

---

⚠️ **Gotcha — cookie vs header, and CSRF:** The **`Authorization: Bearer` header** is immune to CSRF (a cross-site form can't add custom headers) but is exposed to XSS if you store the token in JS-readable storage. A **cookie** (`sid`) is sent automatically by the browser — convenient and, when `HttpOnly`, safe from XSS — but *therefore* vulnerable to **CSRF**, which is exactly why the cookie-based session here is paired with an `X-CSRF-Token` header (double-submit). Rule: **token-in-header only → no CSRF needed; session-in-cookie → you MUST add CSRF protection.** And don't authenticate the *same* request off both a cookie and a header without deciding which one is authoritative — here the **JWT is the identity**, the **cookie/CSRF pair only proves the request came from a live, same-origin session**.

---

## ⚡ Action / RPC-style (non-CRUD)

> **Story:** Riya changes her mind — she taps **"Cancel order"** on order `o1`. This isn't editing a field; it's a *business action* that flips the order to `CANCELLED`, restocks the 2× Wireless Mouse, and voids the draft invoice `inv1` — all atomically.

These verbs (`cancel`, `pay`, `applyCoupon`) don't map to a single row's CRUD. They run multi-step business logic with side effects and state-machine rules. REST models them as a **verb sub-resource** (`POST /orders/o1/cancel`) instead of pretending it's a `PATCH`; GraphQL models them as **explicit mutations** (`cancelOrder(orderId)`). The name *is* the intent.

---

### REST

```http
POST /orders/o1/cancel HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Cookie: sid=…
Content-Type: application/json
Idempotency-Key: 8f3a-cancel-o1-riya
```

- **Verb `POST`, not `PATCH`.** A cancel is a state-machine transition with side effects (restock + void invoice), not a client-supplied field edit. The action is the path segment `/cancel`, never a query flag like `?status=CANCELLED`.
- **Path param:** `o1` — the order the action targets.
- **Body:** optional `reason`; the action itself needs no payload (the URL carries the target).

```json
{ "reason": "Changed my mind" }
```

**Response — `200 OK`** (action completed synchronously, returns the new resource state):

```json
{
  "id": "o1",
  "buyer":  { "id": "b1", "name": "Riya" },
  "seller": { "id": "s1", "name": "Acme" },
  "status": "CANCELLED",
  "total": 2400,
  "items": [
    { "productId": "p1", "name": "Wireless Mouse", "qty": 2, "unitPrice": 1200, "lineTotal": 2400 }
  ],
  "invoice": { "id": "inv1", "amount": 2400, "status": "VOID" }
}
```

**Status-code semantics for actions:**

| Situation | Code |
|-----------|------|
| Action succeeded, state changed | `200 OK` (sync) · `202 Accepted` (async/queued) |
| Order already `CANCELLED` (replayed action / repeated `Idempotency-Key`) | `200 OK` (idempotent — same body, no further change) |
| Cancel a `SHIPPED`/`DELIVERED` order — illegal state transition | `409 Conflict` |
| Body malformed (e.g. `reason` exceeds max length) | `400 Bad Request` |
| Order `o1` doesn't exist | `404 Not Found` |
| No/invalid JWT | `401 Unauthorized` |
| Valid caller, but not the buyer/seller of `o1` | `403 Forbidden` |

> **Why `POST`, not `PATCH /orders/o1 {status:"CANCELLED"}`?** The action encapsulates restock + void-invoice side effects the client must never set by hand. `POST` to a verb sub-resource is the idiomatic REST way to model a non-CRUD command. Note `POST` is *not* HTTP-idempotent by default — we make *this* action idempotent explicitly via the `Idempotency-Key` (see flow + gotcha).

---

### Type contract

```typescript
// Request DTO — the action body (everything optional; the URL carries the target)
export interface CancelOrderDto {
  reason?: string;                 // 'Changed my mind'
}

// Response DTO — reuses OrderResponseDto from the shared foundation,
// now with status: CANCELLED and a voided invoice.
export type CancelOrderResponseDto = OrderResponseDto;
// → { id:'o1', status:'CANCELLED', …, invoice:{ id:'inv1', status:'VOID' } }
```

---

### GraphQL

The action is a **mutation** — never a query (it mutates state) — named for the verb:

```graphql
mutation CancelOrder($orderId: ID!) {
  cancelOrder(orderId: $orderId) {
    id
    status                 # → CANCELLED
    total
    items { product { id name } qty unitPrice }
    invoice { id status }  # → VOID
  }
}
```

**Variables:**

```json
{ "orderId": "o1" }
```

**Response JSON:**

```json
{
  "data": {
    "cancelOrder": {
      "id": "o1",
      "status": "CANCELLED",
      "total": 2400,
      "items": [
        { "product": { "id": "p1", "name": "Wireless Mouse" }, "qty": 2, "unitPrice": 1200 }
      ],
      "invoice": { "id": "inv1", "status": "VOID" }
    }
  }
}
```

**SDL operation signature** (already in the shared schema):

```graphql
cancelOrder(orderId: ID!): Order!
```

> GraphQL has no transport status codes — a failed cancel (illegal transition, not found, forbidden) comes back in the `errors[]` envelope with an `extensions.code` (e.g. `CONFLICT`, `NOT_FOUND`, `FORBIDDEN`), mapped by the exception filter. There is no HTTP `Idempotency-Key` header here; idempotency is enforced in the service by re-checking the order's current state.

**NestJS resolver sketch:**

```typescript
@Resolver(() => Order)
export class OrderResolver {
  constructor(private readonly orders: OrderService) {}

  @Mutation(() => Order)
  @UseGuards(JwtAuthGuard, OwnsOrderGuard)   // verify JWT + ownership of o1
  cancelOrder(
    @Args('orderId', { type: () => ID }) orderId: string,
    @CurrentUser() user: AuthUser,           // from guard, via request context
  ): Promise<Order> {
    return this.orders.cancel(orderId, user); // ALL side effects live in the service
  }

  // Relations resolved on demand (batched via DataLoader to avoid N+1)
  @ResolveField(() => Invoice, { nullable: true })
  invoice(@Parent() order: Order, @Context('loaders') l: Loaders) {
    return l.invoiceByOrderId.load(order.id);
  }
}
```

---

### Backend flow (this endpoint)

```
POST /orders/o1/cancel  { reason }
        │
   ┌────▼─────┐  GATEWAY        route POST /orders/:id/cancel → Order service; global rate-limit
   └────┬─────┘
   ┌────▼─────┐  MIDDLEWARE     parse Authorization header + sid cookie; stamp correlation-id
   └────┬─────┘
   ┌────▼─────┐  GUARD          JwtAuthGuard verifies JWT → OwnsOrderGuard: is req.user buyer/seller of o1?
   └────┬─────┘
   ┌────▼─────┐  INTERCEPTOR    (pre) read Idempotency-Key; if already processed → replay stored 200, short-circuit
   └────┬─────┘
   ┌────▼─────┐  PIPE           validate CancelOrderDto (reason?: string ≤ 280 chars)
   └────┬─────┘
   ┌────▼─────┐  CONTROLLER     OrderController.cancel('o1', dto, user) — mapping only, no logic
   └────┬─────┘
   ┌────▼─────┐  SERVICE        ◄── the real work (see steps below), in ONE transaction
   └────┬─────┘
   ┌────▼─────┐  REPOSITORY     1 locking read + 3 writes, same tx
   └────┬─────┘
   ┌────▼─────┐  DATABASE       ░ rows locked, updated, committed ░
   └────┬─────┘
   ┌────▼─────┐  INTERCEPTOR    (post) serialize OrderResponseDto; store body under Idempotency-Key
   └────┬─────┘
        ▼  200 OK { status:CANCELLED, invoice:VOID }
```

1. **Gateway** — routes `POST /orders/:id/cancel` to the Order service; applies global rate-limit (cancel-spam protection). No business decision here.
2. **Middleware** — *parses* `Authorization: Bearer` + `sid` cookie onto the request and stamps a correlation-id for tracing this state change. No trust decision yet.
3. **Guard** — `JwtAuthGuard` **verifies** the JWT (signature/expiry) and sets `req.user`; then **`OwnsOrderGuard`** does a lightweight read of `o1` and checks `req.user` is its buyer `b1` (or seller `s1`) — else `403` (or `401` if the JWT itself is missing/invalid). Coarse authorization only; the cancellable-state check belongs to the service.
4. **Interceptor (pre)** — reads `Idempotency-Key: 8f3a-cancel-o1-riya`; if this key was already processed, **short-circuits** and replays the stored `200` body, so no double-restock on a retry.
5. **Pipe** — validates the shape of `CancelOrderDto`: `reason` is an optional string within max length. Shape/format only — no DB, no business rules.
6. **Controller** — thin: reads `user` from context, calls `orderService.cancel('o1', user)`, returns the result. No business logic, no DB.
7. **Service** — the heart; owns all business rules and the transaction boundary. In **one transaction**:
   - **a.** Load `o1` **with a row lock** and assert the current status is cancellable (`PLACED`/`PAID` ✅; `SHIPPED`/`DELIVERED` → throw → `409`). This state-machine check lives here, *not* in the guard or pipe.
   - **b.** Flip `o1.status` → `CANCELLED`.
   - **c.** **Restock**: for each line item, `p1.stock += 2` (give back the 2 mice → 50).
   - **d.** **Void invoice**: `inv1.status` → `VOID`.
   - **e.** Idempotency safety-net: if `o1` is *already* `CANCELLED`, return the existing state unchanged (the action is a no-op replay, not a `409`).
8. **Repository / ORM** — the data-access layer the service calls; emits the SQL inside the transaction (no business logic of its own):
   ```sql
   BEGIN;
   SELECT status FROM orders WHERE id='o1' FOR UPDATE;          -- lock the row first
   UPDATE orders   SET status='CANCELLED'  WHERE id='o1';
   UPDATE products SET stock = stock + 2   WHERE id='p1';       -- restock
   UPDATE invoices SET status='VOID'       WHERE order_id='o1';
   COMMIT;
   ```
   *(Mongo equivalent: `session.withTransaction()` wrapping the `findOne`-lock + three `updateOne` calls.)* **DB returns** the updated order + invoice rows.
9. **Interceptor (post)** — serializes the enriched `OrderResponseDto` and stores it under the `Idempotency-Key` for future replays; stops the timer.
10. **Exception Filter** — fires only if a layer threw: illegal transition → `409 Conflict`, missing order → `404 Not Found`, ownership failure → `403 Forbidden`. Maps to a GraphQL `errors[]` envelope on the GraphQL path.

---

> ⚠️ **Gotcha — idempotency & the lock.** Cancel has irreversible side effects (restock + void invoice). Two guards protect it: (1) the **`Idempotency-Key` replay** at the interceptor catches client retries/double-taps before any work runs; (2) the **`SELECT … FOR UPDATE` row lock** in the service serializes two *concurrent* cancels so the second sees `CANCELLED` and no-ops. Without both, a double-tap or race could restock `p1` **twice** (50 → 54). Rule: the action must be **idempotent** — replaying it returns the same `200` and changes nothing further. (`POST` is not idempotent by HTTP spec; we *make* this one idempotent on purpose.)

---

## ⚠️ Error & Validation Flow

> **Story:** Riya (`b1`) keeps hitting walls: she submits an order with `qty: -2`, asks for a product `pX` that doesn't exist, tries to cancel someone else's order without a valid token, and re-issues an invoice that's already issued. Each failure is caught at a *different layer* of the pipeline and formatted into a clean error — REST status code or GraphQL `errors[]`.

---

### REST

The same logical failures map to distinct HTTP status codes. Below: one request, several possible error responses depending on what's wrong.

**Request — place an order (the happy path that can fail many ways):**

```http
POST /orders HTTP/1.1
Host: api.markethub.io
Authorization: Bearer <JWT>
Content-Type: application/json

{ "buyerId": "b1", "sellerId": "s1", "items": [ { "productId": "p1", "qty": -2 } ] }
```

**Error matrix — which input triggers which status:**

| # | What's wrong | Status | Thrown by | Nest exception |
|---|--------------|--------|-----------|----------------|
| 1 | Missing/expired JWT | **401 Unauthorized** | Guard (authN) | `UnauthorizedException` |
| 2 | Token valid, but caller ≠ `b1` | **403 Forbidden** | Guard (authZ) | `ForbiddenException` |
| 3 | `qty: -2` (fails `@Min(1)`) | **400 Bad Request** | Pipe | `BadRequestException` (auto) |
| 4 | Semantically invalid (e.g. `items: []`) | **422 Unprocessable Entity** | Pipe/Service | `UnprocessableEntityException` |
| 5 | `productId: "pX"` not in DB | **404 Not Found** | Service | `NotFoundException` |
| 6 | Stock < qty, or invoice for `o1` already `ISSUED` | **409 Conflict** | Service | `ConflictException` |

> The rows are listed **in pipeline order** — the same order the checks actually run (Guard → Pipe → Service). That ordering is load-bearing: see the gotcha at the end.

> **400 vs 422:** `400` = the payload is *malformed / breaks a field rule* (negative number, wrong type, missing required field). `422` = the payload is *well-formed and typed correctly but business-invalid* (empty cart, ordering an `ARCHIVED` product). Many teams collapse both into 400 — MarketHub keeps them distinct.

**Example response — 400 (validation, the `qty: -2` case):**

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json
```
```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "message": ["items.0.qty must not be less than 1"],
  "path": "/orders",
  "timestamp": "2026-06-04T10:15:00.000Z"
}
```

**Example response — 404 (`GET /products/pX`):**

```http
HTTP/1.1 404 Not Found
Content-Type: application/json
```
```json
{
  "statusCode": 404,
  "error": "Not Found",
  "message": "Product pX not found",
  "path": "/products/pX",
  "timestamp": "2026-06-04T10:15:00.000Z"
}
```

**Example response — 409 (`POST /orders/o1/invoice` when already issued):**

```http
HTTP/1.1 409 Conflict
Content-Type: application/json
```
```json
{
  "statusCode": 409,
  "error": "Conflict",
  "message": "Invoice for order o1 is already ISSUED",
  "path": "/orders/o1/invoice",
  "timestamp": "2026-06-04T10:15:00.000Z"
}
```

> Every error body shares the **same envelope** (`statusCode`, `error`, `message`, `path`, `timestamp`) because one global **Exception Filter** formats them all — services just `throw new NotFoundException(...)` and never touch the response shape. `message` is a **string** for single errors, a **string[]** for validation (one entry per failed field rule).

> **Idempotency note:** `POST /orders/o1/invoice` is **not** naturally idempotent — a retry on a successful call would otherwise issue a second invoice. MarketHub makes invoice-issuing safe to retry by **state guard**: the first call moves the invoice `DRAFT → ISSUED`; any subsequent call sees it's already `ISSUED` and returns **409 Conflict** (rather than minting a duplicate). For create-order retries, the client sends an `Idempotency-Key` header; a replay with the same key returns the original `201` instead of creating a second order, and a key collision with a *different* body surfaces as **409**.

---

### Type contract

```typescript
// The single error envelope every REST failure is serialized into
// by the global HttpExceptionFilter.
export interface ApiErrorResponse {
  statusCode: 400 | 401 | 403 | 404 | 409 | 422 | 500;
  error: string;                 // 'Bad Request' | 'Not Found' | 'Conflict' | ...
  message: string | string[];    // string[] for field-level validation errors
  path: string;                  // '/orders'
  timestamp: string;             // ISO-8601
}

// What class-validator produces inside the Pipe before the filter
// flattens it into ApiErrorResponse.message[].
export interface ValidationFailure {
  property: string;              // 'qty'
  constraints: Record<string, string>; // { min: 'qty must not be less than 1' }
}

// Domain error codes the Service raises (carried in extensions for GraphQL).
export type DomainErrorCode =
  | 'PRODUCT_NOT_FOUND'          // → 404
  | 'ORDER_NOT_FOUND'           // → 404
  | 'INVOICE_ALREADY_ISSUED'    // → 409
  | 'INSUFFICIENT_STOCK'        // → 409
  | 'EMPTY_CART'                // → 422
  | 'FORBIDDEN_BUYER';          // → 403
```

---

### GraphQL

GraphQL **always returns HTTP 200** once the query is parsed and validated against the schema (a query that fails *parse/schema validation* — unknown field, wrong arg type — returns HTTP 400 with no `data`). Runtime failures live in the top-level `errors[]` array; `data` is `null` for a fully-failed operation, or **partially populated** when only some fields throw.

**Mutation with bad input (`qty: -2`):**

```graphql
mutation PlaceOrder($input: PlaceOrderInput!) {
  placeOrder(input: $input) {
    id
    total
  }
}
```
```json
// variables
{ "input": { "buyerId": "b1", "sellerId": "s1", "items": [ { "productId": "p1", "qty": -2 } ] } }
```

**Response — validation failure (`data: null`):**

> `qty: -2` is valid against the SDL (`qty: Int!` is satisfied by any Int), so this is *not* a schema-validation rejection — it's the `ValidationPipe` running `@Min(1)` on the input DTO and throwing `BadRequestException`, which the formatter maps to `BAD_USER_INPUT`.

```json
{
  "data": null,
  "errors": [
    {
      "message": "qty must not be less than 1",
      "path": ["placeOrder"],
      "extensions": {
        "code": "BAD_USER_INPUT",
        "field": "input.items.0.qty",
        "originalError": { "statusCode": 400 }
      }
    }
  ]
}
```

**Response — not found (`query { product(id: "pX") }`):**

```json
{
  "data": { "product": null },
  "errors": [
    {
      "message": "Product pX not found",
      "path": ["product"],
      "extensions": { "code": "PRODUCT_NOT_FOUND", "httpStatus": 404 }
    }
  ]
}
```

**Response — PARTIAL data (one field resolves, a nested resolver throws):**

> Riya fetches order `o1`; the order resolves fine, but its `invoice` field throws (billing service down). Because `Order.invoice` is **nullable** in the SDL, the thrown error nulls only that field — the order survives and the error is listed alongside it.

```json
{
  "data": {
    "order": {
      "id": "o1",
      "total": 2400,
      "invoice": null
    }
  },
  "errors": [
    {
      "message": "Invoice service unavailable",
      "path": ["order", "invoice"],
      "extensions": { "code": "UPSTREAM_UNAVAILABLE", "httpStatus": 503 }
    }
  ]
}
```

**SDL signatures (from the shared schema — no redefinition):**

```graphql
placeOrder(input: PlaceOrderInput!): Order!   # non-null → a thrown error nulls data.placeOrder entirely
product(id: ID!): Product                     # nullable → resolves to null, error still listed
order.invoice: Invoice                         # nullable field → error nulls just this field, partial data survives
```

> **Null-propagation rule:** an error thrown for a **non-null** field bubbles *up* to the nearest nullable parent and nulls that whole subtree. Because `placeOrder: Order!` is non-null and has no nullable parent above it (it sits directly on the root `Mutation`), the entire `data` becomes `null`. Because `order.invoice: Invoice` *is* nullable, only that leaf nulls and the rest of the order survives — that's exactly what makes partial data possible.

**NestJS resolver sketch — just `throw`, the GraphQL error formatter does the rest:**

```typescript
@Resolver(() => Order)
export class OrderResolver {
  constructor(private readonly orders: OrderService) {}

  @Mutation(() => Order)
  async placeOrder(@Args('input') input: PlaceOrderInput): Promise<Order> {
    // ValidationPipe already rejected qty:-2 with BAD_USER_INPUT before we get here.
    return this.orders.place(input); // may throw NotFound (404) / Conflict (409)
  }

  @Query(() => Product, { nullable: true })
  async product(@Args('id') id: string): Promise<Product | null> {
    const p = await this.orders.findProduct(id);
    if (!p) throw new NotFoundException(`Product ${id} not found`); // → errors[], data.product = null
    return p;
  }

  // Field resolver: if THIS throws, only order.invoice becomes null → partial data.
  @ResolveField('invoice', () => Invoice, { nullable: true })
  async invoice(@Parent() order: Order): Promise<Invoice | null> {
    return this.orders.findInvoice(order.id); // throw here → partial data + errors[]
  }
}
```

> A custom `GraphQLExceptionFilter` (or `formatError` in the driver config) maps each Nest exception to an `extensions.code`: `BadRequestException → BAD_USER_INPUT`, `UnauthorizedException → UNAUTHENTICATED`, `ForbiddenException → FORBIDDEN`, `NotFoundException → <DOMAIN>_NOT_FOUND`, `ConflictException → CONFLICT`. The original HTTP status is preserved under `extensions.httpStatus` (or `extensions.originalError.statusCode`) so the *same* service exceptions drive both transports.

---

### Backend flow (this endpoint)

This isn't one happy path — it's **several exit ramps**, one per layer that can throw. Follow where each error leaves the pipeline (layers run top-to-bottom; the **first** to throw wins and short-circuits the rest):

```
  POST /orders { items:[{ productId:"p1", qty:-2 }] }   Authorization: Bearer <JWT>
        │
        ▼
  ┌──────────────┐
  │ MIDDLEWARE   │  parse Authorization header + cookies onto req (no trust decision)
  └──────┬───────┘
         │
         ▼
  ┌──────────────┐                                                                 ┐
  │   GUARD      │  no/expired JWT? ───────────────► throw UnauthorizedException ──┤ 401
  │  (authN/Z)   │  token ok but caller ≠ b1? ─────► throw ForbiddenException ─────┤ 403
  └──────┬───────┘                                                                 │
         │ ok (req.user = b1)                                                      │
         ▼                                                                         │
  ┌──────────────┐                                                                 │
  │    PIPE      │  class-validator runs @Min(1) on qty                            │
  │ (Validation) │  qty = -2  ───────────────────► throw BadRequestException ──────┤ 400
  └──────┬───────┘  items = [] ──────────────────► throw UnprocessableEntity ──────┤ 422
         │ ok (DTO structurally valid)                                             │
         ▼                                                                         │
  ┌──────────────┐                                                                 │
  │ CONTROLLER   │  maps to orderService.place(dto) — no try/catch, no throw       │
  └──────┬───────┘                                                                 │
         ▼                                                                         │
  ┌──────────────┐                                                                 │
  │   SERVICE    │  ── REPO: SELECT * FROM products WHERE id IN ('p1')             │
  │              │     row missing? ───────────► throw NotFoundException ──────────┤ 404
  │              │     stock < qty / invoice    ─► throw ConflictException ────────┤ 409
  │              │     already ISSUED?                                             │
  └──────┬───────┘                                                                 │
         │ all checks pass → INSERT order + items (one tx)                         │
         ▼                                                                         │
  ┌──────────────┐                                                                 ▼
  │ REPO / DB    │  rows in / rows out;                          ┌────────────────────────┐
  │              │  unique-constraint violation ───────────────►│   EXCEPTION FILTER     │ 409
  └──────┬───────┘  (e.g. duplicate idempotency key)            │  catches ANY thrown    │
         │ success                                              │  error →               │
         ▼                                                      │  REST: ApiErrorResponse│
   201 Created                                                  │  GraphQL: errors[]     │
   + Location: /orders/o1                                       └───────────┬────────────┘
   + Order body                                                             ▼
                                                                  CLIENT gets error body
```

**What each layer concretely does for THIS endpoint:**

1. **Gateway** — passes the request through; a *transport-malformed* request (bad JSON, TLS failure) dies here as a raw `400`/connection error before Nest ever sees it. Domain errors are *not* decided here.
2. **Middleware** — *parses* the `Authorization` header and cookies onto `req`. It makes **no** trust decision and **never** throws a domain error.
3. **Guard** — the **only** place `401`/`403` originate. It *verifies* the JWT (signature + expiry): invalid/expired → `UnauthorizedException` (**401**); a valid token whose subject isn't `b1` and isn't an admin → `ForbiddenException` (**403**). Runs **before** the body is ever validated, and sets `req.user`.
4. **Interceptor (pre)** — no error logic; it only starts the timer (and on the way out, logs the final status code the filter produced).
5. **Pipe** — runs `class-validator` against `PlaceOrderDto`. `qty: -2` fails `@Min(1)` → `BadRequestException` (**400**) carrying a `string[]` of every failed constraint. An empty `items: []` is *structurally* well-formed but business-invalid → `UnprocessableEntityException` (**422**). The pipe validates **shape and field rules only** — it has no DB access, so it cannot know whether `p1` exists. The service is **never reached** if the pipe throws.
6. **Controller** — pure delegation to `orderService.place(dto)`; it reads `req.user` from context, contains **no** `try/catch` and **no** `throw`, and lets exceptions bubble to the filter.
7. **Service** — the **only** place `404`/`409` originate, because they require *state the pipe can't see*. After loading products via the repo: a missing `productId` → `NotFoundException` (**404**); `stock < qty`, or an invoice already `ISSUED` → `ConflictException` (**409**). The service owns all business rules and orchestrates the repository inside a transaction.
8. **Repository / DB** — `SELECT * FROM products WHERE id IN ('p1')`. **This is where "not found" becomes knowable** — an empty result set is what the *service* turns into a 404 (the repo returns data, it doesn't throw HTTP exceptions). On success, `INSERT order + order_items` runs inside one transaction; a unique-constraint violation (e.g. duplicate idempotency key) bubbles up and the service maps it to a **409**.
9. **Exception Filter** — if *any* layer threw, the **Exception Filter** (running outside the per-layer flow) catches it and formats the **single** envelope: REST → `ApiErrorResponse { statusCode, error, message, path, timestamp }`; GraphQL → `errors[]` with `extensions.code`. This is why every error in MarketHub looks identical regardless of which layer raised it. (The post-**interceptor** only runs on the *success* path; once a layer throws, the flow jumps straight to the filter.)

> **⚠️ gotcha:** **Order of checks = order of status codes.** The Guard runs *before* the Pipe, so an unauthenticated request with `qty: -2` returns **401, not 400** — you never leak "your payload is invalid" to a caller you haven't even authenticated. Likewise, the Pipe runs *before* the Service, so a bad `qty` returns **400 before** any DB lookup can produce a **404** — cheap, stateless checks fail fast before you ever touch the database. The pipeline deliberately orders failures **cheapest-and-least-trusting first**: authN/authZ → structural validation → stateful business rules.

---

```markdown
## ⚡ Real-time Status Updates (Push)

**Story:** Riya (`b1`) opens the order-tracking page for **Order `o1`** and watches its badge flip **PLACED → PAID → SHIPPED** live — no refresh, no "tap to update". Acme (`s1`) updates the order on a different server instance; Riya's screen reacts within milliseconds.

> The whole point of this pattern: the **server pushes** when *it* has news, instead of the client asking "any news yet?" over and over. See the contrast below.

---

### 2. REST — three ways to push (SSE recommended)

REST has no native push, so you pick a transport. For "one buyer watching one order's status" the cleanest is **Server-Sent Events (SSE)**: a single long-lived `GET` that streams events one-way (server→client).

#### Request (open the stream)

```http
GET /orders/o1/events HTTP/1.1
Host: api.markethub.com
Authorization: Bearer <JWT>
Accept: text/event-stream
Cache-Control: no-cache
Last-Event-ID: 0
```

| Part            | Value                                                              |
|-----------------|-------------------------------------------------------------------|
| Method          | `GET` (the stream is a *read*; safe + idempotent)                  |
| Path param      | `orderId = o1`                                                     |
| `Accept`        | `text/event-stream` — opts into SSE                               |
| `Last-Event-ID` | optional — last `id` the client saw, so the server replays from there on reconnect |

> Note: `Connection: keep-alive` is the HTTP/1.1 default and is set by the agent — clients don't send it explicitly, and it's meaningless under HTTP/2 (the usual transport for SSE today).

#### Response — `200 OK`, then an **open stream** that emits over time

```http
HTTP/1.1 200 OK
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive
X-Accel-Buffering: no
```
```
event: orderStatusChanged
id: 1
data: {"orderId":"o1","status":"PAID","total":2400,"at":"2026-06-04T10:00:05Z"}

event: orderStatusChanged
id: 2
data: {"orderId":"o1","status":"SHIPPED","total":2400,"at":"2026-06-04T10:03:40Z"}

: keep-alive
```

Each `data:` frame is one push. The `id:` lets the browser resume via `Last-Event-ID` after a drop. (`X-Accel-Buffering: no` tells proxies like nginx not to buffer the stream — otherwise frames arrive in a clump instead of live.)

#### The other two REST options (when SSE doesn't fit)

| Option        | How it works                                                                 | Status / shape                              | Use when                          |
|---------------|------------------------------------------------------------------------------|---------------------------------------------|-----------------------------------|
| **Polling**   | Client calls `GET /orders/o1` every N seconds, diffs `status` itself.        | `200 OK` + full `OrderResponseDto` each time (or `304 Not Modified` with `ETag`/`If-None-Match`) | Simplest; tolerates staleness. ⚠️ wasteful — N requests, mostly "nothing changed". |
| **Webhooks**  | *Server→server* push: MarketHub `POST`s to a URL the consumer registered.    | `POST https://buyer-svc/hooks` → expect `2xx` | Backend integrations, not a browser. Must be retried + idempotent (dedupe on `deliveryId`). |
| **SSE**       | One `GET` stays open; server streams events as they happen.                  | `200` + `text/event-stream`                  | A browser tab watching live. ✅    |

---

### 3. Type contract

```typescript
// What each pushed SSE frame / subscription payload carries.
export interface OrderStatusEvent {
  orderId: string;        // 'o1'
  status: OrderStatus;    // PAID → SHIPPED …  (reuses the shared enum)
  total: number;          // 2400
  at: string;             // ISO-8601 timestamp of the transition
}

// Webhook envelope (server→server variant): same payload, wrapped + signed.
export interface OrderStatusWebhook {
  event: 'order.status.changed';
  deliveryId: string;     // unique per delivery → consumer dedupes on this
  payload: OrderStatusEvent;
  signature: string;      // HMAC of the body; consumer verifies before trusting
}
```

The GraphQL `subscription orderStatusChanged(orderId)` resolves to the shared **`Order`** type, so the client gets the full live object (`status`, `total`, …) rather than a bare event.

---

### 4. GraphQL — `subscription` over WebSocket

A subscription is a long-lived operation: subscribe once, the server pushes a new `Order` on every transition.

#### Operation + variables

```graphql
subscription WatchOrder($orderId: ID!) {
  orderStatusChanged(orderId: $orderId) {
    id
    status
    total
  }
}
```
```json
{ "orderId": "o1" }
```

#### Pushed payloads (one frame per transition, same socket)

```json
{ "data": { "orderStatusChanged": { "id": "o1", "status": "PAID",    "total": 2400 } } }
```
```json
{ "data": { "orderStatusChanged": { "id": "o1", "status": "SHIPPED", "total": 2400 } } }
```

#### SDL signature (from the shared schema)

```graphql
type Subscription {
  orderStatusChanged(orderId: ID!): Order!
}
```

#### NestJS resolver sketch — publish on transition, filter per `orderId`

```typescript
@Resolver(() => Order)
export class OrderSubscriptionResolver {
  // Redis-backed PubSub → fans out across every app instance (see flow §5).
  constructor(@Inject('PUB_SUB') private readonly pubSub: RedisPubSub) {}

  @Subscription(() => Order, {
    // Server-side authZ + routing: only forward to sockets allowed to watch THIS order.
    // payload is the *envelope* published by the service; we unwrap it here.
    filter: (payload, variables, context) =>
      payload.orderStatusChanged.id === variables.orderId &&
      context.req.user.id === payload.orderStatusChanged.buyerId,
    // resolve maps the published envelope → the field's return shape.
    resolve: (payload) => payload.orderStatusChanged,
  })
  orderStatusChanged(@Args('orderId', { type: () => ID }) _orderId: string) {
    return this.pubSub.asyncIterator('orderStatusChanged');
  }
}

// Elsewhere — the SERVICE that flips the status publishes AFTER the write commits.
// (The mutation resolver/controller just delegates here; no business logic in the resolver.)
async markPaid(orderId: string): Promise<Order> {
  // Single DB hit for this transition; RETURNING * gives us the fresh row.
  const order = await this.orders.updateStatus(orderId, OrderStatus.PAID); // UPDATE … RETURNING *
  // Publish only after the transaction has committed, so subscribers never
  // observe a status the DB would later roll back.
  await this.pubSub.publish('orderStatusChanged', { orderStatusChanged: order });
  return order;
}
```

---

### 5. Backend flow (this endpoint) — **push** vs request/response

The key difference: the **write path** (Acme flips the status) and the **read/stream path** (Riya's open socket) are *decoupled* by **Redis Pub/Sub**. That decoupling is what lets a status change on **instance A** reach a subscriber pinned to **instance B**.

```
  ── WRITE PATH (Acme, instance A) ──────────────────────────────────────────
  Acme client                                                                
     │ POST /orders/o1/pay  (or GraphQL markPaid)                            
     ▼                                                                        
  Gateway → Middleware → Guard(seller owns o1?) → Interceptor → Pipe          
     │                                                                        
     ▼                                                                        
  Controller → Service.markPaid ──► Repository ──► ┌────────────┐             
                     │                  UPDATE     │  DATABASE  │             
                     │              orders SET     │ o1.status= │             
                     │              status='PAID'  │   'PAID'   │             
                     │              WHERE id='o1'  └─────┬──────┘             
                     │                  RETURNING *      │ updated row        
                     │ ◄──────────── (after COMMIT) ─────┘                    
                     ▼                                                        
            pubSub.publish('orderStatusChanged', order)                       
                     │                                                        
                     ▼                                                        
              ╔══════════════════╗   one channel, all instances              
              ║   REDIS Pub/Sub  ║◄──────────────┐                            
              ╚════════╤═════════╝                │ (fan-out)                 
                       │                          │                           
  ── STREAM PATH (Riya, instance B) ──────────────┴──────────────────────────
                       │ Redis delivers to every subscribed instance          
                       ▼                                                       
  Riya ══(WebSocket / SSE, opened earlier & kept alive)══╗                    
     ▲                                                   ║                    
     │   Subscription filter: id==='o1' && user owns o1?─╝                    
     │        ✔ match → push frame down Riya's socket                         
     └──◄── { orderStatusChanged: { id:'o1', status:'PAID', total:2400 } }    
```

**What each layer concretely does here — the two paths:**

**A. Subscribe (once, when Riya opens the page):** the full Nest pipeline runs *one time* on connect.
1. **Gateway** — routes the WebSocket upgrade (`wss://…/graphql`) or the SSE `GET /orders/o1/events`; must allow long-lived connections (no aggressive idle timeout).
2. **Guard** — runs **on connect**: verifies Riya's JWT and authorizes that she's a party to `o1` (the buyer). The socket then stays authenticated for its lifetime. *(Per-event ownership is re-checked cheaply in the resolver `filter` — see step B6 — so a token that expires mid-stream doesn't leak future events.)*
3. **Subscription resolver** — returns `pubSub.asyncIterator('orderStatusChanged')` and registers the **`filter`/`resolve`**. **No DB read happens here** — it just parks an open listener. **This is where the connection waits.**

**B. Publish (every time the status flips — the actual push):**
4. **Service `markPaid`** (write path, possibly a *different instance*) — runs the business transition behind the normal Guard→Pipe→Controller→Service pipeline, and performs the **only DB hit on this flow**: `UPDATE orders SET status='PAID' WHERE id='o1' RETURNING *` (Mongo: `findOneAndUpdate({_id:'o1'},{$set:{status:'PAID'}},{returnDocument:'after'})`). The transition is idempotent — re-applying `PAID` to an already-`PAID` order is a no-op, so a retried request won't double-publish a meaningful change.
5. **`pubSub.publish(...)`** — **after the write commits**, hands the fresh `Order` row to the **Redis** `orderStatusChanged` channel. Redis fans it out to **every** app instance, so it doesn't matter which instance holds Riya's socket.
6. **Subscription resolver (on Riya's instance)** — wakes on the Redis message and runs the **`filter`**: is this `id === 'o1'` *and* does the connected user own it? ✔ → `resolve` unwraps the envelope, the **Interceptor (post)** serializes the `Order`, and the frame is pushed down the already-open socket. Riya's badge flips to **PAID**. No new request, no controller, no pipe, no guard — the request/response pipeline ran *once at subscribe time*; everything after is pure push.

> Repeat steps 4–6 for `SHIPPED`; Riya sees each transition land live on the same socket.

---

### ⚠️ Gotcha

**Without Redis Pub/Sub, a single in-memory `PubSub` only notifies subscribers on the *same* instance.** With horizontal scaling (Acme's `markPaid` lands on **instance A**, Riya's socket is pinned to **instance B**), an in-process emitter never reaches her — the badge silently never updates. Redis (or any broker) is what makes the fan-out cross-instance. Bonus traps: (a) **publish only *after* the DB transaction commits** — emit inside an open transaction and a subsequent rollback leaves subscribers showing a `PAID` the DB never kept; (b) **SSE auto-reconnects** and replays from `Last-Event-ID`, so each event needs a stable `id` and consumers must treat replays as **idempotent**; (c) load balancers need **sticky sessions or a stateless socket layer** plus generous **idle-timeout**, or long-lived connections get culled mid-stream.
```

---

# REST → GraphQL → HTTP: The Capstone Reference

A single-page master reference mapping every common REST pattern to its GraphQL equivalent, the correct HTTP method/status, where each concern lives in the request pipeline, and a glossary of terms.

---

## 1. Master Table — REST ⇄ GraphQL ⇄ HTTP

> **How to read this:** "REST shape" is the resource-oriented call. "HTTP method + status" is the correct verb and the *success* status (error statuses noted where they matter). "GraphQL equivalent" shows the operation type (Query / Mutation / Subscription) — GraphQL almost always rides on a single `POST /graphql` returning `200`, so the *transport* HTTP column for GraphQL is given once at the bottom.

| # | Pattern | REST shape | HTTP method + success status | GraphQL equivalent | Notes / gotchas |
|---|---------|-----------|------------------------------|--------------------|-----------------|
| 1 | **GET one** (by id) | `GET /users/{id}` | `GET` → `200` (missing → `404`) | `query { user(id: "1") { … } }` | Path param ⇒ field argument. Null result in GQL = `data.user: null` (not a 404). |
| 2 | **GET list** (collection) | `GET /users` | `GET` → `200` (empty list still `200`, body `[]`) | `query { users { … } }` | Empty ≠ error. Never `404` an empty collection. |
| 3 | **GET + filter** (one field) | `GET /users?role=admin` | `GET` → `200` | `query { users(filter: { role: ADMIN }) { … } }` | Query string ⇒ `filter` input object. |
| 3b | **GET + filter** (many fields) | `GET /users?role=admin&active=true&q=sam` | `GET` → `200` | `query { users(filter: { role: ADMIN, active: true, search: "sam" }) { … } }` | One structured `filter` input beats many scalar args. |
| 4 | **GET + sort** (one key) | `GET /users?sort=createdAt` | `GET` → `200` | `query { users(orderBy: { field: CREATED_AT, dir: ASC }) { … } }` | Use enums for field + direction, not raw strings. |
| 4b | **GET + sort** (multi-key) | `GET /users?sort=-createdAt,name` | `GET` → `200` | `query { users(orderBy: [{ field: CREATED_AT, dir: DESC }, { field: NAME, dir: ASC }]) { … } }` | Sort is an *ordered list* of keys. |
| 5 | **GET nested / relations** | `GET /users/{id}/posts` | `GET` → `200` | `query { user(id:"1") { posts { … } } }` | GQL's headline win: fetch graph in one round-trip. |
| 5b | **List-of-list + N+1 risk** | `GET /users` then N× `GET /users/{id}/posts` | many `GET` → `200` | `query { users { posts { author { … } } } }` | Naive resolvers fire 1 + N queries. **Fix with DataLoader** (batch + per-request cache). See §3. |
| 6 | **Offset pagination** | `GET /users?limit=20&offset=40` | `GET` → `200` | `query { users(limit: 20, offset: 40) { … } }` | Simple; drifts/skips rows on concurrent writes. |
| 6b | **Cursor pagination** (Relay) | `GET /users?first=20&after=<cursor>` | `GET` → `200` | `query { users(first: 20, after: "cur") { edges { node { … } cursor } pageInfo { hasNextPage endCursor } } }` | Stable under inserts/deletes. Relay Connection spec = `edges/node/cursor/pageInfo`. |
| 7 | **POST create one** | `POST /users` + body | `POST` → `201 Created` (+ `Location` header) | `mutation { createUser(input: { … }) { user { id } } }` | `400` invalid body; `409` conflict (dup). GQL has no `Location`; return the created object. |
| 8 | **POST bulk create** | `POST /users/batch` + array | `POST` → `201` (or `207 Multi-Status` for partial) | `mutation { createUsers(input: [ … ]) { users { id } } }` | Decide all-or-nothing vs per-item result. `207` signals mixed success in REST. |
| 9 | **PUT full replace** | `PUT /users/{id}` + *full* body | `PUT` → `200` (or `201` if it created) | `mutation { replaceUser(id:"1", input: { …all fields… }) { user { … } } }` | **Idempotent.** Omitted field ⇒ reset to default/null. |
| 10 | **PATCH partial update** | `PATCH /users/{id}` + *partial* body | `PATCH` → `200` | `mutation { updateUser(id:"1", input: { email: "x" }) { user { … } } }` | Only sent fields change. Use **nullable** input fields; distinguish "absent" from "explicit null." |
| 11 | **Bulk update** (many) | `PATCH /users` + array of patches | `PATCH` → `200` / `207` | `mutation { updateUsers(input: [{ id:"1", patch:{…} }]) { users { … } } }` | Same all-or-nothing decision as bulk create. |
| 12 | **DELETE one** | `DELETE /users/{id}` | `DELETE` → `204 No Content` (or `200` w/ body) | `mutation { deleteUser(id:"1") { deletedId } }` | Idempotent. Already-gone → `404` *or* `204` (pick a convention). Prefer **soft delete** (§3) for audits. |
| 13 | **Bulk delete** | `DELETE /users?ids=1,2,3` (or body) | `DELETE` → `204` / `207` | `mutation { deleteUsers(ids:["1","2"]) { deletedIds } }` | Avoid unbounded "delete all"; require explicit ids/filter. |
| 14 | **Auth: JWT header** | `Authorization: Bearer <jwt>` | any verb; `401` no/bad token, `403` no permission | Same header on `POST /graphql`; token parsed into **context** | Guard/middleware verifies once; resolvers read `ctx.user`. |
| 14b | **Auth: cookies** | `Cookie: session=…` (HttpOnly) | any verb | Cookie sent automatically; server reads it into context | Needs CSRF protection + `SameSite`; CORS `credentials: true`. |
| 15 | **Action / RPC** (non-CRUD) | `POST /users/{id}/activate` | `POST` → `200`/`202 Accepted` | `mutation { activateUser(id:"1") { user { status } } }` | Model verbs as **mutations**, not contorted CRUD. `202` if async. |
| 16 | **Error & validation** | `4xx` + error body | `400` validation, `401/403` auth, `404` missing, `409` conflict, `422` semantic, `500` server | HTTP `200` + top-level `errors[]` array (`extensions.code`) | GQL returns errors *in the body*, not via status. Partial data + errors can coexist. |
| 17 | **Real-time push** | SSE (`text/event-stream`) or WebSocket | `GET` → `200` (stream stays open) | `subscription { orderStatus(id:"1") { status } }` over WS | REST polling ❌ → push. SSE = server→client one-way; WS = bidirectional (GQL subs). |

**GraphQL transport (applies to rows 1–16):** all Queries and Mutations → `POST /graphql` → HTTP **`200`** (even on logical errors; check `errors[]`). Subscriptions → WebSocket (`graphql-transport-ws`) or SSE.

---

## 2. Where Each Concern Lives (Pipeline Cheat-Sheet)

Request flows **top → bottom** on the way in, response unwinds **bottom → top** on the way out.

| Layer | Owns | REST (NestJS terms) | GraphQL analogue |
|-------|------|---------------------|------------------|
| **Gateway / Reverse proxy** | TLS, rate-limit, routing to service, edge caching | nginx / API Gateway | Same; single `/graphql` route |
| **Middleware** | Cross-cutting pre-processing: logging, CORS, body parse, raw auth-token extraction | `app.use()` / Nest middleware | Apollo/Yoga server plugins, `context` builder |
| **Guard** | **AuthN + AuthZ** — is the caller allowed? (returns `401/403`) | `@UseGuards(AuthGuard)` | Resolver/field guard, `@Directive`, or check in resolver using `ctx.user` |
| **Interceptor** | Wrap call: response **shaping**, transform, caching, timing, logging-around | `@UseInterceptors()` (RxJS) | Apollo plugin (`willSendResponse`), field middleware |
| **Pipe** | **Input validation + transformation/coercion** (`400` on bad input) | `@UsePipes(ValidationPipe)` | Input-type validation, custom scalars, schema directives |
| **Controller / Resolver** | Map request → call the right service; **no business logic** | `@Controller` route handler | `@Resolver` query/mutation/field resolver |
| **Service** | **Business logic**, orchestration, transactions, authorization rules | `@Injectable()` service | Same service layer, called by resolvers |
| **Repository / DAL** | **DB access only** — queries, ORM, no business rules | TypeORM/Prisma repo | Same repos; **DataLoader** sits here to batch N+1 |
| **Exception Filter** | Catch thrown errors → consistent **error response** + status | `@Catch()` exception filter | `formatError` / error-formatting plugin → `errors[]` |

**One-line ownership map:** Routing = *Gateway/Controller* · Auth = *Guard* · Validation = *Pipe* · Business logic = *Service* · DB access = *Repository* · Response shaping = *Interceptor* · Errors = *Exception Filter*.

**Golden rule:** controllers/resolvers stay thin (translate + delegate); services hold logic; repositories only touch the DB; never put DB queries in resolvers or business rules in repositories.

---

## 3. Glossary

- **Idempotency** — Repeating the same request yields the same end state. `GET`, `PUT`, `DELETE` are idempotent; `POST` and `PATCH` generally are **not**. Matters for safe retries (network timeouts). Use an *idempotency key* to make `POST` safe to retry.

- **PUT vs PATCH** — `PUT` = **full replace**: send the entire resource; omitted fields get reset. `PATCH` = **partial update**: send only changed fields. `PUT` is idempotent; `PATCH` may not be (e.g. "increment by 1").

- **Cursor pagination** — Page by an opaque **cursor** (a pointer to a row, e.g. encoded `id`/`createdAt`) instead of a numeric `offset`. Stable when rows are inserted/deleted between pages — no skipped or duplicated rows. Contrast with **offset pagination** (`limit`/`offset`), which is simpler but drifts under concurrent writes. Relay standardizes the shape: `edges { node, cursor }` + `pageInfo { hasNextPage, endCursor }`.

- **N+1 problem** — Fetching a list (1 query) then lazily loading a relation per item (N queries) = `1 + N` DB hits. Classic in GraphQL nested resolvers and naive ORM lazy-loading. Kills performance at scale.

- **DataLoader** — A per-request batching + caching utility that fixes N+1: it collects all the individual key lookups fired during one tick, issues **one** batched query (`WHERE id IN (…)`), and caches results for the request. Lives at the data-access layer, called from field resolvers.

- **SSE (Server-Sent Events)** — A one-way server→client streaming protocol over a single long-lived HTTP response (`Content-Type: text/event-stream`). Lighter than WebSockets, auto-reconnects, but **unidirectional** — good for status feeds/notifications. WebSocket is bidirectional and is what GraphQL subscriptions typically use.

- **Soft delete** — Mark a row deleted (e.g. set `deletedAt`/`isDeleted`) instead of physically removing it. Preserves history/audit trails and allows restore; queries must filter out soft-deleted rows. Contrast with **hard delete** (row is gone for good).

- **Mutation vs Query vs Subscription** (GraphQL) — `Query` = read (safe, side-effect-free, parallel-resolved); `Mutation` = write (runs serially); `Subscription` = long-lived push stream over WebSocket/SSE.

- **207 Multi-Status** — HTTP status signaling that a **bulk** operation had mixed per-item results (some succeeded, some failed); the body carries each item's individual status.

- **CSRF (with cookies)** — Because browsers auto-attach cookies, cookie auth needs Cross-Site Request Forgery protection (anti-CSRF token and/or `SameSite` cookie attribute); Bearer-header auth is not exposed to CSRF since the header isn't sent automatically.
