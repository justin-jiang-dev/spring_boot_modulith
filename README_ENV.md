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
