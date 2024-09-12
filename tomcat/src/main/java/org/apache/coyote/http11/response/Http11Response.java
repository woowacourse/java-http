package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpMimeType;

public class Http11Response {

    private static final String protocol = "HTTP/1.1";

    private final String responseBody;
    private final Http11ResponseHeaders headers;
    private final HttpStatusCode statusCode;
    private String firstLine = "";

    public Http11Response(HttpStatusCode httpStatusCode, String responseBody, Http11ResponseHeaders headers) {
        this.statusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.headers = headers;
    }

    public Http11Response(HttpStatusCode httpStatusCode, String responseBody, String fileExtensions) {
        this(httpStatusCode, responseBody,
                Http11ResponseHeaders.builder()
                        .addHeader("Content-type", HttpMimeType.from(fileExtensions).asString())
                        .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                        .build());
    }

    public byte[] getBytes() {
        setFirstLine();
        return String.join(" \r\n",
                firstLine,
                headers.asString(),
                responseBody).getBytes();
    }

    private void setFirstLine() {
        firstLine = String.join(" ",
                protocol,
                String.valueOf(statusCode.getValue()),
                statusCode.getName());
    }

    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }
}
