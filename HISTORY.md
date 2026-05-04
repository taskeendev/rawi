# HISTORY.md — Engineering Log

---

## 2026-05-05 — Phase 3 GitHub Actions CI/CD ✅

ทำอะไร: ตั้งค่า GitHub Actions pipeline — build, test, deploy to Render, Telegram notify
ทำไม: ต้องการ auto-deploy ทุกครั้งที่ push to main โดยไม่ต้อง manual trigger
การเปลี่ยนแปลง:
  - .github/workflows/ci.yml: trigger on push/PR to main, postgres service container (pgvector:pg16), Maven build + test + deploy + notify
  - application-ci.properties: เปลี่ยน rawi-postgres → localhost (GitHub Actions service container)
  - GitHub Secrets: RENDER_DEPLOY_HOOK_URL, TELEGRAM_BOT_TOKEN, TELEGRAM_CHAT_ID, DB_PASSWORD
  - Merge phase/1-content-service → main (Phase 1 code ขึ้น main ครั้งแรก)
ผล: ✅ Build #1 สำเร็จใน 1m13s — Render deploy triggered, Telegram แจ้งเตือน

---

## 2026-05-02 — Deploy Rawi บน Render + Migrate DB ไป Neon

ทำอะไร: Deploy Rawi Spring Boot ขึ้น cloud ครั้งแรก
ทำไม: ต้องการให้ Tilawah (Vercel) เรียก Rawi API ได้จาก public URL
การเปลี่ยนแปลง:
  - Dockerfile (multi-stage): build stage eclipse-temurin:21-jdk-alpine, runtime eclipse-temurin:21-jre-alpine, JVM flags -Xmx300m
  - application.properties: เปลี่ยน DB config เป็น env vars (DB_URL, DB_USERNAME, DB_PASSWORD)
  - .env.example: อัปเดต template ให้ครบ
  - DB: ย้ายจาก local PostgreSQL (port 5435) → Neon PostgreSQL 16 (Singapore)
  - Flyway migrate schema บน Neon สำเร็จ (2 migrations)
  - Deploy: Render free tier, branch phase/1-content-service
URL: https://rawi-3fe4.onrender.com
ผล: ✅ Health UP, DB UP, /actuator/health ผ่าน
ปัญหา: Koyeb + Railway เปลี่ยนเป็น paid — ใช้ Render แทน (มี cold start 30วิ)
หมายเหตุ: repo เปลี่ยนเป็น public เพื่อให้ Render free tier เข้าถึงได้

---

## 2026-04-29 15:00 — สร้างโฟลเดอร์โปรเจค

ทำอะไร: สร้าง folder ~/Desktop/Rawi
ทำไม: เตรียม root directory สำหรับโปรเจค
Command: `mkdir /Users/taskeen/Desktop/Rawi`
ผล: ✅ สำเร็จ
ปัญหา: ไม่มี

---

## 2026-04-29 15:05 — Generate Spring Boot Project

ทำอะไร: Generate โครงสร้าง Spring Boot 3.5.14 จาก Spring Initializr
ทำไม: เป็น scaffold เริ่มต้นที่มี dependency ครบ ไม่ต้องเขียน config เอง
Dependencies: Spring Web, Spring Batch, Spring Data JPA, PostgreSQL Driver, Lombok
Command: `curl https://start.spring.io/starter.zip ...`
ผล: ✅ สำเร็จ
ปัญหา: bootVersion 3.3.0 ไม่มีแล้ว Spring Initializr reject ด้วย 400
แก้ยังไง: เช็ค version ที่ available แล้วเปลี่ยนเป็น 3.5.14.RELEASE

---

## 2026-04-29 15:10 — ย้ายไฟล์จาก /Rawi/rawi → /Rawi

ทำอะไร: ย้าย project files ให้ Desktop/Rawi เป็น root ตรงๆ
ทำไม: convention ทั่วไป root folder ตรงกับชื่อโปรเจค อ่านง่ายกว่า
Command: `mv /Users/taskeen/Desktop/Rawi/rawi/* /Users/taskeen/Desktop/Rawi/`
ผล: ✅ สำเร็จ
ปัญหา: ไม่มี

---

## 2026-04-29 15:15 — Git init + GitHub repo

ทำอะไร: init git และสร้าง private repo บน GitHub
ทำไม: version control + เก็บ history การทำงาน
Command: `git init && gh repo create taskeendev/rawi --private`
ผล: ✅ github.com/taskeendev/rawi
ปัญหา: ไม่มี

---

## 2026-04-29 15:20 — แตก branch phase/1-content-service

ทำอะไร: สร้าง branch สำหรับ Phase 1
ทำไม: ไม่ทำงานบน main โดยตรง — main ต้องสะอาดเสมอ
Command: `git checkout -b phase/1-content-service`
ผล: ✅ branch พร้อมใช้งาน
ปัญหา: ไม่มี

---

## 2026-04-29 15:25 — Commit แรก + Push

ทำอะไร: commit โครงสร้าง Spring Boot + PROGRESS.md + DEBT_LOG.md แล้ว push
ทำไม: บันทึก baseline ของโปรเจคขึ้น GitHub
ผล: ✅ push สำเร็จ
ปัญหา: ไม่มี

---

## 2026-04-29 15:30 — Docker + PostgreSQL + pgAdmin

ทำอะไร: สร้าง docker-compose.yml มี postgres:15-alpine + pgAdmin4
ทำไม: ต้องการ local DB สำหรับ dev ก่อน connect Spring Boot
Ports: PostgreSQL → 5435, pgAdmin → 5050
Command: `docker compose up -d`
ผล: ✅ rawi-postgres healthy, rawi-pgadmin up
ปัญหา: ไม่มี

---

## 2026-04-29 15:40 — ตั้งค่า application.properties

ทำอะไร: config Spring Boot ให้เชื่อมต่อ PostgreSQL + ตั้งค่า JPA + Spring Batch
ทำไม: Spring Boot ต้องรู้ว่า DB อยู่ที่ไหน และใช้ credential อะไร
Config หลัก: datasource url port 5435, ddl-auto=validate, batch.job.enabled=false, server port 8090
ผล: ✅ สำเร็จ
ปัญหา: ไม่มี

---

## 2026-04-29 16:00 — ติดตั้ง Java 21 + Build ครั้งแรก

ทำอะไร: ติดตั้ง Temurin 21 ผ่าน SDKMAN แล้ว build โปรเจค
ทำไม: เครื่องมี Java 17 แต่โปรเจคต้องการ Java 21 (Virtual Threads)
Commands: `sdk install java 21.0.11-tem` → `./mvnw clean package -DskipTests`
ผล: ✅ BUILD SUCCESS 10.3s
ปัญหา: 1) Spring Boot version ใน pom.xml เป็น 3.5.14.RELEASE แต่ Maven Central ใช้ 3.5.14 (ไม่มี .RELEASE suffix)
       2) เครื่องใช้ SDKMAN จัดการ Java ต้อง switch ผ่าน SDKMAN ไม่ใช่ JAVA_HOME ตรงๆ
แก้ยังไง: 1) แก้ pom.xml เป็น 3.5.14
          2) `sdk install java 21.0.11-tem` แล้ว SDKMAN set เป็น default ให้อัตโนมัติ

---

## 2026-04-30 14:30 — Obsidian sync

ทำอะไร: สร้าง ObsidianSyncService เขียน .md ไฟล์ลง vault หลัง batch process เสร็จ
ทำไม: ต้องการ private knowledge base ใน Obsidian แยกจาก Tilawah (public)
การเปลี่ยนแปลง:
  - ObsidianSyncService.java — เขียน .md พร้อม frontmatter (title, category, source, url, date, tags)
  - ContentProcessor.java — เรียก obsidianSyncService.sync(item) หลัง status = DONE
  - application.properties — เพิ่ม rawi.obsidian.vault-path
  - ติดตั้ง Obsidian + สร้าง vault ที่ ~/Documents/Rawi-Vault
โครงสร้างไฟล์: Rawi-Vault/{category}/{title}.md
ผล: ✅ Medina - Wikipedia.md สร้างใน Rawi-Vault/Religion/ พร้อม frontmatter ครบ
ปัญหา: ไม่มี

---

## 2026-04-30 13:30 — REST API for Tilawah (pagination + DTO + categories)

ทำอะไร: เพิ่ม pagination, ReadingResponse DTO, และ /categories endpoint
ทำไม: API เดิม return entity ตรงๆ (รวม embedding field) + ไม่มี pagination ไม่เหมาะสำหรับ production
การเปลี่ยนแปลง:
  - ReadingResponse.java — DTO ตัดข้อมูลที่ไม่จำเป็นออก (embedding)
  - ContentItemRepository — เพิ่ม Page<ContentItem> queries + findDistinctCategories
  - ContentService — listDone รับ Pageable, เพิ่ม listCategories
  - ContentController — GET /api/v1/content รับ page/size params, เพิ่ม GET /categories
Endpoints: GET /api/v1/content?page=0&size=20&category=Religion | GET /api/v1/content/categories
ผล: ✅ pagination ทำงาน, categories endpoint คืน ["Religion"]
ปัญหา: ไม่มี

---

## 2026-04-30 12:30 — pgvector integration

ทำอะไร: เพิ่ม pgvector extension + embedding column ใน content_item table
ทำไม: ต้องการ similarity search สำหรับหา content ที่คล้ายกัน ป้องกัน duplicate
การเปลี่ยนแปลง:
  - docker-compose.yml: เปลี่ยน image postgres:15-alpine → pgvector/pgvector:pg15
  - V2__add_vector.sql: CREATE EXTENSION vector + ALTER TABLE เพิ่ม embedding vector(768) + ivfflat index
  - pom.xml: เพิ่ม com.pgvector:pgvector:0.1.6
ผล: ✅ column embedding vector(768) + index ivfflat พร้อมใช้งาน
ปัญหา: postgres:15-alpine ไม่มี pgvector extension → ต้อง recreate container + ลบ volume (ข้อมูล test หาย)
แก้ยังไง: เปลี่ยน image เป็น pgvector/pgvector:pg15 ซึ่งมี extension built-in

---

## 2026-04-30 11:00 — Prometheus metrics endpoint + Grafana dashboard

ทำอะไร: เพิ่ม micrometer-registry-prometheus ใน pom.xml + expose /actuator/prometheus + สร้าง Grafana dashboard provisioning
ทำไม: ต้องการ monitor Rawi ใน Grafana เหมือน Tilawah และ Cerberus
ผล: ✅ Prometheus scrape rawi target ได้ — dashboard ขึ้นใน Grafana
ปัญหา: dashboard แสดง No data เพราะ datasource UID ใน JSON ใช้ค่า "prometheus" แต่ Grafana ใช้ UID จริงคือ afk90p4bgchkwe
แก้ยังไง: query Grafana API ดู UID จริง แล้ว replace ทั้งไฟล์

---

## 2026-04-30 11:30 — Uptime Kuma monitor สำหรับ Rawi

ทำอะไร: เพิ่ม monitor "Rawi API" ใน Uptime Kuma ผ่าน Python uptime-kuma-api
ทำไม: ต้องการ uptime tracking เหมือน Tilawah
URL: http://host.docker.internal:8090/actuator/health, interval 60s
ผล: ✅ monitor ID 15 ขึ้น UP
ปัญหา: login ไม่ได้เพราะ password ใน SERVICES.md บันทึกผิด — password จริงถูก hash ด้วย bcrypt ใน kuma.db
แก้ยังไง: generate bcrypt hash ใหม่สำหรับ raqib2026 แล้ว UPDATE user table ใน SQLite

---

## 2026-04-29 17:00 — Jenkins + SonarQube ใน Raqib

ทำอะไร: เพิ่ม Jenkins (lts-jdk21) + SonarQube (lts-community) ใน Raqib docker-compose.yml
ทำไม: ต้องการ CI/CD pipeline สำหรับ Rawi — build, test, code quality อัตโนมัติ
Config หลัก: Jenkins port 8082, SonarQube port 9002, mount /Users/taskeen/Desktop/Rawi → /var/rawi-source:ro
ผล: ✅ ทั้งคู่ขึ้นใน raqib network
ปัญหา: ไม่มี

---

## 2026-04-29 17:30 — Jenkins Pipeline ครั้งแรก — Test ล้มเหลว

ทำอะไร: สร้าง Jenkinsfile (Checkout → Build → Test → SonarQube) แล้ว run pipeline ครั้งแรก
ทำไม: ต้องการ automated build pipeline
ผล: ❌ Test stage fail
ปัญหา: RawiApplicationTests พยายามต่อ localhost:5435 แต่ Jenkins container ไม่มี postgres
แก้ยังไง:
  1. เพิ่ม raqib network ใน Rawi docker-compose → rawi-postgres เข้า network เดียวกับ Jenkins
  2. สร้าง application-ci.properties ชี้ DB ไปที่ rawi-postgres:5432
  3. แก้ Jenkinsfile ใช้ -Dspring.profiles.active=ci ตอน test

---

## 2026-04-29 18:00 — Jenkins Pipeline — Password Authentication Failed

ทำอะไร: run pipeline หลังแก้ network แล้ว
ผล: ❌ ยัง fail — FATAL: password authentication failed for user "rawi"
ปัญหา: Jenkins container ไม่มี DB_PASSWORD env var — application-ci.properties ใช้ ${DB_PASSWORD} แต่ไม่มีค่า
แก้ยังไง: เพิ่ม DB_PASSWORD=${RAWI_DB_PASSWORD} ใน Jenkins environment ใน Raqib docker-compose + เพิ่ม RAWI_DB_PASSWORD=rawi2026 ใน Raqib .env

---

## 2026-04-29 18:30 — Jenkins Pipeline — BUILD SUCCESS ✅

ทำอะไร: restart Jenkins พร้อม env var ใหม่ แล้ว trigger build
ผล: ✅ Finished: SUCCESS — Checkout → Build → Test → SonarQube ผ่านครบ
ปัญหา: ไม่มี

---

## 2026-04-29 19:00 — Jenkins Dark Theme

ทำอะไร: ติดตั้ง dark-theme plugin ผ่าน jenkins-plugin-cli
ทำไม: UI default สว่างเกินไป
Command: docker exec raqib-jenkins jenkins-plugin-cli --plugins dark-theme
ผล: ✅ สำเร็จ — เปิดที่ Manage Jenkins → Appearance → Dark

---

## 2026-04-29 19:30 — Telegram Notify บน Jenkins Pipeline

ทำอะไร: เพิ่ม post section ใน Jenkinsfile ส่ง Telegram เมื่อ build success/failure
ทำไม: ต้องการแจ้งเตือนผล build ทันทีโดยไม่ต้องเปิด Jenkins
Config: ใช้ TELEGRAM_BOT_TOKEN + TELEGRAM_CHAT_ID จาก Raqib .env inject เข้า Jenkins container
ผล: ✅ Telegram แจ้งเตือน ✅ build #N passed / ❌ failed พร้อม BUILD_URL
ปัญหา: ไม่มี

---

## 2026-04-29 16:20 — ContentItem Entity + REST API

ทำอะไร: สร้าง ContentItem entity, Repository, Service, Controller, ExceptionHandler
ทำไม: Phase 1 — รับ URL เข้าระบบ save ลง DB status=PENDING
Endpoints: POST /api/v1/content, GET /api/v1/content/health
ผล: ✅ 201 Created, 409 Duplicate, 400 Validation ทำงานถูกต้อง
ปัญหา: DuplicateContentException return 500 เพราะไม่มี handler
แก้ยังไง: สร้าง GlobalExceptionHandler ด้วย @RestControllerAdvice return ProblemDetail (RFC 7807)

---
