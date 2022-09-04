package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;

public class HttpResponse {

    private final ResponseStatusLine responseStatusLine;
    private final ResponseHeader responseHeader;
    private final String body;

    public HttpResponse(final ResponseStatusLine responseStatusLine, final ResponseHeader responseHeader, final String body) {
        this.responseStatusLine = responseStatusLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public static HttpResponse ok(final StaticResource staticResource) {
        return new HttpResponse(
                new ResponseStatusLine(HttpStatus.OK),
                ResponseHeader.withStaticResource(staticResource),
                staticResource.getContent()
        );
    }

    public static HttpResponse found(final String location) {
        return new HttpResponse(
                new ResponseStatusLine(HttpStatus.FOUND),
                ResponseHeader.withLocation(location),
                ""
        );
    }

    public void setSessionId(final String sessionId) {
        responseHeader.add("Set-Cookie", "JSESSIONID=" + sessionId);
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
