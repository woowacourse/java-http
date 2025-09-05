package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.responseLine.HttpStatus;

public class DefaultServlet extends HttpServlet{

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return httpRequest.isDefaultRequestPath();
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponseGenerator.generate("Hello world!", ContentType.HTML, HttpStatus.OK);
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
