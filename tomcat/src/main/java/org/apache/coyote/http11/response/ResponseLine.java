package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpVersion;

import static org.apache.coyote.http11.request.HttpVersion.HTTP_1_1;
import static org.apache.coyote.http11.response.HttpResponse.RESPONSE_DELIMITER;
import static org.apache.coyote.http11.response.ResponseStatus.FOUND;
import static org.apache.coyote.http11.response.ResponseStatus.OK;

public class ResponseLine {

    private ResponseStatus responseStatus;
    private HttpVersion httpVersion;

    private ResponseLine(ResponseStatus responseStatus, HttpVersion httpVersion) {
        this.responseStatus = responseStatus;
        this.httpVersion = httpVersion;
    }

    protected static ResponseLine init() {
        return new ResponseLine(OK, HTTP_1_1);
    }

    public void redirect(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        this.responseStatus = FOUND;
    }

    public String generateMessage() {
        return String.join(
                RESPONSE_DELIMITER,
                httpVersion.getVersion(),
                String.valueOf(responseStatus.getStatusCode()),
                responseStatus.name(),
                ""
        );
    }
}
