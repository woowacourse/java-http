package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod().isGet()) {
            doGet(request, response);
        }

        if (request.getMethod().isPost()) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }

    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }
}

