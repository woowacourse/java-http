package nextstep.jwp.controller;

import nextstep.jwp.exception.http.request.InvalidHttpRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.view.View;

public abstract class AbstractController implements Controller {

    protected static final String HOME_PATH = "/index";
    protected static final String LOGIN_PATH = "/login";
    protected static final String BAD_REQUEST_PATH = "/400";
    protected static final String NOT_FOUND_PATH = "/404";

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
