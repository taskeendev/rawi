# Rawi (راوي) — Progress

## Status: Phase 3 — CI/CD ✅

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

### Phase 1.5 — Production Deploy ✅
- [x] CORS — อนุญาต Vercel domain
- [x] Merge phase/1-content-service → main
- [x] Render watch main (via deploy hook)
- [x] Tilawah: NEXT_PUBLIC_RAWI_API_URL → https://rawi-3fe4.onrender.com
- [x] Tilawah: ลบ output: 'standalone' ออกจาก next.config.ts
- [x] Tilawah: cold start UX — loading/fallback state บน Readings page
- [x] Deploy Tilawah บน Vercel — https://tilawah-three.vercel.app

### Phase 2 — Tilawah v2 Frontend ✅
- [x] UI refresh Netflix style (dark theme, hero banner, hover effect)
- [x] Unsplash API integration
- [x] หน้า Ilm (علم) — คลัง content + ปุ่มสุ่ม
- [x] 3D Map UX

### Phase 3 — GitHub Actions CI/CD ✅
- [x] GitHub Actions workflow (Build → Test → Deploy)
- [x] Render deploy hook ใน GitHub Actions
- [x] Telegram notify ผล build ✅/❌
- [x] Jenkins + SonarQube (local — learning/portfolio เท่านั้น)
- [ ] Vercel deploy hook ใน GitHub Actions (ไม่จำเป็น — Vercel auto-deploy จาก GitHub แล้ว)

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
- CI/CD: GitHub Actions ✅
- Notify: Telegram (Raqib bot)
