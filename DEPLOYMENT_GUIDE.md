# Guía de Despliegue — Plataforma Educativa (Microservicios)

## Arquitectura

```
Cliente / Postman
       │
       ▼
 ┌─────────────┐  :8080
 │  API Gateway │  ← único punto de entrada
 └──────┬──────┘
        │ rutea por path
   ┌────┴──────────────┐
   │                   │
   ▼                   ▼                   ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────────┐
│course-service│ │student-service│ │enrollment-service │
│    :8081     │ │    :8082     │ │      :8083       │
└──────┬───────┘ └──────┬───────┘ └────────┬─────────┘
       │                │                  │ (llama a course y student)
       └────────────────┴──────────────────┘
                        │
                        ▼
              ┌──────────────────┐
              │  Oracle Cloud DB  │
              │ (Autonomous ATP)  │
              └──────────────────┘
```

### Puertos
| Servicio           | Puerto |
|--------------------|--------|
| api-gateway        | 8080   |
| course-service     | 8081   |
| student-service    | 8082   |
| enrollment-service | 8083   |

### Base de datos
Cada servicio tiene sus propias tablas en **la misma instancia** Oracle ATP:
- `COURSES`, `COURSE_SEQ` → course-service
- `STUDENTS`, `STUDENT_SEQ` → student-service
- `ENROLLMENTS`, `ENROLLMENT_ITEMS`, `ENROLLMENT_SEQ`, `ENROLLMENT_ITEM_SEQ` → enrollment-service

---

## Parte 1 — Oracle Cloud (Autonomous Database)

### 1.1 Crear la instancia ATP
1. [cloud.oracle.com](https://cloud.oracle.com) → **Oracle Database → Autonomous Transaction Processing**
2. **Create Autonomous Database**:
   - Display name: `EduPlatformDB`
   - Workload type: **Transaction Processing**
   - ECPU: 1 (capa gratuita)
   - Password: guárdala como `ORACLE_DB_PASSWORD`
3. Espera estado **Available**.

### 1.2 Descargar el Wallet
1. Instancia ATP → **DB Connection → Download Wallet**
2. Wallet type: **Instance Wallet**
3. Establece una contraseña y descarga `Wallet_EduPlatformDB.zip`
4. Extráelo localmente en `./wallet/`
5. Abre `wallet/tnsnames.ora` y anota el alias `_high` (ej: `eduplatformdb_high`) → será tu `ORACLE_TNS_ALIAS`

### 1.3 Ejecutar los schemas
Conéctate vía **SQL Developer Web** (Instancia ATP → Database Actions → SQL):

```sql
-- Ejecutar en este orden:

-- 1. course-service/src/main/resources/schema.sql
-- 2. course-service/src/main/resources/data.sql   (cursos demo)
-- 3. student-service/src/main/resources/schema.sql
-- 4. enrollment-service/src/main/resources/schema.sql
```

---

## Parte 2 — AWS EC2

### 2.1 Crear instancia
1. EC2 → **Launch Instance**
   - AMI: **Amazon Linux 2023**
   - Instance type: `t2.micro`
   - Key pair: crea `edu-platform-key.pem` y descárgalo
   - Security Group — agrega estas reglas de entrada:

| Tipo       | Puerto | Origen    |
|------------|--------|-----------|
| SSH        | 22     | 0.0.0.0/0 |
| Custom TCP | 8080   | 0.0.0.0/0 |
| Custom TCP | 8081   | 0.0.0.0/0 |
| Custom TCP | 8082   | 0.0.0.0/0 |
| Custom TCP | 8083   | 0.0.0.0/0 |

2. **Elastic IP** → Allocate → Associate a tu instancia
3. Guarda la IP pública (ej: `54.123.45.67`) → será tu `EC2_HOST`

### 2.2 Instalar Docker en EC2
```bash
ssh -i edu-platform-key.pem ec2-user@54.123.45.67

sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -aG docker ec2-user
exit

# Reconectar para que tome efecto el grupo
ssh -i edu-platform-key.pem ec2-user@54.123.45.67
docker ps   # debe funcionar sin sudo
```

### 2.3 Subir el Wallet a EC2
Desde tu máquina local:
```bash
scp -i edu-platform-key.pem -r ./wallet/ ec2-user@54.123.45.67:/home/ec2-user/wallet
```

Verifica en EC2:
```bash
ls /home/ec2-user/wallet
# Debe mostrar: cwallet.sso  ewallet.p12  keystore.jks  ojdbc.properties  sqlnet.ora  tnsnames.ora  truststore.jks
```

---

## Parte 3 — Docker Hub

1. Crea cuenta en [hub.docker.com](https://hub.docker.com)
2. **Account Settings → Security → New Access Token**
   - Nombre: `edu-platform-token`
   - Permisos: Read, Write, Delete
3. Guarda el token generado → será `DOCKERHUB_TOKEN`

---

## Parte 4 — GitHub Secrets

Repositorio → **Settings → Secrets and variables → Actions → New repository secret**:

| Secret | Valor |
|--------|-------|
| `DOCKERHUB_USERNAME` | Tu usuario Docker Hub |
| `DOCKERHUB_TOKEN` | Token generado en Parte 3 |
| `EC2_HOST` | IP Elástica de tu EC2 (ej: `54.123.45.67`) |
| `USER_SERVER` | `ec2-user` |
| `EC2_SSH_KEY` | Contenido completo del archivo `.pem` (incluye `-----BEGIN RSA PRIVATE KEY-----`) |
| `ORACLE_TNS_ALIAS` | Alias del tnsnames.ora (ej: `eduplatformdb_high`) |
| `ORACLE_DB_USER` | `ADMIN` |
| `ORACLE_DB_PASSWORD` | Password de Oracle |

---

## Parte 5 — Primer despliegue manual (bootstrap)

Antes del primer push automático, levanta los servicios manualmente en EC2 para verificar que todo funciona:

```bash
ssh -i edu-platform-key.pem ec2-user@54.123.45.67

# Levantar course-service
docker run -d --name course-service --restart unless-stopped \
  -p 8081:8081 \
  -v /home/ec2-user/wallet:/app/wallet \
  -e SPRING_DATASOURCE_URL="jdbc:oracle:thin:@eduplatformdb_high?TNS_ADMIN=/app/wallet" \
  -e SPRING_DATASOURCE_USERNAME="ADMIN" \
  -e SPRING_DATASOURCE_PASSWORD="TU_PASSWORD" \
  TU_USUARIO/course-service:latest

# Levantar student-service
docker run -d --name student-service --restart unless-stopped \
  -p 8082:8082 \
  -v /home/ec2-user/wallet:/app/wallet \
  -e SPRING_DATASOURCE_URL="jdbc:oracle:thin:@eduplatformdb_high?TNS_ADMIN=/app/wallet" \
  -e SPRING_DATASOURCE_USERNAME="ADMIN" \
  -e SPRING_DATASOURCE_PASSWORD="TU_PASSWORD" \
  TU_USUARIO/student-service:latest

# Levantar enrollment-service (network host para que acceda a los otros)
docker run -d --name enrollment-service --restart unless-stopped \
  -p 8083:8083 \
  --network host \
  -v /home/ec2-user/wallet:/app/wallet \
  -e SPRING_DATASOURCE_URL="jdbc:oracle:thin:@eduplatformdb_high?TNS_ADMIN=/app/wallet" \
  -e SPRING_DATASOURCE_USERNAME="ADMIN" \
  -e SPRING_DATASOURCE_PASSWORD="TU_PASSWORD" \
  -e SERVICES_COURSE-SERVICE_URL="http://localhost:8081" \
  -e SERVICES_STUDENT-SERVICE_URL="http://localhost:8082" \
  TU_USUARIO/enrollment-service:latest

# Levantar api-gateway (network host)
docker run -d --name api-gateway --restart unless-stopped \
  -p 8080:8080 \
  --network host \
  -e SERVICES_COURSE-SERVICE_URL="http://localhost:8081" \
  -e SERVICES_STUDENT-SERVICE_URL="http://localhost:8082" \
  -e SERVICES_ENROLLMENT-SERVICE_URL="http://localhost:8083" \
  TU_USUARIO/api-gateway:latest
```

Verifica que todos están corriendo:
```bash
docker ps
# Debe mostrar los 4 contenedores: api-gateway, course-service, student-service, enrollment-service
```

---

## Parte 6 — Despliegue automático (CI/CD)

El repositorio tiene **4 workflows independientes**, uno por servicio:

| Workflow | Se activa cuando cambia |
|----------|------------------------|
| `api-gateway.yml` | `api-gateway/**` |
| `course-service.yml` | `course-service/**` |
| `student-service.yml` | `student-service/**` |
| `enrollment-service.yml` | `enrollment-service/**` |

Cada workflow ejecuta: **test → build Docker → push Docker Hub → deploy SSH en EC2**.

```bash
# Activar pipeline (modifica cualquier archivo del servicio y haz push)
git add .
git commit -m "feat: initial microservices deployment"
git push origin main
```

Monitorea en: `https://github.com/TU_USUARIO/TU_REPO/actions`

---

## Parte 7 — Endpoints disponibles

Todos los requests van al **API Gateway en el puerto 8080**. El gateway los rutea internamente.

### Cursos (`/api/courses`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/courses` | Listar cursos disponibles |
| GET | `/api/courses/{id}` | Obtener curso por ID |
| GET | `/api/courses/category/{cat}` | Filtrar por categoría |
| GET | `/api/courses/search?keyword=X` | Buscar por nombre |
| GET | `/api/courses/price-range?min=X&max=Y` | Filtrar por precio |
| GET | `/api/courses/categories` | Listar categorías |
| POST | `/api/courses` | Crear nuevo curso |
| PATCH | `/api/courses/{id}/toggle-availability` | Activar/desactivar |

### Estudiantes (`/api/students`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/students` | Listar estudiantes activos |
| GET | `/api/students/{id}` | Obtener por ID |
| GET | `/api/students/email?email=X` | Obtener por email |
| GET | `/api/students/search?name=X` | Buscar por nombre |
| POST | `/api/students` | Registrar estudiante |
| PUT | `/api/students/{id}` | Actualizar datos |
| PATCH | `/api/students/{id}/deactivate` | Desactivar |

### Inscripciones (`/api/enrollments`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/enrollments` | Inscribir estudiante (genera boleta) |
| GET | `/api/enrollments/{id}` | Obtener inscripción por ID |
| GET | `/api/enrollments/student/{id}` | Historial por ID de estudiante |
| GET | `/api/enrollments/student/email?email=X` | Historial por email |
| PATCH | `/api/enrollments/{id}/cancel` | Cancelar inscripción |

### Swagger UI (por servicio, acceso directo)
| Servicio | URL |
|----------|-----|
| course-service | `http://IP:8081/swagger-ui.html` |
| student-service | `http://IP:8082/swagger-ui.html` |
| enrollment-service | `http://IP:8083/swagger-ui.html` |

---

## Parte 8 — Ejemplos de uso con Postman

### 1. Crear estudiante
```json
POST http://54.123.45.67:8080/api/students
{
  "name": "María López",
  "email": "maria@test.com",
  "phone": "+56912345678",
  "bio": "Desarrolladora frontend con 2 años de experiencia"
}
```

### 2. Ver cursos disponibles
```
GET http://54.123.45.67:8080/api/courses
```

### 3. Crear inscripción con descuento
```json
POST http://54.123.45.67:8080/api/enrollments
{
  "studentId": 1,
  "courseIds": [1, 2, 3],
  "discountCode": "BIENVENIDO10"
}
```

Respuesta incluye boleta completa con subtotal, descuento y total a pagar.

---

## Funcionalidades extra implementadas

| # | Funcionalidad | Dónde |
|---|---------------|-------|
| 1 | Descuento por código promocional (`BIENVENIDO10`, `VERANO20`, `PROMO15`, `ESTUDIANTE5`) | enrollment-service |
| 2 | Descuento automático por volumen (2 cursos → 5%, 3+ → 10%) | enrollment-service |
| 3 | Validación anti-duplicados en la misma inscripción | enrollment-service |
| 4 | Cancelación de inscripción + historial por email/ID | enrollment-service |

---

## Comandos útiles en EC2

```bash
# Ver logs de un servicio
docker logs course-service -f

# Reiniciar un servicio
docker restart enrollment-service

# Ver estado de todos los contenedores
docker ps -a

# Ver uso de recursos
docker stats

# Eliminar imágenes antiguas
docker image prune -f
```
