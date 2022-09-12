package nextstep.jwp.controller;

import org.apache.coyote.http11.enums.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        if (httpRequest.isPostMethod()) {
            return doPost(httpRequest);
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, "text/html", "/404.html");
    }

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);
}
