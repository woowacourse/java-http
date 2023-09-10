package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class ResourceController extends AbstractController {

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        StatusLine statusLine = new StatusLine(request.getHttpVersion(), Status.OK);

        String responseBody = FileReader.read(request.getRequestUri());
        MessageBody messageBody = MessageBody.from(responseBody);

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), request.getContentType());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), String.valueOf(messageBody.getBodyLength()));

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        throw new HttpRequestException.MethodNotAllowed();
    }
}
