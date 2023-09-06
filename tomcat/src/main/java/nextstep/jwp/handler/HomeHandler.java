package nextstep.jwp.handler;

import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;

public class HomeHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("Hello world!");
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

}
