# HISTORY.md — Engineering Log

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

## 2026-04-29 16:20 — ContentItem Entity + REST API

ทำอะไร: สร้าง ContentItem entity, Repository, Service, Controller, ExceptionHandler
ทำไม: Phase 1 — รับ URL เข้าระบบ save ลง DB status=PENDING
Endpoints: POST /api/v1/content, GET /api/v1/content/health
ผล: ✅ 201 Created, 409 Duplicate, 400 Validation ทำงานถูกต้อง
ปัญหา: DuplicateContentException return 500 เพราะไม่มี handler
แก้ยังไง: สร้าง GlobalExceptionHandler ด้วย @RestControllerAdvice return ProblemDetail (RFC 7807)

---
