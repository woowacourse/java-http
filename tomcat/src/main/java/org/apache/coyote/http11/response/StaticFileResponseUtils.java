package org.apache.coyote.http11.response;

import com.techcourse.exception.UncheckedServletException;
import java.util.Map;
import org.apache.coyote.http11.util.StaticFileUtils;

public class StaticFileResponseUtils {
    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE = Map.ofEntries(
            Map.entry("html", "text/html;charset=utf-8"),
            Map.entry("css", "text/css;charset=utf-8"),
            Map.entry("js", "text/javascript;charset=utf-8"),
            Map.entry("svg", "image/svg+xml;charset=utf-8"));
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private StaticFileResponseUtils() {
    }

    public static boolean isExistFile(String resourceFilePath) {
        return StaticFileUtils.isExistStaticFile(resourceFilePath);
    }

    public static String makeResponseBody(String resourceFilePath) {
        return StaticFileUtils.readStaticFile(resourceFilePath);
    }

    public static String getContentType(String filePath) {
        int delimiterIndex = filePath.lastIndexOf(".");
        if (delimiterIndex == -1) {
            throw new UncheckedServletException("파일의 확장자가 존재하지 않습니다.");
        }
        String extension = filePath.substring(delimiterIndex + 1);

        if (EXTENSION_TO_CONTENT_TYPE.containsKey(extension)) {
            return EXTENSION_TO_CONTENT_TYPE.get(extension);
        }
        return DEFAULT_CONTENT_TYPE;
    }
}
