package com.lcsc.wm.agent.attach;

import com.lcsc.wm.agent.agent.AgentLoader;

import java.lang.instrument.Instrumentation;

public class AgentMain {

    /**
     * @param agentArgs
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("AgentMain");
        AgentLoader agentLoader = new AgentLoader(inst);
        agentLoader.run();
    }

}
