# 第一阶段：构建阶段
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# 复制 pom.xml 文件并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/student-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=production
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:student_course_management
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
ENV SPRING_JPA_SHOW_SQL=true
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.H2Dialect

# 增强启动配置
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.net.preferIPv4Stack=true"

# 运行应用（添加详细日志）
ENTRYPOINT ["sh", "-c", "echo 'Starting application...'; echo 'Environment variables:'; env | grep -E 'SPRING_|SERVER_'; echo 'Java options: $JAVA_OPTS'; java $JAVA_OPTS -jar app.jar"]
