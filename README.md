                        英文文档
# Spring Boot Modular Demo

A Maven multi-module Spring Boot 3 application using Java 17, MyBatis, MySQL, Redis, Logback and Docker Compose.

## Modules

- `app-web`: Spring Boot entry point and REST controllers
- `app-service`: business logic, transactions and Redis cache
- `app-dao`: MyBatis mapper and database entity
- `app-common`: shared API response type

## Run with Docker

Requires Docker Desktop / Docker Engine and Docker Compose.

```bash
cp .env.example .env
docker compose up --build -d
docker compose ps
docker compose logs -f app
```

Test the health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

List users:

```bash
curl http://localhost:8080/api/users
```

Create a user:

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"userName":"Bob","email":"bob@example.com"}'
```

Read a user (the service attempts Redis cache first):

```bash
curl http://localhost:8080/api/users/1
```

Update and delete:

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H 'Content-Type: application/json' \
  -d '{"userName":"Alice Updated","email":"alice.updated@example.com"}'

curl -X DELETE http://localhost:8080/api/users/1
```

Stop containers but keep data:

```bash
docker compose stop
```

Stop and remove containers/network (named volumes remain):

```bash
docker compose down
```

**Warning:** `docker compose down -v` deletes the MySQL and Redis named volumes and therefore removes persisted container data.

## Run locally without containerizing the app

Start MySQL and Redis first, then run from the repository root:

```bash
mvn -pl app-web -am clean package
java -jar app-web/target/app.jar
```

Default local connection settings are in `app-web/src/main/resources/application.yml`. Override with environment variables if needed.

## Notes

- Docker Compose service hostnames are `mysql` and `redis`; do not use `localhost` between containers.
- The sample credentials are for local development only. Change them before exposing the service.
- Logback writes to console and to `./logs` locally; in Compose logs are persisted in the `app_logs` named volume.
- MyBatis SQL is written with annotations for this small demo. Larger projects can move SQL to XML mapper files.
- Redis cache uses a 10-minute TTL and is evicted on update/delete. For production systems, also consider transaction/cache consistency, authentication, rate limiting, secrets management and backups.

## ADB

- ADB is a simple command-line tool to manage the database. It is located in `adb/adb.sh` and can be run from the repository root.

                        中文文档
# Spring Boot 多模块示例项目

这是一个基于 Maven 多模块架构的 Spring Boot 3 应用，使用 Java 17、MyBatis、MySQL、Redis、Logback 和 Docker Compose。

## 模块说明

- `app-web`：Spring Boot 启动入口和 REST 控制器。
- `app-service`：业务逻辑、事务处理和 Redis 缓存。
- `app-dao`：MyBatis Mapper 接口和数据库实体类。
- `app-common`：公共 API 响应类型。

## 使用 Docker 运行

需要提前安装 Docker Desktop / Docker Engine 和 Docker Compose。

```bash
cp .env.example .env
docker compose up --build -d
docker compose ps
docker compose logs -f app
```

检查健康状态接口：

```bash
curl http://localhost:8080/actuator/health
```

查询用户列表：

```bash
curl http://localhost:8080/api/users
```

创建用户：

```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"userName":"Bob","email":"bob@example.com"}'
```

查询指定用户（服务会优先尝试从 Redis 缓存中读取）：

```bash
curl http://localhost:8080/api/users/1
```

更新用户：

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H 'Content-Type: application/json' \
  -d '{"userName":"Alice Updated","email":"alice.updated@example.com"}'
```

删除用户：

```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### 停止容器，但保留数据

```bash
docker compose stop
```

### 停止容器并删除容器和网络（保留命名卷）

```bash
docker compose down
```

**警告：** `docker compose down -v` 会删除 MySQL 和 Redis 使用的命名卷，因此会删除保存在这些容器数据卷中的持久化数据。

## 在本地运行应用（不将应用本身容器化）

首先启动 MySQL 和 Redis，然后在项目根目录执行：

```bash
mvn -pl app-web -am clean package
java -jar app-web/target/app.jar
```

本地默认连接配置位于 `app-web/src/main/resources/application.yml`。如有需要，可以通过环境变量覆盖默认配置。

## 注意事项

- Docker Compose 中的服务主机名是 `mysql` 和 `redis`。容器之间通信时，不要使用 `localhost` 代替服务名。
- 示例中的账号和密码仅供本地开发使用。对外开放服务前，请务必修改。
- Logback 会将日志写入控制台和本地的 `./logs` 目录；在 Docker Compose 中，日志保存在名为 `app_logs` 的命名卷中。
- 这个小型示例项目使用 MyBatis 注解编写 SQL。对于较大型的项目，可以将 SQL 迁移到 XML Mapper 文件中。
- Redis 缓存的有效期为 10 分钟，并且在更新或删除用户时会清除对应缓存。对于生产环境，还应考虑事务与缓存的一致性、身份认证、请求频率限制、敏感信息管理和数据备份等问题。

## ADB 数据库管理工具

ADB 是一个用于管理数据库的简单命令行工具，位于 `adb/adb.sh`。可以在项目根目录运行。

查看帮助：

```bash
adb/adb.sh -h
```

列出可用的数据库或连接信息：

```bash
adb/adb.sh -l
```

执行 SQL 查询：

```bash
adb/adb.sh -c "SELECT * FROM users"
```

