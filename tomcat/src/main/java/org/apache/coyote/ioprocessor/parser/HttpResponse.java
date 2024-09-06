package org.apache.coyote.ioprocessor.parser;

import http.HttpHeader;
import http.HttpResponseHeaders;
import http.HttpStatusCode;
import java.util.List;

public class HttpResponse {

    private final HttpStatusCode code;
    private final String responseBody;
    private final HttpResponseHeaders responseHeaders;

    private HttpResponse(HttpStatusCode code, String responseBody, HttpResponseHeaders responseHeaders) {
        this.code = code;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
    }

    public HttpResponse(HttpStatusCode code, String mediaType, String responseBody, List<HttpHeader> headers) {
        this(code, responseBody, new HttpResponseHeaders(mediaType, responseBody.getBytes().length, headers));
    }

    public HttpResponse(HttpStatusCode code, String mediaType, String responseBody) {
        this(code, responseBody, new HttpResponseHeaders(mediaType, responseBody.getBytes().length, List.of()));
    }

    public HttpResponse(HttpStatusCode code, String mediaType, List<HttpHeader> headers) {
        this(code, "", new HttpResponseHeaders(mediaType, 0, headers));
    }

    public String buildResponse() {
        return String.join("\r\n",
                code.buildStatusCode(),
                responseHeaders.buildHeaders(),
                "",
                responseBody
        );
    }
}
