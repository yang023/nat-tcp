# tcp-nat

一个简单的 tcp 内网穿透服务

&lt;可想着它能代理所有 tcp 协议啊，奈何实力不允许，它现在还是个孩砸（只有 http）！&gt;

## 开箱说明

> 普通（Read after me:
> q-i-ong）人，想练习微服务架构，有钱买电脑，没钱每个月/每年供养云主机，又不想花钱搞别人家的内网穿透服务（相信没有几个开发者能有足够资金支撑一台甚至N台性能不差能跑几个服务的云主机），自己研究内网穿透原理开发出来的小工具。
>
> 本玩具仅支持 http 协议及其他 tcp 协议（TODO）的报文转发，要用 ssh、udp、及其他 tcp 协议的穿透功能，出门右拐找花生壳。

## 上菜食用

### 原材料

```xml

<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.0.0</spring-boot.version>
    <netty.version>4.1.84.Final</netty.version>
    <guava.version>31.1-jre</guava.version>
    <commons-cli.version>1.4</commons-cli.version>
</properties>

```

### 硬件需求

> 由一台公网服务器，与一堆内网主机组成。

### 编译

```shell
mvn package

# 得到两个执行包：
# ./app-client/app-client-{version}.jar
# ./app-server/app-server-{version}.jar
```

### 运行

```shell
# 启动服务端（公网[云]主机）
java -jar ./app-server/nat-app-server.jar [-Dxxx=xx]

# 启动客户端（内网主机）
java -jar ./app-client/nat-app-client.jar -c {注册的客户端ID} -s {服务器主机:注册端口} -t {启用的通道列表}
```

* docker 运行 server 端

```shell
# 构建
docker build docker build -t nat-app ./app-server/target/

# 运行
# 8080: web 接口
# 18080: http 代理端口
# 10243: socket 通信端口
# 端口在 ./app-server/Dockerfile 中定义 expose，在 ./app-server/main/java/resources/application.yml 中定义
docker run -itd -p 8080:8080 -p 18080:18080 -p 10243:10243 --name={name} -e {JAVA_OPTS} nat-app
```

* Nginx 配置

```nginx
# 在 http 模块下增加 server：
server {
    listen       80;
    server_name  {自家代理的二级域名泛解析，对应阿里云/腾讯云控制台的配置，如 *.nat.baidu.com};
    location / {
	    proxy_pass http://localhost:{http 代理端口};
    }
}

# 可为【web 接口】、【socket 通信端口】 同时配置反向代理
server {
    listen       80;
    server_name  {精确的子域名，如natweb.baidu.com};
    location / {
	    proxy_pass http://localhost:{web 接口};
    }
}

# 这个没啥必要
server {
    listen       {要监听的端口};
    proxy_pass localhost:{socket 通信端口};
}
```

### To do list

- [x] client、server 的 yml 配置
- [x] http 代理
- [x] tunnel 信息的数据库保存
- [ ] server 端接入 github 认证
- [ ] server 端的管理配置（接口，ui 待定）
- [ ] client 程序关闭 console 日志，补充启动时 console 的信息输出
- [ ] client 端的异常转发
- [ ] 其他 tcp 代理
- [ ] to do 中的战斗机……

### QA

* 这是个什么东西？

> 内网穿透工具，通过报文转发，将部署在内网的服务映射到公网中提供访问。

* 会不会很慢？

> 会，绝对会。相对于直接暴露的公网服务，这多了一层数据转发，肯定会相对慢一点，在文件上传、下载时会更明显一些，也要看部署公网服务的云主机够不够power。

* 为什么要自己搞？用别人的不行嘛？Ngrok、花生壳这些不香？

> 香，都挺香，但从小穷到大穷，但仍希望各位大佬用💰这种肮脏的东西往我脑袋上砸。
> 对着 Netty 不知道学个什么鬼，就用这东西来做个切入点学学 Netty，面试时候可以跟面试官吹个牛X唠嗑唠嗑。
---

## 声明

<div style="color:red;"> <span style="font-weight:bolder;">&gt;&gt;</span> 使用本工具作非正规用途的（包括但不限于搭梯，观看、发布限制级内容），出现的一切问题与本人及本工具无关。</div>
