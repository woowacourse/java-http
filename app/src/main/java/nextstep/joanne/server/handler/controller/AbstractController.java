package nextstep.joanne.server.handler.controller;

import nextstep.joanne.server.http.HttpMethod;
import nextstep.joanne.server.http.HttpSession;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.response.HttpResponse;

import java.util.Objects;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.isSameMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isSameMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected boolean alreadyLogin(HttpResponse response, HttpSession session) {
        if (Objects.nonNull(session) && Objects.nonNull(session.getAttribute("user"))) {
            response.redirect("/index.html");
            return true;
        }
        return false;
    }
}

