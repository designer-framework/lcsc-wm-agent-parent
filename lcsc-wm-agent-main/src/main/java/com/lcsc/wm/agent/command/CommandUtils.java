package com.lcsc.wm.agent.command;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-06-16 01:38
 */
public class CommandUtils {

    public static String execute(String command) {
        return execute(command, Duration.ofSeconds(2));
    }

    public static String execute(String command, Duration timeout) {
        CommandLine commandLine = CommandLine.parse(command);
        try (ByteArrayOutputStream outputConsole = new ByteArrayOutputStream();) {
            //
            DefaultExecutor defaultExecutor = DefaultExecutor.builder().setExecuteStreamHandler(new PumpStreamHandler(outputConsole)).get();
            //设置超时时间
            defaultExecutor.setWatchdog(ExecuteWatchdog.builder().setTimeout(timeout).get());
            int execute = defaultExecutor.execute(commandLine);

            return new String(outputConsole.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
