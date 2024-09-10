package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.startLine.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isMethod(HttpMethod.GET)) {
            doGet(request, response);
        }

        if (request.isMethod(HttpMethod.POST)) {
            doPost(request, response);
        }

        throw new RuntimeException("지원하지 않는 HTTP 메소드: " + request.getHttpMethod());
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }
}
