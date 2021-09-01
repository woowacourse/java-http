package nextstep.jwp.controller;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;

public abstract class AbstractController implements Controller{

    @Override
    public void service(Request request, Response response) {
        if (request.isEqualsHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isEqualsHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(Request request, Response response);

    protected abstract void doPost(Request request, Response response);
}
