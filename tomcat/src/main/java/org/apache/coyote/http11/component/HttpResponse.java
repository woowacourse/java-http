package org.apache.coyote.http11.component;

public class HttpResponse implements HttpInteract {

    private final ResponseLine responseLine;
    private final ResponseHeader responseHeader;
    // TODO: 범용적인 Body로 바꿀 것
    private final String htmlBody;

    public HttpResponse(final ResponseLine responseLine, final ResponseHeader responseHeader, final String htmlBody) {
        this.responseLine = responseLine;
        this.responseHeader = responseHeader;
        this.htmlBody = htmlBody;
    }

    public String getResponseText() {
        return responseLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + htmlBody;
    }
}
