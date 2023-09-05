package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Header;

public class HttpResponse {

    private static final String CHARSET_POSTFIX = ";charset=utf-8";
    private static final String ACCEPT_DELIMITER = ",";
    private static final String TEXT_TYPE = "text";

    private final Map<String, String> headers = new LinkedHashMap<>();

    private StatusCode statusCode;
    private String protocol;
    private String body;

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(" ").append(statusCode.format()).append(" \r\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append(" \r\n"));
        sb.append("\r\n").append(body);

        return sb.toString();
    }

    public HttpResponse body(String body) {
        this.body = body;
        headers.put(Header.CONTENT_LENGTH.getName(), String.valueOf(body.getBytes().length));
        return this;
    }

    public HttpResponse contentType(String accept) {
        if (accept.contains(ACCEPT_DELIMITER)) {
            accept = accept.split(ACCEPT_DELIMITER)[0];
        }
        if (accept.equals(ContentType.ALL.getType())) {
            accept = ContentType.HTML.getType();
        }
        if (accept.contains(TEXT_TYPE)) {
            accept += CHARSET_POSTFIX;
        }
        headers.put(Header.CONTENT_TYPE.getName(), accept);
        return this;
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponse statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }
}
