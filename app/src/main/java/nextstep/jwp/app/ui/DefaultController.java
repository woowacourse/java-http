package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.mvc.controller.Controller;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        return response.forward(INDEX_HTML);
    }
}
