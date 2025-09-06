package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public class DefaultServlet extends HttpServlet {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isDefaultRequestPath();
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.init("Hello world!", ContentType.PLAIN, HttpStatus.OK);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.init("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
