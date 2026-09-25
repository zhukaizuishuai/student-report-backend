# student-report-backend 配置文件说明

## **概述**
- **项目根**: `student-report-backend` 是一个基于 Spring Boot 的后端服务。下面对 `config` 包中的主要配置类做详细说明，并给出常见用法与排查建议。

## **文件位置**
- 配置类位于: [student-report-backend/src/main/java/com/example/studentreport/config](student-report-backend/src/main/java/com/example/studentreport/config)

## **1. GlobalExceptionHandler.java**
- **作用**: 全局捕获并统一处理 Controller 层抛出的异常，返回规范化的错误响应（HTTP 状态、错误码、消息、时间戳等）。
- **典型注解/使用**:
  - `@ControllerAdvice`：声明为全局异常处理器。
  - `@ExceptionHandler(Exception.class)`：对应异常类型的方法会被调用以构造响应。
  - 可以结合 `@ResponseStatus` 或手动设置 `ResponseEntity` 返回状态码。
- **常见实现要点**:
  - 区分业务异常（如自定义 `BusinessException`）与系统异常（如 `NullPointerException`），分别返回不同的状态码和消息。
  - 日志记录：对未预期异常写入 ERROR 日志并保留堆栈，业务异常可写入 WARN 或 INFO。
  - 对 `MethodArgumentNotValidException` 等参数校验异常给出字段级错误信息。
- **排查建议**:
  - 若前端收到 500，但后端预期返回业务错误，检查是否有未被 `@ExceptionHandler` 捕获的异常类型。
  - 打开日志查看异常堆栈，确认异常抛出点与处理器注册是否生效。

## **2. LoggerConfig.java**
- **作用**: 配置项目的日志相关行为，常见配置包括请求/响应日志拦截器、AOP 切面日志、或为第三方库定制 logger。通常还会注册 `CommonsRequestLoggingFilter` 或自定义 `HandlerInterceptor`。
- **典型内容**:
  - 创建 `Filter` 或 `HandlerInterceptor` 的 `@Bean`，用于打印请求 URL、参数、执行时间等。
  - 配置日志级别（也可以在 `application.properties`/`application.yml` 中配置），或在运行时动态调整。
  - 如使用 AOP：定义 `@Aspect` 来记录方法入参/出参、异常、耗时等。
- **示例（典型片段，不一定与代码完全相同）**:

```java
// 注册请求日志过滤器
@Bean
public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeClientInfo(true);
    filter.setIncludeQueryString(true);
    filter.setIncludePayload(true);
    filter.setMaxPayloadLength(10000);
    return filter;
}
```

- **排查建议**:
  - 若日志没有输出，确认日志框架（Logback/Log4j2）配置是否覆盖了程序中的级别设置。
  - 对请求/响应日志敏感信息（如密码）应进行脱敏处理或避免记录。

## **3. WebMvcConfig.java**
- **作用**: 自定义 Spring MVC 行为，例如 CORS、静态资源映射、消息转换器、拦截器注册、格式化器等。
- **常见方法**:
  - `addCorsMappings(CorsRegistry registry)`：配置跨域策略（允许哪些 origin、方法、头、是否携带凭证等）。
  - `addInterceptors(InterceptorRegistry registry)`：注册自定义拦截器（如鉴权、日志、限流等）。
  - `configureMessageConverters(List<HttpMessageConverter<?>> converters)` 或 `extendMessageConverters(...)`：配置 JSON（Jackson）或 XML 的序列化规则（日期格式、忽略 null、字段命名策略等）。
  - `addResourceHandlers(ResourceHandlerRegistry registry)`：映射外部静态资源路径。
- **示例（常见片段）**:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowCredentials(true)
        .maxAge(3600);
}

@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new MyAuthInterceptor()).addPathPatterns("/api/**");
}
```

- **排查建议**:
  - 如果前端报跨域错误，优先检查 `addCorsMappings` 设置是否生效（以及是否被安全配置覆盖）。
  - 如果消息体反序列化失败（如日期格式），检查 `MappingJackson2HttpMessageConverter` 的配置。

## **如何在本项目中定位这些类**
- `GlobalExceptionHandler.java`：[student-report-backend/src/main/java/com/example/studentreport/config/GlobalExceptionHandler.java](student-report-backend/src/main/java/com/example/studentreport/config/GlobalExceptionHandler.java)
- `LoggerConfig.java`：[student-report-backend/src/main/java/com/example/studentreport/config/LoggerConfig.java](student-report-backend/src/main/java/com/example/studentreport/config/LoggerConfig.java)
- `WebMvcConfig.java`：[student-report-backend/src/main/java/com/example/studentreport/config/WebMvcConfig.java](student-report-backend/src/main/java/com/example/studentreport/config/WebMvcConfig.java)

（上面链接会打开对应源码文件，便于快速查看实现细节。）

## **常见改进建议**
- 为 `GlobalExceptionHandler` 添加统一错误码与请求 ID 支持，便于跨服务追踪与前端错误映射。
- 在 `LoggerConfig` 中对慢请求（例如 > 1s）单独记录，以便性能分析。
- 在 `WebMvcConfig` 中添加国际化（`LocaleResolver`）与统一时间格式配置。
- 为 DTO 添加 `javax.validation` 注解，并在 Controller 中使用 `@Valid`，结合 `GlobalExceptionHandler` 返回字段级错误。

## **运行与验证（本地）**
- 构建并运行：

```bash
mvn clean package
mvn spring-boot:run
# 或者运行 jar
java -jar target/student-report-backend-0.0.1-SNAPSHOT.jar
```

- 验证点：
  - 访问主要 API（如 `GET /api/reports`、`POST /api/reports`）查看正常响应。
  - 故意触发参数校验或业务错误，确认 `GlobalExceptionHandler` 返回的错误结构是否符合预期。
  - 修改 CORS 设置，确认前端请求跨域问题是否解决。

## **更多帮助**
- 若需要，我可以：
  - 读取并分析 `GlobalExceptionHandler.java` / `LoggerConfig.java` / `WebMvcConfig.java` 的具体实现并给出逐行注释。
  - 自动为 DTO 添加校验注解并调整异常处理返回格式。

---
*文件已写入： [student-report-backend/CONFIGS_EXPLAINED.md](student-report-backend/CONFIGS_EXPLAINED.md)*