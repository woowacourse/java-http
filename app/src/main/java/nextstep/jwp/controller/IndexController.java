package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

public class IndexController implements Controller {
    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final String httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();

        return "GET".equals(httpMethod) && "/index.html".equals(path);
    }

    @Override
    public HttpResponse doService(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();
        final String responseBody = readFile(path);

        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.OK,
                "text/html",
                responseBody.getBytes().length,
                responseBody
        );
    }
}
