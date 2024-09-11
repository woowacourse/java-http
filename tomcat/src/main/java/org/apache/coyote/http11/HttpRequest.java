package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    private final HttpLine httpLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        httpLine = generateHttpLine(bufferedReader);
        headers = generateHeaders(bufferedReader);
        body = generateBody(bufferedReader);
    }

    private HttpLine generateHttpLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP line은 null일 수 없습니다.");
        }
        return new HttpLine(line);
    }

    private HttpHeaders generateHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return new HttpHeaders(headerLines);
    }

    private String generateBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = headers.getContentLength();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isGetMethod() {
        return httpLine.isGet();
    }

    public boolean isPostMethod() {
        return httpLine.isPost();
    }

    public String getPath() {
        return httpLine.getPath();
    }

    public String getCookies() {
        return headers.getCookies();
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpLine=" + httpLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
