package nextstep.jwp.controller;

import nextstep.jwp.exception.http.request.InvalidHttpRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;

public abstract class AbstractController implements Controller {

    @Override
    public View handle(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            return doGet(request, response);
        }
        if (request.isPost()) {
            return doPost(request, response);
        }
        throw new InvalidHttpRequestException();
    }

    protected abstract View doGet(HttpRequest request, HttpResponse response);

    protected abstract View doPost(HttpRequest request, HttpResponse response);
}
