package org.apache.coyote.http11.response;


import org.apache.coyote.http11.HttpStatus;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private HttpStatus status = HttpStatus.OK;
    private Map<String, String> headers = new TreeMap<>();
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
        StringBuilder response = new StringBuilder(String.format("""
                        HTTP/1.1 %d %s
                        """,
                status.getCode(),
                status.getMessage()
        ));

        for (var entry : headers.entrySet()) {
            response.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\r\n");
        }

        addBody(response);
        return response.toString();
    }

    private void addBody(StringBuilder base) {
        if (body == null) {
            return;
        }
        base.append(String.format("""
                        Content-Length: %d
                                        
                        %s
                        """,
                body.getBytes().length,
                body
        ));
    }
}
