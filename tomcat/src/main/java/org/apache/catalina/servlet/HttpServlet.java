package org.apache.catalina.servlet;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.requestInfo.RequestInfo;
import org.apache.coyote.request.requestInfo.RequestMethod;
import org.apache.coyote.response.HttpResponse;

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

        throw new IllegalArgumentException("[ERROR] 지원하지 않는 메서드입니다");
    }
}
