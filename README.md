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
    <spring-boot.version>3.0.0</spring-boot.version>
    <netty.version>4.1.84.Final</netty.version>
    <guava.version>31.1-jre</guava.version>
    <jetbrains-anno.version>23.1.0</jetbrains-anno.version>
    <reactor.version>2022.0.10</reactor.version>
</properties>

```

### 硬件需求

> 由一台公网服务器，与一堆内网主机组成。

### 编译

```shell
mvn package

# 得到两个执行包：
# ./app-client/client.jar
# ./app-server/server.jar
```

### 运行

```shell
# 启动服务端（公网[云]主机）
java -jar ./app-server/server.jar [-Dxxx=xx]

# 启动客户端（内网主机）
java -jar ./app-client/client.jar [-Dxxx=xx]
```

### Docker 部署
启动服务端，编写Dockerfile
```dockerfile
# 找一个能用的 java17镜像包，写好代码时候就这个能用……
FROM ghcr.io/graalvm/jdk:java17

# 9527端口使用udp协议，用于接收命令相关的udp请求
EXPOSE 9527/udp
# 8000端口使用tcp协议，用于接手客户端的代理通道（channel）注册
EXPOSE 8000

# 8080端口使用tcp协议，为http代理的外网端口
# 如需要使用https，请自备Nginx……
EXPOSE 8080

# 自行处理构建出来的server.jar路径
COPY path/to/server.jar /app.jar

ENV JAVA_OPS=""

ENV JVM_OPS="-Xmx1g -Xms1g"

CMD java ${JVM_OPS} -jar /app.jar ${JAVA_OPS}
```

然后
```shell
docker build -t nat-server path/to/Dockerfile
docker run -itd --name=nat-server -path 9527:9527/udp -p 8080:8080 -p 8000:8000 nat-server
```

启动客户端，类似服务端，自行脑补～

### To do list

-[x] client、server 的 yml 配置
-[ ] client 端的异常转发
-[ ] server 端的管理配置（接口，ui 待定）
-[x] http 代理
-[ ] 其他 tcp 代理
-[ ] to do 中的战斗机……

---

## 声明

<div style="color:red;"> <span style="font-weight:bolder;">&gt;&gt;</span> 使用本工具作非正规用途的（包括但不限于搭梯，观看、发布限制级内容），出现的一切问题与本人及本工具无关。</div>
