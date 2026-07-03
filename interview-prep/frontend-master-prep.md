# 🎯 Frontend / MERN — Master Interview Prep

> For the Wells Fargo customer-facing **MERN / React** Software Engineer role. Every topic: **Definition** → visual → code → **💡 memory trick** → interview answer.
> Jump to the **📖 Definitions Glossary** to speed-memorize one-line definitions.

---

## 🗺️ Contents

1. ⚙️ JavaScript — Execution Model & the Event Loop
2. ⚡ JavaScript — Promises & async/await
3. 🧠 JavaScript — Closures, `this`, Hoisting
4. 🧠 JavaScript — Memory, Prototypes, Operators
5. ⚛️ React — Fundamentals + useState + useEffect
6. ⚛️ React — useRef, useMemo, useCallback, React.memo, useContext
7. ⚛️ React — Custom Hooks, Re-render Triggers, Keys
8. 🔄 Redux / State Management
9. ♿ Accessibility (a11y) — Banking Priority
10. 🧪 Testing — Jest, RTL, TDD, BDD
11. ⚡ Performance
12. 🎨 HTML + CSS
13. 🟢 Node + Express + MongoDB (MERN backend)
14. 🔒 Security (Frontend)
15. 🧩 Design Patterns + OOAD
16. 🚀 CI/CD + Build Tools
17. 🌟 Behavioral (STAR) — Tell Your Story, Lead With Impact

---

## 📖 Definitions Glossary — recite these if asked "what is X?"

- **Call Stack** — A LIFO stack of execution contexts — the engine pushes a frame when a function is called and pops it when the function returns.
- **Web APIs (host-provided)** — Browser- (or Node-) provided APIs that do async work *off* the main thread — `setTimeout`, `fetch`, DOM events, `XMLHttpRequest`, geolocation — and hand a callback back to a queue when done.
- **Callback (Task / Macrotask) Queue** — FIFO queue holding callbacks from completed macrotasks (timers, I/O, UI events) — the event loop pulls **one** per loop iteration.
- **Microtask Queue vs Macrotask Queue** — Two separate queues with different priority — **microtasks** (`Promise.then`, `queueMicrotask`, `await` continuations, `MutationObserver`) drain **completely** after each task; **macrotasks** (`setTimeout`, `setInterval`, I/O, UI events) run **one at a time**.
- **The Event Loop** — The runtime loop that, while the call stack is empty, moves queued callbacks onto the stack — draining all microtasks, then running one macrotask, then repeating.
- **async / await internals** — `async`/`await` is syntactic sugar over promises — `await` **pauses** the function, returns control to the caller, and **resumes** the rest of the function as a **microtask** once the awaited promise settles.
- **Promise states (pending → fulfilled / rejected, settled)** — A promise lives in exactly one of three states; once it leaves `pending` it is **settled** and can never change again (immutable).
- **Promise chaining & return-value flattening** — `.then()` returns a **new** promise; whatever you `return` inside becomes the next `.then`'s input — and if you return a thenable/promise, it gets **flattened** (auto-awaited), not nested.
- **Error propagation down a chain** — A thrown error or rejection **skips all `.then`s** and jumps to the nearest `.catch` (or rejection handler) downstream; after `catch` returns a value, the chain **resumes** fulfilled.
- **`Promise.all` — all succeed or first reject** — Waits for **every** promise to fulfill and returns an array of results **in input order**; if **any** rejects, `all` rejects immediately with that first error (fail-fast).
- **`Promise.allSettled` — never rejects** — Waits for **all** to settle and **always fulfills** with an array (in input order) of `{status:'fulfilled', value}` or `{status:'rejected', reason}` — perfect when you want every result regardless of individual failures.
- **`Promise.race` — first to settle** — Settles as soon as the **first** promise settles — fulfilled **or** rejected — adopting its value/error and ignoring the rest.
- **`Promise.any` — first to FULFILL** — Fulfills with the **first promise that fulfills**, ignoring rejections; only rejects if **all** fail — and then with an **`AggregateError`** whose `.errors` array bundles every reason in input order.
- **`async`/`await` & try/catch** — `async` makes a function always return a promise; `await` pauses *inside* that function until a promise settles, unwrapping its value (or throwing its rejection) — letting you write async code that *reads* synchronously.
- **Parallel vs sequential awaits** — Sequential `await`s run one-after-another (each waits for the previous); to run independent tasks **concurrently**, start them first (or use `Promise.all`) and await together.
- **How async/await works internally (the microtask queue)** — `async`/`await` is **syntactic sugar over promises**: at each `await`, the function *pauses*, the rest of its body is registered as a continuation (effectively a `.then` callback on the awaited promise), and control returns to the caller/event loop; when the awaited promise settles, that continuation is queued on the **microtask queue** and resumed.
- **Closure: Lexical Scope** — A closure is a function that *remembers and accesses* the variables from the scope where it was **defined** (its lexical scope), even after the outer function has returned.
- **Closure Use Cases** — Closures power private state, rate-limiters, and React Hooks — anywhere a function needs to "hold on to" data across calls.
- **`this` — Binding Rules** — `this` is determined by **how a function is called** (its call-site), not where it's defined — except arrow functions, which inherit `this` lexically.
- **Hoisting** — Hoisting is JS moving declarations to the top of their scope during compilation — but **how** they're initialized differs by keyword.
- **Stack vs Heap** — The **stack** holds primitives, references (pointers), and call frames in fixed-size LIFO order; the **heap** is the large unordered region where objects/arrays/functions live.
- **Garbage Collection (mark-and-sweep, reachability)** — GC automatically frees heap memory by finding objects no longer **reachable** from a set of roots and reclaiming them.
- **Reference vs Value** — Primitives are **copied by value** (each name gets an independent copy); objects copy the **reference value** — both names hold a pointer to the *same* heap object. (JS never copies the object itself on assignment; it copies the pointer.)
- **Prototype System: `__proto__` vs `prototype`** — `prototype` is a property on **constructor functions** (the blueprint for instances); `__proto__` is the actual link **on an instance** pointing to its prototype.
- **The Prototype Chain & Lookup** — Property lookup walks up the `__proto__` chain — own property first, then each prototype, until found or it hits `null`.
- **Constructor Functions** — A regular function called with `new` that builds and returns a fresh object, with `this` bound to that new instance.
- **Prototypal Inheritance** — Objects inherit directly from other objects by chaining prototypes, so a child's prototype's `__proto__` points to the parent's prototype.
- **Class as Syntactic Sugar** — ES6 `class` is cleaner syntax over the same constructor-function + prototype machinery — no new inheritance model underneath.
- **`==` (coercion) vs `===` (strict)** — `===` compares type **and** value with no conversion; `==` coerces operands to a common type first, which produces surprises.
- **Spread (`...`) vs Rest (`...`)** — Same `...` token, opposite jobs — **spread** expands an iterable *out* into elements; **rest** collects leftovers *in* to one array/object.
- **Destructuring (array / object, defaults, rename)** — Syntax to unpack values from arrays (by **position**) or objects (by **key**) into distinct variables.
- **Optional Chaining `?.`** — Short-circuits to `undefined` (instead of throwing) the moment a reference in the chain is `null`/`undefined`.
- **Nullish Coalescing `??` (vs `||`)** — `??` returns the right side only when the left is `null` or `undefined`; `||` returns the right side for **any** falsy value (`0`, `""`, `false`, `NaN` too).
- **Virtual DOM** — An in-memory JavaScript object tree that mirrors the real DOM; React builds a fresh copy on each render, compares it to the previous one, and syncs only the differences to the slow real DOM.
- **Reconciliation** — The process React runs to compare the new VDOM with the previous one and decide the minimal set of real-DOM mutations needed.
- **The Diffing Algorithm** — React's O(n) heuristic for reconciliation built on two assumptions — (1) different element **types** produce different trees, and (2) **keys** identify which list children are stable across renders.
- **Fiber Architecture (high level)** — React's reimplemented reconciler (v16+) that splits rendering into small units of work that can be **paused, prioritized, and resumed**, so high-priority updates (typing, clicks) don't get blocked by big renders.
- **One-Way Data Flow** — Data flows **down** from parent to child via props; children communicate **up** only by calling callback functions the parent passed down — never by mutating parent state directly.
- **What happens after setState/useState update?** — A state update is a *request to re-render*, not an immediate DOM write. React runs a four-stage pipeline.
- **Functional Components: props, state, drilling, lifecycle** — Components are plain functions that take `props` (read-only inputs) and return JSX; `useState` gives them local state, and effects replace class lifecycle methods.
- **useState: async nature, batching, functional updater** — `useState` returns `[value, setValue]`; calling the setter schedules a re-render — it does **not** mutate the current variable, and multiple setters in one event (and, in React 18+, inside promises/timeouts/native handlers too) are **batched** into a single re-render.
- **useEffect: the four dependency cases** — `useEffect(fn, deps)` runs side effects *after* the browser paints; the dependency array controls *how often* it runs, and an optional returned function is the **cleanup**.
- **useRef** — `useRef(initialValue)` returns a mutable object `{ current }` that persists across renders and does **not** trigger a re-render when you mutate `.current`. The same object identity is returned on every render.
- **useMemo** — `useMemo(fn, deps)` memoizes the **result of an expensive computation**, recomputing only when one of its dependencies changes (shallow `Object.is` comparison of each dep).
- **useCallback** — `useCallback(fn, deps)` memoizes a **function reference**, returning the same function instance until one of its dependencies changes.
- **React.memo** — `React.memo` is a Higher-Order Component that wraps a component and **skips re-rendering** it when its props are **shallowly equal** (`Object.is` per prop) to the previous render. It only guards against *prop-driven* re-renders — a memoized component still re-renders from its own `useState`/`useContext` changes.
- **useContext** — `useContext(MyContext)` reads the current value from the nearest matching Context Provider above it, letting deeply nested components consume shared state **without prop drilling**.
- **Custom Hooks — extract reusable stateful logic** — A custom hook is a `use`-prefixed JS function that calls other hooks to package reusable stateful logic, so multiple components share the *logic* (not the *state* — each call gets its own isolated state).
- **Rules of Hooks** — Hooks must be called in the **same order on every render**, only at the **top level** of a React function, and only from **React functions** (components or other hooks); the name must start with `use`.
- **Implement `useFetch`** — A hook that fetches a URL and exposes `{ data, loading, error }`, re-fetching when the URL changes and aborting stale in-flight requests.
- **Implement `useDebounce`** — A hook that returns a value only **after it has stopped changing** for `delay` ms — collapsing a burst of updates into one.
- **Implement `useLocalStorage`** — A `useState`-like hook that persists its value to `localStorage` and reads it back on mount, surviving page reloads.
- **Re-render Triggers** — A component re-renders when its **own state changes**, its **parent re-renders**, a **subscribed context value changes**, or it receives **new props** — but **not** when a `useRef().current` mutates.
- **Keys in Lists — why `index` as key is a bug** — A `key` is a stable identity React uses to match each list item to a DOM node across renders; using the array **index** breaks that identity when items are **inserted, removed, or reordered**, causing React to reuse the wrong node (and its component state / uncontrolled input value) for the wrong data.
- **Core Unidirectional Flow** — Data flows one way — a UI event dispatches an action, a pure reducer computes the next state, the store updates, and subscribed UI re-renders.
- **The Pieces (store, reducer, action creators, dispatch, subscribe)** — The five primitives that make Redux work — the **store** holds state, **reducers** compute it, **action creators** describe events, **dispatch** sends them, **subscribe** reacts to changes.
- **Reducers Must Be Pure** — A reducer is a pure function — same input always gives same output, with no side effects (no API calls, no `Date.now()`, no mutation).
- **Immutability** — Never modify existing state; always produce a **new** object/array so Redux can detect change by reference (`prev !== next`).
- **Redux Toolkit (RTK) — The Modern Standard** — The official, batteries-included Redux package that removes boilerplate — `configureStore`, `createSlice` (auto-generates actions + reducers), and Immer for safe "mutating" syntax.
- **Using It in React (`useSelector` / `useDispatch`)** — `useSelector` reads a slice of state and subscribes to it; `useDispatch` returns the dispatch function to send actions.
- **Middleware (thunk, logger)** — Functions that sit between `dispatch` and the reducer, intercepting actions to add capabilities like async (thunk) or logging.
- **`createAsyncThunk` (async / API)** — RTK helper that generates an async thunk plus three auto-dispatched lifecycle actions — `pending`, `fulfilled`, `rejected` — so you can track loading/error state cleanly.
- **When To Use What** — Choose the lightest tool that fits the scope — local state for one component, Context for small low-frequency globals, Redux for large shared frequently-updated state.
- **Semantic HTML vs `<div onClick>`** — Using meaningful HTML elements (`<button>`, `<nav>`, `<main>`...) that carry built-in roles, keyboard handling, and screen-reader semantics — instead of generic `<div>`/`<span>` wired up manually.
- **ARIA — attributes & the cardinal rule** — ARIA (Accessible Rich Internet Applications) adds roles/states/properties to tell assistive tech what an element *is* and *is doing*, when native HTML can't express it. ARIA changes only the accessibility tree — it never adds behavior (no focus, no keyboard, no events); you still wire those yourself.
- **Keyboard Navigation** — Every interactive control must be operable with the keyboard alone — no mouse required.
- **Focus Management — trap & restore** — Programmatically controlling where keyboard focus lives — trapping it inside an open modal, and returning it to the trigger element on close.
- **Screen Readers — NVDA / JAWS / VoiceOver** — Software that reads the accessibility tree aloud and lets blind/low-vision users navigate by headings, landmarks, links, and form fields.
- **Accessible Forms** — Form fields with programmatically associated labels, clear errors tied to inputs, and validation state exposed to assistive tech.
- **WCAG Levels A / AA / AAA + Color Contrast** — Web Content Accessibility Guidelines — testable success criteria grouped into three conformance levels; **AA** is the legal/industry target for banking.
- **Accessible Custom Dropdown (listbox pattern)** — A non-native select built from a button + list that replicates native keyboard, focus, and SR behavior via ARIA roles. This is the **collapsible listbox** pattern: focus *stays on the button*, and the highlighted option is tracked with `aria-activedescendant` (not real DOM focus).
- **Unit testing with Jest** — Testing a single, isolated piece of logic (usually one pure function) to verify it returns the right output for given inputs.
- **React Testing Library (RTL)** — A library that tests React components the way a **user** experiences them — by querying the rendered DOM (roles, text, labels) and firing real interactions, not by poking component internals.
- **Mocking** — Replacing a real dependency (function, module, network call) with a controllable fake so the test is fast, deterministic, and isolated.
- **Test types** — Tests differ by **scope** — how much of the system they exercise at once.
- **TDD — Test-Driven Development** — Write a **failing test first**, write the minimum code to pass it, then clean up — repeat in tiny cycles.
- **BDD — Behavior-Driven Development** — TDD framed in **business language** — describe behavior as **Given / When / Then** so tests read like specs a product owner could understand.
- **The Test Pyramid** — A guideline for test *distribution* — lots of fast cheap unit tests at the base, fewer integration tests, very few slow expensive E2E tests at the top.
- **React.memo** — A higher-order component that memoizes a component, skipping re-render when its props are shallow-equal to the previous render.
- **useMemo** — Caches the *result* of an expensive computation, recomputing only when its dependencies change.
- **useCallback** — Caches a *function instance* so the same reference is passed across renders (useful when a child is memoized or the function is a hook dependency).
- **Avoiding needless renders (putting it together)** — Combine `React.memo` (skip child render) + `useMemo`/`useCallback` (keep prop references stable) so a parent re-render doesn't cascade into untouched children.
- **Code-splitting with React.lazy + Suspense** — Defer loading a component's JS until it's actually rendered, shrinking the initial bundle; `Suspense` shows a fallback while the chunk downloads.
- **List virtualization (react-window)** — Render only the rows currently visible in the viewport (plus a small buffer) instead of mounting thousands of DOM nodes.
- **Stable keys** — Give list items a unique, persistent `key` so React can match elements across renders instead of re-creating/misplacing DOM and state.
- **Bundle size** — The total JS/CSS the browser must download, parse, and execute before the app is interactive — smaller is faster.
- **Tree shaking** — Dead-code elimination — the bundler drops exports you never import, relying on static ES-module (`import`/`export`) analysis.
- **Lazy-loading images (loading="lazy")** — Native browser attribute that defers off-screen image loading until the user scrolls near them.
- **Caching / CDN** — Serve static assets from edge servers near the user and tell browsers to reuse them via HTTP cache headers — fewer/closer round-trips.
- **Core Web Vitals — LCP** — **Largest Contentful Paint** — time until the biggest visible element (hero image, headline, main text block) is rendered. **Good ≤ 2.5s** (measured at the 75th percentile of real users).
- **Core Web Vitals — INP (replaced FID)** — **Interaction to Next Paint** — measures responsiveness: the latency from a user interaction (click/tap/key) to the next visual update, reporting (roughly) the worst interaction across the whole visit. **Good ≤ 200ms.** Replaced **FID** as a Core Web Vital in March 2024.
- **Core Web Vitals — CLS** — **Cumulative Layout Shift** — a unitless score of how much visible content unexpectedly jumps around during the page's life. **Good ≤ 0.1.**
- **Tools — Lighthouse** — Automated audit (Chrome DevTools / CI) scoring Performance, Accessibility, Best Practices, SEO, with Core Web Vitals and concrete fix suggestions.
- **Tools — React Profiler** — React DevTools tab that records renders, showing which components rendered, how long they took, and *why*.
- **Tools — why-did-you-render** — A dev-only library that logs to the console whenever a tracked component re-renders with props/state that are *equal in value but different by reference* (i.e., avoidable renders).
- **Diagnose & fix a slow React page — step by step** — A repeatable playbook: measure first, find the bottleneck, fix the biggest one, re-measure.
- **Semantic HTML Elements** — Tags that describe the *meaning* of content (`header`, `nav`, `main`) rather than just its appearance (`div`, `span`).
- **Forms: label / input / fieldset** — Form controls bound to descriptive labels and grouped semantically so every input is programmatically identifiable.
- **Accessibility Basics (a11y)** — Building UI usable by everyone — keyboard, screen reader, low vision — via semantics, ARIA, focus management, and contrast.
- **The Box Model** — Every element is a box of four layers — content → padding → border → margin — and `box-sizing` decides whether the declared `width`/`height` includes padding and border.
- **Flexbox** — A *one-dimensional* layout system that distributes space along a single axis (row or column) with powerful alignment.
- **CSS Grid** — A *two-dimensional* layout system defining explicit rows and columns, with optional named template areas.
- **Position** — `position` controls how an element is placed and what `top/right/bottom/left` are relative to.
- **z-index & Stacking Context** — `z-index` orders overlapping elements on the Z axis — but only *within the same stacking context*, a self-contained layering "bubble."
- **Specificity** — The scoring system that decides which conflicting CSS rule wins — computed as a tuple **(inline, id, class, element)**.
- **Media Queries & Mobile-First Responsive Design** — `@media` applies CSS conditionally on viewport features; *mobile-first* means base styles target small screens, then `min-width` queries layer on enhancements upward.
- **XSS — Cross-Site Scripting** — An attacker injects malicious JavaScript that runs in *another user's* browser, in *your site's* origin — so it can read cookies, tokens, the DOM, and act as that user.
- **CSRF — Cross-Site Request Forgery** — A malicious site tricks a logged-in user's browser into sending an *authenticated* request to your app — the browser auto-attaches the user's cookies, so the request looks legitimate.
- **JWT — Access & Refresh Tokens** — A JWT is a signed, self-contained token (`header.payload.signature`, base64url) the server issues after login; the client sends it to prove identity. **Signed, not encrypted** — anyone can read the payload, so never put secrets in it.
- **CSP — Content-Security-Policy** — An HTTP response header that tells the browser which sources of scripts, styles, images, etc. are allowed — so even if an attacker injects a `<script>`, the browser refuses to run it.
- **HTTPS & No Secrets in the Bundle** — Serve everything over TLS (HTTPS) so data is encrypted in transit, and remember that **anything shipped to the browser is public** — there are no secrets on the client.
- **SOLID Principles** — A module/class/component should have exactly one reason to change (one job).
- **Design Patterns** — Guarantees a single shared instance with one global access point.
- **Build tools: Webpack vs Vite** — Build tools take your many source files (TS/JSX/CSS) and transform + combine them into optimized assets the browser can run; Webpack is the mature bundler-first tool, Vite is the modern fast tool using native ESM in dev and Rollup for production.
- **Bundling** — Combining many separate source modules (and their dependencies) into one or a few files to reduce HTTP requests and resolve `import`/`require` for the browser.
- **Tree shaking** — Dead-code elimination that drops unused **exports** from the final bundle, relying on static `import`/`export` (ESM) analysis.
- **Minification** — Shrinking output by removing whitespace/comments and renaming variables to short names, without changing behavior.
- **Source maps** — A `.map` file that maps minified/transpiled production code back to your original source, so DevTools shows real file names, lines, and variable names when debugging.
- **Code splitting** — Breaking the bundle into smaller chunks loaded **on demand** (e.g. per route), so users download only what the current view needs.
- **CI/CD: the three pipelines** — **CI (Continuous Integration)** = on every push, automatically build and test the merged code; **CD (Continuous Delivery/Deployment)** = automatically package and release that validated code to an environment.
- **Why CI/CD** — Automating build/test/deploy so problems are caught early and releases are small, frequent, and reversible instead of rare and risky.
- **Code: minimal GitHub Actions workflow** — GitHub Actions runs YAML-defined **workflows** (triggered by events) made of **jobs** that run **steps** on a hosted runner — the standard way to wire CI/CD in a GitHub repo.

---

## ⚙️ JavaScript — Execution Model & the Event Loop

JavaScript is **single-threaded**: one call stack, one thing at a time. Concurrency comes not from threads but from the **event loop** orchestrating the stack, the host's Web APIs, and two queues. Below is every piece.

---

### 🧱 Call Stack

**Definition:** A LIFO stack of execution contexts — the engine pushes a frame when a function is called and pops it when the function returns.

```
   call: a() -> b() -> c()

   ┌──────────┐  <- top (runs first to finish)
   │   c()    │
   ├──────────┤
   │   b()    │
   ├──────────┤
   │   a()    │
   ├──────────┤
   │  main()  │  <- bottom (global)
   └──────────┘
   LIFO: last in, first out
```

```js
function c() { console.log("c"); }
function b() { c(); }
function a() { b(); }
a(); // stack: a → b → c, unwinds c → b → a
// logs: c
```

If the stack never empties (e.g. infinite recursion) you get `RangeError: Maximum call stack size exceeded`. **The event loop can only push new work when the stack is empty.**

> 💡 **Remember:** Stack = a pile of plates. You only touch the top one. Synchronous code runs *to completion* before anything async gets a turn.

---

### 🌐 Web APIs (host-provided)

**Definition:** Browser- (or Node-) provided APIs that do async work *off* the main thread — `setTimeout`, `fetch`, DOM events, `XMLHttpRequest`, geolocation — and hand a callback back to a queue when done.

```
  JS Engine (V8)              Host environment (browser)
  ┌──────────┐   register     ┌─────────────────────────┐
  │  Stack   │ ─────────────► │ Web APIs                │
  │          │                │  timer | fetch | DOM ev │
  └──────────┘                └───────────┬─────────────┘
                                          │ when done, callback →
                                          ▼
                                   Task / Microtask Queue
```

```js
console.log("1");
setTimeout(() => console.log("3"), 0); // handed to timer Web API, NOT the stack
console.log("2");
// 1, 2, 3  — setTimeout's callback waits in the queue until the stack is clear
```

> 💡 **Remember:** `setTimeout`/`fetch` aren't "JavaScript" — they're the **host's** APIs. The engine itself has no timers or network. They do the waiting *for* you, then queue the callback. (`fetch` is a special case: its callback runs as a **microtask** because it resolves a promise — only timers/I/O/UI events are macrotasks.)

---

### 📋 Callback (Task / Macrotask) Queue

**Definition:** FIFO queue holding callbacks from completed macrotasks (timers, I/O, UI events) — the event loop pulls **one** per loop iteration.

```
  Macrotask Queue (FIFO):
  front → [ timerCb ][ clickCb ][ fetchDoneCb ] ← back
            take one per tick
```

```js
setTimeout(() => console.log("A"), 0);
setTimeout(() => console.log("B"), 0);
// A then B — FIFO order, but only one runs per event-loop iteration
```

> 💡 **Remember:** Tasks = a **deli ticket line** (FIFO). One customer served per loop pass.

---

### ⚡ Microtask Queue vs Macrotask Queue

**Definition:** Two separate queues with different priority — **microtasks** (`Promise.then`, `queueMicrotask`, `await` continuations, `MutationObserver`) drain **completely** after each task; **macrotasks** (`setTimeout`, `setInterval`, I/O, UI events) run **one at a time**.

```
                       ┌───────────────────────────┐
   each loop tick →    │  1. run ONE macrotask     │
                       │  2. drain ALL microtasks  │  ← full drain, incl. ones
                       │  3. (render) repeat       │     queued during the drain
                       └───────────────────────────┘

   MICROTASK  : Promise.then/.catch/.finally, await, queueMicrotask, MutationObserver
   MACROTASK  : setTimeout, setInterval, setImmediate(Node), I/O, message/UI events
```

| | Microtask | Macrotask |
|---|---|---|
| Examples | `.then`, `await`, `queueMicrotask` | `setTimeout`, `setInterval`, I/O |
| Drained | **ALL** before next macrotask | **ONE** per loop tick |
| Priority | Higher (runs first) | Lower |

```js
setTimeout(() => console.log("macro"), 0);      // macrotask
Promise.resolve().then(() => console.log("micro")); // microtask
console.log("sync");
// sync, micro, macro
```

> 💡 **Remember:** **"Micro before Macro"** (alphabetical = priority). Microtasks are *greedy* — they all drain before a single timer fires.

> ⚠️ **Gotcha:** An endlessly self-queuing microtask (`queueMicrotask` that re-queues itself) **starves** the macrotask queue and freezes rendering — the loop can't advance until microtasks are empty.

---

### 🔁 The Event Loop

**Definition:** The runtime loop that, while the call stack is empty, moves queued callbacks onto the stack — draining all microtasks, then running one macrotask, then repeating.

```
        ┌─────────────────────────────────────────────┐
        │                EVENT LOOP                    │
        │                                              │
        │   stack empty?                               │
        │      │ yes                                   │
        │      ▼                                       │
        │   ┌───────────────────────┐                  │
        │   │ drain ALL microtasks  │◄──┐ (incl. newly │
        │   └───────────┬───────────┘   │  queued ones)│
        │               │ empty         │              │
        │               ▼               │              │
        │   ┌───────────────────────┐   │              │
        │   │ run ONE macrotask     │───┘              │
        │   └───────────┬───────────┘                  │
        │               ▼                              │
        │            (render)                          │
        │               │                              │
        │               └──── loop ────────────────────┘
        └─────────────────────────────────────────────┘
```

**The one-sentence model:** *Stack empties → drain ALL microtasks → run ONE macrotask → (render) → repeat.*

> 💡 **Remember:** **"Empty, drain, one, repeat."** The loop never interrupts running sync code — it only acts when the stack is bare.

> ❓ **Interview — "Explain the event loop."**
> "JS is single-threaded with one call stack. Synchronous code runs on the stack to completion. Async operations are handed to host **Web APIs** (timers, fetch, DOM); when they complete, their callbacks are placed in a queue — **microtasks** (promises, await) or **macrotasks** (timers, I/O). The **event loop** waits for the stack to be empty, then **drains the entire microtask queue**, then runs **exactly one macrotask**, optionally renders, and repeats. That priority is why a resolved promise's `.then` always beats a `setTimeout(0)`. Technically the initial `<script>` run is itself the first macrotask, which is why the very first microtask drain happens right after top-level sync code finishes."

---

### ⏱️ async / await internals

**Definition:** `async`/`await` is syntactic sugar over promises — `await` **pauses** the function, returns control to the caller, and **resumes** the rest of the function as a **microtask** once the awaited promise settles.

```
  async function fn() {
     console.log("before await")   ──► runs synchronously NOW
     await something                ──► PAUSE, return to caller
     console.log("after await")     ──► resumes LATER as a MICROTASK
  }
        │
        ▼  everything after the first await ≈ wrapped in .then(...)
```

```js
async function run() {
  console.log("1: sync start");
  await null;                       // pause; schedule rest as microtask
  console.log("3: after await");    // microtask continuation
}
console.log("0: top");
run();
console.log("2: after run() call");
// 0, 1, 2, 3   ✅ verified
```

The code after the *first* `await` is equivalent to wrapping the remainder in a `.then` on the awaited value:
```js
console.log("1: sync start");
Promise.resolve(null).then(() => {
  console.log("3: after await");
});
```

> 💡 **Remember:** **`await` = `.then` in disguise.** Everything before the first `await` runs *now*; everything after runs *as a microtask*. "Await splits the function in two."

> ⚠️ **Trap:** `await null` / `await <non-promise>` costs **one** microtask tick. But `await <a real promise>` can cost **extra** ticks (the engine adapts a thenable through one or more internal microtask hops). So in head-to-head ordering puzzles, a chain of `await realPromise` may log *later* than an equivalent chain of plain `.then`. When in doubt, count microtask hops, don't assume one.

---

### 🎯 ❓ Why does `Promise.then` run before `setTimeout(0)`?

Because they live in **different queues with different priority**. After each macrotask (and after the initial sync script, which is itself the first macrotask), the event loop **fully drains the microtask queue before touching the next macrotask**. `Promise.then` → microtask (drained immediately). `setTimeout(0)` → macrotask (waits for the next tick). The `0` ms is a *minimum* delay, not a guarantee — microtasks always cut the line. (Browsers also clamp nested timers to ~4 ms after 5 levels, so `setTimeout(0)` is never truly 0.)

```js
setTimeout(() => console.log("timeout"), 0);  // macrotask
Promise.resolve().then(() => console.log("promise")); // microtask
// promise, then timeout — every time   ✅ verified
```

> 💡 **Remember:** Micro = "do it the moment I'm free." Macro = "do it next tick." Free happens before next tick.

---

### 🧩 Worked snippet — predict the exact log order

```js
console.log("A");                                  // 1 sync

setTimeout(() => console.log("B"), 0);             // macrotask

Promise.resolve().then(() => console.log("C"));    // microtask

(async () => {
  console.log("D");                                // sync (before first await)
  await null;
  console.log("E");                                // microtask continuation
})();

queueMicrotask(() => console.log("F"));            // microtask

console.log("G");                                  // sync
```

**Step through it:**

```
SYNC PHASE (the initial script = first macrotask):
  A            → log
  setTimeout   → B queued to MACRO
  Promise.then → C queued to MICRO
  IIFE runs    → D logged sync; await null pauses; E queued to MICRO
  queueMicrotask → F queued to MICRO
  G            → log
  → sync done. Logged so far: A, D, G

DRAIN ALL MICROTASKS (FIFO, in the order queued): C, E, F
  → C, E, F

NEXT MACROTASK: B
  → B
```

**Output:**
```
A
D
G
C
E
F
B
```
✅ *Verified in Node — actual output is exactly `A D G C E F B`.*

> 💡 **Remember the recipe to ace any ordering question:**
> 1. **All synchronous logs first**, top to bottom.
> 2. **Then every microtask** (`.then`, `await`-continuation, `queueMicrotask`) in queue order.
> 3. **Then macrotasks** (`setTimeout`) one per tick.
> Mantra: **"Sync → Micro → Macro."**

> 🏦 **Banking-front-end angle:** Microtasks starving the loop = a frozen UI on an account dashboard; never busy-loop or chain unbounded promise work on the main thread. Defer heavy work (Web Workers / chunked `setTimeout`) so input handlers and rendering — the *macrotask*-level responsiveness customers feel — stay snappy and accessible.

---

## ⚡ JavaScript — Promises & async/await

A **Promise** is an object representing the eventual result of an asynchronous operation. Think of it as a "receipt" you get immediately, redeemable later for a value or an error.

---

### 1. Promise states (pending → fulfilled / rejected, settled)

**Definition:** A promise lives in exactly one of three states; once it leaves `pending` it is **settled** and can never change again (immutable).

```
            ┌─────────────┐
            │   PENDING   │   (initial, no value yet)
            └──────┬──────┘
        resolve()  │  reject()
        ┌──────────┴──────────┐
        ▼                     ▼
 ┌─────────────┐       ┌─────────────┐
 │  FULFILLED  │       │  REJECTED   │
 │ (has value) │       │ (has reason)│
 └─────────────┘       └─────────────┘
        └────── SETTLED ──────┘   (locked, immutable)
```

```js
const p = new Promise((resolve, reject) => {
  const ok = true;
  if (ok) resolve("account loaded");    // → fulfilled
  else    reject(new Error("timeout")); // → rejected
});

p.then(v => console.log(v))            // runs on fulfill
 .catch(e => console.error(e.message)) // runs on reject
 .finally(() => console.log("done"));  // runs either way
```

💡 **Remember:** **"PFR + S"** — **P**ending starts it, **F**ulfilled or **R**ejected ends it, both mean **S**ettled. A settled promise is **frozen** — calling resolve/reject again is a no-op.

❓ *Settled vs resolved?* "Settled" = fulfilled OR rejected (final). "Resolved" is looser — a resolved promise has had its fate **locked in**, but if you resolve it *to another pending promise* it stays pending (and may still end up rejected) until that inner one settles. So "resolved" ≠ "fulfilled."

---

### 2. Promise chaining & return-value flattening

**Definition:** `.then()` returns a **new** promise; whatever you `return` inside becomes the next `.then`'s input — and if you return a thenable/promise, it gets **flattened** (auto-awaited), not nested.

```
.then(A) ──returns x──▶ .then(B gets x) ──returns Promise<y>──▶ .then(C gets y)
                                              ▲ flattened, not Promise<Promise<y>>
```

```js
fetch("/api/account/123")
  .then(res => res.json())          // returns a Promise → flattened
  .then(account => account.balance) // returns a plain number
  .then(balance => console.log(balance))
  .catch(err => console.error("chain failed:", err));
```

💡 **Remember:** **"Return to pass the baton."** Each `.then` hands its return value to the next. Forget to `return` and the next `.then` gets `undefined` — the #1 chaining bug.

---

### 3. Error propagation down a chain

**Definition:** A thrown error or rejection **skips all `.then`s** and jumps to the nearest `.catch` (or rejection handler) downstream; after `catch` returns a value, the chain **resumes** fulfilled.

```
.then ──✗ throws──▶ (skip) .then ──▶ (skip) .then ──▶ .catch  ✅ handled
                                                          │
                                                          ▼ chain continues fulfilled
```

```js
doStep1()
  .then(() => { throw new Error("step2 broke"); })
  .then(() => console.log("SKIPPED — never runs"))
  .catch(err => {
    console.error("caught:", err.message); // caught: step2 broke
    return "recovered";                    // chain becomes fulfilled again
  })
  .then(v => console.log(v));               // logs: recovered
```

💡 **Remember:** **"One catch to rule them all"** — a single trailing `.catch` covers every `.then` above it. Errors fall *down* the chain like water until something catches them. (But a `.catch` only catches errors from steps **above** it, not from `.then`s placed after it.)

❓ *Where to put `.catch`?* At the **end** of the chain to catch everything; add inline `.catch` mid-chain only if you want to recover and continue. Note: if your `.catch` itself throws (or returns a rejected promise), the chain goes rejected again and needs a further downstream handler.

---

### 4. `Promise.all` — all succeed or first reject

**Definition:** Waits for **every** promise to fulfill and returns an array of results **in input order**; if **any** rejects, `all` rejects immediately with that first error (fail-fast).

```
all([p1, p2, p3])
  p1 ✅──┐
  p2 ✅──┼──▶ [r1, r2, r3]   (all fulfilled, input order)
  p3 ✅──┘

  p1 ✅
  p2 ✗ ──────▶ REJECT (first rejection wins; p1/p3 keep running, results discarded)
  p3 …
```

```js
const [user, accounts, cards] = await Promise.all([
  fetch("/api/user").then(r => r.json()),
  fetch("/api/accounts").then(r => r.json()),
  fetch("/api/cards").then(r => r.json()),
]);
// All three run IN PARALLEL; one failure rejects the whole thing.
```

💡 **Remember:** **"ALL or nothing, fail-FAST."** Order of *results* is by **input position**, not completion time. Gotcha: a rejection doesn't *cancel* the other promises — they still run, their results are just thrown away (and an unhandled rejection from a later one can warn).

---

### 5. `Promise.allSettled` — never rejects

**Definition:** Waits for **all** to settle and **always fulfills** with an array (in input order) of `{status:'fulfilled', value}` or `{status:'rejected', reason}` — perfect when you want every result regardless of individual failures.

```
allSettled([p1, p2, p3])  ──▶ always fulfills:
[
 {status:'fulfilled', value: r1},
 {status:'rejected',  reason: e2},
 {status:'fulfilled', value: r3},
]
```

```js
const results = await Promise.allSettled([
  fetch("/api/balance"),
  fetch("/api/transactions"),
  fetch("/api/rewards"),
]);

results.forEach(r => {
  if (r.status === "fulfilled") render(r.value);
  else logError(r.reason);   // one widget failing doesn't kill the dashboard
});
```

💡 **Remember:** **"allSettled = settle for whatever you get."** Never throws → no `.catch` needed. Banking dashboards love it: show the accounts that loaded, flag the ones that didn't.

❓ *all vs allSettled?* `all` is fail-fast (one error aborts, gives you raw values); `allSettled` is fail-soft (waits for everyone, wraps each in a `{status}` object, never rejects).

---

### 6. `Promise.race` — first to settle

**Definition:** Settles as soon as the **first** promise settles — fulfilled **or** rejected — adopting its value/error and ignoring the rest.

```
race([p1, p2])
  p1 ───────✅ (250ms) ──▶ race FULFILLS with r1
  p2 ──✗ (100ms) ─────────▶ race REJECTS with e2  ← whichever is FIRST, win or lose
```

(Here p2 settles first at 100ms, so the race **rejects** with e2 — being first is all that matters, even if it's a rejection.)

```js
function withTimeout(promise, ms) {
  const timeout = new Promise((_, reject) =>
    setTimeout(() => reject(new Error("Request timed out")), ms)
  );
  return Promise.race([promise, timeout]); // whoever finishes first
}

await withTimeout(fetch("/api/transfer"), 5000); // reject if >5s
```

💡 **Remember:** **"race = first PAST the post, win or crash."** Classic use: bolt a timeout onto any slow request. (Caveat: the loser isn't cancelled — `fetch` keeps going; pair with `AbortController` if you need to actually stop it.)

❗ *Edge case:* `Promise.race([])` (empty array) stays **pending forever** — same for `Promise.any([])`, which rejects immediately with an `AggregateError`.

---

### 7. `Promise.any` — first to FULFILL

**Definition:** Fulfills with the **first promise that fulfills**, ignoring rejections; only rejects if **all** fail — and then with an **`AggregateError`** whose `.errors` array bundles every reason in input order.

```
any([p1, p2, p3])
  p1 ✗
  p2 ✅ ──────▶ any FULFILLS with r2  (ignores rejections before/after)
  p3 ✗

  ALL reject ──▶ REJECT with AggregateError { errors: [e1, e2, e3] }
```

```js
try {
  const fastest = await Promise.any([
    fetch("https://mirror1.bank.com/rates"),
    fetch("https://mirror2.bank.com/rates"),
    fetch("https://mirror3.bank.com/rates"),
  ]);
  // first mirror that fulfills wins
} catch (err) {
  console.error(err);                  // AggregateError
  console.error(err.errors);           // [e1, e2, e3] — all the reasons
}
```

💡 **Remember:** **"any = first WINNER" vs race = first FINISHER.** `any` ignores losers (rejections) and waits for a success; `race` reacts to whoever settles *first*, win or lose. Mnemonic: **A**ny **A**ccepts only success → **A**ggregateError if none.

---

### Combinators cheat-sheet

```
┌──────────────┬───────────────────────┬─────────────────────────────┐
│ Combinator   │ Fulfills when…        │ Rejects when…               │
├──────────────┼───────────────────────┼─────────────────────────────┤
│ all          │ ALL fulfill           │ ANY rejects (fail-fast)     │
│ allSettled   │ ALL settle (always)   │ never                       │
│ race         │ first SETTLES (✅)     │ first SETTLES (✗)           │
│ any          │ first FULFILLS        │ ALL reject → AggregateError │
└──────────────┴───────────────────────┴─────────────────────────────┘
```

💡 **Remember:** **"all = AND, any = OR, race = FIRST, allSettled = REPORT."**

❓ **Promise.all vs race?** `all` waits for **every** promise and gives you **all results** in input order (rejects if any one fails); `race` returns the moment the **first** promise settles — win or lose — giving you a single result/error. Use `all` for "I need everything"; `race` for "I need the quickest / add a timeout."

---

### 8. `async`/`await` & try/catch

**Definition:** `async` makes a function always return a promise; `await` pauses *inside* that function until a promise settles, unwrapping its value (or throwing its rejection) — letting you write async code that *reads* synchronously.

```js
async function loadDashboard(userId) {
  try {
    const user  = await getUser(userId);    // unwraps the resolved value
    const accts = await getAccounts(user);  // throws here if it rejects
    return { user, accts };                 // wrapped in a Promise automatically
  } catch (err) {
    showBanner("Could not load dashboard");  // catches ANY await above
    throw err;                               // re-throw to let caller know
  } finally {
    hideSpinner();                           // runs whether we resolved or threw
  }
}
```

💡 **Remember:** **"`await` = unwrap; `try/catch` = the async `.catch`."** A rejected awaited promise behaves exactly like a thrown error — so wrap awaits in `try/catch`. Gotcha: a bare `await somePromise()` *without* try/catch (or a `.catch` on the call) surfaces as an **unhandled rejection**.

❓ *Does `await` block the whole thread?* **No.** It only suspends the *async function*; the engine returns to the event loop and is free to run other code. It's non-blocking, unlike a busy-wait loop.

---

### 9. Parallel vs sequential awaits

**Definition:** Sequential `await`s run one-after-another (each waits for the previous); to run independent tasks **concurrently**, start them first (or use `Promise.all`) and await together.

```
SEQUENTIAL (slow):   getA ───▶ getB ───▶ getC      total ≈ a + b + c
                     await    await     await

PARALLEL (fast):     getA ──┐
                     getB ──┼──▶ Promise.all       total ≈ max(a, b, c)
                     getC ──┘
```

```js
// ❌ SEQUENTIAL — 3 round-trips back to back (≈ sum of all)
const a = await getA();
const b = await getB();   // didn't even start until A finished
const c = await getC();

// ✅ PARALLEL — all fire immediately, await together (≈ slowest one)
const [a, b, c] = await Promise.all([getA(), getB(), getC()]);

// Subtle: this ALSO runs in parallel — the promises START before the awaits
const pA = getA(), pB = getB(), pC = getC();   // all kicked off now
const a2 = await pA, b2 = await pB, c2 = await pC;
```

💡 **Remember:** **"Independent? Fire first, await later."** Only chain sequentially when B genuinely *needs* A's result. The difference between a 900ms and a 300ms dashboard. (Note: `Promise.all` is fail-fast — if you need every result even on partial failure, use `allSettled`.)

❓ *When is sequential correct?* When there's a true data dependency (e.g., you need the `userId` from step 1 to fetch accounts in step 2).

---

### 10. How async/await works internally (the microtask queue)

**Definition:** `async`/`await` is **syntactic sugar over promises**: at each `await`, the function *pauses*, the rest of its body is registered as a continuation (effectively a `.then` callback on the awaited promise), and control returns to the caller/event loop; when the awaited promise settles, that continuation is queued on the **microtask queue** and resumed.

**The event loop — microtasks drain *before* the next macrotask:**

```
        ┌─────────────── EVENT LOOP ───────────────┐
        │                                           │
 [ Call Stack ] ──empties──▶ drain ALL microtasks   │
                              (Promises, await,      │
                               queueMicrotask)       │
                                    │                │
                                    ▼                │
                            run ONE macrotask        │
                            (setTimeout, I/O,         │
                             UI event) ──────────────┘
```

```js
console.log("1: sync start");

setTimeout(() => console.log("4: macrotask (setTimeout)"), 0);

Promise.resolve().then(() => console.log("3: microtask (promise)"));

console.log("2: sync end");

// Output order:
// 1: sync start
// 2: sync end
// 3: microtask (promise)   ← microtasks flush BEFORE timers
// 4: macrotask (setTimeout)
```

**What `await` really desugars to:**

```js
// This async function...
async function f() {
  const x = await getX();
  return x + 1;
}

// ...is roughly equivalent to:
function f() {
  return Promise.resolve(getX()).then(x => x + 1); // continuation runs as a microtask
}
```

A more complete picture: the body is compiled into a **state machine** — each `await` is a resumption point, and the engine attaches the "next chunk" as a microtask callback on the awaited promise. (This is also why even `await` of an already-resolved value still defers the rest of the function by *at least one microtask tick*, rather than running it synchronously.)

💡 **Remember:** **"`await` = `.then` in disguise; its continuation is a MICROTASK, and microtasks jump the queue ahead of `setTimeout`."** Promises/await ALWAYS run before timers — that's why promise callbacks feel "instant."

❓ **How does async/await work internally?** It's **sugar over promises**. The engine transforms the function into a **state machine**: each `await` splits the function, attaches the remainder as a continuation (a `.then`-style callback) on the awaited promise, and returns control to the event loop. When the promise settles, that continuation is scheduled on the **microtask queue** — which drains completely after the current call stack and *before* any macrotask (`setTimeout`, I/O, rendering). So `await` never blocks the thread; it just defers the rest of the function to a microtask.

---

### 🎯 One-look recap

```
States:     pending → (fulfilled | rejected) = settled, immutable
Chaining:   .then returns a new promise; return value flows down; promises flatten
Errors:     fall down to nearest .catch / try-catch around await
all → AND (fail-fast)   any → OR (first FULFILL, else AggregateError)
race → FIRST to settle  allSettled → REPORT all outcomes (never rejects)
await:      unwraps a promise; pauses the async fn only; continuation = MICROTASK
Perf:       independent tasks → Promise.all (parallel), not back-to-back awaits
Microtasks: drain fully after the stack, BEFORE setTimeout/render macrotasks
```

💡 **Final mnemonic:** **"Promises settle once; `.then` passes the baton; errors fall to `catch`; `all`/`any`/`race`/`allSettled` = AND/OR/FIRST/REPORT; `await` is a `.then` that runs as a microtask."**

---

## 🧠 JavaScript — Closures, `this`, Hoisting

---

### 🔒 Closure: Lexical Scope

**Definition:** A closure is a function that *remembers and accesses* the variables from the scope where it was **defined** (its lexical scope), even after the outer function has returned.

```
outer() called, returns inner ──┐
                                 │  outer's stack frame is GONE
   ┌─────────────────────────┐   │  but `count` survives because
   │ outer() scope           │   │  `inner` still references it.
   │   count = 0  ◄──────────┼───┼──┐
   │   ┌──────────────────┐  │   │  │  (closed-over variable)
   │   │ inner() {        │  │   │  │
   │   │   count++  ──────┼──┼───┼──┘
   │   │ }                │  │   │
   │   └──────────────────┘  │   │
   └─────────────────────────┘   │
        returned ────────────────┘
```

```js
function makeCounter() {
  let count = 0;                 // private, lives in the closure
  return function () {
    count++;
    return count;
  };
}
const c = makeCounter();
c(); // 1
c(); // 2  -> `count` persisted between calls
```

**💡 Remember:** *"A closure is a function + its backpack of variables."* The function carries the backpack everywhere, even after the outer function dies.

---

### 🎯 Closure Use Cases

**Definition:** Closures power private state, rate-limiters, and React Hooks — anywhere a function needs to "hold on to" data across calls.

#### 1. Private variables (Module Pattern)

**Definition:** Wrap state in a closure so it's unreachable from outside — only the returned methods can touch it.

```js
const bankAccount = (function () {
  let balance = 0;                    // truly private — no outside access
  return {
    deposit(amt) { balance += amt; return balance; },
    withdraw(amt) {
      if (amt > balance) throw new Error("Insufficient funds");
      balance -= amt; return balance;
    },
    getBalance() { return balance; },
  };
})();
bankAccount.deposit(100); // 100
bankAccount.balance;      // undefined — encapsulated 🔒
```

**💡 Remember:** *IIFE + return object = a vault.* The variables inside are the safe; the returned methods are the only keys.

#### 2. Debounce

**Definition:** Debounce delays running a function until the user **stops** triggering it for N ms — only the *last* call in a burst wins.

```
calls:   x x x x        x
              └─ wait ─►│ fires once (trailing edge)
                        ▼
fired:                  ●
```

```ts
function debounce<T extends (...a: any[]) => void>(fn: T, delay = 300) {
  let timer: ReturnType<typeof setTimeout>;
  return (...args: Parameters<T>) => {
    clearTimeout(timer);                 // timer is closed-over
    timer = setTimeout(() => fn(...args), delay);
  };
}

// Banking use: validate account number only after user stops typing
const validate = debounce((acct: string) => api.check(acct), 400);
```

**💡 Remember:** **Debounce = "wait for quiet."** Like an elevator door — every new person resets the close timer; it shuts only after everyone stops entering.

#### 3. Throttle

**Definition:** Throttle runs a function **at most once per N ms**, no matter how often it's triggered — guarantees a steady rate.

```
calls:   x x x x x x x x x x x x
         │       │       │
fired:   ●───────●───────●        (every N ms, leading edge)
```

```ts
function throttle<T extends (...a: any[]) => void>(fn: T, limit = 300) {
  let inThrottle = false;
  return (...args: Parameters<T>) => {
    if (!inThrottle) {                  // inThrottle is closed-over
      fn(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
}

// Banking use: throttle scroll/resize handlers on a transactions dashboard
window.addEventListener("scroll", throttle(onScroll, 200));
```

**💡 Remember:** **Throttle = "every N ms, guaranteed."** Like a metronome — it ticks at a fixed beat regardless of how wildly you wave. *Debounce waits, Throttle paces.*

#### 4. React Hooks internals (`useState` closes over state)

**Definition:** Each render creates fresh functions that *close over* that render's state value — which is why stale closures happen and why functional updates fix them.

```jsx
function Counter() {
  const [count, setCount] = useState(0);

  // ❌ Stale closure: this `count` is frozen at the render it was created
  const badIncrement = () => setCount(count + 1);

  // ✅ Functional update: React passes the latest value — no closure-over-stale-state
  const goodIncrement = () => setCount(c => c + 1);

  useEffect(() => {
    const id = setInterval(() => setCount(c => c + 1), 1000);
    return () => clearInterval(id);
  }, []); // empty deps → callback closes over render-0 state; functional update saves us

  return <button onClick={goodIncrement}>{count}</button>;
}
```

**💡 Remember:** *Each render is a photograph; its functions see only that snapshot.* Use the **updater function** (`c => c + 1`) to read the live value instead of the snapshot.

---

### 👉 `this` — Binding Rules

**Definition:** `this` is determined by **how a function is called** (its call-site), not where it's defined — except arrow functions, which inherit `this` lexically.

```
HOW is the fn called?            →  what is `this`?
──────────────────────────────────────────────────
fn()           (plain call)      →  undefined (strict) / global (sloppy)
obj.fn()       (method call)     →  obj   (left of the dot)
new Fn()       (constructor)     →  the new instance
fn.call/apply  (explicit)        →  whatever you pass
() => {}       (arrow)           →  inherited from enclosing scope
```

#### Global context & plain function call

**Definition:** A bare `fn()` has no owner — `this` is `undefined` in strict mode (and ES modules), or the global object (`window`/`globalThis`) in sloppy mode.

```js
"use strict";
function show() { return this; }
show();        // undefined  (strict / module)
// non-strict: window (browser) / globalThis (Node)
```

**💡 Remember:** **"No dot, no owner."** If nothing is to the *left of the dot* at the call-site, `this` falls back to undefined/global.

#### Object method

**Definition:** When called as `obj.method()`, `this` is the object **left of the dot**.

```js
const acct = {
  owner: "Divakar",
  greet() { return `Hi ${this.owner}`; },
};
acct.greet();                 // "Hi Divakar"

const fn = acct.greet;        // detached from the dot!
fn();                         // "Hi undefined" — lost `this`
                              // (strict: throws — `this` is undefined; sloppy: "Hi undefined")
```

**💡 Remember:** *`this` is whoever is left of the dot — at call time, not definition time.* Detach it and it forgets who it belongs to.

#### Arrow function (lexical `this`)

**Definition:** Arrow functions have **no own `this`** — they capture `this` from the surrounding scope at definition, permanently.

```js
const timer = {
  seconds: 0,
  start() {
    // arrow's `this` = whatever `this` is inside start().
    // Called as timer.start(), so `this` = timer ✅
    setInterval(() => { this.seconds++; }, 1000);
  },
  startBad() {
    // plain fn → its own `this` at call time = undefined (strict) / window (sloppy) ❌
    setInterval(function () { this.seconds++; }, 1000);
  },
};
```

**💡 Remember:** **Arrows inherit, they don't reset.** An arrow has no `this` of its own — it borrows its parent's. Great for callbacks; never use as object methods or constructors.

#### `bind` / `call` / `apply`

**Definition:** Three ways to set `this` explicitly — `call` passes args one-by-one, `apply` passes an args array, `bind` returns a *new function* with `this` permanently fixed.

```
call(thisArg, a, b, c)     → invokes NOW, args as a LIST
apply(thisArg, [a, b, c])  → invokes NOW, args as an ARRAY
bind(thisArg, a, b)        → returns NEW fn, call LATER
```

```js
function intro(greeting, punct) {
  return `${greeting}, ${this.name}${punct}`;
}
const user = { name: "Divakar" };

intro.call(user, "Hi", "!");          // "Hi, Divakar!"   — args list
intro.apply(user, ["Hi", "!"]);       // "Hi, Divakar!"   — args array
const bound = intro.bind(user, "Hi"); // returns a new fn, `this` locked
bound("!");                           // "Hi, Divakar!"
```

**💡 Remember:** **"C for Comma, A for Array, B for Bind-it-for-later."** Call = Comma-separated, Apply = Array, Bind = Bound copy you invoke later. (Mnemonic: *Call → Commas, Apply → Array*.)

#### ❓ Interview: *Why do arrow functions behave differently?*

> Arrow functions **don't have their own `this`** (nor `arguments`, `super`, or `new.target`). Instead they capture `this` **lexically** — from the enclosing scope at the moment they're defined — and it can never be reassigned, not even with `call`/`apply`/`bind`. Regular functions get a *fresh* `this` decided dynamically by the call-site each time they run. This makes arrows ideal for callbacks (e.g. inside `setTimeout`, `.map`, event handlers in a class) where you want to keep the outer `this`, but **unsuitable as object methods or constructors** (you can't `new` an arrow — it throws `TypeError`).

**💡 Remember:** *Regular fn: `this` is set at call-time (dynamic). Arrow fn: `this` is set at write-time (lexical, frozen).*

---

### 🏗️ Hoisting

**Definition:** Hoisting is JS moving declarations to the top of their scope during compilation — but **how** they're initialized differs by keyword.

```
Declaration type        Hoisted?   Initialized to?        Usable before line?
────────────────────────────────────────────────────────────────────────────
var                     ✅ yes     undefined              yes (but undefined)
function declaration    ✅ yes     the full function      yes (fully callable)
let / const             ✅ yes     <nothing> → TDZ        NO → ReferenceError
class                   ✅ yes     <nothing> → TDZ        NO → ReferenceError
```

#### `var` — hoisted, initialized `undefined`

```js
console.log(x); // undefined  (declaration hoisted, assignment not)
var x = 5;
console.log(x); // 5
```

#### Function declarations — fully hoisted

```js
greet();                       // "hello" ✅ works before definition
function greet() { return "hello"; }

// ⚠️ Function EXPRESSIONS are NOT fully hoisted (only the var binding is):
sayHi();                       // TypeError: sayHi is not a function
var sayHi = function () {};    // only `var sayHi` is hoisted (= undefined)
```

#### `let` / `const` — hoisted but in the TDZ

```js
console.log(y); // ❌ ReferenceError: Cannot access 'y' before initialization
let y = 10;
```

**💡 Remember:** **VFL = `var`→undefined, `function`→Full, `let/const`→Locked (TDZ).** Only `var` and function *declarations* are safe to use before their line.

#### ❓ Interview: *What is the TDZ (Temporal Dead Zone)?*

> The **Temporal Dead Zone** is the span between when a `let`/`const`/`class` binding is **hoisted** (enters scope at the top of the block) and when it's **actually initialized** (its declaration line runs). The variable *exists* but is uninitialized; any access during this window throws `ReferenceError: Cannot access 'x' before initialization`. This is deliberate — it catches use-before-declaration bugs that `var`'s silent `undefined` would hide. (Note: `typeof` doesn't rescue you either — `typeof y` in the TDZ also throws, unlike `typeof` on a truly undeclared variable, which returns `"undefined"`.)

```
┌──── block scope starts ────────────────────────────┐
│  let z is hoisted here  ──┐                          │
│                           │  ⚠️ TDZ                   │
│   console.log(z) ───► ReferenceError                 │
│                           │                          │
│  let z = 1;  ◄────────────┘  TDZ ENDS here (declared)│
│   console.log(z) ───► 1  ✅                           │
└─────────────────────────────────────────────────────┘
```

**💡 Remember:** *TDZ = "hoisted but handcuffed."* The name is reserved from the top of the block, but you're forbidden to touch it until its declaration line runs — `var` says `undefined`, `let`/`const` say *"too early, error."*

---

### ⚡ One-Glance Recap

| Concept | One-liner |
|---|---|
| **Closure** | Function + backpack of outer variables, survives the outer return |
| **Debounce** | Wait for quiet — fires after the burst stops |
| **Throttle** | Steady beat — fires at most once per N ms |
| **`this` plain call** | `undefined` (strict) — no dot, no owner |
| **`this` method** | Left of the dot, at call time |
| **Arrow `this`** | Lexical & frozen — inherited from parent scope |
| **call / apply / bind** | Comma list / Array / Bound-later |
| **var hoist** | Hoisted as `undefined` |
| **function hoist** | Fully hoisted, callable early |
| **let/const hoist** | Hoisted but TDZ-locked → ReferenceError |
| **TDZ** | Hoisted but handcuffed until its declaration line |

---

## 🧠 JavaScript — Memory, Prototypes, Operators

---

### Stack vs Heap

**Definition:** The **stack** holds primitives, references (pointers), and call frames in fixed-size LIFO order; the **heap** is the large unordered region where objects/arrays/functions live.

```
  STACK (fast, fixed-size, LIFO)        HEAP (large, dynamic)
  ┌──────────────────────────┐          ┌────────────────────────┐
  │ name  = "Sam"  (prim)    │          │  {0x1A}  { age: 30 }   │
  │ age   = 30     (prim)    │          │  {0x2B}  [1, 2, 3]     │
  │ user  ── 0x1A ───────────┼────────► │                        │
  │ nums  ── 0x2B ───────────┼────────► │                        │
  └──────────────────────────┘          └────────────────────────┘
     variable -> reference                  actual object data
```

```js
let age = 30;            // primitive value sits ON the stack
let user = { age: 30 };  // 'user' holds a reference (on stack) -> object on heap
let nums = [1, 2, 3];    // 'nums' -> array on heap
```

**Call frames:** each function invocation pushes a frame (params + locals); returning pops it.

```js
function a() { return b(); }   // frame a pushed -> calls b
function b() { return 42; }    // frame b pushed -> returns, popped -> a popped
```

💡 **Remember:** *"Primitives Park on the stack; Objects are an Overflow to the heap."* Stack = small & ordered, Heap = big & messy.

❓ *Why are primitives on the stack and objects on the heap?* Primitives are fixed-size, so they fit in the stack's tidy slots; objects can grow arbitrarily, so they live on the heap and the stack only keeps a fixed-size pointer to them. (Engine reality: V8 may keep small values in registers or optimize layout, but the stack/heap model is the right mental picture.)

---

### Garbage Collection (mark-and-sweep, reachability)

**Definition:** GC automatically frees heap memory by finding objects no longer **reachable** from a set of roots and reclaiming them.

```
  ROOTS (globals, current call stack, closures)
    │
    ▼
  [user] ──► {profile} ──► {address}      ✅ reachable — KEPT

  {orphan} ──► {temp}                       ❌ unreachable — SWEPT (freed)
```

**Mark-and-sweep:** start at roots → **mark** everything reachable → **sweep** (free) everything unmarked. Note: *unreachable ≠ zero references* — two objects referencing each other but detached from roots are still collected (modern GCs handle cycles; old reference-counting did not).

```js
let user = { name: "Sam" };
let admin = user;   // object has 2 references
user = null;        // still reachable via 'admin' -> KEPT
admin = null;       // now unreachable from any root -> eligible for GC
```

Common leak in React — a listener/timer keeps a closure alive:

```js
useEffect(() => {
  const id = setInterval(tick, 1000);
  return () => clearInterval(id); // cleanup → frees closure, prevents leak
}, []);
```

💡 **Remember:** *"Mark what you can reach, sweep what you can't."* GC cares about **reachability**, not reference counts.

❓ *What causes a memory leak in JS if GC is automatic?* Lingering reachability — forgotten timers/listeners, detached DOM nodes held by a variable, or ever-growing global caches/closures keep objects reachable so GC never frees them.

---

### Reference vs Value

**Definition:** Primitives are **copied by value** (each name gets an independent copy); objects copy the **reference value** — both names hold a pointer to the *same* heap object. (JS never copies the object itself on assignment; it copies the pointer.)

```
  COPY A PRIMITIVE (value)            COPY AN OBJECT (the reference value)
  a = 1                               x ──► {n:1}
  b = a   (independent copy)          y = x   (same arrow!)
  ┌─────┐   ┌─────┐                   ┌─────┐
  │ a=1 │   │ b=1 │                   │ x ──┼──► {n:1} ◄──┐
  └─────┘   └─────┘                   │ y ──┼────────────┘
  change b → a UNAFFECTED             mutate via y → x SEES it
```

```js
// VALUE — primitives copy
let a = 1;
let b = a;
b = 99;
console.log(a); // 1  (a untouched)

// REFERENCE VALUE — objects share the same target
let x = { n: 1 };
let y = x;
y.n = 99;
console.log(x.n); // 99  (same object!)

// Reassigning the binding does NOT affect the other
y = { n: 0 };
console.log(x.n); // 99  (x still points to the original)
```

Function args follow the same rule — and this is *the* React state bug:

```js
// ❌ Mutation — same reference, React skips re-render (Object.is sees no change)
state.items.push(newItem); setState(state.items);
// ✅ New reference — React detects the change
setState([...state.items, newItem]);
```

💡 **Remember:** *"Primitives copy the **value**, objects copy the **arrow**."* (the pointer, not the data)

❓ *Does JS pass by reference?* No — JS is **always pass-by-value**; for objects the *value being copied is the reference*. So you can mutate the object through it, but reassigning the parameter never affects the caller.

---

### Prototype System: `__proto__` vs `prototype`

**Definition:** `prototype` is a property on **constructor functions** (the blueprint for instances); `__proto__` is the actual link **on an instance** pointing to its prototype.

```
        Dog (constructor function)
         │  .prototype
         ▼
   ┌─────────────────────┐
   │ Dog.prototype       │◄───────┐
   │   bark()            │        │ __proto__
   └─────────────────────┘        │
                            ┌──────┴──────┐
   rex = new Dog()  ───────►│ rex {name}  │
                            └─────────────┘
   rex.__proto__ === Dog.prototype   // true
```

```js
function Dog(name) { this.name = name; }
Dog.prototype.bark = function () { return `${this.name} woof`; };

const rex = new Dog("Rex");
rex.bark();                            // "Rex woof"
rex.__proto__ === Dog.prototype;       // true
Object.getPrototypeOf(rex) === Dog.prototype; // true (preferred over __proto__)
```

💡 **Remember:** *"`prototype` lives on the **F**unction (the **F**actory); `__proto__` lives on the **I**nstance (the **I**tem)."*

❓ *Difference between `prototype` and `__proto__`?* `prototype` is the object a constructor function attaches to instances it builds; `__proto__` is the per-instance pointer to that object. So `rex.__proto__ === Dog.prototype`. (`__proto__` is a legacy accessor — prefer `Object.getPrototypeOf`/`Object.setPrototypeOf`.)

---

### The Prototype Chain & Lookup

**Definition:** Property lookup walks up the `__proto__` chain — own property first, then each prototype, until found or it hits `null`.

```
  rex ──__proto__──► Dog.prototype ──__proto__──► Object.prototype ──► null
  │                  │                            │
  name (own)         bark()                       toString(), hasOwnProperty()

  rex.toString() → not on rex → not on Dog.prototype → found on Object.prototype ✅
  rex.fly()      → walks whole chain → null → undefined → TypeError (calling undefined)
```

```js
rex.name;          // own property — stop immediately
rex.bark();        // found on Dog.prototype
rex.toString();    // found on Object.prototype
rex.hasOwnProperty("name"); // true — only own props
"bark" in rex;              // true — 'in' checks the whole chain
```

💡 **Remember:** *"Lookup goes **UP** the chain, never down — own → proto → proto → null."* Ends in `null`, returns `undefined` if not found.

❓ *What happens when you access a property not on the object?* JS walks the prototype chain link by link; if no link has it (chain ends at `null`), the read yields `undefined` — and *calling* that `undefined` is what throws the `TypeError`.

---

### Constructor Functions

**Definition:** A regular function called with `new` that builds and returns a fresh object, with `this` bound to that new instance.

```
  new Dog("Rex")  performs 4 steps:
  1. create {}                 ── new empty object
  2. {}.__proto__ = Dog.prototype
  3. call Dog with this = {}   ── assigns this.name
  4. return this               ── unless ctor returns its OWN object
                                  (returning a primitive is ignored)
```

```js
function Dog(name) {
  // this = {} created automatically; __proto__ wired to Dog.prototype
  this.name = name;
  // return this; (implicit)
}
const rex = new Dog("Rex"); // rex = { name: "Rex" }

// Forgetting 'new' → in strict mode 'this' is undefined → throws on this.name.
// (Non-strict, 'this' is the global object → silent bug.) Classes always throw.
```

💡 **Remember:** *"`new` does 4 things: **C**reate, **L**ink, **B**ind-and-run, **R**eturn"* (CLBR ≈ "Caliber").

❓ *What does the `new` keyword do?* Creates an empty object, links its `__proto__` to the constructor's `prototype`, calls the constructor with `this` = that object, and returns it — unless the constructor explicitly returns its **own object** (a returned primitive is ignored).

---

### Prototypal Inheritance

**Definition:** Objects inherit directly from other objects by chaining prototypes, so a child's prototype's `__proto__` points to the parent's prototype.

```
  puppy ──► Puppy.prototype ──► Dog.prototype ──► Object.prototype ──► null
            (whine)             (bark)
  puppy.bark()  → not on Puppy.prototype → found on Dog.prototype ✅
```

```js
function Dog(name) { this.name = name; }
Dog.prototype.bark = function () { return "woof"; };

function Puppy(name) { Dog.call(this, name); }      // 1. inherit instance props
Puppy.prototype = Object.create(Dog.prototype);     // 2. inherit methods
Puppy.prototype.constructor = Puppy;                // 3. fix constructor pointer
Puppy.prototype.whine = function () { return "yip"; };

const p = new Puppy("Bit");
p.bark();  // "woof"  (from Dog.prototype)
p.whine(); // "yip"   (from Puppy.prototype)
```

💡 **Remember:** *"`call` copies the **state**, `Object.create` chains the **behavior**."* (props vs methods)

❓ *How do you implement inheritance without classes?* `Parent.call(this, ...)` for instance properties + `Child.prototype = Object.create(Parent.prototype)` to chain methods, then restore `Child.prototype.constructor`.

---

### Class as Syntactic Sugar

**Definition:** ES6 `class` is cleaner syntax over the same constructor-function + prototype machinery — no new inheritance model underneath.

```
  class                          ≈ desugars to
  ───────────────────────────────────────────────
  constructor(){}                  function Dog(){}
  method(){}                       Dog.prototype.method = ...
  extends Dog                      Object.create(Dog.prototype)
  super(name)                      Dog.call(this, name)
```

```js
class Dog {
  constructor(name) { this.name = name; }
  bark() { return `${this.name} woof`; }   // → Dog.prototype.bark
}
class Puppy extends Dog {
  constructor(name) { super(name); }       // → Dog.call(this, name)
  whine() { return "yip"; }
}
const p = new Puppy("Bit");
p.bark();                                   // "Bit woof"
typeof Dog;                                 // "function" (!) — still a function
Object.getPrototypeOf(p) === Puppy.prototype; // true
```

> Differences from functions: class bodies are **not hoisted** (TDZ), run in **strict mode**, methods are non-enumerable, and calling a class without `new` **throws**.

💡 **Remember:** *"Class is **lipstick on a prototype** — `typeof Dog === 'function'` proves it."*

❓ *Are JS classes real classes?* No — they're syntactic sugar over prototypes. `typeof MyClass === "function"`, methods land on `.prototype`, and `extends` wires the same prototype chain.

---

### `==` (coercion) vs `===` (strict)

**Definition:** `===` compares type **and** value with no conversion; `==` coerces operands to a common type first, which produces surprises.

```
  ===  ┌─ same type? ──no──► false
       └─ yes ► compare values

  ==   ┌─ different types? ──► COERCE, then compare  (gotcha zone)
```

```js
1 === "1";        // false — different types, no coercion
1 == "1";         // true  — "1" coerced to 1

0 == "";          // true   (both → 0)
0 == false;       // true   (false → 0)
null == undefined;// true   — special case, equal to each other only
null == 0;        // false  — null only loosely equals undefined
NaN === NaN;      // false  — NaN equals nothing; use Number.isNaN()
[] == false;      // true   ([] → "" → 0, false → 0)  😱
[] == ![];        // true   (![] → false → 0; [] → "" → 0)  😱
```

💡 **Remember:** *"`===` is the **honest** one — **always use it.**"* `==` only safely for `x == null` (catches both null & undefined).

❓ *When is `==` ever OK?* The one idiomatic use: `x == null` to check for **both** `null` and `undefined` in a single comparison. Everywhere else use `===`.

---

### Spread (`...`) vs Rest (`...`)

**Definition:** Same `...` token, opposite jobs — **spread** expands an iterable *out* into elements; **rest** collects leftovers *in* to one array/object.

```
  SPREAD  (unpack →)         REST  (pack ←)
  [...[1,2,3]]               function f(...args)
   1, 2, 3                    args = [a, b, c]
  expands OUT                gathers IN
```

```js
// SPREAD — expand
const a = [1, 2];
const b = [...a, 3];               // [1, 2, 3]
const clone = { ...user, age: 31 };// shallow copy + override
Math.max(...[4, 9, 2]);            // 9

// REST — collect
function sum(...nums) { return nums.reduce((t, n) => t + n, 0); }
sum(1, 2, 3);                       // 6
const [first, ...others] = [10, 20, 30]; // first=10, others=[20,30]
const { id, ...rest } = user;             // pull out id, rest = everything else
```

> Gotcha: spread is a **shallow** copy — nested objects/arrays are still shared by reference (`{...user}` clones the top level only). Rest must be the **last** element.

💡 **Remember:** *"**Spread spreads out, Rest rests in.**"* Spread on the **right/call**; rest on the **left/params**.

❓ *Same syntax — how do you tell spread from rest?* Position. On the **right-hand side / in a call** it's spread (expanding); in a **function parameter list or left-hand destructuring** it's rest (collecting).

---

### Destructuring (array / object, defaults, rename)

**Definition:** Syntax to unpack values from arrays (by **position**) or objects (by **key**) into distinct variables.

```
  ARRAY  → by POSITION          OBJECT → by KEY
  [a, b] = [1, 2]               {x, y} = {x:1, y:2}
   a=1 b=2                       x=1   y=2
```

```js
// Array — by position, skip with commas, swap without temp
const [a, , c] = [1, 2, 3];           // a=1, c=3
let m = 1, n = 2; [m, n] = [n, m];    // swap → m=2, n=1

// Object — by key
const { name, age } = user;

// Defaults (used only when value is undefined)
const { role = "guest" } = user;

// Rename + default together
const { name: userName = "N/A" } = user;

// Nested + params (super common in React)
function Card({ title, meta: { date } = {} }) { /* ... */ }
const [count, setCount] = useState(0);   // array destructuring you use daily
```

💡 **Remember:** *"**A**rrays by **A**ddress (position), **O**bjects by **O**riginal-name (key)."* Rename uses `key: newName`.

❓ *Default vs rename syntax?* Default: `{ role = "guest" }`. Rename: `{ name: userName }`. Both: `{ name: userName = "N/A" }`. Defaults trigger **only on `undefined`**, not on `null` or `0`.

---

### Optional Chaining `?.`

**Definition:** Short-circuits to `undefined` (instead of throwing) the moment a reference in the chain is `null`/`undefined`.

```
  user?.address?.city
        │         │
   null/undef? ──►── returns undefined, stops (no TypeError)
```

```js
const user = { profile: null };

user.profile.city;     // ❌ TypeError: Cannot read 'city' of null
user?.profile?.city;   // ✅ undefined — safe

user.getName?.();      // calls only if getName is non-nullish (else undefined)
user.tags?.[0];        // safe dynamic/array access
api?.data?.items ?? [];// pairs perfectly with ?? for a fallback
```

> Note: `?.` guards `null`/`undefined` only — it does **not** save you from `0`, `""`, or reading a missing key on a *valid* object. Also it **short-circuits**: in `a?.b.c`, if `a` is nullish the whole `.b.c` is skipped, not just `.b`.

💡 **Remember:** *"`?.` = **ask politely** — if it's missing, get `undefined` instead of a crash."*

❓ *What does `?.` return and what does it guard?* Returns `undefined` and stops the chain as soon as the left side is `null`/`undefined`; it guards only those two — not other falsy values.

---

### Nullish Coalescing `??` (vs `||`)

**Definition:** `??` returns the right side only when the left is `null` or `undefined`; `||` returns the right side for **any** falsy value (`0`, `""`, `false`, `NaN` too).

```
  LEFT value   │  a || b      │  a ?? b
  ─────────────┼──────────────┼──────────
  0            │  b  (falsy)  │  0  (kept!)   ← the critical difference
  ""           │  b           │  ""  (kept!)
  false        │  b           │  false (kept!)
  null         │  b           │  b
  undefined    │  b           │  b
```

```js
const count = 0;
count || 10;   // 10  ❌ wrong — 0 is valid but falsy
count ?? 10;   // 0   ✅ correct — only null/undefined trigger fallback

// Banking-relevant: a real $0 balance must NOT become a fallback
const balance = account.balance ?? "N/A"; // 0 stays 0; missing → "N/A"

const name = input.name || "Anonymous";   // here || is fine — "" should fall back
```

💡 **Remember:** *"`??` cares only about **null-ish** (null/undefined); `||` rejects **all falsy**."* Use `??` for numbers/booleans where `0`/`false` are valid.

❓ *`??` vs `||` — when does it matter?* Whenever `0`, `""`, or `false` are **legitimate values**. `||` wrongly replaces them with the fallback; `??` only falls back on `null`/`undefined`. (Syntax note: you can't mix `??` with `||`/`&&` on the same level without parentheses — `a ?? b || c` is a **SyntaxError**.)

---

💡 **Section one-liner:** *Stack holds primitives & pointers, heap holds objects; GC sweeps the unreachable; primitives copy by value, objects share an arrow; everything inherits up the `__proto__` chain (classes are just sugar); use `===`, `??`, and `?.` for safe, predictable code.*

---

## ⚛️ React — Fundamentals + useState + useEffect

---

### Virtual DOM

**Definition:** An in-memory JavaScript object tree that mirrors the real DOM; React builds a fresh copy on each render, compares it to the previous one, and syncs only the differences to the slow real DOM.

```
 setState()
     │
     ▼
 ┌─────────────┐                    ┌─────────────┐
 │  OLD VDOM   │  ◄──── diff ─────  │  NEW VDOM   │
 │ (previous)  │   (new vs old)     │ (this render)│
 └─────────────┘                    └─────────────┘
     │  minimal patch (only what changed)
     ▼
 ┌─────────────┐
 │  REAL DOM   │  ◄── expensive, touched as little as possible
 └─────────────┘
```

```jsx
// A VDOM node is just a plain object — JSX compiles to this:
const vnode = React.createElement("h1", { className: "x" }, "Hi");
// ≈ { type: "h1", props: { className: "x", children: "Hi" } }
```

💡 **Remember:** "Virtual DOM = a **draft** you scribble on before writing the **final letter** (real DOM)."

---

### Reconciliation

**Definition:** The process React runs to compare the new VDOM with the previous one and decide the minimal set of real-DOM mutations needed.

```
render() → new tree ─┐
                     ├─► reconcile (compare) ─► list of changes ─► commit to DOM
previous tree ───────┘
```

```jsx
// Same type 'div' at same position → React keeps the node, updates props.
// Different type → React unmounts old subtree, mounts new one.
{isLoggedIn ? <Dashboard /> : <Login />} // type swap = full remount of that subtree
```

💡 **Remember:** "**Reconcile** = React's accountant balancing **old vs new** before paying the DOM."

---

### The Diffing Algorithm

**Definition:** React's O(n) heuristic for reconciliation built on two assumptions — (1) different element **types** produce different trees, and (2) **keys** identify which list children are stable across renders.

```
LISTS without keys (or key=index)   LISTS with stable keys (GOOD)
┌───┬───┬───┐  prepend Z            ┌───┬───┬───┐  prepend Z
│ A │ B │ C │  ──► React compares   │A•│B•│C•│  ──► React matches
└───┴───┴───┘     by position:      └───┴───┴───┘     by key, inserts Z,
   Z A B C        A→Z,B→A,C→B,+D        Z A B C        moves A,B,C unchanged
                  ALL rows mutate                      1 insert only
```

```jsx
// ✅ Stable, unique key → React moves nodes instead of rebuilding them
{accounts.map((acc) => (
  <AccountRow key={acc.id} account={acc} />
))}
// ❌ key={index} breaks on insert/reorder/delete — state attaches to wrong row
```

💡 **Remember:** "**Type + Key**" are the two levers. Index keys = identity theft for list items.

❓ **Why O(n) and not O(n³)?** The optimal general tree-diff (full edit distance) is O(n³); React drops to O(n) by *never* comparing across element types or moving nodes between tree levels — it only compares same-position, same-type nodes and uses keys to match list children.

---

### Fiber Architecture (high level)

**Definition:** React's reimplemented reconciler (v16+) that splits rendering into small units of work that can be **paused, prioritized, and resumed**, so high-priority updates (typing, clicks) don't get blocked by big renders.

```
 OLD (stack reconciler)        FIBER (v16+)
 render = one giant            render = many small units
 synchronous call             ┌──┐ ┌──┐ ┌──┐ ┌──┐
 ▓▓▓▓▓▓▓▓▓▓ (blocks UI)        │  │ │  │ │  │ │  │  ← can yield to browser
 cannot interrupt              └──┘ └──┘ └──┘ └──┘  ← urgent input cuts in

 Two phases:
   render/reconcile  → interruptible, no side effects, can restart
                       (builds the work-in-progress tree)
   commit            → synchronous, applies DOM mutations (NOT interruptible)
```

```jsx
// Fiber powers concurrent features — e.g. deprioritize a heavy update:
import { useTransition } from "react";
const [isPending, startTransition] = useTransition();
startTransition(() => setFilter(query)); // urgent typing stays smooth
```

💡 **Remember:** "**Fiber = render with a pause button.**" Render phase = pausable (and may run twice, so keep it pure); commit phase = all-at-once.

---

### One-Way Data Flow

**Definition:** Data flows **down** from parent to child via props; children communicate **up** only by calling callback functions the parent passed down — never by mutating parent state directly.

```
   ┌─────────── Parent (owns state) ───────────┐
   │  state ──props──►  Child                   │
   │    ▲                 │                     │
   │    └──── callback ◄───┘ (child calls fn)   │
   └────────────────────────────────────────────┘
```

```jsx
function Parent() {
  const [balance, setBalance] = useState(100);
  return <Deposit balance={balance} onDeposit={(n) => setBalance((b) => b + n)} />;
}
function Deposit({ balance, onDeposit }) {
  return <button onClick={() => onDeposit(50)}>Balance: {balance}</button>;
}
```

💡 **Remember:** "**Props down, events up.**" State has one owner; the rest just borrow it read-only.

---

### ❓ Why is React fast?

**Answer (say this crisply):**
1. **Virtual DOM** — batches and computes a minimal diff in cheap JS memory instead of thrashing the real DOM directly.
2. **O(n) heuristic diffing** — same-type/same-position comparison + keys avoid an expensive full tree diff.
3. **Batched updates** — multiple state changes in one tick produce a single re-render + single commit.
4. **Fiber scheduling** — interruptible rendering + priorities keep urgent interactions responsive under load.

```
React fast = (cheap diff in memory) + (minimal real-DOM writes) + (batching) + (Fiber scheduling)
```

💡 **Remember:** "React isn't magically fast — it's fast because it **touches the real DOM as little as possible**." (Honest caveat: a hand-tuned vanilla app *can* beat React; React optimizes for maintainable speed, not raw speed.)

---

### ❓ What happens after setState/useState update?

**Definition:** A state update is a *request to re-render*, not an immediate DOM write. React runs a four-stage pipeline.

```
 setState(x)
     │
     ▼
 1. SCHEDULE   → mark component dirty, queue update (batched with others in this tick)
     │
     ▼
 2. RENDER     → call component fn again, build new VDOM (render phase, interruptible)
     │
     ▼
 3. DIFF       → reconcile new VDOM vs old, compute minimal change set
     │
     ▼
 4. COMMIT     → mutate real DOM (sync) → run useLayoutEffect → browser paints → run useEffect
```

> **Bail-out note:** if the new state is `Object.is`-equal to the current state, React may skip the re-render (it can still re-run the component once before bailing).

💡 **Remember:** "**Schedule → Render → Diff → Commit.**" State change = *ring the bell*, React decides *when* to answer.

---

### Functional Components: props, state, drilling, lifecycle

**Definition:** Components are plain functions that take `props` (read-only inputs) and return JSX; `useState` gives them local state, and effects replace class lifecycle methods.

```
LIFECYCLE in the functional world:
  MOUNT   →  useEffect(fn, [])            // runs once after first paint
  UPDATE  →  useEffect(fn, [dep])         // re-runs when dep changes
  UNMOUNT →  return () => {...} from effect // cleanup on removal

PROP DRILLING (the pain):
  App ──user──► Layout ──user──► Sidebar ──user──► Avatar   (only Avatar needs it)
                                                    ▲ Context API removes the middlemen
```

```jsx
function Avatar({ user }) {        // props: read-only input
  const [open, setOpen] = useState(false); // state: local & mutable via setter
  useEffect(() => {                // mount: subscribe
    const id = subscribe(user.id);
    return () => unsubscribe(id);  // cleanup: before re-run AND on unmount
  }, [user.id]);                   // update: re-run when user.id changes
  return <img onClick={() => setOpen((o) => !o)} src={user.avatar} alt={user.name} />;
}
```

💡 **Remember:** "Props are **arguments**, state is **memory**, effects are **lifecycle**." Drilling > 2 levels → reach for Context.

---

### useState: async nature, batching, functional updater

**Definition:** `useState` returns `[value, setValue]`; calling the setter schedules a re-render — it does **not** mutate the current variable, and multiple setters in one event (and, in React 18+, inside promises/timeouts/native handlers too) are **batched** into a single re-render.

```
WHY setCount(count+1) twice fails:
  count = 0  (frozen for this render)
  setCount(count + 1)  // queues "set to 0+1 = 1"
  setCount(count + 1)  // queues "set to 0+1 = 1"  ← stale 'count' still 0!
  ──► result: 1  ❌

FUNCTIONAL UPDATER fixes it:
  setCount(c => c + 1) // "take whatever's latest, +1"
  setCount(c => c + 1) // "take that result, +1"
  ──► result: 2  ✅  (each receives the freshest queued value)
```

```jsx
function Counter() {
  const [count, setCount] = useState(0);
  const addTwo = () => {
    setCount((c) => c + 1); // ✅ functional updater — safe across batched calls
    setCount((c) => c + 1); // both apply → +2
  };
  return <button onClick={addTwo}>{count}</button>;
}
```

💡 **Remember:** "If your **next** state depends on your **previous** state, use the **updater function** `prev => ...`."

---

### ❓ Why is a state update not immediate?

**Answer:**
- The state variable is a **const captured by closure** for *this* render — React can't reassign it mid-function.
- React **batches** all updates in an event handler and re-renders **once** for performance (avoids N renders for N setters).
- So `setCount(5); console.log(count)` logs the **old** value — the new value only exists in the *next* render's snapshot.

```jsx
const [count, setCount] = useState(0);
function handle() {
  setCount(5);
  console.log(count); // 👉 logs 0, NOT 5 — 'count' is frozen for this render
}
```

💡 **Remember:** "State is a **snapshot**, not a live variable. The setter affects the **next** snapshot."

---

### useEffect: the four dependency cases

**Definition:** `useEffect(fn, deps)` runs side effects *after* the browser paints; the dependency array controls *how often* it runs, and an optional returned function is the **cleanup**.

```
 NO deps        useEffect(fn)        → after EVERY render
 []             useEffect(fn, [])    → ONCE, after mount
 [dep]          useEffect(fn, [dep]) → after mount + whenever dep changes
 cleanup        return () => {...}   → before next run AND on unmount

 Timeline for [dep] with cleanup:
   mount ─► effect(A)
   dep A→B ─► cleanup(A) ─► effect(B)
   dep B→C ─► cleanup(B) ─► effect(C)
   unmount ─► cleanup(C)
```

```jsx
// 1) NO deps — runs after every render (rarely what you want)
useEffect(() => { console.log("every render"); });

// 2) [] — mount only (fetch once, set up a global listener)
useEffect(() => { fetchAccounts(); }, []);

// 3) [dep] — mount + when dep changes (refetch when userId changes)
useEffect(() => { fetchProfile(userId); }, [userId]);

// 4) cleanup — runs before next effect AND on unmount (prevent leaks)
useEffect(() => {
  const id = setInterval(tick, 1000);
  return () => clearInterval(id); // ✅ no dangling timers / memory leaks
}, []);
```

> **Strict Mode (dev only):** React mounts, unmounts, and remounts each component once on first mount, so every effect runs **setup → cleanup → setup**. This is a dev-only smoke test that your cleanup is correct — it does **not** happen in production.

💡 **Remember:** "**Empty = once, deps = on change, no array = always.** Always clean up subscriptions, timers, and sockets."

---

### ❓ When does cleanup run?

**Answer:** The cleanup function runs in **two situations**:
1. **Before the effect re-runs** — right before React applies the effect again because a dependency changed (it tears down the *previous* effect first).
2. **On unmount** — when the component is removed from the tree.

```
 render 1 ─► effect runs (sets up)
              │
 dep changes ─┤
              ▼
 render 2 ─► CLEANUP of render-1 effect ─► effect runs again (sets up)
              ...
 component removed ─► CLEANUP (final)
```

```jsx
useEffect(() => {
  const socket = openLiveBalance(accountId);   // setup
  return () => socket.close();                 // cleanup: before re-run AND on unmount
}, [accountId]); // switching accounts: close old socket, then open new one
```

💡 **Remember:** "Cleanup runs **before the next effect** and **once at the end** (unmount). Old effect always tears down before the new one builds up."

---

### 🏦 Banking-context one-liners (drop these to stand out)

- **Security:** never put raw tokens/PII in state that gets logged; sanitize before `console.log`, and remember client state is fully visible to the user — auth checks must live on the server. One-way data flow keeps mutations auditable and predictable.
- **Performance:** stable `key={acc.id}` on transaction/account lists prevents costly re-mounts and flicker on live updates.
- **Cleanup discipline:** always `clearInterval`/`socket.close()` in cleanup — leaked balance-polling timers and a "setState on unmounted component" race are real production bugs.
- **Accessibility:** type-swap reconciliation (`isLoading ? <Spinner/> : <Table/>`) plays nicely with `aria-live` regions; stable keys help screen readers track row changes, and every meaningful `<img>` (e.g. an avatar) needs an `alt`.

💡 **Master mnemonic:** "**V**irtual DOM **R**econciles via **D**iffing, **F**iber schedules, data flows **O**ne-way → **VRDFO**. State is a **snapshot**; effects are **lifecycle with cleanup**."

---

## ⚛️ React — useRef, useMemo, useCallback, React.memo, useContext

A quick mental map before the details:

```
HOOK            MEMOIZES / HOLDS      RE-RENDER ON CHANGE?   TYPICAL USE
─────────────   ───────────────────   ───────────────────   ───────────────────────
useRef          a mutable box         NO                     DOM node, timer id, prev value
useMemo         a VALUE               n/a (returns value)    expensive calc, stable object
useCallback     a FUNCTION ref        n/a (returns fn)       callback to memoized child
React.memo      a COMPONENT (HOC)     skips if props equal   pure child re-render guard
useContext      a shared VALUE        YES (all consumers)    avoid prop drilling
```

---

### 🔗 useRef

**Definition:** `useRef(initialValue)` returns a mutable object `{ current }` that persists across renders and does **not** trigger a re-render when you mutate `.current`. The same object identity is returned on every render.

```
   render 1 ───┐
   render 2 ───┼──►  same ref object ──► { current: <whatever you set> }
   render 3 ───┘        (identity stable; mutating .current ≠ re-render)
```

**Three classic use cases:**

```tsx
function Demo() {
  // 1) DOM reference — focus an input
  const inputRef = useRef<HTMLInputElement>(null);
  useEffect(() => { inputRef.current?.focus(); }, []);

  // 2) Store a timer/interval id (survives renders, no re-render)
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const start = () => { timerRef.current = setInterval(tick, 1000); };
  const stop  = () => { if (timerRef.current) clearInterval(timerRef.current); };

  // 3) Hold the PREVIOUS state/value
  const [count, setCount] = useState(0);
  const prevCount = useRef(count);
  useEffect(() => { prevCount.current = count; }, [count]);
  // During render, prevCount.current is still LAST render's count;
  // the effect updates it AFTER paint, so next render sees the new "previous".

  return <input ref={inputRef} />;
}
```

> **Key contrast:** changing `useState` re-renders and is visible immediately; changing a `useRef` does **not** re-render — the new value is simply read on the next render. Never read/write `ref.current` *during* render to derive UI (it's a side effect React doesn't track) — do it in effects or event handlers.

> **DOM refs & React versions:** attaching `ref` to a **native** element (`<input ref={...}>`) just works. To forward a ref to your **own custom component**, you needed `React.forwardRef` before React 19; from **React 19** `ref` is a regular prop, so `forwardRef` is no longer required.

> **Lazy init gotcha:** `useRef(expensiveCall())` evaluates `expensiveCall()` on **every** render (the result is just ignored after the first). For a costly initial value, guard it: `if (ref.current === null) ref.current = expensiveCall();`.

**💡 Remember:** *"Ref = remember without re-render."* A ref is a sticky note the component keeps in its pocket; editing the note doesn't repaint the screen.

**❓ Interview: useRef vs useState?**
`useState` → value drives the UI; setting it schedules a re-render and is batched. `useRef` → mutable storage that lives across renders but is invisible to the render cycle (no re-render). Use state for things the user sees; refs for DOM handles, IDs, and "previous value" bookkeeping.

---

### 🧮 useMemo

**Definition:** `useMemo(fn, deps)` memoizes the **result of an expensive computation**, recomputing only when one of its dependencies changes (shallow `Object.is` comparison of each dep).

```
deps unchanged  ──►  return CACHED value   (skip the calc)
deps changed    ──►  run calc, cache, return new value
```

```tsx
// Expensive derived value — recompute only when `accounts` changes
const totalBalance = useMemo(
  () => accounts.reduce((sum, a) => sum + a.balance, 0),
  [accounts]
);

// Referential stability — keep the SAME object identity between renders
const chartConfig = useMemo(
  () => ({ currency: 'USD', locale: 'en-US' }),
  [] // never recreated → safe to pass to a memoized child / effect dep
);
```

**When to use:** (1) the calculation is genuinely expensive (big list filter/sort/reduce), or (2) you need **referential stability** — a stable object/array identity so a `React.memo` child or a `useEffect` dependency doesn't see a "new" value every render. Don't wrap trivial math; the memo bookkeeping (storing deps + comparing) can cost more than the calc.

> **Not a guarantee:** `useMemo` is a performance hint, not a semantic promise. React may discard the cache (e.g. to free memory) and recompute. Never rely on it for correctness — only for speed/identity.

**💡 Remember:** *"useMemo caches a VALUE."* Memo = "memo to self: same inputs → reuse last answer."

---

### 🪝 useCallback

**Definition:** `useCallback(fn, deps)` memoizes a **function reference**, returning the same function instance until one of its dependencies changes.

```
const fn = useCallback(() => doThing(id), [id]);
   id same    ──►  same fn reference  (child sees identical prop)
   id changed ──►  new fn reference
```

```tsx
function AccountList({ accounts }: { accounts: Account[] }) {
  const [selected, setSelected] = useState<string | null>(null);

  // Stable handler — identity only changes if something in deps changes
  const handleSelect = useCallback((id: string) => {
    setSelected(id);
  }, []); // the setState setter is guaranteed stable → empty deps is correct

  return accounts.map(a => (
    <AccountRow key={a.id} account={a} onSelect={handleSelect} />
  ));
}
const AccountRow = React.memo(/* ... */);
```

**When to use:** primarily when passing a callback to a **`React.memo`-wrapped child** (or as a dependency of another hook). Without `useCallback`, a brand-new function each render makes the memoized child re-render anyway, defeating the optimization. If the child is **not** memoized, `useCallback` gives no benefit — and still costs the bookkeeping.

> **Watch the deps:** any value the callback reads (like `id`) must be in `deps`, or you'll capture a **stale closure**. An empty `[]` is only safe when the body references nothing reactive (state setters are stable, so they're exempt).

**useMemo vs useCallback — value vs function:**

```
useCallback(fn, deps)  ≡  useMemo(() => fn, deps)
        │                          │
   returns the fn itself      returns whatever the fn RETURNS
```
(`≡` here means *semantically equivalent*, not reference-equal — they produce the same stable function, but `useCallback` exists so you don't write the wrapper.)

**💡 Remember:** *"useCallback memoizes the function; useMemo memoizes the function's result."* Callback → **C**ode (the function). Memo → the **M**aterial (the value).

**❓ Interview: useMemo vs useCallback?**
Both cache by dependencies. `useMemo` returns the **value** a function produces; `useCallback` returns the **function** itself. By definition, `useCallback(fn, d)` is equivalent to `useMemo(() => fn, d)`.

---

### 🛡️ React.memo

**Definition:** `React.memo` is a Higher-Order Component that wraps a component and **skips re-rendering** it when its props are **shallowly equal** (`Object.is` per prop) to the previous render. It only guards against *prop-driven* re-renders — a memoized component still re-renders from its own `useState`/`useContext` changes.

```
Parent re-renders
        │
        ▼
React.memo(Child):  shallow-compare new props vs old props
        │
   equal? ──► YES → SKIP re-render (reuse last output)
        └──► NO  → re-render Child
```

```tsx
type Props = { account: Account; onSelect: (id: string) => void };

const AccountRow = React.memo(function AccountRow({ account, onSelect }: Props) {
  console.log('render', account.id);
  return (
    <button type="button" onClick={() => onSelect(account.id)}>
      {account.name}: ${account.balance}
    </button>
  );
});
```

> **Custom comparator footgun:** `React.memo(Comp, areEqual)` lets you supply your own comparison (return `true` = "props equal, skip"). But you then own **all** props — comparing only one field silently ignores changes to the rest:
> ```tsx
> // ⚠️ BUG: only compares balance — name and onSelect changes are NEVER reflected
> const Row = React.memo(AccountRow, (prev, next) =>
>   prev.account.balance === next.account.balance
> );
> ```
> Use a custom comparator only when you can enumerate every prop that matters; otherwise rely on the default shallow compare.

> **Shallow-compare gotcha:** new object/array/function props (e.g. inline `onClick={() => …}`, `style={{}}`, or `data={[...]}`) are a *different identity* every render and break memoization — pair `React.memo` with `useCallback`/`useMemo` on the props you pass in. Also note: `children` is usually a fresh element each render, so a `React.memo` around a component that takes `children` often won't skip anyway.

**💡 Remember:** *"memo the COMPONENT, useMemo the VALUE, useCallback the FUNCTION."* React.memo = a bouncer at the child's door checking "did your props actually change?"

**❓ Interview: React.memo vs useMemo?**
`React.memo` is a **HOC around a whole component** — it skips that component's re-render when props are shallow-equal. `useMemo` is a **hook inside a component** that caches a single computed **value**. One guards a component; the other caches a value. They're commonly used together: `useMemo`/`useCallback` keep prop identities stable so the surrounding `React.memo` actually pays off.

---

### 🌐 useContext

**Definition:** `useContext(MyContext)` reads the current value from the nearest matching Context Provider above it, letting deeply nested components consume shared state **without prop drilling**.

```
WITHOUT context (prop drilling)        WITH context
  App ─user→ Layout ─user→ Nav          App
              ─user→ Avatar              └ <UserContext.Provider value={user}>
  (passed through every level)              └ Layout
                                               └ Nav
                                               └ Avatar ◄── useContext(UserContext)
                                            (any depth reads directly)
```

```tsx
// 1) Create
const AuthContext = createContext<{ user: User | null } | null>(null);

// 2) Provide
function App() {
  const [user, setUser] = useState<User | null>(null);
  // memoize the value so consumers don't re-render on unrelated App renders
  const value = useMemo(() => ({ user }), [user]);
  return (
    <AuthContext.Provider value={value}>
      <Dashboard />
    </AuthContext.Provider>
  );
}

// 3) Consume (no prop drilling)
function Avatar() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('Avatar must be inside an AuthContext.Provider');
  return <span>{ctx.user?.name ?? 'Guest'}</span>;
}
```

> **React 19 note:** you can render `<AuthContext value={value}>` directly (the context object is now a valid provider component); `<AuthContext.Provider>` still works and remains the broadly compatible form.

**Limitation:** when the Provider's `value` changes, **every** consumer of that context re-renders — even ones that only read an unchanged part of it. `React.memo` on a consumer does **not** save it: context updates bypass the props shallow-compare. Mitigations:
- **Split contexts** by concern (e.g. separate `AuthContext` and `ThemeContext`) so a change in one doesn't churn the other.
- **Memoize the value** (`useMemo`) so it doesn't get a new identity on every Provider render (an inline `value={{ user }}` creates a new object each render → re-renders all consumers every time).
- **Split state and dispatch** into two contexts so components that only dispatch don't re-render when the state changes.
- For high-frequency / fine-grained updates, reach for an external store (Redux/Zustand) with **selector-based** subscriptions, which re-render only components that read the changed slice.

**💡 Remember:** *"Context = global props teleport — but the whole subscriber list re-renders on every value change."* Split + memoize the value.

**❓ Interview: when does Context hurt performance, and how do you fix it?**
Any change to the Provider `value` re-renders all consumers, regardless of which field they read — and `React.memo` won't shield them. Fix by (1) memoizing the value object, (2) splitting one big context into smaller focused contexts (often state vs dispatch), and (3) using a selector-based store (Zustand/Redux) when updates are frequent and fine-grained.

---

### 🎯 One-screen recap

```
useRef       → mutable .current, NO re-render        (DOM, timer id, prev value)
useMemo      → cache a VALUE by deps                 (expensive calc, stable object)
useCallback  → cache a FUNCTION ref by deps          (callback → memoized child)
React.memo   → HOC, skip child if props shallow-equal (pair w/ useCallback/useMemo)
useContext   → read shared value, no prop drilling   (split + memoize value)

Equivalence: useCallback(fn, d)  ≡  useMemo(() => fn, d)
Pairing:     React.memo(child) only pays off if its props are identity-stable
             → that's exactly what useCallback / useMemo provide.
Caveat:      React.memo does NOT stop re-renders from useState / useContext.
```

**💡 Master mnemonic:** *"**Ref** remembers, **Memo** computes, **Callback** is the function, **memo** guards the component, **Context** teleports."*

---

The memory is not relevant to this technical review task. Let me analyze the draft for correctness.

I've reviewed the draft thoroughly. The technical content is largely strong and correct. Here are the issues I found and fixed:

**Correctness issues fixed:**
1. **`useFetch` race condition claim** — The draft says `AbortController` prevents an old response overwriting a new one. This is true, but I added a defensive note that abort isn't guaranteed to win the race in all cases (the cleanup runs synchronously before the new effect, and `AbortError` is filtered), so the logic is sound — but I tightened the wording. More importantly, the `useState(true)` initial `loading` is correct.
2. **`useLocalStorage` cross-tab sync gap** — The draft's hook doesn't listen to the `storage` event, so it won't sync across tabs. I noted this as a known limitation (not a bug, but interview-relevant).
3. **Re-render bailout nuance** — The draft correctly notes `Object.is` bailout but I clarified that the bailout still re-runs the component function once before bailing in some cases (state setter with same value can still trigger one render). I kept it accurate.
4. **Context re-render nuance** — Added that context consumers re-render even if wrapped in `React.memo` (memo doesn't block context updates) — an important and commonly-missed interview point.
5. **Index-key visual** — The "WRONG" annotation was slightly off; with index keys on prepend, React keeps DOM nodes by index so the *input values* stay put while the *text content* shifts. I corrected the visual to show this precisely.

Here is the polished Markdown:

---

## ⚛️ React — Custom Hooks, Re-render Triggers, Keys

---

### 1. Custom Hooks — extract reusable stateful logic

**Definition:** A custom hook is a `use`-prefixed JS function that calls other hooks to package reusable stateful logic, so multiple components share the *logic* (not the *state* — each call gets its own isolated state).

```
   Component A          Component B
       │                    │
       └──── useFetch() ────┘    ← same logic, called from both
              │
   ┌──────────┴──────────┐
   │ useState + useEffect │   ← built-in hooks live INSIDE
   └─────────────────────┘
   Each caller → its OWN state (NOT shared)
```

```ts
function useToggle(initial = false): [boolean, () => void] {
  const [on, setOn] = useState(initial);
  const toggle = useCallback(() => setOn(v => !v), []);
  return [on, toggle];
}
// Each component that calls useToggle() gets an independent `on`.
```

💡 **Remember:** "Custom hooks share **behaviour, not state**." Think of it as a *recipe* — every kitchen that follows it cooks its own meal.

---

### 2. Rules of Hooks

**Definition:** Hooks must be called in the **same order on every render**, only at the **top level** of a React function, and only from **React functions** (components or other hooks); the name must start with `use`.

```
✅ TOP LEVEL                    ❌ CONDITIONAL / LOOP / NESTED
function C() {                  function C() {
  const [a] = useState();         if (x) useState();   // order varies!
  const [b] = useState();         for (…) useEffect(); // order varies!
  …                               function inner(){ useState(); } // not React fn
}
```

```ts
// ❌ BAD — hook behind a condition: render N has 2 hooks, render N+1 has 1
function Bad({ show }: { show: boolean }) {
  if (show) { const [x] = useState(0); }   // breaks hook ordering
  const [y] = useState(0);
}

// ✅ GOOD — always call, branch on the value instead
function Good({ show }: { show: boolean }) {
  const [x] = useState(0);
  const [y] = useState(0);
  return show ? <>{x}</> : <>{y}</>;
}
```

**Why it matters:** React tracks hook state by **call order**, not by name. Skip one conditionally and every subsequent hook reads the *wrong* slot.

💡 **Remember:** **"Top, every time, `use` it, React-only."** Hooks are a numbered list — never renumber it mid-render.

❓ **Why must hooks be called in the same order every render?**
React stores hook state in a linked list traversed in call order (1st `useState`, 2nd `useState`…). It has no names to match on, so a conditional/looped hook shifts the positions and React hands back state belonging to a different hook (and the dev build throws *"Rendered fewer/more hooks than during the previous render"*).

---

### 3. Implement `useFetch`

**Definition:** A hook that fetches a URL and exposes `{ data, loading, error }`, re-fetching when the URL changes and aborting stale in-flight requests.

```ts
function useFetch<T>(url: string) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const ctrl = new AbortController();
    setLoading(true);
    setError(null);

    fetch(url, { signal: ctrl.signal })
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.json();
      })
      .then((json: T) => setData(json))
      .catch((e: Error) => { if (e.name !== 'AbortError') setError(e); })
      .finally(() => setLoading(false));

    return () => ctrl.abort();   // cancel stale request on url change/unmount
  }, [url]);

  return { data, loading, error };
}
```

**How the abort prevents the race:** when `url` changes, React runs the **cleanup of the old effect first** (`ctrl.abort()` rejects the old `fetch` with an `AbortError`), *then* runs the new effect. The aborted promise hits `.catch`, is filtered out by the `e.name !== 'AbortError'` guard, and never calls `setData` — so the old response can't clobber the new one. (Edge case: if the old response had *already* resolved before abort fired, abort is a no-op; for strict correctness you'd also guard writes with an `ignore` flag.)

💡 **Remember:** **"Fetch → Abort on cleanup."** The `return () => ctrl.abort()` is what prevents the *race condition* where an old response overwrites a new one (critical for a banking account-switch UI).

❓ *Banking gotcha:* without `AbortController`, switching accounts fast can render Account A's balance under Account B — a correctness/security bug.

---

### 4. Implement `useDebounce`

**Definition:** A hook that returns a value only **after it has stopped changing** for `delay` ms — collapsing a burst of updates into one.

```
typing:  W..e..l..l..s.........→ (pause 300ms) → emit "Wells"
         │  │  │  │  │
         timer reset on each keystroke; fires once on settle
```

```ts
function useDebounce<T>(value: T, delay = 300): T {
  const [debounced, setDebounced] = useState(value);

  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(id);   // cancel previous timer on each change
  }, [value, delay]);

  return debounced;
}

// Usage: only hit the search API once typing settles
const debouncedQuery = useDebounce(query, 300);
useEffect(() => { search(debouncedQuery); }, [debouncedQuery]);
```

💡 **Remember:** **"Debounce = wait for the silence."** The `clearTimeout` in cleanup is the whole trick — each keystroke kills the pending timer.
*(Debounce = fire after quiet; Throttle = fire at most once per interval — don't mix them up.)*

---

### 5. Implement `useLocalStorage`

**Definition:** A `useState`-like hook that persists its value to `localStorage` and reads it back on mount, surviving page reloads.

```ts
function useLocalStorage<T>(key: string, initial: T) {
  const [value, setValue] = useState<T>(() => {
    try {
      const raw = localStorage.getItem(key);
      return raw ? (JSON.parse(raw) as T) : initial;
    } catch {
      return initial;                       // corrupt JSON / SSR / private mode
    }
  });

  useEffect(() => {
    try { localStorage.setItem(key, JSON.stringify(value)); }
    catch { /* quota / unavailable — fail silently */ }
  }, [key, value]);

  return [value, setValue] as const;
}
```

```
mount ─► read localStorage[key] ─► state
setValue ─► state changes ─► effect writes back to localStorage
```

💡 **Remember:** **"Lazy read, effect write."** Use the lazy `useState(() => …)` initialiser so you read storage *once*, not on every render.
⚠️ *Banking:* never store tokens, PANs, or PII in `localStorage` — it's readable by any XSS (no `HttpOnly` protection). Persist UI prefs only; keep auth in `HttpOnly` cookies.
*Known limitation:* this version doesn't subscribe to the `window` `storage` event, so a change in **another tab** won't sync here — add a `storage` listener in a separate effect if cross-tab sync is required.

---

### 6. Re-render Triggers

**Definition:** A component re-renders when its **own state changes**, its **parent re-renders**, a **subscribed context value changes**, or it receives **new props** — but **not** when a `useRef().current` mutates.

```
RE-RENDER CAUSED BY            NOT a re-render
──────────────────            ───────────────
① own state  (setState)        ✗ ref.current = x   (mutable box, no subscribe)
② parent re-renders            ✗ a plain variable changing
③ context value changes        ✗ mutating an object in place w/o setState
④ new props (incl. new ref     ✗ derived value you didn't store in state
   identity from parent)
```

```tsx
function Counter() {
  const [count, setCount] = useState(0);  // ① setState → re-render
  const renders = useRef(0);              // ✗ ref bump → NO re-render
  renders.current++;                      // updates silently, screen unchanged

  return (
    <button onClick={() => setCount(c => c + 1)}>
      count {count} (rendered {renders.current}× — only visible AFTER a state render)
    </button>
  );
}
```

💡 **Remember:** **"State, Parent, Context, Props — `ref` is the exception."** Mnemonic: **S-P-C-P**. A ref is a *backpack* — you can change what's inside without telling React.

❓ **What causes a re-render?**
1. **Own state** updates via `useState`/`useReducer` — but React **bails out** if the new value is `Object.is`-equal to the current one (note: it may still re-run the component function *once* before bailing, then discard the output).
2. **Parent re-renders** — children re-render by default unless memoised with `React.memo` *and* their props are referentially stable.
3. **Context value** the component consumes changes identity — this fires **even if the component is wrapped in `React.memo`**; `memo` does not block context-driven re-renders.
4. **New props** passed down (a new object/array/function identity counts as "new", which is why `useMemo`/`useCallback` matter for memoised children).
> Crucially, a `useRef().current` mutation does **not** trigger a re-render — refs are an escape hatch from the render cycle. Likewise, changing a normal variable or mutating state in place (without `setState`) won't re-render.

---

### 7. Keys in Lists — why `index` as key is a bug

**Definition:** A `key` is a stable identity React uses to match each list item to a DOM node across renders; using the array **index** breaks that identity when items are **inserted, removed, or reordered**, causing React to reuse the wrong node (and its component state / uncontrolled input value) for the wrong data.

#### The index-key bug, visually

```
Initial list (index keys):              Prepend "Wells" to the front:

 key=0 ─ [Acme ]  <input "111">          key=0 ─ [Wells]  <input "111">  ← WRONG!
 key=1 ─ [Beta ]  <input "222">          key=1 ─ [Acme ]  <input "222">  ← shifted
                                         key=2 ─ [Beta ]  <input  ""  >

React sees keys 0 and 1 already exist → it KEEPS those DOM nodes (and the
text the user typed into them) and only PATCHES the changed text/props.
So the typed "111"/"222" stay glued to indices 0/1 while the row LABELS
shift down — every typed value now sits next to the WRONG account.
```

With a **stable id** key, React matches by identity instead:

```
 key="acme" ─ [Acme]  "111"      key="wells" ─ [Wells]  ""    ← new node, empty
 key="beta" ─ [Beta]  "222"      key="acme"  ─ [Acme ]  "111" ← travels WITH its data
                                 key="beta"  ─ [Beta ]  "222"
```

```tsx
// ❌ BUG: index as key — breaks on insert/reorder/delete
{accounts.map((acc, i) => <Row key={i} account={acc} />)}

// ✅ FIX: stable, unique id from the data
{accounts.map(acc => <Row key={acc.id} account={acc} />)}
```

**Why it's specifically dangerous in banking:** rows containing uncontrolled `<input>`s (amounts, payee names), checkboxes, or focus state will visually "stick" to the wrong record after a sort/insert — a user could approve a transfer against the wrong account.

💡 **Remember:** **"Key = identity, not position."** Index keys lie when the list changes shape. Rule of thumb: **index keys are only safe for a static, append-only, non-interactive list** — otherwise use a real `id`. (Don't fabricate keys with `Math.random()` either — a fresh key every render forces React to destroy and rebuild every node, killing state and performance.)

❓ **Why is index-as-key a bug?**
React diffs siblings by `key`. With index keys, after an insert/reorder the *same* index points to *different* data, so React reuses the existing DOM node (preserving its internal/uncontrolled state — input text, focus, checkbox) but feeds it new props. Result: stale/mismatched UI state on the wrong row. A stable unique id ties the node to its data, so React **moves** the node correctly and creates/removes state exactly where it should.

---

#### 🔑 One-screen recap

| Concept | One-liner |
|---|---|
| Custom hook | Reuse logic, not state; name starts with `use` |
| Rules of hooks | Top level, same order, React fns only |
| `useFetch` | Fetch + abort stale on cleanup (race-safe) |
| `useDebounce` | Emit after the typing goes quiet; `clearTimeout` is the trick |
| `useLocalStorage` | Lazy read, effect write; no secrets; no cross-tab sync by default |
| Re-render triggers | **S**tate, **P**arent, **C**ontext, **P**rops (not `ref`); context pierces `memo` |
| Keys | Stable id = identity; index breaks on insert/reorder; never `Math.random()` |

---

## 🔄 Redux / State Management

---

### Core Unidirectional Flow

**Definition:** Data flows one way — a UI event dispatches an action, a pure reducer computes the next state, the store updates, and subscribed UI re-renders.

```
   ┌──────────────────────────────────────────────────┐
   │                                                    │
   ▼                                                    │
 [UI]  --dispatch(action)-->  [Store]                   │
   ▲                             │                      │
   │                             ▼                      │
   │                        [Reducer]  (prev, action)   │
   │                             │       → new state     │
   │                             ▼                      │
   └──── re-render ◄──── [New State] ───────────────────┘
        (via useSelector / subscribe)
```

The cycle is **closed and predictable**: state only changes through dispatched actions, never by direct mutation.

```js
// One full lap of the loop:
store.dispatch({ type: 'counter/increment' }); // 1. action in
// 2. reducer runs: (state, action) => newState
// 3. store holds newState
// 4. subscribers fire → UI reads via useSelector → re-render
```

**💡 Remember:** **"A-D-R-S-U"** → **A**ction → **D**ispatch → **R**educer → **S**tore → **UI**. *"Actions Don't Reach State Unilaterally"* — they go through the reducer.

**❓ Why unidirectional?** Predictability and debuggability: every state change is traceable to one action, time-travel debugging works, and you never have two-way bindings silently mutating shared state from multiple places.

---

### The Pieces (store, reducer, action creators, dispatch, subscribe)

**Definition:** The five primitives that make Redux work — the **store** holds state, **reducers** compute it, **action creators** describe events, **dispatch** sends them, **subscribe** reacts to changes.

| Piece | One-liner | Shape |
|-------|-----------|-------|
| **store** | Single source of truth; holds the whole state tree | `{ getState, dispatch, subscribe }` |
| **reducer** | Pure `(state, action) => newState` | a function |
| **action** | Plain object describing *what happened* | `{ type, payload }` |
| **action creator** | Function that returns an action | `(p) => ({type, payload: p})` |
| **dispatch** | The only way to trigger a state change | `store.dispatch(action)` |
| **subscribe** | Register a listener for state changes | `store.subscribe(fn)` |

```js
import { createStore } from 'redux';
// NOTE: bare `createStore` is deprecated — in real apps use RTK's
// configureStore (below). Shown here only to expose the raw primitives.

// reducer (pure)
function counter(state = { value: 0 }, action) {
  switch (action.type) {
    case 'INCREMENT': return { value: state.value + 1 };
    default:          return state;
  }
}

// action creator
const increment = () => ({ type: 'INCREMENT' });

const store = createStore(counter);              // store
const unsub = store.subscribe(() =>              // subscribe
  console.log(store.getState().value));
store.dispatch(increment());                     // dispatch → logs 1
unsub();                                          // stop listening
```

**💡 Remember:** **Store = box, Reducer = recipe, Action = order ticket, Dispatch = waiter, Subscribe = notification bell.** One box, one recipe, many tickets.

**❓ Can you have multiple stores?** Technically yes, but the Redux convention is **one store** per app — the whole point is a single source of truth. Use `combineReducers` (or RTK's `reducer` map) to split logic, not multiple stores.

---

### Reducers Must Be Pure

**Definition:** A reducer is a pure function — same input always gives same output, with no side effects (no API calls, no `Date.now()`, no mutation).

```
PURE reducer:                IMPURE (forbidden):
(state, action) → newState   ✗ fetch() inside
   • no mutation             ✗ Math.random() / Date.now()
   • no I/O                  ✗ state.x = 5  (mutation!)
   • deterministic           ✗ dispatch() inside
```

```js
// ❌ IMPURE — mutates + side effect
function bad(state, action) {
  state.value++;                 // mutation!
  localStorage.setItem('v', 1);  // side effect!
  return state;
}

// ✅ PURE — returns brand-new object
function good(state, action) {
  return { ...state, value: state.value + 1 };
}
```

**💡 Remember:** **"Reducers are math, not magic."** Given the same args, no surprises. Side effects live in thunks/middleware.

**❓ Where do side effects (API calls) go?** Not in reducers — in **middleware** (thunks/sagas) or component effects. Reducers only transform data.

---

### Immutability

**Definition:** Never modify existing state; always produce a **new** object/array so Redux can detect change by reference (`prev !== next`).

```
Reference equality drives re-render:

  oldState ──ref──► {value: 0}
  newState ──ref──► {value: 1}   ← different ref → React re-renders

  MUTATION: oldState.value = 1   → same ref → NO re-render (bug!)
```

```js
// Objects
return { ...state, user: { ...state.user, name: 'Ana' } };

// Arrays — use non-mutating ops
return { ...state, todos: [...state.todos, newTodo] };       // add
return { ...state, todos: state.todos.filter(t => t.id !== id) }; // remove
return { ...state, todos: state.todos.map(t =>                // update
  t.id === id ? { ...t, done: true } : t) };

// ❌ NEVER: state.todos.push(...), state.x = ..., arr.sort() in place
//    (sort/reverse/splice mutate — copy first: [...arr].sort())
```

**💡 Remember:** **"Spread, don't shred."** Copy with `...`, use `map/filter/concat` (return new) — never `push/splice/sort/reverse` (mutate in place).

**❓ Why does immutability matter for React-Redux?** `useSelector` and `connect` use **shallow reference checks** to decide re-renders. Mutating in place keeps the same reference → React thinks nothing changed → stale UI.

---

### Redux Toolkit (RTK) — The Modern Standard

**Definition:** The official, batteries-included Redux package that removes boilerplate — `configureStore`, `createSlice` (auto-generates actions + reducers), and Immer for safe "mutating" syntax.

```
Classic Redux                 Redux Toolkit
─────────────                 ─────────────
action types (strings)   ┐
action creators          ├──►  createSlice()  (all three, auto)
reducer switch-case      ┘
createStore + middleware ────►  configureStore()  (thunk+devtools built-in)
manual immutable spreads ────►  Immer (write "mutating" code safely)
```

#### `configureStore`

**Definition:** Sets up the store with good defaults — thunk middleware, Redux DevTools, and dev-mode immutability/serializability checks — all out of the box.

```ts
import { configureStore } from '@reduxjs/toolkit';
import counterReducer from './counterSlice';

export const store = configureStore({
  reducer: { counter: counterReducer },   // root state: { counter: ... }
});

// Types for hooks (TS)
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

#### `createSlice`

**Definition:** One function that bundles a slice of state + its reducers + auto-generated action creators, using Immer so you can write `state.value++`.

```ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

const counterSlice = createSlice({
  name: 'counter',
  initialState: { value: 0 },
  reducers: {
    increment: (state) => { state.value += 1; },          // Immer: looks like mutation
    decrement: (state) => { state.value -= 1; },
    addBy: (state, action: PayloadAction<number>) => {
      state.value += action.payload;
    },
  },
});

export const { increment, decrement, addBy } = counterSlice.actions; // auto-created
export default counterSlice.reducer;
```

> **Immer note:** Inside `createSlice`, `state.value += 1` is *not* a real mutation — Immer tracks your edits on a draft and produces a new immutable object behind the scenes. Rule: either **mutate the draft** *or* **`return` a new value** — never both in the same reducer.

**💡 Remember:** **"Slice = name + initialState + reducers → free actions."** RTK is **"Redux with the boilerplate deleted."**

**❓ Does RTK let you mutate state?** *Inside `createSlice`/`createReducer` only*, thanks to **Immer** — it looks like mutation but produces a new immutable state. Outside (or with plain `createStore`), you must still spread.

---

### Using It in React (`useSelector` / `useDispatch`)

**Definition:** `useSelector` reads a slice of state and subscribes to it; `useDispatch` returns the dispatch function to send actions.

```tsx
import { useSelector, useDispatch } from 'react-redux';
import { increment, addBy } from './counterSlice';
import type { RootState } from './store';

function Counter() {
  const value = useSelector((s: RootState) => s.counter.value); // read + subscribe
  const dispatch = useDispatch();
  return (
    <>
      <span aria-live="polite">{value}</span>
      <button onClick={() => dispatch(increment())}>+1</button>
      <button onClick={() => dispatch(addBy(5))}>+5</button>
    </>
  );
}
// App must be wrapped in <Provider store={store}>...</Provider>
```

> **a11y note:** Wrap a value that updates from user actions in `aria-live="polite"` so screen readers announce the new count without stealing focus. The native `<button>` already gives you keyboard + role for free — don't replace it with a clickable `<div>`.

**💡 Remember:** **"Select to read, Dispatch to write."** Keep selectors narrow — select the smallest slice you need so the component re-renders only when *that* changes.

**❓ Why select narrowly?** `useSelector` re-runs the component when its selected value changes (by reference). Selecting a whole object — or returning a **new** array/object each render (e.g. `state.items.filter(...)`) — defeats the reference check and causes needless re-renders. For derived data, memoize with `createSelector` (Reselect) or use `useSelector(..., shallowEqual)`.

---

### Middleware (thunk, logger)

**Definition:** Functions that sit between `dispatch` and the reducer, intercepting actions to add capabilities like async (thunk) or logging.

```
dispatch(action)
      │
      ▼   (middleware run in REGISTRATION order; RTK puts thunk first,
      │    then whatever you .concat())
 [ thunk middleware ]  → if action is a FUNCTION, run it (async allowed)
      │
      ▼
 [ logger middleware ] → console.log before/after (added via .concat)
      │
      ▼
   [ reducer ] → new state
```

```ts
// thunk lets you dispatch a FUNCTION instead of a plain object:
const fetchUser = (id) => async (dispatch) => {
  dispatch({ type: 'user/loading' });
  const res = await fetch(`/api/users/${id}`);
  dispatch({ type: 'user/loaded', payload: await res.json() });
};

// simple custom logger middleware (curried: store => next => action)
const logger = (store) => (next) => (action) => {
  console.log('dispatching', action);
  const result = next(action);        // pass along to the next middleware/reducer
  console.log('next state', store.getState());
  return result;
};
```

> RTK includes **thunk** by default. Add custom middleware via
> `configureStore({ middleware: (gDM) => gDM().concat(logger) })`.
> Order matters: the array order is the order actions flow through, so a `.concat(logger)` logger sits **after** the default thunk.

**💡 Remember:** **"Middleware = airport security between dispatch and reducer."** The triple-arrow `store => next => action =>` is the classic signature — *"Store, Next, Action."* Call `next(action)` to pass it on; skip it to swallow the action.

**❓ thunk vs saga?** **Thunk** = simple async via functions (great default, MERN-friendly). **Saga** = generator-based, better for complex orchestration (cancellation, debouncing, long flows). For most banking dashboards, thunks suffice.

---

### `createAsyncThunk` (async / API)

**Definition:** RTK helper that generates an async thunk plus three auto-dispatched lifecycle actions — `pending`, `fulfilled`, `rejected` — so you can track loading/error state cleanly.

```
createAsyncThunk('user/fetch', apiCall)
        │ dispatch(fetchUser(id))
        ▼
   ┌─ pending    → status: 'loading'
   ├─ fulfilled  → status: 'succeeded', data = payload
   └─ rejected   → status: 'failed',   error  = message
```

```ts
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

export const fetchUser = createAsyncThunk(
  'user/fetch',
  async (id: string, { rejectWithValue }) => {
    const res = await fetch(`/api/users/${id}`);
    if (!res.ok) return rejectWithValue('Failed to load user');
    return res.json();                       // becomes action.payload on fulfilled
  }
);

const userSlice = createSlice({
  name: 'user',
  initialState: { data: null, status: 'idle', error: null as string | null },
  reducers: {},
  extraReducers: (builder) => {              // handle thunk lifecycle here
    builder
      .addCase(fetchUser.pending,   (s) => { s.status = 'loading'; s.error = null; })
      .addCase(fetchUser.fulfilled, (s, a) => { s.status = 'succeeded'; s.data = a.payload; })
      .addCase(fetchUser.rejected,  (s, a) => {
        s.status = 'failed';
        // a.payload = value from rejectWithValue; a.error.message = thrown error
        s.error = (a.payload as string) ?? a.error.message ?? 'Error';
      });
  },
});
export default userSlice.reducer;
// Component: dispatch(fetchUser('42')); then useSelector(s => s.user.status)
```

**💡 Remember:** **"Three states of an API call: Pending, Fulfilled, Rejected — P.F.R."** Handle them in **`extraReducers`** (the slice didn't *create* these actions, so they're "extra").

**❓ reducers vs extraReducers?** `reducers` → actions this slice *owns* (auto-generated). `extraReducers` → actions defined *elsewhere* (thunks, other slices) that this slice also reacts to. (Tip: prefer `rejectWithValue` for expected API errors so the message lands on `action.payload`; uncaught throws land on `action.error`.)

---

### When To Use What

**Definition:** Choose the lightest tool that fits the scope — local state for one component, Context for small low-frequency globals, Redux for large shared frequently-updated state.

```
                 Scope & frequency
  local ─────────────────────────────────────► global
  │                                                   │
  useState ──► useReducer ──► Context ──► Redux/RTK
  (one comp)   (complex      (small, rarely   (large app,
               local logic)   changing global  shared state,
                              e.g. theme/auth)  frequent updates,
                                                devtools)
```

| Need | Use |
|------|-----|
| State used by **one component** (form input, toggle) | `useState` |
| **Complex local** transitions (multi-field form, wizard) | `useReducer` |
| **Small global, low-frequency** (theme, current user, locale) | **Context** |
| **Large/shared, frequently updated**, needs devtools/middleware/async | **Redux Toolkit** |

```tsx
// useState — local
const [open, setOpen] = useState(false);

// Context — small global, rarely changes
const ThemeCtx = createContext('light');
<ThemeCtx.Provider value="dark">{children}</ThemeCtx.Provider>

// Redux — large, shared, frequent (accounts, transactions, balances)
const balance = useSelector((s: RootState) => s.account.balance);
```

> **Context caveat:** Context re-renders **all consumers** on every value change — and passing a fresh object literal as `value` (`value={{ user, setUser }}`) re-renders them on *every* parent render. Fine for theme/auth; bad for high-frequency data (live balances, ticking prices). That's exactly where Redux's fine-grained `useSelector` subscriptions win.

**💡 Remember:** **"useState for me, Context for a few, Redux for the zoo."** Don't reach for Redux until state is **shared + complex + frequently changing**.

**❓ Context vs Redux — when does Context fall short?** Context has no built-in re-render optimization (every consumer re-renders), no middleware, no devtools/time-travel, no structured async. For large apps with frequent cross-cutting updates (a banking dashboard with live accounts, transactions, notifications), Redux scales better.

---

### 🏦 Banking-Context One-Liners (interview gold)

- **Predictability/audit:** Redux's single source of truth + traceable actions makes financial state changes auditable and reproducible — every balance change ties to one dispatched action.
- **DevTools time-travel:** Replay a user's exact action sequence to reproduce a bug in a money-handling flow — huge for a bank.
- **Security:** Never store secrets/access tokens in Redux state that you persist to `localStorage` — `localStorage` is readable by any script, so an XSS payload exfiltrates it. Keep auth tokens in `httpOnly` cookies (JS can't read them); Redux holds only non-sensitive UI/derived state. If you must persist Redux, allowlist which slices and exclude anything sensitive.
- **Performance:** Fine-grained `useSelector` subscriptions prevent unrelated re-renders when one account's balance ticks — critical for a live dashboard.
- **Testing:** Pure reducers are trivially unit-testable — `expect(reducer(prev, action)).toEqual(next)` with no mocks, which suits a bank's high test-coverage bar.

**💡 Final mnemonic:** **"A.D.R.S.U + Pure + Immutable + RTK."** If you can say *"actions flow one way through pure reducers that return new state, and RTK removes the boilerplate,"* you've nailed the Redux interview.

---

That memory is about backend PR style — not directly relevant to this a11y review task. I have enough to finalize. Let me address the technical issues I found and produce the polished Markdown.

The key correctness fixes I'm making:

1. **Combobox/listbox keyboard bug (the biggest issue):** In the draft, `onKeyDown` is on both the `<button>` and the `<ul role="listbox">` with `tabIndex={-1}`. With the `aria-activedescendant` pattern, focus must *stay* on a single focusable element while the visual highlight moves via id. The `<ul>` never receives focus (nothing moves focus to it), so its `onKeyDown` is dead, and after opening, focus stays on the button which is correct — but the `aria-activedescendant` and `aria-controls` wiring needs to be on the *focused* element. I'll move `aria-activedescendant`/`aria-controls` onto the button, drop the dead listbox handler, and add `aria-controls`/`aria-selected` scrolling. I'll also make the active option visually indicated and add `aria-label` to the trigger.

2. **`aria-activedescendant` placement:** must be on the element with DOM focus (the button), not the non-focused `<ul>`.

3. **WCAG contrast precision:** add px equivalents (18pt ≈ 24px, 14pt bold ≈ 18.66px) and clarify the 3:1 UI-component criterion is WCAG 2.1 (1.4.11).

4. **Focus-trap robustness:** the trap only wraps when `document.activeElement` is exactly first/last; if focus somehow escapes the modal it won't pull it back. Minor — I'll note it. Also `first?.focus()` with empty focusables would leave focus on body; add a fallback.

5. **Live region nuance:** `aria-live` region must exist in the DOM *before* content changes for reliable announcement — worth a one-line note.

Here is the polished section.

---

## ♿ Accessibility (a11y) — Banking Priority

Banking front-ends are legally bound (ADA, Section 508, WCAG 2.1 AA). Inaccessible flows = lawsuits + locked-out customers. Below is the full revision toolkit.

---

### 1. Semantic HTML vs `<div onClick>`

**Definition:** Using meaningful HTML elements (`<button>`, `<nav>`, `<main>`...) that carry built-in roles, keyboard handling, and screen-reader semantics — instead of generic `<div>`/`<span>` wired up manually.

```
SEMANTIC PAGE SKELETON
┌─────────────────────────────────────┐
│ <header>   logo + login state       │
│ ┌─────────────────────────────────┐ │
│ │ <nav>  Accounts | Transfers ... │ │
│ └─────────────────────────────────┘ │
│ <main>                              │
│   <section aria-label="Balances">   │
│     <article> Checking …</article>  │
│   </section>                        │
│ <footer>  © Wells Fargo             │
└─────────────────────────────────────┘
```

A real `<button>` gives you, for FREE:
- Focusable in tab order (no `tabindex` needed)
- Fires on **Enter** AND **Space**
- Announced as "button" by screen readers
- Disabled state + form submission semantics

```tsx
// ❌ Reinvents the wheel — and gets it wrong
<div className="btn" onClick={transfer}>Send money</div>
// Not focusable, no keyboard, SR says nothing.

// ✅ Free behavior, correct semantics
<button type="button" onClick={transfer}>Send money</button>

// If you MUST use a div (don't), you owe ALL of this:
<div
  role="button"
  tabIndex={0}
  onClick={transfer}
  onKeyDown={(e) => {
    // Note: native buttons fire on Space *keyup* and on Enter *keydown*.
    if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); transfer(); }
  }}
>Send money</div>
```

> **Landmark note:** Use *one* `<main>` per page and `<h1>` for the page title; screen-reader users jump by landmark and heading, so `<nav>`/`<main>`/`<footer>` aren't decoration — they're the navigation map. Multiple same-type landmarks (e.g. two `<nav>`s) need distinct `aria-label`s.

**💡 Remember:** *"A `div` is a costume; a `button` is the actor."* If you `role="button"` you signed up to re-implement everything the real one gave free.

**❓ "Why is `<button>` better than `<div onClick>`?"** It's keyboard-focusable, fires on Enter+Space, announces its role to screen readers, and supports `disabled`/form semantics — all of which a `div` lacks and you'd have to (imperfectly) rebuild with `role`, `tabIndex`, and `onKeyDown`.

---

### 2. ARIA — attributes & the cardinal rule

**Definition:** ARIA (Accessible Rich Internet Applications) adds roles/states/properties to tell assistive tech what an element *is* and *is doing*, when native HTML can't express it. ARIA changes only the accessibility tree — it never adds behavior (no focus, no keyboard, no events); you still wire those yourself.

| Attribute | Plain meaning |
|---|---|
| `aria-label` | Invisible accessible name (icon-only buttons) |
| `aria-labelledby` | Name comes from another element's id |
| `aria-describedby` | Extra description (hint/error) by id |
| `aria-hidden="true"` | Hide decorative node from SR (keep visual) |
| `role="dialog"` | This is a modal dialog |
| `aria-invalid="true"` | This field has a validation error |
| `aria-live="polite"` | Announce changes in this region |
| `aria-expanded` | Is this disclosure/dropdown open? |

```tsx
// Icon-only button needs a name
<button aria-label="Close" onClick={close}>✕</button>

// Decorative icon hidden from SR (text already conveys meaning)
<span aria-hidden="true">💳</span> Cards

// Live region: announces async balance update without moving focus.
// The container must already exist in the DOM (render it empty first),
// then update its text — SRs only announce *changes* to a present region.
<div aria-live="polite">{balance ? `Balance updated: ${balance}` : ''}</div>
```

```
aria-live="polite"    → waits for SR to finish, then announces (most UI)
aria-live="assertive" → interrupts immediately (errors, alerts only)
role="alert"          → implicit aria-live="assertive" + aria-atomic
role="status"         → implicit aria-live="polite"
```

**💡 Remember:** *"No ARIA is better than bad ARIA."* Reach for native HTML first; `role="button"` on a real `<button>` is redundant, and `aria-hidden="true"` on a **focusable** element is a trap — keyboard users will Tab onto a control the screen reader pretends doesn't exist.

**❓ "What does 'no ARIA is better than bad ARIA' mean?"** A wrong/over-applied ARIA attribute actively misleads assistive tech (e.g. a fake `role` that breaks expected keyboard behavior, or `aria-hidden` over a focusable control), which is worse than plain HTML that at least degrades correctly. Use semantic elements first; add ARIA only to fill genuine gaps.

---

### 3. Keyboard Navigation

**Definition:** Every interactive control must be operable with the keyboard alone — no mouse required.

```
KEY            EXPECTED ACTION
Tab            → next focusable element
Shift + Tab    ← previous focusable element
Enter          activate link / submit / button
Space          activate button / toggle checkbox (also scrolls page on body)
Escape         close modal / dropdown / cancel
Arrows ↑↓←→    move WITHIN a widget (menu, radio group, tabs, slider)
```

Rule of thumb: **Tab moves BETWEEN widgets; Arrows move WITHIN one widget.** A dropdown, radio group, or tablist is ONE tab stop ("roving tabindex"); arrows move the active item inside it.

```tsx
function Dialog({ onClose }: { onClose: () => void }) {
  return (
    <div role="dialog"
      onKeyDown={(e) => { if (e.key === 'Escape') onClose(); }}>
      …
    </div>
  );
}
```

> **Don't break the natural order:** avoid positive `tabIndex` values (`tabIndex={1}` etc.) — they hijack tab order globally and are an anti-pattern. Use `0` (in order) or `-1` (focusable only programmatically).

**💡 Remember:** *"Tab between, Arrow within, Escape to bail."* If you can't `Tab` to it and operate it without a mouse, it's broken.

**❓ "How do you make a custom widget keyboard accessible?"** Make it focusable (native element or `tabIndex={0}`), handle the expected keys for its role (Enter/Space to activate, Arrows to navigate within, Escape to dismiss), keep visible focus indicators, and use roving `tabIndex` so the whole widget is a single tab stop.

---

### 4. Focus Management — trap & restore

**Definition:** Programmatically controlling where keyboard focus lives — trapping it inside an open modal, and returning it to the trigger element on close.

```
Focus BEFORE open:  [Transfer] button  ← remember this node
        │ open modal
        ▼
┌─ MODAL (focus trapped) ──────────┐
│  Tab cycles: Amount → Confirm →  │
│  Cancel → (loops back to Amount) │   Tab at last → first
│                                  │   Shift+Tab at first → last
└──────────────────────────────────┘
        │ close (Esc / Cancel / submit)
        ▼
Focus RESTORED → [Transfer] button   ← user never "loses" their place
```

```tsx
function Modal({ onClose, children }: { onClose: () => void; children: React.ReactNode }) {
  const ref = useRef<HTMLDivElement>(null);
  const triggerRef = useRef<HTMLElement | null>(null);

  useEffect(() => {
    triggerRef.current = document.activeElement as HTMLElement; // 1. remember opener
    const node = ref.current!;
    const getFocusables = () =>
      Array.from(
        node.querySelectorAll<HTMLElement>(
          'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])'
        )
      );

    const focusables = getFocusables();
    // 2. move focus in — fall back to the dialog itself if it has no focusables
    (focusables[0] ?? node).focus();

    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') { onClose(); return; }
      if (e.key !== 'Tab') return;
      // Re-query each time: contents may have changed (errors, async fields)
      const items = getFocusables();
      if (items.length === 0) { e.preventDefault(); return; }
      const first = items[0], last = items[items.length - 1];
      // 3. trap: wrap around the edges
      if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    };

    node.addEventListener('keydown', onKey);
    return () => {
      node.removeEventListener('keydown', onKey);
      triggerRef.current?.focus(); // 4. restore focus to opener on unmount
    };
  }, [onClose]);

  // role="dialog" + aria-modal hide the background from SRs; give it an accessible name
  return (
    <div ref={ref} role="dialog" aria-modal="true" aria-labelledby="modal-title" tabIndex={-1}>
      {children}
    </div>
  );
}
```

Why the dialog itself gets `tabIndex={-1}`: it lets us focus the container as a fallback (and is required by the focus-in step) without putting it in the tab order. Production tip: the native `<dialog>` element plus `showModal()` gives a focus trap, `Escape`-to-close, and background inertness for free — prefer it, or a vetted library (`focus-trap-react`, Radix, React-Aria).

**💡 Remember:** *"Trap it, then hand it back."* Remember the opener → focus first element in → loop Tab at the edges → restore on unmount.

**❓ "How do you handle focus in a modal?"** Save the currently focused element, move focus into the dialog, trap Tab/Shift+Tab so it cycles within, close on Escape, and on close return focus to the element that opened it. Mark it `role="dialog"` + `aria-modal="true"` with an accessible name, or use native `<dialog>.showModal()`.

---

### 5. Screen Readers — NVDA / JAWS / VoiceOver

**Definition:** Software that reads the accessibility tree aloud and lets blind/low-vision users navigate by headings, landmarks, links, and form fields.

```
SCREEN READER     PLATFORM         NOTE
NVDA              Windows (free)   most common test target, open-source
JAWS              Windows (paid)   enterprise/banking standard
VoiceOver         macOS / iOS      built-in (Cmd+F5 / triple-click side)
TalkBack          Android          built-in
```

What they rely on (so YOU must provide it): correct **roles**, **accessible names** (label/`aria-label`), **landmarks** (`<nav>`, `<main>`), a logical **heading hierarchy** (`h1→h2→h3`, no skipped levels), and `aria-live` for dynamic updates. SR users navigate by *pulling up lists* of headings, landmarks, and form fields — so those structures are the primary UX, not an afterthought.

**💡 Remember:** *"NVDA & JAWS on Windows, VoiceOver on Mac/iOS."* Banking QA usually tests **JAWS + NVDA**. The SR reads the *accessibility tree*, not your CSS — semantics are what it "sees."

**❓ "Which screen readers would you test with?"** NVDA (free, Windows) and JAWS (paid, common in enterprise/banking) on Windows, plus VoiceOver on macOS/iOS — test at least one Windows SR and VoiceOver, since behavior and ARIA support differ between engines.

---

### 6. Accessible Forms

**Definition:** Form fields with programmatically associated labels, clear errors tied to inputs, and validation state exposed to assistive tech.

```
 label ──for="amt"──┐
                    ▼
   [ Amount ]  <input id="amt">
                    │ invalid?
        aria-invalid="true"
        aria-describedby="amt-err"
                    ▼
   ⚠ "Amount exceeds balance"  id="amt-err" role="alert"
```

```tsx
function AmountField({ error }: { error?: string }) {
  return (
    <div>
      {/* label's htmlFor MUST equal input's id */}
      <label htmlFor="amt">Transfer amount</label>
      <input
        id="amt"
        type="number"
        inputMode="decimal"
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? 'amt-err' : undefined}
        required
      />
      {error && (
        <span id="amt-err" role="alert">{error}</span>
      )}
    </div>
  );
}
```

Key rules:
- `<label htmlFor>` ↔ `<input id>` must match (clicking the label focuses the input; the SR reads it as the field's name).
- Never use a placeholder as the *only* label (it vanishes on type, has low contrast, and isn't a reliable accessible name).
- `aria-invalid` flags the error state; `aria-describedby` links the message text; `role="alert"` makes the new error announce. Use `aria-invalid={undefined}` (not `"false"`) when valid so SRs don't redundantly announce "valid."
- Mark required fields with the `required` attribute (and a visible indicator) — don't rely on color/asterisk alone.

**💡 Remember:** *"Label by `for/id`, describe the error by `describedby`, flag it with `invalid`."* Placeholder ≠ label.

**❓ "How do you make a form field accessible?"** Associate a visible `<label htmlFor>` with the input's `id`, set `aria-invalid` on error, link the error text via `aria-describedby`, announce it with `role="alert"`/`aria-live`, mark required fields with `required`, and never rely on placeholder text as the label.

---

### 7. WCAG Levels A / AA / AAA + Color Contrast

**Definition:** Web Content Accessibility Guidelines — testable success criteria grouped into three conformance levels; **AA** is the legal/industry target for banking.

```
LEVEL   MEANING                      EXAMPLE CRITERIA
A       minimum / must-have          keyboard operable, alt text, form labels
AA  ◄── TARGET (ADA/508/banking)     4.5:1 contrast, focus visible, resize text
AAA     gold standard (rarely full)  7:1 contrast, sign language, no timing

CONTRAST RATIOS (WCAG 2.1 AA):
 normal text  (<18pt / <14pt bold)  ── 4.5 : 1   (1.4.3)
 large text   (≥18pt ≈24px /         ── 3   : 1   (1.4.3)
               ≥14pt bold ≈18.66px)
 UI components & graphical objects  ── 3   : 1   (1.4.11)
```

```
  #767676 on #FFFFFF  → 4.54:1  ✅ passes AA normal text
  #999999 on #FFFFFF  → 2.85:1  ❌ fails (too light)
```

```tsx
// Don't encode meaning by color ALONE — add text/icon (color-blind users).
// WCAG 1.4.1 "Use of Color": color can't be the only way info is conveyed.
<span className="status status--declined">
  <span aria-hidden="true">●</span> Declined ✕
</span>
```

Two more AA criteria worth naming: **1.4.11 Non-text Contrast** (input borders, icons, focus rings need 3:1) and **2.4.7 Focus Visible** (the focus indicator must always be visible — never `outline: none` without a replacement).

**💡 Remember:** *"Aim for AA; 4.5 for normal, 3 for large/UI."* Mnemonic: **"four-point-five for fine print."** And **never use color as the only signal**.

**❓ "What WCAG level do you target and what's the contrast ratio?"** AA (the ADA/Section-508 industry standard). Minimum text contrast is 4.5:1 for normal text and 3:1 for large text (≥18pt / ≥14pt bold); UI components and graphics also need 3:1. And information must never be conveyed by color alone.

---

### 8. Accessible Custom Dropdown (listbox pattern)

**Definition:** A non-native select built from a button + list that replicates native keyboard, focus, and SR behavior via ARIA roles. This is the **collapsible listbox** pattern: focus *stays on the button*, and the highlighted option is tracked with `aria-activedescendant` (not real DOM focus).

```
┌ button (aria-haspopup="listbox", aria-expanded, aria-controls) ┐
│  Checking ••1234              ▼   ← keeps DOM focus while open   │
└──────────────────────────────────────────────────────────────────┘
   ▼ open (role="listbox", id="acct-list")
   ┌──────────────────────────────────┐
   │ ▸ Checking ••1234  (aria-selected)│ ← active option (id pointed to
   │   Savings  ••5678                 │    by the button's
   │   Credit   ••9012                 │    aria-activedescendant)
   └──────────────────────────────────┘

KEYS:  Enter/Space/↓ open · ↑↓ move active option · Enter/Space select · Esc close
```

```tsx
const accounts = ['Checking ••1234', 'Savings ••5678', 'Credit ••9012'];

function AccountDropdown() {
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);     // highlighted option index
  const [selected, setSelected] = useState(0); // chosen option index

  // One handler on the BUTTON — it keeps focus the whole time, so the
  // listbox needs no key handler and no tabIndex. aria-activedescendant
  // (on the button) is what moves the SR's "cursor" between options.
  const onKey = (e: React.KeyboardEvent) => {
    switch (e.key) {
      case 'Enter':
      case ' ':
        e.preventDefault();
        if (open) { setSelected(active); setOpen(false); }
        else { setOpen(true); }
        break;
      case 'ArrowDown':
        e.preventDefault();
        if (open) setActive((i) => Math.min(i + 1, accounts.length - 1));
        else { setOpen(true); setActive(selected); }
        break;
      case 'ArrowUp':
        e.preventDefault();
        if (open) setActive((i) => Math.max(i - 1, 0));
        else { setOpen(true); setActive(selected); }
        break;
      case 'Escape':
        e.preventDefault();
        setOpen(false);
        break;
    }
  };

  return (
    <div>
      <button
        type="button"
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls="acct-list"
        // activedescendant lives on the FOCUSED element (the button)
        aria-activedescendant={open ? `opt-${active}` : undefined}
        onClick={() => setOpen((o) => !o)}
        onKeyDown={onKey}
      >
        {accounts[selected]} <span aria-hidden="true">▼</span>
      </button>

      {open && (
        <ul id="acct-list" role="listbox">
          {accounts.map((acc, i) => (
            <li
              key={acc}
              id={`opt-${i}`}
              role="option"
              aria-selected={i === selected}
              // Visual highlight tracks `active`; CSS gives it a focus-like style
              className={i === active ? 'is-active' : undefined}
              onMouseDown={(e) => e.preventDefault()} // keep focus on the button
              onClick={() => { setSelected(i); setOpen(false); }}
            >
              {acc}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
```

ARIA contract: trigger = `aria-haspopup="listbox"` + `aria-expanded` + `aria-controls` + `aria-activedescendant`; list = `role="listbox"`; items = `role="option"` + `aria-selected`. Because the **button keeps DOM focus**, the listbox itself takes no `tabIndex` and no key handler — the highlighted option is a *virtual* cursor pointed to by `aria-activedescendant`, and `onMouseDown` is suppressed so clicking an option doesn't steal focus. (The alternative pattern moves real focus to each option via roving `tabIndex`; pick one — don't mix.)

**💡 Remember:** *"haspopup → listbox → option, and `aria-activedescendant` is the laser pointer — it lives on whatever has focus."* If a real `<select>` works, USE IT — only hand-roll when design truly demands it.

**❓ "How would you build an accessible dropdown?"** Prefer native `<select>`. If custom: a `button` with `aria-haspopup="listbox"`/`aria-expanded`/`aria-controls`, a `role="listbox"` container, `role="option"` items with `aria-selected`, full keyboard support (Enter/Space/Arrows/Escape), and `aria-activedescendant` on the focused element to track the highlight — with visible active/focus styles.

---

### ⚡ 60-Second Cheat Sheet

```
SEMANTICS   real <button>/<nav>/<main> > <div onClick> (free a11y)
ARIA        fill gaps only; "no ARIA > bad ARIA"; never adds behavior
KEYBOARD    Tab between · Arrows within · Enter/Space activate · Esc close
FOCUS       trap in modal → restore to trigger; or native <dialog>.showModal()
FORMS       <label for/id> + aria-invalid + aria-describedby + role="alert"
WCAG 2.1    target AA · 4.5:1 normal / 3:1 large+UI · never color alone · focus visible
SCREEN RDR  NVDA + JAWS (Win), VoiceOver (Mac/iOS) — reads the a11y tree
DROPDOWN    native <select> first; else listbox/option + activedescendant
```

**One-liner to say in the interview:** *"For banking I default to semantic HTML so I get keyboard and screen-reader support for free, target WCAG 2.1 AA with 4.5:1 contrast and visible focus, manage focus in modals (trap then restore, or native `<dialog>`), associate every input with a label and wire errors via `aria-invalid`/`aria-describedby`, and only reach for ARIA to fill real gaps — because bad ARIA is worse than none."*

---

## 🧪 Testing — Jest, RTL, TDD, BDD

> Banking lens: tests are your audit trail. A bug in a balance calculation or a transfer flow is real money. Interviewers want: *behavior-driven* tests, *deterministic* mocks, and the *pyramid* mindset (cheap fast tests at the base).

---

### 1. Unit testing with Jest

**Definition:** Testing a single, isolated piece of logic (usually one pure function) to verify it returns the right output for given inputs.

```
 input ──► [ pure function ] ──► output
            (no DB, no network, no DOM)
   assert: expect(output).toBe(expected)
```

**Structure — `describe` / `it` / `expect`:**
- `describe(name, fn)` → groups related tests (a test *suite*).
- `it(name, fn)` / `test(name, fn)` → one test case (`it` and `test` are aliases).
- `expect(value).matcher(...)` → the assertion.

```ts
// money.ts
export const toCents = (dollars: number): number => Math.round(dollars * 100);

// money.test.ts
import { toCents } from "./money";

describe("toCents", () => {
  it("converts whole dollars", () => {
    expect(toCents(10)).toBe(1000);
  });

  it("rounds floating-point cents correctly", () => {
    // 19.99 * 100 === 1998.9999999999998 in IEEE-754 → Math.round saves us
    expect(toCents(19.99)).toBe(1999); // guards the 0.1 + 0.2 problem
  });
});
```

**Common matchers (know these cold):**

| Matcher | Use |
|---|---|
| `.toBe(x)` | strict identity via `Object.is` (primitives, same reference) |
| `.toEqual(x)` | deep value equality (objects/arrays); **ignores `undefined` props** |
| `.toStrictEqual(x)` | deep + checks `undefined` props, array sparseness & types |
| `.toBeNull()` / `.toBeUndefined()` / `.toBeTruthy()` | nullish / boolean-ish |
| `.toContain(x)` | array/string includes (uses `===`) |
| `.toThrow(msg)` | function throws; `msg` is a **substring/regex** match on the message |
| `.toHaveBeenCalledWith(...args)` | spy/mock was called with these args |
| `.toBeCloseTo(n, digits)` | float comparison (avoids `0.1 + 0.2` traps) |

```ts
// .toThrow needs a FUNCTION, not a value — wrap the call so Jest can catch the throw:
expect(() => withdraw(100, 50)).toThrow("Insufficient funds"); // substring match
expect(() => withdraw(100, 50)).toThrow(/insufficient/i);      // regex also works
```

> ⚠️ DOM matchers like `.toBeInTheDocument()` / `.toHaveTextContent()` are **not** built into Jest — they come from `@testing-library/jest-dom` (imported once in your test setup file). Easy thing to forget; interviewers notice.

💡 **Remember:** **"DIE"** — **D**escribe groups, **I**t is one case, **E**xpect asserts. And **`toBe` for primitives, `toEqual` for objects** (two distinct objects are never `===`).

❓ *`toBe` vs `toEqual`?* `toBe` uses `Object.is` (reference/primitive identity) — `{a:1}` is **not** `toBe` `{a:1}`. `toEqual` does a recursive deep value comparison, so two separate objects with the same contents pass. Use `toStrictEqual` when `undefined` properties or types must also match.

---

### 2. React Testing Library (RTL)

**Definition:** A library that tests React components the way a **user** experiences them — by querying the rendered DOM (roles, text, labels) and firing real interactions, not by poking component internals.

```
 Enzyme (old)                 RTL (now)
 ───────────                  ─────────
 wrapper.state('count')       screen.getByText('Count: 1')
 wrapper.instance().method()  await userEvent.click(button)
 → tests IMPLEMENTATION        → tests BEHAVIOR (what user sees)
 breaks on refactor            survives refactor
```

**Queries — pick by priority (accessibility-first):**

| Priority | Query | When |
|---|---|---|
| 1 | `getByRole` | almost always — `button`, `textbox`, `heading`, `link` (+ `{ name }` for the accessible name) |
| 2 | `getByLabelText` | form inputs (ties input to its `<label>`) |
| 3 | `getByText` | non-interactive content |
| last | `getByTestId` | escape hatch only (`data-testid`) |

**`get` vs `query` vs `find`:**
- `getBy*` → element **must** exist now; **throws** if not found.
- `queryBy*` → returns `null` if absent → use it to assert **absence** (`expect(...).not.toBeInTheDocument()`).
- `findBy*` → **async**; returns a Promise that retries (default ~1s) until the element appears → use for data that loads. Always `await` it.

> Each query has `*AllBy*` variants (`getAllByRole`, etc.) returning an **array** when you expect multiple matches.

```tsx
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

test("increments on click", async () => {
  const user = userEvent.setup(); // recommended: create an instance per test
  render(<Counter />);
  const btn = screen.getByRole("button", { name: /add/i });

  await user.click(btn);

  expect(screen.getByText("Count: 1")).toBeInTheDocument();
});
```

**`userEvent` vs `fireEvent`:** prefer `userEvent` — it simulates the *full* interaction (e.g. a click does `pointerdown → mousedown → focus → pointerup → mouseup → click`; typing fires `keydown → keypress → input → keyup` per key), far closer to a real user. `fireEvent` dispatches a single raw DOM event. In `@testing-library/user-event` v14+, every `userEvent` method is **async** and must be `await`ed.

```tsx
const user = userEvent.setup();
await user.type(screen.getByLabelText(/amount/i), "500");
await user.click(screen.getByRole("button", { name: /transfer/i }));
```

💡 **Remember:** **"Find it like a user, not like a developer."** Query order = **R**ole → **L**abel → **T**ext → **T**estId. And **`get` = now, `query` = maybe-absent, `find` = later (async, await it)**.

❓ *Why RTL over Enzyme?* Enzyme tests internals (`state`, instance methods, shallow rendering) → tests break on refactor even when behavior is unchanged, and it encourages testing things users never see. RTL tests rendered output and user interactions, so tests are refactor-resilient and validate real UX + accessibility (queries *fail* if there's no proper role/label). Critically, Enzyme never got a maintained adapter for React 18+ (the community moved on / it's effectively unmaintained), while RTL is the official recommendation.

❓ *What does "test behavior not implementation" mean?* Assert on **what the user observes** (text on screen, element enabled/disabled, an alert appearing, navigation) — not on *how* it's done (which hook, what state variable, internal function calls). This lets you refactor internals freely without rewriting tests.

---

### 3. Mocking

**Definition:** Replacing a real dependency (function, module, network call) with a controllable fake so the test is fast, deterministic, and isolated.

```
 REAL:   component ──► fetch() ──► 🌐 bank API  (slow, flaky, real money!)
 MOCKED: component ──► fetch() ──► 📦 canned response  (instant, deterministic)
```

**`jest.fn()` — a spy/stub function:**
```ts
const onTransfer = jest.fn();
onTransfer(100, "ACC-22");

expect(onTransfer).toHaveBeenCalledTimes(1);
expect(onTransfer).toHaveBeenCalledWith(100, "ACC-22");

// control its return value:
const getRate = jest.fn().mockReturnValue(1.1);
const fetchUser = jest.fn().mockResolvedValue({ name: "Ann" });   // async resolve
const failing = jest.fn().mockRejectedValue(new Error("boom"));   // async reject
```

**Mock a whole module:**
```ts
jest.mock("./api");                 // auto-mock: every export becomes a jest.fn()
import { getBalance } from "./api";
(getBalance as jest.Mock).mockResolvedValue(2500); // give it behavior per test
```

**Mock `fetch` (quick & dirty):**
```ts
global.fetch = jest.fn().mockResolvedValue({
  ok: true,
  status: 200,
  json: async () => ({ balance: 2500 }),
}) as jest.Mock;

afterEach(() => jest.clearAllMocks()); // reset call history between tests
```

**MSW (Mock Service Worker) — the modern, preferred way:** intercepts requests at the **network layer**, so your code calls real `fetch`/`axios` unchanged. More realistic than stubbing `fetch`, and the same handlers are reusable across tests *and* the running app/Storybook.

```ts
import { http, HttpResponse } from "msw";   // MSW v2 API
import { setupServer } from "msw/node";

const server = setupServer(
  http.get("/api/balance", () => HttpResponse.json({ balance: 2500 }))
);

beforeAll(() => server.listen({ onUnhandledRequest: "error" })); // catch stray calls
afterEach(() => server.resetHandlers());                          // undo per-test overrides
afterAll(() => server.close());
```

💡 **Remember:** **`jest.fn()` = a fake function you can spy on; `jest.mock()` = fake a whole file; MSW = fake the network.** Mantra: *"Mock at the boundary"* (network/DB), not your own logic. Reset between tests so order can't leak state.

❓ *`mockReturnValue` vs `mockResolvedValue`?* `mockReturnValue(x)` returns `x` synchronously; `mockResolvedValue(x)` returns `Promise.resolve(x)` (sugar for `mockReturnValue(Promise.resolve(x))`, for async functions); `mockRejectedValue(e)` returns a rejected promise for the error path.

❓ *`clearAllMocks` vs `resetAllMocks` vs `restoreAllMocks`?* `clear` = wipe **call history** only (keep implementation). `reset` = clear history **and** remove the mock implementation (calls return `undefined`). `restore` = put back the **original** implementation (only works for spies created via `jest.spyOn`). Use `clear` when you set the implementation fresh inside each test.

❓ *Why MSW over mocking `fetch`?* You don't rewrite tests when you swap `fetch`↔`axios`; the mock lives at the real network boundary (more realistic — it exercises your actual request/parse code); and the same handlers power tests, Storybook, and local dev.

---

### 4. Test types

**Definition:** Tests differ by **scope** — how much of the system they exercise at once.

```
 UNIT         one function / one component in isolation     (ms, 1000s)
 INTEGRATION  several units wired together (form + state)   (slower, 100s)
 E2E          the whole app in a real browser, real API     (slowest, 10s)
```

| Type | Scope | Tool | Banking example |
|---|---|---|---|
| **Unit** | one function/component | Jest, RTL | `toCents()` rounds correctly |
| **Integration** | multiple components + state/api | RTL + MSW | Transfer form: type amount → submit → success message |
| **E2E** | full app, real browser | Cypress, Playwright, WebdriverIO | Log in → transfer → see updated balance |

```ts
// INTEGRATION (RTL): form + validation + mocked API together
test("shows error when transfer exceeds balance", async () => {
  const user = userEvent.setup();
  render(<TransferForm balance={100} />);
  await user.type(screen.getByLabelText(/amount/i), "500");
  await user.click(screen.getByRole("button", { name: /transfer/i }));
  expect(await screen.findByText(/insufficient funds/i)).toBeInTheDocument();
});
```

> Note: in RTL the unit/integration line is blurry — testing a single component already renders its real children. "Integration" here means multiple *features/components + the API boundary* working together, not the rendering of one component in isolation.

💡 **Remember:** **Scope ladder: Unit → Integration → E2E = "one → some → whole".** Cost & confidence rise as you climb; speed & quantity fall.

❓ *Unit vs integration vs E2E?* Unit = isolate one piece (mock everything around it). Integration = several real units working together (mock only the network/DB boundary). E2E = no mocks, real browser + real backend, validates the full user journey end-to-end.

---

### 5. TDD — Test-Driven Development

**Definition:** Write a **failing test first**, write the minimum code to pass it, then clean up — repeat in tiny cycles.

```
        ┌──────────────────────────────┐
        ▼                              │
   🔴 RED ──► 🟢 GREEN ──► 🔵 REFACTOR ─┘
   write a    write just     improve code,
   failing    enough code    tests stay
   test       to pass        green
```

```ts
// 1. 🔴 RED — write the test; it FAILS (canTransfer doesn't exist yet)
test("rejects transfer above balance", () => {
  expect(canTransfer(100, 150)).toBe(false);
});

// 2. 🟢 GREEN — minimum code to pass
export const canTransfer = (balance: number, amount: number) =>
  amount <= balance;

// 3. 🔵 REFACTOR — add a guard (driven by a new failing test first!), stays green
test("rejects non-positive amounts", () => {
  expect(canTransfer(100, 0)).toBe(false);
});
export const canTransfer = (balance: number, amount: number) =>
  amount > 0 && amount <= balance;
```

💡 **Remember:** **Red, Green, Refactor — "fail first, pass cheap, clean last."** You never write production code without a failing test demanding it (and you confirm the test fails *first* — a test that's green before you write code is a broken test).

❓ *What is TDD / benefits?* Test-first development in Red→Green→Refactor loops. Benefits: forces clear requirements before coding, guarantees coverage by construction, enables fearless refactoring (the suite is a safety net), and produces simpler, decoupled designs (code that's easy to test is well-factored code).

---

### 6. BDD — Behavior-Driven Development

**Definition:** TDD framed in **business language** — describe behavior as **Given / When / Then** so tests read like specs a product owner could understand.

```
 GIVEN  some initial context / state
 WHEN   an action occurs
 THEN   an observable outcome is expected
```

```ts
// BDD-style structure with describe/it reading as a sentence
describe("Transfer", () => {
  describe("given a balance of $100", () => {           // GIVEN
    it("blocks a $150 transfer and shows an error", async () => { // WHEN + THEN
      const user = userEvent.setup();
      render(<TransferForm balance={100} />);
      await user.type(screen.getByLabelText(/amount/i), "150");          // WHEN
      await user.click(screen.getByRole("button", { name: /transfer/i }));
      expect(await screen.findByText(/insufficient funds/i)).toBeInTheDocument(); // THEN
    });
  });
});
```

In tools like **Cucumber/Gherkin**, the same is written in plain-English `.feature` files, with each step backed by a "step definition":
```gherkin
Scenario: Transfer exceeds balance
  Given my balance is $100
  When I transfer $150
  Then I should see "Insufficient funds"
```

💡 **Remember:** **BDD = TDD that speaks English: Given / When / Then.** *"Given a state, When an action, Then an outcome."*

❓ *TDD vs BDD?* Same Red-Green-Refactor rhythm. TDD is developer-centric and tests units/functions; BDD raises the abstraction to **behavior in business terms** (Given/When/Then), is more collaborative (PO/QA/dev share one living spec), and tends to drive acceptance/integration tests. BDD ≈ "TDD for behavior, written in the language of the business."

---

### 7. The Test Pyramid

**Definition:** A guideline for test *distribution* — lots of fast cheap unit tests at the base, fewer integration tests, very few slow expensive E2E tests at the top.

```
            /\
           /E2E\        ← few:  slow, brittle, expensive (full journeys)
          /──────\
         / INTEG  \     ← some: components + API together
        /──────────\
       /    UNIT     \  ← many: fast, cheap, isolated (functions/components)
      /──────────────\

   ▲ confidence / cost / time          ▼ speed / quantity
```

**Anti-pattern — the "ice-cream cone"** (inverted pyramid): mostly E2E + manual testing, few units → slow, flaky CI that's painful to maintain. Avoid it.

💡 **Remember:** **"Many unit, some integration, few E2E."** Pyramid points UP; if it's top-heavy (ice-cream cone), it'll melt (flaky/slow CI).

❓ *What is the test pyramid / why?* It prescribes most tests be unit (fast, deterministic, pinpoint the failing line), fewer integration, and a thin E2E layer for critical journeys. Rationale: E2E tests are slow and flaky and a poor ROI in bulk, so you get fast feedback and stability by pushing coverage down to the cheap base — while keeping a few E2E tests for true end-to-end confidence (e.g., login → transfer → balance updates).

---

### 🎯 Putting it together — RTL test of a component that fetches data (mocked)

This is the **canonical interview code sample** — a component that loads data, and a test that mocks the API, handles the async load, and asserts on what the user sees.

```tsx
// AccountBalance.tsx
import { useEffect, useState } from "react";
import { getBalance } from "./api";

export function AccountBalance({ accountId }: { accountId: string }) {
  const [balance, setBalance] = useState<number | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true; // guard against setting state after unmount / id change
    getBalance(accountId)
      .then((b) => active && setBalance(b))
      .catch(() => active && setError(true));
    return () => { active = false; };
  }, [accountId]);

  if (error) return <p role="alert">Failed to load balance</p>;
  if (balance === null) return <p>Loading…</p>;
  return <p>Balance: ${balance.toFixed(2)}</p>;
}
```

```tsx
// AccountBalance.test.tsx
import { render, screen } from "@testing-library/react";
import { AccountBalance } from "./AccountBalance";
import { getBalance } from "./api";

jest.mock("./api"); // auto-mock the module → getBalance is a jest.fn()
const mockGetBalance = getBalance as jest.MockedFunction<typeof getBalance>;

afterEach(() => jest.clearAllMocks()); // reset call history between tests

describe("AccountBalance", () => {
  it("shows a loading state, then the fetched balance", async () => {
    mockGetBalance.mockResolvedValue(2500);

    render(<AccountBalance accountId="ACC-1" />);

    // GIVEN it's still loading (synchronous first render)
    expect(screen.getByText(/loading/i)).toBeInTheDocument();

    // THEN the balance appears — findBy* awaits the async re-render
    expect(await screen.findByText("Balance: $2500.00")).toBeInTheDocument();
    expect(mockGetBalance).toHaveBeenCalledWith("ACC-1");
  });

  it("shows an error message when the API rejects", async () => {
    mockGetBalance.mockRejectedValue(new Error("network"));

    render(<AccountBalance accountId="ACC-1" />);

    // role="alert" → accessible (announced by screen readers) + queryable
    expect(await screen.findByRole("alert")).toHaveTextContent(/failed to load/i);
  });
});
```

**What this demonstrates to an interviewer (say it out loud):**
1. **Mock the boundary** — `jest.mock('./api')`, not the component's internals.
2. **`findBy*` for async** — auto-retries until React re-renders, and its built-in `waitFor` wraps the update in `act()`, so you avoid manual `act()` and "not wrapped in act" warnings.
3. **Query by role/text** — tests behavior + accessibility (`role="alert"` is screen-reader-friendly, which matters in banking).
4. **Test both paths** — happy path *and* error path (resilience is non-negotiable in fintech).
5. **Isolation** — `jest.clearAllMocks()` between tests + per-test mock setup → deterministic, order-independent runs.

> Setup gotchas worth naming: `.toBeInTheDocument()` requires `@testing-library/jest-dom`; with `userEvent` v14 you'd `setup()` and `await` interactions; and never assert on a balance with raw float math — render via `toFixed`/`Intl.NumberFormat` so `$2500.00` is exact.

---

### ⚡ 30-second cheat-sheet

| Concept | One-liner |
|---|---|
| `describe/it/expect` | group / case / assert |
| `toBe` vs `toEqual` | reference (`Object.is`) vs deep value |
| `toThrow` | wrap call in arrow; matches message substring/regex |
| RTL query order | Role → Label → Text → TestId |
| `get/query/find` | throws-now / null-if-absent / async-await |
| `userEvent` > `fireEvent` | full real interaction (v14 = async) |
| `jest.fn()` / `jest.mock()` / MSW | fake fn / fake module / fake network |
| `clear/reset/restore` mocks | history / +impl / original impl |
| Test types | unit → integration → E2E |
| TDD | Red → Green → Refactor |
| BDD | Given → When → Then |
| Pyramid | many unit, some integration, few E2E |

💡 **Master mnemonic:** *"**G**ROW your tests" — **G**iven-When-Then (BDD), **R**ed-Green-Refactor (TDD), **O**ne-some-whole (pyramid), **W**atch behavior not implementation (RTL).*

---

## ⚡ Performance

---

### React.memo

**Definition:** A higher-order component that memoizes a component, skipping re-render when its props are shallow-equal to the previous render.

```
Parent re-renders
      │
      ▼
 props changed? ──No──▶ React.memo skips child render (reuses last output)
      │Yes
      ▼
   re-render child
```

```tsx
type Props = { label: string; balance: number };

const AccountTile = React.memo(function AccountTile({ label, balance }: Props) {
  console.log("render", label); // only logs when label/balance actually change
  return <div>{label}: ${balance.toFixed(2)}</div>;
});

// Optional custom comparator: return true = "props equal" = SKIP render
// NOTE: when you supply one, YOU own the whole comparison — list every prop that matters
const Memoized = React.memo(AccountTile, (prev, next) =>
  prev.label === next.label && prev.balance === next.balance
);
```

**💡 Remember:** *memo = "memo to React: don't bother re-rendering unless props changed."* Shallow compare only — a new object/array/function prop defeats it. (`memo` only guards props; the component still re-renders when its own `useState`/`useContext` change.)

**❓ Does React.memo deep-compare props?** No — shallow by default. `{a:1}` vs a fresh `{a:1}` are different references, so it re-renders. Provide a custom comparator or memoize the prop with `useMemo`/`useCallback`.

---

### useMemo

**Definition:** Caches the *result* of an expensive computation, recomputing only when its dependencies change.

```
deps unchanged ─▶ return cached value
deps changed   ─▶ recompute, cache, return
```

```tsx
function TransactionsTable({ txns, query }: { txns: Txn[]; query: string }) {
  // Without useMemo this filter+sort runs on EVERY render
  const visible = useMemo(
    () => txns.filter(t => t.payee.includes(query))
              .sort((a, b) => b.date - a.date), // sort copies first — see note
    [txns, query] // recompute only when these change
  );
  return <List rows={visible} />;
}
```

> ⚠️ `Array.prototype.sort` mutates **in place**. `filter` returns a fresh array here, so this is safe — but never `.sort()` a prop/state array directly; copy first (`[...txns].sort(...)`).

**💡 Remember:** *useMemo memoizes a **value**.* Use it for heavy compute or to keep a referentially-stable object/array prop you pass to a memoized child. (It's a *cache hint*, not a guarantee — React may discard it, so never rely on it for correctness.)

**❓ When NOT to use useMemo?** For cheap calculations — the cache bookkeeping + extra memory can cost more than it saves. Don't wrap everything; profile first.

---

### useCallback

**Definition:** Caches a *function instance* so the same reference is passed across renders (useful when a child is memoized or the function is a hook dependency).

```
useCallback(fn, deps)  ≡  useMemo(() => fn, deps)
   (memoizes the FUNCTION, not its return value)
```

```tsx
function Payments({ onPay }: { onPay: (id: string) => void }) {
  // Stable reference → memoized PayButton won't re-render each time Payments does
  const handlePay = useCallback((id: string) => onPay(id), [onPay]);
  return <PayButton onClick={handlePay} />;
}
```

**💡 Remember:** **useCallback = useMemo for functions.** `useCallback(fn, d)` === `useMemo(() => fn, d)`. Only pays off when paired with `React.memo`d children or used as an effect/hook dependency.

**❓ useMemo vs useCallback?** `useMemo` returns the *value* a function produces; `useCallback` returns the *function itself*. `useCallback(fn, d)` is sugar for `useMemo(() => fn, d)`.

---

### Avoiding needless renders (putting it together)

**Definition:** Combine `React.memo` (skip child render) + `useMemo`/`useCallback` (keep prop references stable) so a parent re-render doesn't cascade into untouched children.

```
❌  Parent renders → new {} / new () => {} props → memo'd child STILL re-renders
✅  Parent renders → stable props via useMemo/useCallback → memo'd child SKIPPED
```

```tsx
type RowProps = { acct: Acct; onSelect: (id: string) => void };

const Row = React.memo(({ acct, onSelect }: RowProps) => (
  <li onClick={() => onSelect(acct.id)}>{acct.name}</li>
));

function Accounts({ accounts }: { accounts: Acct[] }) {
  const navigate = useNavigate();
  // stable across renders → empty deps is safe because navigate is stable
  const onSelect = useCallback((id: string) => navigate(`/acct/${id}`), [navigate]);
  return <ul>{accounts.map(a => <Row key={a.id} acct={a} onSelect={onSelect} />)}</ul>;
}
```

**💡 Remember:** The trio works as a **team** — `memo` alone is useless if you hand it a fresh object/function each render. Memoize the *props*, then memoize the *child*. (React 19's compiler can auto-memoize much of this — but knowing the manual mechanics still matters.)

---

### Code-splitting with React.lazy + Suspense

**Definition:** Defer loading a component's JS until it's actually rendered, shrinking the initial bundle; `Suspense` shows a fallback while the chunk downloads.

```
Initial bundle:  [App core] ──────────────▶ fast first paint
On route hit:    ── fetch Reports.chunk.js ──▶ <Suspense fallback> shows spinner
                                              ──▶ chunk arrives → renders Reports
```

```tsx
import { lazy, Suspense } from "react";
import { Routes, Route } from "react-router-dom";

const Statements = lazy(() => import("./Statements")); // separate chunk
const Transfers  = lazy(() => import("./Transfers"));

export default function App() {
  return (
    <Suspense fallback={<Spinner aria-label="Loading…" />}>
      <Routes>
        <Route path="/statements" element={<Statements />} />
        <Route path="/transfers"  element={<Transfers  />} />
      </Routes>
    </Suspense>
  );
}
```

**💡 Remember:** **lazy = "load it later."** `lazy()` must resolve to a module with a *default export* (or map a named export yourself), and must render inside a `Suspense` boundary. Great for routes, modals, heavy charts.

**❓ Why code-split?** Smaller initial JS → faster LCP/INP. Users only download code for the screens they visit. Split by route first, then by heavy components (charts, editors, PDF viewers).

---

### List virtualization (react-window)

**Definition:** Render only the rows currently visible in the viewport (plus a small buffer) instead of mounting thousands of DOM nodes.

```
10,000 transactions, viewport shows ~12:

 DOM without virtualization:  [████████████████████ 10,000 nodes] 😱
 DOM with react-window:       [██ ~15 nodes, recycled on scroll] 🚀
                              ▲ window slides as you scroll
```

```tsx
import { FixedSizeList as List } from "react-window";

// Define Row OUTSIDE render (or memoize) so it isn't recreated each render
function TxnList({ txns }: { txns: Txn[] }) {
  const Row = ({ index, style }: { index: number; style: React.CSSProperties }) => (
    <div style={style}>{txns[index].payee} — ${txns[index].amount}</div>
  );
  return (
    <List height={400} itemCount={txns.length} itemSize={48} width="100%">
      {Row}
    </List>
  );
}
```

**💡 Remember:** **Virtualize = "only mount what you can see."** The `style` prop (absolute positioning) is mandatory — it's how the list places each row. Reach for it when lists exceed a few hundred rows. (Trade-off: in-page Ctrl+F, tab-to-offscreen-row, and print won't see un-rendered rows.)

---

### Stable keys

**Definition:** Give list items a unique, persistent `key` so React can match elements across renders instead of re-creating/misplacing DOM and state.

```
key=index, then prepend an item:
  before: [A=0, B=1]   after: [NEW=0, A=1, B=2]
  React thinks 0 changed A→NEW, 1 changed B→A … re-renders everything, state shifts ❌

key=item.id:
  React sees NEW is new, A & B unchanged → only inserts one node ✅
```

```tsx
{accounts.map(a => <Row key={a.id} acct={a} />)}   // ✅ stable, unique id
{accounts.map((a, i) => <Row key={i} acct={a} />)} // ❌ index breaks on reorder/insert
```

**💡 Remember:** **Key by identity, not position.** Index keys are a bug magnet for lists that reorder, filter, or insert — they scramble component state and hurt diffing. (Index keys are fine *only* for static, append-only lists that never reorder.)

**❓ Why not use array index as key?** On insert/reorder/delete the index→item mapping shifts, so React reuses the wrong DOM/state and may re-render unnecessarily. Use a stable domain id.

---

### Bundle size

**Definition:** The total JS/CSS the browser must download, parse, and execute before the app is interactive — smaller is faster.

```
Levers:  split (lazy)  │  trim deps (moment→date-fns/dayjs)  │  tree-shake  │  compress (gzip/brotli)
Measure: source-map-explorer / webpack-bundle-analyzer / vite-bundle-visualizer
```

```bash
# Visualize what's actually in your bundle
npx source-map-explorer 'build/static/js/*.js'
# or the import-cost VS Code ext to see per-import weight inline
```

**💡 Remember:** *Every KB of JS is downloaded, parsed AND executed* — and parse/execute on a mid-range phone hurts more than download. Audit deps, prefer light alternatives, lazy-load the rest. "Ship less JavaScript" is the #1 perf lever.

---

### Tree shaking

**Definition:** Dead-code elimination — the bundler drops exports you never import, relying on static ES-module (`import`/`export`) analysis.

```
import { debounce } from "lodash-es";   // ✅ shakeable → only debounce ships
import _ from "lodash";                  // ❌ whole library pulled in
        └ named ESM imports = tree-shakeable; default/CJS namespace import = not
```

```tsx
// ✅ pulls in just one function
import debounce from "lodash/debounce";   // deep path import (works even for CJS lodash)
import { debounce } from "lodash-es";     // ✅ ESM build → tree-shakeable
// ❌ import _ from "lodash"; _.debounce(...)  ← bundles ALL of lodash
```

**💡 Remember:** **Tree-shaking needs ESM + named imports + `"sideEffects": false`.** Import the *branch*, not the whole *tree*. CommonJS (`require`) can't be statically shaken.

**❓ What breaks tree shaking?** CommonJS modules, namespace/default imports of big libs, and packages without `"sideEffects": false` in `package.json` (the bundler must then assume every imported module has side effects and keep it).

---

### Lazy-loading images (loading="lazy")

**Definition:** Native browser attribute that defers off-screen image loading until the user scrolls near them.

```
<img loading="lazy">  ──▶  below the fold? skip download until ~near viewport
                              ▲ saves bandwidth, speeds initial load
```

```html
<img src="/statement-preview.webp" loading="lazy" width="320" height="180"
     alt="March statement preview" decoding="async" />
```

**💡 Remember:** **`loading="lazy"` = "don't fetch till near."** Always set explicit `width`/`height` (or `aspect-ratio`) so the placeholder reserves space — that prevents CLS. *Don't* lazy-load above-the-fold/LCP images (it delays LCP); give those `fetchpriority="high"` instead.

---

### Caching / CDN

**Definition:** Serve static assets from edge servers near the user and tell browsers to reuse them via HTTP cache headers — fewer/closer round-trips.

```
User ──▶ nearest CDN edge (cached, ~10ms) ──▶ origin (only on miss)

Hashed filename + immutable cache:
  app.4f3a9b.js   Cache-Control: max-age=31536000, immutable
  (new build ⇒ new hash ⇒ guaranteed fresh, old one stays cached)
```

```http
Cache-Control: public, max-age=31536000, immutable   # hashed static assets
Cache-Control: no-cache                                # index.html (always revalidate)
```

**💡 Remember:** **Hash + immutable for assets, `no-cache` for `index.html`.** The content-hash in the filename is what makes "cache forever" safe — a code change changes the hash. (`no-cache` ≠ "don't cache"; it means "cache but revalidate every time." `no-store` means truly never store.)

---

### Core Web Vitals — LCP

**Definition:** **Largest Contentful Paint** — time until the biggest visible element (hero image, headline, main text block) is rendered. **Good ≤ 2.5s** (measured at the 75th percentile of real users).

```
0s ──── FCP ──────── LCP ──────────▶
        ▲first pixel  ▲largest element painted   target: ≤2.5s
```

**Fixes:** optimize/preload the LCP image, server-side render or stream, cut render-blocking CSS/JS, use a CDN, `fetchpriority="high"` on the hero image.

```html
<link rel="preload" as="image" href="/hero.webp" fetchpriority="high" />
```

**💡 Remember:** **LCP = "how fast does the main thing show up?"** It's about *loading*. Biggest wins: faster server/CDN + a fast, prioritized hero image.

---

### Core Web Vitals — INP (replaced FID)

**Definition:** **Interaction to Next Paint** — measures responsiveness: the latency from a user interaction (click/tap/key) to the next visual update, reporting (roughly) the worst interaction across the whole visit. **Good ≤ 200ms.** Replaced **FID** as a Core Web Vital in March 2024.

```
click ─▶ [input delay] ─▶ [JS handler runs] ─▶ [render] ─▶ next paint
        └──────────────── INP measures this whole gap ──────────────┘   target ≤200ms
FID = only the *input delay* before the handler started;  INP = full interaction → paint
```

**Fixes:** break up long tasks, defer non-urgent work (`useTransition`/`startTransition`), debounce handlers, avoid heavy synchronous renders, virtualize big lists.

```tsx
const [isPending, startTransition] = useTransition();

const onType = (e: React.ChangeEvent<HTMLInputElement>) => {
  const next = e.target.value;
  setText(next);                                   // urgent: keep input snappy
  startTransition(() => setResults(filter(next))); // non-urgent: low priority
};
```

**💡 Remember:** **INP = "how fast does the UI respond when I poke it?"** FID only timed the *delay before* handling; INP times the *whole* interaction to paint. Keep the main thread free.

**❓ What replaced FID and why?** INP, since it measures the *full* interaction-to-paint latency (not just initial input delay) and considers *all* interactions during the visit, giving a truer picture of responsiveness.

---

### Core Web Vitals — CLS

**Definition:** **Cumulative Layout Shift** — a unitless score of how much visible content unexpectedly jumps around during the page's life. **Good ≤ 0.1.**

```
Without reserved space:          With width/height (or aspect-ratio):
 [ text ]                         [ text ]
 [ text ]   ⟵ image loads,        [ IMG  ]   ⟵ space reserved up front
 [ IMG  ]      shoves text down    [ text ]      nothing jumps  ✅
   😱 user mis-taps "Transfer"
```

**Fixes:** set `width`/`height`/`aspect-ratio` on images & ads, reserve space for dynamic content, avoid inserting banners above existing content, use `font-display: optional/swap` for fonts.

```css
img { aspect-ratio: 16 / 9; width: 100%; height: auto; } /* reserves space → no shift */
```

**💡 Remember:** **CLS = "does stuff jump around?"** Reserve space for anything that loads late (images, ads, fonts, embeds). Layout shifts within ~500ms of a user interaction don't count — only *unexpected* shifts. In banking, a jump can make a user mis-tap "Transfer/Confirm" — real stakes.

---

### Tools — Lighthouse

**Definition:** Automated audit (Chrome DevTools / CI) scoring Performance, Accessibility, Best Practices, SEO, with Core Web Vitals and concrete fix suggestions.

```
DevTools ▸ Lighthouse ▸ Analyze
 Perf 72 │ A11y 95 │ BP 100 │ SEO 90
 Opportunities: "Eliminate render-blocking resources −1.2s", "Properly size images −0.8s"
```

**💡 Remember:** **Lighthouse = your perf/a11y report card.** Lab data (simulated device/network), great for catching regressions in CI; pair with *field* data (CrUX/real users) since lab ≠ real network. Note: Lighthouse's lab score uses TBT, not INP — INP needs field data.

---

### Tools — React Profiler

**Definition:** React DevTools tab that records renders, showing which components rendered, how long they took, and *why*.

```
Flamegraph (wider/yellower = slower):
  App ▏
   └ Dashboard ▏▏▏▏▏▏  18ms  ⟵ hot
       ├ Chart ▏▏▏▏     12ms  ⟵ investigate
       └ List  ▏        2ms
 "Why did this render?" → props/state/hooks/parent
```

**💡 Remember:** **Profiler answers "what rendered and why?"** Turn on "Record why each component rendered" in DevTools settings. Wide yellow bars = your optimization targets (memoize, split, virtualize).

---

### Tools — why-did-you-render

**Definition:** A dev-only library that logs to the console whenever a tracked component re-renders with props/state that are *equal in value but different by reference* (i.e., avoidable renders).

```
[WDYR] AccountTile re-rendered because prop "style" changed:
        prev {} !== next {}   (same value, new reference) ⟵ memoize this!
```

```tsx
// wdyr.ts — must be imported FIRST in your entry file, development only
import React from "react";

if (process.env.NODE_ENV === "development") {
  const wdyr = require("@welldone-software/why-did-you-render");
  wdyr(React, { trackAllPureComponents: true });
}
```

**💡 Remember:** **WDYR = "why did you render?" snitch.** It pinpoints renders caused by unstable object/function props — exactly the ones `useMemo`/`useCallback` fix. Dev-only; never ship it to production.

---

### 💡 Diagnose & fix a slow React page — step by step

**Definition:** A repeatable playbook: measure first, find the bottleneck, fix the biggest one, re-measure.

```
1. MEASURE   Lighthouse + DevTools Performance + Network → which Web Vital is bad?
2. NETWORK?  Big bundle / slow assets → code-split (lazy), tree-shake, compress, CDN, lazy images
3. RENDER?   React Profiler / WDYR → too many or too slow renders?
4. FIX           ├ needless re-renders → React.memo + useMemo/useCallback (stable props)
                 ├ huge list          → react-window virtualization
                 ├ heavy compute      → useMemo / move to a Web Worker / startTransition
                 └ janky input (INP)  → debounce + useTransition, split long tasks
5. STABILITY CLS → set image width/height/aspect-ratio, reserve space, font-display
6. RE-MEASURE  Confirm the vital improved; guard with Lighthouse CI to prevent regressions.
```

**💡 Remember:** **Measure → isolate → fix the biggest → re-measure.** Never optimize by guesswork: *"profile, don't speculate."* Network problems and render problems have different fixes — diagnose which one you have first.

**❓ A page feels slow — walk me through it.** "First I'd *measure*, not guess: Lighthouse for Web Vitals plus the Performance/Network tabs to split network vs. runtime. If it's network — big bundle or heavy assets — I code-split with `React.lazy`, tree-shake, compress (brotli), and put assets on a CDN with long cache headers. If it's runtime, the React Profiler (and why-did-you-render) shows wasteful renders, which I fix with `React.memo` + `useMemo`/`useCallback` and stable keys; long lists get virtualized with `react-window`; heavy work moves into `useMemo`/`startTransition` to protect INP. For CLS I reserve space with width/height/aspect-ratio. Then I *re-measure* and add Lighthouse CI so it doesn't regress."

---

### 🎯 One-glance recap

| Concern | Tool / API | Fix |
|---|---|---|
| Needless re-render | React.memo + useMemo/useCallback | stable props, memoize child |
| Big initial bundle | React.lazy + Suspense, tree-shaking | split by route, trim deps |
| Huge list | react-window | virtualize visible rows |
| Wrong DOM reuse | stable `key` | key by domain id, not index |
| LCP (load ≤2.5s) | preload, CDN, SSR | fast/prioritized hero, less blocking JS |
| INP (≤200ms) | useTransition, debounce | short tasks, free main thread |
| CLS (≤0.1) | width/height, aspect-ratio | reserve space for late content |
| Find the problem | Lighthouse, Profiler, WDYR | measure → isolate → fix → re-measure |

---

## 🎨 HTML + CSS

A crisp, interview-ready revision of HTML semantics, accessibility, and the CSS layout/specificity model. Banking front-ends live or die on accessibility and predictable layout, so every sub-topic below ties back to "why it matters in a customer-facing app."

---

### 1. Semantic HTML Elements

**Definition:** Tags that describe the *meaning* of content (`header`, `nav`, `main`) rather than just its appearance (`div`, `span`).

```
┌─────────────────────────────────────────┐
│ <header>   logo + top nav                │
├──────────┬──────────────────────────────┤
│ <nav>    │ <main>                        │
│ sidebar  │   <article> account summary   │
│          │   <section> transactions      │
│          │     <aside> related offers    │
├──────────┴──────────────────────────────┤
│ <footer>   legal, contact                │
└─────────────────────────────────────────┘
```

```html
<header>
  <nav aria-label="Primary">
    <a href="/accounts">Accounts</a>
  </nav>
</header>
<main>
  <article>
    <h1>Checking Account</h1>
    <section aria-labelledby="txn-h">
      <h2 id="txn-h">Recent Transactions</h2>
    </section>
  </article>
</main>
<footer><small>© Wells Fargo</small></footer>
```

**Why it matters:** Screen readers build a *landmark* map from these tags, letting users jump straight to `main` or `nav`; SEO and maintainability improve too. A page of `<div>`s is invisible structure to assistive tech.

> 💡 **Remember:** "Divs are dumb, tags talk." Semantic tags = free accessibility + SEO. Landmark roles map automatically: `header`→`banner`, `nav`→`navigation`, `main`→`main`, `aside`→`complementary`, `footer`→`contentinfo`.

> ❓ **Interview — "div vs section vs article?"**
> - `div`: no meaning, pure styling/grouping hook.
> - `section`: thematic grouping that *needs a heading* (a tab panel, a chapter).
> - `article`: a self-contained, independently distributable unit (a blog post, a transaction card, a comment) — would make sense syndicated on its own.

---

### 2. Forms: label / input / fieldset

**Definition:** Form controls bound to descriptive labels and grouped semantically so every input is programmatically identifiable.

```
<fieldset>  ─ groups related controls + gives a <legend> caption
  <legend>Transfer Details</legend>
  <label> ── ties text to ONE control (click label focuses input)
    └─ for="id"  ⇄  input id="id"
```

```tsx
<form onSubmit={handleSubmit}>
  <fieldset>
    <legend>Transfer Funds</legend>

    {/* Explicit label: htmlFor === id */}
    <label htmlFor="amount">Amount (USD)</label>
    <input
      id="amount"
      name="amount"
      type="number"
      inputMode="decimal"
      required
      aria-describedby="amount-help"
    />
    <span id="amount-help">Daily limit $5,000</span>

    {/* Implicit label: wraps the control */}
    <label>
      <input type="checkbox" name="confirm" /> I confirm this transfer
    </label>
  </fieldset>
  <button type="submit">Send</button>
</form>
```

**Three ways to label:** explicit (`for`/`id`), implicit (wrap), or `aria-label`/`aria-labelledby` when no visible text exists. **Prefer a real `<label>`** — it gives a bigger click target (clicking the label focuses/toggles the control) and is read on focus.

> 💡 **Remember:** "**Every input needs a label, every group needs a legend.**" `for` points to `id` like a name tag points to a person. (In JSX it's `htmlFor` because `for` is a reserved word.)

> ❓ **Interview — "Why not just placeholder text instead of a label?"**
> Placeholders vanish on input (no persistent context), have poor contrast, aren't reliably announced by screen readers, and break for users who forget what the field was. Placeholder = *hint*, label = *name*. Always need the label.

---

### 3. Accessibility Basics (a11y)

**Definition:** Building UI usable by everyone — keyboard, screen reader, low vision — via semantics, ARIA, focus management, and contrast.

```
A11y pillars:
  SEMANTICS  → use the right element (button, not div onClick)
  KEYBOARD   → Tab order, focus visible, Enter/Space activate
  ARIA       → roles/states when HTML can't express it
  CONTRAST   → 4.5:1 normal text, 3:1 large text (WCAG AA)
  ALT/LABELS → images & icons describe themselves
```

```tsx
// ❌ Inaccessible: div isn't focusable, no keyboard, no role
<div onClick={pay}>Pay</div>

// ✅ Native button: focusable, Enter/Space work, announced as "button"
<button onClick={pay}>Pay</button>

// Icon-only button still needs an accessible name
<button aria-label="Close dialog" onClick={close}>✕</button>

// Live region: announce async updates (e.g., "Transfer complete")
<div role="status" aria-live="polite">{statusMessage}</div>
```

**Key rule (ARIA Rule #1):** *No ARIA is better than bad ARIA* — use a native element before reaching for a `role`. Manage focus on route/modal changes (`element.focus()`), keep a visible `:focus` outline, ensure 4.5:1 contrast for normal text.

> 💡 **Remember:** **"POUR"** = **P**erceivable, **O**perable, **U**nderstandable, **R**obust (the 4 WCAG principles). And: *"If you reach for a div with onClick, you owe ARIA a debt."*

> ❓ **Interview — "How do you make a custom dropdown accessible?"**
> Use native `<select>` if possible. If custom: `role="listbox"` on the popup / `role="option"` on items, manage `aria-expanded` on the trigger, `aria-activedescendant` to track the highlighted option, full keyboard support (arrows, Enter, Esc, type-ahead), trap/restore focus, and ensure it's announced. Mention testing with keyboard-only + a screen reader (NVDA/VoiceOver).

---

### 4. The Box Model

**Definition:** Every element is a box of four layers — content → padding → border → margin — and `box-sizing` decides whether the declared `width`/`height` includes padding and border.

```
        ┌─────────── margin (transparent, outside) ───────────┐
        │   ┌────────── border ──────────┐                    │
        │   │   ┌────── padding ──────┐   │                   │
        │   │   │     CONTENT         │   │                   │
        │   │   │   (width×height)    │   │                   │
        │   │   └─────────────────────┘   │                   │
        │   └─────────────────────────────┘                   │
        └─────────────────────────────────────────────────────┘

content-box (default): width = content only  → rendered width = w + pad + border
border-box           : width = content+pad+border → rendered width = w  (predictable!)
```

```css
/* Make every element predictable — width means total width */
*, *::before, *::after {
  box-sizing: border-box;
}

.card {
  width: 300px;      /* with border-box: TOTAL box is 300px */
  padding: 20px;
  border: 2px solid #ccc;
  margin: 16px;      /* margin is ALWAYS outside the 300px */
}
```

**The classic trap:** with default `content-box`, `width:300px; padding:20px; border:2px` renders **344px** wide (300 + 20×2 + 2×2). `border-box` keeps the rendered box at 300 (content shrinks to 256).

> 💡 **Remember:** Inside-out order = **"C**ontent **P**adding **B**order **M**argin" → *"**C**areful **P**eople **B**uild **M**argins."* And: **`border-box` = WYSIWYG width.** Margin is never inside the box.

> ❓ **Interview — "What's margin collapse?"**
> Adjacent **vertical** margins of block-level boxes *merge* into the larger of the two (not the sum). E.g. `margin-bottom:20px` + next element's `margin-top:30px` = 30px gap, not 50. Applies to adjacent siblings and parent/child (when nothing separates them). Only vertical, only block-flow boxes; flex/grid items, floats, absolutely-positioned elements, and `overflow` containers (e.g. `overflow:auto`) don't collapse. Horizontal margins never collapse.

---

### 5. Flexbox

**Definition:** A *one-dimensional* layout system that distributes space along a single axis (row or column) with powerful alignment.

```
flex-direction: row  →  MAIN axis = horizontal,  CROSS axis = vertical
 ┌──────────────────────────────────────────────┐
 │ [A] [B] [C]            ← justify-content (MAIN)│   ↕ align-items
 └──────────────────────────────────────────────┘     (CROSS)

justify-content (along MAIN):  flex-start | center | flex-end
                               space-between | space-around | space-evenly
align-items     (along CROSS): flex-start | center | flex-end | stretch | baseline
```

```css
.flex { display: flex; }
.row    { flex-direction: row; }    /* main = → */
.column { flex-direction: column; } /* main = ↓ , axes SWAP */

/* Perfect centering — the famous one-liner */
.center { display: flex; justify-content: center; align-items: center; }

/* Item growth: grow / shrink / basis */
.sidebar { flex: 0 0 240px; }  /* don't grow, don't shrink, 240px base */
.content { flex: 1; }          /* = 1 1 0% : take all remaining space */
```

**Flex navbar (common ask):**

```css
.navbar {
  display: flex;
  align-items: center;            /* vertical center, all items */
  justify-content: space-between; /* logo left, links right */
  gap: 1.5rem;                    /* modern spacing, no margins */
  padding: 0 1.5rem;
}
.navbar .links { display: flex; gap: 1rem; }
```
```html
<nav class="navbar">
  <a class="logo" href="/">🏦 Bank</a>
  <div class="links">
    <a href="/accounts">Accounts</a>
    <a href="/transfer">Transfer</a>
    <a href="/logout">Log out</a>
  </div>
</nav>
```
*(With only two flex children — `.logo` and `.links` — `space-between` pins the logo left and links right. If you instead drop the wrapper and want to push one item over, `margin-left:auto` on that item shoves it and everything after it to the end.)*

> 💡 **Remember:** **"justify = main, align = cross."** When `flex-direction: column`, the axes flip — `justify-content` now controls *vertical*. `margin-left:auto` = "push me and everything after me to the end of the main axis."

> ❓ **Interview — "Flexbox vs Grid?"**
> Flexbox = **1D** (content flows along one axis; size driven by content). Grid = **2D** (rows *and* columns simultaneously; layout-first). Rule of thumb: **components → Flexbox** (navbars, toolbars, button rows), **page layout → Grid** (dashboards, card galleries). They compose — grid cells often contain flex.

---

### 6. CSS Grid

**Definition:** A *two-dimensional* layout system defining explicit rows and columns, with optional named template areas.

```
grid-template-columns: 1fr 2fr 1fr;   grid-template-rows: auto 1fr auto;

   col1(1fr) col2(2fr) col3(1fr)
 ┌─────────┬───────────┬─────────┐
 │ header (spans all 3 columns)  │  row1 auto
 ├─────────┼───────────┼─────────┤
 │ nav     │  main     │  aside  │  row2 1fr
 ├─────────┴───────────┴─────────┤
 │ footer (spans all 3 columns)  │  row3 auto
 └───────────────────────────────┘
```

```css
.layout {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  grid-template-rows: auto 1fr auto;
  grid-template-areas:
    "header header header"
    "nav    main   aside"
    "footer footer footer";
  gap: 1rem;
  min-height: 100vh;
}
.layout > header { grid-area: header; }
.layout > nav    { grid-area: nav; }
.layout > main   { grid-area: main; }
.layout > aside  { grid-area: aside; }
.layout > footer { grid-area: footer; }
```

**Responsive grid (auto-fit + minmax — no media queries needed):**

```css
.cards {
  display: grid;
  /* Fit as many ≥240px columns as fit; they share leftover space */
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
}
```
```
1 wide screen → [▮][▮][▮][▮]
  narrower    → [▮][▮]
  mobile      → [▮]          ← auto-reflows, zero media queries
```

> 💡 **Remember:** **`fr`** = "fraction of leftover space." **`minmax(min, max)`** + **`auto-fit`** = self-responsive grid. Template areas = *draw your layout with ASCII art in CSS* — what you see is what you get.

> ❓ **Interview — "`auto-fit` vs `auto-fill`?"**
> Both create as many tracks as fit the container. `auto-fill` keeps *empty* phantom columns (items stay their min size, leaving gaps on the right). `auto-fit` *collapses* those empty tracks to 0 so existing items **stretch** to fill the row. For card grids you usually want **auto-fit**.

---

### 7. Position

**Definition:** `position` controls how an element is placed and what `top/right/bottom/left` are relative to.

```
static   → normal flow; top/left IGNORED (default)
relative → offset from its OWN normal spot; still occupies original space
absolute → removed from flow; positioned vs nearest POSITIONED ancestor
fixed    → removed from flow; positioned vs the VIEWPORT (stays on scroll)
sticky   → relative until a scroll threshold, then "sticks" (hybrid)

 relative parent ─┐
 ┌────────────────┴─────────────┐
 │ .parent {position:relative}  │
 │    ┌──────────┐              │
 │    │ absolute │ top:0;right:0│ ← anchored to parent, not page
 │    └──────────┘              │
 └──────────────────────────────┘
```

```css
.dropdown        { position: relative; }                     /* anchor */
.dropdown__menu  { position: absolute; top: 100%; left: 0; } /* under trigger */

.toast           { position: fixed; bottom: 1rem; right: 1rem; } /* viewport */

.table-header    { position: sticky; top: 0; background: #fff; } /* sticky header */
```

**The #1 pattern:** `position: relative` on the parent + `position: absolute` on the child = "anchor the child inside this box." Without a positioned ancestor, `absolute` is placed relative to the initial containing block (the viewport-sized box), so it jumps to the page.

> 💡 **Remember:** **"Relative parent, absolute child"** is the anchoring combo. **Sticky = relative + fixed had a baby** (flows normally, then pins). **Fixed ignores scroll; absolute ignores siblings.**

> ❓ **Interview — "Why is my `position:absolute` element jumping to the page corner?"**
> No positioned ancestor. `absolute` is relative to the nearest ancestor with `position` other than `static` (or, failing that, the initial containing block ≈ the viewport). Add `position: relative` to the intended parent. (Also: `sticky` silently fails if a scroll-direction threshold like `top` isn't set, or an ancestor clips/scrolls via `overflow:hidden/auto/scroll`.)

---

### 8. z-index & Stacking Context

**Definition:** `z-index` orders overlapping elements on the Z axis — but only *within the same stacking context*, a self-contained layering "bubble."

```
Stacking context = a sealed layer group. A child's z-index:9999
can NEVER escape above a sibling of its PARENT's context.

 Context A (z-index:1) ───────────  Context B (z-index:2)
   └ child z-index:9999  ✗ still BELOW everything in B
   (9999 only competes INSIDE A)

Creates a new stacking context: position≠static + z-index (any value
incl. 0), opacity<1, transform, filter, will-change, isolation:isolate,
flex/grid item with z-index ...
```

```css
.modal-backdrop { position: fixed; z-index: 1000; }
.modal          { position: fixed; z-index: 1001; }

/* Gotcha: this card creates its OWN context — its children
   can't pop above siblings outside this card no matter the z-index */
.card { transform: translateZ(0); /* or opacity:.99, filter, etc. */ }

/* Fix layering wars by isolating intentionally */
.widget { isolation: isolate; }  /* new context, no positioning/z-index hack */
```

**Requirement:** `z-index` only takes effect on **positioned** elements (`relative/absolute/fixed/sticky`) or **flex/grid items**. On a plain `static` element it does nothing.

> 💡 **Remember:** **"z-index is local, not global."** A high z-index is only a big fish *in its own pond (context)*. Context-creators to memorize: **transform, opacity (<1), position+z-index, filter, will-change, isolation, plus `overflow`-clipping doesn't create one but commonly traps `sticky`.**

> ❓ **Interview — "z-index:9999 isn't on top, why?"**
> Its parent forms a stacking context that sits *below* another context, so the child is trapped beneath. Fix: raise the *parent's* z-index, flatten the hierarchy, or remove the property (transform/opacity/filter) that created the unwanted context. z-index competes only among elements in the same stacking context.

---

### 9. Specificity

**Definition:** The scoring system that decides which conflicting CSS rule wins — computed as a tuple **(inline, id, class, element)**.

```
Weight tuple →  (a, b, c, d)   higher tuple wins, compared left→right
                 │  │  │  └ elements & pseudo-elements (p, ::before)
                 │  │  └─── classes, attributes, pseudo-classes (.x, [type], :hover)
                 │  └────── IDs (#id)
                 └───────── inline style="" 

#nav .link a        → (0,1,1,1)
.menu .link a:hover → (0,0,3,1)   ← #nav wins (id beats 3 classes)
a                   → (0,0,0,1)
style="color:red"   → (1,0,0,0)   ← inline beats every selector but not !important
```

```css
/* Compute by counting: */
#sidebar ul li.active a   /* ids=1, classes=1, elements=3 → (0,1,1,3) */
.nav a                    /* (0,0,1,1) — LOSES to the above */
```

**Tie-breaker:** equal specificity → **last one wins** (source order). `!important` jumps to a higher tier above normal declarations (and fighting `!important` with `!important` is a code smell). Note: `:not()`, `:is()`, and `:has()` don't add specificity themselves but take the specificity of their *most specific* argument; `:where()` always adds **zero**. Keep selectors flat (favor single classes, BEM) to avoid wars.

> 💡 **Remember:** **"I-I-C-E"** = **I**nline, **I**d, **C**lass, **E**lement (decreasing power). Or count like a phone number **(0,0,0,0)** and compare left-to-right. *"IDs beat any number of classes."*

> ❓ **Interview — "How do you override a third-party `!important` without `!important`?"**
> You can't beat `!important` with a normal declaration at any specificity — but you *can* win by adding an earlier `@layer` (later layers and unlayered styles win) or, cleanest, use `@layer` so third-party CSS sits in a lower layer. Otherwise you're forced into a more specific `!important`. Best long-term: avoid `!important` and deep selectors so the cascade stays predictable.

---

### 10. Media Queries & Mobile-First Responsive Design

**Definition:** `@media` applies CSS conditionally on viewport features; *mobile-first* means base styles target small screens, then `min-width` queries layer on enhancements upward.

```
MOBILE-FIRST (min-width, scale UP):     DESKTOP-FIRST (max-width, scale DOWN):
  base = phone                            base = desktop
   ├ @min-width:768  → tablet              ├ @max-width:1024 → tablet
   └ @min-width:1024 → desktop             └ @max-width:768  → phone
  ✅ lighter default for slow phones      ⚠️ phones still parse desktop CSS first

 320px        768px         1024px
 │ mobile  →  │  tablet  →  │ desktop
 base styles  +tablet rules +desktop rules   (each query ADDS on)
```

```css
/* 1. Base = mobile: single column, stacked */
.container { display: grid; grid-template-columns: 1fr; gap: 1rem; }
.nav-links { display: none; }            /* hidden behind a hamburger */

/* 2. Tablet and up */
@media (min-width: 768px) {
  .container { grid-template-columns: 1fr 1fr; }
  .nav-links { display: flex; }          /* show full nav */
}

/* 3. Desktop and up */
@media (min-width: 1024px) {
  .container { grid-template-columns: repeat(3, 1fr); }
}

/* Respect user motion preference — important for a11y */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
```

**Essential prerequisite** — without this, mobile zooms out instead of reflowing:
```html
<meta name="viewport" content="width=device-width, initial-scale=1" />
```

> 💡 **Remember:** **"Mobile-first = `min-width`, build UP."** Start small, *add* at breakpoints. The viewport meta tag is the on-switch for responsiveness. Use `rem`/`%`/`fr`, not fixed `px`, so layouts breathe.

> ❓ **Interview — "Why mobile-first over desktop-first?"**
> Performance + progressive enhancement: phones (often weaker/slower networks) load a lean base and don't have to override heavy desktop styles. It forces content-priority thinking, and `min-width` queries read additively ("as the screen grows, add more"), which is easier to reason about and maintain.

---

### ⚡ 30-Second Recall Sheet

| Concept | One-liner |
|---|---|
| Semantic HTML | Tags talk; landmarks = free a11y/SEO |
| Forms | Every input a label, every group a legend |
| A11y | POUR; native element before ARIA |
| Box model | `border-box` = WYSIWYG width; margin is outside |
| Flexbox | 1D; justify=main, align=cross |
| Grid | 2D; `repeat(auto-fit, minmax())` = self-responsive |
| Position | Relative parent + absolute child = anchor |
| z-index | Local to its stacking context, not global |
| Specificity | I-I-C-E (inline>id>class>element); count left-to-right |
| Media queries | Mobile-first, `min-width`, build up |

**Banking-context closers (drop these in interview):** "I lean on semantic HTML and native form controls because a banking app *must* pass WCAG AA — keyboard nav, 4.5:1 contrast, labeled inputs, focus management on route changes. For layout I use Flexbox for components and Grid for page structure with `border-box` globally so widths stay predictable, and I keep specificity flat (BEM/single classes) so styles are maintainable across a large codebase."

---

## 🟢 Node + Express + MongoDB (MERN backend)

A crisp, interview-ready revision of the backend half of MERN, tuned for a customer-facing banking front-end role where security, correctness, and articulation matter.

---

### 1. REST API Principles — resources, verbs, status codes + CRUD mapping

**Plain definition:** REST is an architectural style where you expose **nouns (resources)** at URLs and act on them with HTTP **verbs**, getting back standard **status codes**; it is stateless (every request carries its own auth/context).

**Resource + verb + CRUD + status mapping:**

```
ACTION   VERB    URL                  CRUD     SUCCESS   FAIL
------   ----    ---                  ----     -------   ----
List     GET     /accounts            Read     200       500
Read one GET     /accounts/:id        Read     200       404
Create   POST    /accounts            Create   201       400/422
Replace  PUT     /accounts/:id        Update   200       404/400
Modify   PATCH   /accounts/:id        Update   200       404/400
Delete   DELETE  /accounts/:id        Delete   204       404
```

**Status code families (memorize the buckets):**

```
1xx  info        (rare)
2xx  success     200 OK · 201 Created · 204 No Content
3xx  redirect    301 Moved · 304 Not Modified
4xx  YOU erred   400 Bad Request · 401 Unauthorized · 403 Forbidden
                 404 Not Found · 409 Conflict · 422 Unprocessable
5xx  SERVER erred 500 Internal · 502 Bad Gateway · 503 Unavailable
```

**Key REST traits:** stateless, resource-based URLs (nouns not verbs — `/accounts` not `/getAccounts`), uniform interface, idempotency (GET/PUT/DELETE are idempotent; POST is not).

```js
// Resource-oriented routing — nouns, plural, nested for relationships
router.get('/accounts',                  listAccounts);   // 200
router.post('/accounts',                 createAccount);  // 201
router.get('/accounts/:id',              getAccount);     // 200 / 404
router.patch('/accounts/:id',            updateAccount);  // 200
router.delete('/accounts/:id',           deleteAccount);  // 204
router.get('/accounts/:id/transactions', listTxns);       // nested resource
```

> 💡 **Remember:** **"POST/GET/PUT/PATCH/DELETE = Create/Read/Update/Update/Delete."** Status buckets: **4xx = you (client) messed up, 5xx = server messed up.** 201 for create, 204 for delete.

**❓ Interview: PUT vs PATCH? Which verbs are idempotent?**
PUT **replaces the whole resource** (send the full object); PATCH **modifies part** of it. Idempotent = same result no matter how many times you call it: **GET, PUT, DELETE are idempotent**; **POST is not** (calling it twice creates two records). PATCH is *usually* idempotent but not guaranteed (e.g. `{$inc: 1}` style ops aren't). Note: idempotency is about **resource state**, not the response code — a second DELETE may return 404 yet leave the resource just as deleted.

**❓ Interview: 401 vs 403?**
**401 Unauthorized** = "I don't know who you are" (missing/invalid auth — log in). **403 Forbidden** = "I know who you are, but you're not allowed" (valid auth, insufficient permission). *Banking note:* never leak existence — for a record the user can't access, returning **404** instead of 403 avoids confirming the resource exists.

---

### 2. Express middleware — `(req, res, next)` and the request lifecycle

**Plain definition:** Middleware is a function `(req, res, next)` that runs in order during a request, can read/mutate `req`/`res`, and must either **end the response** or call **`next()`** to pass control onward.

**Request lifecycle (top → bottom, first match wins):**

```
  Incoming request
        │
        ▼
 ┌──────────────┐ app-level middleware (run for every request)
 │ helmet()     │  security headers
 │ cors()       │
 │ express.json │  body parser → fills req.body
 │ logger       │  morgan / pino
 └──────┬───────┘
        │ next()
        ▼
 ┌──────────────┐ router / route-level middleware
 │ authGuard    │  verify JWT → req.user   (or 401 and STOP)
 │ validate()   │  reject bad input → 422
 └──────┬───────┘
        │ next()
        ▼
   route handler  ───► res.json(...)   ⟵ response sent, chain ends
        │ (if it throws / next(err))
        ▼
  error-handling middleware (4 args)  ───► res.status(...).json(...)
```

Key rule: **a request must end in exactly one response.** Forgetting `next()` (and not sending a response) = the request hangs. Calling `next()` *after* sending a response = "headers already sent" error.

```js
import express from 'express';
import crypto from 'crypto';

const app = express();

// 1) App-level: runs on EVERY request, in declaration order
app.use(express.json());                  // parse JSON body → req.body
app.use((req, _res, next) => {            // custom logger
  req.requestId = crypto.randomUUID();
  console.log(`${req.method} ${req.url} [${req.requestId}]`);
  next();                                 // MUST call next() or end response
});

// 2) Route-level middleware: only for routes that opt in
function authGuard(req, res, next) {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'Missing token' }); // ends here
  req.user = verifyJwt(token);            // attach context for downstream
  next();
}

app.get('/profile', authGuard, (req, res) => res.json(req.user));
```

> 💡 **Remember:** Middleware = an **assembly line**: each station can **inspect, stamp (mutate req), reject, or pass it on (`next()`)**. "**Either you `res.send` or you `next` — never neither, never both.**"

**❓ Interview: What does `next()` do? What about `next(err)`?**
`next()` passes control to the **next middleware** in the stack. `next(err)` (with any argument other than the string `'route'`) **skips all remaining normal middleware** and jumps straight to the **error-handling middleware**. `next('route')` is a special case that skips the rest of the *current route's* middleware and moves to the next matching route.

**❓ Interview: Order of middleware — does it matter?**
Yes, hugely. Express runs middleware **top-to-bottom in registration order**. `express.json()` must come *before* handlers that read `req.body`; `authGuard` must come *before* protected handlers; the error handler must be registered **last**.

---

### 3. Error-handling middleware (the 4-argument signature)

**Plain definition:** An Express middleware with **exactly four parameters `(err, req, res, next)`** — Express recognizes the arity and routes errors here; it's your single centralized place to shape error responses.

```
normal mw:  (req, res, next)          ← 3 args
error  mw:  (err, req, res, next)      ← 4 args  ← Express keys off the count
                ▲
   reached via: throw (in async-wrapped fn) OR next(err)
```

```js
// Centralized error handler — register AFTER all routes, LAST.
// All four params are REQUIRED — keep `next` even if unused, or Express
// treats this as a normal 3-arg middleware and never routes errors here.
app.use((err, req, res, next) => {
  // log full detail server-side (with requestId for tracing)...
  console.error(`[${req.requestId}]`, err);

  // If headers were already sent, delegate to Express's default handler.
  if (res.headersSent) return next(err);

  const status = err.status || 500;
  res.status(status).json({
    error: status >= 500 ? 'Internal Server Error' : err.message, // hide 5xx internals
    requestId: req.requestId,             // safe correlation id for support
  });
  // Note: do NOT call next() on success — this is the end of the line.
});
```

**Catching async errors** — in Express 4, a rejected promise does **not** auto-route to the error handler; you must catch it. A tiny wrapper avoids `try/catch` in every handler:

```js
const asyncH = fn => (req, res, next) =>
  Promise.resolve(fn(req, res, next)).catch(next); // rejection → next(err)

app.get('/accounts/:id', asyncH(async (req, res) => {
  const acct = await Account.findById(req.params.id);
  if (!acct) { const e = new Error('Not found'); e.status = 404; throw e; }
  res.json(acct);
}));
```

(Express **5** auto-forwards rejected promises from async handlers, so the wrapper becomes optional there.)

> 💡 **Remember:** **"4 args = error handler"** — Express literally counts the parameters (keep `next` even if unused). And **"register it last"** (after every route), or errors fall through to Express's built-in default handler.

**❓ Interview: How do you handle errors thrown inside an async route?**
In Express 4, async rejections aren't auto-caught — wrap handlers (`Promise.resolve(fn(...)).catch(next)`) or `try/catch` + `next(err)`. The error then lands in the 4-arg error middleware. **Never leak stack traces / internal messages to clients on a 5xx** (banking/security) — log them server-side, return a generic message + correlation id.

---

### 4. MongoDB document modeling — embed vs reference

**Plain definition:** Embedding nests related data **inside one document** (one read, denormalized); referencing stores an **ObjectId pointer** to another document (normalized, joined at query time).

```
EMBED  (one-to-few, read together, owned)     REFERENCE (one-to-many/large, shared, grows)
─────────────────────────────────────        ──────────────────────────────────────────
 customer {                                    customer { _id: C1, name: "Asha" }
   _id, name: "Asha",                          account  { _id: A1, owner: C1,    ← ObjectId
   addresses: [                                           type: "checking" }
     { line1, city, zip },   ← embedded        transaction { _id, account: A1,   ← ref
     { line1, city, zip }                                     amount: -50 }
   ]                                            // join via populate / $lookup
 }
```

**Decision rules:**

| Embed when… | Reference when… |
|---|---|
| Data is read together, almost always | Data is queried/updated independently |
| "Owned" / contained (1-to-few) | Shared across many parents (many-to-many) |
| Bounded size | **Unbounded** growth (avoids the 16 MB doc limit) |
| Atomic single-doc updates wanted | Each side has a large/heavy lifecycle |

*Banking example:* a customer's **addresses** → embed (few, owned). A customer's **transactions** → reference (unbounded, queried on their own, must never blow the 16 MB document cap).

> 💡 **Remember:** **"Embed what you OWN and read together; reference what GROWS or is SHARED."** Watch the **16 MB document limit** — unbounded arrays must be a separate collection.

**❓ Interview: Embed vs reference — how do you decide?**
By **access pattern + cardinality + growth**. Read-together + bounded + owned → embed (faster, one round-trip, atomic). Independent access, many-to-many, or unbounded growth → reference (and join with `$lookup`/`populate`). MongoDB is schema-flexible, so model for *how you read*, not for normalization purity.

---

### 5. Indexes — why and types

**Plain definition:** An index is a sorted B-tree on one or more fields that lets MongoDB find documents **without scanning the whole collection** — turning an O(n) collection scan into an O(log n) lookup.

```
NO INDEX (COLLSCAN)           WITH INDEX (IXSCAN)
────────────────────          ────────────────────
scan every doc ❌              B-tree → jump to match ✅
1M docs = 1M reads             1M docs ≈ ~20 hops
```

**Common index types:**

```
Single-field   { email: 1 }                     one field (1=asc, -1=desc)
Compound       { account: 1, date: -1 }         multi-field — order matters!
Unique         { accountNumber: 1 } unique:true  enforces no duplicates
Text           { description: "text" }           full-text search
TTL            { createdAt: 1 } expireAfter:3600 auto-delete old docs (sessions/OTPs)
Multikey       (auto, on array fields)           one entry per array element
```

**Compound index prefix rule:** an index on `{ account: 1, date: -1 }` serves queries on `account`, and on `account + date` — but **not** on `date` alone (left-to-right prefix only).

```js
// Mongoose schema indexes
txnSchema.index({ account: 1, date: -1 });      // list a user's txns, newest first
txnSchema.index({ idempotencyKey: 1 }, { unique: true }); // block double-spend
sessionSchema.index({ createdAt: 1 }, { expireAfterSeconds: 1800 }); // TTL

// Verify a query uses an index:
db.transactions.find({ account: id }).explain('executionStats'); // want IXSCAN, not COLLSCAN
```

> 💡 **Remember:** **"Index = book's index — jump straight to the page instead of reading every page."** Compound = **left-to-right prefix** (order stages by the **ESR** rule: **E**quality → **S**ort → **R**ange). Indexes speed reads but **slow writes** (every insert updates each index) — don't over-index.

**❓ Interview: Trade-off of adding indexes?**
Reads get faster; **writes get slower** (each insert/update must maintain every index) and indexes consume **RAM/disk**. Index the fields you **filter and sort on**, confirm with `.explain()` (look for `IXSCAN`), and avoid redundant/unused indexes.

---

### 6. Aggregation pipeline basics — `$match` / `$group` / `$sort` / `$lookup`

**Plain definition:** The aggregation pipeline transforms documents through an ordered series of **stages**, where each stage's output feeds the next — like Unix pipes for data (filter → group → sort → join).

```
docs ─► $match ─► $group ─► $sort ─► $lookup ─► result
        (filter)  (rollup)  (order)  (join)
        WHERE     GROUP BY  ORDER BY JOIN   ← SQL analogy
```

**Core stages:**
- `$match` — filter docs (use **early** to cut the working set; can use indexes if it's the first stage).
- `$group` — bucket by `_id` and aggregate (`$sum`, `$avg`, `$max`, etc.; use `{ $sum: 1 }` to count per group).
- `$sort` — order results.
- `$lookup` — left-outer-join another collection.
- (others: `$project` reshape, `$limit`, `$unwind` flatten arrays.)

```js
// "Total spend per category this month, for one account, biggest first"
const summary = await Transaction.aggregate([
  { $match: { account: acctId, date: { $gte: monthStart } } },     // filter first!
  { $group: { _id: '$category', total: { $sum: '$amount' },
              count: { $sum: 1 } } },                              // rollup
  { $sort:  { total: -1 } },                                       // order
  { $lookup: { from: 'categories', localField: '_id',
               foreignField: 'code', as: 'cat' } },                // join
]);
// → [{ _id:'groceries', total: 842.5, count: 17, cat:[{...}] }, ...]
```

> 💡 **Remember:** Aggregation = **SQL in stages**: **`$match` = WHERE, `$group` = GROUP BY, `$sort` = ORDER BY, `$lookup` = JOIN.** Golden rule: **`$match` first** (filter before you group — shrink the data early so the index can apply).

**❓ Interview: Why put `$match` before `$group`?**
Performance. `$match` early reduces the number of documents flowing through the pipeline and, as the **first** stage, can use an **index**. Once you `$group`, the data is transformed (new computed `_id`) and the original collection's indexes no longer apply, so filtering afterward scans far more in-memory data.

---

### 7. Mongoose — models/schemas, validation, populate (joins)

**Plain definition:** Mongoose is an ODM that wraps MongoDB with **schemas** (structure + types), **models** (the queryable class bound to a collection), built-in **validation**, and **`populate()`** to resolve referenced ObjectIds into full documents (an application-level join).

```
Schema  ──defines──►  Model  ──instances──►  Documents
(shape, rules)        (Account)              (rows you save/query)

ref + populate:
 Account.owner: ObjectId ──ref:'Customer'──► .populate('owner') ─► full Customer doc
```

**Model + schema + validation:**

```js
import { Schema, model } from 'mongoose';

const accountSchema = new Schema({
  owner:  { type: Schema.Types.ObjectId, ref: 'Customer', required: true }, // reference
  type:   { type: String, enum: ['checking', 'savings'], required: true },  // validation
  balance:{ type: Number, default: 0, min: [0, 'Balance cannot be negative'] },
  iban:   { type: String, required: true, unique: true,
            match: [/^[A-Z]{2}\d{2}[A-Z0-9]+$/, 'Invalid IBAN'] },          // regex validate
}, { timestamps: true });

// Custom validator (banking rule)
accountSchema.path('balance').validate(v => Number.isFinite(v), 'Balance must be a number');

export const Account = model('Account', accountSchema);
```

**Populate (the join):**

```js
// Resolve the `owner` ObjectId into the real Customer doc, picking only some fields
const account = await Account.findById(id)
  .populate('owner', 'name email')        // join Customer, project 2 fields
  .lean();                                // plain JS object → faster, read-only

// account.owner → { _id, name: 'Asha', email: 'asha@…' }  (not just an ObjectId)
```

**Validation note:** `required`, `min/max`, `enum`, `match`, and custom validators run on `.save()` and (with `{ runValidators: true }`) on `findOneAndUpdate`/`updateOne`. Validation errors surface as a `ValidationError` (HTTP 400/422). Note `unique` is **not** a validator — it's a *unique index*, so a duplicate throws a `E11000` duplicate-key error at write time, not a `ValidationError`.

> 💡 **Remember:** **Schema = blueprint, Model = factory, Document = product.** **`populate` = Mongoose's JOIN** (follows a `ref` ObjectId → full doc, via a *second* query). Add **`.lean()`** for read-only queries (skips hydration → faster). For updates, remember **`runValidators: true`** or your validators silently don't run.

**❓ Interview: What is `populate` and how does it differ from `$lookup`?**
`populate` is Mongoose's **application-level join**: it issues a *second* query to fetch referenced docs and stitches them in (in the app). `$lookup` is a **database-level join** done inside the aggregation pipeline (server-side, no extra app round-trip). Use `populate` for simple ref resolution; use `$lookup` when you need joining *plus* grouping/filtering in one efficient server-side pipeline.

**❓ Interview: Schema-less Mongo but Mongoose has schemas — why?**
MongoDB itself is flexible/schema-less, but real apps need **structure, types, validation, defaults, and middleware**. Mongoose gives you those guarantees at the application layer (so a banking app can enforce `balance ≥ 0`, required IBAN, enums) while keeping Mongo's flexibility underneath.

---

### ⭐ 60-second recall sheet

```
REST    nouns + verbs + status; 201 create, 204 delete; 4xx=client 5xx=server
        idempotent: GET PUT DELETE (not POST) — about state, not response code
EXPRESS middleware (req,res,next) in order; next() or respond, never both
        error handler = 4 args (err,req,res,next), registered LAST
        async errors: wrap with Promise…catch(next)  (Express 4; auto in 5)
MONGO   embed (own + read together + bounded) vs reference (grows/shared)
        index = jump not scan; compound = left-prefix (ESR); 16MB doc cap
        aggregate: $match→$group→$sort→$lookup  (= WHERE/GROUP BY/ORDER BY/JOIN); $match first
MONGOOSE schema=blueprint, model=factory, doc=product
        validation on save (+runValidators on update); unique=index not validator
        populate = app-level JOIN (2nd query); .lean() to speed reads
```

---

## 🔒 Security (Frontend)

Banking front-ends are high-value targets. The golden rule: **never trust input, never trust the client, defend in depth.** Every layer below is one slice of "defense in depth" — no single control is enough on its own.

---

### XSS — Cross-Site Scripting

**Definition:** An attacker injects malicious JavaScript that runs in *another user's* browser, in *your site's* origin — so it can read cookies, tokens, the DOM, and act as that user.

**Three flavors:**

```
STORED      attacker payload saved in DB → served to every viewer
            e.g. <script> in a "transaction note" field
            ┌────────┐  save  ┌─────┐  serve  ┌────────┐
            │attacker│──────▶ │ DB  │ ──────▶ │ victim │ 💥 runs
            └────────┘        └─────┘         └────────┘

REFLECTED   payload bounced back off a URL/param in the response
            e.g. ?q=<script>... → echoed into the page
            victim clicks crafted link → script runs once

DOM-BASED   no server round-trip; client JS writes attacker data
            into the DOM (innerHTML, location.hash) → executes
            url#<img src=x onerror=steal()>
```

**Prevention — escape on output, sanitize HTML, lock down with CSP:**

```jsx
// ✅ SAFE: React escapes by default. {userInput} is rendered as TEXT.
function Note({ text }) {
  return <p>{text}</p>;        // "<script>" shows literally, never executes
}

// ❌ DANGEROUS: bypasses React's escaping entirely
<div dangerouslySetInnerHTML={{ __html: userInput }} />

// ✅ If you MUST render HTML (e.g. rich-text statement), sanitize first:
import DOMPurify from "dompurify";

function RichNote({ html }) {
  const clean = DOMPurify.sanitize(html, {
    ALLOWED_TAGS: ["b", "i", "em", "strong", "a", "p"],
    ALLOWED_ATTR: ["href"],
  });
  return <div dangerouslySetInnerHTML={{ __html: clean }} />;
}
```

Other XSS sinks to treat as dangerous: `eval`, `new Function`, `el.innerHTML`, `document.write`, and `<img src=x onerror=...>`.

⚠️ **A trap React does *not* save you from:** `<a href={userUrl}>`. React escapes *text* and most attribute *values*, but it does **not** block a `javascript:` URL in `href`/`src` — `<a href="javascript:steal()">` will execute on click. (React 16.9+ only logs a dev-time warning; it doesn't strip it.) **Validate URL schemes yourself:**

```jsx
const safe = /^(https?:|mailto:|tel:|\/)/i.test(userUrl) ? userUrl : "#";
return <a href={safe}>link</a>;
```

```
Defense layers:  ESCAPE output  →  SANITIZE any HTML  →  CSP as net
                 (React default)   (DOMPurify)           (blocks the rest)
```

> 💡 **Remember:** *"React escapes for you — until you say `dangerouslySetInnerHTML`… or put a URL in `href`."* The word **dangerously** is literally a warning in the prop name. If you see it, you must sanitize. And always allowlist URL schemes.

> ❓ **What is XSS and how do you prevent it in React?**
> XSS = injecting JS that runs in another user's session in your origin (stored / reflected / DOM). React auto-escapes interpolated text, so the main risks are `dangerouslySetInnerHTML` and `javascript:` URLs in `href`/`src`. Prevent it by: rely on React's escaping, sanitize any raw HTML with DOMPurify (allowlist tags/attrs), validate URL schemes (block `javascript:`), and add a Content-Security-Policy as a backstop.

> ❓ **Stored vs reflected vs DOM-based?**
> Stored = persisted server-side, hits every viewer (worst). Reflected = bounced back off a request param, needs the victim to click a crafted link. DOM-based = pure client-side, attacker data flows into a DOM sink (`innerHTML`, `location.hash`) with no server involved.

---

### CSRF — Cross-Site Request Forgery

**Definition:** A malicious site tricks a logged-in user's browser into sending an *authenticated* request to your app — the browser auto-attaches the user's cookies, so the request looks legitimate.

```
You're logged into bank.com (cookie stored). You visit evil.com:

  evil.com  ──── auto-submits hidden form ───▶  bank.com/transfer
              <form action="bank.com/transfer">      │
                <input name="to"  value="attacker">  │ browser ATTACHES
                <input name="amt" value="10000">     │ your bank cookie
              </form>  (fires on load)                ▼
                                              💸 transfer succeeds
```

The key insight: CSRF abuses **ambient credentials** (cookies sent automatically). It does *not* need to read the response — fire-and-forget is enough to move money.

**Prevention:**

```js
// 1) SameSite cookie — browser won't send it on cross-site requests
//    (set by the SERVER on the Set-Cookie header)
Set-Cookie: session=abc; HttpOnly; Secure; SameSite=Strict; Path=/

// 2) Anti-CSRF token (synchronizer / double-submit): a secret the
//    attacker's site cannot read (Same-Origin Policy blocks it)
const csrf = getCsrfTokenFromMeta();      // server-rendered, per-session
await fetch("/api/transfer", {
  method: "POST",
  headers: { "X-CSRF-Token": csrf, "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({ to, amount }),
});
// Server rejects the request if the header token != the session token.
```

- `SameSite=Strict` — cookie never sent cross-site (safest; can break inbound links from email, since even a top-level click from another site arrives unauthenticated). `Lax` — sent on top-level GET navigations only, **not** on cross-site POST/`fetch`/iframe/image loads (good default, and the browser default for unspecified cookies). `None` — always sent, **requires `Secure`** (needed for legit cross-origin APIs).
- The anti-CSRF token works because of the **Same-Origin Policy**: evil.com's JS can't read bank.com's token (or its DOM/cookies) to copy it into the forged request.
- ⚠️ **Caveat:** the *double-submit* variant (token in both a cookie and a header) can be bypassed if an attacker can set/inject cookies (e.g. a vulnerable subdomain), since they could plant a matching pair. The *synchronizer* pattern (token tied to server-side session state) is stronger. Either way, pair tokens with `SameSite`.

> 💡 **Remember:** *"XSS steals the token; CSRF rides the cookie."* CSRF needs no script on your page — it just needs your browser to auto-send a cookie. Fix = **SameSite + token**.

> ❓ **XSS vs CSRF — difference?**
> XSS = run attacker's code in your origin (a *code* injection / confidentiality problem). CSRF = make the browser send a forged request using credentials it already holds (a *request* forgery / integrity problem). XSS can defeat most CSRF defenses (it can read the token), so **fixing XSS is a prerequisite**.

> ❓ **Why does SameSite help?**
> It tells the browser not to attach the cookie on cross-site requests, so the forged request from evil.com arrives *unauthenticated* and the server rejects it.

---

### JWT — Access & Refresh Tokens

**Definition:** A JWT is a signed, self-contained token (`header.payload.signature`, base64url) the server issues after login; the client sends it to prove identity. **Signed, not encrypted** — anyone can read the payload, so never put secrets in it.

```
  header . payload . signature
  {alg}    {sub,exp}  HMAC/RSA(header.payload, serverSecret)
   └──────── base64url, dot-separated ────────┘
  Server VERIFIES the signature → trusts claims without a DB lookup.
```

**Two-token pattern:**

```
ACCESS  token   short-lived (5–15 min)  → sent on every API call
REFRESH token   long-lived (days/weeks) → used ONLY to mint new access

  access expires (15m) ──▶ POST /auth/refresh (with refresh token)
                          ──▶ new access token  (no re-login)
  Short access = small blast radius if leaked.
```

**Where to store — the core security tradeoff:**

```
┌─────────────────────┬───────────────┬──────────────┬─────────────────┐
│ Storage             │ XSS exposed?  │ CSRF exposed?│ Sent auto?      │
├─────────────────────┼───────────────┼──────────────┼─────────────────┤
│ localStorage        │ YES (JS reads)│ no           │ no (manual hdr) │
│ JS memory (variable)│ harder*       │ no           │ no (manual hdr) │
│ httpOnly cookie     │ NO (JS can't) │ YES          │ yes (auto)      │
└─────────────────────┴───────────────┴──────────────┴─────────────────┘
  *memory: not readable via a persisted store, but active XSS on the page can
   still grab it in-flight; it's lost on refresh/tab close (small blast radius)
```

**Recommended banking pattern:**

```js
// Access token → in MEMORY (a module variable), sent via Authorization header.
//   Not in localStorage (XSS can read it). Lost on reload → re-fetch via refresh.
let accessToken = null;
export const setAccessToken = (t) => { accessToken = t; };

async function api(path, opts = {}) {
  const res = await fetch(path, {
    ...opts,
    headers: { ...opts.headers, Authorization: `Bearer ${accessToken}` },
    credentials: "include",          // sends the refresh cookie
  });
  if (res.status === 401) {          // access expired → silently refresh
    const r = await fetch("/auth/refresh", { method: "POST", credentials: "include" });
    if (r.ok) {
      setAccessToken((await r.json()).accessToken);
      return api(path, opts);         // retry once
    }
  }
  return res;
}

// Refresh token → httpOnly + Secure + SameSite cookie (JS can NEVER read it).
//   Server: Set-Cookie: refresh=...; HttpOnly; Secure; SameSite=Strict; Path=/auth/refresh
```

Why this split: access token in memory means **XSS can't exfiltrate it from a persistent store** (no long-lived credential sitting in `localStorage`); refresh token in an httpOnly cookie means **XSS can't read it at all** (and `SameSite` + a CSRF token on `/refresh` covers CSRF). Short access-token TTL limits damage if one is grabbed in-flight. (Note: active XSS is still catastrophic — it can call your API as the user while the page is open. These choices shrink the blast radius; they don't make XSS harmless. Refresh-token *rotation* — issuing a new refresh token on each use and revoking the old — further limits a stolen refresh token.)

> 💡 **Remember:** *"Access in memory, refresh in httpOnly."* Pick your poison: **localStorage → XSS-readable; cookie → CSRF-prone.** Best of both = short access token in memory + httpOnly refresh cookie + SameSite + CSRF token.

> ❓ **Where do you store a JWT and why?**
> Not in localStorage (any XSS reads it and it persists). Best: keep the short-lived access token in JS memory and send it in the `Authorization` header; keep the long-lived refresh token in an `httpOnly; Secure; SameSite` cookie so JS can't read it. Add a CSRF token because the cookie is auto-sent. Short access TTL minimizes the blast radius.

> ❓ **Why access + refresh instead of one token?**
> A single long-lived token is a big blast radius if stolen and can't be revoked easily (stateless JWTs aren't revocable without extra infra). Short access tokens limit exposure; the refresh token (stored more safely, revocable server-side, and ideally rotated) lets you mint new access tokens without forcing re-login.

> ❓ **Is a JWT encrypted?** No — it's signed (base64url, readable by anyone). The signature guarantees *integrity* (not tampered), not *confidentiality*. Never put passwords/PII/secrets in the payload. (If you need confidentiality, that's JWE — encrypted — which is a separate construct.)

---

### CSP — Content-Security-Policy

**Definition:** An HTTP response header that tells the browser which sources of scripts, styles, images, etc. are allowed — so even if an attacker injects a `<script>`, the browser refuses to run it.

```
Browser receives page  +  CSP header
  injected <script src=evil.com> ──▶ ❌ blocked (not in allowlist)
  inline   <script>steal()</script> ──▶ ❌ blocked (no 'unsafe-inline')
  CSP is the SAFETY NET that catches XSS your escaping/sanitizing missed.
```

**Example header (strict, banking-grade):**

```http
Content-Security-Policy:
  default-src 'self';
  script-src 'self' 'nonce-r4nd0m';
  style-src  'self';
  img-src    'self' data:;
  connect-src 'self' https://api.bank.com;
  frame-ancestors 'none';
  object-src 'none';
  base-uri 'self';
  upgrade-insecure-requests;
```

⚠️ The `nonce-r4nd0m` above is a placeholder: a CSP nonce must be **cryptographically random and regenerated on every response**, then echoed onto each trusted `<script nonce="…">`. A hard-coded/static nonce is worthless — an attacker can just reuse it.

```js
// Express (helmet) — set CSP from the server with a PER-REQUEST nonce:
const crypto = require("crypto");
const helmet = require("helmet");

app.use((req, res, next) => {
  res.locals.nonce = crypto.randomBytes(16).toString("base64");
  next();
});

app.use(helmet.contentSecurityPolicy({
  directives: {
    defaultSrc: ["'self'"],
    // fresh nonce each request; no 'unsafe-inline' → blocks injected inline JS
    scriptSrc: ["'self'", (req, res) => `'nonce-${res.locals.nonce}'`],
    objectSrc: ["'none'"],
    frameAncestors: ["'none'"],     // anti-clickjacking (replaces X-Frame-Options)
    baseUri: ["'self'"],
    upgradeInsecureRequests: [],
  },
}));
```

- `default-src 'self'` — only same-origin by default; directives you don't specify fall back to this. (Note: a few directives like `frame-ancestors`, `base-uri`, and `object-src` do **not** inherit from `default-src` — set them explicitly, as above.)
- Avoid `'unsafe-inline'`/`'unsafe-eval'` — they re-open the XSS hole CSP is meant to close. Use a per-request **nonce** or a hash for any inline script you genuinely need.
- `frame-ancestors 'none'` stops **clickjacking** (your bank page embedded in an attacker iframe).

> 💡 **Remember:** *"CSP = an allowlist of where code may come from; it's the net under your XSS tightrope."* Self by default, no inline, no eval — and any nonce must be fresh per request.

> ❓ **How does CSP mitigate XSS?**
> It restricts which script sources can execute. Even if an attacker injects markup, the browser won't load an off-origin script or run inline JS (without a matching nonce/hash), so the payload never executes. It's a backstop, not a substitute for escaping/sanitizing.

---

### HTTPS & No Secrets in the Bundle

**Definition:** Serve everything over TLS (HTTPS) so data is encrypted in transit, and remember that **anything shipped to the browser is public** — there are no secrets on the client.

```
HTTP   client ───plaintext───▶ server   👀 anyone on the wire reads it
HTTPS  client ══encrypted════▶ server   🔒 TLS: confidential + integrity
                                          + authenticates the server

The bundle is PUBLIC:  build → main.js → DevTools → anyone reads it
  ❌ const API_KEY = "sk_live_abc123"   // shipped to every user!
```

```js
// ❌ NEVER — secret is baked into the bundle, visible in DevTools/Sources
const STRIPE_SECRET = "sk_live_...";

// ✅ Only NON-secret, publishable config belongs client-side.
//    REACT_APP_* / VITE_* vars are EMBEDDED at build time = public.
const apiBase = import.meta.env.VITE_API_BASE_URL;   // fine: a URL, not a secret

// ✅ Real secrets live on the server; the client calls a backend route
//    that holds the key and proxies the request.
await fetch("/api/charge", { method: "POST", credentials: "include", body });
```

Also: force HTTPS with **HSTS** (`Strict-Transport-Security`) so browsers refuse to downgrade to HTTP; never log card numbers/tokens to the console; and set cookies with `Secure` so they're never sent over plain HTTP.

> 💡 **Remember:** *"If it ships to the browser, it's public."* TLS protects data *in transit*; it does **not** hide your code or any key inside it. Secrets stay server-side.

> ❓ **Can you keep an API key safe in a React app?**
> No. Everything in the bundle is downloadable and inspectable, and env vars like `VITE_*`/`REACT_APP_*` are inlined at build time. Keep secret keys on the server and expose a backend proxy endpoint; the client only ever holds publishable, non-secret values.

> ❓ **Why HTTPS for a banking app?**
> It encrypts traffic (no eavesdropping/credential theft on the wire), guarantees integrity (no tampering/injection in transit), and authenticates the server (anti-MITM). Add HSTS to prevent protocol downgrade. It's also a baseline compliance requirement (PCI-DSS).

---

### 🎯 One-Screen Recap

```
XSS    attacker JS runs in YOUR origin   → escape output, DOMPurify, validate URLs, CSP, no dSIH
CSRF   forged request rides YOUR cookie  → SameSite cookies + anti-CSRF token
JWT    signed identity token             → short access (memory) + httpOnly refresh
CSP    allowlist of code sources         → 'self', no inline/eval, per-request nonce
HTTPS  TLS in transit                    → always; + HSTS; no secrets in bundle
```

> 💡 **Master mnemonic — "X-C-J-C-H":** **X**SS (sanitize), **C**SRF (SameSite+token), **J**WT (memory+httpOnly), **C**SP (allowlist), **H**TTPS (TLS+no secrets). And the through-line: **"Never trust the client; defend in depth."**

---

## 🧩 Design Patterns + OOAD

A crisp, banking-flavoured refresher: SOLID principles, the classic GoF patterns you actually see in MERN, and the React composition trio (HOC → Render Props → Custom Hooks).

---

### 🧱 SOLID Principles

> **💡 Remember SOLID:** **"Some Old People Lift Iron Dumbbells"** → **S**RP, **O**CP, **L**SP, **I**SP, **D**IP.

```
S  Single Responsibility   — one reason to change
O  Open/Closed             — open to extend, closed to modify
L  Liskov Substitution     — subtypes swap in cleanly
I  Interface Segregation   — small focused contracts
D  Dependency Inversion    — depend on abstractions
```

---

#### S — Single Responsibility Principle (SRP)

**Definition:** A module/class/component should have exactly one reason to change (one job).

```
BAD: <TransactionRow>           GOOD: split the jobs
 ├─ formats currency            <TransactionRow> ── uses ──> formatCurrency()
 ├─ fetches data                                  └─ uses ──> useTransactions()
 └─ renders UI                  (component only renders)
```

```tsx
// BAD: component formats money AND fetches AND renders
function TransactionRow({ id }: { id: string }) {
  const [tx, setTx] = useState<{ cents: number } | null>(null);
  useEffect(() => { fetch(`/api/tx/${id}`).then(r => r.json()).then(setTx); }, [id]);
  const amount = tx ? `$${(tx.cents / 100).toFixed(2)}` : "…"; // formatting mixed in
  return <div>{amount}</div>;
}

// GOOD: each piece has one responsibility
const formatUSD = (cents: number) => `$${(cents / 100).toFixed(2)}`; // 1 job: format
const useTransaction = (id: string): { cents: number } | null => { /* 1 job: fetch */ };

function TransactionRow({ id }: { id: string }) {           // 1 job: render
  const tx = useTransaction(id);
  if (!tx) return <div>…</div>;
  return <div>{formatUSD(tx.cents)}</div>;
}
```

> **Note:** In the BAD version, `tx?.cents / 100` would be `NaN` on the first render (when `tx` is `null`, `tx?.cents` is `undefined`), so always guard the null state — which is exactly why splitting jobs and handling loading explicitly is cleaner.

**💡 Remember:** *"One class, one job, one reason to change."* If you need the word "and" to describe it, split it.

> **❓ Interview:** *How does SRP show up in React?* → Keep components presentational, push data-fetching into hooks, push formatting/business logic into pure utility functions. Each then changes for its own independent reason.

---

#### O — Open/Closed Principle (OCP)

**Definition:** Open for extension, closed for modification — add behaviour without editing existing, already-tested code.

```
BAD: edit switch every new type   GOOD: register/extend a map
  switch(type){                     strategies = { wire, ach, card }
    case 'wire': ...                 add 'crypto' → no edit to existing
    case 'ach' : ...   ← must edit              code paths
  }
```

```tsx
// BAD: every new payment type forces editing this function
function fee(type: string, amt: number): number {
  switch (type) {
    case "wire": return amt * 0.01;
    case "ach":  return 0;
    default:     throw new Error(`unknown type: ${type}`);
    // adding "card" means modifying this tested function
  }
}

// GOOD: strategy map — extend by adding a key, never touch existing ones
type FeeFn = (amt: number) => number;
const feeStrategies: Record<string, FeeFn> = {
  wire: (a) => a * 0.01,
  ach:  () => 0,
};
feeStrategies.card = (a) => a * 0.025; // EXTEND, don't modify

const fee = (type: string, amt: number): number => {
  const strategy = feeStrategies[type];
  if (!strategy) throw new Error(`unknown type: ${type}`); // guard missing key
  return strategy(amt);
};
```

> **Note:** A bare `feeStrategies[type](amt)` throws a cryptic *"... is not a function"* for an unknown key. Guard the lookup so the failure mode stays clear — handling the unknown case is itself part of staying "closed to modification."

**💡 Remember:** *"Add new code, don't edit old code."* Reach for a map/strategy/polymorphism instead of growing a `switch`.

> **❓ Interview:** *Give a React example of OCP.* → A component that takes a `render`/`renderItem` prop, or a registry of variant components keyed by type, extends to new cases without modifying the component itself.

---

#### L — Liskov Substitution Principle (LSP)

**Definition:** Any subtype must be usable wherever its base type is expected — without surprises or broken promises.

```
Base: Account.withdraw(x)
        ▲              ▲
   Checking        FixedDeposit
   (works)         (throws on withdraw??)  ← LSP VIOLATION
```

```tsx
// BAD: subtype breaks the contract callers rely on
class Account { withdraw(amt: number) { /* deduct */ } }
class FixedDeposit extends Account {
  withdraw(_amt: number) { throw new Error("not allowed"); } // breaks substitutability
}

// GOOD: don't force an unsupported capability into the base type
interface Readable     { balance(): number; }
interface Withdrawable { withdraw(amt: number): void; }
class Checking     implements Readable, Withdrawable { /* both */ }
class FixedDeposit implements Readable { /* only read — honestly typed */ }
```

**💡 Remember:** *"If it looks like a duck but needs batteries, it's not a duck."* A subclass must not weaken or break the parent's promises (no throwing where the base succeeds, no narrowing inputs / widening outputs).

> **❓ Interview:** *React angle?* → A component swapped in for another via the same props interface must honor that contract (same props, same expected behaviour) — e.g. a custom `<Button>` replacing a native one must still accept `onClick`, `disabled`, and `type`, and not silently ignore them.

---

#### I — Interface Segregation Principle (ISP)

**Definition:** Prefer many small, specific interfaces over one fat one; clients shouldn't depend on props/methods they don't use.

```
FAT props:  <Widget onEdit onDelete onPrint onExport ... />   ← consumers forced to know all
THIN props: <Widget {...rowActions} />  where rowActions = { onEdit, onDelete } only
```

```tsx
// BAD: fat prop interface — a read-only row must still satisfy all of these
interface RowProps {
  data: Tx; onEdit(): void; onDelete(): void; onExport(): void;
}

// GOOD: segregate into focused, optional capability props
interface RowProps  { data: Tx; }
interface Editable  { onEdit(): void; }
interface Deletable { onDelete(): void; }
function TxRow(p: RowProps & Partial<Editable & Deletable>) { /* use what's given */ }
```

**💡 Remember:** *"Don't make clients pay for methods they don't call."* Many slim contracts beat one bloated one.

> **❓ Interview:** *ISP in TypeScript?* → Split big interfaces; compose with intersections (`A & B`) and `Partial<>` so each consumer only depends on the slice it actually needs.

---

#### D — Dependency Inversion Principle (DIP)

**Definition:** High-level modules depend on abstractions, not concrete details; both depend on interfaces. (Inject dependencies, don't hard-wire them.)

```
BAD:  Component ──directly──> fetch()/axios   (glued to a concrete tool)
GOOD: Component ──> interface ApiClient <── axiosImpl / mockImpl (injected)
```

```tsx
// BAD: component is hard-wired to a concrete data source
function Balance() {
  const [b, setB] = useState(0);
  useEffect(() => { axios.get("/api/balance").then(r => setB(r.data)); }, []);
  return <span>{b}</span>;
}

// GOOD: depend on an abstraction, inject it (easy to mock in tests)
interface AccountApi { getBalance(): Promise<number>; }

function Balance({ api }: { api: AccountApi }) {   // depends on the interface
  const [b, setB] = useState(0);
  useEffect(() => { api.getBalance().then(setB); }, [api]);
  return <span>{b}</span>;
}
// prod: <Balance api={httpAccountApi}/>   test: <Balance api={fakeApi}/>
```

In React the **inversion mechanism** is usually **props, Context, or a custom hook** — they let you swap the concrete implementation (great for testing banking flows with mocks).

**💡 Remember:** *"Depend on the socket, not the plug."* Code to interfaces; inject the concrete thing.

> **❓ Interview:** *DIP vs Dependency Injection?* → DIP is the *principle* (high-level code depends on abstractions). DI is one *technique* to achieve it (pass the dependency in instead of constructing it inside). You can satisfy DIP without a DI framework — plain props/Context count.

---

### 🏭 Design Patterns

---

#### Singleton

**Definition:** Guarantees a single shared instance with one global access point.

```
   ┌────────────┐
   │  Singleton │  ← exactly ONE instance
   └────────────┘
   ▲    ▲     ▲
 modA modB  modC   (all share it)
```

```ts
// ES module = singleton by default (module is evaluated once, then cached)
class AuthStore {
  private token: string | null = null;
  setToken(t: string) { this.token = t; }
  getToken() { return this.token; }
}
export const authStore = new AuthStore(); // one instance, shared everywhere

// import { authStore } from "./authStore"  → same object across the app
```

**💡 Remember:** *"One instance to rule them all."* In JS, an exported module object already IS a singleton (the module cache guarantees single evaluation).

> **❓ Interview:** *Downside of Singletons?* → Hidden global state → harder to test, hidden coupling, and trouble in SSR (state can leak across requests since the module is shared per server process). In React, prefer Context over a raw module singleton when the value must reset per user/request.

---

#### Factory

**Definition:** A function/method that decides which object (or component) to create, hiding the construction logic from the caller.

```
 createNotifier(type) ──► ┌─ "email"  → EmailNotifier
                          ├─ "sms"    → SmsNotifier
                          └─ "push"   → PushNotifier
   caller just asks; factory picks the concrete class
```

```tsx
interface Notifier { send(msg: string): void; }
class EmailNotifier implements Notifier { send(m: string) {/*...*/} }
class SmsNotifier   implements Notifier { send(m: string) {/*...*/} }

// Factory: caller passes a type, gets back the right concrete object
function createNotifier(type: "email" | "sms"): Notifier {
  return type === "email" ? new EmailNotifier() : new SmsNotifier();
}

// React component factory: pick a component by data
const fieldRegistry = { text: TextInput, date: DatePicker, money: MoneyInput };
type FieldKind = keyof typeof fieldRegistry;
const Field = ({ kind, ...p }: { kind: FieldKind } & Record<string, unknown>) => {
  const Cmp = fieldRegistry[kind] ?? TextInput;
  return <Cmp {...p} />;
};
```

**💡 Remember:** *"Ask the factory, don't `new` it yourself."* Centralizes "which thing to build."

> **❓ Interview:** *Factory vs Strategy?* → Factory chooses **which object to create**; Strategy chooses **which algorithm/behaviour to run** on an already-created object. They're often combined (a factory hands you the right strategy).

---

#### Observer (Pub/Sub)

**Definition:** A subject maintains a list of subscribers and notifies them all when its state changes (push notifications between objects).

```
        ┌──────────┐  notify()   ┌────────────┐
        │ Subject  │ ──────────► │ Observer A │
        │ (store)  │ ──────────► │ Observer B │
        └──────────┘             │ Observer C │
          state++                └────────────┘
```

```ts
// Minimal pub/sub — this IS how Redux/Zustand/RxJS-style stores work
class Store<T> {
  private listeners = new Set<(s: T) => void>();
  constructor(private state: T) {}
  subscribe(fn: (s: T) => void) {
    this.listeners.add(fn);
    return () => { this.listeners.delete(fn); }; // unsubscribe (avoid leaks!)
  }
  getState() { return this.state; }
  setState(next: T) {
    this.state = next;
    this.listeners.forEach((fn) => fn(next)); // notify all observers
  }
}
const balanceStore = new Store(0);
const off = balanceStore.subscribe((b) => console.log("balance:", b));
balanceStore.setState(500); // logs "balance: 500"
off(); // clean up
```

> **Note:** The unsubscribe returns `void` (the arrow body is wrapped in braces so it doesn't return the boolean from `Set.delete`) — handy when React's effect cleanup or `useSyncExternalStore` expects a `() => void`.

**💡 Remember:** *"Don't call us, we'll call you."* Subject pushes; observers react. **Always return an unsubscribe.**

> **❓ Interview:** *Where is Observer in React?* → State libs (Redux, Zustand, MobX, RxJS) and even browser `EventTarget`/`addEventListener` use it. `useSyncExternalStore` is React's official hook to subscribe a component to an external observable store (it takes the `subscribe` fn + a `getSnapshot`).

---

#### Module Pattern (Closures)

**Definition:** Use a closure to expose a public API while keeping internal state truly private.

```
 createCounter()
 ┌─────────────────────────┐
 │ (private) count = 0      │   ← not reachable from outside
 │ return { inc, get }      │   ← only these are public
 └─────────────────────────┘
```

```ts
// The IIFE/closure keeps `failedAttempts` private — no external mutation
const loginGuard = (() => {
  let failedAttempts = 0;            // PRIVATE state via closure
  return {
    fail() { failedAttempts++; },
    isLocked() { return failedAttempts >= 3; },
    reset() { failedAttempts = 0; },
  };
})();

loginGuard.fail(); loginGuard.fail(); loginGuard.fail();
loginGuard.isLocked(); // true — and nobody can poke failedAttempts directly
```

**💡 Remember:** *"Closure = data privacy without classes."* What's not returned can't be touched.

> **❓ Interview:** *Module pattern vs ES modules?* → Same intent (encapsulation). The closure/IIFE version predates `import`/`export`; today ES modules give file-level privacy natively, but closures still give **per-instance** privacy (call a factory twice, get two independent private states — e.g. inside custom hooks/factories). Class `#private` fields are the modern in-class equivalent.

---

#### Higher-Order Component (HOC)

**Definition:** A function that takes a component and returns a new, enhanced component (wraps to add behaviour). *Legacy pattern — know it, but prefer hooks.*

```
   withAuth( <Page/> )  ──►  <AuthWrapper>
                                 <Page/>   ← enhanced with auth logic
                              </AuthWrapper>
```

```tsx
// HOC: injects auth-gating around any page
function withAuth<P extends object>(Wrapped: React.ComponentType<P>) {
  function Guarded(props: P) {
    const user = useUser();
    if (!user) return <Redirect to="/login" />;
    return <Wrapped {...props} />;
  }
  Guarded.displayName = `withAuth(${Wrapped.displayName ?? Wrapped.name ?? "Component"})`;
  return Guarded; // preserve a readable name for DevTools
}
const ProtectedDashboard = withAuth(Dashboard); // enhanced component
```

> **Note:** Setting `displayName` on the returned wrapper is the fix for the "lost displayName / wrapper soup in DevTools" pitfall called out below — without it you just see `Guarded` everywhere.

**💡 Remember:** *"A component in, a beefier component out."* Naming convention: `withX`.

> **❓ Interview:** *HOC pitfalls?* → Wrapper hell / deep nesting, prop-name collisions, lost `displayName` in DevTools, and ref-forwarding hassle (you need `forwardRef` to pass a ref through). Hooks solve most of these — hence the shift.

---

#### Render Props

**Definition:** A component takes a function-as-a-prop (often `children`) and calls it with its internal state, letting the parent control rendering.

```
 <DataFetcher url="/api/tx">
    {(data) => <List items={data}/>}   ← consumer decides the UI
 </DataFetcher>
       │
       └─ component owns LOGIC, caller owns MARKUP
```

```tsx
function DataFetcher<T>({ url, children }: {
  url: string;
  children: (state: { data: T | null; loading: boolean }) => React.ReactNode;
}) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    let alive = true;
    setLoading(true);
    fetch(url)
      .then(r => r.json())
      .then(d => { if (alive) { setData(d); setLoading(false); } });
    return () => { alive = false; }; // guard stale responses on url change/unmount
  }, [url]);
  return <>{children({ data, loading })}</>; // hand state back to caller
}

// usage — caller owns the markup, component owns the logic
<DataFetcher<Tx[]> url="/api/tx">
  {({ data, loading }) => loading ? <Spinner/> : <TxList items={data!} />}
</DataFetcher>
```

**💡 Remember:** *"Pass a function, get back the state."* Share logic, let the caller draw.

> **❓ Interview:** *HOC vs Render Props?* → Both share cross-cutting logic. HOC wraps at definition time (static composition); render props share at render time (dynamic, no extra wrapper component but can cause a "callback pyramid" when nested). Both largely superseded by hooks.

---

#### Custom Hooks (the modern replacement)

**Definition:** A reusable function starting with `use` that encapsulates stateful logic and composes other hooks — the idiomatic way to share logic today.

```
  HOC / Render Props   ──►  Custom Hook
  (wrapper components)      (plain function, no extra nesting)

  useAuth() ─┐
  useTx()   ─┼─► composed inside one component, flat tree
  useForm() ─┘
```

```tsx
// Same auth + data logic as the HOC/render-prop above — but flat & composable
function useTransactions(accountId: string) {
  const [data, setData] = useState<Tx[]>([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    let alive = true;                              // guard against race/unmount
    setLoading(true);
    fetch(`/api/accounts/${accountId}/tx`)
      .then(r => r.json())
      .then(d => { if (alive) { setData(d); setLoading(false); } });
    return () => { alive = false; };               // cleanup
  }, [accountId]);
  return { data, loading };
}

// Consumer: no wrapper component, no render-prop pyramid — just call it
function Statement({ accountId }: { accountId: string }) {
  const { data, loading } = useTransactions(accountId);
  return loading ? <Spinner /> : <TxList items={data} />;
}
```

**Why hooks won:**
```
            HOC          Render Props     Custom Hook
 nesting    deep         pyramid          flat ✅
 props      collisions   verbose          plain returns ✅
 typing     awkward      awkward          natural TS ✅
 DevTools   wrapper soup wrapper soup     clean ✅
```

**💡 Remember:** *"Logic reuse without wrapper hell."* Rules of Hooks: name it `use*`, only call hooks at the **top level** (never inside loops, conditions, or nested functions), and only from React functions or other hooks.

> **❓ Interview:** *Why did custom hooks replace HOCs/render props?* → They reuse **stateful logic without adding to the component tree** (no wrapper hell, no prop collisions), compose cleanly, and type far better in TypeScript. One caveat: a hook shares *logic*, not *one instance of state* — every component that calls `useTransactions` gets its own state, so for shared global state you still reach for Context or a store. HOCs/render props remain valid but are now the exception, not the default.

---

### 🎯 30-Second Recall Sheet

```
SOLID  → Some Old People Lift Iron Dumbbells
         SRP  one reason to change
         OCP  extend, don't modify (strategy map)
         LSP  subtype swaps in cleanly
         ISP  small interfaces, no fat props
         DIP  depend on abstractions, inject deps

Singleton → one shared instance (ES module is one)
Factory   → ask it which object to build
Observer  → subscribe + notify (Redux/Zustand, useSyncExternalStore)
Module    → closure = private state (per-instance)
HOC       → component in → enhanced component out  (withX)
RenderProp→ pass a fn → get the state back
Custom Hook→ use* — modern logic reuse, no wrapper hell ✅
```

> **Banking tie-in:** DIP + Factory + custom hooks make data layers **mockable** → easier **testing**; Observer powers **real-time balances**; Module/closure keeps **secrets/attempt-counters private**; SRP keeps **formatting/validation auditable**.

---

## 🚀 CI/CD + Build Tools

A front-end engineer at a bank must ship safely and fast. This section covers how your code goes from `src/` to a minified bundle (build tools) and from a commit to production (CI/CD).

---

### 🧰 Build tools: Webpack vs Vite

**Definition:** Build tools take your many source files (TS/JSX/CSS) and transform + combine them into optimized assets the browser can run; Webpack is the mature bundler-first tool, Vite is the modern fast tool using native ESM in dev and Rollup for production.

```
DEV SERVER STARTUP
Webpack:  [bundle EVERYTHING first] ───────────▶ 😴 then serve   (slow cold start)
Vite:     serve instantly ▶ browser asks for a module ▶ esbuild transforms ON DEMAND
          └ native ES modules, no upfront bundle ─────────────────▶ ⚡ fast

PRODUCTION BUILD
Vite ──▶ Rollup (great tree-shaking, clean output)
Webpack ──▶ its own bundler (config-heavy, plugin ecosystem huge)
```

| | Webpack | Vite |
|---|---|---|
| Dev start | Bundles all up front → slow | Native ESM, no bundle → instant |
| Dev transform | Loaders (babel-loader, ts-loader) | **esbuild** (Go, ~10–100× faster) |
| Prod bundler | Webpack | **Rollup** |
| Config | Verbose `webpack.config.js` | Minimal `vite.config.ts` |
| HMR | Slower as app grows | Near-instant, file-scoped |

```ts
// vite.config.ts — minimal, readable
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: { sourcemap: true, outDir: 'dist' },
});
```

**💡 Remember:** **"Vite = esbuild in Dev, Rollup in Deploy."** Webpack bundles *before* serving; Vite serves *then* transforms. (Vite = French for "fast", pronounced "veet".)

**❓ "Why is Vite's dev server faster than Webpack's?"** Webpack builds a full bundle of the whole app before it can serve a single page, so cold start grows with app size. Vite serves immediately over native ES modules and only transforms each file when the browser requests it, using esbuild (written in Go) instead of JS-based Babel. So startup is roughly constant regardless of project size, and HMR only re-processes the changed module. (Note: Webpack 5 supports lazy compilation and persistent caching, which narrow the gap — but Vite's no-bundle-in-dev model still wins on cold start.)

---

### 📦 Bundling

**Definition:** Combining many separate source modules (and their dependencies) into one or a few files to reduce HTTP requests and resolve `import`/`require` for the browser.

```
src/index.js ─┐
src/utils.js ─┼──▶ [BUNDLER] ──▶ bundle.js   (1 file, deps resolved & ordered)
node_modules ─┘
```

```js
// index.js
import { format } from './money.js';
console.log(format(1000));

// money.js
export const format = (n) => `$${n.toLocaleString()}`;

// → bundler inlines both into one runnable bundle.js
```

**💡 Remember:** **Bundling = "many modules → few files."** Fewer round-trips = faster load.

**❓ "Why bundle at all if HTTP/2 multiplexes?"** HTTP/2 reduces the request-overhead penalty, but bundling still helps: better compression (gzip/brotli over combined content), fewer cache entries to manage, and it's the stage where tree shaking + minification happen. Modern practice is *some* bundling plus code splitting — not one giant file, not 1000 tiny ones.

---

### 🌳 Tree shaking

**Definition:** Dead-code elimination that drops unused **exports** from the final bundle, relying on static `import`/`export` (ESM) analysis.

```
utils.js exports: add, sub, mul, div
app.js  imports: add only
            ▼ tree shaking
bundle.js keeps: add   ✂️ (sub, mul, div removed)
```

```js
// math.js
export const add = (a, b) => a + b;
export const sub = (a, b) => a - b;   // never imported

// app.js
import { add } from './math.js';      // only `add` survives bundling
console.log(add(2, 3));
```

**💡 Remember:** **"Shake the tree, dead leaves (unused exports) fall off."** Needs **ESM** (`import/export`), not CommonJS `require` — and mark side-effect-free code with `"sideEffects": false` in package.json so the bundler can drop unused modules safely.

**❓ "Why does tree shaking need ES modules?"** ESM imports/exports are *static* — resolvable at build time without running the code — so the bundler can prove an export is never used. CommonJS `require()` is dynamic (can be conditional, computed), so the bundler can't safely tell what's unused.

---

### 🗜️ Minification

**Definition:** Shrinking output by removing whitespace/comments and renaming variables to short names, without changing behavior.

```
function calculateTotal(price, taxRate) {   ──▶  function c(a,b){return a+a*b}
  return price + price * taxRate;          minify
}
```

```js
// before (dev):  meaningful names, formatting
const accountBalance = getBalance();

// after (minified):  const a=g();  ← smaller payload, same behavior
```

**💡 Remember:** **Minify = "remove humans, keep logic."** Tree shaking removes *unused* code; minification compresses *used* code. (Different jobs!)

**❓ "Difference between tree shaking and minification?"** Tree shaking *deletes code paths that are never imported/used*; minification *rewrites the code that remains* into a smaller form (rename, strip whitespace). Both shrink the bundle, but tree shaking is about reachability, minification is about representation.

---

### 🗺️ Source maps

**Definition:** A `.map` file that maps minified/transpiled production code back to your original source, so DevTools shows real file names, lines, and variable names when debugging.

```
bundle.min.js  (line 1: c(a,b){...})
      │  bundle.min.js.map
      ▼
account.ts  line 42: calculateTotal(price, taxRate)   ← what you actually see in DevTools
```

```ts
// vite.config.ts / webpack: enable source maps
build: { sourcemap: true }        // Vite
// webpack:  devtool: 'source-map'
```

**💡 Remember:** **Source map = "GPS from minified code back home to your source."**

**❓ "Should you ship source maps to production in a banking app?"** Be careful: source maps expose your original source structure. Common safe practices — generate them but **don't publicly serve them** (upload to your error tracker like Sentry, then delete or gitignore them from the deployed assets), or restrict access behind auth. They're invaluable for debugging real prod errors but are a mild information-disclosure risk if public.

---

### ✂️ Code splitting

**Definition:** Breaking the bundle into smaller chunks loaded **on demand** (e.g. per route), so users download only what the current view needs.

```
WITHOUT splitting:  [=========== one huge bundle ===========]  load it all upfront
WITH splitting:     [main] + [dashboard.chunk] + [transfers.chunk]
                       ▲ load route chunks lazily, when visited
```

```tsx
import { lazy, Suspense } from 'react';
const Transfers = lazy(() => import('./Transfers')); // its own chunk

function App() {
  return (
    <Suspense fallback={<Spinner />}>
      <Transfers />        {/* downloaded only when rendered */}
    </Suspense>
  );
}
```

**💡 Remember:** **Code splitting = "lazy-load what you don't need yet."** `React.lazy` + `Suspense` + dynamic `import()` = route-level splitting. (The dynamic `import()` is the seam the bundler splits on — `React.lazy` just wraps it.)

**❓ "How do you reduce initial load time of a large React app?"** Route-based code splitting with `React.lazy`/dynamic `import()`, so the dashboard's JS isn't downloaded on the login page; plus tree shaking, minification, compression (brotli), caching with content hashes, and lazy-loading heavy components/images below the fold.

---

### 🔁 CI/CD: the three pipelines

**Definition:** **CI (Continuous Integration)** = on every push, automatically build and test the merged code; **CD (Continuous Delivery/Deployment)** = automatically package and release that validated code to an environment.

```
 commit / PR
     │
     ▼
┌──────────┐   ┌──────────┐   ┌───────────┐
│  BUILD   │──▶│   TEST   │──▶│  DEPLOY   │
│ install  │   │ lint     │   │ to staging│
│ compile  │   │ unit     │   │ → prod    │
│ bundle   │   │ e2e/a11y │   │ (gated)   │
└──────────┘   └──────────┘   └───────────┘
   fails?         fails?         fails?
   stop ✋        stop ✋        rollback ↩
```

- **Build stage:** install deps, type-check, compile/transpile, produce the production bundle. Catches "doesn't even build."
- **Test stage:** lint (ESLint/Prettier), unit tests (Jest/Vitest), integration, e2e (Playwright/Cypress), accessibility (axe), coverage gate. Catches regressions before merge.
- **Deploy stage:** publish artifact to staging, run smoke tests, then promote to production — often behind manual approval (Continuous *Delivery*) or fully automatic (Continuous *Deployment*).

**💡 Remember:** **"Build it ▶ Test it ▶ Ship it."** CI = build+test on every push; CD = the ship. Delivery = human clicks "go"; Deployment = auto.

**❓ "Difference between Continuous Delivery and Continuous Deployment?"** Both automate everything through to a deployable/released artifact. In **Delivery**, the release to production requires a manual approval gate (a human clicks deploy). In **Deployment**, every change that passes the pipeline goes to production automatically, no human step.

---

### 🤔 Why CI/CD

**Definition:** Automating build/test/deploy so problems are caught early and releases are small, frequent, and reversible instead of rare and risky.

```
NO CI/CD:  big-bang release every 3 months ──▶ 💥 huge blast radius, scary
WITH CI/CD: tiny changes daily ──▶ ✅ each verified, easy to pinpoint & roll back
```

- **Catch breakage early** — a failing test blocks the PR, not the customer.
- **Safe, frequent releases** — small diffs are easy to review, deploy, and revert.
- **Consistency** — same scripted pipeline every time; no "works on my machine."
- **Banking angle:** enforced quality gates (tests, a11y, security scans, approvals) give an auditable, repeatable release process — critical for compliance.

**💡 Remember:** **"Small batches, fast feedback."** The earlier a bug is caught, the cheaper it is to fix (shift left).

**❓ "Why does CI/CD matter for a banking app?"** It enforces consistent quality and security gates on every change (tests, accessibility, dependency/security scans, mandatory approvals), gives an auditable trail for compliance, and makes releases small and reversible — so a defect has a tiny blast radius and can be rolled back fast, which is exactly what regulated financial software needs.

---

### 📝 Code: minimal GitHub Actions workflow

**Definition:** GitHub Actions runs YAML-defined **workflows** (triggered by events) made of **jobs** that run **steps** on a hosted runner — the standard way to wire CI/CD in a GitHub repo.

```
event (push/PR)
   └─▶ workflow ──▶ job(s) ──▶ step(s) on a runner (ubuntu-latest)
                    install ▶ lint ▶ test ▶ build ▶ deploy
```

```yaml
# .github/workflows/ci.yml
name: CI/CD

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4              # 1. get the code

      - uses: actions/setup-node@v4            # 2. set up Node
        with:
          node-version: 20
          cache: 'npm'                         # cache deps for speed

      - run: npm ci                            # 3. install (clean, lockfile-exact)
      - run: npm run lint                      # 4. lint
      - run: npm test -- --coverage            # 5. unit tests + coverage
      - run: npm run build                     # 6. production build

      - uses: actions/upload-artifact@v4       # 7. save the build output
        with:
          name: dist
          path: dist/

  deploy:
    needs: build-test                          # only if build-test passed
    if: github.ref == 'refs/heads/main'        # deploy only from main
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: echo "Deploying to production..." # e.g. deploy to S3 / Azure / Vercel
```

**Key vocabulary:**
- `on:` — the **trigger** (push, pull_request, schedule, manual via `workflow_dispatch`).
- `jobs:` — run in parallel by default; `needs:` chains them sequentially.
- `steps:` — `uses:` runs a prebuilt **action**; `run:` runs a shell command.
- `npm ci` vs `npm install` — `ci` is faster and installs **exactly** from `package-lock.json` (deterministic, ideal for pipelines).

**💡 Remember:** **"Workflow → Jobs → Steps."** Order of a step set: **checkout → setup-node → ci → lint → test → build → deploy.** Use `needs:` to gate deploy behind a green build.

**❓ "Walk me through a CI workflow for a React app."** On every push/PR, GitHub spins up a runner: checkout the code, set up Node (with dep caching), `npm ci` for a deterministic install, then lint → unit tests with coverage → production build. If any step fails the PR is blocked. On merge to `main`, a separate `deploy` job (gated by `needs:` and an `if:` branch check) publishes the artifact to the hosting environment — optionally behind a manual approval for production (GitHub Environments with required reviewers).

---

**Section cheat-sheet:** Bundle (many→few) → Tree-shake (drop unused exports, needs ESM) → Minify (compress what's left) → Source maps (debug minified) → Code-split (lazy-load per route). Pipeline: **Build ▶ Test ▶ Deploy**, each gated, via GitHub Actions **Workflow → Jobs → Steps**. CI = verify every push; CD = ship safely and often. Banking = the gates (tests, a11y, security, approvals, audit trail) are the point.

---

## 🌟 Behavioral (STAR) — Tell Your Story, Lead With Impact

### The STAR Framework

**Plain definition:** STAR is a four-part structure — Situation, Task, Action, Result — for answering "Tell me about a time when…" questions so your answer stays focused, concrete, and ends on measurable impact.

```
 S ──► T ──► A ──────────────► R
Context Goal  What *I* did      Outcome (with a NUMBER)
 ~5%    ~5%   ~70% (the meat)    ~20%

  ▲ keep short        ▲ spend most time here   ▲ never skip
```

**The 4 parts:**

| Letter | Question it answers | Length | Trap to avoid |
|--------|--------------------|--------|---------------|
| **S**ituation | Where/when? What was the context? | 1–2 sentences | Don't over-explain backstory |
| **T**ask | What was *your* responsibility / the goal? | 1 sentence | Don't confuse with Action |
| **A**ction | What did **you** specifically do? | Bulk of answer | Say "I", not "we" |
| **R**esult | What changed? Quantify it. | 1–2 sentences | Never end without an outcome |

**Three rules that separate good from great:**
1. **Lead with impact** — for senior-ish candidates, you can open with a one-line headline result, *then* unpack it. ("I cut our dashboard load time by 60%. Here's how…")
2. **Use "I", not "we"** — interviewers are scoring *you*. Use "we" for context, "I" for contribution.
3. **Always land the Result with a metric** — %, ms, $, users, error-rate, tickets. A number makes it real.

**💡 Remember:** *"**S**ome **T**igers **A**re **R**eliable"* — and the tiger (Action) is the biggest animal. Also: **"We sets the scene, I gets the credit."**

**❓ "Why structure answers with STAR?"**
> "It keeps me from rambling. I give just enough context, state the goal, then spend most of the time on what *I* specifically did, and I always close with a measurable result so the impact is clear. For a banking role that matters — it shows I think in terms of outcomes like uptime, latency, and user trust, not just code."

---

### The "Tell Me Your Journey" Answer Structure

**Plain definition:** A 60–90 second plain-words narrative of your career arc — Present → Past → Why-here — used to open almost every interview ("Tell me about yourself").

```
   PRESENT  ──►  PAST  ──►  WHY THIS ROLE
  "I'm a..."   "I got here   "...which is why
  what you do   by..."        Wells Fargo fits"
  right now    1-2 proof pts   bridge to them
```

**Template:**
> "I'm a full-stack engineer with about three years building production apps on the **MERN** stack — React and TypeScript on the front end, Node, Express and MongoDB behind it. **(Present)**
> Most recently I owned customer-facing features end-to-end — from talking through requirements, to architecture, to shipping and monitoring in production. Along the way I got deep on the things that break real apps at scale: performance, accessibility, and writing tests I can trust. **(Past + proof)**
> I'm drawn to Wells Fargo because I want my work to matter to real people's money — where reliability, security, and accessibility aren't nice-to-haves, they're the job. That's the kind of front-end engineering I want to do next. **(Why here)**"

**💡 Remember:** **"P-P-W: Present, Past, Why."** Don't tell your life story from birth — start with today and aim at *them*.

---

### Story 1 — A Production Bug You Fixed

**Plain definition:** A story proving you stay calm under fire, diagnose methodically, and fix root cause — not just symptoms.

**Talking points / structure:**

| STAR | What to say |
|------|-------------|
| **S** | "In production, users intermittently saw stale account balances after a transfer." |
| **T** | "I owned the fix — get it diagnosed and shipped without breaking other flows." |
| **A** | Reproduce → isolate → root cause → fix → guard with a test. *Be specific about the diagnosis.* |
| **R** | "Error reports dropped to zero; I added a regression test so it couldn't recur." |

**Example diagnosis narrative (be concrete):**
```
Symptom:  Stale balance shown after transfer
Repro:    Only on fast double-submit
Tooling:  Network tab + logs → React Query cache not invalidated
Root cause: Mutation didn't invalidate the 'accounts' query key
Fix:      invalidateQueries on success + optimistic rollback
Guard:    Added test for the cache-invalidation path
```
```tsx
// The actual fix: invalidate the cache after the mutation succeeds.
// onMutate snapshots the old data BEFORE the optimistic update, so onError
// can roll back; onSuccess invalidates so the UI refetches fresh balances.
const queryClient = useQueryClient();

const mutation = useMutation({
  mutationFn: postTransfer,
  // 1) snapshot previous balances so we can roll back on failure
  onMutate: async (vars) => {
    await queryClient.cancelQueries({ queryKey: ['accounts'] });
    const previous = queryClient.getQueryData(['accounts']);
    // (optionally apply the optimistic update here)
    return { previous }; // becomes `ctx` in onError / onSettled
  },
  // 2) roll back optimistic update so the UI never lies about money
  onError: (_err, _vars, ctx) => {
    if (ctx?.previous !== undefined) {
      queryClient.setQueryData(['accounts'], ctx.previous);
    }
  },
  // 3) the line that was missing — refetch fresh balances after success
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['accounts'] });
  },
});
```

> **Why the rollback needs `onMutate`:** `onError`'s third argument (`ctx`) is whatever you *return* from `onMutate`. The original draft read `ctx?.previous` but never populated it — so the rollback would have restored `undefined`. Snapshotting in `onMutate` is what makes the rollback real.

**💡 Remember:** **"Reproduce → Root-cause → Repair → Regression-test"** = the **4 R's** of a bug story. Never say "I just restarted it."

**❓ "Tell me about a hard bug you fixed."**
> Lead with impact: "We had intermittent stale balances after transfers — scary in a money app. I reproduced it on fast double-submits, traced it through the network tab to a cache that wasn't being invalidated after the mutation, added the invalidation plus an optimistic rollback, and locked it in with a regression test. Error reports went to zero."

---

### Story 2 — Feature Ownership (End-to-End)

**Plain definition:** A story showing you can take a fuzzy requirement and drive it through architecture, implementation, and a shipped result.

```
Requirement ──► Architecture ──► Implementation ──► Result
"users need     "chose X over Y    "built it,         "shipped,
 to export"      because..."        tested, a11y"      adopted by N%"
```

**Talking points:**
- **Requirement:** State the user need + constraint. *"Customers needed to export statements as PDF; had to work on mobile + be accessible."*
- **Architecture:** Name a real decision *and the trade-off*. *"Generated PDFs server-side in Node rather than client-side, so low-end phones wouldn't choke and the logic stayed testable."*
- **Implementation:** Components, API contract, edge cases, tests, a11y. *"React form with proper labels + ARIA, Express endpoint, MongoDB for the audit log."*
- **Result:** Adoption / time saved / tickets reduced. *"Shipped in two sprints; ~30% of active users used it in month one; support tickets for 'how do I get my statement' dropped."*

**💡 Remember:** **"RAIR" — Requirement, Architecture, Implementation, Result.** The standout part is *naming the trade-off* in Architecture.

**❓ "Tell me about a feature you owned."**
> "I owned PDF statement export. The requirement was customers needing self-serve statements that worked on mobile and were accessible. I chose to generate PDFs server-side in Node — trading a little server cost for not choking low-end devices and keeping the logic unit-testable. I built the React form with full keyboard + ARIA support, the Express endpoint, and an audit log in Mongo. It shipped in two sprints and about a third of active users adopted it in the first month."

---

### Story 3 — A Performance Improvement (with a Metric)

**Plain definition:** A story proving you can measure first, fix the real bottleneck, and prove the win with before/after numbers.

```
 BEFORE                          AFTER
 ──────                          ─────
 Load: 4.2s  ┃████████████       Load: 1.6s  ┃████
 Re-renders  ┃ on every keystroke Re-renders ┃ only on submit
        ▲ MEASURE first                ▲ verify the win
```

**Talking points (pick ONE concrete lever):**
- **Cut re-renders:** `React.memo`, `useMemo`/`useCallback` for referential stability, stable keys, splitting context.
- **Cut load time:** code-splitting (`React.lazy`), list virtualization, debounced input, fewer/parallel network calls, caching.
- **Always:** "I profiled with the React DevTools Profiler / Lighthouse *first*, found the actual hotspot, fixed it, and re-measured."

```tsx
// Example lever: stop a heavy list re-rendering on every parent state change.
// React.memo only helps if the props are referentially stable between renders —
// that's why `sorted` is memoized and each row gets a stable `key`.
const TransactionRow = React.memo(function TransactionRow({ txn }: { txn: Txn }) {
  return <li>{txn.date} — ${txn.amount}</li>;
});

function TransactionList({ txns }: { txns: Txn[] }) {
  // memoize derived data so the array identity is stable between renders
  const sorted = useMemo(
    () => [...txns].sort((a, b) => b.date.localeCompare(a.date)),
    [txns]
  );
  return (
    <ul>
      {sorted.map((t) => (
        <TransactionRow key={t.id} txn={t} />
      ))}
    </ul>
  );
}
```

> **Note on `React.memo`:** it does a *shallow* prop compare, so it only prevents re-renders when the props it receives are referentially stable. Passing a fresh inline `onClick={() => …}` or a freshly-built array would defeat it — hence memoizing `sorted` and keeping handlers stable with `useCallback`.

**💡 Remember:** **"Measure → Fix → Re-measure. No number, no story."** Profiling first is what makes it senior, not lucky.

**❓ "Tell me about a performance problem you solved."**
> "Our transactions dashboard took ~4 seconds to become interactive and janked on every keystroke. I profiled with the React DevTools Profiler first and found two things: the whole list re-rendered on each filter keystroke, and we shipped one giant bundle. I memoized the rows, debounced the filter input, and code-split the route. Time-to-interactive dropped from ~4.2s to ~1.6s and the typing jank was gone — I verified both with Lighthouse before and after."

---

### Story 4 — Team Conflict / Disagreement (Disagree-and-Commit)

**Plain definition:** A story showing you can disagree respectfully *with data*, then fully commit to the team's decision either way — no sulking, no "I told you so".

```
   Disagree ──► with DATA ──► Decide ──► COMMIT
   (respectfully)  (not ego)   (team)   (fully, even if you lost)
                                            ▲ this is the point
```

**Talking points:**
- Frame it as a **technical disagreement**, not a personality clash.
- Show you **sought to understand** the other view first.
- Bring **data/trade-offs**, not opinion. *"I raised that approach X risked memory leaks; I built a small spike to show it."*
- **Land on commit:** *"We went with their approach for delivery speed; I disagreed but committed, helped ship it, and we added monitoring to catch the risk I'd flagged."*
- Bonus: a graceful "and they were right" or "the monitoring caught it later" shows zero ego.

**💡 Remember:** **"Disagree with data, commit without drama."** The interviewer is testing maturity, not who won.

**❓ "Tell me about a disagreement with a teammate."**
> "A teammate wanted to keep heavy state in React Context; I worried about re-render performance and proposed a more scoped store. I built a tiny profiling spike to show the re-render cost rather than just argue. We discussed the trade-off and, given the deadline, chose Context for now with a plan to revisit. I disagreed but committed — helped ship it and added a Profiler check to our review checklist. It stayed fine at our scale, which honestly was the right call for that moment."

---

### Story 5 — "Why Banking / Why Wells Fargo?"

**Plain definition:** A story (really a *motivation* answer) connecting your engineering values to what a bank uniquely demands: reliability, scale, security, and earning user trust.

```
   YOUR VALUES        ────►      WHAT A BANK NEEDS
   reliability                   uptime on people's money
   accessibility       maps to   everyone can bank (often regulated)
   security/testing              trust + compliance
   scale                         millions of customers
```

**Talking points — name all four pillars:**
- **Reliability:** *"In a banking app, 'works most of the time' isn't acceptable — it's someone's paycheck. I like engineering where correctness is the bar."*
- **Scale:** *"Wells Fargo serves millions — I want problems where performance and resilience genuinely matter."*
- **Security & trust:** *"I already care about input validation, auth, not leaking data — here it's mission-critical, and I want that discipline to be the norm around me."*
- **Accessibility & user trust:** *"Banking has to work for everyone — keyboard users, screen readers. Building software people trust with their money is meaningful work."*
- **Wells Fargo specifically:** tie to its scale + customer focus + that it's investing in modern front-end. (Drop one concrete, current detail if you know it.)

**💡 Remember:** **"R-S-S-T: Reliability, Scale, Security, Trust."** Make it about *their customers*, not just "it's a stable job."

**❓ "Why do you want to work in banking / at Wells Fargo?"**
> "Three years in, the engineering problems I find most motivating are exactly the ones banking forces you to take seriously — reliability when it's someone's money, performance at the scale of millions of customers, and security and accessibility as non-negotiables rather than afterthoughts. I already try to build that way; I want to do it where it genuinely matters and be surrounded by people held to the same bar. Wells Fargo's scale and its push toward modern, customer-facing front-end is exactly where I want to grow."

---

### Quick Cheat-Sheet (last-minute glance)

```
STAR        = Situation · Task · Action · Result  (spend ~70% on Action)
"We" sets scene, "I" gets credit. End with a NUMBER.
Journey     = Present → Past → Why-here  (60–90s)
Bug         = Reproduce → Root-cause → Repair → Regression-test
Feature     = Requirement → Architecture(trade-off!) → Implementation → Result
Performance = Measure → Fix → Re-measure  (no metric, no story)
Conflict    = Disagree with data → Commit without drama
Why WF      = Reliability · Scale · Security · Trust  (about THEIR customers)
```

**💡 Final memory hook:** **"Every story is a tiny STAR; every Result is a number; every answer points back at the bank's customer."**

---

## 🧠 Night-Before Cheat Sheet — Wells Fargo MERN / Front-End SWE

### 💡 Memory Tricks & Mnemonics

1. **Event loop** — "Micro before macro": ALL microtasks (Promises, queueMicrotask) drain before the NEXT macrotask (setTimeout, I/O) runs.
2. **Promise.all** — "All or nothing": resolves with array, but **rejects on first failure**.
3. **Promise.race** — "First to finish, win OR lose": settles on the first to settle (resolve *or* reject).
4. **Promise.any** — "First to *succeed*": ignores rejections, throws `AggregateError` only if all fail.
5. **Promise.allSettled** — "Never rejects": waits for everyone, gives `{status, value/reason}` for each.
6. **Closures** — "A function remembers the crib it was born in": inner fn keeps access to outer scope vars.
7. **`this`** — "Arrows have no `this` — they steal the parent's": arrows lexically bind; regular fns bind at *call* time.
8. **TDZ** — "`let`/`const` exist but are jailed until declared": Temporal Dead Zone = ReferenceError before the line.
9. **Ref vs value** — "Primitives copy, objects share": `=` clones a number/string but aliases an object/array.
10. **useMemo** — "Memo cves a **VALUE**" (the result of a computation).
11. **useCallback** — "Callback caches a **FUNCTION**" (`useCallback(fn) === useMemo(() => fn)`).
12. **React.memo** — "memo = **shallow props** check": skips re-render if props are shallow-equal.
13. **useEffect cleanup** — "Cleanup runs BEFORE the next effect (and on unmount)": old effect tears down first.
14. **Keys** — "Keys need a stable ID, never the index": index keys break on reorder/insert/delete.
15. **Redux flow** — "**A-D-R-S**: Action → Dispatch → Reducer → Store" (one-way, pure reducers, single source of truth).
16. **WCAG AA** — "**4.5:1** for normal text, **3:1** for large/UI" — contrast is the AA bar.
17. **RTL queries** — "Query by **role** first, test ID last": `getByRole` > label > text > testId (use what users use).
18. **TDD** — "**Red → Green → Refactor**": fail first, pass minimally, then clean up.
19. **Core Web Vitals** — "**LIC**: **L**CP load (<2.5s), **I**NP interactivity (<200ms), **C**LS layout shift (<0.1)."
20. **CSS specificity** — "**I-C-E**: **I**nline > IDs > **C**lasses/attrs/pseudo > **E**lements" (then source order; `!important` overrides all).
21. **XSS** — "XSS = injected **script**; defense = escape/encode output + sanitize + CSP."
22. **CSRF** — "CSRF = forged **request**; defense = anti-CSRF token + SameSite cookies."
23. **JWT** — "JWT = signed, not secret: 3 parts (header.payload.signature), verify the signature, don't trust the body."
24. **SOLID** — "**S**ingle-responsibility, **O**pen/closed, **L**iskov, **I**nterface-segregation, **D**ependency-inversion."
25. **Tree-shaking** — "Tree-shaking needs **ESM** (`import`/`export`): static analysis drops unused dead code; CommonJS can't."

---

### 🎤 Answer Openers (5 most likely verbal questions)

- **"Why is React fast?"** → *"Because it works against a virtual DOM — it diffs in memory, batches updates, and only touches the real DOM where something actually changed, which avoids expensive layout thrashing."*
- **"Explain the event loop."** → *"JS is single-threaded with a call stack. Async work goes to Web APIs, then completions queue up — and the loop always drains the **microtask** queue (Promises) fully before pulling the next **macrotask** (timers/events)."*
- **"useMemo vs useCallback?"** → *"Both memoize to skip work across renders. `useMemo` caches a computed **value**; `useCallback` caches a **function** reference — really just `useMemo` returning the function — so child components don't re-render on a new identity."*
- **"What causes a re-render in React?"** → *"Three things: a state change, a prop change, or a parent re-rendering. Context value changes also re-render consumers — and React.memo can short-circuit re-renders when props are shallow-equal."*
- **"Why banking / Wells Fargo?"** → *"Because front-end here isn't just UI — it's trust at scale. Millions rely on it daily, so accessibility, security, and performance actually matter, and I want my code to carry that weight of reliability."*
