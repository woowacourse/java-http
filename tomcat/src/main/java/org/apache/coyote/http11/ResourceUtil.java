package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.text.AbstractDocument.Content;

public class ResourceUtil {

    public static String getResponseBody(final String path, final Class<?> classes) {
        try {
            URL resource = classes.getResource("/static" + path);
            return Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            return "예외가 발생했습니다.";
        }
    }
}
