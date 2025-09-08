package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestMethod;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.responseLine.HttpStatus;

public abstract class HttpServlet implements Servlet {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final RequestLine requestLine = httpRequest.getRequestLine();

        if (requestLine.isSame(RequestMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (requestLine.isSame(RequestMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }

        httpResponse.init("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
