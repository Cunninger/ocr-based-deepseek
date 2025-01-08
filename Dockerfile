# 使用官方的 Java 8 作为基础镜像
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中
COPY target/ocr-based-deepseek-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口（根据你的应用程序配置）
EXPOSE 8088

# 运行应用程序
ENTRYPOINT ["java", "-jar", "app.jar"]

