class Task:
    def __init__(self, title, done=False):   # ONE constructor, meaningful fields
        self.title = title
        self.done = done

    def complete(self):                       # sets the flag — no param needed
        self.done = True

    def __str__(self):                        # human view (print)
        box = "x" if self.done else " "
        return f"[{box}] {self.title}"

    def __repr__(self):                       # debug view (lists/REPL)
        return f"Task(title={self.title!r}, done={self.done})"


t1 = Task("Buy milk")
t2 = Task("Write code")
t1.complete()
print(t1)          # [x] Buy milk
print(t2)          # [ ] Write code
print([t1, t2])    # uses __repr__ for each
