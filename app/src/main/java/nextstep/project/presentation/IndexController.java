package nextstep.project.presentation;

import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.HttpStatus;

public class IndexController extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);
        renderPage(
            "./static/index.html",
            HttpStatus.OK,
            httpResponse
            );
    }
}
