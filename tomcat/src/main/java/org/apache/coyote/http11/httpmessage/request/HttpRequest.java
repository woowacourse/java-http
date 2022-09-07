package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
        Headers headers = Headers.of(extractHeaders(bufferedReader));
        RequestBody requestBody = new RequestBody(extractBody(bufferedReader, headers));

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headers = new LinkedList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            headers.add(line);
        }
        return headers;
    }

    private static String extractBody(BufferedReader bufferedReader, Headers headers) throws IOException {
        Optional<Object> contentLengthValue = headers.getHeader("Content-Length");

        if (contentLengthValue.isEmpty()) {
            return "";
        }

        int contentLength = Integer.parseInt((String) contentLengthValue.get());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean matchRequestLine(HttpMethod httpMethod, Pattern uriPattern) {
        return requestLine.matchHttpMethod(httpMethod) && requestLine.matchUri(uriPattern);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
