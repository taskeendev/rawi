# Rawi (راوي) — Progress

## Status: กำลังทำ

## Phase Roadmap

### Phase 1 — Content Service
- [x] สร้างโฟลเดอร์โปรเจค
- [x] Generate Spring Boot 3.5.14 (Java 21 + Spring Batch + JPA + PostgreSQL + Lombok)
- [ ] Git init + GitHub repo
- [ ] Docker + PostgreSQL setup
- [ ] Flyway migration setup
- [ ] Spring Batch job — รับ URL → ดึงเนื้อหา
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
- [ ] Jenkins บน Docker
- [ ] SonarQube
- [ ] GitHub Webhook → auto deploy
- [ ] Telegram notify ผล build ✅/❌

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
