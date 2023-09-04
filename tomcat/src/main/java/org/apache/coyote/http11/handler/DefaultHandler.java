package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.OK;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeaderType.CONTENT_TYPE;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatusLine;

public class DefaultHandler implements RequestHandler {

    public HttpResponse handle(final HttpRequest httpRequest) {
        var responseBody = "Hello world!";

        HttpResponseStatusLine statusLine = new HttpResponseStatusLine(
                httpRequest.getStartLine().getHttpVersion(), OK);

        HttpResponseHeader httpResponseHeader = new HttpResponseHeader();
        httpResponseHeader.add(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponseHeader.add(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));

        HttpResponseBody body = HttpResponseBody.from(responseBody);

        return new HttpResponse(statusLine, httpResponseHeader, body);
    }
}
