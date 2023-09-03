package org.apache.coyote.http11.handler;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.apache.coyote.http11.common.MessageBody;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.Status;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class ResourceHandler implements RequestHandler {
    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        String requestUri = httpRequest.getRequestUri();
        StatusLine statusLine = new StatusLine(httpRequest.getHttpVersion(), Status.OK);

        String responseBody = FileReader.read(requestUri);
        MessageBody messageBody = MessageBody.from(responseBody);

        ResponseHeaders responseHeaders = new ResponseHeaders();
        responseHeaders.addHeader(HttpHeaderName.CONTENT_TYPE.getValue(), httpRequest.getContentType());
        responseHeaders.addHeader(HttpHeaderName.CONTENT_LENGTH.getValue(), String.valueOf(messageBody.getBodyLength()));

        return new HttpResponse(statusLine, responseHeaders, messageBody);
    }
}
