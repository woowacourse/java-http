package nextstep.jwp.controller;

import org.apache.coyote.http11.exception.ParameterNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.Controller;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        final RequestMethod method = request.getMethod();

        try {
            if (method.equals(RequestMethod.GET)) {
                return doGet(request);
            }
            if (method.equals(RequestMethod.POST)) {
                return doPost(request);
            }
            return methodNotAllowed();

        } catch (final ParameterNotFoundException e) {
            return fail(HttpStatus.BAD_REQUEST, Page.BAD_REQUEST);
        }
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return methodNotAllowed();
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return methodNotAllowed();
    }

    private HttpResponse methodNotAllowed() {
        return fail(HttpStatus.METHOD_NOT_ALLOWED, Page.METHOD_NOT_ALLOWED);
    }

    protected final HttpResponse success(final HttpStatus status, final Page page) {
        return HttpResponse.status(status)
                .body(page.getResource());
    }

    protected final HttpResponse redirect(final HttpStatus status, final String uri) {
        return HttpResponse.status(status)
                .location(uri);
    }

    protected final HttpResponse redirectToIndex() {
        return redirect(HttpStatus.FOUND, Page.INDEX.getPath());
    }

    protected final HttpResponse fail(final HttpStatus status, final Page page) {
        return HttpResponse.status(status)
                .body(page.getResource());
    }
}