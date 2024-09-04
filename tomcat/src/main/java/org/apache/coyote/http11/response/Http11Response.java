package org.apache.coyote.http11.response;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Http11Response {

    private Http11ResponseStartLine startLine;
    private final Map<String, List<String>> headers;
    private String body;

    public Http11Response() {
        this(Http11ResponseStartLine.defaultLine(), new TreeMap<>(CASE_INSENSITIVE_ORDER), null);
    }

    public Http11Response(Http11ResponseStartLine startLine, Map<String, List<String>> headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public void sendRedirect(String url) {
        this.startLine = new Http11ResponseStartLine(HttpStatusCode.FOUND);
        headers.put("Location", List.of(url));
    }

    public void addHeader(String key, String value) {
        headers.put(key, List.of(value));
    }

    public void addCookie(String key, String value) {
        headers.computeIfAbsent("Set-Cookie", k -> new ArrayList<>())
                .add(key + "=" + value);
    }

    public void addBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        String headerString = headerToString();

        if (body == null) {
            return String.join(
                    "\r\n",
                    startLine.toString(),
                    headerString,
                    ""
            );
        }

        return String.join(
                "\r\n",
                startLine.toString(),
                headerString,
                "",
                body
        );
    }

    private String headerToString() {
        if (body != null) {
            headers.put("Content-Length", List.of(String.valueOf(body.getBytes().length)));
        }

        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(";", entry.getValue()) + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
