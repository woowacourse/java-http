package nextstep.jwp.app.ui;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mvc.controller.Controller;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        return response.sendRedirect(INDEX_HTML);
    }
}
