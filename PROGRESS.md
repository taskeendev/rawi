# Rawi (راوي) — Progress

## Status: กำลังทำ

## Phase Roadmap

### Phase 1 — Content Service
- [x] สร้างโฟลเดอร์โปรเจค
- [x] Generate Spring Boot 3.5.14 (Java 21 + Spring Batch + JPA + PostgreSQL + Lombok)
- [x] Git init + GitHub repo (github.com/taskeendev/rawi)
- [x] แตก branch phase/1-content-service
- [x] Docker + PostgreSQL setup (port 5435) + pgAdmin (port 5050)
- [x] ตั้งค่า application.properties (DB connection)
- [x] Flyway migration setup (V1__init.sql — table content_item)
- [x] ติดตั้ง Java 21 (Temurin via SDKMAN)
- [x] Build ผ่าน ✅ (./mvnw clean package -DskipTests)
- [x] Spring Batch job — รับ URL → ดึงเนื้อหา (scrape + AI summary + category, POST /api/v1/batch/run)
- [x] เพิ่ม Uptime Kuma monitor (http://host.docker.internal:8090/actuator/health)
- [x] เพิ่ม Grafana dashboard สำหรับ Rawi
- [ ] Ollama integration (AI summarize)
- [ ] pgvector (similarity check)
- [ ] REST API ให้ Tilawah ดึง content
- [ ] Obsidian sync (สร้าง .md ลง vault)

### Phase 2 — Tilawah v2 Frontend
- [ ] UI refresh Netflix style (dark #141414, hero banner, hover effect)
- [ ] Unsplash API integration
- [ ] หน้า Ilm (علم) — คลัง content + ปุ่มสุ่ม
- [ ] แก้ 3D Map UX

### Phase 3 — DevOps
- [x] Jenkins บน Docker (raqib-jenkins port 8082)
- [x] SonarQube (raqib-sonarqube port 9002)
- [x] Build pipeline pass ✅ (Checkout → Build → Test → SonarQube)
- [ ] GitHub Webhook → auto deploy
- [x] Telegram notify ผล build ✅/❌

### Phase 4 — Cloudflare Tunnel
- [ ] Cloudflare Tunnel free tier
- [ ] Telegram แจ้ง URL ใหม่ทุกครั้งที่ deploy

## Tech Stack
- Language: Java 21
- Framework: Spring Boot 3.5.14 + Spring Batch
- AI: Ollama (Mac Mini M4)
- Vector DB: pgvector
- Deploy: Docker (Mac Mini M4)
- CI/CD: Jenkins + SonarQube + GitHub Webhook
- Notify: Telegram (Raqib bot)
