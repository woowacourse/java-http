package nextstep.jwp.handler;

import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatusLine;

public class HomeHandler implements RequestHandler {

    public static final String DEFAULT_MESSAGE = "Hello world!";

    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpStatusLine httpStatusLine = new HttpStatusLine(request.getHttpVersion(), HttpStatus.OK);
        HttpBody httpBody = HttpBody.from(DEFAULT_MESSAGE);
        HttpHeaders httpHeaders = HttpHeaders.createDefaultHeaders(request.getNativePath(), httpBody);

        return new HttpResponse(httpStatusLine, httpHeaders, httpBody);
    }

}
