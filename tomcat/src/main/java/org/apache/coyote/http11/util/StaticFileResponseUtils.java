package org.apache.coyote.http11.util;

import com.techcourse.exception.UncheckedServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import javax.annotation.Nullable;

public class StaticFileResponseUtils {

    private static final String STATIC_RESOURCE_LOCATION = "static";
    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.ofEntries(
            Map.entry("html", "text/html;charset=utf-8"),
            Map.entry("css", "text/css;charset=utf-8"),
            Map.entry("js", "text/javascript;charset=utf-8"),
            Map.entry("svg", "image/svg+xml;charset=utf-8"));
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private StaticFileResponseUtils() {
    }

    public static boolean isExistFile(String resourceFilePath) {
        return getResource(resourceFilePath) != null;
    }

    public static String readStaticFile(String resourceFilePath) {
        URL resource = getResource(resourceFilePath);
        return readFile(resource);
    }

    @Nullable
    private static URL getResource(String filePath) {
        return StaticFileResponseUtils.class.getClassLoader()
                .getResource(STATIC_RESOURCE_LOCATION + filePath);
    }

    private static String readFile(@Nullable URL resource) {
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException | NullPointerException e) {
            throw new UncheckedServletException("해당 파일이 존재하지 않습니다.");
        }
    }

    public static String getContentType(String filePath) {
        int delimiterIndex = filePath.lastIndexOf(".");
        if (delimiterIndex == -1) {
            throw new UncheckedServletException("파일의 확장자가 존재하지 않습니다.");
        }
        String extension = filePath.substring(delimiterIndex + 1);

        return EXTENSION_TO_CONTENT_TYPE.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }
}
