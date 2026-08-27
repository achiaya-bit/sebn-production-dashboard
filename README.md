# SEBN Production Dashboard

A production monitoring dashboard for the SEBN welding area. Compares planned and reported quantities, tracks backlog, and provides paginated access to individual production orders — all driven from a real MySQL dataset.

---

## V1 Scope

| Feature | Description |
|---|---|
| KPI cards | Planned Quantity, Reported Quantity, Scrapped Quantity, Backlog, Completion Rate |
| Date & status filters | Filter all charts and orders by start date, end date, order status, and part number |
| Reporting-time filter | Filter by WARMUZ reporting time range (both boundaries required together) |
| Planned vs Reported trend | Daily ComposedChart — grey bars (Planned) + orange line (Reported) |
| Cumulative Backlog trend | Running backlog AreaChart, floored at zero |
| Top 10 Parts by Backlog | Horizontal bar chart ranked by remaining quantity |
| Orders by Status | Donut chart with order counts per status code |
| Paginated Production Orders | Server-side pagination, 25 rows per page, filters preserved across pages |
| Data freshness display | Latest reporting timestamp from the database (WARMDA + WARMUZ), not the server clock |
| 60-second auto-refresh | All dashboard queries refresh automatically while the tab is visible |
| Manual Refresh button | Triggers an immediate refetch without resetting filters |

---

## Architecture

```
┌─────────────────────────────────────┐
│  Browser                            │
│  React 19 + TanStack Start (SSR)    │
│  Vite 8 · Nitro · TanStack Router   │
│  port 3000                          │
└────────────────┬────────────────────┘
                 │ HTTP /api/**
┌────────────────▼────────────────────┐
│  Spring Boot 4 (Java 17)            │
│  JPA · SpringDoc OpenAPI            │
│  port 8080                          │
└────────────────┬────────────────────┘
                 │ JDBC
┌────────────────▼────────────────────┐
│  MySQL 8.0                          │
│  database: sebn_dashboard           │
│  host port: 3307                    │
└─────────────────────────────────────┘
```

All three services run as Docker containers coordinated by Docker Compose.

---

## Prerequisites

- Docker 28.5+ and Docker Compose 2.40+
- A `mysqldump` export of the `sebn_dashboard` database (see below)
- Git

---

## Quick start

### 1. Clone the repository

```bash
git clone https://github.com/achiaya-bit/sebn-production-dashboard.git
cd sebn-production-dashboard
```

### 2. Create your `.env` file

```bash
cp .env.example .env
```

Open `.env` and set a real database password:

```
DB_PASSWORD=your_secure_password
DB_USERNAME=root
MYSQL_DATABASE=sebn_dashboard
DATA_MODE=IMPORTED
```

The `.env` file is listed in `.gitignore` and is never committed.

### 3. Place the database dump

Export the database from the source machine:

```bash
mysqldump -u root -p sebn_dashboard > docker/mysql/init/sebn_dashboard.sql
```

Place the resulting file at exactly:

```
docker/mysql/init/sebn_dashboard.sql
```

MySQL imports every `.sql` file in that directory automatically on first startup. **The import runs only once** — when the named volume `mysql_data` does not yet exist.

### 4. Start the stack

```bash
docker compose up -d
```

Watch startup:

```bash
docker compose logs -f
```

Check that all three containers are healthy:

```bash
docker compose ps
```

---

## URLs

| Service | URL |
|---|---|
| Dashboard | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| MySQL (optional) | localhost:3307 |

---

## Local development (without Docker)

Requirements: Java 17, Node 22+, npm, a local MySQL 8 instance.

**Backend:**

```bash
cd backend
# Set DB_PASSWORD in your environment or a local .env before running tests
.\mvnw.cmd spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm run dev          # dev server → http://localhost:5173
npm run build        # standard production build
npm run build:docker # Node/Nitro production build (used by Docker)
```

---

## Running tests

**Backend (66 tests):**

```bash
cd backend
.\mvnw.cmd test
```

Tests cover: KPI calculation, cumulative backlog formula, reporting-time filter validation, data-freshness service.

**Frontend (TypeScript check):**

```bash
cd frontend
npx tsc --noEmit
```

---

## Docker commands

```bash
# Start all services
docker compose up -d

# Rebuild images after a code change
docker compose up -d --build

# Rebuild a single service
docker compose build backend
docker compose up -d backend

# Tail logs
docker compose logs -f
docker compose logs -f backend

# Stop and remove containers (data volume preserved)
docker compose down

# Full teardown including the database volume
docker compose down -v
```

---

## KPI and backlog formulas

| KPI | Formula |
|---|---|
| Planned Quantity | `SUM(WAURMG)` |
| Reported Quantity | `SUM(WAGFMG)` |
| Scrapped Quantity | `SUM(WAAUMG)` |
| Backlog | `MAX(0, Planned − Reported)` |
| Completion Rate | `(Reported / Planned) × 100`, rounded to 2 dp |

**Cumulative backlog trend:**

```
dailyDiff(t)        = plannedQty(t) - reportedQty(t)
running(t)          = cumulativeBacklog(t-1) + dailyDiff(t)
cumulativeBacklog(t) = MAX(0, running(t))
```

The cumulative value is floored at zero on each day — negative daily differences do not carry forward as credit.

---

## Data freshness semantics

`GET /api/dashboard/data-freshness` returns:

```json
{
  "latestReportedAt": "2026-08-04T15:58:24",
  "latestOrderModificationDate": "20260804",
  "dataMode": "IMPORTED"
}
```

- **`latestReportedAt`** is derived from `MAX(WARMDA + LPAD(WARMUZ, 6, '0'))` in MySQL — it reflects the newest reporting entry in the stored dataset. It is not the current server time.
- **`dataMode: IMPORTED`** means data was loaded from a `mysqldump` export. The dashboard shows the latest state available in MySQL.
- **Auto-refresh** re-reads MySQL every 60 seconds while the browser tab is visible. It does **not** itself synchronize WAO with MySQL. True live data requires an external WAO-to-MySQL ingestion process.
- Historical state reconstruction is not supported — the `wao_orders` table stores the current state of each order, not a change history.

---

## Known V1 limitations

- `fromTime` / `toTime` filtering is fully wired but excluded rows where `WARMDA` or `WARMUZ` are absent.
- The `wrangler`/Cloudflare build target was removed; the Docker build uses the Nitro `node-server` preset.
- No authentication or authorization layer.
- No real-time WAO synchronization.

---

## Version 2 — future scope

Version 2 has not been started. It requires the following inputs to be available before design can begin:

| Input | Description |
|---|---|
| Weekly demand | External demand plan per part number per week |
| Current warehouse stock | Physical inventory counts per PN |
| Nominal stock levels | Target stock per PN (safety stock, reorder points) |
| Existing planned quantities | Already-scheduled production (avoid double-counting) |
| Capacity rules | Available hours, machine constraints, shift structure |
| Priority rules | Customer priority, urgency tiers |
| Validation workflow | Who reviews and approves the generated schedule |

Version 2 will suggest a production schedule that bridges the gap between current stock, demand, and nominal levels — but this requires all of the above to be defined and integrated first.

---

## Repository

GitHub: [achiaya-bit/sebn-production-dashboard](https://github.com/achiaya-bit/sebn-production-dashboard)
