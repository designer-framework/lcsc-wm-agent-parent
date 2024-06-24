1. 项目启动参数

~~~
-Denv=DEV
-XX:-RestrictContended
-javaagent:C:\software\work\space\lcsc-wm-agent-parent\lcsc-wm-agent-performance\target\lcsc-wm-agent-performance-1.0-SNAPSHOT.jar
~~~

2.

~~~
-Denv=DEV
-XX:-RestrictContended
-javaagent:C:\software\work\space\lcsc-wm-agent-parent\arthas\arthas-agent.jar
~~~

控制台
<code>
com.taobao.arthas.core.shell.term.impl.http.TtyServerInitializer
</code>

启动类
<code>

- com.taobao.arthas.core.server.ArthasBootstrap#httpApiHandler
- Http  : 127.0.0.1:8563
- Telnet: 127.0.0.1:3658
  </code>

代码与应用隔离
https://yeas.fun/archives/arthas-isolation