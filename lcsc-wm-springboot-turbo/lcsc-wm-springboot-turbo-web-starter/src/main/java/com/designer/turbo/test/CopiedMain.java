package com.designer.turbo.test;

import com.designer.turbo.annotation.Test;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-01 01:13
 */
@Test
@Slf4j
public class CopiedMain {

    public static void main(String[] args) throws IOException {
        //AOP
        copySource(
                "D:\\TeamWork\\lcsc-wm-agent-parent\\test\\TemplateServiceImpl.java"
                , "D:\\TeamWork\\lcsc-wm-agent-parent\\lcsc-wm-springboot-turbo\\lcsc-wm-springboot-turbo-web-starter\\src\\main\\java\\com\\designer\\turbo\\test\\tests\\service\\Template%sServiceImpl.java"
                , "\n    @Test" +
                        "\n    public void test%s() {\n" +
                        "    }\n"
                , 100
                , 500
        );

        //
        copySource(
                "D:\\TeamWork\\lcsc-wm-agent-parent\\test\\TestAspectj.java"
                , "D:\\TeamWork\\lcsc-wm-agent-parent\\lcsc-wm-springboot-turbo\\lcsc-wm-springboot-turbo-web-starter\\src\\main\\java\\com\\designer\\turbo\\test\\tests\\aspectj\\Test%sAspectj.java"
                , "@Pointcut(\"(!execution(* com.designer.turbo.service.BaseService.*(..)) && (execution(* com.lcsc..*.*(..)) || execution(* com.designer..*.*(..)) || @annotation(com.designer.turbo.annotation.Test)))\")"
                , 1
                , 10
        );

    }

    public static void copySource(String sourceFilePath, String targetFilePath, String methodStr, int methodNum, int fileNum) throws IOException {
        String javaSource = getJavaSource(sourceFilePath);

        for (int index = 0; index < fileNum; index++) {

            String methods = buildServiceMethod(methodStr, methodNum);

            String classSource = StringUtils.replaceOnce(javaSource, "${index}", index + "");
            String classSource_1 = StringUtils.replaceOnce(classSource, "${method}", methods);
            try (
                    InputStream fileInputStream = new ByteArrayInputStream(classSource_1.getBytes(StandardCharsets.UTF_8));
            ) {
                FileUtils.copyInputStreamToFile(fileInputStream, new File(String.format(String.format(targetFilePath, index), index)));
            } catch (Exception e) {
                log.error("", e);
            }

        }

    }

    public static String buildServiceMethod(String methodSourceCode, int methodNum) throws IOException {
        StringBuilder methodsBuilder = new StringBuilder();
        for (int i = 0; i < methodNum; i++) {
            methodsBuilder.append(String.format(methodSourceCode, i));
        }
        return methodsBuilder.toString();
    }

    public static String getJavaSource(String source) throws IOException {
        File file0 = new File(source);
        return FileUtils.readFileToString(file0, StandardCharsets.UTF_8);
    }

}
