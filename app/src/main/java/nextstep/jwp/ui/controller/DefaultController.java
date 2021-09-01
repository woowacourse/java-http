package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import nextstep.jwp.ui.response.HttpStatus;

public class DefaultController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        String responseBody = "Hello world!";
        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
        response.write(responseBody);
    }
}
