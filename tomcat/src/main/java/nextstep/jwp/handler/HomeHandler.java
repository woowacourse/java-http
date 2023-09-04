package nextstep.jwp.handler;

import java.util.List;
import nextstep.jwp.http.HttpBody;
import nextstep.jwp.http.HttpHeaders;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;

public class HomeHandler implements RequestHandler {

    private static final RequestHandler HANDLER = new HomeHandler();

    private HomeHandler(){}

    public static RequestHandler getInstance() {
        return HANDLER;
    }
    @Override
    public HttpResponse handle(HttpRequest request) {
        HttpStatus httpStatus = HttpStatus.OK;
        HttpVersion httpVersion = request.getHttpVersion();
        HttpBody httpBody = HttpBody.from("Hello world!");
        HttpHeaders httpHeaders = createDefaultHeaders(httpBody);

        return new HttpResponse(httpVersion, httpStatus, httpHeaders, httpBody);
    }

    private HttpHeaders createDefaultHeaders(HttpBody httpBody) {
        List<String> headers = List.of(
                "Content-Type: text/html;charset=utf-8",
                "ContentLength: " + httpBody.getBytesLength()
        );

        return HttpHeaders.from(headers);
    }
}
