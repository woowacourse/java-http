package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String value = request.getCookie().get("JSESSIONID");
        if (Objects.isNull(value)) {
            response.addCookie("JSESSIONID", UUID.randomUUID().toString());
        } else {
            response.addCookie("JSESSIONID", value);
        }

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
