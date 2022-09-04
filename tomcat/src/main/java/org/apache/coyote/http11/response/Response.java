package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.response.header.ContentType;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Headers;
import org.apache.coyote.http11.response.header.StatusCode;

public class Response {

    private final StatusCode statusCode;
    private final Headers headers;
    private final String body;

    public Response(final ContentType contentType, final StatusCode statusCode, final Map<Header, String> headers,
                    final String body) {
        this.statusCode = statusCode;
        this.headers = Headers.of(contentType, body);
        this.headers.putAll(headers);
        this.body = body;
    }

    public Response(final ContentType contentType, final StatusCode statusCode, final String body) {
        this(contentType, statusCode, Map.of(), body);
    }

    public Response(final StatusCode statusCode, final String body) {
        this(ContentType.HTML, statusCode, Map.of(), body);
    }

    public static Response ofOk(final ContentType contentType, final String body) {
        return new Response(contentType, StatusCode.OK, body);
    }

    public String toText() {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode.toString() + " ",
                headers.toText(),
                "",
                body);
    }
}
