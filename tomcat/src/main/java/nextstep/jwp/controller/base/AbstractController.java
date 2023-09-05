package nextstep.jwp.controller.base;

import nextstep.jwp.exception.UnsupportedMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) throws Exception {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        if (httpRequest.isPostMethod()) {
            return doPost(httpRequest);
        }

        throw new UnsupportedMethodException();
    }

    protected HttpResponse doGet(HttpRequest httpRequest) throws Exception {
        throw new UnsupportedMethodException();
    }

    protected HttpResponse doPost(HttpRequest httpRequest) throws Exception {
        throw new UnsupportedMethodException();
    }
}
