FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# 复制 Maven 镜像配置
COPY settings.xml /root/.m2/settings.xml

# 复制父工程和子模块 POM
COPY pom.xml .
COPY app-common/pom.xml app-common/pom.xml
COPY app-dao/pom.xml app-dao/pom.xml
COPY app-service/pom.xml app-service/pom.xml
COPY app-web/pom.xml app-web/pom.xml

# 使用指定的 Maven 配置下载依赖
RUN mvn -s /root/.m2/settings.xml \
    -B -ntp -pl app-web -am dependency:go-offline
COPY app-common app-common
COPY app-dao app-dao
COPY app-service app-service
COPY app-web app-web
RUN mvn -B -ntp -pl app-web -am package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN useradd --system --uid 10001 appuser
COPY --from=builder /workspace/app-web/target/app.jar /app/app.jar
RUN mkdir -p /app/logs && chown -R appuser:appuser /app
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
