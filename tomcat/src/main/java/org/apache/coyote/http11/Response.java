package org.apache.coyote.http11;

import org.apache.tomcat.http.common.body.Body;
import org.apache.tomcat.http.response.ResponseHeader;
import org.apache.tomcat.http.response.StatusLine;

public class Response {

    private static final String LINE_DELIMITER = "\r\n";

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final Body body;

    public Response(final StatusLine statusLine, final ResponseHeader responseHeader, final Body body) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public String getResponseText() {
        return statusLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize();
    }

    public byte[] getResponseBytes() {
        return (statusLine.getResponseText() + LINE_DELIMITER + responseHeader.getResponseText() + LINE_DELIMITER
                + LINE_DELIMITER
                + body.deserialize()).getBytes();
    }
}
