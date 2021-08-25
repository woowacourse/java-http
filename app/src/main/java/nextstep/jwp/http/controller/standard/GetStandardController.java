package nextstep.jwp.http.controller.standard;

import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.response.HttpResponse;

public class GetStandardController extends StandardController {

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
