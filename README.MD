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

# 2. [性能分析工具介绍](https://github.com/linyimin0812/spring-startup-analyzer/tree/main)

> 1. spring-startup-analyzer性能分析工具简介

- (1). 该工具是由Arthas工具魔改而成
- (2). 该工具的核心是利用javaagent字节码插装技术对Spring加载Bean的一些关键方法进行增强
  <br> 然后借助字节码类库对方法的调用数据进行采集, 然后将火焰图, 前端UI......等开源框架聚合到本项目中,
  最后通过Netty启动Http服务器将数据展示出来
    * 字节码增强使用的是阿里的bytekit字节码类库。 在熟悉相关API后可以很简单的对代码进行增强, 以及调用数据进行监听。
    * 作者利用picocontainer(轻量级依赖注入框架)对方法调用数据的监听做了拓展, 使得用户可以通过JDK自带的SPI机制完成对方法调用数据的监听
      <br>参阅: io.github.linyimin0812.profiler.core.container.IocContainer#start()
    * spring-startup-analyzer的无用Jar包分析功能是不准确的, 仅供参考

> 2. Agent入口(修改类加载器, 类插装)

- io.github.linyimin0812.profiler.agent.ProfilerAgentBoostrap

> 3. 性能分析报告HttpServer启动类

- io.github.linyimin0812.profiler.core.http.SimpleHttpServer
- io.github.linyimin0812.profiler.core.http.SimpleHttpServer
- io.github.linyimin0812.profiler.common.ui.StartupVO

> 3. Arthas Server端命令行处理类

- com.taobao.arthas.core.server.ArthasBootstrap#bind(Configure configure)
- com.taobao.arthas.core.command.BuiltinCommandPack
- com.taobao.arthas.core.server.ArthasBootstrap#httpApiHandler
- com.taobao.arthas.core.shell.term.impl.http.TtyServerInitializer

项目启动完成

- [Http 服务 点此查看性能分析报告](http://127.0.0.1:8563)
- [Netty服务 点此查看性能分析报告](http://127.0.0.1:8563)
-

参阅

- 注释<br>
    * Classloader类加载原理
        - https://******
    * Arthas 实现插装的原理:
        - https://yeas.fun/archives/arthas-isolation
        - https://blog.csdn.net/baidu_28523317/article/details/108439696
    * picocontainer(轻量级依赖注入框架):
        - https://github.com/picocontainer/picocontainer

缺点:

1. xxxxxx
   <br>

功能拓展:

1. 监听AOP切面耗时
2. 监听Apollo耗时
3. 监听主流框架加载耗时
