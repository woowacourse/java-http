package org.apache.coyote.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseBuilder {

    private static final Map<String, HttpContentType> contentMapper = new HashMap<>();
    private static final String STATUS_CODE_OK = "200 OK";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    static {
        contentMapper.put("html", HttpContentType.HTML);
        contentMapper.put("css", HttpContentType.CSS);
        contentMapper.put("js", HttpContentType.JS);
        contentMapper.put("json", HttpContentType.JSON);
    }

    private HttpResponseBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static String build(String fileName, List<String> contentLines) {
        var fileContent = String.join("\r\n", contentLines);

        String mediaType = httpContentType(fileName);
        int contentLength = fileContent.getBytes(StandardCharsets.UTF_8).length;

        return String.join(
                "\r\n",
                "HTTP/1.1 " + STATUS_CODE_OK + " ",
                buildLine(CONTENT_TYPE, mediaType + ";charset=utf-8"),
                buildLine(CONTENT_LENGTH, String.valueOf(contentLength)),
                "",
                fileContent
        );
    }

    private static String httpContentType(String fileName) {
        return contentMapper.get(fileName.substring(fileName.lastIndexOf(".") + 1)).mediaType();
    }

    private static String buildLine(String key, String value) {
        return String.join(": ", key, value) + " ";
    }
}
