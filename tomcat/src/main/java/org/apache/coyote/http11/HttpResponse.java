package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    public static final HttpResponse DEFAULT_HTTP_RESPONSE;

    static {
        String statusLine = "HTTP/1.1 200 OK";
        String body = "Hello world!";

        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", ContentType.HTML.getValue());
        headers.put("Content-Length", String.valueOf(body.getBytes().length));

        DEFAULT_HTTP_RESPONSE = new HttpResponse(statusLine, headers, body);
    }

    private final String statusLine;
    private final LinkedHashMap<String, String> headers;
    private final String body;

    public HttpResponse(final String statusLine, final LinkedHashMap<String, String> headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public String parseResponse() {
        StringBuilder headers = new StringBuilder();
        for (String key : this.headers.keySet()) {
            headers.append(key)
                    .append(": ")
                    .append(this.headers.get(key))
                    .append(" \r\n");
        }

        return statusLine + " \r\n" +
                headers +
                "\r\n" +
                body;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
