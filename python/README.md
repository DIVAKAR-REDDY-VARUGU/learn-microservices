# 🐍 Python Full-Stack Track

Learning Python **from scratch** → **FastAPI + React + PostgreSQL**, with a data toolkit
(NumPy / Pandas / Seaborn) on the side. Sibling track to [`../dsa`](../dsa), [`../nest`](../nest),
[`../spring`](../spring).

## Stack
- **Language:** Python 3.11
- **Backend:** FastAPI + Uvicorn
- **Data:** NumPy · Pandas · Matplotlib · Seaborn · openpyxl
- **DB:** SQLAlchemy + PostgreSQL (+ Alembic migrations)
- **Auth:** JWT (python-jose) + passlib
- **Frontend:** React + Redux (reused from [`../frontend`](../frontend))
- **Tooling:** ruff · pytest · httpx

## Setup (one-time — already done)
```powershell
# from learn-microservices/python
python -m venv .venv                          # isolated interpreter + libs (≈ per-project node_modules)
.\.venv\Scripts\Activate.ps1                  # activate → prompt shows (.venv)
python -m pip install -r requirements.txt     # install the toolkit
```
> If activation is ever blocked, run once:
> `Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned`

## Daily use
```powershell
.\.venv\Scripts\Activate.ps1        # activate (every new terminal)
python 01-language-core\hello.py    # run a script
deactivate                          # leave the venv
```

## Module ladder (fast track)
| # | Folder | Focus |
|---|--------|-------|
| 1 | `01-language-core`      | types, strings, collections, control flow |
| 2 | `02-idioms-oop`         | functions, comprehensions, classes, errors, type hints |
| 3 | `03-data-toolkit`       | NumPy → Pandas → Seaborn (+ Excel via openpyxl) |
| 4 | `04-fastapi`            | routing, Pydantic DTOs, `Depends()` DI, async |
| 5 | `05-db-auth`            | SQLAlchemy + Postgres, JWT auth, CSV/Excel upload |
| 6 | `06-fullstack-capstone` | React UI ↔ FastAPI ↔ Postgres |

## 📓 Lessons journal
_Append as we go — doubts, explanations, mistakes, optimized approaches._

### Day 1 — Setup
- Created `.venv` — a **per-project** isolated Python interpreter + libraries
  (≈ Node's per-project `node_modules`, but it also pins the interpreter).
- Installed the full toolkit from `requirements.txt`.

### Day 1 — Lesson 1: Variables & Types
- No `let`/`const` — Python infers type from the value. `True`/`False` (capital), `None` (not `null`).
- Core types: `str`, `int`, `float`, `bool`, `NoneType`. Inspect with `type(x)` / clean name `type(x).__name__`.
- **f-strings** = JS template literals: `f"Age: {age}"`.

### Day 1 — Lesson 2: Collections (list · dict · set · tuple)
- Map to Java: `list`→ArrayList, `dict`→HashMap, `set`→HashSet, `tuple`→immutable record.
- ⚠️ **`list` is a dynamic array (ArrayList), NOT a deque:** `l[i]` O(1), append/pop **back** O(1),
  but `insert(0,x)` / `pop(0)` at the **front** is **O(n)** (shifts all). For O(1) both ends use
  `collections.deque` (= Java ArrayDeque) — but `deque[i]` in the middle is O(n).
- **Membership:** `x in coll` — set/dict O(1), list O(n). No try/catch needed.
- **Find index:** `l.index(x)` **raises `ValueError`** if absent (not -1). Use `l.index(x) if x in l else -1`;
  for dicts, `d.get(k, -1)` (= `getOrDefault`). try/except is the "EAFP" alternative.
- `{}` is an empty **dict**; empty **set** is `set()`. Slicing `l[start:stop:step]`; negative index `l[-1]`.

### Day 1 — Lesson 3: Control Flow
- No braces — **indentation defines blocks**; `:` opens them. `elif` (not "else if"). Booleans are words: `and`/`or`/`not`.
- Iterate items directly: `for x in coll:`. `enumerate(coll)` → index+value. `zip(a,b)` walks in parallel and
  **stops at the shortest** (extra elements silently dropped).
- `range(stop)` / `range(start, stop, step)`. Truthiness: empty list/dict/str, `0`, `None` are falsy → `if not x:`.
- **Ternary is an expression** → both branches must be VALUES: `label = "big" if a>10 else "small"`
  (don't put a `print()`/side-effect in a branch; assign/use the result instead).

### Day 1 — Lesson 4: Functions
- `def name(params):` + indented body; no `return` → returns `None`. **Multiple return = a tuple** → unpack it.
- Defaults `def f(a, b=5):`; keyword args call by name (order-free). `*args`→**tuple**, `**kwargs`→**dict**;
  `*list` / `**dict` unpack at the **call site**.
- Type hints `def add(a: int, b: int) -> int:` (FastAPI/Pydantic use these later).
- ⚠️ **Mutable default footgun:** `def f(x=[])` reuses ONE list across calls → use `x=None; if x is None: x=[]`.
- ⚠️ **Don't shadow built-ins** (`max min list dict sum id type`) with variable names → later `TypeError: 'int' object is not callable`.
- 📛 **PEP 8:** `snake_case` funcs/vars · `PascalCase` classes · `UPPER_CASE` constants (not Java/JS camelCase).

### Day 1 — Lesson 5: Comprehensions + Generators  ✅ Phase 1 complete
- Shape: `[EXPRESSION for ITEM in ITERABLE if CONDITION]` = map+filter in one line.
  `{k: v for ...}` dict comp · `{x for ...}` set comp.
- **Generator `(...)` = LAZY:** computes one value at a time, **O(1) memory**, **single-pass** (consumed once),
  no index / no `len()`. `[...]` is **eager** (whole list, O(n), reusable). Use a generator for stream-once
  (`sum`/`max`/`any`/`for`) and huge/infinite data.
- ⚠️ `print(gen)` shows `<generator object ...>` (its repr), **not** the values — a generator isn't a container.
  Consume it to see values: `list(gen)` / loop / `sum(gen)`. After one full pass it's empty.
- See it: `sys.getsizeof([...])` grows with n; `sys.getsizeof((...))` stays ~constant.

### Day 2 — Lesson 6: Classes & OOP
- `class Name:`; `__init__(self, ...)` = constructor; `self` = Java's `this` but **explicit** (first param of every method). **No `new`** — just `Task("x")`.
- ⚠️ **No method overloading** — two `def __init__` → the 2nd silently **overwrites** the 1st. For an "optional" arg use ONE constructor with a **default** (`done=False`), not two constructors.
- 🪤 `(name, bases, dict)` is the **metaclass** signature — not a normal constructor. Model real fields (`title`, `done`).
- Dunders: `__init__` construct · `__str__` print/str (like `toString`) · `__repr__` debug (lists/REPL) · `__eq__` (`==`) · `__len__`.
- `!r` in an f-string calls `repr()` on the value (adds quotes to strings). Constructors shouldn't `print()` (side-effect).

### Day 2 — Lesson 7: Inheritance · @property · @dataclass  ✅ Phase 2 complete
- Inheritance: `class Dog(Animal):`; call parent via `super().__init__(...)`; override = just redefine (no `@Override`).
- No `private` keyword: `_x` = "internal" by convention, `__x` = **name-mangled** (stronger hide).
- `@property` = getter accessed like a **field** (no `()`); `@x.setter` for writes + validation.
  ⚠️ The constructor must assign **through** the property (`self.radius = r`) so setter validation runs on construction too —
  assigning `self.__radius` directly **bypasses** it.
- `@dataclass` auto-generates **`__init__`, `__repr__`, `__eq__`** from annotated fields (NOT `__str__` — print falls back to `__repr__`).
  This is the foundation Pydantic (FastAPI models) builds on.

### Day 2 — Lesson 8: FastAPI — first API
- `@app.get("/")` = route + method (= Nest `@Get()`); return a dict/str → auto-JSON. Run `uvicorn main:app --reload`.
- Path param: `@app.get("/tasks/{task_id}")` + `def f(task_id: int)` — name in path MUST match the param; `int`
  auto-parses & validates (bad type → **422**). Query param = a default-valued arg `name: str = "world"` → `/hello?name=x`.
- **Free interactive docs at `/docs`** (Swagger) + `/openapi.json` — generated from type hints, zero config.
- ⚠️ **JS→Python trap:** dict keys are **evaluated expressions**, not implicit strings. `{task: {id: x}}` →
  `NameError: name 'task' is not defined`. Quote them: `{"task": {"id": x}}`.

### Day 2 — Lesson 9: Pydantic models + POST
- `class TaskIn(BaseModel)` = request DTO; validation is baked into the **type** (no `@IsString()` needed) → missing/bad field = **422**.
- FastAPI routing rule: a **simple-typed** param (int/str) = path/query; a **BaseModel** param = the JSON **body** (no `@Body()` decorator).
- Design/REST notes: **server assigns `id`** (don't accept it from the client); use `POST /tasks` not `POST /create`
  (the HTTP verb already means "create"); for not-found `raise HTTPException(status_code=404)`, not a 200 + message.

### Day 2 — Lesson 10: CRUD · status codes · HTTPException · PATCH
- Full CRUD: `@app.post(..., status_code=201)`, `@app.put` (full replace), `@app.patch` (partial), `@app.delete(..., status_code=204)`.
- Errors: `raise HTTPException(status_code=404, detail=...)` — **first arg is the int status**. Not-found → 404, never 200+message.
- **PATCH partial-update recipe:** a DTO with all-optional fields + `body.model_dump(exclude_unset=True)` (apply only the keys the
  client actually sent). PUT replaces the whole resource; PATCH touches only what changed.
- ⚠️ Pydantic gotcha: `x: T | None` = **required** but nullable; `x: T | None = None` = **optional** (omittable). PATCH needs `= None`.
- The field name must be **consistent** across create DTO + patch DTO + stored record, else `.update()` adds a stray key.
