package org.apache.coyote.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.common.HttpStatus;

public class HttpResponseBuilder {

    private static final Map<String, HttpContentType> contentMapper = new HashMap<>();
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

    public static void buildStaticContent(
            final HttpResponse httpResponse,
            final String fileName,
            final List<String> contentLines
    ) {
        final String fileContent = String.join("\r\n", contentLines);
        final String mediaType = httpContentType(fileName);
        int contentLength = fileContent.getBytes(StandardCharsets.UTF_8).length;

        httpResponse.setProtocol(HTTP_VERSION_PROTOCOL);
        httpResponse.setStatusCode(HttpStatus.OK.statusCode());
        httpResponse.setStatusMessage(HttpStatus.OK.statusMessage());
        httpResponse.addHeaders(CONTENT_TYPE, mediaType + ";" + CHARSET_UTF_8);
        httpResponse.addHeaders(CONTENT_LENGTH, String.valueOf(contentLength));
        httpResponse.setBody(fileContent);
    }

    private static String httpContentType(String fileName) {
        return contentMapper.get(fileName.substring(fileName.lastIndexOf(".") + 1)).mediaType();
    }

    public static void setRedirection(HttpResponse httpResponse, String redirectUrl) {
        httpResponse.setStatusCode(HttpStatus.FOUND.statusCode());
        httpResponse.setStatusMessage(HttpStatus.FOUND.statusMessage());
        httpResponse.setProtocol(HTTP_VERSION_PROTOCOL);
        httpResponse.addHeaders("Location", redirectUrl);
        httpResponse.addHeaders(CONTENT_TYPE, HttpContentType.HTML.mediaType() + ";" + CHARSET_UTF_8);
        httpResponse.setBody("");
    }

    public static void buildNotFound(final HttpResponse httpResponse, final List<String> contentLines) {
        final String fileContent = String.join("\r\n", contentLines);
        int contentLength = fileContent.getBytes(StandardCharsets.UTF_8).length;

        httpResponse.setProtocol(HTTP_VERSION_PROTOCOL);
        httpResponse.setStatusCode(HttpStatus.NOT_FOUND.statusCode());
        httpResponse.setStatusMessage(HttpStatus.NOT_FOUND.statusMessage());
        httpResponse.addHeaders(CONTENT_TYPE, HttpContentType.HTML.mediaType() + ";" + CHARSET_UTF_8);
        httpResponse.addHeaders(CONTENT_LENGTH, String.valueOf(contentLength));
        httpResponse.setBody(fileContent);
    }
}
