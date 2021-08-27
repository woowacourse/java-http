package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GET /index.html HTTP/1.1        // Request Line
 * Host: localhost:8000           // Request Headers Connection:
 * keep-alive Upgrade-Insecure-Request: 1
 * Content-Type: text/html
 * Content-Length: 345
 *
 * something1=123&something2=123   // Request Message Body
 */

/**
 * GET /login?something1=123&something2=123  HTTP/1.1        // Request Line
 * Host: localhost:8000                                      // Request Headers Connection:
 * keep-alive Upgrade-Insecure-Request: 1
 * Content-Type: text/html
 * Content-Length: 345
 */

public class HttpRequest {

    private final RequestLine requestLine;
    private final List<String> headers;

    public HttpRequest(RequestLine requestLine, List<String> headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> lines) {
        RequestLine requestLine = RequestLine.of(lines.get(0));
        List<String> headers = lines.subList(1, lines.size());
        return new HttpRequest(requestLine, headers);
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();

        String tempLine;
        System.out.println("======LOG======");
        while (!Objects.isNull(tempLine = bufferedReader.readLine())) {
            System.out.println(tempLine);
            lines.add(tempLine);
        }
        return of(new ArrayList<>(lines));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
