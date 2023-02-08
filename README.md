# joshua-el-support

Java 的动态表达式支持，包括 `Groovy` `MVEL` `Aviator` ，后续加入 `FEL` 和其它表达式支持

## 依赖三方库

| 依赖                   | 版本号          | 说明                     |
|----------------------|--------------|------------------------|
| fastjson             | 1.2.73       |                        |
| commons-lang3        | 3.11         |                        |
| commons-collections4 | 4.4          |                        |
| groovy               | 3.0.10       | 只依赖了groovy和groovy-json |
| guava                | 29.0-jre     |                        |
| aviator              | 5.3.1        |                        |
| mvel                 | 2.4.14.Final |                        |
| lombok               | 1.18.16      |                        |

## 使用前准备

- [Maven](https://maven.apache.org/) (构建/发布当前项目)
- Java 8 ([Download](https://adoptopenjdk.net/releases.html?variant=openjdk8))

## 构建/安装项目

使用以下命令:

`mvn clean install`

## 引用项目

```xml

<dependency>
    <groupId>com.mogudiandian</groupId>
    <artifactId>joshua-el-support</artifactId>
    <version>LATEST</version>
</dependency>
```

## 发布项目

修改 `pom.xml` 的 `distributionManagement` 节点，替换为自己在 `settings.xml` 中 配置的 `server` 节点，
然后执行 `mvn clean deploy`

举例：

`settings.xml`

```xml
<servers>
    <server>
        <id>snapshots</id>
        <username>yyy</username>
        <password>yyy</password>
    </server>
    <server>
        <id>releases</id>
        <username>xxx</username>
        <password>xxx</password>
    </server>
</servers>
```

`pom.xml`

```xml
<distributionManagement>
    <snapshotRepository>
        <id>snapshots</id>
        <url>http://xxx/snapshots</url>
    </snapshotRepository>
    <repository>
        <id>releases</id>
        <url>http://xxx/releases</url>
    </repository>
</distributionManagement>
```
