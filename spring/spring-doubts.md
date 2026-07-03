# 🌱 Spring Boot — My Doubts & Answers (running log)

> A Q&A journal as I learn Spring. I add doubts, the answer goes here, I push to git to revise later.

---

## Q1 — What is Spring? (simple words)
**Spring is a framework for building Java apps, whose heart is a "container" that creates your objects and wires them together for you.**

- You write classes and mark them as components (`@Service`, `@Component`, `@RestController`).
- Spring's **IoC container** (Inversion of Control) **creates** those objects (called **beans**) and **injects** them where needed — so you never `new` your dependencies by hand. That injecting is **DI (Dependency Injection)**.
- Around that core, Spring has modules: **Spring MVC** (web), **Spring Data** (databases), **Spring Security** (auth), etc.

🧠 **Analogy:** Spring is a smart **assembly-line manager** — you describe the parts (beans), and it builds the machine and plugs the parts into each other automatically.
🔗 **Nest bridge:** Spring's container + `@Service` = Nest's IoC container + `@Injectable`.

---

## Q2 — What is Spring Boot?
**Spring Boot = Spring + "batteries included" so it just runs with almost no configuration.**

Plain Spring needs a lot of manual setup. Spring Boot adds three things:
1. **Auto-configuration** — sensible defaults; it sees H2 on the classpath and auto-configures a database connection for you.
2. **Starters** — one dependency pulls in everything you need (e.g., `spring-boot-starter-web` brings web + JSON + an embedded server).
3. **Embedded server** — Tomcat is built in, so `mvnw spring-boot:run` starts a web server; no separate install.

🧠 **Analogy:** Plain Spring = building a PC from individual parts. Spring Boot = a PC that boots out of the box.

---

## Q3 — What is JPA?
**JPA (Jakarta Persistence API) is a STANDARD/specification for mapping Java objects to database tables — so you work with objects, not raw SQL.** (ORM = Object-Relational Mapping.)

- It's just a **set of interfaces + annotations** (`@Entity`, `@Id`, `@Column`) — the **rulebook**, not the actual working code.
- You annotate a class `@Entity` and JPA says "this maps to a table."

🧠 **Analogy:** JPA is the **USB standard** — it defines how things should plug together, but it isn't a device itself.

---

## Q4 — What is Hibernate?
**Hibernate is the most popular IMPLEMENTATION of the JPA standard — the actual engine that turns your `@Entity` objects into SQL, runs it, and maps the rows back to objects.**

- JPA = the interface/standard. **Hibernate = the engine that implements it.**
- When you call `repo.save(task)`, the chain is: Spring Data JPA → JPA → **Hibernate generates `INSERT ...`** → JDBC → database.

🧠 **Analogy:** JPA is the USB *standard*; Hibernate is an actual USB *device* that follows it.

**The full layering (top = your code):**
```
Your code
   │  repo.save(task)
   ▼
Spring Data JPA   ← gives you repositories for free (JpaRepository)
   ▼
JPA (the standard / interfaces)
   ▼
Hibernate (the engine that writes the SQL)
   ▼
JDBC (Java's low-level DB driver API)
   ▼
Database (H2 / MySQL / Postgres)
```
🔗 **Nest bridge:** Spring Data JPA + Hibernate ≈ TypeORM (TypeORM is both the "standard" and the "engine" rolled into one).

---

## Q5 — What is H2?
**H2 is a tiny, fast SQL database written in Java that can run IN-MEMORY (in RAM) — zero install, starts instantly, wiped when the app stops.**

- Perfect for **learning and tests**: no need to install MySQL/Postgres.
- In production you swap H2 for MySQL/Postgres — and because you coded against **JPA/Hibernate**, you barely change code, just config.
- It also has a browser **H2 console** to view your tables.

🧠 **Analogy:** H2 is a **disposable practice database**.

---

## Q6 — There's no `canActivate` Guard in Spring Boot. How do I check a valid user / role permission to hit an API?
Correct — Spring has no single `@Guard`/`canActivate`. The equivalent is **Spring Security** (the real way), with a lightweight `HandlerInterceptor` option for simple cases.

**Spring Security = a filter chain that runs BEFORE your controllers (the "guard at the door").** It splits into two jobs:

- **Authentication ("who are you?")** — a filter validates the JWT (or session), and builds an `Authentication` (the logged-in user + their roles) into the `SecurityContext`. Invalid/expired → **401 Unauthorized**.
- **Authorization ("what are you allowed to do?")** — two styles:

  **(a) URL-based** — in a `SecurityConfig`:
  ```java
  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
      http.authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/admin/**").hasRole("ADMIN")   // only admins
              .requestMatchers("/api/tasks/**").hasAnyRole("USER","ADMIN")
              .anyRequest().authenticated())
          .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults())); // validate JWT
      return http.build();
  }
  ```

  **(b) Method-based — the closest thing to Nest's `@Roles('admin')` guard.** Turn it on with `@EnableMethodSecurity`, then annotate methods:
  ```java
  @PreAuthorize("hasRole('ADMIN')")            // role check
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) { ... }

  @PreAuthorize("hasAuthority('task:write')")  // permission/authority check
  @PostMapping
  public TaskResponse create(...) { ... }
  ```
  Denied → **403 Forbidden**.

**Mapping to NestJS (so it clicks):**
| NestJS | Spring |
|---|---|
| `@UseGuards(JwtAuthGuard)` (authN) | Spring Security filter validates JWT → `SecurityContext` |
| `@UseGuards(RolesGuard)` + `@Roles('admin')` (authZ) | `@PreAuthorize("hasRole('ADMIN')")` |
| `canActivate()` returns `boolean` | the filter / `@PreAuthorize` (allow-deny), or `HandlerInterceptor.preHandle` returning `boolean` |

**Lightweight version (no Spring Security — fine for learning):** a `HandlerInterceptor.preHandle` IS a guard — return `false` to block:
```java
public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
    String role = currentUserRole(req);                 // read from JWT/header
    if (!"ADMIN".equals(role)) { res.setStatus(403); return false; } // deny → blocks the controller
    return true;                                        // allow
}
```
That's exactly the "guard" shape — it's why the `LoggingInterceptor` comment said *"a guard lives here."*

> 📌 **Takeaway:** Nest's Guard → Spring's **Spring Security** (filter for authN + `@PreAuthorize` for role/permission authZ). Full Spring Security is its own lesson, but the concept = a gate before the controller that says yes/no based on who you are and what role you have.
