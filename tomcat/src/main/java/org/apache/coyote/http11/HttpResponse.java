package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(HttpStatus status, Map<String, String> headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public String getResponse() {
        List<String> response = new ArrayList<>();
        response.add("HTTP/1.1 " + status.getCode() + " " + status.name());
        for (String key : headers.keySet()) {
            response.add(key + ": " + headers.get(key));
        }
        response.add("\n");
        response.add(body);
        return String.join("\r\n", response);
    }
}
