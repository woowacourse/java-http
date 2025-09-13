package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private static final String HTTP11_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String COOKIE_HEADER = "Set-Cookie";
    private static final String LOCATION_HEADER = "Location";

    private final String protocolVersion;
    private int statusCode;
    private String statusMessage;
    private final Map<String, String> headers;
    private byte[] body;

    public Http11Response() {
        this(HTTP11_VERSION, HttpStatus.OK.getCode(), HttpStatus.OK.name(), new LinkedHashMap<>(), new byte[0]);
    }

    public Http11Response status(final HttpStatus httpStatus) {
        this.statusCode = httpStatus.getCode();
        this.statusMessage = httpStatus.name();

        return this;
    }

    public Http11Response contentType(final String value) {
        this.headers.put(CONTENT_TYPE_HEADER, value);

        return this;
    }

    public Http11Response cookie(final String value) {
        this.headers.put(COOKIE_HEADER, value);

        return this;
    }

    public Http11Response redirect(final String location) {
        this.statusCode = HttpStatus.FOUND.getCode();
        this.statusMessage = HttpStatus.FOUND.name();
        this.headers.put(LOCATION_HEADER, location);

        return this;
    }

    public Http11Response body(final byte[] body) {
        this.body = body;

        return this;
    }

    public void build() {
        this.headers.put(CONTENT_LENGTH_HEADER, String.valueOf(this.body.length));
    }

    public byte[] toMessage() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        final List<String> headerStrings = new ArrayList<>();
        for (final String key : headers.keySet()) {
            headerStrings.add(String.format("%s: %s ", key, headers.get(key)));
        }
        final String headerString = String.join("\r\n", headerStrings);

        if (body == null) {
            return String.join("\r\n",
                    String.format("%s %s %s ", protocolVersion, statusCode, statusMessage),
                    headerString,
                    ""
            );
        }
        return String.join("\r\n",
                String.format("%s %s %s ", protocolVersion, statusCode, statusMessage),
                headerString,
                "",
                new String(body, StandardCharsets.UTF_8)
        );
    }

    private Http11Response(
            final String protocolVersion,
            final int statusCode,
            final String statusMessage,
            final Map<String, String> headers,
            final byte[] body
    ) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }
}
