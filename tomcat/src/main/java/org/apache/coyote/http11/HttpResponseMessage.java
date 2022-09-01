package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseMessage {

    private final String statusLine;
    private final LinkedHashMap<String, String> headers;
    private final String body;

    public HttpResponseMessage(final String statusLine, final LinkedHashMap<String, String> headers, final String body) {
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
