package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.ResponseBody.EMPTY_RESPONSE_BODY;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

    private final StatusLine statusLine;
    private final ResponseHeaders responseHeaders;
    private final ResponseBody responseBody;

    private HttpResponse(final StatusLine statusLine, final ResponseHeaders responseHeaders, final ResponseBody responseBody) {
        this.statusLine = statusLine;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse of(final StatusLine statusLine, final ContentType contentType, final String responseBody) {
        final ResponseHeaders responseHeaders = getHeaders(contentType, responseBody);
        final ResponseBody body = new ResponseBody(responseBody);

        return new HttpResponse(statusLine, responseHeaders, body);
    }

    private static ResponseHeaders getHeaders(final ContentType contentType, final String responseBody) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add(CONTENT_TYPE, contentType.getValue());
        responseHeaders.add(CONTENT_LENGTH, Integer.toString(responseBody.getBytes().length));

        return responseHeaders;
    }

    public static HttpResponse ofRedirect(final StatusLine statusLine, final String location) {
        final ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.add(LOCATION, location);
        responseHeaders.add(CONTENT_TYPE, ContentType.HTML.getValue());
        responseHeaders.add(CONTENT_LENGTH, "0");

        return new HttpResponse(statusLine, responseHeaders, EMPTY_RESPONSE_BODY);
    }

    public void setCookie(final String jsessionId) {
        final String value = "JSESSIONID=" + jsessionId;
        responseHeaders.add(SET_COOKIE, value);
    }

    public String toMessage() {
        return String.join(
                System.lineSeparator(),
                statusLine.toMessage(),
                responseHeaders.toMessage(),
                responseBody.toMessage()
        );
    }
}
