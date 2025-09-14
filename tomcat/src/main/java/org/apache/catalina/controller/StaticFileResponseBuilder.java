package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.ResponseUtil;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.HttpStatus;

public class StaticFileResponseBuilder {

    public static final String STATIC_FILE_PATH = "static/";

    public static HttpResponse buildStaticFileResponse(String filePath, String httpVersion) throws IOException {
        InputStream inputStream = StaticFileResponseBuilder.class.getClassLoader().getResourceAsStream(STATIC_FILE_PATH + filePath);
        if (inputStream == null) {
            return ResponseUtil.buildNotFoundResponse(httpVersion);
        }

        String content = StaticFileReader.readFile(inputStream);
        return buildResponse(content, filePath, httpVersion);
    }

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8",
            ".html", "text/html;charset=utf-8"
    );

    private static final String DEFAULT_CONTENT_TYPE = "text/plain;charset=utf-8";

    public static HttpResponse buildResponse(String content, String filePath, String httpVersion) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", getContentType(filePath));

        return new HttpResponse(
                httpVersion,
                HttpStatus.OK.getCode(),
                HttpStatus.OK.getReasonPhrase(),
                headers,
                content
        );
    }

    private static String getContentType(String filePath) {
        return CONTENT_TYPES.entrySet().stream()
                .filter(entry -> filePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_CONTENT_TYPE);
    }
}