package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.request.RequestReader;

public class Response {

    private final String protocol;
    private final StatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public Response(RequestReader requestReader, StatusCode statusCode, String body) {
        this.statusCode = statusCode;
        this.protocol = requestReader.getProtocol();
        this.headers.put(Header.CONTENT_TYPE.getName(), requestReader.getContentType());
        this.headers.put(Header.CONTENT_LENGTH.getName(), String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(" ").append(statusCode.format()).append("\r\n");
        headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append("\r\n"));
        sb.append("\n").append(body);

        return sb.toString();
    }
}
