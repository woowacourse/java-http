package org.apache.coyote.http11.handler;

import static org.apache.coyote.header.HttpMethod.GET;
import static org.apache.coyote.header.HttpMethod.POST;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.httpMethod().equals(GET)) {
            doGet(request, response);
            return;
        }
        if (request.httpMethod().equals(POST)) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }
}
