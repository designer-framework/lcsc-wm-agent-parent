# 1. 项目启动参数

~~~
-Denv=DEV
-XX:-RestrictContended
-javaagent:C:\software\work\space\lcsc-wm-agent-parent\lcsc-wm-agent-performance\target\lcsc-wm-agent-performance-1.0-SNAPSHOT.jar
~~~

~~~
-Denv=DEV
-XX:-RestrictContended
-javaagent:C:\software\work\space\lcsc-wm-agent-parent\arthas\arthas-agent.jar=C:\software\work\space\lcsc-wm-agent-parent\arthas\arthas-core.jar
~~~

# 2. arthas

> 1. agent入口

- com.taobao.arthas.core.server.AgentBootstrap

> 2. HttpServer启动类

- com.taobao.arthas.core.server.ArthasBootstrap#bind(Configure configure)
- com.taobao.arthas.core.command.BuiltinCommandPack
- com.taobao.arthas.core.server.ArthasBootstrap#httpApiHandler
- com.taobao.arthas.core.shell.term.impl.http.TtyServerInitializer
- [Http 服务](http://127.0.0.1:8563)
- [Netty服务](http://127.0.0.1:8563)

> 3. Command命令行处理类

- com.taobao.arthas.core.server.AgentBootstrap

参阅

- arthas如何做到代码与应用隔离
- https://yeas.fun/archives/arthas-isolation
