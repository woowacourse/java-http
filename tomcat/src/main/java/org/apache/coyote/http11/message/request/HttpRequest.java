package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.message.common.HttpHeaders;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpRequest(InputStream inputStream) throws IOException { //TODO : 파싱 클래스 분리
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        requestLine = generateHttpLine(bufferedReader);
        headers = generateHeaders(bufferedReader);
        body = generateBody(bufferedReader);
    }

    private RequestLine generateHttpLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP line은 null일 수 없습니다.");
        }
        return new RequestLine(line);
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
        return requestLine.isGet();
    }

    public boolean isPostMethod() {
        return requestLine.isPost();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
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
                "httpLine=" + requestLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
