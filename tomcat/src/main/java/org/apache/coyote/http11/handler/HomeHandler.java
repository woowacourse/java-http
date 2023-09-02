package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;

public class HomeHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);
        MessageBody messageBody = MessageBody.from("Hello world!");

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), httpRequest.getContentType());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), messageBody.getBodyLength());

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }
}
