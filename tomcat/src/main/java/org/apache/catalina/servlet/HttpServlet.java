package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestInfo.RequestInfo;
import org.apache.coyote.request.requestInfo.RequestMethod;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpResponseGenerator;
import org.apache.coyote.response.HttpStatus;

public abstract class HttpServlet implements Servlet {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        final RequestInfo requestInfo = httpRequest.getRequestInfo();

        if(requestInfo.isSame(RequestMethod.GET)){
            return doGet(httpRequest);
        }
        if(requestInfo.isSame(RequestMethod.POST)){
            return doPost(httpRequest);
        }

        return HttpResponseGenerator.generate("", ContentType.HTML, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
