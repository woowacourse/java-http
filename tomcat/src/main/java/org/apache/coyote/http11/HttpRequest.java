package org.apache.coyote.http11;

import static org.apache.catalina.Globals.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpRequestBody requestBody;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpRequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String firstLine = reader.readLine();
        RequestLine requestLine = RequestLine.from(firstLine);

        String line;
        List<String> headerLines = new ArrayList<>();
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        HttpHeaders headers = HttpHeaders.from(headerLines);

        HttpRequestBody body = HttpRequestBody.empty();
        if (requestLine.getMethod() == HttpMethod.POST) {
            int contentLength = Integer.parseInt(headers.getHeader(HttpHeader.CONTENT_LENGTH).get());
            char[] unparsedBody = new char[contentLength];
            if (contentLength > 0) {
                reader.read(unparsedBody, 0, contentLength);
            }

            String requestBody = new String(unparsedBody);
            String decodedBodyLine = URLDecoder.decode(requestBody, StandardCharsets.UTF_8);
            body = HttpRequestBody.from(decodedBodyLine);
        }

        return new HttpRequest(
            requestLine,
            headers,
            body
        );
    }

    public Optional<String> getSessionIdFromCookie() {
        return headers.getCookie(SESSION_COOKIE_NAME);
    }

    public boolean isHttp11VersionRequest() {
        return requestLine.isHttp11Version();
    }

    public HttpRequestBody getRequestBody() {
        if (requestLine.getMethod() == HttpMethod.GET) {
            throw new IllegalArgumentException("GET method don't have payload");
        }
        return requestBody;
    }

    public boolean hasMethod(HttpMethod method) {
        return this.requestLine.getMethod() == method;
    }

    public String getUri() {
        return requestLine.getPath();
    }
}
