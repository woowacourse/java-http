package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Http11Response {

    private static final String HTTP11_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";

    private String protocolVersion;
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private String body;

    public Http11Response() {
        this(null, 0, null, null, null);
    }

    public void setResponse(
            final HttpStatus httpStatus,
            final byte[] body,
            final String contentType
    ) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(body.length));

        this.protocolVersion = HTTP11_VERSION;
        this.statusCode = httpStatus.getCode();
        this.statusMessage = httpStatus.name();
        this.headers = headers;
        this.body = new String(body, StandardCharsets.UTF_8);
    }

    public void setRedirectResponse(final String redirectTarget) {
        final Map<String, String> headers = new LinkedHashMap<>();
        headers.put(LOCATION, redirectTarget);
        headers.put(CONTENT_LENGTH, "0");

        this.protocolVersion = HTTP11_VERSION;
        this.statusCode = HttpStatus.FOUND.getCode();
        this.statusMessage = HttpStatus.FOUND.name();
        this.headers = headers;
    }

    public void setRedirectResponse(
            final String redirectTarget,
            final Map<String, String> headers
    ) {
        headers.put(LOCATION, redirectTarget);
        headers.put(CONTENT_LENGTH, "0");

        this.protocolVersion = HTTP11_VERSION;
        this.statusCode = HttpStatus.FOUND.getCode();
        this.statusMessage = HttpStatus.FOUND.name();
        this.headers = headers;
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
                body
        );
    }

    private Http11Response(
            final String protocolVersion,
            final int statusCode,
            final String statusMessage,
            final Map<String, String> headers,
            final String body
    ) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }
}
