package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Constants;
import org.apache.coyote.http11.types.ContentType;
import org.apache.coyote.http11.types.HeaderType;
import org.apache.coyote.http11.types.HttpProtocol;
import org.apache.coyote.http11.types.HttpStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.apache.coyote.http11.types.HeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.types.HeaderType.CONTENT_TYPE;

public class HttpResponse {

    private HttpProtocol httpProtocol;
    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpResponse(HttpProtocol httpProtocol, HttpStatus httpStatus, String body) {
        this.httpProtocol = httpProtocol;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(null, null, null);
    }

    public String toResponseFormat() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        joiner.add(getStatusLine());
        joiner.add(getHeaderLines());
        if (body != null) {
            joiner.add(body);
        }
        return joiner.toString();
    }

    private String getStatusLine() {
        return String.format("%s %s %s ", httpProtocol.getProtocol(), httpStatus.getCode(), httpStatus.getMessage());
    }

    private String getHeaderLines() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        for (var entry : this.headers.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            joiner.add(String.format("%s: %s ", name, value));
        }
        joiner.add("");
        return joiner.toString();
    }

    public void addHeader(HeaderType headerType, String value) {
        headers.put(headerType.getType(), value);
    }

    public void setHttpProtocol(HttpProtocol httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setBody(String body, ContentType contentType) {
        this.body = body;
        addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        addHeader(CONTENT_TYPE, contentType.getType());
    }
}
