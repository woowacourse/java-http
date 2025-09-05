package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.requestLine.RequestMethod;
import org.apache.coyote.response.responseHeader.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.responseLine.HttpStatus;

public abstract class HttpServlet implements Servlet {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final RequestLine requestLine = httpRequest.getRequestLine();

        if(requestLine.isSame(RequestMethod.GET)){
            return doGet(httpRequest);
        }
        if(requestLine.isSame(RequestMethod.POST)){
            return doPost(httpRequest);
        }

        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
