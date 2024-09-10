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
    public void ok(MimeType mimeType, String body) {
        setStatus(HttpStatus.OK);
        setBody(mimeType, body);
    }

    @Override
    public void found(String path) {
        setStatus(HttpStatus.FOUND);
        this.headers.put("Location", path);
    }

    @Override
    public void notFound(String body) {
        setStatus(HttpStatus.NOT_FOUND);
        setBody(MimeType.HTML, body);
    }

    @Override
    public void setSession(Session session) {
        this.headers.put("Set-Cookie", SESSION_ID_COOKIE_NAME + "=" + session.getId());
    }

    private void setStatus(HttpStatus status) {
        this.statusCode = status.statusCode();
        this.statusMessage = status.statusMessage();
    }

    private void setBody(MimeType mimeType, String body) {
        this.body = body;
        this.headers.put("Content-Type", mimeType.value());
        this.headers.put("Content-Length", body.getBytes().length + " ");
    }
}
