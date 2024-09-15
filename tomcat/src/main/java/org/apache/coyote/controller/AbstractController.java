package org.apache.coyote.controller;

import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        HttpMethod httpMethod = request.getHttpMethod();

        validateMethod(httpMethod);

        if (httpMethod.isPost()) {
            doPost(request, response);
        }

        if (httpMethod.isGet()) {
            doGet(request, response);
        }
    }

    private static void validateMethod(HttpMethod httpMethod) {
        if (!httpMethod.isValidMethod(httpMethod)) {
            throw new UnsupportedOperationException("지원하지 않는 HTTP METHOD 요청: " + httpMethod);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void redirect(String location, HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(location);
        response.setMimeType(MimeType.from(location));
    }
}
