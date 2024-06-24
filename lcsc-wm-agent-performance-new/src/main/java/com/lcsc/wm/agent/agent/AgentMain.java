package com.lcsc.wm.agent.agent;

import com.lcsc.wm.agent.command.SpringTraceCommand;
import com.taobao.arthas.core.server.ArthasBootstrap;
import com.taobao.arthas.core.shell.ShellServer;
import com.taobao.arthas.core.shell.command.CommandResolver;
import com.taobao.arthas.core.shell.command.impl.AnnotatedCommandImpl;
import com.taobao.arthas.core.shell.system.impl.InternalCommandManager;

import java.lang.instrument.Instrumentation;
import java.util.Collections;
import java.util.List;

public class AgentMain {

    /**
     * @param agentArgs
     * @param instrumentation
     */
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("premain");

        ArthasBootstrap instance = ArthasBootstrap.getInstance();
        ShellServer shellServer = instance.getShellServer();
        InternalCommandManager commandManager = shellServer.getCommandManager();
        List<CommandResolver> resolvers = commandManager.getResolvers();
        resolvers.add(() -> Collections.singletonList(new AnnotatedCommandImpl(SpringTraceCommand.class)));
    }

}
