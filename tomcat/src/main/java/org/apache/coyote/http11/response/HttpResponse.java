package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;

public class HttpResponse {

    private final ResponseStatusLine responseStatusLine;
    private final ResponseHeader responseHeader;
    private String body;

    public HttpResponse() {
        this(
                new ResponseStatusLine("HTTP/1.1", null),
                new ResponseHeader(),
                ""
        );
    }

    public HttpResponse(final ResponseStatusLine responseStatusLine, final ResponseHeader responseHeader, final String body) {
        this.responseStatusLine = responseStatusLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public void ok(final StaticResource staticResource) {
        setStatus(HttpStatus.OK);
        responseHeader.setContentInfo(staticResource);
        setBody(staticResource.getContent());
    }

    public void found(final String location) {
        setStatus(HttpStatus.FOUND);
        responseHeader.setLocation(location);
    }

    public void setStatus(final HttpStatus status) {
        responseStatusLine.setStatus(status);
    }

    public void setSessionId(final String sessionId) {
        responseHeader.add("Set-Cookie", "JSESSIONID=" + sessionId);
    }

    public void setBody(final String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                responseStatusLine.toString(),
                responseHeader.toString(),
                "",
                body);
    }
}
