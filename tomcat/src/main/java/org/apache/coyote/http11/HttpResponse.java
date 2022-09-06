package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private final String statusLine;
    private final Map<String, String> responseHeaders;
    private String responseBody;

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
        this.responseHeaders = new HashMap<>();
        this.responseBody = "";
    }

    public void addBody(final String responseBody) {
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));
        this.responseBody = responseBody;
    }

    public void addHeader(final String name, final String value) {
        responseHeaders.put(name, value);
    }

    public byte[] writeValueAsBytes() {
        return writeValueAsString().getBytes();
    }

    public String writeValueAsString() {
        String response = statusLine;
        for (String key : responseHeaders.keySet()) {
            response += key + ": " + responseHeaders.get(key) + " \r\n";
        }
        response += "\r\n";
        response += responseBody;
        return response;
    }

    public boolean hasBody() {
        return responseBody.length() > 0;
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
                .equals(responseHeaders, that.responseHeaders) && Objects.equals(responseBody, that.responseBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, responseHeaders, responseBody);
    }
}
