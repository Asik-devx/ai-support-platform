# AI Support Platform

A **production-grade, AI-assisted customer support system.** principles: microservices, async processing, reliability patterns, and AI orchestration.

---

## 🎯 Purpose
This project is intentionally designed **beyond CRUD** to reflect how real systems are built:

- AI is treated as an **external, unreliable dependency**
- User-facing APIs are **fast and resilient**
- Background processing is **event-driven and idempotent**
- Failures are **observable and recoverable**

---

## 🧱 Architecture Overview

```
[ Client / UI ]
        |
        v
[ Auth Service ]  ---> issues JWT
        |
        v
[ Ticket Service ]  --(event)--->  Redis Pub/Sub
        |                              |
        |                              v
        |                        [ AI Consumer ]
        |                              |
        |                              v
        |                     [ AI Orchestrator ]
        |                              |
        |                              v
        |                         [ OpenAi ]
        v
[ PostgreSQL ]
```

---

## 🔑 Core Services

### 1️⃣ Auth Service (Spring Boot)
- User registration & login
- JWT issuance
- Stateless authentication

**Why isolated?**  
Auth is a cross-cutting concern and must scale independently.

---

### 2️⃣ Ticket Service (Spring Boot)
Responsible for:
- Ticket lifecycle management
- Publishing ticket-created events
- Async AI enrichment

**Key design decisions**:
- Ticket creation never waits for AI
- Uses UUIDs (safe for distributed systems)
- Explicit AI lifecycle state

#### Ticket AI State
| Field | Purpose |
|-----|--------|
| `priority` | Business priority (LOW/MEDIUM/HIGH) |
| `aiStatus` | PENDING / COMPLETED / FAILED |

This makes **eventual consistency explicit**.

---

### 3️⃣ AI Orchestrator (Node.js + TypeScript)
Acts as a **protective boundary** around AI:

- AI provider abstraction
- Rate limiting
- Caching
- Provider swapping (OpenAI → Local LLM)

**Why separate service?**
> AI is expensive, slow, and unreliable — it must never pollute core business logic.

---

### 4️⃣ Ollama (Local LLM)
- Runs locally (no API keys)
- Provides deterministic classification
- Easy to swap with hosted providers

**Interview talking point**:
> Local-first AI for cost, privacy, and predictability.

---

## 🔄 Async Processing Model

### Why async?
AI latency is unpredictable. Blocking user requests on AI leads to:
- Poor UX
- Cascading failures
- Thread exhaustion

### Flow
1. Ticket is created (fast)
2. Event published to Redis
3. Background consumer processes AI
4. Ticket updated asynchronously

This results in **eventual consistency**, which is explicitly modeled.

---

## 🛡 Reliability Patterns Implemented

### ✅ Retry with Backoff
- AI failures are retried up to **3 times**
- Prevents transient failures from causing data loss

### ✅ Dead Letter Queue (DLQ)
- Permanent failures are pushed to Redis list
- Allows inspection & replay

### ✅ Idempotency Protection
- Redis `SETNX` used to ensure:
  > One ticket → one AI update
- Protects against retries, duplicates, and restarts

### ✅ Rate Limiting
- Prevents abuse and runaway AI costs

### ✅ Caching
- Hash-based cache keys
- Reduces duplicate AI calls

---

## 🔐 Security

- JWT-based authentication
- Each service validates JWT independently
- No shared sessions

**Why?**
> Avoids tight coupling and single points of failure.

---

## 🐳 Infrastructure

- Docker & Docker Compose
- PostgreSQL per service
- Redis for:
  - Pub/Sub
  - Caching
  - Rate limiting
  - Idempotency

All services communicate via **Docker DNS**, not localhost.

---
