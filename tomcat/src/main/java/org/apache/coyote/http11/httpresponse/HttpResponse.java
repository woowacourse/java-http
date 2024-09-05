package org.apache.coyote.http11.httpresponse;

import java.util.Map;

public class HttpResponse {

    private final HttpStatusLine httpStatusLine;
    private final HttpResponseHeader httpResponseHeader;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpResponseHeader httpResponseHeader,
            HttpResponseBody httpResponseBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpResponseHeader = httpResponseHeader;
        this.httpResponseBody = httpResponseBody;
    }

    public HttpResponse(HttpStatusLine httpStatusLine, HttpResponseHeader httpResponseHeader) {
        this(httpStatusLine, httpResponseHeader, null);
    }

    public byte[] getBytes() {
        String statusLine = httpStatusLine.getVersion() + " " + httpStatusLine.getHttpStatusCode().getCode() + " "
                + httpStatusLine.getHttpStatusCode().getMessage();
        Map<String, String> headers = httpResponseHeader.getHeaders();
        StringBuilder sb = new StringBuilder();
        int size = headers.keySet().size();
        int i = 1;
        for (String key : headers.keySet()) {
            if (i < size) {
                sb.append(key).append(": ").append(headers.get(key)).append(" \r\n");
                size++;
            } else {
                sb.append(key).append(": ").append(headers.get(key));
            }
        }
        if (httpResponseBody != null) {
            String responseBody = httpResponseBody.getBody();
            String join = String.join("\r\n",
                    statusLine,
                    sb.toString(),
                    responseBody);
            return join.getBytes();
        }
        String join = String.join("\r\n",
                statusLine,
                sb.toString());
        return join.getBytes();
    }

    public HttpStatusLine getHttpStatusLine() {
        return httpStatusLine;
    }

    public HttpResponseHeader getHttpResponseHeader() {
        return httpResponseHeader;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }
}
