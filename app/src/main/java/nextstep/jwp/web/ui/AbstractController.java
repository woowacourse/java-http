package nextstep.jwp.web.ui;

import nextstep.jwp.server.http.request.HttpRequest;
import nextstep.jwp.server.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
            return;
        }
        doPost(request, response);
    }

    protected abstract void doGet(HttpRequest request, HttpResponse response);

    protected abstract void doPost(HttpRequest request, HttpResponse response);
}
