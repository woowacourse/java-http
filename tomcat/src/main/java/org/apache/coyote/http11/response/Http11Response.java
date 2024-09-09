package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class Http11Response implements HttpResponse {

    private String protocol;
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public Http11Response() {
        this.protocol = "HTTP/1.1";
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    @Override
    public String getResponseMessage() {
        String headerMessage = headers.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                protocol + " " + statusCode + " " + statusMessage + " ",
                headerMessage,
                "",
                body
        );
    }

    @Override
    public void setStatus(HttpStatus status) {
        this.statusCode = status.statusCode();
        this.statusMessage = status.statusMessage();
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public void setBody(String body) {
        this.body = body;
        setHeader("Content-Length", body.getBytes().length + " ");
    }
}
