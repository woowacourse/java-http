package org.apache.catalina.controller;

import org.apache.catalina.handler.ViewResolver;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    protected final ViewResolver viewResolver = new ViewResolver(); // TODO: HttpResponse 내부로 옮기기

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpMethod httpMethod = request.getHttpMethod();
        if (HttpMethod.POST.equals(httpMethod)) {
            doPost(request, response);
            return;
        }
        if (HttpMethod.GET.equals(httpMethod)) {
            doGet(request, response);
            return;
        }
        response.setStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    abstract protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    abstract protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception;
}
