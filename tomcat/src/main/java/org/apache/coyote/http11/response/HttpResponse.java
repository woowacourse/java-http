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

    private final HttpProtocol httpProtocol;
    private final HttpStatus httpStatus;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    private HttpResponse(HttpProtocol httpProtocol, HttpStatus httpStatus, String body) {
        this.httpProtocol = httpProtocol;
        this.httpStatus = httpStatus;
        this.body = body;
    }

    public static HttpResponse of(HttpProtocol httpProtocol, HttpStatus httpStatus, String body, ContentType contentType) {
        HttpResponse response = new HttpResponse(httpProtocol, httpStatus, body);
        response.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        response.addHeader(CONTENT_TYPE, contentType.getType());
        return response;
    }

    public static HttpResponse of(HttpProtocol httpProtocol, HttpStatus httpStatus) {
        return new HttpResponse(httpProtocol, httpStatus, null);
    }

    public String toResponseFormat() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        joiner.add(getStatusLine());
        joiner.add(getHeaderLines());
        joiner.add(body);
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
}
