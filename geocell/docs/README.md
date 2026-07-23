# GeoCell — Documentação Técnica

## Índice

1. [Visão Geral](#1-visão-geral)
2. [Stack Tecnológica](#2-stack-tecnológica)
3. [Arquitectura](#3-arquitectura)
4. [Base de Dados](#4-base-de-dados)
5. [Segurança e Autenticação](#5-segurança-e-autenticação)
6. [Endpoints da API](#6-endpoints-da-api)
7. [Services](#7-services)
8. [Deployment e Configuração](#8-deployment-e-configuração)
9. [Importação de Dados CSV](#9-importação-de-dados-csv)

---

## 1. Visão Geral

O **GeoCell** é uma API REST para gestão e pesquisa geoespacial de células de redes móveis (2G/3G/4G/5G/NB-IoT). Permite:

- Consultar células por CGI/ECGI/NCGI.
- Pesquisar células por proximidade geográfica (raio a partir de uma célula de referência, raio a partir de coordenadas, ou rectângulo delimitador).
- Importar grandes volumes de dados via CSV.
- Gerir utilizadores e departamentos com controlo de permissões.
- Gerar polígonos de cobertura de célula (sector antenna coverage) em WKT/GeoJSON.

A API é orientada a analistas de redes e sistemas de informação geográfica (SIG).

---

## 2. Stack Tecnológica

| Componente         | Tecnologia                                        |
|--------------------|---------------------------------------------------|
| Linguagem          | Kotlin 2.3 (JVM 25)                               |
| Framework          | Spring Boot 4.1 (Web MVC, Security, Data JPA)     |
| Base de Dados      | PostgreSQL + PostGIS                              |
| Acesso a dados     | Spring JDBC (`JdbcTemplate`) — sem ORM para queries geoespaciais |
| Build              | Gradle (Kotlin DSL)                               |
| CSV                | Apache Commons CSV 1.14                           |
| Serialização JSON  | Jackson + `jackson-module-kotlin`                 |
| Autenticação       | Session cookie stateless (custom filter)          |

---

## 3. Arquitectura

```
┌─────────────────────────────────────────────┐
│                  HTTP Client                │
└───────────────────┬─────────────────────────┘
                    │  HTTPS
┌───────────────────▼─────────────────────────┐
│          Spring Web MVC (Controllers)       │
│  CellController  UserController  AuthCtrl   │
│  DepartmentController                       │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│                Services                     │
│  CellService  UserService  AuthSessionService│
│  DepartmentService                          │
└───────────────────┬─────────────────────────┘
                    │
┌───────────────────▼─────────────────────────┐
│              Repositories                   │
│  JdbcCellRepository  JdbcUserRepository     │
│  JdbcDepartmentRepository  AuthSessionRepo  │
└───────────────────┬─────────────────────────┘
                    │  JDBC
┌───────────────────▼─────────────────────────┐
│       PostgreSQL 15+ com PostGIS 3.x        │
└─────────────────────────────────────────────┘
```

### Camadas

| Camada         | Pacote                         | Responsabilidade                                                   |
|----------------|--------------------------------|--------------------------------------------------------------------|
| Controller     | `controller/`                  | Receber pedidos HTTP, validar parâmetros simples, delegar ao service |
| Service        | `service/`                     | Lógica de negócio, validações, orquestração de repositórios       |
| Repository     | `repository/`                  | Queries SQL via `JdbcTemplate`; interface + implementação JDBC     |
| Domain         | `domain/`                      | Records de leitura/escrita internos (não expostos ao cliente)      |
| DTO            | `dto/request/`, `dto/response/`| Contratos da API (serialização Jackson)                            |
| Validator      | `validator/`                   | Validação de requests complexos (reutilizáveis)                    |
| Security       | `security/`, `config/`         | Filtro de autenticação por cookie de sessão                        |

### Perfis Spring

- `!test` — activo em runtime normal; carrega Security, Services, Repositories.
- `test` — activo nos testes unitários; não carrega beans de infraestrutura.

---

## 4. Base de Dados

### Extensões necessárias

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### Diagrama simplificado de tabelas

```
department ◄── user_department ──► auth_user ──► session
                                        │
                                  active_user_session

country ◄── district ◄── county ◄── location
                                        │
                                     enbgnb ──► cell ──► cell_polygon
                                                 │
                                    mccmnc ──────┘
                                    band ─────────┘
```

### Tabelas principais

#### Utilizadores e Sessões

| Tabela                | Descrição                                                                 |
|-----------------------|---------------------------------------------------------------------------|
| `department`          | Departamentos; flag `have_operations` controla acesso a operações.        |
| `auth_user`           | Utilizadores com perfis: `is_superuser`, `is_analyst`, `is_operation_admin`. |
| `user_department`     | Relação N:M utilizador ↔ departamento.                                    |
| `session`             | Sessões HTTP stateless (chave + dados + expiração).                       |
| `active_user_session` | Sessão activa por utilizador; regista IP, user-agent, heartbeat SSE.      |
| `admin_log`           | Registo de acções administrativas.                                        |

#### Geolocalização

| Tabela               | Descrição                                                                  |
|----------------------|----------------------------------------------------------------------------|
| `country`            | Países com polígono de fronteira (EPSG:4326).                              |
| `district`           | Distritos com polígono.                                                    |
| `county`             | Concelhos com polígono.                                                    |
| `location`           | Ponto geográfico (`GEOMETRY(Point,4326)`) + morada postal.                 |
| `caop_23p`           | Carta Administrativa Oficial de Portugal 2023 (freguesias, MultiPolygon).  |
| `portugal_grid_500m` | Grelha de 500 m sobre Portugal (centro + bbox por célula de grelha).       |

#### Células de Rede

| Tabela         | Descrição                                                                              |
|----------------|----------------------------------------------------------------------------------------|
| `mccmnc`       | Operadores móveis (MCC + MNC + marca + país). Índice único em `(mcc, mnc)`.           |
| `enbgnb`       | eNodeB / gNodeB identificado por número inteiro + localização.                         |
| `band`         | Banda de frequência: banda, largura, uplink/downlink freq, EARFCN.                     |
| `cell`         | Célula de rede — CGI, tecnologia (2/3/4/5/10), azimute, referências a `location`, `mccmnc`, `band`, `enbgnb`. |
| `cell_polygon` | Polígono 3D de cobertura da célula (`GEOGRAPHY(PolygonZ,4326)`): `polygon` (full, 500 m) e `polygon_short` (curto, variável por tecnologia). |

#### Restrições e índices notáveis

- `cell.cgi` — formato validado por CHECK: `MCC-MNC-ECI` (4G/5G) ou `MCC-MNC-LAC-CI` (2G/3G).
- `cell.technology` — IN (2, 3, 4, 5, 10) onde 10 = NB-IoT.
- Índices GIST em `location.coordinates`, `cell_polygon.polygon`, `cell_polygon.polygon_short` para queries espaciais eficientes.
- `UNIQUE (cgi, paragon_cgi, azimuth)` em `cell`.

#### Tabelas de Operações e Análise

| Tabela                        | Descrição                                                       |
|-------------------------------|-----------------------------------------------------------------|
| `operation`                   | Operação de campo associada a departamento.                     |
| `operation_target`            | Alvo de uma operação.                                           |
| `operation_cell`              | Células associadas a um alvo.                                   |
| `operation_poi`               | Pontos de interesse de uma operação.                            |
| `operation_user_cell`         | Histórico de associação utilizador ↔ célula em operação.        |
| `user_activity`               | Última actividade do utilizador (path, IP, timestamp).          |
| `user_location`               | Localização em tempo real do utilizador (ponto + timestamp).    |
| `combain_cache`               | Cache de respostas da API Combain de geolocalização.            |
| `google_geolocation_cache`    | Cache de respostas da API Google Geolocation.                   |
| `chronological_path_analysis` | Análise de percurso cronológico por utilizador.                 |

### Triggers

| Trigger                              | Tabela                        | Acção                              |
|--------------------------------------|-------------------------------|------------------------------------|
| `update_user_location_timestamp`     | `user_location`               | Actualiza `timestamp` no UPDATE.   |
| `update_combain_cache_updated_at`    | `combain_cache`               | Actualiza `updated_at` no UPDATE.  |
| `update_google_geocache_updated_at`  | `google_geolocation_cache`    | Actualiza `updated_at` no UPDATE.  |
| `update_chronological_path_analysis_updated_at` | `chronological_path_analysis` | Actualiza `updated_at` no UPDATE. |
| `update_cell_updated_at`             | `cell`                        | Actualiza `updated_at` no UPDATE (só se não foi alterado manualmente). |

---

## 5. Segurança e Autenticação

### Mecanismo

A autenticação é **stateless por cookie de sessão**:

1. O cliente faz `POST /api/auth/login` com `username`/`password`.
2. A resposta devolve um cookie `session_key` (HTTP-only).
3. Todos os pedidos subsequentes enviam automaticamente esse cookie.
4. O `SessionCookieAuthenticationFilter` intercepta cada pedido, lê o cookie, valida a sessão na BD e popula o `SecurityContextHolder`.

### Permissões

| Rota                     | Requisito                 |
|--------------------------|---------------------------|
| `POST /api/auth/login`   | Público                   |
| Todas as outras rotas    | Utilizador autenticado    |
| Gestão de utilizadores   | `is_superuser = true`     |
| Gestão de departamentos  | `is_superuser = true`     |
| Operações de escrita em cells | Utilizador autenticado |

### CSRF e Sessão HTTP

- CSRF desactivado (API REST stateless).
- HTTP Basic e form login desactivados.
- `SessionCreationPolicy.STATELESS` — o Spring não cria sessão HTTP própria.

---

## 6. Endpoints da API

A especificação completa está em [`openapi.yaml`](./openapi.yaml).

### Auth — `/api/auth`

| Método | Rota             | Descrição                          | Auth |
|--------|------------------|------------------------------------|------|
| POST   | `/login`         | Autenticar e obter cookie de sessão | ❌   |
| POST   | `/logout`        | Terminar sessão                     | ✅   |
| GET    | `/me`            | Obter utilizador autenticado        | ✅   |

### Departments — `/api/departments`

| Método | Rota    | Descrição           | Perfil necessário |
|--------|---------|---------------------|-------------------|
| GET    | `/`     | Listar departamentos | superuser         |
| POST   | `/`     | Criar departamento   | superuser         |
| PUT    | `/{id}` | Actualizar departamento | superuser      |
| DELETE | `/{id}` | Remover departamento sem users nem operations associadas | superuser |

### Users — `/api/users`

| Método | Rota     | Descrição            | Perfil necessário |
|--------|----------|----------------------|-------------------|
| GET    | `/`      | Listar utilizadores  | superuser         |
| POST   | `/`      | Criar utilizador     | superuser         |
| GET    | `/{id}`  | Obter utilizador     | superuser         |
| PUT    | `/{id}`  | Actualizar utilizador | superuser        |
| DELETE | `/{id}`  | Remover utilizador    | superuser        |

#### Regras adicionais de administração

- `PUT /api/users/{id}` aceita `password` opcional; se omitida, a password actual é mantida.
- `DELETE /api/users/{id}` remove o utilizador e limpa referências administrativas e sessões associadas antes do apagamento final.
- `DELETE /api/departments/{id}` devolve conflito (`409`) se existirem utilizadores atribuídos ao departamento ou operações ligadas ao mesmo.

### Cells — `/api/cells`

| Método | Rota                   | Descrição                                                  | Auth |
|--------|------------------------|------------------------------------------------------------|------|
| GET    | `/?cgi=`               | Pesquisar por CGI/ECGI/NCGI ou paragon CGI                  | ✅   |
| POST   | `/`                    | Criar nova célula (com polígono gerado automaticamente)    | ✅   |
| PUT    | `/{id}`                | Actualizar célula existente                                | ✅   |
| DELETE | `/{id}`                | Remover célula                                             | ✅   |
| POST   | `/import`              | Importar células via ficheiro CSV (multipart)              | ✅   |
| GET    | `/districts`           | Listar distritos de um país (`?country=`)                  | ✅   |
| GET    | `/counties`            | Listar concelhos de um distrito (`?districtId=`)           | ✅   |
| GET    | `/nearby`              | Células num raio em torno de uma célula por CGI            | ✅   |
| GET    | `/search/circle`       | Células num círculo por coordenadas + raio                 | ✅   |
| GET    | `/search/bbox`         | Células num rectângulo definido por dois pontos            | ✅   |
| GET    | `/search/county`       | Células por distrito/concelho + filtros de rede/tecnologia | ✅   |
| GET    | `/search/lac-tac`      | Células por MCC/MNC/LAC-TAC                                | ✅   |
| GET    | `/search/lac-tac/polygon` | Polygon de cobertura por MCC/MNC/LAC-TAC               | ✅   |
| GET    | `/search/enb-gnb`      | Células por eNB/gNB                                        | ✅   |

#### `GET /api/cells/districts`

Lista os distritos de um país.

| Parâmetro | Tipo   | Obrig. | Descrição        |
|-----------|--------|--------|------------------|
| `country` | string | ✅     | Nome do país     |

**Resposta:** `DistrictResponse[]`

#### `GET /api/cells/counties`

Lista os concelhos de um distrito.

| Parâmetro    | Tipo   | Obrig. | Descrição          |
|--------------|--------|--------|--------------------|
| `districtId` | string | ✅     | ID do distrito     |

**Resposta:** `CountyResponse[]`

#### `GET /api/cells/nearby`

Pesquisa células em redor de uma **célula de referência** (identificada por CGI). Devolve o polígono completo da célula central e polígonos curtos das células no raio.

| Parâmetro       | Tipo     | Obrig. | Descrição                                             |
|-----------------|----------|--------|-------------------------------------------------------|
| `cgi`           | string   | ✅     | CGI/ECGI/NCGI da célula central                       |
| `radiusKm`      | double   | ✅     | Raio em km (> 0)                                      |
| `sameNetwork`   | string   | ❌     | `1`/`true` — filtrar pelo mesmo MNC da célula central |
| `techGeneration`| string[] | ❌     | `2G`, `3G`, `4G`, `5G`, `NB-IoT`                     |

**Resposta:** `NearbyCellsResponse { centralCell, cellsInRadius[] }`

#### `GET /api/cells/search/circle`

Pesquisa células dentro de um **círculo** definido por coordenadas centrais e raio. Não requer uma célula de referência.

| Parâmetro       | Tipo     | Obrig. | Descrição                          |
|-----------------|----------|--------|------------------------------------|
| `lat`           | double   | ✅     | Latitude do centro                 |
| `lon`           | double   | ✅     | Longitude do centro                |
| `radiusKm`      | double   | ✅     | Raio em km (> 0)                   |
| `mnc`           | integer  | ❌     | Filtrar por MNC                    |
| `band`          | string   | ❌     | Filtrar por banda                  |
| `techGeneration`| string[] | ❌     | `2G`, `3G`, `4G`, `5G`, `NB-IoT`  |

**Resposta:** `CellsInCircleResponse { centerLatitude, centerLongitude, radiusKm, cells[] }`  
As células são ordenadas por distância crescente ao centro.

**Query PostGIS utilizada:**
```sql
ST_DWithin(l.coordinates::geography,
           ST_SetSRID(ST_MakePoint(lon, lat), 4326)::geography,
           radiusMeters)
```

#### `GET /api/cells/search/bbox`

Pesquisa células dentro do **rectângulo delimitador** (bounding box) formado por dois pontos (cantos opostos). A ordem dos cantos é indiferente.

| Parâmetro       | Tipo     | Obrig. | Descrição                           |
|-----------------|----------|--------|-------------------------------------|
| `lat1`, `lon1`  | double   | ✅     | Coordenadas do primeiro canto       |
| `lat2`, `lon2`  | double   | ✅     | Coordenadas do segundo canto        |
| `mnc`           | integer  | ❌     | Filtrar por MNC                     |
| `band`          | string   | ❌     | Filtrar por banda                   |
| `techGeneration`| string[] | ❌     | `2G`, `3G`, `4G`, `5G`, `NB-IoT`   |

**Resposta:** `CellsInBboxResponse { corner1Latitude, corner1Longitude, corner2Latitude, corner2Longitude, cells[] }`

**Query PostGIS utilizada:**
```sql
ST_Within(l.coordinates,
          ST_MakeEnvelope(minLon, minLat, maxLon, maxLat, 4326))
```

#### `GET /api/cells/search/county`

Pesquisa células pelos concelhos de um distrito inteiro, ou por um concelho específico quando `countyId` é enviado.

| Parâmetro        | Tipo     | Obrig. | Descrição                           |
|------------------|----------|--------|-------------------------------------|
| `districtId`     | string   | ✅     | ID do distrito                      |
| `countyId`       | int64    | ❌     | ID interno do concelho              |
| `mnc`            | integer  | ❌     | MNC                                 |
| `techGeneration` | string[] | ❌     | `2G`, `3G`, `4G`, `5G`, `NB-IoT`    |

**Resposta:** `CellsByAdministrativeAreaResponse { districtId, countyId, caopPolygonGeoJson, cells[] }`  
Quando `countyId` é enviado, `caopPolygonGeoJson` vem uma única vez ao nível da resposta.

#### `GET /api/cells/search/lac-tac`

Pesquisa cells pelo mesmo `MCC + MNC + LAC/TAC`.

| Parâmetro | Tipo    | Obrig. | Descrição |
|-----------|---------|--------|-----------|
| `mcc`     | integer | ✅     | MCC       |
| `mnc`     | integer | ✅     | MNC       |
| `lacTac`  | string  | ✅     | LAC/TAC   |

**Resposta:** `CellResponse[]`

#### `GET /api/cells/search/lac-tac/polygon`

Calcula o polygon de cobertura a partir das localizações das cells do mesmo `MCC + MNC + LAC/TAC`.

| Parâmetro | Tipo    | Obrig. | Descrição |
|-----------|---------|--------|-----------|
| `mcc`     | integer | ✅     | MCC       |
| `mnc`     | integer | ✅     | MNC       |
| `lacTac`  | string  | ✅     | LAC/TAC   |

**Resposta:** `LacTacCoverageResponse { mcc, mnc, lacTac, polygonGeoJson }`

#### `GET /api/cells/search/enb-gnb`

Pesquisa cells associadas ao mesmo `eNB/gNB`.

| Parâmetro | Tipo    | Obrig. | Descrição |
|-----------|---------|--------|-----------|
| `enbGnb`  | integer | ✅     | eNB/gNB   |

**Resposta:** `CellResponse[]`

---

## 7. Services

### `CellService`

Principal service da aplicação. Responsabilidades:

- **`getCellsByCgi(cgi)`** — normaliza e pesquisa por `cgi` ou `paragon_cgi`.
- **`getDistrictsByCountry(country)`** — lista distritos por país.
- **`getCountiesByDistrict(districtId)`** — lista concelhos por distrito.
- **`getNearbyCells(cgi, radiusKm, sameNetwork, techGenerations)`** — resolve coordenadas da célula central e delega no repositório.
- **`getCellsInCircle(lat, lon, radiusKm, mnc, techGenerations)`** — pesquisa geoespacial por coordenadas directas.
- **`getCellsInBbox(lat1, lon1, lat2, lon2, mnc, techGenerations)`** — pesquisa por rectângulo delimitador.
- **`getCellsByAdministrativeArea(districtId, countyId, mnc, techGenerations)`** — pesquisa por distrito/concelho com filtros e polígono CAOP correspondente.
- **`getCellsByLacTac(mcc, mnc, lacTac)`** — lista cells pelo mesmo MCC/MNC/LAC-TAC.
- **`getLacTacCoveragePolygon(mcc, mnc, lacTac)`** — calcula o polygon de cobertura por MCC/MNC/LAC-TAC.
- **`getCellsByEnbGnb(enbGnb)`** — lista cells pelo mesmo eNB/gNB.
- **`createCell(request, principal)`** — cria/resolve `location`, `band`, `mccmnc`, `enbgnb` e a `cell`; gera polígonos.
- **`updateCell(id, request, principal)`** — actualiza todos os sub-registos e regenera polígonos.
- **`deleteCell(id)`** — remove a célula (cascade para `cell_polygon`).
- **`importCellsCsv(file, principal)`** — parse de CSV com delimitador auto-detectado (`,` ou `;`), upsert por CGI/paragon_cgi, geração de polígonos.

#### Geração de Polígonos (`CellPolygonGenerator`)

Gera dois polígonos 3D (`SRID=4326;POLYGON Z(...)`) por célula:

| Tipo              | Radius base | Amplitude | Uso                          |
|-------------------|-------------|-----------|------------------------------|
| `polygon`         | 500 m       | 110°      | Visualização full (nearby central cell) |
| `polygon_short`   | 225–300 m   | 110°      | Visualização compacta (variável por tecnologia) |

O radius do `polygon_short` varia por geração:

| Tecnologia | Radius | Altitude |
|------------|--------|----------|
| 2G         | 300 m  | 20 m     |
| 3G         | 275 m  | 25 m     |
| 4G         | 250 m  | 30 m     |
| 5G         | 225 m  | 35 m     |
| NB-IoT     | 200 m  | 40 m     |

#### Parsing de `techGeneration`

Aceita valores case-insensitive, com ou sem underscore:

| Input           | Tecnologia (BD) |
|-----------------|-----------------|
| `2G`            | 2               |
| `3G`            | 3               |
| `4G`            | 4               |
| `5G`            | 5               |
| `NB-IoT`, `NBIOT` | 10            |

### `AuthSessionService`

- Valida credenciais e cria sessão na tabela `session` + `active_user_session`.
- `resolveUserFromSessionKey` — lê o cookie e constrói `AuthUserPrincipal` com roles Spring Security.
- `touchSession` — actualiza `last_heartbeat`, `last_path` e `last_ip`.

### `UserService`

- Criação de utilizadores com hash de password (BCrypt).
- Associação a departamento via `user_department`.
- Listagem/consulta com perfis e departamento.

### `DepartmentService`

- CRUD de departamentos com validação de nome único.

---

## 8. Deployment e Configuração

### Variáveis de Ambiente

| Variável      | Padrão (dev)                              | Descrição                        |
|---------------|-------------------------------------------|----------------------------------|
| `DB_URL`      | `jdbc:postgresql://192.168.1.95:5432/geocell` | URL JDBC do PostgreSQL          |
| `DB_USERNAME` | `geocell_user`                            | Utilizador da BD                 |
| `DB_PASSWORD` | `...`                                     | Password da BD                   |

> **⚠️ Produção:** definir sempre `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` como variáveis de ambiente externas. Nunca usar os valores padrão.

### Configuração da aplicação (`application.properties`)

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Limite de upload CSV
spring.servlet.multipart.max-file-size=30MB
spring.servlet.multipart.max-request-size=30MB
```

### Build e execução

```bash
# Compilar
./gradlew build

# Executar
./gradlew bootRun

# Jar executável
./gradlew bootJar
java -jar build/libs/geocell-0.0.1-SNAPSHOT.jar
```

### Requisitos de infraestrutura

- **Java 25** (JDK)
- **PostgreSQL 15+** com extensão **PostGIS 3.x**
- Portas: aplicação em `8080` (por omissão), BD em `5432`

### Inicialização da BD

Executar o script SQL de criação do schema antes da primeira execução:

```bash
psql -U geocell_user -d geocell -f geocell/src/sql/create_schema.sql
```

Depois, carregar os dados de referência (países, distritos, concelhos, operadores):

```bash
psql -U geocell_user -d geocell -f geocell/src/sql/inserts/country.sql
psql -U geocell_user -d geocell -f geocell/src/sql/inserts/district.sql
psql -U geocell_user -d geocell -f geocell/src/sql/inserts/county.sql
psql -U geocell_user -d geocell -f geocell/src/sql/inserts/mccmnc.sql
psql -U geocell_user -d geocell -f geocell/src/sql/inserts/portugalGrid.sql
```

---

## 9. Importação de Dados CSV

O endpoint `POST /api/cells/import` aceita ficheiros `.csv` até 30 MB.

### Formato esperado

Colunas (case-insensitive, delimitador `,` ou `;` auto-detectado):

| Coluna           | Obrig. | Tipo    | Descrição                                |
|------------------|--------|---------|------------------------------------------|
| `MCC`            | ✅     | inteiro | Mobile Country Code                      |
| `MNC`            | ✅     | inteiro | Mobile Network Code                      |
| `TECNOLOGIA`     | ✅     | inteiro | 2, 3, 4, 5 ou 10 (NB-IoT)               |
| `AZIMUTE`        | ✅     | inteiro | 0–360 graus                              |
| `LATITUDE`       | ✅     | decimal | WGS84                                    |
| `LONGITUDE`      | ✅     | decimal | WGS84                                    |
| `DATA`           | ✅     | data    | Data do registo (vários formatos aceites) |
| `CGI_ECGI_NCGI`  | ⚠️     | string  | CGI/ECGI/NCGI — pelo menos um de CGI ou PARAGON_ID |
| `PARAGON_ID`     | ⚠️     | string  | Identificador alternativo                |
| `LAC_TAC`        | ❌     | string  | LAC ou TAC                               |
| `CI`             | ❌     | string  | Cell Identity                            |
| `ECI_NCI`        | ❌     | string  | ECI ou NCI                               |
| `BANDA`          | ❌     | string  | Banda de frequência                      |
| `ENB_GNB`        | ❌     | inteiro | eNodeB / gNodeB                          |
| `NOME`           | ❌     | string  | Nome da célula                           |
| `MORADA`         | ❌     | string  | Morada                                   |
| `MORADA1`        | ❌     | string  | Morada linha 2                           |
| `CP4`            | ❌     | inteiro | Código postal (4 dígitos)                |
| `CP3`            | ❌     | inteiro | Código postal (3 dígitos)                |
| `DESIGNACAO_POSTAL` | ❌  | string  | Designação postal                        |
| `CONCELHO_CD`    | ❌     | string  | Código de concelho (para lookup em BD)   |

### Comportamento de upsert

- Se existir uma célula com o mesmo `cgi` ou `paragon_cgi`, é **actualizada**.
- Caso contrário, é **inserida**.
- Para cada célula, os polígonos `polygon` e `polygon_short` são sempre regenerados.
- Os timestamps (`created_at`/`updated_at`) são preservados com base na coluna `DATA` do CSV.

### Resposta

```json
{
  "rowsProcessed": 1500,
  "inserted": 1200,
  "updated": 300,
  "polygonsUpserted": 1500
}
```
