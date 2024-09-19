package org.apache.coyote.http11.response;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.HttpStatus;

public class HttpResponse {
    private final Map<String, String> headers = new HashMap<>();
    private HttpStatus status = HttpStatus.OK;
    private String body;

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setCookie(String key, String value){
        headers.put("Set-Cookie", String.format("%s=%s;", key, value));
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

    public void setRedirect(String location) {
        setStatus(HttpStatus.FOUND);
        addHeader("Content-Type", "text/html");
        addHeader("Location", location);
    }

    public void setHomeRedirection(){
        setRedirect("/index.html");
    }

    private void addBody(StringBuilder base) {
        if (Objects.isNull(body)) {
            return;
        }

        base.append("Content-Length: ").append(body.getBytes().length)
                .append(" \r\n")
                .append("\r\n")
                .append(body);
    }
}
