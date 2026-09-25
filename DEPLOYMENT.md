# 阿里云部署指南

## 1. 阿里云服务器准备

### 1.1 购买阿里云ECS服务器
1. 登录[阿里云控制台](https://homenew.console.aliyun.com/)
2. 选择「云服务器 ECS」
3. 点击「创建实例」
4. 配置服务器参数：
   - 地域：选择靠近目标用户的地域
   - 实例规格：根据业务需求选择，建议至少2核4G
   - 操作系统：选择「CentOS 7.x」或「Ubuntu 20.04 LTS」
   - 存储：建议40GB以上高效云盘
   - 网络：选择「专有网络VPC」，分配公网IP
   - 安全组：创建或选择已有安全组，确保开放8080端口（或其他你想要使用的端口）
5. 设置登录密码或SSH密钥
6. 完成购买

### 1.2 连接服务器
```bash
# 使用SSH连接服务器（替换为你的服务器公网IP）
ssh root@你的服务器公网IP
```

## 2. 环境安装

### 2.1 安装Java 8
```bash
# CentOS系统
yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

# Ubuntu系统
apt update
apt install -y openjdk-8-jdk

# 验证Java安装
java -version
javac -version
```

### 2.2 安装MySQL（可选，也可使用阿里云RDS）

#### 2.2.1 安装MySQL 8.0
```bash
# CentOS系统
yum localinstall -y https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
yum install -y mysql-community-server

# Ubuntu系统
wget -c https://dev.mysql.com/get/mysql-apt-config_0.8.15-1_all.deb
dpkg -i mysql-apt-config_0.8.15-1_all.deb
apt update
apt install -y mysql-server

# 启动MySQL服务并设置开机自启
systemctl start mysqld
systemctl enable mysqld

# 查看初始密码（CentOS）
grep 'temporary password' /var/log/mysqld.log

# 登录MySQL并修改密码
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY '你的新密码';
```

#### 2.2.2 创建数据库和用户
```sql
-- 创建数据库
CREATE DATABASE student_report CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权（允许远程访问）
CREATE USER 'student_report'@'%' IDENTIFIED BY '你的密码';
GRANT ALL PRIVILEGES ON student_report.* TO 'student_report'@'%';
FLUSH PRIVILEGES;
```

### 2.3 安装Maven（可选，用于在服务器上编译项目）
```bash
# 下载Maven
wget https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz

# 解压
mkdir -p /opt/maven
tar -zxvf apache-maven-3.8.8-bin.tar.gz -C /opt/maven

# 配置环境变量
echo 'export MAVEN_HOME=/opt/maven/apache-maven-3.8.8' >> /etc/profile
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> /etc/profile

# 使环境变量生效
source /etc/profile

# 验证Maven安装
mvn -version
```

## 3. 项目配置修改

### 3.1 修改数据库连接配置
编辑`src/main/resources/application.properties`文件，修改数据库连接信息：

```properties
# 如果使用本地MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/student_report?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

# 如果使用阿里云RDS
# spring.datasource.url=jdbc:mysql://你的RDS地址:3306/student_report?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

spring.datasource.username=student_report
spring.datasource.password=你的数据库密码
```

### 3.2 修改CORS配置（允许所有来源访问）
```properties
spring.web.cors.allowed-origins=*
```

### 3.3 修改服务器端口（可选）
如果需要使用80端口（默认HTTP端口），可以修改：
```properties
server.port=80
```

## 4. 项目打包

在本地项目根目录执行：
```bash
mvn clean package -DskipTests
```

打包成功后，会在`target`目录生成`student-report-backend-0.0.1-SNAPSHOT.jar`文件。

## 5. 上传项目到服务器

使用SCP命令将JAR包上传到服务器：
```bash
scp target/student-report-backend-0.0.1-SNAPSHOT.jar root@你的服务器公网IP:/opt/
```

## 6. 启动服务

### 6.1 直接启动（测试用）
```bash
# 进入服务器
ssh root@你的服务器公网IP

# 启动服务
java -jar /opt/student-report-backend-0.0.1-SNAPSHOT.jar
```

### 6.2 使用systemd管理服务（推荐，生产环境）

创建服务配置文件：
```bash
vi /etc/systemd/system/student-report.service
```

添加以下内容：
```ini
[Unit]
Description=Student Report Backend
After=syslog.target network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt
ExecStart=/usr/bin/java -jar /opt/student-report-backend-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10s

[Install]
WantedBy=multi-user.target
```

启动服务并设置开机自启：
```bash
# 重新加载systemd配置
systemctl daemon-reload

# 启动服务
systemctl start student-report

# 设置开机自启
systemctl enable student-report

# 查看服务状态
systemctl status student-report

# 查看服务日志
journalctl -u student-report -f
```

## 7. 安全组配置

确保阿里云ECS实例的安全组开放了相应的端口：

1. 登录阿里云控制台
2. 进入ECS实例详情页
3. 点击「安全组」标签
4. 点击「配置规则」
5. 添加入方向规则：
   - 授权策略：允许
   - 协议类型：TCP
   - 端口范围：8080（或你设置的端口）
   - 授权对象：0.0.0.0/0（允许所有IP访问，生产环境建议设置为特定IP）

## 8. 访问测试

### 8.1 使用IP访问
在浏览器或Postman中访问：
```
http://你的服务器公网IP:8080
```

### 8.2 配置域名访问（可选）

#### 8.2.1 购买域名
1. 登录阿里云控制台
2. 选择「域名与网站」->「域名注册」
3. 搜索并购买合适的域名

#### 8.2.2 域名解析
1. 进入「域名控制台」
2. 点击「解析」
3. 添加A记录：
   - 记录类型：A
   - 主机记录：@（或www，根据需求）
   - 记录值：你的服务器公网IP
   - TTL：默认10分钟

#### 8.2.3 使用域名访问
配置完成后，等待解析生效（通常10-30分钟），然后可以通过域名访问：
```
http://你的域名:8080
```

### 8.3 配置Nginx反向代理（可选，推荐）

安装Nginx：
```bash
# CentOS系统
yum install -y nginx

# Ubuntu系统
apt install -y nginx

# 启动Nginx并设置开机自启
systemctl start nginx
systemctl enable nginx
```

配置反向代理：
```bash
vi /etc/nginx/conf.d/student-report.conf
```

添加以下内容：
```nginx
server {
    listen 80;
    server_name 你的域名;  # 替换为你的域名

    location / {
        proxy_pass http://localhost:8080;  # 替换为你的应用端口
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

重启Nginx：
```bash
nginx -t  # 测试配置是否正确
systemctl restart nginx
```

现在可以通过域名直接访问，无需端口：
```
http://你的域名
```

## 9. 常见问题排查

### 9.1 服务无法启动
- 查看日志：`journalctl -u student-report -f`
- 检查数据库连接：确保数据库服务正常运行，连接信息正确
- 检查端口是否被占用：`netstat -tuln | grep 8080`

### 9.2 无法通过公网访问
- 检查安全组规则：确保相应端口已开放
- 检查防火墙：`firewall-cmd --list-ports`（CentOS）或 `ufw status`（Ubuntu）
- 检查服务是否正常运行：`systemctl status student-report`

### 9.3 数据库连接失败
- 检查数据库是否正常运行：`systemctl status mysqld`
- 检查数据库用户权限：确保用户有远程访问权限
- 检查数据库连接URL：确保地址、端口、数据库名正确

## 10. 监控与维护

### 10.1 查看服务日志
```bash
journalctl -u student-report -f
```

### 10.2 重启服务
```bash
systemctl restart student-report
```

### 10.3 更新服务
1. 本地重新打包项目
2. 上传新的JAR包到服务器
3. 重启服务：`systemctl restart student-report`

## 11. 扩展建议

1. **使用阿里云RDS**：将数据库迁移到阿里云RDS，提高可靠性和安全性
2. **配置HTTPS**：使用阿里云SSL证书，配置HTTPS访问
3. **使用负载均衡**：如果业务量较大，可配置阿里云SLB负载均衡
4. **配置CDN**：对于静态资源，可使用阿里云CDN加速访问
5. **使用云监控**：配置阿里云云监控，监控服务器和应用状态

---

部署完成后，你可以通过以下方式访问接口：
- IP访问：`http://你的服务器公网IP:8080`
- 域名访问（已配置）：`http://你的域名`

例如，访问学生报告列表接口：
```
http://你的服务器公网IP:8080/api/reports
```

或使用域名：
```
http://你的域名/api/reports
```