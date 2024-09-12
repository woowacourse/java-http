package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private static final String NEWLINE = "\r\n";
    private static final String END_OF_HEADERS = "";

    private final HttpStatusLine statusLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(String protocolVersion, HttpStatusCode statusCode, List<String> headers) {
        this(protocolVersion, statusCode, headers, "");
    }

    public HttpResponse(String protocolVersion, HttpStatusCode statusCode, List<String> headers, String body) {
        List<String> updatedHeaders = new ArrayList<>(headers);
        updatedHeaders.add("Content-Length: " + body.getBytes().length);

        this.statusLine = new HttpStatusLine(protocolVersion, statusCode);
        this.headers = new HttpHeaders(updatedHeaders);
        this.body = body;
    }

    public String convertMessage() {
        return String.join(
                NEWLINE,
                statusLine.convertMessage(),
                headers.convertMessage(),
                END_OF_HEADERS,
                body
        );
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", bodyLength='" + body.getBytes().length + '\'' +
                '}';
    }
}
