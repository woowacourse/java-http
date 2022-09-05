package nextstep.jwp.controller;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.exception.UnsupportedHttpMethodException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        httpResponse.found("/404.html");
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UnsupportedHttpMethodException();
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new UnsupportedHttpMethodException();
    }
}
