package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.Body.EMPTY_BODY;

public class Response {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final StartLine startLine;
    private final Headers headers;
    private final Body body;

    private Response(final StartLine startLine, final Headers headers, final Body body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Response of(final StartLine startLine, final ContentType contentType, final String responseBody) {
        final Headers headers = getHeaders(contentType, responseBody);
        final Body body = new Body(responseBody);

        return new Response(startLine, headers, body);
    }

    private static Headers getHeaders(final ContentType contentType, final String responseBody) {
        final Headers headers = new Headers();
        headers.add(CONTENT_TYPE, contentType.getValue());
        headers.add(CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));

        return headers;
    }

    public static Response ofRedirect(final StartLine startLine, final String location) {
        final Headers headers = new Headers();
        headers.add(LOCATION, location);
        headers.add(CONTENT_TYPE, ContentType.HTML.getValue());
        headers.add(CONTENT_LENGTH, "0");

        return new Response(startLine, headers, EMPTY_BODY);
    }

    public void setCookie(final String jsessionId) {
        final String value = "JSESSIONID=" + jsessionId;
        headers.add(SET_COOKIE, value);
    }

    public String toMessage() {
        return String.join(
                System.lineSeparator(),
                startLine.toMessage(),
                headers.toMessage(),
                body.toMessage()
        );
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
