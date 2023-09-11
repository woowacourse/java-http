package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpHeaders;

public class HttpResponse {

    private final String httpVersion;
    private HttpStatus httpStatus;
    private final HttpHeaders headers = HttpHeaders.empty();
    private String body;
    private String responseFileName;

    public HttpResponse(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getBody() {
        return body;
    }

    public String getResponseFileName() {
        return responseFileName;
    }

    public HttpResponse addHeader(String key, String value) {
        headers.add(key, value);
        return this;
    }

    public HttpResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse setResponseFileName(String responseFileName) {
        this.responseFileName = responseFileName;
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public String format() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLineFormat());
        responseBuilder.append(headers.format()).append("\r\n");
        responseBuilder.append(body);
        return responseBuilder.toString();
    }

    private String statusLineFormat() {
        return String.join(
                " ",
                httpVersion,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                "\r\n"
        );
    }
}
