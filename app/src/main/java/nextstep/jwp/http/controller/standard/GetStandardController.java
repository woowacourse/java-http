package nextstep.jwp.http.controller.standard;

import nextstep.jwp.http.Body;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.request_line.HttpMethod;
import nextstep.jwp.http.request.request_line.HttpPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.response_line.ResponseLine;

public class GetStandardController extends StandardController {

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        HttpPath path = httpRequest.getPath();
        path.toFile();

        ResponseLine responseLine = new ResponseLine(httpRequest.getVersion(), HttpStatus.OK);

        Headers headers1 = new Headers();
        Body body = Body.fromFile(path.toFile());

        return new HttpResponse(
            responseLine,
            headers1,
            body
        );
    }

    @Override
    public boolean isSatisfiedBy(HttpRequest httpRequest) {
        return httpRequest.getHttpMethod().equals(HttpMethod.GET);
    }
}
