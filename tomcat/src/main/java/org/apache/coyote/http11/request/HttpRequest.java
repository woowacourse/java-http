package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        RequestLine requestLine = new RequestLine(reader.readLine());
        RequestHeaders requestHeaders = RequestHeaders.from(readHeaderLines(reader));
        RequestBody requestBody = readBody(reader, requestHeaders);

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private static List<String> readHeaderLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        return lines;
    }

    private static RequestBody readBody(BufferedReader reader, RequestHeaders headers) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return new RequestBody("", headers.getContentType());
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer), headers.getContentType());
    }

}
