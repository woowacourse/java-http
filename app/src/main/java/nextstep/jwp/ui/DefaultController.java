package nextstep.jwp.ui;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = "Hello world!";
        HttpResponse response = new HttpResponse();
        response.setStatus(200);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
        response.write(responseBody);
        return response;
    }
}
