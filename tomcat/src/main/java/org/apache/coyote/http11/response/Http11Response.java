package org.apache.coyote.http11.response;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.Session;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;

public class Http11Response implements HttpResponse {

    private static final String VERSION_OF_PROTOCOL = "HTTP/1.1";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String RESPONSE_LINE_FORMAT = "%s %s %s ";
    private static final String HEADER_FORMAT = "%s: %s";
    private static final String CONTENT_TYPE_FORMAT = "%s;charset=%s ";
    private static final String SESSION_FORMAT = "JSESSIONID=%s";
    private static final String HEADER_BODY_DELIMITER = "";

    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public Http11Response() {
        this.headers = new LinkedHashMap<>();
        this.body = "";
    }

    @Override
    public String getResponseMessage() {
        String headerMessage = headers.entrySet()
                .stream()
                .map(entry -> String.format(HEADER_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                String.format(RESPONSE_LINE_FORMAT, VERSION_OF_PROTOCOL, statusCode, statusMessage),
                headerMessage,
                HEADER_BODY_DELIMITER,
                body
        );
    }

    @Override
    public void ok(MimeType mimeType, String body, Charset charSet) {
        setStatus(HttpStatus.OK);
        setBody(mimeType, body, charSet);
    }

    @Override
    public void found(String path) {
        setStatus(HttpStatus.FOUND);
        this.headers.put(LOCATION, path);
    }

    @Override
    public void notFound(String body, Charset charset) {
        setStatus(HttpStatus.NOT_FOUND);
        setBody(MimeType.HTML, body, charset);
    }

    @Override
    public void setSession(Session session) {
        this.headers.put(SET_COOKIE, String.format(SESSION_FORMAT, session.getId()));
    }

    private void setStatus(HttpStatus status) {
        this.statusCode = status.statusCode();
        this.statusMessage = status.statusMessage();
    }

    private void setBody(MimeType mimeType, String body, Charset charset) {
        this.body = body;
        this.headers.put(CONTENT_TYPE,
                String.format(CONTENT_TYPE_FORMAT, mimeType.value(), charset.name().toLowerCase()));
        this.headers.put(CONTENT_LENGTH, body.getBytes().length + " ");
    }
}
