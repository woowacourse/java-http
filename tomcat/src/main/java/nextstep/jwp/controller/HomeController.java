package nextstep.jwp.controller;

import nextstep.jwp.controller.support.ErrorResponse;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.ContentType;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.ResponseStatusCode;

public class HomeController extends AbstractController {

    private static final String RESPONSE_BODY = "Hello world!";

    private final HttpRequest httpRequest;

    public HomeController(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.matchRequestMethod(Method.GET)) {
            return HttpResponse.of(ResponseStatusCode.OK, httpRequest.getVersion(), ContentType.HTML, RESPONSE_BODY);
        }
        return ErrorResponse.getNotFound(getClass(), httpRequest);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return null;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new IllegalArgumentException("올바르지 않은 Method 요청입니다.");
    }
}
