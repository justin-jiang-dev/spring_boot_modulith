# 开发环境配置之 VSCode
```bash
# 安装插件
code --install-extension vscjava.vscode-java-pack
code --install-extension rectcircle.vscode-p3c
```
# 开发环境配置之 Maven
## 配置国内 maven repository 镜像
```xml
<!-- maven settings.xml 文件(~/.m2/settings.xml)中进行如下修改 -->
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>central</mirrorOf>
      <name>aliyun maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
```  
# 开发环境配置之 H2 Database
## 如何查看 H2 数据库里的数据
使用 URL（http://localhost:8686/h2-console/） 启动 H2 console   

# 开发环境配置之启动本地 Redis
## 安装 redis
https://redis.io/docs/latest/operate/oss_and_stack/install/install-redis/install-redis-on-mac-os/
## 配置 redis cluster
创建 6 个 redis.conf 文件，在各自文件夹下，端口从7000 到 7005，以下是样例：  
```bash
  port 7000  

  cluster-enabled yes  

  cluster-config-file nodes-7000.conf  

  cluster-node-timeout 5000  

  appendonly yes
```

## 启动 redis cluster
```bash
  redis-server ./7001/redis.conf  &&
  redis-server ./7002/redis.conf  &&
  redis-server ./7003/redis.conf  &&
  redis-server ./7004/redis.conf  &&
  redis-server ./7005/redis.conf  &&
  redis-server ./7006/redis.conf
  redis-cli --cluster create 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 127.0.0.1:7006 --cluster-replicas 1
```
