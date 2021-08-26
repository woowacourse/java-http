package nextstep.jwp.server.handler.controller.standard;

import nextstep.jwp.http.header.element.HttpStatus;
import nextstep.jwp.http.header.request.HttpRequest;
import nextstep.jwp.http.header.request.request_line.HttpMethod;
import nextstep.jwp.http.header.request.request_line.HttpPath;
import nextstep.jwp.http.header.response.HttpResponse;

public class GetStandardController implements StandardController {

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        HttpPath path = httpRequest.getPath();
        return HttpResponse.status(HttpStatus.OK, path.getUri());
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.GET);
    }
}
