# Acceso a Swagger UI – Microservicios TrainingNow

Este documento recoge las URLs de Swagger y los datos necesarios para probar la API de cada microservicio.

---

## URLs de Swagger por microservicio

| Microservicio      | Puerto | Swagger UI                          | OpenAPI JSON              |
|--------------------|--------|-------------------------------------|---------------------------|
| **tn-usuarios**    | 8081   | http://localhost:8081/swagger-ui/index.html | http://localhost:8081/v3/api-docs |
| **tn-biblioteca**  | 8082   | http://localhost:8082/swagger-ui/index.html | http://localhost:8082/v3/api-docs |
| **tn-rutinas**     | 8083   | http://localhost:8083/swagger-ui/index.html | http://localhost:8083/v3/api-docs |
| **tn-comunicaciones** | 8084 | http://localhost:8084/swagger-ui/index.html | http://localhost:8084/v3/api-docs |

> **Nota:** Swagger UI y `/v3/api-docs` están en **permitAll** en tn-usuarios. No hace falta token para abrir la documentación.

---

## tn-usuarios – Autenticación JWT

Este es el microservicio de identidad. Aquí obtienes el token JWT para usarlo en endpoints protegidos (y, si aplica, en otros microservicios).

### 1. Obtener token (login)

**Opción A – Desde Swagger UI**

1. Abre: **http://localhost:8081/swagger-ui/index.html**
2. Localiza **POST /api/auth/login** (tag *Identidad y Acceso*).
3. Pulsa **Try it out**.
4. Body de ejemplo:

```json
{
  "email": "tu_email@trainingnow.com",
  "password": "TuContraseña123"
}
```

5. Pulsa **Execute**.
6. En la respuesta **200** copia el valor de `token` (sin comillas).

**Opción B – Registrar usuario primero**

Si aún no tienes usuario:

1. En Swagger, **POST /api/auth/register**.
2. Body de ejemplo:

```json
{
  "email": "nuevo@trainingnow.com",
  "password": "MiClaveSegura123",
  "nombre": "Ana",
  "apellidos": "García",
  "rol": "CLIENT",
  "telefono": "+34 612 345 678",
  "fechaNacimiento": "1995-03-20",
  "genero": "FEMENINO",
  "pesoActual": 65.0,
  "altura": 165,
  "objetivo": "PERDIDA_PESO"
}
```

3. La respuesta **201** incluye el `token`. Cópialo.

### 2. Autorizar en Swagger (tn-usuarios)

1. En la misma página de Swagger, pulsa el botón **Authorize** (candado).
2. En **bearerAuth** pega solo el token (sin escribir "Bearer").
3. Pulsa **Authorize** y luego **Close**.
4. A partir de ahí, las llamadas a **GET /api/users/me** (y otros protegidos) irán con el token.

### 3. Usuarios de prueba (creados por UsuariosDataLoader)

| Rol    | Email                  | Contraseña  | Uso en Swagger |
|--------|------------------------|-------------|-----------------|
| **Admin**  | `admin@trainingnow.com`  | `admin123`  | Login → Authorize → Probar GET /api/users/me |
| **Client** | `client@trainingnow.com`  | `client123` | Atleta de prueba con datos biométricos completos |

### 4. Valores de ejemplo para registro (Swagger)

| Campo        | Ejemplo              |
|-------------|----------------------|
| **email**   | `usuario@trainingnow.com` |
| **password**| `MiClaveSegura123`   |
| **nombre**  | `María`              |
| **apellidos** | `García López`     |
| **rol**     | `CLIENT` / `TRAINER` / `ADMIN` |
| **genero**  | `MASCULINO` / `FEMENINO` / `OTRO` |
| **objetivo**| `PERDIDA_PESO` / `GANAR_MUSCULO` / `MANTENER` / `DEFINICION` / `RESISTENCIA` / `OTRO` |
| **pesoActual** | `80.5` (kg)       |
| **altura**  | `180` (cm)           |
| **telefono**| `+34 612 345 678`    |
| **fechaNacimiento** | `1990-05-15` |

---

## Resumen rápido

1. **Solo ver la documentación:**  
   Abre la URL de Swagger UI del microservicio (tabla de arriba). En tn-usuarios no necesitas token para eso.

2. **Probar endpoints protegidos en tn-usuarios:**  
   - Haz **POST /api/auth/login** (o **register**) en Swagger.  
   - Copia el `token` de la respuesta.  
   - Pulsa **Authorize**, pega el token en bearerAuth, **Authorize** y **Close**.  
   - Prueba **GET /api/users/me** y el resto de operaciones.

3. **Otros microservicios (biblioteca, rutinas, comunicaciones):**  
   Si en el futuro exigen el mismo JWT de tn-usuarios, usa el mismo token en **Authorize** de su Swagger (o en la cabecera `Authorization: Bearer <token>`).

---

## Requisitos

- Tener el microservicio en marcha (por ejemplo: `mvn spring-boot:run` en su carpeta).
- Para tn-usuarios con perfil `dev`: no hace falta MySQL; usa H2 en memoria.
