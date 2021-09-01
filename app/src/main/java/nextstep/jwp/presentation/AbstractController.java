package nextstep.jwp.presentation;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.Method;
import nextstep.jwp.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Method method = request.getMethod();

        if (method == Method.GET) {
            doGet(request, response);
        }
        if (method == Method.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
