package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class DefaultController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws IOException {
        String responseBody = "Hello world!";
        HttpResponse response = new HttpResponse();
        response.setHttpStatus(HttpStatus.OK);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", "12");
        response.setBody(responseBody);
        return response;
    }
}
