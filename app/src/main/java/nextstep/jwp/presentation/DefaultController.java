package nextstep.jwp.presentation;

import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.ContentType;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;

public class DefaultController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setStatusLine(StatusCode.OK);
        response.addHeader("Content-Type", ContentType.HTML.getValue());
        response.addHeader("Content-Length", String.valueOf("Hello world!".getBytes().length));
        response.addBody("Hello world!");
    }
}
