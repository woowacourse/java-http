package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final StatusLine statusLine;
    private final Headers headers;
    private final String body;

    public HttpResponse(final StatusLine statusLine, final Headers headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse withStaticResource(final StaticResource staticResource) {
        return new HttpResponse(
                new StatusLine(HttpStatus.OK), // TODO: 2022/09/01 파라미터로 받으면 좋을 것 같다.
                Headers.withStaticResource(staticResource),
                staticResource.getContent()
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
