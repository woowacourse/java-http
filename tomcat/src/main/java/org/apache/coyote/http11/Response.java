package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {

    private static final String STATIC = "static";
    public static final String SPACE_CRLF = " \r\n";
    public static final String HTML_EXTENSION = ".html";
    public static final String PERIOD = ".";
    public static final String SPACE = " ";
    public static final String CRLF = "\r\n";
    public static final String COLON = ": ";

    private final RequestReader requestReader;
    private final StatusCode statusCode;
    private final Map<String, String> headers = new LinkedHashMap<>();

    private String body;

    public Response(RequestReader requestReader, StatusCode statusCode) {
        this.requestReader = requestReader;
        this.statusCode = statusCode;
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(requestReader.getProtocol()).append(SPACE).append(statusCode.format()).append(SPACE_CRLF);
        headers.forEach((key, value) -> sb.append(key).append(COLON).append(value).append(SPACE_CRLF));
        sb.append(CRLF).append(body);

        return sb.toString();
    }

    public Response createBodyByPlainText(String text) {
        this.body = text;
        headers.put(Header.CONTENT_LENGTH.getName(), String.valueOf(body.length()));
        return this;
    }

    public Response createResponseBodyByFile(String url) throws IOException {
        url = resolveExtension(url);
        String path = getClass().getClassLoader().getResource(STATIC + url).getPath();
        File file = new File(path);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            this.body = sb.toString();
        }
        headers.put(Header.CONTENT_LENGTH.getName(), String.valueOf(file.length()));
        return this;
    }

    private String resolveExtension(String url) {
        if (url.contains(PERIOD)) {
            return url;
        }
        return url + HTML_EXTENSION;
    }

    public Response addBaseHeaders() {
        headers.put(Header.CONTENT_TYPE.getName(), requestReader.getContentType());
        return this;
    }

    public Response addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
}
