package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;

public class HttpResponse {

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final StatusLine statusLine, final Headers headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse ok(final StaticResource staticResource) {
        return new HttpResponse(
                new StatusLine(HttpStatus.OK),
                Headers.withStaticResource(staticResource),
                staticResource.getContent()
        );
    }

    public static HttpResponse found(final String location) {
        return new HttpResponse(
                new StatusLine(HttpStatus.FOUND),
                Headers.withLocation(location),
                ""
        );
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                headers.toString(),
                "",
                body);
    }
}
