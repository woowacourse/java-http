package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.join;

public class HttpResponse {

    public static final String CRLF = "\r\n";
    private HttpStatus httpStatus;
    private ContentType contentType;
    private String body;
    private Map<String, String> headers;

    public HttpResponse(final HttpStatus httpStatus) {
        this(httpStatus, new ContentType("text/plain", "utf-8"));
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType) {
        this(httpStatus, contentType, null);
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body) {
        this(httpStatus, contentType, body, new HashMap<>());
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body,
                        final Map<String, String> headers) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public HttpResponse setCookie(String key, String value) {
        if (headers.containsKey("Set-Cookie")) {
            headers.put("Set-Cookie", headers.get("Set-Cookie") + "; " + key + "=" + value);
            return new HttpResponse(httpStatus, contentType, body, headers);
        }
        headers.put("Set-Cookie", key + "=" + value);
        return new HttpResponse(httpStatus, contentType, body, headers);
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return new HttpResponse(httpStatus, contentType, body, headers);
    }

    private String getHeaders() {
        String joined = joinHeaders();
        if (body == null) {
            return joined;
        }
        if (joined.isBlank()) {
            return contentType + CRLF + "Content-Length: " + body.getBytes().length + " ";
        }
        return join(CRLF,
                joined,
                contentType.toString(),
                "Content-Length: " + body.getBytes().length + " ");
    }

    private String joinHeaders() {
        String joined = "";
        if (headers != null) {
            joined = headers.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                    .collect(Collectors.joining(CRLF));
        }
        return joined;
    }

    private String getBody() {
        if (body == null) {
            return "";
        }
        return CRLF + body;
    }

    public String buildResponse() {
        String joinedHeader = getHeaders();

        String startLine = "HTTP/1.1 " + httpStatus + " ";
        String withHeader = join(CRLF, startLine, joinedHeader);

        return join(CRLF, withHeader, getBody());
    }

}
