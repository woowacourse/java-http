package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    private HttpRequest(RequestLine requestLine, Headers headers, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        Headers headers = Headers.of(getHeaders(bufferedReader));
        RequestBody requestBody = RequestBody.of(getBody(bufferedReader));

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> getHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private static List<String> getBody(BufferedReader bufferedReader) throws IOException {
        if (bufferedReader.ready()) {
            return readBody(bufferedReader);
        }
        return List.of();
    }

    private static List<String> readBody(BufferedReader bufferedReader) throws IOException {
        List<String> body = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            body.add(line);
        }
        return body;
    }

    public boolean matchRequestLine(HttpMethod httpMethod, Pattern uriPattern) {
        return requestLine.matchHttpMethod(httpMethod) && requestLine.matchUri(uriPattern);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Object getParameter(String key) {
        return requestLine.getParameter(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(headers,
                that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, headers);
    }
}
