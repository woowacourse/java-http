package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.StatusCode;

public class Response {

    private final StatusCode statusCode;
    private final Map<String, String> headers;
    private final String body;

    public Response(final ContentType contentType, final StatusCode statusCode, final Map<String, String> headers,
                    final String body) {
        this.statusCode = statusCode;
        this.headers = new LinkedHashMap<>();
        this.headers.put("Content-Type", contentType.toString());
        this.headers.put("Content-Length", Integer.toString(body.getBytes().length));
        this.headers.putAll(headers);
        this.body = body;
    }

    public Response(final ContentType contentType, final StatusCode statusCode, final String body) {
        this(contentType, statusCode, Map.of(), body);
    }

    public String toText() {
        final String header = headers.entrySet().stream()
                .map(entry -> String.join(": ", entry.getKey(), entry.getValue() + " "))
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.toString() + " ",
                header,
                "",
                body);
    }
}
