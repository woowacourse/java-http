package nextstep.project.presentation;

import nextstep.jwp.dispatcher.handler.HttpHandler;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.message.ContentType;
import nextstep.jwp.http.message.HttpHeaders;
import nextstep.jwp.http.message.HttpStatus;

public class HelloWorldController extends HttpHandler {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        super.doGet(httpRequest, httpResponse);
        HttpHeaders headers = new HttpHeaders();
        headers.addHeader("Content-Type", ContentType.HTML.getDescription());
        headers.addHeader("Content-Length", "12");

        httpResponse.setHeaders(headers);
        httpResponse.setContent("Hello world!");
        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setVersionOfProtocol("HTTP/1.1");
    }
}
