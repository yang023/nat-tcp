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
java -jar ./app-server/app-server-{version}.jar [-Dxxx=xx]

# 启动客户端（内网主机）
java -jar ./app-client/app-client-{version}.jar [-Dxxx=xx]
```

### To do list

- [ ] client、server 的 yml 配置
- [ ] client 端的异常转发
- [ ] server 端的管理配置（接口，ui 待定）
- [x] http 代理
- [ ] 其他 tcp 代理
- [ ] to do 中的战斗机……

---

## 声明

<div style="color:red;"> <span style="font-weight:bolder;">&gt;&gt;</span> 使用本工具作非正规用途的（包括但不限于搭梯，观看、发布限制级内容），出现的一切问题与本人及本工具无关。</div>
