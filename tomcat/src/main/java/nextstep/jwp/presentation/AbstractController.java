package nextstep.jwp.presentation;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGet()) {
            return doGet(httpRequest);
        }

        if (httpRequest.isPost()) {
            return doPost(httpRequest);
        }

        return HttpResponse.internalServerError();
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) {
        throw new UncheckedServletException();
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new UncheckedServletException();
    }
}
