package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeaders;
import org.apache.coyote.http11.response.HttpResponseStatusLine;

public class DefaultHandler extends RequestHandler {

    public static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String responseBody = DEFAULT_MESSAGE;
        final HttpResponseStatusLine statusLine = new HttpResponseStatusLine(httpRequest.getHttpVersion(), OK);

        final HttpResponseHeaders httpResponseHeaders = new HttpResponseHeaders();
        httpResponseHeaders.add(CONTENT_TYPE, CONTENT_TYPE_HTML);
        httpResponseHeaders.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        final HttpResponseBody body = new HttpResponseBody(responseBody);

        return new HttpResponse(statusLine, httpResponseHeaders, body);
    }
}
