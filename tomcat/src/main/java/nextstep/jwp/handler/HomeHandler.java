package nextstep.jwp.handler;

import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;

public class HomeHandler implements RequestHandler {

    private static final RequestHandler HANDLER = new HomeHandler();

    private HomeHandler() {
    }

    public static RequestHandler getInstance() {
        return HANDLER;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("Hello world!");
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

}
