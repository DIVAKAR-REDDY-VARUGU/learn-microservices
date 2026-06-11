# GraphQL — From Zero to Pro (a real lesson)

> **Who this is for:** You write Node/NestJS backends. You know REST. You've *heard* GraphQL terms but they don't click yet. By the end of this, they will — well enough to defend them in a senior interview at a real-time gaming company.
>
> **The one analogy we use the whole way through:** a **RESTAURANT with an à-la-carte menu**.
> - The **customer** = the client (your frontend, mobile app, another service).
> - The **menu** = the **schema** (the list of everything you're allowed to order).
> - The **order** = the **query** (exactly the dishes/fields the customer wants).
> - The **kitchen stations** = the **resolvers** (each one prepares one dish/field).
> - The **waiter / single front desk** = the **single GraphQL endpoint**.
>
> Keep that picture in your head. Every concept below maps onto it.
>
> **One canonical dataset we reuse everywhere:** Player **42** is **"Zara"**, rank **"Diamond"**, with exactly **two** recent matches — **score 1820 on map "Dust"** and **score 1760 on map "Nuke"**. Whenever you see Player 42, it's always this same data, so you can read every query↔response pair line-by-line and trust the values never silently change.

---

## Table of contents

1. [The problem GraphQL solves](#1-the-problem-graphql-solves)
2. [What GraphQL actually is](#2-what-graphql-actually-is)
3. [Schema & types (SDL)](#3-schema--types-sdl)
4. [The Query](#4-the-query)
5. [The Resolver](#5-the-resolver)
6. [The resolver chain (execution)](#6-the-resolver-chain-execution)
7. [Arguments & variables](#7-arguments--variables)
8. [Mutations](#8-mutations)
9. [Subscriptions (real-time)](#9-subscriptions-real-time)
10. [The N+1 problem & DataLoader](#10-the-n1-problem--dataloader)
11. [GraphQL vs REST](#11-graphql-vs-rest)
12. [Pagination](#12-pagination)
13. [Error handling](#13-error-handling)
14. [Auth in resolvers](#14-auth-in-resolvers)
15. [Apollo Federation (pro)](#15-apollo-federation-pro)
16. [GraphQL in NestJS](#16-graphql-in-nestjs)
17. [Putting it all together](#17-putting-it-all-together)
18. [Glossary](#18-glossary)
19. [Interview "say-this" one-liners](#19-interview-say-this-one-liners)

---

## 1. The problem GraphQL solves

**Plain definition:** GraphQL exists to fix three pains you hit with REST APIs: **over-fetching**, **under-fetching**, and **versioning churn**.

Let's define each in plain words.

- **Over-fetching** = the server gives you **more data than you asked for**. You wanted a player's name; you got their name, email, address, settings, avatar URL, and 20 other fields.
- **Under-fetching** = one endpoint **doesn't give you enough**, so you have to make **several more requests** (round-trips) to assemble what you need.
- **Versioning churn** = every time the data shape changes, REST teams spin up `/v2`, `/v3` endpoints, and old clients break or get frozen.

### The menu analogy

Imagine a restaurant with **no à-la-carte menu** — only **fixed combo platters**.

- You want just the grilled fish. But the "Seafood Platter" combo brings fish **plus** fries, slaw, soup, and bread. You eat the fish, throw the rest away. → **over-fetching**.
- You want fish **and** a specific dessert that's on a *different* combo. Now you order two combos and wait twice. → **under-fetching** (multiple round-trips).
- The chef changes the recipe, so they print "Platter v2" and the old menu confuses everyone. → **versioning churn**.

GraphQL turns the kitchen into **à la carte**: you order *exactly* the dishes you want, on *one* order ticket.

### Before/after: a gaming example

You're building a match-summary screen. You need: the player's **username**, their **current rank**, and the **scores and maps of their two recent matches** (our canonical Player 42 dataset).

**REST (the painful way):**

```
CLIENT                                   SERVER
  |                                         |
  |  GET /players/42  ─────────────────────►|   round-trip #1
  |◄──────────── { id, username, email,     |   (over-fetch: email, country,
  |                country, rank, avatar,    |    avatar, createdAt... unwanted)
  |                createdAt, settings... }  |
  |                                         |
  |  GET /players/42/matches  ─────────────►|   round-trip #2
  |◄──────────── [ {matchId:101},           |   (under-fetch: THIS particular API
  |                {matchId:102} ]           |    was designed to return only match
  |                                         |    REFERENCES/IDs, forcing follow-ups.
  |                                         |    That's a design choice, not a rule
  |                                         |    of REST — but it's very common.)
  |  GET /matches/101 ─────────────────────►|   round-trip #3
  |  GET /matches/102 ─────────────────────►|   round-trip #4
  |                                         |
  v                                         v
  4 requests. Tons of unused fields. Slow on mobile/high-latency networks.
```

> **Note:** "only IDs came back" above is **one common API design** (the list endpoint returns references, and you fetch each item separately) — it is **not** an inherent rule of REST. Some REST APIs embed full objects. The point is that a fixed server-decided shape often forces extra round-trips.

**GraphQL (the à-la-carte way):**

```
CLIENT                                   SERVER
  |                                         |
  |  POST /graphql                          |
  |  query {                                |
  |    player(id: "42") {                   |   ONE round-trip
  |      username                           |   exactly the fields asked for
  |      rank                               |   nested data in one shot
  |      matches {                          |
  |        score                            |
  |        map                              |
  |      }                                  |
  |    }                                    |
  |  } ───────────────────────────────────► |
  |◄──── { "data": { "player": {            |
  |        "username":"Zara",               |
  |        "rank":"Diamond",                |
  |        "matches":[                       |
  |          {"score":1820,"map":"Dust"},   |
  |          {"score":1760,"map":"Nuke"}]}}}|
  v                                         v
  1 request. No wasted bytes. No /v2 needed when fields are added.
```

> **Style note (we'll explain this fully in section 4):** real apps wrap a read in the word `query { ... }`. We show it that way here from the start so your very first example already follows the convention. You *can* omit `query` for a plain read, but writing it is clearer — more on that in section 4.

**Why it matters / where interviewers probe:** They want to hear the words **over-fetching** and **under-fetching**, and that GraphQL collapses many round-trips into one request returning **only the requested fields**. Bonus points: mention that on a **mobile / laggy gaming network**, fewer round-trips = lower latency.

---

## 2. What GraphQL actually is

**Plain definition:** GraphQL (the name = "**Graph** **Q**uery **L**anguage") is **a query language for your API** *plus* a **runtime**. By **runtime** we mean: **server-side code that takes an incoming query, validates it against the schema, runs the resolvers, and assembles the JSON response.** (Don't confuse this with a programming-language runtime like the Node.js runtime — here "runtime" just means "the GraphQL engine that executes queries.") The client sends a text query describing the exact shape of the data it wants; the server returns JSON in that same shape.

Two key facts that make it different from REST:

1. **One single endpoint.** Usually `POST /graphql`. There is no `/players`, `/matches`, `/odds` — just one door. *What* you get is decided by the **body** of the request, not the URL.
2. **The client picks the fields.** The server says "here is everything you *could* ask for" (the schema/menu). The client says "I'll take exactly these."

> **Note on "graph":** Your data is modeled as a **graph** — **nodes** (a Player, a Match) connected by **edges** (a Player *played* Matches). A "node" is just a thing; an "edge" is just a relationship/connection between two things. A query is a path you walk through that graph. You don't need graph theory; just know the name comes from "data is connected like a graph." (Heads-up: in section 12 the word "Edge" gets a *second, more specific* meaning inside the Relay pagination spec. We'll flag it clearly there — it's a related but distinct idea.)

### The menu analogy

```
        REST                                 GRAPHQL
  ┌───────────────────┐               ┌───────────────────────┐
  │ Many fixed combos │               │  ONE à-la-carte menu  │
  │ each its own door │               │  ONE front desk       │
  ├───────────────────┤               ├───────────────────────┤
  │ /players          │               │                       │
  │ /players/:id      │               │     POST /graphql      │
  │ /matches          │   ───────►    │                       │
  │ /matches/:id      │               │  body decides WHAT     │
  │ /odds             │               │  you get               │
  │ /odds/live        │               │                       │
  └───────────────────┘               └───────────────────────┘
   URL = what you get                  Query body = what you get
```

### The two halves of the request/response

```
   REQUEST (what client sends)            RESPONSE (what server returns)
   ───────────────────────────           ───────────────────────────────
   query {                               {
     player(id: "42") {                    "data": {
       username                              "player": {
       rank                                    "username": "Zara",
     }                                          "rank": "Diamond"
   }                                          }
                                            }
                                          }
```

Notice: **the request looks like the response with the values removed.** That symmetry is the whole "aha."

> **Why `"42"` is quoted:** in section 3 you'll learn the `ID` scalar is text. We quote it here to stay consistent. (GraphQL *would* also accept `42` unquoted and coerce it, but since we treat IDs as strings everywhere, we always quote them.)

**Why it matters / where interviewers probe:** "One endpoint, client-specified fields, typed by a schema." If you can say that sentence, you've defined GraphQL. They may ask "is GraphQL a database?" — **No.** It's an API layer. Your data can still live in MongoDB, Redis, other microservices, anything.

> **What is "Apollo"?** You'll see the name **Apollo** several times below. **Apollo is the most popular GraphQL server/client implementation for JavaScript** — a set of libraries that implement the GraphQL spec for you. GraphQL is the *standard*; Apollo is one widely-used *product* built on that standard (NestJS's GraphQL support is built on Apollo by default).

---

## 3. Schema & types (SDL)

**Plain definition:** The **schema** is the **typed contract** of your API — the full list of what data exists and what shape it has. You write it in **SDL** = **S**chema **D**efinition **L**anguage (GraphQL's small language for declaring types). Think TypeScript interfaces, but for your API's public surface.

### The menu analogy

The schema **is the menu**. It lists every dish (type), every detail of each dish (field), and what kind of thing each detail is (a number, text, yes/no, etc.). If it's not on the menu, the customer **cannot order it** — the server rejects unknown fields *before* running anything.

### Building blocks

- **Type** (also "object type") = a kind of thing in your domain. e.g. `Player`, `Match`, `Team`. (Menu: a dish category.)
- **Field** = a named piece of data on a type. e.g. `username` on `Player`. (Menu: a detail of a dish.)
- **Scalar** = a "leaf" value that holds an actual primitive, not a nested object. GraphQL's **5 built-in spec scalars**:

```
  ┌─────────┬───────────────────────────────────────────────┐
  │ Scalar  │ Meaning                                        │
  ├─────────┼───────────────────────────────────────────────┤
  │ Int     │ whole number, e.g. 1820 (a match score)        │
  │ Float   │ decimal number, e.g. 2.75 (live betting odds)  │
  │ String  │ text, e.g. "Zara"                              │
  │ Boolean │ true / false, e.g. isOnline                    │
  │ ID      │ a unique identifier (a key). Serialized as a   │
  │         │ String on OUTPUT, but on INPUT it accepts both │
  │         │ an int literal (42) and a string ("42") and    │
  │         │ coerces it to a string. We always quote it.    │
  └─────────┴───────────────────────────────────────────────┘
```

(You can also define **custom scalars** like `DateTime`; libraries add their own, but the 5 above are the spec built-ins. Interview nugget: **`ID` is serialized as a String, yet accepts both `Int` and `String` as input.**)

- **The `!` (non-null marker)** = "this field is guaranteed to have a value, never null." `username: String!` means always present. No `!` means nullable (may be null).

- **Lists and the confusing `[Match!]!` syntax.** This is the single most-confusing bit of SDL, so here's a labeled breakdown. There are **two** independent `!` positions — one *inside* the brackets (about each item) and one *outside* (about the list itself):

```
                 [ Match ! ] !
                   │      │   │
                   │      │   └── OUTER ! : the LIST itself is non-null
                   │      │             (you always get a list, never null)
                   │      └────────── INNER ! : each ITEM is non-null
                   │                            (no null elements inside)
                   └───────────────── the kind of item in the list (Match)

  ┌────────────┬──────────────────────────────────────────────────────────┐
  │ Written as │ Meaning                                                   │
  ├────────────┼──────────────────────────────────────────────────────────┤
  │ [Match]    │ list may be null; items may be null    (loosest)          │
  │ [Match]!   │ list always present; but items may be null                │
  │ [Match!]   │ list may be null; but if present, no null items           │
  │ [Match!]!  │ list always present AND no null items   (strictest)       │
  └────────────┴──────────────────────────────────────────────────────────┘
```

So `matches: [Match!]!` = "you will always get a list, and every element in it is a real `Match` (never null)."

### A small schema (gaming flavored)

We add a **`Team`** type here (and a `team` field on `Player`) **on purpose** — section 10's N+1 example uses it, so it must be on the menu *before* we get there.

```graphql
# This is SDL — the menu, written down.

type Team {
  id: ID!
  name: String!
}

type Player {
  id: ID!                 # ID, guaranteed present
  username: String!       # text, always there
  rank: String!
  isOnline: Boolean!
  winRate: Float!         # e.g. 0.62
  team: Team!             # which team this player belongs to (used in section 10)
  matches: [Match!]!      # a non-null list of non-null Match objects (an edge/connection)
}

type Match {
  id: ID!
  score: Int!
  map: String!
  player: Player!         # back-reference: the graph is bidirectional
}

# Every schema has special ROOT types — the entry doors:
type Query {
  player(id: ID!): Player          # "player" takes an ID arg, returns a Player
  topPlayers(limit: Int!): [Player!]!
}
```

### Visual: the menu as a typed graph

```
   type Query  (the front desk — entry points)
      │
      ├── player(id) ──────────►  type Player
      │                               │ id: ID!
      │                               │ username: String!
      │                               │ rank: String!
      │                               │ winRate: Float!
      │                               │ team: Team! ───────► type Team { id, name }
      │                               └ matches: [Match!]! ──► type Match
      │                                                          │ id: ID!
      └── topPlayers(limit) ──► [Player!]!                       │ score: Int!
                                                                 │ map: String!
                                                                 └ player ──► (back to Player)
```

**Why it matters / where interviewers probe:** The schema is the **single source of truth** and a **contract**. Because it's typed and **introspectable** — meaning *you can query the schema to ask it about its own types and fields* — tools auto-generate docs and client types from it. Interviewers love: *"the schema is a strongly-typed contract; the server validates every query against it before executing, so malformed/unknown-field requests fail fast."*

---

## 4. The Query

**Plain definition:** A **query** is a **read request** — the customer's **order ticket**. It names the fields it wants, and (crucially) **its shape mirrors the response shape**.

### The menu analogy

The query is the **order you hand the waiter**: "I'll have the player with id 42 — just the username and rank, and for their matches just the score and map." You list dishes and sub-details; the kitchen returns exactly those, plated in the same structure.

### Request shape ⇄ response shape (side by side)

This is our canonical Player 42, with exactly the two canonical matches:

```
   QUERY (request)                       RESULT (response)
   ──────────────────                    ─────────────────────────────
   query {                               {
     player(id: "42") {                    "data": {
       username                              "player": {
       rank                                    "username": "Zara",
       matches {                               "rank": "Diamond",
         score                                 "matches": [
         map                                      { "score": 1820, "map": "Dust" },
       }                                          { "score": 1760, "map": "Nuke" }
     }                                          ]
   }                                          }
                                            }
                                          }
```

Read the two columns line by line. The query is a **skeleton**; the response is the same skeleton **with data poured in**. Nesting in the query (`matches { score map }`) produces nesting in the JSON.

A few mechanics:

- The outer `query { ... }` keyword **can be omitted** for a plain read (it's the default operation), but **write it explicitly** — it's clearer, and it's required anyway once you name the operation or use variables (section 7).
- Everything inside braces is a **selection set** = "the set of fields I'm selecting here."
- The `id` argument is written as `"42"` (a quoted string) because `ID` is text — consistent with section 3 and the variables you'll send in section 7.
- You **must drill down to scalars.** You cannot just ask for `matches` and stop — `matches` is an object list, so you must say *which fields* of each match (`score`, `map`). Asking for an object without selecting sub-fields is an error. (Menu: you can't order "a platter" — you must specify what's on it.)

**Why it matters / where interviewers probe:** "Request shape mirrors response shape" and "client selects exactly the fields" are the lines they want. They may ask why you can't stop at an object field — answer: GraphQL needs to reach **scalar leaves** to know what to actually return.

---

## 5. The Resolver

**Plain definition:** A **resolver** is a **function on the server that produces the value for one field**. Every field in the schema is backed by a resolver — either one you write explicitly, or a **default resolver** GraphQL supplies for free.

> **What is a "default resolver"?** If you *don't* write a resolver for a field, GraphQL uses a **default resolver** that simply reads the property of the **same name** off the `parent` object (`parent[fieldName]`). If that property happens to be a function, it calls it. That's why simple fields like `username` need **no code** — the default resolver reads `parent.username` automatically. In NestJS code-first (section 16), plain `@Field()` properties rely on exactly this default and have no resolver method of their own.

### The menu analogy

A resolver is a **kitchen station**. Each station knows how to prepare **one dish/field**. The `username` station reads it off the player record (the default resolver does this for free). The `matches` station goes to the pantry (database) and fetches matches. The order ticket routes each requested dish to its station.

### The resolver signature (the 4 arguments) — explained simply

In JavaScript (and Apollo — the popular JS GraphQL implementation from section 2), a resolver looks like:

```js
fieldName(parent, args, context, info) { ... }
```

```
  ┌──────────┬──────────────────────────────────────────────────────────┐
  │ Argument │ Plain meaning (menu analogy)                              │
  ├──────────┼──────────────────────────────────────────────────────────┤
  │ parent   │ The result of the PARENT field — the dish this one sits   │
  │ (a.k.a.  │ on. For Player.username, parent is the Player object.     │
  │  root /  │ (The half-plated dish handed from the previous station.)  │
  │  source) │ When a field is unused, it's often written `_` by         │
  │          │ convention — that underscore is still this `parent` slot. │
  ├──────────┼──────────────────────────────────────────────────────────┤
  │ args     │ The arguments passed to THIS field, e.g. id: "42" or      │
  │          │ limit: 3. (The customer's special instructions.)          │
  ├──────────┼──────────────────────────────────────────────────────────┤
  │ context  │ A shared object built ONCE per request: the logged-in     │
  │          │ user, DB connections, DataLoaders (a batching helper      │
  │          │ we'll define fully in section 10), a Redis client. The    │
  │          │ SAME object for every resolver in this request. (The      │
  │          │ shared kitchen pass: tickets, tools, who-the-customer-is.)│
  ├──────────┼──────────────────────────────────────────────────────────┤
  │ info     │ Metadata about the query itself: which fields were asked, │
  │          │ the path, and the AST (Abstract Syntax Tree — the parsed, │
  │          │ tree-structured form of the query the server works with   │
  │          │ internally). Rarely used; advanced. (The full order       │
  │          │ ticket, for inspection.)                                  │
  └──────────┴──────────────────────────────────────────────────────────┘
```

### Tiny example

```js
const resolvers = {
  Query: {
    // parent is unused at the root, so we name it `_` by convention;
    // args.id came from the query (e.g. "42")
    player: (_, args, context) =>
      context.db.players.findById(args.id),
  },
  Player: {
    // 'username' usually needs NO resolver — the default resolver reads
    // parent.username for you. Shown here only to illustrate what the
    // default effectively does:
    username: (parent) => parent.username,          // trivial; usually auto
    matches: (parent, args, context) =>
      context.db.matches.findByPlayerId(parent.id), // uses parent.id!
  },
};
```

**Why it matters / where interviewers probe:** Senior bait: "Walk me through a resolver's arguments." Nail `parent`, `args`, `context`, `info`. Emphasize that **`context` is per-request shared state** — that's exactly where the auth user and **DataLoaders** live (section 10).

---

## 6. The resolver chain (execution)

**Plain definition:** To answer a query, the server **resolves fields one at a time, parent → child**. The value a parent resolver returns becomes the `parent` argument of its child resolvers. This is the **resolver chain** (or execution).

### The menu analogy

The order ticket flows down the line of stations. Station 1 (`player`) plates the base. It hands that half-finished plate to station 2 (`username`) and station 3 (`matches`). Station 3 in turn hands each match to station 4 (`score`, `map`). Each station only finishes **its** part, then passes the plate on.

### Visual: resolving the canonical match-summary query

```
QUERY:
  query { player(id:"42") { rank   matches { score  map } } }

EXECUTION ORDER (parent → child).
Note: we write the unused root parent as `_` by convention (it's the same
`parent` slot you met in section 5):

  ┌─ Query.player(_, {id:"42"}, ctx) ──────────► returns Player{ id:42, ... }
  │        │ this return value becomes `parent` below
  │        ▼
  ├─ Player.rank(parent=Player42) ─────────────► "Diamond"
  │
  └─ Player.matches(parent=Player42, ctx) ─────► [Match101, Match102]
            │ each Match becomes `parent` for its children
            ├─ Match.score(parent=Match101) ───► 1820
            ├─ Match.map(parent=Match101)   ───► "Dust"
            ├─ Match.score(parent=Match102) ───► 1760
            └─ Match.map(parent=Match102)   ───► "Nuke"

The tree of resolvers mirrors the tree of fields in the query.
```

Two important properties — stated carefully, because interviewers test the nuance:

- **Sibling fields run concurrently (not truly in parallel).** In the diagram above, `rank` and `matches` are siblings. The JavaScript reference engine (`graphql-js`) calls each resolver **in field order**, within a single pass, on **one thread**. If a resolver returns a Promise (async I/O like a DB call), the engine moves on and lets that async work **overlap** with its siblings' async work. So `rank` and `matches` can have their database calls **in flight at the same time** — that's *cooperative concurrency on the single-threaded event loop*, **not** true multi-core parallelism, and the resolvers are still *invoked* in order. (Word of caution: don't claim "parallel" flatly in an interview — say "concurrent async, single-threaded.")
- **A field's children wait for that field to resolve first.** `score`/`map` cannot run until `matches` has produced its list — children literally need the parent value. ("Where possible" earlier just means: the overlap only happens for the async parts, and how much overlaps depends on the server implementation and on whether resolvers actually do async I/O.)
- **The list multiplies the work.** `matches` returned 2 items, so `score`/`map` run **once per item**. Hold that thought — it's the seed of the N+1 problem (section 10).

**Why it matters / where interviewers probe:** "The query is a tree; resolvers form a matching tree; parent results flow into children." Knowing this is the *only* way to truly understand N+1 and DataLoader.

---

## 7. Arguments & variables

**Plain definition:**
- **Arguments** = inputs to a field, written **in the query**, e.g. `player(id: "42")` or `lastMatches(limit: 3)`.
- **Variables** = named placeholders (e.g. `$id`) so the query text stays constant and the **values** are passed separately. This is the safe, reusable, cache-friendly way.

### The menu analogy

- An **argument** is a **special instruction on the order**: "the fish, *grilled, no salt*." (`no salt` = an arg.)
- A **variable** is a **fill-in-the-blank order form**: the form text never changes ("Player number ___, give me last ___ matches"); you just hand over the blanks. The kitchen reuses the same form template every time.

### Hardcoded args vs variables (side by side)

```
   HARDCODED (fine for demos)            WITH VARIABLES (do this in apps)
   ───────────────────────────          ─────────────────────────────────
   query {                              query GetPlayer($id: ID!, $n: Int!) {
     player(id: "42") {                   player(id: $id) {
       lastMatches(limit: 3) {              lastMatches(limit: $n) {
         score                                score
       }                                    }
     }                                    }
   }                                    }

                                        VARIABLES (sent as separate JSON):
                                        { "id": "42", "n": 3 }
```

Why variables win:

- **No string-building** → no injection-style bugs, and types are checked (`$id: ID!`).
- **Cacheable**: the query *document* is identical across calls; only variables change. Servers and clients can key caches on it.
- **Reusable**: one `GetPlayer` operation, many inputs.

```
HTTP body actually sent to POST /graphql:
{
  "query": "query GetPlayer($id: ID!, $n: Int!) { player(id:$id){ lastMatches(limit:$n){ score } } }",
  "variables": { "id": "42", "n": 3 }
}
```

**Why it matters / where interviewers probe:** "Always use variables, not string interpolation." Mention they're **typed and validated** against the schema, and keep the **operation cacheable**.

---

## 8. Mutations

**Plain definition:** A **mutation** is a **write request** — it **changes** data (create / update / delete). Queries read; mutations write. They use the same syntax but the keyword is `mutation`, and they live under the special root type `Mutation`.

> **Why separate from queries at all?** Two reasons.
> **(1) Intent clarity** — anyone reading the operation instantly knows it has side effects.
> **(2) Execution rule** — **top-level mutation fields are awaited strictly one after another (serially)**, *not* overlapped like query fields.
>
> Why does that matter? Running things **at the same time (concurrently)** is **safe for reads** because reads don't change anything — two readers can't corrupt each other. But two **writes** happening at once could **interfere** ("race each other" = two operations touch the same data and the final result depends on unpredictable timing). To avoid that, GraphQL runs top-level mutations **one-by-one, in order**.

### The menu analogy

A **query** is *ordering food to eat* (read the kitchen's output). A **mutation** is *asking the kitchen to actually change the pantry* — "add this new dish to inventory," "throw out that batch," "update the recipe." It changes the restaurant's state, not just your plate. And the restaurant processes such change-requests **one at a time, in order**, to avoid chaos.

### Schema + example: submitting a match result

```graphql
# Inputs to mutations often use a special "input" type (a form to fill in)
input SubmitMatchInput {
  playerId: ID!
  score: Int!
  map: String!
}

type Mutation {
  submitMatch(input: SubmitMatchInput!): Match!   # returns the created Match
  updateRank(playerId: ID!, rank: String!): Player!
  deleteMatch(id: ID!): Boolean!
}
```

```
   MUTATION (request)                    RESPONSE
   ─────────────────────────────         ──────────────────────────────
   mutation Submit($in: SubmitMatchInput!) {
     submitMatch(input: $in) {           {
       id                                  "data": {
       score                                 "submitMatch": {
       player { username }                     "id": "9001",
     }                                          "score": 1820,
   }                                            "player": { "username": "Zara" }
                                              }
   variables:                              }
   { "in": { "playerId":"42",            }
            "score":1820,
            "map":"Dust" } }
```

Note a lovely property: a mutation **returns data too** — you choose which fields of the changed object to read back. One round-trip writes *and* fetches the fresh state.

**Why it matters / where interviewers probe:** Key senior nuance — **top-level mutations are awaited serially; query fields run concurrently** (overlapping async work, single-threaded — section 6). Also: use **`input` types** for write payloads, and **return the mutated object** so the client can update its cache without a refetch.

---

## 9. Subscriptions (real-time)

**Plain definition:** A **subscription** is a **long-lived connection where the server PUSHES data to the client whenever an event happens** — instead of the client repeatedly asking. It typically runs over **WebSockets** (a persistent two-way connection), not one-shot HTTP. This is the gaming bread-and-butter: live scores, live odds, lobby presence.

### The menu analogy

A query/mutation is "I order, you serve, we're done." A **subscription** is putting your name on a **standing order**: "Every time the kitchen plates a fresh batch of today's special, bring me one — automatically, all evening, until I say stop." You don't re-ask. The kitchen pushes each new plate to your table as it's ready.

### Visual: query vs subscription

```
  QUERY / MUTATION (request–response, HTTP)
  ─────────────────────────────────────────
  client ──ask──► server
  client ◄─answer─ server      (connection closes; done)


  SUBSCRIPTION (push, WebSocket — stays open)
  ─────────────────────────────────────────
  client ══subscribe══► server          (handshake, stays connected)
  client ◄═══ push #1 ═══ server   (odds changed → 2.50)
  client ◄═══ push #2 ═══ server   (goal scored → score 1-0)
  client ◄═══ push #3 ═══ server   (odds changed → 1.80)
        ...                        (continues until client unsubscribes)
```

### Schema + example: live odds for a match

```graphql
type Odds {
  matchId: ID!
  home: Float!
  away: Float!
  updatedAt: String!
}

type Subscription {
  oddsUpdated(matchId: ID!): Odds!   # push a new Odds every time it changes
}
```

```
   SUBSCRIPTION (client opens once)      PUSHED MESSAGES (server, repeatedly)
   ──────────────────────────────       ────────────────────────────────────
   subscription($m: ID!) {              { "data": { "oddsUpdated":
     oddsUpdated(matchId: $m) {             { "home": 2.50, "away": 1.55 }}}
       home                              ─────────────────────────────────
       away                             { "data": { "oddsUpdated":
     }                                      { "home": 1.80, "away": 2.10 }}}
   }                                     ─────────────────────────────────
                                        { "data": { "oddsUpdated":
   variables: { "m": "777" }                { "home": 3.10, "away": 1.40 }}}
```

### How it works under the hood

First, three plain-words definitions, because this is where distributed-systems jargon usually ambushes beginners:

- **Publish/subscribe (PubSub):** a messaging pattern. **Publishers** emit events to a **named channel** (e.g. the channel `"odds:777"`). Anyone who **subscribed** to that channel receives every event published to it. Publishers and subscribers don't know about each other — they only share the channel name.
- **Fan-out:** delivering **one** published event to **many** listeners at once (one plate → every table that ordered it).
- **Horizontal scaling / multiple instances/nodes:** running **several identical copies of your server** behind a **load balancer** (a traffic cop that spreads incoming connections across the copies). Copy A, B, and C each handle some of the connected clients. (We do this so one machine isn't overwhelmed.)

Now the mechanism: when something changes (e.g. the odds engine computes a new price), it **publishes** an event, and the GraphQL server's **PubSub** mechanism **fans it out** to every client subscribed to that match.

The catch: a **plain in-memory PubSub only reaches clients connected to the same server copy**. If you run multiple copies (horizontal scaling), an event published on copy A won't reach a subscriber on copy B. The fix is to back PubSub with **Redis** (an in-memory data store that *can* broadcast across machines) — a `RedisPubSub`. Now any copy that publishes reaches subscribers on *every* copy.

```
   odds engine ──publish("odds:777")──► [ Redis Pub/Sub ]
                                              │  fan-out
                  ┌───────────────────────────┼───────────────────────────┐
                  ▼                            ▼                            ▼
            server copy A                server copy B                server copy C
              │  │  │                       │   │                        │
            ws ws ws                       ws  ws                       ws
          (subscribed clients watching match 777 across all copies)
```

> **Analogy bridge (so we don't abandon the restaurant):** think of each server copy as a **separate kitchen** in a chain of restaurants. **Redis is the one shared central order-board** all kitchens watch. A standing order placed at *any* kitchen still receives every new plate, because every kitchen posts new plates to — and reads them from — the same central board. (Beyond this bridge, the Redis/load-balancer layer is pure infrastructure with no deeper menu counterpart — that's fine; it's the "pro" plumbing the interviewer wants to hear you name.)

**Why it matters / where interviewers probe:** For a real-time gaming role this is *the* topic. Be ready to say: subscriptions = **server push over WebSockets**, backed by a **PubSub** that you scale **horizontally with Redis** so events reach clients connected to *any* copy. If they push deeper, you can mention **auth-on-connect** (verify the user when the socket opens) and **backpressure** — *backpressure is what you do when the server produces events faster than a slow client can consume them; you buffer, drop, or slow down so the client isn't flooded.*

---

## 10. The N+1 problem & DataLoader

**This is the #1 senior GraphQL topic. Slow down here.**

**Plain definition of N+1:** When resolving a **list of N parents**, a naive child resolver runs **one database call per item** — so you do **1** query to get the list **plus N** queries for the children = **N+1** total. It silently murders performance as N grows.

We'll use a field already on our menu: **`Player.team`** (added in section 3), and the `topPlayers` query. This is exactly the situation section 6 foreshadowed ("the list multiplies the work").

### The menu analogy

A waiter takes a table of 50 guests. For *each* guest he walks to the kitchen, asks "what's this one guest's side dish?", walks back — **50 separate trips** plus the 1 trip to get the guest list. Insane. The fix: **collect all 50 requests, make ONE trip**, ask the kitchen for all 50 sides at once. That single batched trip is what **DataLoader** does.

### Visual: the problem

Query: "get the top 50 players, and for each, their team name." (`Player.team`)

```
  Query.topPlayers ──► [P1, P2, P3, ... P50]          (1 query)
        │
        ├─ Player.team(P1) ─► find team WHERE id = P1.teamId   (+1)
        ├─ Player.team(P2) ─► find team WHERE id = P2.teamId   (+1)
        ├─ Player.team(P3) ─► find team WHERE id = P3.teamId   (+1)
        │                  ...
        └─ Player.team(P50)─► find team WHERE id = P50.teamId  (+1)

  TOTAL = 1 + 50 = 51 database round-trips   ← N+1 (here N=50)
```

### DataLoader: the fix

**Plain definition:** **DataLoader** is a per-request helper that does two things:

1. **Batching** — it collects all the individual `load(key)` calls your resolvers make during the **current frame of execution**, then calls **your batch function once** with **all the keys** (`[teamId1, ...teamId50]`), which you turn into **one** DB/Redis query.

> **"Current frame of execution / tick" in plain words:** Node.js runs your synchronous code in one continuous pass before it pauses to wait on I/O. DataLoader gathers **every `load()` call made during that pass** and then fires the batch **once on the next "tick"** (`process.nextTick` by default — i.e. the very next moment, before any awaited database call actually goes out). Analogy: **the waiter waits until everyone at the table has finished speaking, then walks to the kitchen once.** (For the curious: the scheduling is configurable via `batchScheduleFn`, but the default next-tick behavior is what you'll use 99% of the time.)

2. **Caching (per request)** — if the same key is loaded twice in one request, it's fetched once and memoized (remembered). The cache is **scoped to a single request** (it lives in `context`) so you never serve stale data across requests.

```
  WITHOUT DataLoader            WITH DataLoader
  ──────────────────           ────────────────────────────────────────
  team(P1)  → DB                team(P1) → loader.load(teamId1) ┐
  team(P2)  → DB                team(P2) → loader.load(teamId2) │ collected in
  team(P3)  → DB                team(P3) → loader.load(teamId3) │ the current
   ...                           ...                            │ frame, fired
  team(P50) → DB                team(P50)→ loader.load(teamId50)┘ on next tick
                                                  │
  51 round-trips                ONE batched call: teams WHERE id IN (t1..t50)
                                TOTAL = 1 + 1 = 2 round-trips   ✔
```

> **What does `IN (...)` / `$in` mean?** Both are database ways to say **"where the id is any one of these values"**, fetching all matching rows in a **single** query. SQL writes it `WHERE id IN (1,2,3)`; MongoDB writes it `{ _id: { $in: [1,2,3] } }`. That single multi-key lookup is the whole point of batching.

### Tiny code (concept)

```js
// Built ONCE per request, stored in context:
const teamLoader = new DataLoader(async (teamIds) => {
  // ONE query for ALL ids ($in = "match any id in this list"):
  const teams = await db.teams.find({ _id: { $in: teamIds } });

  // MUST return results in the SAME ORDER as the input ids:
  const byId = new Map(teams.map(t => [String(t._id), t]));
  // `?? null` means: if get() returns undefined (team not found), use null instead.
  return teamIds.map(id => byId.get(String(id)) ?? null);
});

// Resolver just asks the loader — no idea batching is happening:
const resolvers = {
  Player: {
    team: (player, _args, ctx) => ctx.teamLoader.load(player.teamId),
  },
};
```

Two non-negotiable rules to state in an interview:

- The batch function **must return an array the same length and in the same order as the input keys** (map each key to its result, or `null`). DataLoader matches results to callers by **position**.
- A DataLoader is **created per request** (in `context`), **not** shared globally — otherwise its per-request cache would leak data between users/requests.

**Why it matters / where interviewers probe:** Expect "What is the N+1 problem and how do you solve it?" Crisp answer: *"A nested field over a list of N parents fires N separate fetches. I add a per-request DataLoader that batches all those keys into one `IN (...)` / `$in` query and caches duplicates within the request."* For a Mongo/Redis shop, mention batching with `$in` and that the loader cache is per-request to avoid stale reads.

---

## 11. GraphQL vs REST

**Plain definition:** GraphQL isn't strictly "better" — it's a **trade-off**. Use the right tool per situation. (If you say "GraphQL always wins," seniors will dock you.)

### The menu analogy

À la carte (GraphQL) is amazing when **diners want very different, custom plates** and hate waste. A **fixed combo counter** (REST) is faster and simpler when **everyone orders the same thing** and you want dead-simple caching and a line that moves fast.

### Fair comparison table

> **First, two terms used in the table:**
> - **CDN = Content Delivery Network**: a fleet of edge servers spread around the world that **cache** responses close to users so repeat requests are served instantly without hitting your origin server. A CDN **keys its cache on the URL** (plus method/headers).
> - That's exactly why GraphQL is harder to CDN-cache: **every GraphQL call is a `POST` to the *same* URL (`/graphql`) with the real request in the body**, so a URL-keyed cache can't tell two different queries apart. REST's `GET /players/42` has a unique, cacheable URL.

```
  ┌──────────────────────┬───────────────────────────┬───────────────────────────┐
  │ Concern              │ GraphQL                    │ REST                       │
  ├──────────────────────┼───────────────────────────┼───────────────────────────┤
  │ Endpoints            │ one (/graphql)             │ many (/players, /matches)  │
  │ Fetching shape       │ client picks exact fields  │ server fixes the response  │
  │ Over/under-fetching  │ solved by design           │ common; needs custom routes│
  │ Round-trips          │ one for nested data        │ often several              │
  │ HTTP caching (CDN)   │ HARDER (POST, one URL,      │ EASY (GET + unique URL +   │
  │                      │ query in body)             │ headers)                   │
  │ File uploads/binary  │ awkward                     │ natural                    │
  │ Versioning           │ evolve schema, deprecate    │ /v2, /v3 endpoints         │
  │ Learning curve / ops │ higher (schema, N+1, tools) │ lower, ubiquitous          │
  │ Error model          │ body has errors[]; status   │ HTTP status codes          │
  │                      │ depends on media type (see  │                            │
  │                      │ section 13)                 │                            │
  │ Best when            │ many clients, varied/nested │ simple CRUD, public cache, │
  │                      │ needs, rapid UI iteration   │ binary, heavy CDN caching  │
  └──────────────────────┴───────────────────────────┴───────────────────────────┘
```

**A balanced take to say out loud:** "GraphQL shines when you have **many different clients** (web, mobile, partners) with **varied, nested data needs** and you want to iterate the UI fast without touching backend routes. REST is still great for **simple CRUD**, **binary/file** endpoints, and anywhere you lean hard on **HTTP/CDN caching** — because GraphQL's single `POST` endpoint, with the query in the body, makes URL-level caching harder."

**Why it matters / where interviewers probe:** Maturity check. Lead with trade-offs, name **caching** and **file uploads** as REST's strengths, and **client-tailored nested fetching** as GraphQL's.

---

## 12. Pagination

**Plain definition:** **Pagination** = returning a big list in **chunks (pages)** instead of all at once. Two styles: **offset-based** (skip N, take M) and **cursor-based** (give me items *after this marker*). The cursor style is standardized by the **Relay Connections** spec.

> **What is "Relay"?** **Relay is a GraphQL client library from Facebook/Meta.** It defined a pagination convention — the "Connections" shape with `edges`, `node`, and `pageInfo` — that became a **widely adopted de-facto standard**, even for people who don't use the Relay client itself.

> **Heads-up on the word "Edge":** back in section 2, an "edge" meant *any relationship between two nodes* in the data graph. Here, a Relay **`Edge`** is a **specific spec construct**: a small wrapper object that carries a `cursor` (a bookmark) plus a `node` (the real item). They're related in spirit (both connect you to a thing) but the Relay `Edge` is a precise, named type — don't conflate it with the general graph-edge idea.

### The menu analogy

The kitchen has 10,000 menu items. **Offset** = "skip the first 40, give me items 41–60" (page 3 of 20). Easy, but if someone **inserts a new dish at the top while you browse**, your "page 3" shifts and you see a duplicate or miss one. **Cursor** = "give me the 20 items **after** the bookmark I'm holding" — a stable bookmark that doesn't drift when the list changes.

### Offset vs cursor (side by side)

```
   OFFSET-BASED                          CURSOR-BASED (Relay)
   ────────────────────────              ──────────────────────────────
   query {                               query {
     leaderboard(offset: 40,               leaderboard(first: 20,
                 limit: 20) {                          after: "Y3Vyc29yOjQw") {
       username                              edges {
       score                                  cursor      # bookmark for THIS row
     }                                        node {      # the actual item
   }                                            username
                                                score
   Offset is simple, but:                     }
   - it DRIFTS on inserts/deletes           }
     (rows shift under you)                 pageInfo {
   - it's SLOW at deep offsets                hasNextPage   # is there more?
     (DB still scans/skips all               endCursor     # bookmark to pass next
      the rows it jumps over)              }
                                         }
```

> **What does "opaque cursor" mean?** An **opaque** cursor is a **coded string the client should treat as a black box** — just store it and pass it back as `after`, **never try to parse or interpret it**. The example `"Y3Vyc29yOjQw"` is a **Base64-encoded** value (Base64 is a common way to encode arbitrary data as a plain text string); it happens to decode to `cursor:40`, but the client neither knows nor cares. The server is free to change the encoding any time.

### Relay connection shape (define each part)

```
  Connection
   ├── edges: [ Edge ]          a list of wrapper objects
   │     ├── cursor             an OPAQUE bookmark string for that row
   │     └── node               the real object (the Player)
   └── pageInfo
         ├── hasNextPage        Boolean: more pages after this?
         ├── hasPreviousPage    Boolean: more before this?
         ├── startCursor        bookmark of the first edge (may be null if empty)
         └── endCursor          bookmark of the last edge  ← reuse as `after` next call
                                (may be null if the page is empty)
```

```graphql
type PlayerEdge { cursor: String!  node: Player! }

# Relay-compliant PageInfo: cursors are NULLABLE (a page can be empty),
# and BOTH hasNextPage and hasPreviousPage are required.
type PageInfo {
  hasNextPage: Boolean!
  hasPreviousPage: Boolean!
  startCursor: String        # nullable on purpose
  endCursor: String          # nullable on purpose
}

type PlayerConnection { edges: [PlayerEdge!]!  pageInfo: PageInfo! }

type Query {
  leaderboard(first: Int!, after: String): PlayerConnection!
}
```

**Why cursor wins for a live leaderboard:** scores change constantly; offset pages would jump and double-show players. A cursor (e.g., encoding "score=1820, id=42") gives a **stable, drift-resistant** position, and it's **fast** because the DB seeks directly to the cursor instead of scanning/skipping millions of rows.

**Why it matters / where interviewers probe:** For a gaming leaderboard they'll ask "how do you paginate?" Answer: **cursor-based / Relay connections** for stability under writes and performance at scale; offset is fine for small, static lists.

---

## 13. Error handling

**Plain definition:** GraphQL can return **partial data**: a single response may contain **both** a `data` object (the parts that succeeded) **and** an `errors` array (the parts that failed). The *errors live in the response body*, not (only) in the HTTP status code.

> **About the HTTP status code (a senior-level nuance):** The classic behavior — and still the default with the legacy `application/json` media type — is **HTTP 200 even when some fields errored**. But under the modern **GraphQL-over-HTTP spec** with the `application/graphql-response+json` media type (used by **Apollo Server 4+** in its default mode), the status now depends on *what* failed:
> - **Parse/validation failures** (malformed query, unknown field, wrong arg type) → a **4xx** like **400**, because the request was never executable.
> - **A well-formed request that executed** (even if individual *fields* errored at runtime) → **200**, with the failures in `errors[]`.
>
> So the safe interview line is: *"The errors array is always in the body; the status code is 200 for executed operations, but parse/validation errors return 4xx under the modern GraphQL-over-HTTP spec."*

### The menu analogy

You ordered three dishes. The kitchen burns the soup but the steak and salad are perfect. Instead of cancelling the whole table, the waiter brings the **steak and salad**, plus a note: "**sorry, the soup failed.**" You got partial service **plus** a clear explanation. That note is the `errors` array; the served dishes are `data`.

### Visual: partial success (a NULLABLE field fails)

Here `team` is **nullable** (`team: Team`, no `!`), so when it fails it simply becomes `null`:

```
  query { player(id:"42"){ username  team { name } } }     # team: Team  (nullable)

  team resolver throws (e.g. team service down)
                │
                ▼
  RESPONSE:
  {
    "data": {
      "player": {
        "username": "Zara",
        "team": null            ← the failed nullable field is just null
      }
    },
    "errors": [
      {
        "message": "Team service unavailable",
        "path": ["player", "team"],            ← exactly which field failed
        "locations": [{ "line": 1, "column": 30 }],
        "extensions": { "code": "UPSTREAM_UNAVAILABLE" }  ← machine-readable code
      }
    ]
  }
```

### Visual: null-bubbling (a NON-NULL field fails) — the senior tell

This is the harder case the prose always mentions but rarely shows. Suppose the schema says:

```graphql
type Player {
  username: String!     # NON-NULL: promised to never be null
  team: Team            # nullable
}
type Query {
  player(id: ID!): Player    # nullable Player
}
```

Now imagine **`username` itself throws** (e.g. a serialization bug). `username` is `String!` — the schema **promised it can never be null**. GraphQL is not allowed to put `null` there. So the error **bubbles UP to the nearest *nullable* ancestor**, and *that* ancestor becomes `null`:

```
   field that threw: Player.username  (String!  — non-null, can't be null)
            │  can't be null here, so error propagates upward...
            ▼
   parent: player  (Player — this is NULLABLE)  ◄── nearest nullable ancestor
            │  STOPS here: this whole object becomes null
            ▼

  BEFORE (imagined, illegal):           AFTER (actual GraphQL behavior):
  {                                      {
    "data": {                              "data": {
      "player": {                            "player": null   ◄── whole player nulled
        "username": null  ← ILLEGAL          },
        (String! can't be null)            "errors": [
      }                                      {
    }                                          "message": "Cannot serialize username",
  }                                            "path": ["player", "username"],
                                               "extensions": { "code": "INTERNAL" }
                                             }
                                           ]
                                         }
```

The non-null violation "bubbles" until it reaches something that *is* allowed to be null (`player`), which absorbs the `null`. If `player` had *also* been non-null (`player: Player!`), the null would keep bubbling up — potentially all the way to making `"data": null`.

Key points to know:

- **`data` + `errors` can coexist.** Clients should check both.
- **`path`** tells you *which field* failed. **`extensions.code`** is your machine-readable error code (e.g. `UNAUTHENTICATED`, `BAD_USER_INPUT`) — clients branch on this, not on the human message.
- **Null-bubbling:** a `null` produced for a **non-null** field propagates up to the **nearest nullable ancestor**, which becomes `null` (and if none exists, `data` itself becomes `null`).

**Why it matters / where interviewers probe:** "GraphQL returns partial `data` plus an `errors` array; use `extensions.code` for typed errors; non-null errors bubble up to the nearest nullable parent." That null-bubbling bit is a senior-level tell.

---

## 14. Auth in resolvers

**Plain definition:** Authentication ("who are you?") and authorization ("are you allowed?") in GraphQL are usually done by putting the **logged-in user into `context`** (built once per request, from a **JWT**), then **checking it inside resolvers** — at the **operation level** (whole query/mutation) or even the **field level** (one sensitive field).

> **What is a JWT?** **JWT = JSON Web Token** — a **signed token the client sends (usually in the `Authorization: Bearer <token>` HTTP header) to prove who it is.** The server verifies the signature (so it knows the token wasn't forged) and reads the user info inside it. No DB lookup needed just to identify the caller.

### The menu analogy

The waiter checks your **membership card at the door** and clips it to your order ticket (that's `context`). Then each **kitchen station** can glance at the card before serving: the regular dishes go to anyone; the **VIP-only dish** (a sensitive field) is served **only if the card says VIP**. Same meal, per-dish access rules.

### How context is built, then checked

```
  REQUEST ──► [ build context once ]
                  verify JWT → { user: { id, roles } }  (or null if anonymous)
                         │
                         ▼ (same object handed to every resolver)
   ┌──────────────────────────────────────────────────────────────┐
   │ Query.player        → needs a logged-in user (operation-level) │
   │ Player.email        → needs role ADMIN     (FIELD-level)       │
   │ Mutation.banPlayer  → needs role MODERATOR (operation-level)   │
   └──────────────────────────────────────────────────────────────┘
```

```js
// context factory (runs once per request): pull the JWT from the header,
// verify it, and stash the resulting user on context.
context: ({ req }) => ({ user: verifyJwt(req.headers.authorization) });

const resolvers = {
  Query: {
    me: (_p, _a, ctx) => {
      if (!ctx.user) throw new GraphQLError('Not authenticated',
        { extensions: { code: 'UNAUTHENTICATED' } });
      return ctx.user;
    },
  },
  Player: {
    // FIELD-level: only admins see another player's email.
    // `ctx.user?.roles` uses optional chaining (?.): if ctx.user is null/undefined,
    // the whole expression is undefined instead of throwing.
    email: (player, _a, ctx) => {
      if (!ctx.user?.roles.includes('ADMIN'))
        throw new GraphQLError('Forbidden', { extensions: { code: 'FORBIDDEN' } });
      return player.email;
    },
  },
};
```

In NestJS you'd express the same thing with **Guards**.

> **What is a Guard (NestJS)?** A **Guard is a Nest class that runs *before* a resolver and decides whether to allow or block the request.** Here a `GqlAuthGuard` reads the user from the GraphQL execution context and rejects anonymous calls — so your resolver code stays clean of manual `if (!ctx.user)` checks. You attach it with `@UseGuards(GqlAuthGuard)` on a `@Query`, `@Mutation`, or `@ResolveField`.

**Why it matters / where interviewers probe:** "Auth user lives in `context`; enforce at operation **and** field level; in Nest use Guards." They may ask why you can't rely on URL-based route protection — because there's **one endpoint**, so authorization must live **inside the graph**, per field/operation.

---

## 15. Apollo Federation (PRO)

**Plain definition:** **Federation** lets you split one big graph across **multiple independent services** (each owns part of the schema = a **subgraph**) and have a **gateway/router** stitch them into **one unified graph (the supergraph)** that clients query as if it were a single API. It's how large companies let separate teams own separate domains without one giant monolithic GraphQL server.

A few terms first, plainly:
- **Entity:** a type that can be **shared and referenced across subgraphs** (e.g. `Player`). It has a key that uniquely identifies it.
- **Directive:** an **annotation in the schema** that starts with `@` and tells the tooling to do something special. `@key` is a directive.
- **`@key`:** declares **which field(s) uniquely identify an entity** so other subgraphs can refer to the same object. The correct syntax is **`@key(fields: "id")`** (not `@key(id)`).
- **Reference resolver (`__resolveReference` / `resolveReference`):** when a subgraph *adds fields to an entity it doesn't own*, it must supply a small function that, given just the key (e.g. `{ id }`), **fetches that entity** so the router can stitch the data together.

### The menu analogy

A **food hall**: separate kitchens — one does sushi, one does pizza, one does desserts (the **subgraphs**). A **single front counter** (the **gateway/router**) takes your one combined order, routes each dish to the right kitchen, and brings back **one tray**. To you it feels like a single restaurant; behind the curtain, independent kitchens own their menus. The magic: a dessert kitchen can **add a topping detail to the pizza** by *referencing the pizza's key* — the graph is shared even though ownership is split.

### Diagram

```
                         ┌──────────────────────────┐
        client ────────► │   Gateway / Router        │  exposes ONE supergraph
                         │   (composes subgraphs)    │
                         └─────┬───────────┬─────────┘
                routes each part of the query to its owner
              ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
              │ Players       │ │ Matches      │ │ Betting/Odds │   ← subgraphs
              │ subgraph      │ │ subgraph     │ │ subgraph     │     (own teams,
              │ (NestJS)      │ │ (NestJS)     │ │ (NestJS)     │      DBs, deploys)
              └──────────────┘ └──────────────┘ └──────────────┘
                    │                 │                 │
                 MongoDB           MongoDB         Redis/odds engine
```

How a `Player` gets enriched by two teams:

```graphql
# Players subgraph OWNS Player and declares its key:
type Player @key(fields: "id") {
  id: ID!
  username: String!
}

# Betting subgraph REFERENCES the same Player (same @key) and ADDS a field.
# It must provide a reference resolver to fetch a Player by its key.
type Player @key(fields: "id") {
  id: ID!            # the key the router uses to match the same entity
  openBets: [Bet!]!  # NEW field contributed by the Betting subgraph
}
```

In the Betting subgraph (NestJS-style), the reference resolver looks like:

```ts
// Given just { id }, return the Player object this subgraph can extend.
@ResolveReference()
resolveReference(ref: { __typename: 'Player'; id: string }) {
  return this.players.findStub(ref.id);  // enough for openBets to resolve
}
```

**Why it matters / where interviewers probe:** At a gaming company with many teams (accounts, matchmaking, betting), they'll ask "how do you scale GraphQL across teams?" Answer: **federation** — each team ships a **subgraph**, a **router composes the supergraph**, and entities are joined via **`@key(fields: "id")`** so one entity (a `Player`) can be enriched by multiple services; a subgraph contributing fields it doesn't own provides a **reference resolver** so the router can fetch the entity by its key. One graph for clients, independent deploys for teams.

---

## 16. GraphQL in NestJS

**Plain definition:** NestJS has a first-class `@nestjs/graphql` module (built on **Apollo**, the popular JS GraphQL implementation). You pick one of two ways to define your schema:

- **Schema-first** = you **write the SDL `.graphql` files by hand**, and Nest generates TypeScript types from them. (The menu is the source of truth; code follows.)
- **Code-first** = you **write TypeScript classes with decorators**, and Nest **generates the SDL for you**. (Code is the source of truth; the menu is auto-printed.) *This is the common choice in NestJS shops* because it stays in TypeScript and avoids drift.

### The Nest decorators (your toolkit)

```
  ┌────────────────┬──────────────────────────────────────────────────┐
  │ Decorator      │ What it marks (menu analogy)                      │
  ├────────────────┼──────────────────────────────────────────────────┤
  │ @ObjectType()  │ a GraphQL type / a dish on the menu               │
  │ @Field()       │ a field on that type / a detail of the dish       │
  │ @Resolver(T)   │ a class of kitchen stations for type T            │
  │ @Query()       │ a read entry point (Query root)                   │
  │ @Mutation()    │ a write entry point (Mutation root)               │
  │ @Subscription()│ a push entry point (Subscription root)            │
  │ @Args()        │ pull an argument out of the request               │
  │ @ResolveField()│ resolver for a CHILD field (e.g. Player.matches)  │
  │ @Parent()      │ inject the parent object (the `parent` slot)      │
  │ @Context()     │ inject the per-request context (user, loaders)    │
  └────────────────┴──────────────────────────────────────────────────┘
```

Recall: a plain scalar `@Field()` (like `username`) gets **no resolver method** — it relies on the **default resolver** (section 5) that reads `parent.username`. You only write a method (`@ResolveField`) for fields that need real work, like fetching from the DB.

### A tiny working example (code-first, gaming)

This snippet defines **everything it references** (including `Odds` and `GqlAuthGuard`) so there are no dangling names, and it keeps mutations and subscriptions conceptually clean: `submitMatch` recomputes odds and **publishes a complete `Odds` object** that satisfies the `Odds!` type from section 9.

```ts
import {
  Resolver, Query, Mutation, Subscription,
  Args, ResolveField, Parent, Context, ObjectType, Field, ID, Int, Float,
} from '@nestjs/graphql';
import { Injectable, UseGuards, CanActivate, ExecutionContext } from '@nestjs/common';
import { GqlExecutionContext } from '@nestjs/graphql';
import { RedisPubSub } from 'graphql-redis-subscriptions';

// Back subscriptions with RedisPubSub so the fan-out works ACROSS server copies
// (see section 9). A plain `new PubSub()` is in-memory and single-process only —
// it would NOT deliver events to subscribers connected to a different copy.
const pubSub = new RedisPubSub(/* { connection: { host, port } } */);

// ---- Types (these become the SDL automatically: code-first) ----
@ObjectType()
class Match {
  @Field(() => ID) id: string;
  @Field(() => Int) score: number;
  @Field() map: string;
  @Field() playerId: string;
}

@ObjectType()
class Player {
  @Field(() => ID) id: string;
  @Field() username: string;
  @Field() rank: string;
  // 'matches' is resolved separately (see @ResolveField below)
}

// Defined here so the @Subscription below references a real, complete type:
@ObjectType()
class Odds {
  @Field(() => ID) matchId: string;
  @Field(() => Float) home: number;
  @Field(() => Float) away: number;
  @Field() updatedAt: string;
}

// A minimal Guard so @UseGuards(GqlAuthGuard) below isn't a dangling name.
// A Guard runs BEFORE the resolver and allows/blocks the request.
@Injectable()
export class GqlAuthGuard implements CanActivate {
  canActivate(ctx: ExecutionContext): boolean {
    const gqlCtx = GqlExecutionContext.create(ctx).getContext();
    return !!gqlCtx.user;            // block anonymous calls
  }
}

// ---- Resolver class = the kitchen stations for Player ----
@Resolver(() => Player)
@Injectable()
export class PlayerResolver {
  constructor(
    private readonly players: PlayerService,
    private readonly matches: MatchService,
    private readonly oddsEngine: OddsService,
  ) {}

  // QUERY: GET one player.  @Args pulls 'id' from the request.
  @Query(() => Player, { name: 'player' })
  getPlayer(@Args('id', { type: () => ID }) id: string) {
    return this.players.findById(id);          // talks to MongoDB
  }

  // CHILD FIELD: Player.matches — 'parent' is the resolved Player.
  // Use a per-request DataLoader here to avoid N+1 (section 10)!
  @ResolveField(() => [Match])
  matches(@Parent() player: Player, @Context() ctx) {
    return ctx.matchesByPlayerLoader.load(player.id);  // batched, no N+1
  }

  // MUTATION: write a match result, recompute odds, then publish a FULL Odds
  // payload (home/away/updatedAt) so it satisfies the non-null Odds! type.
  @Mutation(() => Match)
  @UseGuards(GqlAuthGuard)                      // auth via the Guard above
  async submitMatch(
    @Args('playerId') playerId: string,
    @Args('score', { type: () => Int }) score: number,
    @Args('map') map: string,
  ) {
    const match = await this.matches.create({ playerId, score, map });

    // A new result changes the odds → recompute and publish a COMPLETE Odds.
    const odds = await this.oddsEngine.recompute(match.id); // { home, away }
    await pubSub.publish('oddsUpdated', {
      oddsUpdated: {
        matchId: match.id,
        home: odds.home,
        away: odds.away,
        updatedAt: new Date().toISOString(),
      },
    });

    return match;
  }

  // SUBSCRIPTION: push live odds to subscribed clients (WebSockets).
  // A subscription resolver returns an ASYNC ITERATOR — a stream the server
  // pushes new values into over time — instead of returning a single value.
  // Current graphql-subscriptions API: asyncIterableIterator (NOT asyncIterator).
  @Subscription(() => Odds, {
    filter: (payload, vars) => payload.oddsUpdated.matchId === vars.matchId,
  })
  oddsUpdated(@Args('matchId', { type: () => ID }) matchId: string) {
    return pubSub.asyncIterableIterator('oddsUpdated');
  }
}
```

What maps to what (so the analogy lands):

```
  @ObjectType Player / Match / Odds  →  dishes on the menu (schema types)
  @Query getPlayer                  →  the front-desk read order
  @ResolveField matches             →  the 'matches' kitchen station, fed `parent`
  @Args                             →  special instructions on the order
  @Mutation submitMatch             →  change-the-pantry request (serial write)
  @Subscription oddsUpdated         →  standing order, pushed over WebSockets
  @Context ctx                      →  the shared kitchen pass (user + DataLoaders)
```

**Why it matters / where interviewers probe:** Expect "code-first vs schema-first?" Answer: *"Code-first — decorate TS classes, Nest emits the SDL; less drift, full type-safety."* Then connect the dots: **`@ResolveField` is exactly where N+1 bites, so I inject a DataLoader from `@Context`.** That single sentence signals senior. Two correctness details worth dropping: **a subscription resolver returns an async iterator** (via `asyncIterableIterator` in current `graphql-subscriptions`), and **for cross-instance fan-out you must use `RedisPubSub`, not the in-memory `PubSub`.**

---

## 17. Putting it all together

A full **request → resolve → response** trace for one realistic query (our canonical Player 42), touching schema, args/variables, resolver chain, DataLoader, context/auth, and the response shape.

```
CLIENT sends ONE request to POST /graphql
────────────────────────────────────────────────────────────────────────
  query Summary($id: ID!) {
    player(id: $id) {
      username
      rank
      matches {            # list → N children → DataLoader territory
        score
        map
      }
    }
  }
  variables: { "id": "42" }
  headers:   Authorization: Bearer <jwt>

         │
         ▼
┌────────────────────────────────────────────────────────────────────────┐
│ 1. VALIDATE against the SCHEMA (the menu).                               │
│    Unknown fields? wrong arg types? → reject NOW, before any resolver.   │
│    (Under modern GraphQL-over-HTTP, that rejection is a 4xx — section 13)│
├────────────────────────────────────────────────────────────────────────┤
│ 2. BUILD CONTEXT (once):                                                 │
│    verify JWT → user{id, roles};  create per-request DataLoaders.        │
├────────────────────────────────────────────────────────────────────────┤
│ 3. RESOLVER CHAIN (parent → child). Unused root parent written `_`:      │
│                                                                          │
│    Query.player(_, {id:"42"}, ctx)                                       │
│        └─► reads MongoDB → Player{ id:42, username:"Zara", rank:"Diamond"}│
│                 │ becomes `parent`                                       │
│        ┌────────┼─────────────┐                                          │
│        ▼        ▼             ▼                                          │
│   .username  .rank        .matches(parent=P42, ctx)                      │
│   "Zara"     "Diamond"        └─► ctx.matchLoader.load(42)               │
│   (default   (default          (batched: ONE query, no N+1)             │
│    resolver)  resolver)        → [M101(1820,"Dust"), M102(1760,"Nuke")]  │
│                                     each → .score, .map                  │
│   (.username & .rank may have their DB-backed async work overlap —       │
│    concurrent on one thread, not truly parallel — section 6)             │
├────────────────────────────────────────────────────────────────────────┤
│ 4. ASSEMBLE response in the SAME SHAPE as the query.                     │
│    Any field that threw → null + an entry in `errors[]` (partial data);  │
│    a non-null field that fails null-bubbles to its nearest nullable      │
│    ancestor (section 13).                                                │
└────────────────────────────────────────────────────────────────────────┘
         │
         ▼
SERVER returns:
{
  "data": {
    "player": {
      "username": "Zara",
      "rank": "Diamond",
      "matches": [
        { "score": 1820, "map": "Dust" },
        { "score": 1760, "map": "Nuke" }
      ]
    }
  }
}
```

The full restaurant, end to end:

```
 customer writes an à-la-carte order  ──►  waiter checks the menu (schema)
        ──►  checks membership card (auth/context via JWT)
        ──►  routes each dish to its station (resolver chain)
        ──►  station batches 50 sides into one kitchen trip (DataLoader)
        ──►  plates everything in the exact order requested (response shape)
        ──►  notes any dish that failed on the ticket (errors array)
        ──►  one tray, exactly what was ordered, nothing wasted.
```

---

## 18. Glossary

```
┌────────────────────┬──────────────────────────────────────────────────────────┐
│ Term               │ Plain meaning (+ menu analogy)                            │
├────────────────────┼──────────────────────────────────────────────────────────┤
│ GraphQL            │ Graph Query Language: query language + runtime for APIs.  │
│ Runtime            │ Server-side code that validates a query against the       │
│                    │ schema, runs resolvers, and assembles the JSON response.  │
│ Apollo             │ The most popular JS GraphQL server/client implementation. │
│ SDL                │ Schema Definition Language — the syntax for writing       │
│                    │ schemas (the language the menu is written in).            │
│ Schema             │ The typed contract: everything you can ask for (the menu).│
│ Type / Object type │ A kind of thing (Player, Match, Team) — a dish category.  │
│ Field              │ A named value on a type (username) — a detail of a dish.  │
│ Scalar             │ A leaf value: Int, Float, String, Boolean, ID.            │
│ Int / Float        │ Whole number / decimal number.                            │
│ String / Boolean   │ Text / true-false.                                        │
│ ID                 │ Unique identifier (a key). Output as String; on input     │
│                    │ accepts Int or String and coerces to String.              │
│ Non-null (!)       │ Guaranteed to have a value, never null. Inner ! in        │
│                    │ [Match!]! = items non-null; outer ! = the list non-null.  │
│ Node / Edge (graph)│ A thing / a relationship between two things in the graph. │
│ Query              │ A READ request — the order ticket. Shape mirrors response.│
│ Mutation           │ A WRITE request (create/update/delete). Top-level fields  │
│                    │ run serially (one after another).                         │
│ Subscription       │ Server PUSHES data over WebSockets on each event.         │
│ Selection set      │ The { fields } you pick inside a type.                    │
│ Resolver           │ Function that returns one field's value (a kitchen        │
│                    │ station).                                                 │
│ Default resolver   │ The auto resolver GraphQL uses when you write none: it    │
│                    │ returns parent[fieldName] (and calls it if it's a fn).    │
│ parent/root/source │ Resolver arg: the parent field's result. Often written    │
│                    │ `_` when unused.                                          │
│ args               │ Resolver arg: the field's arguments (special              │
│                    │ instructions).                                            │
│ context            │ Per-request shared object: user, DB, loaders (kitchen     │
│                    │ pass).                                                    │
│ info               │ Resolver arg: metadata about the query, including the     │
│                    │ AST (Abstract Syntax Tree — the parsed, tree-structured    │
│                    │ form of the query the server works with internally).      │
│ Resolver chain     │ Fields resolved parent→child; query tree = resolver tree. │
│ Concurrent (vs     │ Async work overlapping on one thread (sibling query       │
│  parallel)         │ fields) — NOT true multi-core parallelism.                │
│ Argument           │ Input to a field, e.g. player(id: "42").                  │
│ Variable           │ Named, typed placeholder ($id) passed separately.         │
│ Input type         │ A type used as a write payload (a fill-in form).          │
│ JWT                │ JSON Web Token — a signed token sent in the Authorization │
│                    │ header to prove who the caller is.                        │
│ Guard (NestJS)     │ A Nest class that runs before a resolver to allow/block   │
│                    │ the request (e.g. reject anonymous callers).              │
│ N+1 problem        │ 1 query for a list + N queries for its children = N+1.    │
│ DataLoader         │ Per-request batching+caching to kill N+1: collects load() │
│                    │ calls in the current frame, fires one batch on next tick. │
│ tick / next tick   │ One pass of Node's event loop; DataLoader dispatches its  │
│                    │ batch on the next tick (process.nextTick by default).     │
│ IN (...) / $in     │ DB "where id is any one of these" — fetch all keys in one │
│                    │ query (SQL: IN; MongoDB: $in).                            │
│ PubSub             │ Publish/subscribe: publishers emit to a named channel;    │
│                    │ all subscribers on that channel receive the event.        │
│ Fan-out            │ Delivering one published event to many subscribers.       │
│ Horizontal scaling │ Running several copies of the server behind a load        │
│                    │ balancer.                                                 │
│ Load balancer      │ Traffic cop that spreads connections across server copies.│
│ RedisPubSub        │ PubSub backed by Redis → fan-out works across many copies.│
│ Backpressure       │ Handling events faster than a slow client can consume     │
│                    │ them (buffer/drop/slow down).                             │
│ Pagination         │ Returning a big list in pages.                            │
│ Offset pagination  │ skip N / take M — simple, drifts on writes, slow deep.    │
│ Cursor pagination  │ "after this bookmark" — stable, fast at scale.            │
│ Opaque cursor      │ A coded (e.g. Base64) bookmark string the client must     │
│                    │ pass back verbatim, never parse.                          │
│ Relay              │ A Facebook GraphQL client whose Connections pagination    │
│                    │ convention became a de-facto standard.                    │
│ Relay connection   │ Standard cursor shape: edges{cursor,node}, pageInfo.      │
│ Edge (Relay)       │ A wrapper carrying cursor + node — a SPECIFIC spec        │
│                    │ construct, distinct from the general graph-edge idea.     │
│ node               │ The actual item inside each Relay edge.                   │
│ pageInfo           │ hasNextPage/hasPreviousPage + start/endCursor (cursors    │
│                    │ nullable; where to continue).                             │
│ errors array       │ List of failures returned alongside partial data.         │
│ extensions.code    │ Machine-readable error code (UNAUTHENTICATED, etc.).      │
│ Null-bubbling      │ A null in a non-null field bubbles up to nearest nullable │
│                    │ ancestor, which becomes null.                             │
│ HTTP status (GQL)  │ Legacy json = always 200; modern graphql-response+json    │
│                    │ = 4xx for parse/validation, 200 for executed ops.         │
│ Introspection      │ Querying the schema about its own types (auto-docs/tools).│
│ CDN                │ Content Delivery Network: edge servers caching responses  │
│                    │ keyed on URL, close to users.                             │
│ Federation         │ Compose one supergraph from many subgraphs.               │
│ Subgraph           │ One service's slice of the schema.                        │
│ Supergraph         │ The composed, unified graph clients see.                  │
│ Gateway / Router   │ Routes each query part to the owning subgraph.            │
│ Entity             │ A type shareable/referenceable across subgraphs (has key).│
│ Directive          │ A schema annotation starting with @ (e.g. @key).          │
│ @key               │ Declares which field(s) identify an entity, written       │
│                    │ @key(fields: "id"), so subgraphs can share it.            │
│ Reference resolver │ __resolveReference: fetches an entity from just its key   │
│                    │ so a subgraph can add fields to one it doesn't own.       │
│ Code-first         │ Write TS decorators → Nest generates SDL.                 │
│ Schema-first       │ Write SDL by hand → generate TS types.                    │
└────────────────────┴──────────────────────────────────────────────────────────┘
```

---

## 19. Interview "say-this" one-liners

Crisp lines to drop in the room. Each is defensible and senior-flavored.

- **What is GraphQL?** "A query language and runtime for APIs: **one endpoint**, and the **client asks for exactly the fields it needs**, typed by a schema."
- **Why over REST?** "It kills **over-fetching and under-fetching** — one request returns precisely the nested data the client wants, instead of many round-trips with wasted fields."
- **Is it always better than REST?** "No. REST still wins for **HTTP/CDN caching**, **file uploads**, and simple CRUD. GraphQL wins for **many clients with varied, nested needs**."
- **The schema?** "A **strongly-typed, introspectable contract**; the server **validates every query against it before executing**, so bad requests fail fast."
- **Resolver args?** "**parent, args, context, info** — context is **per-request shared state**: auth user, DB connections, and DataLoaders. Fields with no explicit resolver use the **default resolver** that reads `parent[fieldName]`."
- **How does execution work?** "The query is a **tree**; resolvers form a **matching tree**; each **parent's result flows into its children**. Sibling query fields run **concurrently on one thread** (overlapping async I/O), not truly in parallel."
- **Mutations vs queries?** "Top-level mutations are **awaited serially** so writes don't race; query fields run **concurrently** because reads don't conflict."
- **Subscriptions?** "**Server push over WebSockets** via a **PubSub** (publishers emit to a channel, subscribers receive). I scale it with **Redis** so events fan out to clients on **any server copy** — the in-memory PubSub is single-process only."
- **N+1?** "A nested field over **N** parents fires **N** fetches; I add a **per-request DataLoader** that **batches the keys into one `$in`/`IN (...)` query** and caches duplicates within the request."
- **Why is DataLoader per-request?** "So its cache **can't leak data across users/requests** and never serves stale reads."
- **Pagination?** "**Cursor-based / Relay connections** for a live leaderboard — **stable under concurrent writes** and **fast at depth**; offset is fine for small static lists. Cursors are **opaque** — pass them back, don't parse them."
- **Error handling?** "Errors live in the **`errors` array in the body** alongside partial `data`; clients branch on **`extensions.code`**, and **non-null field errors bubble up to the nearest nullable ancestor**. Status is **200 for executed ops**, **4xx for parse/validation** under modern GraphQL-over-HTTP."
- **Auth?** "User goes in **context** from a **JWT**; enforce at **operation and field level** — in Nest via **Guards** — because there's **one endpoint**, so authorization lives **inside the graph**."
- **Scaling across teams?** "**Apollo Federation**: each team owns a **subgraph**, a **router composes the supergraph**, entities are joined via **`@key(fields: \"id\")`**, and a contributing subgraph supplies a **reference resolver** — one graph for clients, independent deploys for teams."
- **Code-first or schema-first in Nest?** "**Code-first**: decorate TS classes, Nest emits the SDL — less drift, full type-safety; and **`@ResolveField` is where I wire in DataLoaders** to avoid N+1."

---

*You now have the full arc: the problem, the schema, queries, resolvers and their chain, args/variables, mutations, real-time subscriptions, N+1/DataLoader, the honest REST trade-off, pagination, errors, auth, federation, and the NestJS implementation — all on one menu, with one canonical Player 42 dataset carried verbatim throughout. Walk in and order with confidence.*