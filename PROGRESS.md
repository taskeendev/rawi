# Rawi (راوي) — Progress

## Status: Phase 1.5 — Production Deploy (กำลังทำ)

## Phase Roadmap

### Phase 1 — Content Service ✅
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
- [~] Ollama integration — ⏭️ ข้าม: ใช้ Groq API แทน (เร็วกว่า, ฟรี tier พอ)
- [x] pgvector (similarity check) — vector(768) + ivfflat index
- [x] REST API ให้ Tilawah ดึง content — GET /content + GET /categories
- [x] Obsidian sync — สร้าง .md ลง ~/Documents/Rawi-Vault/{category}/
- [x] Dockerfile (multi-stage Java 21) + env var config สำหรับ cloud
- [x] Deploy บน Render — https://rawi-3fe4.onrender.com
- [x] DB migrate ไป Neon (PostgreSQL 16 + pgvector) — Singapore region

### Phase 1.5 — Production Deploy 🔄
- [ ] CORS — อนุญาต Vercel domain
- [ ] Merge phase/1-content-service → main
- [ ] Render เปลี่ยนมา watch main
- [ ] Tilawah: แก้ NEXT_PUBLIC_RAWI_API_URL → https://rawi-3fe4.onrender.com
- [ ] Tilawah: แก้ output: 'standalone' ออกจาก next.config.ts
- [ ] Tilawah: cold start UX — loading/fallback state บน Readings page
- [ ] Deploy Tilawah บน Vercel

### Phase 2 — Tilawah v2 Frontend
- [ ] UI refresh Netflix style (dark #141414, hero banner, hover effect)
- [ ] Unsplash API integration
- [ ] หน้า Ilm (علم) — คลัง content + ปุ่มสุ่ม
- [ ] แก้ 3D Map UX

### Phase 3 — GitHub Actions CI/CD
- [ ] GitHub Actions workflow (Build → Test → SonarCloud → Deploy)
- [ ] Render deploy hook ใน GitHub Actions
- [ ] Vercel deploy hook ใน GitHub Actions
- [ ] Telegram notify ผล build ✅/❌
- [x] Jenkins + SonarQube (local — learning/portfolio เท่านั้น)

### Phase 4 — Domain
- [ ] ซื้อ domain
- [ ] ตั้ง custom domain บน Vercel (Tilawah)
- [ ] ตั้ง custom domain บน Render (Rawi)

## Tech Stack
- Language: Java 21
- Framework: Spring Boot 3.5.14 + Spring Batch
- AI: Groq API (llama-3.3-70b-versatile)
- Vector DB: pgvector (Neon)
- Deploy: Render (https://rawi-3fe4.onrender.com)
- DB: Neon PostgreSQL 16 (Singapore)
- CI/CD: GitHub Actions (Phase 3)
- Notify: Telegram (Raqib bot)
