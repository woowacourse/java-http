package org.apache.coyote.http11.response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.request.RequestReader;

public class Response {

    private static final String STATIC = "static";

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
        sb.append(requestReader.getProtocol()).append(" ").append(statusCode.format()).append(" \r\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append(" \r\n"));
        sb.append("\r\n").append(body);

        return sb.toString();
    }

    public Response createBodyByPlainText(String text) {
        this.body = text;
        headers.put(Header.CONTENT_LENGTH.getName(), String.valueOf(body.length()));
        return this;
    }

    public Response createResponseBodyByFile(String url) throws IOException {
        url = Resolver.resolve(url);
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

    public Response addBaseHeaders() {
        headers.put(Header.CONTENT_TYPE.getName(), requestReader.getContentType());
        return this;
    }

    public Response addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    private static class Resolver {

        private Resolver() {
        }

        public static String resolve(String url) {
            if (url.contains(".")) {
                return url;
            }
            return url + ".html";
        }
    }
}
