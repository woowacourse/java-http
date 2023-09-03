package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String CRLF = "\r\n";
    private static final String WHITE_SPACE = " ";

    private final String method;
    private final String target;
    private final String version;
    private final List<HttpHeader> headers;
    private final String body;

    private HttpRequest(final String method, final String target, final String version, final List<HttpHeader> headers,
                        final String body) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String[] requestLineElements = reader.readLine().split(WHITE_SPACE);

            List<String> headerLines = new ArrayList<>();
            String next;
            while (reader.ready() && !(next = reader.readLine()).isEmpty()) {
                headerLines.add(next);
            }

            String body = reader.lines()
                    .collect(Collectors.joining(CRLF));

            return new HttpRequest(
                    requestLineElements[0],
                    requestLineElements[1],
                    requestLineElements[2],
                    headerLines.stream()
                            .map(HttpHeader::of)
                            .collect(Collectors.toList()),
                    body
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청을 잘 읽지 못했습니다");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public List<HttpHeader> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
