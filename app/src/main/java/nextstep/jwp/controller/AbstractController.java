package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(Request request, Response response) throws IOException {
        if (request.isEqualsHttpMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isEqualsHttpMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doGet(Request request, Response response) throws IOException;

    protected abstract void doPost(Request request, Response response) throws IOException;
}
