# Kafka — From Zero (a real lesson)

Welcome. You said you don't understand even the first sentence of most Kafka tutorials. That is fine. This lesson assumes you know **nothing** about Kafka and nothing about "distributed systems." If you can write a small backend program in **Node** (a JavaScript runtime — the thing that runs JavaScript outside a web browser) or **NestJS** (a Node.js backend framework — a toolkit for building server programs in Node), you have enough to follow along.

We will use **ONE story the whole way through**: a **busy restaurant kitchen with an order-ticket rail**. Every Kafka idea maps onto something in that kitchen. We will introduce **one idea at a time**, and each new idea builds on the one before it. Every technical word is defined in plain English **the first time it appears**.

(One quick note: the word **service** appears just above. A *service* simply means a running program that does one job — it is fully defined in Section 1, right below.)

Take your time. Read top to bottom. Do not skip ahead.

A word on the diagrams: every picture below is drawn with plain text and only lines up correctly in a **monospace font** (one where every letter is the same width, like code). If the art ever looks crooked, view it in a code block / monospace view. Each of the trickier diagrams has a small **legend** explaining its arrows and symbols.

---

## 0. The setup for our analogy (read this first)

Picture a restaurant.

- **Waiters** take orders from customers and write them on paper tickets.
- There is a long metal **rail** (a rod) above the kitchen where tickets get clipped, left to right, in the order they arrive.
- **Cooks** take tickets off the rail and cook the food.

```
   WAITERS                 THE RAIL                      COOKS
  (write tickets)   (tickets clipped left->right)   (read & cook)

   [Anna] --\                                        /-- [Cook 1]
   [Ben]  ---->  | T1 | T2 | T3 | T4 | T5 | ...  --->  [Cook 2]
   [Cara] --/                                        \-- [Cook 3]

   Legend: --> means "writes onto / reads from the rail".
           T1..T5 are tickets, clipped in arrival order, left to right.
```

*(This is a loose first picture. Exactly HOW the cooks divide the work among themselves comes much later, in Section 12. For now, do not read anything into "three cooks on one rail" — just picture **writers** and **readers** separated by **the rail** in the middle.)*

Hold this picture in your head. We will keep adding to it.

---

## 1. The PROBLEM Kafka solves

**Plain definition:** Kafka is a system that lets different programs hand data to each other **reliably**, without being **tightly wired together**, and **without losing the data**.

Let me unpack the three scary parts of that sentence, because they ARE the whole point.

**Term: service.** A *service* is just a running program that does one job. Example: an "Orders service" that handles new orders, a "Billing service" that charges cards, an "Email service" that sends receipts. (When the definition above said "different programs," it meant different services.)

**Term: tightly coupled / tightly wired.** Two services are *tightly coupled* when one calls the other **directly** and must wait for an answer. If the called service is down, the caller breaks too.

Here is the painful "before Kafka" world. The Orders service calls Billing, Email, and Analytics directly:

```
   BEFORE KAFKA (direct calls — fragile)

                 +----> [Billing]   (if down -> Orders breaks)
                 |
   [Orders] -----+----> [Email]     (if slow  -> Orders waits)
                 |
                 +----> [Analytics] (add a 4th? edit Orders code again)

   Legend: ----> means "calls directly and waits for a reply".
```

Problems with this:
1. If Billing is down, the Orders service errors out. **Tightly coupled.**
2. If Email is slow, Orders has to **wait** for it.
3. To add a new listener (say, Analytics), you must **edit and redeploy** the Orders service. They all know about each other. Messy.
4. If a service is down when an event happens, that event is **lost forever**.

**The Kafka idea:** Put a **shared, durable middle-man** between them. Orders just writes "an order happened" into the middle-man and moves on. Anyone who cares reads it later, at their own speed. If a reader was down, the data is still there waiting.

**Term: durable.** *Durable* means the data is written to disk and **survives** — it does not vanish if a program crashes or restarts.

```
   WITH KAFKA (decoupled — robust)

   [Orders] --writes--> [ KAFKA (durable middle-man) ] --read by--> [Billing]
                                                       --read by--> [Email]
                                                       --read by--> [Analytics]

   Orders does NOT know or care who reads. Readers come and go freely.

   Legend: --writes--> = puts data in;  --read by--> = others take data out.
```

**In our analogy:** Kafka is **the rail**. Waiters (writers) clip tickets on it and walk away. Cooks (readers) take tickets when they're ready. The waiter never waits for the cook. A new cook can start any time and read tickets. That's the whole spirit of Kafka.

**Why it matters:** This is the #1 reason Kafka exists. It lets services communicate **without** waiting on each other and **without** losing data, even when some of them are temporarily down.

---

## 2. Message / Event

**Plain definition:** A *message* (also called an *event*) is one single piece of data you put into Kafka — like one record of "something that happened."

In our kitchen, **a message is one order ticket**.

A message is usually a small chunk of data — at the lowest level, computers store and send everything as **bytes** (the tiny units that all data is made of), so you'll often hear a message called "a small bundle of bytes." In practice it's often **JSON** (JavaScript Object Notation — a common, human-readable text format for data, the curly-brace `{ ... }` style shown below). It has two main parts:
- A **key** (optional) — a label used for grouping (we'll use it later; ignore for now).
- A **value** — the actual content.

```
   ONE MESSAGE (one ticket)

   +-------------------------------------+
   | key:   "order-42"                   |
   | value: { "orderId": 42,             |
   |          "item": "Pizza",           |
   |          "amount": 19.99 }          |
   +-------------------------------------+
```

The words **message** and **event** mean the same thing in Kafka. "Event" emphasizes that it describes *something that happened* ("OrderPlaced"). We'll use both.

**Why it matters:** The message is the **unit** Kafka moves around. Everything else (topics, partitions, offsets) is about **organizing and tracking** these messages.

---

## 3. Topic

**Plain definition:** A *topic* is a **named category** of messages. Producers write messages into a topic; consumers read from a topic. (Producer and consumer are defined in Sections 10 and 11 — for now, "writer" and "reader.")

Think of a topic as a **named rail**. A real kitchen has more than one rail: one rail for **food orders**, another rail for **drink orders**, another for **dessert orders**.

```
   TOPICS = NAMED RAILS

   topic "orders":     | T1 | T2 | T3 | T4 | ...
   topic "payments":   | P1 | P2 | P3 | ...
   topic "emails":     | E1 | E2 | ...
```

You give a topic a name like `orders` or `user-signups`. All messages about orders go on the `orders` topic. A service that cares about orders reads the `orders` topic and ignores the rest.

**Why it matters:** Topics keep different kinds of data **separated and labeled**, so each consumer reads only what it cares about.

---

## 4. Partition

This is the first idea that trips people up. Go slow.

**Plain definition:** A *partition* is a **slice** of a topic. A topic is split into one or more partitions, and **each partition is its own independent ordered line of messages**.

Why split a topic at all? Because one single rail can only be filled and emptied so fast. If your restaurant gets thousands of orders, one rail becomes a bottleneck. So you put up **several parallel rails** for the *same* category, and split the incoming tickets across them. More rails = more cooks can work at once = more throughput.

**Term: throughput.** *Throughput* means how much work you can do per second (e.g., messages handled per second). More parallel rails = higher throughput.

```
   TOPIC "orders" SPLIT INTO 3 PARTITIONS

   orders / partition 0:   | A1 | A2 | A3 | A4 | ...
   orders / partition 1:   | B1 | B2 | B3 | ...
   orders / partition 2:   | C1 | C2 | C3 | C4 | C5 | ...

   (3 parallel rails, all belonging to the same topic "orders")
```

**Key rule about order:** Kafka keeps messages **in order INSIDE a single partition**, but does **NOT** guarantee order **across** partitions. Partition 0 is its own neat left-to-right line. Partition 1 is its own neat line. But message B1 on partition 1 and A1 on partition 0 have **no defined order between them**.

**How does Kafka decide which partition a message goes to?** It uses the message **key**. Kafka runs the key through a **hash function** — a small, fixed formula that turns any input (like the text `"customer-42"`) into a number, always giving the **same** number for the **same** input. It then uses that number to pick a partition. Because the formula is consistent, **all messages with the same key always land on the same partition**. So if you key orders by `customerId` (the customer's identification number), every order from customer 42 lands on the same partition — and therefore stays **in order relative to each other**.

**What about messages with NO key?** They get **spread across the partitions** so no single rail is overloaded. (How modern Kafka spreads them is described just below — it is *not* strictly "one to each rail in turn.")

```
   key "customer-42" --hash--> always the SAME partition (say, 1)
   key "customer-99" --hash--> always the SAME partition (say, 0)

   -> All of customer 42's events stay in order, because they share a rail.

   Legend: --hash--> = "run the key through the hash formula to pick a rail".
```

> **Note on the specific numbers:** Do NOT look for a pattern in "42 -> rail 1" and "99 -> rail 0." The exact rail for a key is whatever the hash formula produces; the numbers here are just illustrative examples, not a rule you can predict by eye.

> **Two terms you'll hear for the no-key case:**
> - **round-robin** means handing things out **one at a time, in rotation**: rail 0, then rail 1, then rail 2, then back to rail 0, and so on — like dealing cards evenly around a table. This was Kafka's *older* default for keyless messages.
> - **sticky partitioner** is the *modern* default (since Kafka version 2.4, released 2019). Instead of switching rails on every single message, it fills up one rail's batch, then switches to another rail for the next batch. Over time the messages still end up **evenly spread**, but in efficient bursts rather than strict one-by-one rotation.
>
> Either way, the practical takeaway is the same: **keyless messages get spread roughly evenly across all partitions.** In the kitchen: a keyless ticket just goes onto whichever rail the distributor is currently filling, and the rails fill up evenly over time.

**Why it matters:** Partitions are **how Kafka scales** (parallel rails) **and** how it gives you **ordering where you need it** (same key = same partition = ordered).

---

## 5. Offset

**Plain definition:** An *offset* is the **position number** of a message **within its partition**. It's a counter — 0, 1, 2, 3, … — that always **goes up** and never **repeats** inside that partition.

Think of each ticket on a rail having a **printed slot number**: slot 0, slot 1, slot 2… The numbers count up and never go backward.

```
   orders / partition 0

   offset:    0     1     2     3     4
            +-----+-----+-----+-----+-----+
            | A1  | A2  | A3  | A4  | A5  |
            +-----+-----+-----+-----+-----+
                                        ^
                              next message goes to offset 5 (usually)
```

Important details:
- Offsets are **per-partition**. Partition 0 has offset 0, AND partition 1 also has its own offset 0. They are unrelated.
- An offset, once assigned, **never changes**. The message at offset 3 is *always* the message at offset 3.
- To point at one exact message anywhere in Kafka, you need three things: **topic name + partition number + offset**.

> **A small honesty note (safe to skim now, useful later):** offsets always **increase** and never repeat, but they are **not guaranteed to be perfectly gap-free**. A few advanced Kafka features (log compaction, and special internal "transaction" markers) can leave small **gaps** in the numbering. So the message after offset 4 is *usually* offset 5, but occasionally the next readable one might be offset 7. The safe mental model: treat offsets as **always-increasing ID numbers**, not as a guaranteed dense 0,1,2,3 sequence.

**Why it matters:** The offset is the **address** of each message. It's also the basis of the **bookmark** — a saved place-marker that lets a consumer remember "how far have I read?" The bookmark is fully defined in Section 13; whenever you see "bookmark" before then, picture a saved place-marker.

---

## 6. The append-only "log"

**Plain definition:** A *log* (in Kafka's sense) is a file you can **only add to the end of** — never insert in the middle, never edit, never delete-in-place. New messages are **appended** (added at the end). This is called **append-only**.

(Note: "log" here does NOT mean console/error logs. If you've used `console.log` in JavaScript, this is **NOT** that. It means an *ordered, append-only record*, like a ship's logbook where you only ever write the next line at the bottom — that logbook image is the one to hold onto.)

Each partition IS a log. That's literally what a partition is under the hood: an append-only file on disk.

```
   A PARTITION IS AN APPEND-ONLY LOG

   write direction --->
   +-----+-----+-----+-----+-----+ - - - >
   |  0  |  1  |  2  |  3  |  4  |   (new ones added here, at the end)
   +-----+-----+-----+-----+-----+
   ^
   oldest message (never moves, never edited)

   Reading = start at some offset, walk to the right.
   Writing = always at the far right end.
```

Why is append-only such a big deal?
- **It's fast.** Always writing to the end of a file is one of the fastest things a disk can do.
- **It's a source of truth.** Nothing rewrites history. You can replay it. If a new consumer shows up tomorrow, it can read from offset 0 and re-process **everything that ever happened**.
- **Many readers, no conflict.** Because nobody edits or deletes, lots of consumers can read the same log at the same time without stepping on each other.

**Term: retention.** *Retention* is the setting for **how long Kafka keeps messages** before old ones are cleaned up (for example, "keep 7 days"). Within that window, the log is your replayable history.

**In our analogy:** The rail is a one-way conveyor. Tickets get clipped on the **right end** in arrival order. Nobody slides a ticket into the middle. Nobody rewrites a ticket. Crucially, **cooks read tickets but leave them clipped on the rail** — reading a ticket does **not** remove it. Tickets are only taken down **by time** (retention, e.g., after 7 days), never by being read. (We rely on this "reading doesn't remove" rule again in Section 11.)

**Why it matters:** The append-only log is the **core mechanic** of Kafka. Topics, partitions, offsets, replay, multiple independent readers — they all come from this one simple "only add to the end" idea.

---

## 7. Broker

**Plain definition:** A *broker* is **one Kafka server** — one running Kafka process, usually on its own machine. It stores partitions on its disk and serves reads and writes for them.

Up to now "Kafka" was a magic box. Really, Kafka runs as one or more **servers**, and each server is called a broker.

In our analogy, a **broker is one wall of rails** with a **clerk** standing at it. The clerk accepts new tickets (writes), hands tickets to cooks (reads), and physically holds those rails. **One clerk per wall**, and a wall holds many rails — remember this; we keep the "clerk = a broker" mapping consistent later.

```
   ONE BROKER (one Kafka server)

   +-------------------- Broker 1 --------------------+
   |  holds these partitions on its disk:             |
   |                                                  |
   |   orders / partition 0:   | 0 | 1 | 2 | 3 | ...  |
   |   payments / partition 2: | 0 | 1 | 2 | ...      |
   +--------------------------------------------------+
```

A broker can hold **many partitions** from **many topics**. One physical server, lots of rails on it.

**Why it matters:** The broker is the **actual machine** that does the storing and serving. When people say "my Kafka has 3 brokers," they mean three servers sharing the load.

---

## 8. Cluster

**Plain definition:** A *cluster* is a **group of brokers working together** as one system. To your application, the cluster looks like a single Kafka, even though it's many servers.

In the analogy, a **cluster is the whole kitchen's wall of rails**, made of several broker-walls side by side. Tickets and rails are **spread across** the walls so no single wall is overloaded.

> **Shorthand used from here on:** `orders-0` means "the **orders** topic, **partition 0**." `pay-2` means "the **payments** topic (`pay` is just short for `payments`), **partition 2**." Earlier we wrote this as "orders / partition 0"; from now on the compact `topic-N` form is the same thing.

```
   A CLUSTER = SEVERAL BROKERS TOGETHER

   +----------+   +----------+   +----------+
   | Broker 1 |   | Broker 2 |   | Broker 3 |
   |          |   |          |   |          |
   | orders-0 |   | orders-1 |   | orders-2 |
   | pay-2    |   | pay-0    |   | pay-1    |
   +----------+   +----------+   +----------+
        \______________|______________/
                       |
              your app talks to the cluster
              as if it were ONE Kafka

   Legend: orders-0 = topic "orders", partition 0.
           pay-2    = topic "payments", partition 2.
```

Spreading partitions across brokers is how Kafka shares the work **and** survives a server dying (next section).

**Why it matters:** A cluster gives you **scale** (more brokers = more capacity) and is the foundation for **fault tolerance** — surviving the loss of a machine.

---

## 9. Replication: replica, leader, follower, ISR

Now the most important reliability idea. Read slowly; we define several terms.

**The problem:** A broker is a machine. Machines die — disk fails, power cut, crash. If `orders-0` lived on **only** Broker 1 and Broker 1 dies, those messages are **gone**. Not acceptable. We promised "doesn't lose data."

**The fix:** Keep **copies** of each partition on **multiple brokers**. This copying is called **replication**.

**Term: replica.** A *replica* is **one copy of a partition**. If a partition has 3 replicas, the same data lives on 3 different brokers.

**Term: replication factor.** The *replication factor* is simply **how many copies** you keep — here, 3. It's a setting you choose per topic.

But copies raise a question: if there are 3 identical copies, **who is in charge** of accepting new writes? You can't let all 3 accept writes independently — they'd drift apart. So Kafka picks one boss per partition.

**Term: leader.** The *leader* is the **one replica that's in charge** of a partition. **All writes go to the leader, and reads go to the leader by default.** There is exactly one leader per partition at a time. (Since Kafka version 2.4, consumers *can* be specially configured to read from a nearby follower copy instead — handy across distant data centers — but unless someone sets that up, assume reads go to the leader.)

**Term: follower.** A *follower* is a replica that is **NOT the leader**. Its only job is to **copy** the leader — to constantly fetch new messages from the leader and stay up to date. Followers don't normally serve clients; they're standbys.

```
   PARTITION "orders-0" WITH 3 REPLICAS (replication factor = 3)

   +-----------+        +-----------+        +-----------+
   | Broker 1  |        | Broker 2  |        | Broker 3  |
   | orders-0  |        | orders-0  |        | orders-0  |
   | LEADER    | -copy->| follower  |        | follower  |
   | (writes   | -copy->| (copies   |        | (copies   |
   |  go here) |        |  leader)  |        |  leader)  |
   +-----------+        +-----------+        +-----------+

   Producers/consumers talk to the LEADER (by default).
   Followers silently keep copies in case the leader dies.

   Legend: -copy-> = follower continuously fetches new messages from leader.
```

**What happens when the leader dies?** Kafka has an **internal coordinator** (a built-in supervisor that keeps track of every broker and partition). It **detects** that the leader's broker has gone silent and **elects a new leader** from the surviving copies — automatically, with no human involved. Writes and reads continue on the new leader. The dead broker can come back later as a follower. This automatic swap is why a machine can die and you don't lose availability.

But there's a catch: what if a follower is **slow** or **stuck** and hasn't copied the latest messages? Promoting *that* follower would lose the newest data. So Kafka tracks which followers are **caught up**.

**Term: ISR — in-sync replica.** An *in-sync replica (ISR)* is a replica that is **caught up** with the leader. "Caught up" doesn't mean byte-for-byte identical at every instant — it means the follower is **within an allowed time-lag** of the leader. That allowed lag is a **configurable threshold** (Kafka's `replica.lag.time.max.ms` setting) chosen by whoever administers Kafka — so "in-sync" is a precise, tunable rule, not a vague feeling. The ISR is the set of replicas that are **safe to promote** to leader.

```
   ISR = the replicas that are caught up (within the allowed time-lag)

   leader   (Broker 1): has offsets 0..100
   follower (Broker 2): has offsets 0..100  <-- caught up -> IN the ISR
   follower (Broker 3): has offsets 0..70   <-- too far behind -> NOT in ISR

   If the leader dies, Kafka promotes from the ISR (Broker 2),
   so no *acknowledged* data is lost. (More on "acknowledged" in Section 15.)
```

A subtle but honest point: even an in-sync follower can be a *tiny* bit behind on the very newest, not-yet-confirmed messages. On a failover, those last unconfirmed records can be dropped. The promise is specifically that **confirmed/acknowledged** data (under the safety settings in Section 15) survives — not that the new leader has every last byte the old leader briefly held.

**In our analogy:** Each rail's data is also photocopied onto **other walls (other brokers)**. The wall whose copy is the live, write-accepting one is the **leader** for that rail; the matching rails on the **other walls** are **followers** that continuously photocopy the leader. A follower wall that's **fully up to date** (within the allowed lag) is **in-sync (ISR)**. If the leader wall's clerk faints, the coordinator promotes an up-to-date follower wall — never one that's far behind on its photocopies. (Note: it's the **wall/broker** that holds each copy — we're not adding extra clerks per rail.)

**Why it matters:** Replication + leader/follower + ISR is **how Kafka keeps your data safe** when machines fail. The ISR concept comes back in Section 15 (`acks=all`), so remember: *ISR = the caught-up copies that are safe to trust.*

---

## 10. Producer

**Plain definition:** A *producer* is any program that **writes** (sends) messages **into** a topic.

In our analogy, the producer is the **waiter who clips a ticket onto the rail**.

```
   PRODUCER WRITES TO A TOPIC

   [Producer] --"OrderPlaced {id:42}"--> [ leader of the chosen partition ]

   Producer decides the partition via the message KEY:
     key "customer-42" --hash--> partition 1 (always the same one)
     no key            --------> spread across partitions (evenly over time)
```

A producer's job in steps:
1. Build a message (key + value).
2. Pick the target partition (hash the key; or spread it out if there's no key — see Section 4).
3. Send it to the **leader** of that partition (remember: writes go to the leader).
4. Wait for an acknowledgement — how long it waits is the **acks** setting (Section 15).

In Node/NestJS terms, your Orders service acts as a producer:
`producer.send({ topic: 'orders', messages: [{ key: 'customer-42', value: JSON.stringify(order) }] })`.

**Why it matters:** The producer is the **entry point** of data into Kafka. How it sets keys (ordering) and `acks` (safety) directly controls correctness.

---

## 11. Consumer

**Plain definition:** A *consumer* is any program that **reads** messages **from** a topic and does something with them.

In our analogy, the consumer is the **cook who takes a ticket off the rail and cooks it**.

```
   CONSUMER READS FROM A TOPIC

   [ orders-0 ] --reads offset 0,1,2,...--> [Consumer]  -> does work
                                                          (charge card,
                                                           send email, etc.)
```

A consumer reads in a loop: "give me the next messages from where I left off," processes them, then asks for more. It moves **forward through the offsets**, left to right along the rail.

> **Important — Kafka does NOT push to you; you PULL from Kafka.** Many beginners assume Kafka shoves messages at the consumer. It doesn't. The **consumer asks** Kafka for the next batch of messages (this is called the **pull** or **poll** model — "poll" just means "repeatedly ask"). Kafka waits to be asked.

Crucially, a consumer **does not delete** the message when it reads it (remember the append-only log, Section 6 — reading a ticket leaves it clipped on the rail). It just moves its own pointer forward. Other consumers can still read the same messages independently.

**Why it matters:** The consumer is the **exit point** — where Kafka data turns into real actions in your system.

---

## 12. Consumer group + how partitions are split

This is how Kafka does **parallel reading**. Define one term first.

**Term: consumer group.** A *consumer group* is a **team of consumers that share a name** and **split the work** of reading between them. Each partition is handed to **exactly one** consumer in the group.

(A consumer group can subscribe to **one or more topics**. Whatever it subscribes to, **all** the partitions across **all** those topics get divided among the group's members. To keep things simple, the diagrams below show a single topic, but the same one-partition-to-one-consumer rule applies across multiple topics too.)

Picture a team of cooks who agree: "We're the *kitchen-team*. Let's not both grab the same ticket. Divide the rails between us."

**The core rule:** Within one consumer group, **each partition is read by exactly one consumer**. Kafka divides the partitions among the group members.

```
   TOPIC "orders" HAS 3 PARTITIONS.
   CONSUMER GROUP "billing-team" HAS 3 CONSUMERS.

   orders-0 ---> Consumer A  \
   orders-1 ---> Consumer B   } each partition -> exactly ONE consumer
   orders-2 ---> Consumer C  /

   Perfect spread: 3 partitions, 3 consumers, 1 each. Max parallelism.
```

What if you have **fewer** consumers than partitions? Some consumers handle more than one partition:

```
   3 partitions, 2 consumers:

   orders-0 ---> Consumer A
   orders-1 ---> Consumer A   (A handles two rails)
   orders-2 ---> Consumer B
```

What if you have **more** consumers than partitions? The extras sit **idle** — there's nothing left to give them. **Partition count is the cap on parallelism within a group.**

```
   3 partitions, 4 consumers:

   orders-0 ---> Consumer A
   orders-1 ---> Consumer B
   orders-2 ---> Consumer C
                 Consumer D  <-- IDLE (no partition left)
```

**Now the other half: multiple groups read the SAME topic independently.** Each group gets its **own full copy** of every message and its **own bookmark** (its own saved place-marker — formally the *committed offset*, defined in Section 13). This is the magic from Section 1 — many independent readers.

```
   TWO GROUPS READING THE SAME "orders" TOPIC

                            +--> group "billing"   (charges cards)
   orders (all partitions) -+
                            +--> group "emailing"  (sends receipts)

   Both groups see EVERY order. They don't compete; they don't share a
   bookmark (each keeps its own saved place-marker; see Section 13).
   Inside each group, partitions are still split 1-per-consumer.
```

So there are **two levels**:
- **Across groups:** every group sees all messages (broadcast). Billing AND Emailing both get every order.
- **Inside a group:** partitions are divided so each message is handled **once** by the group (work-sharing).

**Why it matters:** Consumer groups give you **both** patterns at once: scale a single job across many workers (inside a group) **and** fan the same data out to many different jobs (across groups). To scale a slow job, you add consumers to its group — up to the partition count.

---

## 13. Committed offset (the bookmark)

**Plain definition:** A *committed offset* is a saved marker that records **"my consumer group has successfully processed up to here in this partition."** It is the **bookmark** that lets a consumer resume in the right place after a restart.

**Term: commit.** To *commit* an offset means to **save that bookmark** back into Kafka.

A consumer reads message after message. Periodically it tells Kafka, "I'm done through offset 4 on orders-0." Kafka stores that. If the consumer restarts, it asks "where was I?" and resumes at offset 5 — not from 0, and not skipping ahead.

```
   COMMITTED OFFSET = BOOKMARK PER (group, partition)

   orders-0:   | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | ...
                             ^               ^
                  committed offset = 4       current read position = 7
                  (group "billing" has
                   safely processed 0..4)

   On restart, group "billing" resumes at offset 5.
```

Two subtle but vital points:

1. **The bookmark belongs to the consumer GROUP, not one consumer.** If Consumer A dies and Consumer B takes over orders-0, B reads the group's committed offset and continues from there. On takeover, B will **re-process everything from the last committed offset up to wherever A actually got to** — that can be **several messages**, not just one, depending on how often the group commits. (This re-processing is exactly why we make work safe to repeat — see Sections 16–17.)

2. **WHEN you commit decides your safety.** Commit **after** you've truly done the work, not before. If you commit first and then crash before doing the work, the bookmark says "done" but the work never happened — the message is effectively **lost**. We'll make this concrete in Sections 16–17.

**In our analogy:** The kitchen-team keeps a sticky note on each rail: "cooked through ticket #4." Any cook on the team reads the sticky note to know where to continue. They update the note **after** the dish is actually plated — not before.

**Why it matters:** The committed offset is **how consumers survive restarts** without re-doing everything or skipping work. Getting the **timing** of the commit right is the difference between losing messages and processing them safely.

---

## 14. Rebalance

**Plain definition:** A *rebalance* is when Kafka **re-divides the partitions** among the consumers in a group, because the membership of the group changed.

First, how does Kafka know a consumer is still alive? Each consumer periodically sends a **heartbeat** — a tiny "I'm alive" signal — to Kafka. If Kafka **stops receiving** heartbeats from a consumer for a set timeout, it assumes that consumer is **dead** and triggers a rebalance.

When does the group's membership change?
- A new consumer **joins** the group (you scaled up).
- A consumer **leaves** or **crashes** (you scaled down, or it died — detected by missing heartbeats).
- The topic **gains partitions**.

When that happens, the old "who reads which partition" assignment is no longer valid, so Kafka recomputes it.

```
   BEFORE (2 consumers):           AFTER C joins -> REBALANCE (3 consumers):

   orders-0 -> A                   orders-0 -> A
   orders-1 -> A                   orders-1 -> B
   orders-2 -> B                   orders-2 -> C

   The partitions get re-dealt like a fresh hand of cards.
```

```
   IF A CONSUMER CRASHES (heartbeats stop):

   Before:  orders-0 -> A,  orders-1 -> B
   A dies -> REBALANCE
   After:   orders-0 -> B,  orders-1 -> B   (B picks up A's partition)
```

Two things to know:
- A rebalance can cause a brief **pause** in reading while partitions are reassigned. In the **older** style (called *eager* rebalancing), **all** consumers in the group stop briefly — a "stop-the-world" pause. **Modern** Kafka clients default toward **cooperative (incremental) rebalancing**, where consumers **keep** the partitions they already hold and only the few partitions actually being moved are handed over — so most consumers keep working. Either way, frequent rebalances hurt throughput, so they're worth avoiding.
- Because the new owner of a partition resumes from the **committed offset** (Section 13), it continues cleanly from where the group left off.

**In our analogy:** A cook clocks in or clocks out, so the team **re-divides the rails**. In the old style everyone pauses while they re-sort all the rails; in the modern style only the rail that's actually changing hands gets paused. Then work resumes, each cook starting from the rail's sticky-note bookmark.

**Why it matters:** Rebalancing is **how a consumer group heals and scales automatically** when workers come and go — including recovering after a crash. It's also a thing to **watch**, because too-frequent rebalances make a system stall.

---

## 15. acks (0 / 1 / all) — how safe is a write?

**Term: ack.** *Ack* is short for **acknowledgement** — Kafka saying "got it, I've stored your message." `acks` is a **producer setting** that controls **how much confirmation the producer waits for** before it considers a write successful. It's the dial between **speed** and **safety**.

Recall from Section 9: writes go to the **leader**, and **followers** copy the leader; the caught-up ones form the **ISR (in-sync replicas)**. The `acks` setting decides how many of those must confirm.

There are three settings.

**`acks=0` — "fire and forget."** The producer sends and **does not wait** for any confirmation. Fastest. Least safe. If the message never arrives, the producer never knows.

```
   acks=0

   [Producer] --send--> [Leader]
        |
        +-- immediately considers it "done" (no waiting, no proof)

   Risk: leader was down? message dropped? Producer has NO idea. Data can vanish.
```

**`acks=1` — "leader got it."** The producer waits until the **leader** has written the message, and the leader sends back a confirmation. Medium speed, medium safety. But if the leader dies **before** any follower copied that message, it's lost.

```
   acks=1

   [Producer] --send--> [Leader]
   [Producer] <--"I wrote it"-- [Leader]   (now producer is done)
                           |
                           (followers may NOT have copied it yet)

   Risk: leader crashes before followers copy -> that message is lost.
```

**`acks=all` (also written `acks=-1` — the value `-1` is just Kafka's shorthand for "all") — "leader AND all in-sync replicas got it."** The producer waits until the leader **and every replica currently in the ISR** have the message and have confirmed back. Slowest. Safest.

```
   acks=all

   [Producer] --send--> [Leader]
                          | -copy->  [ISR follower 1]
                          | -copy->  [ISR follower 2]
                          |
   [ISR follower 1] --ack--> [Leader]      (each ISR copy confirms back)
   [ISR follower 2] --ack--> [Leader]
   [Leader] --"done (all ISR have it)"--> [Producer]

   Legend: -copy-> leader pushes the message to followers.
           --ack-> each follower confirms it stored the copy.
           Only after ALL current ISR confirm does the leader tell the producer.
```

> **The crucial fine print most tutorials skip — `min.insync.replicas`.**
> `acks=all` waits for everyone **currently** in the ISR — but the ISR is a **living set that can shrink**. If followers fall behind and drop out, the ISR can shrink all the way down to **just the leader**. In that situation, `acks=all` would be "satisfied" by the leader alone — quietly giving you no better safety than `acks=1`!
>
> The real guard is a **broker/topic setting called `min.insync.replicas`** (commonly set to **2**): the minimum number of in-sync copies that must exist for a write to be accepted. If the ISR shrinks **below** that number, the leader **rejects** the write (it returns an error rather than a false "success"). So the genuine durability guarantee is **`acks=all` AND `min.insync.replicas` ≥ 2** *together*. With just `acks=all` and no minimum set, "nothing is lost" is not actually guaranteed.

```
   QUICK COMPARISON

   setting    waits for...                  speed    safety
   --------   ---------------------------   ------   ------------------------
   acks=0     nothing                       fastest  can lose data
   acks=1     leader only                   medium   loses if leader dies early
   acks=all   leader + all current ISR      slowest  strongest — BUT only real
              (pair with min.insync                  if min.insync.replicas>=2
               .replicas >= 2)
```

**In our analogy:** You hand a ticket to the lead clerk.
- `acks=0`: you toss the ticket and walk off without looking.
- `acks=1`: you wait until the lead clerk says "clipped it" — but the backup walls may not have photocopied it yet.
- `acks=all`: you wait until the lead clerk AND every **currently up-to-date** backup wall confirm they have a copy. (And `min.insync.replicas`=2 is the house rule: "refuse to even accept a ticket unless at least two walls can hold a copy" — so you're never fooled into thinking one lonely wall is safe.)

**Why it matters:** `acks` is your **main durability dial on the write side**. For data you can't lose (orders, payments), use `acks=all` **plus** `min.insync.replicas` ≥ 2. Picking these wrong is a classic cause of silent data loss.

---

## 16. Delivery guarantees (at-least-once) and why "idempotent" matters

**Plain definition:** A *delivery guarantee* is the promise about **how many times** a message might get delivered/processed. The common, practical one in Kafka is **at-least-once**.

Three possible guarantees, in plain words:
- **At-most-once:** each message is processed **0 or 1 times** — never twice, but it **might be skipped/lost**.
- **At-least-once:** each message is processed **1 or more times** — never lost, but it **might be processed twice** (a duplicate).
- **Exactly-once:** processed **exactly 1 time** — never lost, never duplicated.

A word on **exactly-once**, because it's widely misunderstood. Kafka really does offer **exactly-once semantics** ("EOS"), and it's production-grade — *but its scope is limited*. It works for **Kafka-to-Kafka** pipelines: read from Kafka, process, and write the result **back into Kafka**, committing the offset in the same all-or-nothing transaction. It does **NOT** stretch to external side effects. Our running example — **charging a credit card** through some outside payment service — is exactly the case EOS **cannot** cover, because the card charge happens in a system outside Kafka's transaction. So for ordinary app work with external side effects, **plan around at-least-once**, and make your processing safe to repeat (below).

**Here's *why* duplicates happen under at-least-once**, tied to the bookmark (Section 13):

A safe consumer does work **first**, then commits the bookmark. But a crash can land **between** those two steps:

```
   AT-LEAST-ONCE: how a duplicate happens

   1. read message at offset 5
   2. DO THE WORK (e.g., charge the card)   <-- this succeeds
   3. ... CRASH before committing offset 5 ...

   On restart, committed offset is still 4, so the consumer
   reads offset 5 AGAIN and charges the card a SECOND time. Duplicate!
```

So you must **assume** a message can arrive more than once. The cure is to make your processing **idempotent**.

**Term: idempotent.** An action is *idempotent* if **doing it twice has the same effect as doing it once**. Running it again causes no extra change.

Plain examples:
- "Set the light switch to ON" is **idempotent** — flip it to ON twice, it's still just ON.
- "Toggle the light switch" is **NOT idempotent** — do it twice and you're back to OFF.
- "Set order 42's status to PAID" is idempotent. "Add $20 to the balance" is NOT (twice = $40).

```
   NON-IDEMPOTENT (dangerous under at-least-once):
     processMessage() { balance = balance + 20 }   // twice -> +40  BAD

   IDEMPOTENT (safe under at-least-once):
     processMessage() {
       if (alreadyProcessed(msg.id)) return;        // ignore duplicates
       balance = balance + 20;
       markProcessed(msg.id);
     }
   // OR design the op itself to be naturally idempotent:
     setOrderStatus(42, "PAID")                      // twice -> still PAID  GOOD
```

> **Note:** `alreadyProcessed(...)` and `markProcessed(...)` are **functions YOU write** in your own app — for example, checking and updating a small database table of message ids you've already handled. They are **not** built-in Kafka features. The idea: give each message a **unique id**, and before acting, check "have I already handled this id?" If yes, skip. That makes a duplicate delivery harmless.

**In our analogy:** Sometimes the same ticket gets cooked twice (a cook crashed before crossing it off, so the next cook re-cooks it). If your rule is "mark order #42 as *served*," cooking it twice still just means "served" — no harm. If your rule is "add one more plate to the customer's bill each time," you just double-charged them. Design for the first kind.

**Why it matters:** Kafka practically guarantees **at-least-once**, which means **duplicates will happen eventually**. Making your handlers **idempotent** is what keeps duplicates from corrupting your data. This is one of the most important real-world Kafka lessons.

---

## 17. Failure: a consumer crashes mid-processing → redelivery

Let's walk the crash from Section 16 end to end, now that we have all the pieces (offset, committed offset, group, rebalance, heartbeat).

**Scenario:** Group `billing` is processing `orders-0`. Consumer A has read up to offset 7. The committed bookmark is at 4. A is currently working on offset 5 (charging a card). Then A crashes.

```
   STEP BY STEP

   orders-0:  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
                              ^bookmark=4    ^A had read to 7
                                  ^A is mid-processing offset 5

   1. Consumer A CRASHES while processing offset 5.
   2. Kafka notices A is gone (A's heartbeats — its "I'm alive" signals — stop).
   3. REBALANCE: orders-0 is reassigned to Consumer B (same group).
   4. B asks "where's the bookmark for orders-0?"  -> offset 4.
   5. B resumes at offset 5 — re-reading 5, 6, 7.
   6. So offsets 5,6,7 may be processed AGAIN (including the earlier card charge).
```

```
   RESULT: NO MESSAGE LOST (good) but offsets 5..7 possibly REDELIVERED
           (duplicates). -> exactly why we made processing IDEMPOTENT (Section 16).
```

**Term: redelivery.** *Redelivery* means Kafka hands the **same message to a consumer again**, because the previous attempt wasn't safely committed (the consumer crashed, or processing failed before commit).

Notice how every earlier concept shows up here:
- **Heartbeat** (Section 14) is *how* Kafka learns A died.
- **Committed offset** (Section 13) is *why* B knows to resume at 5, not 0 or 8.
- **Consumer group** (Section 12) is *why* B inherits A's partition and bookmark.
- **Rebalance** (Section 14) is the *mechanism* that hands orders-0 to B.
- **Idempotency** (Section 16) is *why* the redelivery of offset 5 doesn't corrupt anything.

**In our analogy:** A cook collapses mid-dish (their "I'm alive" taps on the rail stop). The team re-divides rails (rebalance), and another cook reads the rail's sticky-note bookmark ("cooked through #4") and restarts at ticket #5 — possibly re-cooking #5. Because "mark as served" is idempotent, no harm done. **No ticket is skipped** (no lost data), which is the at-least-once promise.

**Why it matters:** This is the **everyday failure** Kafka is built to survive. Understanding it tells you exactly **when** to commit (after work) and **why** idempotency is mandatory.

---

## 18. Poison message → retry topic → dead-letter queue

Sometimes a message can **never** be processed successfully, no matter how many times you retry. Re-reading it forever would **block the whole partition** behind it. We need an escape hatch.

**Term: poison message (also "poison pill").** A *poison message* is a message that **always fails** to process — e.g., it's malformed, references data that doesn't exist, or hits a bug. Retrying it endlessly is pointless and **stalls everything after it** on that partition.

```
   A POISON MESSAGE BLOCKS THE RAIL

   orders-0:  | ok | ok | BAD | ok | ok | ok |
                          ^^^
                  fails... retried... fails... retried...
                  meanwhile offsets after BAD are STUCK waiting.   BAD!
```

The standard handling pattern has two more pieces: a **retry topic** and a **dead-letter queue**.

> **Read this first — two honest caveats about this pattern:**
> 1. **Retry topics and DLQs are NOT built-in Kafka mechanics.** They are an **application-level pattern** that you (or a framework like Spring Kafka) implement on top of plain topics. Kafka itself has no "retry" button.
> 2. **Diverting a failed message to a side topic SACRIFICES per-key ordering.** Remember Section 4: keying guarantees a customer's messages stay in order. But if message 3 fails and you shunt it aside so messages 4, 5, 6 keep flowing, then message 3 gets processed *after* them — **out of order** for that key. If strict per-key ordering matters more than throughput, you must instead **pause/block that partition** until the message succeeds (accepting the slowdown). The retry-topic pattern trades ordering for keep-moving throughput; choose deliberately.

**Term: retry topic.** A *retry topic* is a **separate topic** where you put a message that failed, so you can **try it again later** — usually after a short delay — **without blocking the main topic.** Note: the "delay" is **not** a built-in Kafka timer; your **retry consumer implements it** (for example, it waits a few seconds before processing each retry message). Kafka has no native per-message delayed delivery.

**Term: DLQ — dead-letter queue.** A *dead-letter queue (DLQ)* is a **separate topic that holds messages that failed too many times** (gave up after N retries). Nothing automatically processes a DLQ; it's a **parking lot** for humans/tools to inspect, fix, and maybe replay later.

(The phrase "dead letter" comes from the postal service: a letter that can't be delivered and can't be returned goes to the "dead letter office." Same idea.)

```
   THE FLOW: main -> retry -> DLQ

   [orders] --process--> success?  --yes--> done
                              |
                              no (it failed)
                              v
                      [orders.retry]  --wait a bit, try again-->
                              |
                       still failing after N attempts?
                              |
                              v
                          [orders.DLQ]   (parking lot; alert a human)

   Main topic keeps flowing the WHOLE time — the bad message stepped aside.
   (Trade-off: that bad message is now out of order for its key.)
```

In practice, teams often use **several** retry topics — one per delay tier — so each failed message waits a bit **longer** before each new attempt:

```
   RETRY LADDER (common real setup — each tier is its OWN topic)

   orders            -> try once
   orders.retry.5s   -> wait 5s,  try again
   orders.retry.30s  -> wait 30s, try again
   orders.retry.5m   -> wait 5m,  try again
   orders.DLQ        -> gave up; hold for inspection

   (The 5s/30s/5m waits are enforced by your retry consumers, not by Kafka.)
```

**In our analogy:** A ticket is unreadable — coffee spilled on it, can't tell what it says. Instead of holding up the whole rail staring at it, the cook moves it to a **"try again later" hook** (retry topic). If it's still unreadable after a few tries, it goes into the **"problem tickets" box** (DLQ) for the manager to sort out by hand. The main rail never stopped moving — at the cost that this one ticket is now handled out of its original order.

**Why it matters:** Retry topics + DLQ keep **one bad message from freezing your entire pipeline**, while making sure that bad message is **never silently lost** — it's safely parked for investigation. Just remember it's an app-level pattern and it costs you per-key ordering.

---

## 19. Consumer lag + debugging a slow/stuck consumer

**Term: consumer lag.** *Consumer lag* is the gap between the **newest offset in a partition** (latest message written) and the **committed offset of a consumer group** (how far that group has processed). In plain words: **how far behind the readers are.**

```
   CONSUMER LAG = how many unread messages are piling up

   orders-0:  | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |
                              ^                       ^
                  committed offset = 4        latest offset = 9

   LAG = 9 - 4 = 5   (5 messages produced but not yet processed)
```

- **Lag near 0** = consumers are keeping up. Healthy.
- **Lag steady and small** = fine, just a small backlog.
- **Lag growing over time** = consumers are **falling behind** — producers are outpacing them. Something's wrong or under-scaled.
- **Lag stuck / not moving** = the consumer is likely **stuck** (crashed, frozen, or jammed on a poison message).

**In our analogy:** Lag is **how many tickets are hanging on the rail, uncooked**. A couple of tickets = normal. A rail jammed full and growing = the cooks can't keep up. A rail that's full and the count never drops = a cook has stopped cooking entirely.

### How to debug a slow or stuck consumer

A practical checklist, from most common to least:

```
   DEBUGGING HIGH / GROWING / STUCK LAG

   1. Is the consumer even alive?
      - Check it's running and not crash-looping
        (crash-looping = starting, crashing, restarting, over and over).
      - Check for constant REBALANCES (members joining/leaving repeatedly).
        Frequent rebalances = lots of pauses = lag grows.

   2. Is one message poisoning a partition? (Section 18)
      - Lag stuck on ONE partition while others are fine
        => likely a poison message blocking that rail.
      - Fix: send failures to retry/DLQ so the rail can move.

   3. Is processing just too SLOW per message?
      - Slow database (DB) calls, slow external API (Application Programming
        Interface — how programs talk to other programs/services over the
        network), or heavy CPU (the processor — the chip doing the
        calculations) work in the handler.
      - Measure time-per-message. Optimize the part of the code that runs
        most often or takes the most time.

   4. Not enough parallelism?
      - Add more consumers to the group...
        BUT remember the cap: you can't exceed the PARTITION count
        (Section 12). 3 partitions -> at most 3 useful consumers.
      - If already maxed, you may need MORE PARTITIONS on the topic.

   5. Is the consumer committing correctly?
      - Committing too late / in huge batches can cause big redelivery
        spikes after a restart, which looks like lag.

   6. Are the BROKERS healthy?
      - A struggling broker (disk full, network trouble) slows everything.
```

```
   QUICK DECISION TREE

   Lag growing?
     |
     +-- stuck on ONE partition? ------> suspect POISON message -> retry/DLQ
     |
     +-- all partitions behind?
            |
            +-- consumers maxed (= partition count)? --> add PARTITIONS
            |
            +-- per-message slow? --------------------> optimize handler / scale out
            |
            +-- constant rebalances? -----------------> fix membership stability
```

**Why it matters:** Lag is the **single most useful health metric** for Kafka consumers. Watching it tells you instantly whether your readers are keeping up, and the *pattern* of the lag (one partition vs all, growing vs stuck) points straight at the cause.

---

## A) Putting it all together — the full flow

Here is the entire system in one picture, using everything above. Follow the numbers.

```
  ============================ KAFKA, END TO END ============================

  (1) PRODUCER (your Orders service, e.g. a NestJS app)
      builds message { key:"customer-42", value: OrderPlaced{...} }
      sends with acks=all + min.insync.replicas>=2
        (wait for leader + all current in-sync replicas; refuse if too few)
          |
          v  key "customer-42" is run through a HASH formula -> partition 1
  --------------------------------------------------------------------------
  (2) TOPIC "orders" (split into 3 PARTITIONS = 3 append-only logs)

      partition 0:  | 0 | 1 | 2 | 3 |                 (leader on Broker 1)
      partition 1:  | 0 | 1 | 2 | 3 | 4 |  <-- msg    (leader on Broker 2)
      partition 2:  | 0 | 1 | 2 |                     (leader on Broker 3)

      each partition is replicated to 3 brokers (replication factor = 3):
        leader takes writes; FOLLOWERS copy it; caught-up ones = ISR
        acks=all -> the write is confirmed only after all current ISR have it
        (min.insync.replicas>=2 makes the leader REJECT writes if ISR shrinks
         too far, so "confirmed" really means safe)
  --------------------------------------------------------------------------
  (3) BROKERS form a CLUSTER (Broker 1,2,3). If a leader dies, Kafka's
      internal coordinator promotes an ISR follower -> no acknowledged data lost.
  --------------------------------------------------------------------------
  (4) CONSUMER GROUP "billing"  (3 consumers -> 1 per partition)
        partition 0 -> Consumer A
        partition 1 -> Consumer B   reads offset 4 (our message)
        partition 2 -> Consumer C

      each consumer (it PULLS messages; Kafka doesn't push):
        read -> DO WORK (charge card) -> COMMIT offset (bookmark)
                       ^ idempotent, so a redelivery can't double-charge

      if B crashes mid-message (its heartbeats stop):
        REBALANCE -> partition 1 moves to A or C
        new owner resumes at COMMITTED OFFSET -> at-least-once
        (may re-process from the bookmark up to where B got to)

      if a message always fails (poison):
        send to [orders.retry] (app-level; try later) -> still failing? ->
        [orders.DLQ].  partition 1 keeps flowing
        (trade-off: that message is now out of order for its key)
  --------------------------------------------------------------------------
  (5) SECOND CONSUMER GROUP "emailing" reads the SAME topic independently,
      with its OWN bookmarks -> also sees every order, sends receipts.
  --------------------------------------------------------------------------
  (6) You MONITOR consumer LAG = latestOffset - committedOffset per partition.
      Lag growing on ONE partition usually means a poison message.
      Lag growing on ALL partitions means scale or optimize the consumers.

  ==========================================================================
```

Read in one breath: **A producer writes a message (an event) with a key into a topic. The key is run through a hash formula to pick a partition, which is an append-only log with per-message offsets. The partition lives on a broker, one of several brokers forming a cluster, and is replicated to followers — the caught-up ones form the ISR — with one leader taking writes; `acks=all` together with `min.insync.replicas` makes the write wait for the ISR (and refuse if too few copies are in sync) so confirmed data isn't lost. Consumers in a consumer group pull messages, split the partitions (one each), process them, and commit offsets as bookmarks. If a consumer crashes (its heartbeats stop), a rebalance moves its partition to another consumer, which resumes from the committed offset — that's at-least-once delivery, so handlers must be idempotent. Messages that always fail go to an app-level retry topic and finally a dead-letter queue (trading per-key ordering for throughput). A different consumer group reads the same topic independently. You watch consumer lag to know if your readers are keeping up.**

If that paragraph now makes sense, you've learned Kafka.

---

## B) Glossary — the key terms in one line

| Term | One-line meaning |
|---|---|
| **Service** | A running program that does one job (e.g., Orders service). |
| **Tightly coupled** | One service calls another directly and breaks if it's down. |
| **Decoupled** | Services don't call each other directly; a middle-man passes data. |
| **Durable** | Written to disk; survives crashes and restarts. |
| **Kafka** | A durable middle-man that passes messages between services reliably. |
| **Message / Event** | One piece of data ("something happened"); has a key and a value. |
| **Key** | Optional label on a message; same key → same partition (keeps order). |
| **Value** | The actual content/payload of a message. |
| **JSON** | JavaScript Object Notation; a common text format for data (`{ ... }`). |
| **Byte** | The tiny unit all data is ultimately made of. |
| **Topic** | A named category of messages (a "named rail"). |
| **Partition** | A slice of a topic; one independent, ordered, append-only line of messages. |
| **Hash / hash function** | A fixed formula turning a key into a consistent number, used to pick a partition. |
| **Round-robin** | Handing items out one at a time in rotation (rail 0, 1, 2, 0…); old keyless default. |
| **Sticky partitioner** | Modern keyless default (Kafka 2.4+): fill one partition's batch, then switch; evens out over time. |
| **Throughput** | How much work (messages/sec) the system handles. |
| **Offset** | The position number of a message within its partition (0,1,2,…); always increases, not always gapless. |
| **Log (append-only)** | An ordered file you can only add to the end of; never edit/insert. |
| **Retention** | How long Kafka keeps messages before cleaning up old ones. |
| **Broker** | One Kafka server; stores partitions and serves reads/writes. |
| **Cluster** | A group of brokers working together as one Kafka. |
| **Coordinator** | Kafka's internal supervisor that detects dead brokers and elects new leaders. |
| **Replication** | Keeping copies of a partition on multiple brokers. |
| **Replica** | One copy of a partition. |
| **Replication factor** | How many copies of a partition you keep (e.g., 3). |
| **Leader** | The one replica that takes all writes (and reads, by default) for a partition. |
| **Follower** | A replica that just copies the leader; a standby. |
| **ISR (in-sync replica)** | A replica caught up with the leader (within an allowed time-lag); safe to promote. |
| **Producer** | A program that writes messages into a topic. |
| **Consumer** | A program that reads messages from a topic and acts on them. |
| **Pull / poll model** | The consumer asks Kafka for messages; Kafka does not push them. |
| **Consumer group** | A team of consumers sharing a name that split a topic's (or topics') partitions. |
| **(rule) 1 partition → 1 consumer** | Within a group, each partition is read by exactly one consumer. |
| **Committed offset (bookmark)** | Saved marker of how far a group has processed a partition. |
| **Commit** | To save the bookmark (committed offset) back to Kafka. |
| **Heartbeat** | A periodic "I'm alive" signal a consumer sends; its absence triggers a rebalance. |
| **Rebalance** | Re-dividing partitions among a group's consumers when membership changes. |
| **Eager vs cooperative rebalance** | Eager = whole group pauses; cooperative (modern) = only moved partitions pause. |
| **Ack (acknowledgement)** | Kafka confirming "I stored your message." |
| **acks=0** | Producer waits for nothing; fastest, can lose data. |
| **acks=1** | Producer waits for the leader only; loses data if leader dies early. |
| **acks=all (-1)** | Producer waits for leader + all current ISR; strongest (pair with min.insync.replicas). |
| **min.insync.replicas** | Minimum in-sync copies required to accept a write; the real guard for acks=all. |
| **Delivery guarantee** | The promise about how many times a message is processed. |
| **At-most-once** | 0 or 1 times; may be lost, never duplicated. |
| **At-least-once** | 1+ times; never lost, may be duplicated (Kafka's practical default). |
| **Exactly-once (EOS)** | Exactly 1 time; real but scoped to Kafka-to-Kafka, not external side effects. |
| **Idempotent** | Doing it twice has the same effect as doing it once. |
| **Redelivery** | Kafka hands the same message again because it wasn't safely committed. |
| **Poison message** | A message that always fails and would block its partition. |
| **Retry topic** | An app-level side topic to try failed messages again later without blocking the main one. |
| **DLQ (dead-letter queue)** | A topic parking failed-too-many-times messages for humans to inspect. |
| **Consumer lag** | latest offset − committed offset; how far behind the readers are. |