package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.StaticResource;

public class HttpResponse {

    private final ResponseStatusLine responseStatusLine;
    private final ResponseHeaders responseHeaders;
    private final String body;

    public HttpResponse(final ResponseStatusLine responseStatusLine, final ResponseHeaders responseHeaders, final String body) {
        this.responseStatusLine = responseStatusLine;
        this.responseHeaders = responseHeaders;
        this.body = body;
    }

    public static HttpResponse ok(final StaticResource staticResource) {
        return new HttpResponse(
                new ResponseStatusLine(HttpStatus.OK),
                ResponseHeaders.withStaticResource(staticResource),
                staticResource.getContent()
        );
    }

    public static HttpResponse found(final String location) {
        return new HttpResponse(
                new ResponseStatusLine(HttpStatus.FOUND),
                ResponseHeaders.withLocation(location),
                ""
        );
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                responseStatusLine.toString(),
                responseHeaders.toString(),
                "",
                body);
    }
}
