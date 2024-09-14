package org.apache.coyote.http11.response;


import org.apache.coyote.http11.HttpStatus;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private final Map<String, String> headers = new TreeMap<>();
    private HttpStatus status = HttpStatus.OK;
    private String body;

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }


    public String normalize() {
        StringBuilder response = new StringBuilder("HTTP/1.1 " + status.getCode() + " " + status.getMessage());
        response.append(" \r\n");

        for (var entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ").append(entry.getValue()).append(" \r\n");
        }

        addBody(response);
        return response.toString();
    }

    private void addBody(StringBuilder base) {
        if (body == null) {
            return;
        }
        base.append("Content-Length: ").append(body.getBytes().length).append(" \r\n")
                .append("\r\n")
                .append(body);
    }
}
