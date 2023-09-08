package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;

public class HttpResponse {

    private static final String DELIMITER = " " + System.lineSeparator();
    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    private ResponseBody responseBody;

    public HttpResponse(String httpVersion) {
        this.responseLine = new ResponseLine(httpVersion, null);
        this.responseHeader = new ResponseHeader(new LinkedHashMap<>());
    }

    public int measureContentLength() {
        return responseBody.measureContentLength();
    }

    public String responseMessage() {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(responseLine.responseLineMessage()).append(DELIMITER);

        for (String headerMessage : responseHeader.headerMessages()) {
            messageBuilder.append(headerMessage).append(DELIMITER);
        }

        if (responseBody != null) {
            messageBuilder.append(System.lineSeparator()).append(responseBody.bodyMessage());
        }

        return messageBuilder.toString();
    }

    public void setResponseHeader(HttpHeader field, String value) {
        responseHeader.put(field, value);
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = new ResponseBody(responseBody);
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        responseLine.setResponseStatus(responseStatus);
    }
}
