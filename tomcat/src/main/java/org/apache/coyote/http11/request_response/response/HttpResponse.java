package org.apache.coyote.http11.request_response.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.request_response.HttpHeader;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final List<HttpHeader> headers;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, List<HttpHeader> headers, String body) {
        this.responseLine = new ResponseLine("HTTP/1.1", httpStatus);
        this.headers = new ArrayList<>(headers);
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    @Override
    public String toString() {
        String protocolVersion = responseLine.getProtocolVersion();
        int statusCode = responseLine.getStatusCode();
        String statusMessage = responseLine.getStatusMessage();
        StringBuilder sb = new StringBuilder();
        sb.append("%s %d %s ".formatted(protocolVersion, statusCode, statusMessage)).append("\r\n");
        for (HttpHeader httpHeader : headers) {
            sb.append("%s: %s ".formatted(httpHeader.name(), httpHeader.value())).append("\r\n");
        }
        sb.append("").append("\r\n");
        sb.append(body);
        return sb.toString();
    }
}
