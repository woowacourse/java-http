package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(StatusLine statusLine, Headers headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse withStaticResource(final StaticResource staticResource) {
        return new HttpResponse(
                new StatusLine(HttpStatus.OK),
                Headers.withStaticResource(staticResource),
                staticResource.getContent()
        );
    }

    @Override
    public String toString() {
        return String.join(NEW_LINE,
                statusLine.toString(),
                headers.toString(),
                "",
                body);
    }
}
