package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.Session;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class Http11Response implements HttpResponse {

    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";

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
    public void setLocationHeader(String value) {
        this.headers.put("Location", value);
    }

    @Override
    public void setContentTypeHeader(MimeType mimeType) {
        this.headers.put("Content-Type", mimeType.value());
    }

    @Override
    public void setSessionHeader(Session session) {
        this.headers.put("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId());
    }

    @Override
    public void setBody(String body) {
        this.body = body;
        this.headers.put("Content-Length", body.getBytes().length + " ");
    }
}
