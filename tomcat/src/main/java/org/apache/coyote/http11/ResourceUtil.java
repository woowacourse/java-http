package org.apache.coyote.http11;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceUtil {

    public static String getResponseBody(final String path, final Class<?> classes) {
        try {
            URL resource = classes.getResource("/static" + path);
            return Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            return "파일을 불러올 수 없습니다.";
        }
    }
}
