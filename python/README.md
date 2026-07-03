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
