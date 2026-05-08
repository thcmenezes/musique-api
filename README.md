# musique-api

API REST da Musique para cadastro de artistas, albuns e sessao de mood, com arquitetura em camadas, validacao, tratamento global de excecoes e migrations Flyway.

## Stack

- Java 25
- Spring Boot 3.5
- Spring Web + Validation
- Spring Data JPA
- PostgreSQL
- Flyway
- JUnit 5, Mockito, MockMvc

## Estrutura

- `src/main/java/com/musique/api/controller`: endpoints REST
- `src/main/java/com/musique/api/service`: regras de negocio
- `src/main/java/com/musique/api/domain`: entidades e repositorios
- `src/main/java/com/musique/api/dto`: contratos de request/response
- `src/main/java/com/musique/api/exception`: erros e handler global
- `src/main/resources/db/migration`: scripts Flyway

## Pre-requisitos

1. JDK 25 instalado e configurado no `JAVA_HOME`
2. Maven 3.9+
3. Docker Desktop (ou Docker Engine + Compose)

## 1) Arquivo central de configuracao da API

O arquivo central da API e:

- `src/main/resources/application.yml`

Observacao: neste projeto usamos **YAML** (`application.yml`) em vez de `application.properties`.

As configuracoes de conexao no `application.yml` estao parametrizadas por variaveis de ambiente:

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/musique`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `postgres`)
- `SERVER_PORT` (default: `8080`)

Configuracao da integracao com Spotify:

- `APP_SPOTIFY_CLIENT_ID` (obrigatorio para consulta real)
- `APP_SPOTIFY_CLIENT_SECRET` (obrigatorio para consulta real)
- `APP_SPOTIFY_ACCOUNTS_BASE_URL` (default: `https://accounts.spotify.com`)
- `APP_SPOTIFY_API_BASE_URL` (default: `https://api.spotify.com`)

## Integracao com Spotify (o que ela faz)

Quando voce cria ou atualiza um album em `POST /api/albums` ou `PUT /api/albums/{id}`:

1. A API consulta o Spotify usando **Client Credentials Flow** (server-to-server)
2. Busca o album pelo nome + artista
3. Seleciona a melhor correspondencia valida
4. Persiste automaticamente:
   - `coverUrl` (capa)
   - `idExternal` (id do Spotify)
   - `spotifyUrl` (link publico)
5. Retorna esses dados no `AlbumResponse`

Se nao encontrar correspondencia, a API retorna erro de negocio amigavel (sem 500 generico).

## Como configurar credenciais Spotify

1. Crie um app no [Spotify for Developers](https://developer.spotify.com/dashboard)
2. Copie o **Client ID** e o **Client Secret**
3. Opcional: crie um arquivo `.env` na **raiz do `musique-api`** (mesmo nivel do `pom.xml`) com:

```env
APP_SPOTIFY_CLIENT_ID=seu_client_id
APP_SPOTIFY_CLIENT_SECRET=seu_client_secret
```

A aplicacao carrega esse arquivo na inicializacao. **Variaveis de ambiente do sistema continuam com prioridade** sobre o `.env`.

4. Ou defina variaveis de ambiente antes de subir a API:

PowerShell:

```powershell
$env:APP_SPOTIFY_CLIENT_ID="seu_client_id"
$env:APP_SPOTIFY_CLIENT_SECRET="seu_client_secret"
```

Bash:

```bash
export APP_SPOTIFY_CLIENT_ID="seu_client_id"
export APP_SPOTIFY_CLIENT_SECRET="seu_client_secret"
```

> Sem essas credenciais a criacao/atualizacao de album nao consegue enriquecer com dados do Spotify.

## 2) Subir somente o banco com Docker Compose

Na raiz do `musique-api` existem os arquivos:

- `docker-compose.yml`
- `.env`

No arquivo `.env`, defina (ou ajuste) as credenciais do banco **e, se quiser, as do Spotify** (tambem lidas pela API ao rodar localmente):

```env
POSTGRES_DB=musique
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432
```

Subir o PostgreSQL:

```bash
cd c:/Projects/musique/musique-api
docker-compose up -d
```

O banco sobe com volume persistente:

- `musique_postgres_data`

Parar/remover container e rede:

```bash
docker-compose down
```

Parar/remover tambem o volume (apaga os dados):

```bash
docker-compose down -v
```

## 3) Rodar a API localmente

Com o banco no ar:

```bash
mvn spring-boot:run
```

API sobe em: `http://localhost:8080`

## 4) Flyway

Migrations em:

- `src/main/resources/db/migration/V1__create_artists_and_albums.sql`
- `src/main/resources/db/migration/V2__album_rating_half_star_constraint.sql`
- `src/main/resources/db/migration/V3__create_mood_albums.sql`

Ao iniciar a API, o Flyway executa automaticamente migrations pendentes.

## 5) Rodar testes

```bash
mvn test
```

## 6) Endpoints

### Artistas

- `POST /api/artists`
- `GET /api/artists`
- `GET /api/artists/{id}`
- `PUT /api/artists/{id}`
- `DELETE /api/artists/{id}`

### Albuns

- `POST /api/albums`
- `GET /api/albums`
- `GET /api/albums/{id}`
- `PUT /api/albums/{id}`
- `DELETE /api/albums/{id}`

Request de criacao/atualizacao:

```json
{
  "name": "Dark Matter",
  "releaseYear": 2024,
  "rating": 4.5,
  "artistId": 1
}
```

### Mood

- `GET /api/mood`
- `POST /api/mood`
- `DELETE /api/mood/{albumId}`

`POST /api/mood` recebe:

```json
{
  "albumId": 10
}
```

## Contrato de album (response)

`AlbumResponse` agora retorna objeto `artist` completo:

```json
{
  "id": 10,
  "name": "Discovery",
  "releaseYear": 2001,
  "rating": 4.5,
  "coverUrl": "https://...",
  "idExternal": "spotify_album_id",
  "spotifyUrl": "https://open.spotify.com/album/...",
  "artist": {
    "id": 1,
    "name": "Daft Punk"
  }
}
```

## Regra de rating

- O `rating` dos albuns deve estar entre `0.0` e `5.0`
- O `rating` aceita apenas incrementos de `0.5` (ex.: `3.5`, `4.0`, `4.5`)

## Collections

- Postman: `collections/musique-api.postman_collection.json`
- Insomnia: `collections/musique-api.insomnia_collection.json`

As colecoes ja estao atualizadas para o fluxo Spotify (request de album enxuto e enriquecimento automatico na resposta).
