package com.lcsc.wm.agent;

import com.lcsc.wm.agent.command.CommandUtils;
import com.lcsc.wm.agent.model.AttachVO;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;

public class Attach2JVM {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, InterruptedException {
        AttachVO attachVO = toAttachVO(args);
        VirtualMachine vm = attach2JVM(parserPid(attachVO.getMainClassName(), Duration.ofSeconds(20)));
        loadAgent(vm, attachVO.getAgentJarPath());
        vm.detach();
    }

    private static void loadAgent(VirtualMachine vm, String agentJarPath) throws IOException {
        try {
            vm.loadAgent(agentJarPath);
        } catch (AgentLoadException | AgentInitializationException e) {
            throw new RuntimeException("加载失败", e);
        }
    }

    private static VirtualMachine attach2JVM(String mainPid) throws IOException, AttachNotSupportedException, InterruptedException {
        VirtualMachine vm = VirtualMachine.attach(mainPid);
        while (vm == null) {
            Thread.sleep(1000);
            vm = VirtualMachine.attach(mainPid);
        }
        return vm;
    }

    private static String parserPid(String mainClassName, Duration timeout) throws InterruptedException {
        long timeoutMillis = timeout.toMillis();
        long connectStartMillis = System.currentTimeMillis();
        while (System.currentTimeMillis() - connectStartMillis < timeoutMillis) {
            try {
                return parserPid(mainClassName);
            } catch (IllegalArgumentException ignored) {
                Thread.sleep(1000);
            }
        }

        throw new IllegalStateException("进程" + mainClassName + "连接超时");
    }

    private static String parserPid(String mainClassName) {
        //执行jvm命令, 列出运行中的main方法
        String execute = CommandUtils.execute("jps -l");

        //筛选目标main方法
        List<String> runtimeMains = IOUtils.readLines(new StringReader(execute));
        return runtimeMains.stream()
                .map(StringUtils::split)
                .filter(line -> {
                    if (line.length < 2) {
                        return false;
                    } else {
                        return StringUtils.containsIgnoreCase(mainClassName, StringUtils.trim(line[1]));
                    }
                })
                .map(line -> StringUtils.trim(line[0]))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("进程" + mainClassName + "不存在"));
    }

    private static AttachVO toAttachVO(String[] args) {
        Validate.isTrue(args.length == 2, "输入有误");

        AttachVO.AttachVOBuilder attachVOBuilder = AttachVO.builder();
        Validate.notEmpty(args[0], "请输入待Attach的进程类");
        attachVOBuilder.mainClassName(StringUtils.trim(args[0]));

        Validate.notEmpty(args[1], "请指定待插装的Jar包");
        attachVOBuilder.agentJarPath(StringUtils.trim(args[1]));

        return attachVOBuilder.build();
    }

}
