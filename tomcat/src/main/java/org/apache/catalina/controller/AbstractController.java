package org.apache.catalina.controller;

import static org.apache.coyote.http11.response.ResponseHeaderType.ALLOW;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public abstract class AbstractController implements Controller {

    static final String RESOURCE_DIRECTORY = "static";
    public static final String ALLOW_HEADER_DELIMITER = ", ";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestMethod requestMethod = request.getMethod();

        if (requestMethod == HttpRequestMethod.POST) {
            doPost(request, response);
            return;
        }

        if (requestMethod == HttpRequestMethod.GET) {
            doGet(request, response);
            return;
        }
        getMethodNotAllowedResponse(response);
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        getMethodNotAllowedResponse(response);
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        getMethodNotAllowedResponse(response);
    }

    private void getMethodNotAllowedResponse(final HttpResponse response) {
        final String allowedHeaders = String.join(ALLOW_HEADER_DELIMITER,
                HttpRequestMethod.POST.name(),
                HttpRequestMethod.GET.name());

        response.setStatusCode(HttpStatusCode.METHOD_NOT_ALLOWED)
                .addHeader(ALLOW, allowedHeaders);
    }
}
