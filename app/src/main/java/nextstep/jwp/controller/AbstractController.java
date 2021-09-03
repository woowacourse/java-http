package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseReference;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {

        HttpResponse httpResponse = ResponseReference.create404Response();
        if (request.isPost()) {
            httpResponse = doPost(request);
        }
        if (request.isGet()) {
            httpResponse = doGet(request);
        }

        return httpResponse;
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return ResponseReference.create404Response();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return ResponseReference.create404Response();
    }
}

