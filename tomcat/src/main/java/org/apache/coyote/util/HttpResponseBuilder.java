package org.apache.coyote.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseBuilder {

    private static final Map<String, HttpContentType> contentMapper = new HashMap<>();
    private static final String STATUS_CODE_OK = "200 OK";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String HTTP_VERSION_PROTOCOL = "HTTP/1.1";
    private static final String CHARSET_UTF_8 = "charset=utf-8";

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
                buildLine(CONTENT_TYPE, mediaType + ";" + CHARSET_UTF_8),
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

    public static HttpResponse build(String fileName, List<String> contentLines, HttpStatus status) {
        final var fileContent = String.join("\r\n", contentLines);
        final HttpResponse response = new HttpResponse();

        String mediaType = httpContentType(fileName);
        int contentLength = fileContent.getBytes(StandardCharsets.UTF_8).length;
        response.setProtocol(HTTP_VERSION_PROTOCOL);
        response.setStatusCode(status.statusCode());
        response.setStatusMessage(status.statusMessage());
        response.addHeaders(CONTENT_TYPE, mediaType + ";" + CHARSET_UTF_8);
        response.addHeaders(CONTENT_LENGTH, String.valueOf(contentLength));
        response.setBody(fileContent);

        return response;
    }
}
