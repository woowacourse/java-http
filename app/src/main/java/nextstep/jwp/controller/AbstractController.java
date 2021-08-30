package nextstep.jwp.controller;

import nextstep.jwp.model.httpmessage.request.HttpMethod;
import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

public abstract class AbstractController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isPost()) {
            doPost(request, response, mv);
        }

        if (method.isGet()) {
            doGet(request, response, mv);
        }

//        response.sendError(NOT_IMPLEMENTED);
    }

    protected void doGet(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
    }

    protected void doPost(HttpRequest request, HttpResponse response, ModelAndView mv) throws IOException {
    }
}
