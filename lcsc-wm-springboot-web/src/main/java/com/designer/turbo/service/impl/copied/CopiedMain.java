package com.designer.turbo.service.impl.copied;

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
        String source = "D:\\TeamWork\\lcsc-wm-agent-parent\\test\\TemplateServiceImpl.java";
        String target = "D:\\TeamWork\\lcsc-wm-agent-parent\\lcsc-wm-springboot-web\\src\\main\\java\\com\\designer\\turbo\\service\\impl\\tests\\Template%sServiceImpl.java";
        File file0 = new File(String.format(source, ""));

        String readFileToString = FileUtils.readFileToString(file0, StandardCharsets.UTF_8);
        String method =
                "\n    @Test" +
                        "\n    public void test%s() {\n" +
                        "    }\n";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            builder.append(String.format(method, i));
        }
        String replaced = StringUtils.replace(readFileToString, "${method}", builder.toString());

        for (int i = 0; i < 30; i++) {
            String className = StringUtils.replaceOnce(replaced, "${index}", "" + i);
            try (
                    InputStream fileInputStream = new ByteArrayInputStream(className.getBytes(StandardCharsets.UTF_8));
            ) {
                FileUtils.copyInputStreamToFile(fileInputStream, new File(String.format(target, i)));
            } catch (Exception e) {
                log.error("", e);
            }
        }

    }

}
