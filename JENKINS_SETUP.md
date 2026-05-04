# Jenkins CI/CD Setup — Rawi

คู่มือนี้อธิบายทุกขั้นตอนที่ทำเพื่อตั้งค่า Jenkins ให้ build และ test โปรเจค Rawi โดยอัตโนมัติ
เหมาะสำหรับคนที่ไม่เคย deploy Jenkins มาก่อน อ่านครั้งเดียวแล้วเข้าใจได้เลย

---

## ภาพรวม — ทำอะไร และทำทำไม

Jenkins คือ CI/CD server — ทุกครั้งที่มีการ trigger (ปัจจุบัน manual, อนาคต GitHub Webhook) Jenkins จะ:

```
1. Checkout โค้ดจาก /var/rawi-source (mount จาก Desktop/Rawi)
2. Build โปรเจคด้วย Maven
3. Run Tests
4. ส่งโค้ดไป SonarQube วิเคราะห์ code quality
5. แจ้งผลทาง Telegram ทันที (✅ หรือ ❌)
```

**ทำไมต้องมี Jenkins?**
- ป้องกัน deploy โค้ดที่ build ไม่ผ่านหรือ test fail
- เห็น code quality ผ่าน SonarQube ก่อน merge
- แจ้งเตือน Telegram ทันทีโดยไม่ต้องคอยดู

---

## Architecture

```
GitHub (taskeendev/rawi)
        │
        │ (mount read-only)
        ▼
Desktop/Rawi ──────────────────────────────────────┐
                                                    │
raqib network:                                      │
┌─────────────────┐     ┌──────────────────┐        │
│  raqib-jenkins  │────▶│ raqib-sonarqube  │        │
│  port 8082      │     │ port 9002         │        │
│  JDK 21         │     │ lts-community     │        │
└────────┬────────┘     └──────────────────┘        │
         │                                          │
         │ connects to                              │
         ▼                                          │
┌─────────────────┐                                 │
│  rawi-postgres  │◀──────────────────────────────── │
│  port 5435      │   (shared DB สำหรับ test)
└─────────────────┘
         │
         ▼
    Telegram Bot
  (แจ้งผล build)
```

---

## ขั้นตอนที่ 1 — เพิ่ม Jenkins และ SonarQube ใน Raqib

**ไฟล์:** `Desktop/Raqib/docker-compose.yml`

เพิ่ม 2 services และ 4 volumes:

```yaml
volumes:
  jenkins_home:        # เก็บ config, plugins, build history ของ Jenkins
  sonarqube_data:      # เก็บ projects, analyses
  sonarqube_logs:
  sonarqube_extensions:

services:
  jenkins:
    image: jenkins/jenkins:lts-jdk21   # LTS = stable, jdk21 = ต้องการ Java 21
    container_name: raqib-jenkins
    restart: unless-stopped
    ports:
      - "8082:8080"      # expose ที่ port 8082 (8080 ถูกใช้โดย service อื่น)
      - "50000:50000"    # Jenkins agent port
    environment:
      JAVA_OPTS: "-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true"
      # ↑ อนุญาตให้ checkout จาก local path (file:///var/rawi-source)
      DB_PASSWORD: ${RAWI_DB_PASSWORD}
      # ↑ ส่ง password ให้ Spring Boot ใช้ตอน run tests
      TELEGRAM_BOT_TOKEN: ${TELEGRAM_BOT_TOKEN}
      TELEGRAM_CHAT_ID: ${TELEGRAM_CHAT_ID}
      # ↑ ส่ง Telegram credentials สำหรับ notify ผล build
    volumes:
      - jenkins_home:/var/jenkins_home
      - /Users/taskeen/Desktop/Rawi:/var/rawi-source:ro
      # ↑ mount source code เข้า Jenkins (read-only)
    networks:
      - raqib

  sonarqube:
    image: sonarqube:lts-community
    container_name: raqib-sonarqube
    restart: unless-stopped
    ports:
      - "9002:9000"
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_logs:/opt/sonarqube/logs
      - sonarqube_extensions:/opt/sonarqube/extensions
    ulimits:
      nofile:
        soft: 65536
        hard: 65536      # SonarQube ต้องการ file descriptor limit สูง
    networks:
      - raqib
```

**เพิ่มใน Raqib `.env`:**
```
RAWI_DB_PASSWORD=rawi2026
```

**รัน:**
```bash
cd Desktop/Raqib
docker compose up -d jenkins sonarqube
```

---

## ขั้นตอนที่ 2 — Jenkins First-Time Setup (ทำใน UI)

เปิด http://localhost:8082

### 2.1 Unlock Jenkins
Jenkins สร้าง initial admin password อัตโนมัติ — ดูได้จาก:
```bash
docker exec raqib-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
วางรหัสนี้ในหน้า Unlock แล้วกด Continue

### 2.2 Install Suggested Plugins
เลือก **"Install suggested plugins"** — รอจนเสร็จ (ประมาณ 3-5 นาที)

### 2.3 สร้าง Admin User
กรอก username / password / email แล้ว Save

### 2.4 ติดตั้ง Dark Theme (optional)
```bash
docker exec raqib-jenkins jenkins-plugin-cli --plugins dark-theme
docker restart raqib-jenkins
```
จากนั้นไปที่ **Manage Jenkins → Appearance → Dark → Save**

---

## ขั้นตอนที่ 3 — ตั้งค่า SonarQube (ทำใน UI)

เปิด http://localhost:9002 — default: `admin` / `admin`

### 3.1 สร้าง Project
- **Create Project → Manually**
- Project key: `rawi`
- Display name: `Rawi`

### 3.2 สร้าง Token สำหรับ Jenkins
- **My Account → Security → Generate Token**
- ชื่อ: `jenkins-rawi`
- คัดลอก token ไว้ใช้ใน Step 4

### 3.3 ตั้งค่า SonarQube ใน Jenkins
ไปที่ **Manage Jenkins → System → SonarQube servers:**
- Name: `Rawi-SonarQube`
- URL: `http://raqib-sonarqube:9000`
- Token: ใส่ token จาก 3.2

---

## ขั้นตอนที่ 4 — ตั้งค่า Maven และ JDK ใน Jenkins

ไปที่ **Manage Jenkins → Tools:**

### JDK
- กด **Add JDK**
- Name: `JDK21`
- ติ๊ก **Install automatically** → เลือก JDK 21

### Maven
- กด **Add Maven**
- Name: `Maven`
- ติ๊ก **Install automatically** → เลือก version ล่าสุด

---

## ขั้นตอนที่ 5 — สร้าง Pipeline Job

ไปที่ **New Item:**
- ชื่อ: `rawi`
- เลือก **Pipeline**
- กด OK

ใน **Pipeline section:**
- Definition: `Pipeline script from SCM`
- SCM: `Git`
- Repository URL: `file:///var/rawi-source`
- Branch: `*/phase/1-content-service`
- Script Path: `Jenkinsfile`

กด **Save**

---

## ขั้นตอนที่ 6 — Jenkinsfile

**ไฟล์:** `Desktop/Rawi/Jenkinsfile`

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK21'
    }

    environment {
        SONAR_PROJECT_KEY = 'rawi'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                // ดึงโค้ดจาก /var/rawi-source (mount จาก Desktop/Rawi)
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                // compile + package ข้ามขั้นตอน test ก่อน (test แยก stage)
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -Dspring.profiles.active=ci'
                // ใช้ profile "ci" → application-ci.properties
                // ซึ่งชี้ DB ไปที่ rawi-postgres:5432 แทน localhost:5435
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('Rawi-SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}'
                }
            }
        }
    }

    post {
        success {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                    -d chat_id="${TELEGRAM_CHAT_ID}" \
                    -d text="✅ Rawi build #${BUILD_NUMBER} passed"
            '''
        }
        failure {
            sh '''
                curl -s -X POST "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage" \
                    -d chat_id="${TELEGRAM_CHAT_ID}" \
                    -d text="❌ Rawi build #${BUILD_NUMBER} failed — ${BUILD_URL}"
            '''
        }
    }
}
```

---

## ขั้นตอนที่ 7 — แก้ปัญหา Test ต่อ DB ไม่ได้

**ปัญหา:** Jenkins run tests แล้ว fail เพราะ `application.properties` ใช้ `localhost:5435`
แต่ Jenkins container ไม่มี postgres อยู่ใน localhost

**วิธีแก้:**

### 7.1 เชื่อม rawi-postgres เข้า raqib network
**ไฟล์:** `Desktop/Rawi/docker-compose.yml`
```yaml
networks:
  raqib:
    external: true

services:
  postgres:
    # ... config เดิม ...
    networks:
      - default
      - raqib    # ← เพิ่มบรรทัดนี้
```

```bash
cd Desktop/Rawi
docker compose up -d postgres   # recreate ให้เข้า network ใหม่
```

### 7.2 สร้าง application-ci.properties
**ไฟล์:** `src/main/resources/application-ci.properties`
```properties
spring.datasource.url=jdbc:postgresql://rawi-postgres:5432/rawi_db
spring.datasource.username=rawi
spring.datasource.password=${DB_PASSWORD}
```
Profile `ci` override datasource URL ให้ชี้ไปที่ container name แทน localhost

---

## ปัญหาที่เจอและวิธีแก้

| ปัญหา | สาเหตุ | วิธีแก้ |
|---|---|---|
| Test fail: `Connection to localhost:5435 refused` | Jenkins ไม่มี postgres ใน localhost | เชื่อม rawi-postgres เข้า raqib network + สร้าง application-ci.properties |
| Test fail: `password authentication failed` | Jenkins ไม่มี `DB_PASSWORD` env var | เพิ่ม `DB_PASSWORD: ${RAWI_DB_PASSWORD}` ใน Jenkins environment ใน docker-compose |
| Plugin ไม่มีใน Available list | Update center ยังไม่ refresh | กด "Check now" ใน Plugin Manager |
| dark-theme หาไม่เจอ | UI search ไม่เจอ | ติดตั้งผ่าน CLI: `jenkins-plugin-cli --plugins dark-theme` |

---

## Credentials สรุป

| Service | URL | Username | Password |
|---|---|---|---|
| Jenkins | http://localhost:8082 | taskeen | rawi2026 |
| SonarQube | http://localhost:9002 | admin | admin |

---

## สิ่งที่ยังไม่ได้ทำ (Backlog)

- [ ] **GitHub Webhook** — auto trigger build เมื่อ push (รอ Cloudflare Tunnel — Phase 4)
- [ ] เปลี่ยน SonarQube password จาก default `admin/admin`
