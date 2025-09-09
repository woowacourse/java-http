package org.apache.coyote.http11.parser;

public class RequestResult {

    private static final String DEAFULT_HTTP_RESOPNSE_STATUS = "HTTP/1.1 200 OK ";

    private final byte[] parsedContent;
    private final String additionalResponse;
    private final String httpResponseStatus;

    public RequestResult(byte[] parsedContent, String additionalResponse) {
        this.parsedContent = parsedContent;
        this.additionalResponse = additionalResponse;
        this.httpResponseStatus = DEAFULT_HTTP_RESOPNSE_STATUS;
    }

    public RequestResult(byte[] parsedContent, String additionalResponse, String httpResponseStatus) {
        this.parsedContent = parsedContent;
        this.additionalResponse = additionalResponse;
        this.httpResponseStatus = httpResponseStatus;
    }

    public byte[] getParseContent() {
        return parsedContent;
    }

    public String getAdditionalResponse() {
        return additionalResponse;
    }

    public String getHttpResponseStatus() {
        return httpResponseStatus;
    }
}
