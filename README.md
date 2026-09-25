# Student Report Backend

## 项目简介
这是一个基于 Spring Boot 的学生报告后端系统，使用 Maven 构建，支持学生报告管理和待办事项管理功能。

## 技术栈
- Java 8
- Spring Boot 2.7.18
- Spring Data JPA
- MySQL 8.0.33
- Lombok

## 项目结构
```
src/
├── main/
│   ├── java/com/example/studentreport/
│   │   ├── config/           # 配置类
│   │   ├── controller/       # 控制器
│   │   ├── dto/              # 数据传输对象
│   │   ├── entity/           # 实体类
│   │   ├── repository/       # 数据访问层
│   │   ├── service/          # 业务逻辑层
│   │   └── StudentReportBackendApplication.java  # 主启动类
│   └── resources/
│       └── application.properties  # 配置文件
└── test/                     # 测试代码
```

## 启动前准备

### 1. 数据库配置
项目使用 MySQL 数据库，连接信息通过环境变量注入（代码中不存放真实密码），本地开发默认值如下：
```properties
spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:student_report}?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:123456}
```

**注意：**
- 本地开发可直接使用默认值（localhost + 本机 MySQL）
- 部署到服务器时，通过环境变量覆盖真实配置，例如：
  ```bash
  export DB_HOST=数据库地址 DB_PORT=3306 DB_NAME=student_report
  export DB_USERNAME=数据库账号 DB_PASSWORD=数据库密码
  ```
- 也可以把真实配置写在 `application-dev.properties` 中（已在 .gitignore 中排除，不会被提交）
- 请勿将真实服务器 IP、数据库密码提交到仓库

### 2. 确保已安装 Java
项目需要 Java 8 环境，请确保已安装并配置好 JAVA_HOME。

### 3. 确保已安装 Maven（可选）
如果使用 Maven 命令行启动，需要安装 Maven 并配置好 MAVEN_HOME。

## 启动方式

### 方式一：使用 JAR 包启动（推荐）

1. 确保项目已编译（如果没有编译，请先执行编译命令）：
```bash
mvn clean package
```

2. 启动项目：
```bash
java -jar target/student-report-backend-0.0.1-SNAPSHOT.jar
```

### 方式二：使用 Maven 命令启动

```bash
mvn spring-boot:run
```

### 方式三：在 IDE 中启动

1. 导入项目到 IDE（如 IntelliJ IDEA、Eclipse）
2. 找到主启动类 `StudentReportBackendApplication.java`
3. 右键点击，选择 "Run" 或 "Debug" 启动项目

## 常见问题及解决方案

### 1. 数据库连接失败
**错误信息：**
```
java.sql.SQLException: Access denied for user 'root'@'localhost' (using password: YES)
```

**解决方案：**
- 检查 MySQL 服务是否正在运行
- 检查数据库用户名和密码是否正确
- 检查数据库用户是否有足够的权限
- 检查数据库地址和端口是否正确

### 2. 端口被占用
**错误信息：**
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案：**
- 修改 `application.properties` 中的 `server.port` 配置，使用其他端口
- 关闭占用 8080 端口的进程

### 3. Maven 编译失败
**解决方案：**
- 确保 Maven 已正确安装和配置
- 执行 `mvn clean install -DskipTests` 命令，跳过测试编译
- 检查项目依赖是否正确

## 接口文档

项目启动后，可以通过以下地址访问：
- 项目首页：http://localhost:8080
- API 接口：可以通过 Postman 或其他工具测试

## 功能模块

### 1. 学生报告管理
- 增删改查学生报告
- 支持按条件查询

### 2. 待办事项管理
- 增删改查待办事项
- 支持标记完成状态

## 日志配置
项目使用 Spring Boot 默认日志配置，日志级别为 INFO。

## 异常处理
项目实现了全局异常处理，统一返回错误信息格式。

## 许可证

MIT License
