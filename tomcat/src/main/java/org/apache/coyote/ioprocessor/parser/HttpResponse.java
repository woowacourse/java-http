package org.apache.coyote.ioprocessor.parser;

import http.HttpResponseHeaders;
import http.requestheader.HttpStatusCode;

public class HttpResponse {

    private final HttpStatusCode code;
    private final String responseBody;
    private final HttpResponseHeaders responseHeaders;

    private HttpResponse(HttpStatusCode code, String responseBody, HttpResponseHeaders responseHeaders) {
        this.code = code;
        this.responseBody = responseBody;
        this.responseHeaders = responseHeaders;
    }

    public HttpResponse(HttpStatusCode code, String mediaType, String responseBody) {
        this(code, responseBody, new HttpResponseHeaders(mediaType, responseBody.getBytes().length));
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
