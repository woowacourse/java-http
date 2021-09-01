package nextstep.jwp.app.ui;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        String responseBody = "Hello world!";
        HttpResponse response = new HttpResponse(HttpStatus.OK, responseBody);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", Integer.toString(responseBody.length()));
        return response;
    }
}
