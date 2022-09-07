package nextstep.jwp.presentation;

import static org.apache.coyote.http11.support.HttpMethod.GET;
import static org.apache.coyote.http11.support.HttpMethod.POST;

import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;

public abstract class AbstractController implements Controller {

    private static final NotFoundException METHOD_NOT_FOUND_EXCEPTION =
            new NotFoundException("지원되지 않는 method 입니다");

    @Override
    public HttpResponse service(final HttpRequest httpRequest) throws NotFoundException, IOException {
        if (httpRequest.isMethod(GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.isMethod(POST)) {
            return doPost(httpRequest);
        }
        throw METHOD_NOT_FOUND_EXCEPTION;
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) throws IOException {
        throw METHOD_NOT_FOUND_EXCEPTION;
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw METHOD_NOT_FOUND_EXCEPTION;
    }
}
