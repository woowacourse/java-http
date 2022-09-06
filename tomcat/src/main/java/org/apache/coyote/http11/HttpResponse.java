package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpResponse {

    private final String statusLine;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;
    private String body;

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder("HTTP/1.1 200 OK \r\n");
    }

    public static HttpResponseBuilder redirect(final String url) {
        return new HttpResponseBuilder("HTTP/1.1 302 Found \r\n")
                .header("Location", url);
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder("HTTP/1.1 404 Not Found \r\n");
    }

    HttpResponse(String statusLine) {
        this.statusLine = statusLine;
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        this.body = "";
    }

    public void addBody(final String responseBody) {
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        this.body = responseBody;
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addCookie(final String name, final String value) {
        cookies.put(name, value);
    }

    public boolean hasBody() {
        return body.length() > 0;
    }

    public byte[] writeValueAsBytes() {
        return writeValueAsString().getBytes();
    }

    public String writeValueAsString() {
        String response = statusLine;

        for (String key : headers.keySet()) {
            response += key + ": " + headers.get(key) + " \r\n";
        }

        if (!cookies.isEmpty()) {
            response += "Set-Cookie: " + parseCookies() + " \r\n";
        }

        response += "\r\n";
        response += body;
        return response;
    }

    private String parseCookies() {
        return cookies.keySet().stream()
                .map(name -> name + "=" + cookies.get(name))
                .collect(Collectors.joining("; "));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(statusLine, that.statusLine) && Objects
                .equals(headers, that.headers) && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, headers, body);
    }
}
