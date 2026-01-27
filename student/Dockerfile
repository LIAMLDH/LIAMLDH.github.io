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

# 安装必要的工具
RUN apk add --no-cache curl net-tools

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
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.net.preferIPv4Stack=true -Dlogging.level.org.springframework=INFO"

# 创建启动脚本
RUN echo '#!/bin/sh

echo "========================================"
echo "Starting Student Management Application"
echo "========================================"
echo "Current directory: $(pwd)"
echo "Java version: $(java -version 2>&1)"
echo "Environment variables:"
env | grep -E 'SPRING_|SERVER_|JAVA_'
echo "Java options: $JAVA_OPTS"
echo "========================================"
echo "Starting application..."

# 启动应用
java $JAVA_OPTS -jar app.jar &

# 获取应用进程 ID
APP_PID=$!
echo "Application started with PID: $APP_PID"

# 等待应用启动
echo "Waiting for application to start..."
sleep 10

# 检查应用状态
echo "========================================"
echo "Checking application status..."
echo "========================================"

# 检查端口是否被占用
if netstat -tuln | grep 8080; then
    echo "✅ Port 8080 is in use (application is likely running)"
else
    echo "❌ Port 8080 is not in use (application may have failed to start)"
fi

# 尝试访问应用
echo "========================================"
echo "Testing application access..."
echo "========================================"

if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health | grep -E '200|404'; then
    echo "✅ Application is accessible"
else
    echo "⚠️  Application may not be fully accessible yet, but it's starting up"
fi

# 等待应用进程结束
wait $APP_PID
' > start.sh && chmod +x start.sh

# 运行应用
ENTRYPOINT ["sh", "start.sh"]
