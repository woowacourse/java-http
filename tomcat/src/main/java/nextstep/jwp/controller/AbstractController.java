package nextstep.jwp.controller;

import static org.apache.coyote.http11.message.common.HttpHeader.LOCATION;
import static org.apache.coyote.http11.message.common.HttpMethod.DELETE;
import static org.apache.coyote.http11.message.common.HttpMethod.GET;
import static org.apache.coyote.http11.message.common.HttpMethod.PATCH;
import static org.apache.coyote.http11.message.common.HttpMethod.POST;
import static org.apache.coyote.http11.message.common.HttpMethod.PUT;
import static org.apache.coyote.http11.message.response.HttpStatus.FOUND;

import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        if (request.matchesMethod(GET)) {
            return handleGet(request);
        }

        if (request.matchesMethod(POST)) {
            return handlePost(request);
        }

        if (request.matchesMethod(PATCH)) {
            return handlePatch(request);
        }

        if (request.matchesMethod(PUT)) {
            return handlePut(request);
        }

        if (request.matchesMethod(DELETE)) {
            return handleDelete(request);
        }

        return notFound();
    }

    protected HttpResponse handleGet(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse handlePost(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse handlePatch(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse handlePut(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse handleDelete(HttpRequest request) {
        return notFound();
    }

    protected HttpResponse notFound() {
        return new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, "/404.html")
                .build();
    }

    protected HttpResponse redirectTo(final String path) {
        return new HttpResponse.Builder()
                .status(FOUND)
                .header(LOCATION, path)
                .build();
    }
}
