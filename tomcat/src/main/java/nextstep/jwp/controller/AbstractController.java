package nextstep.jwp.controller;

import nextstep.jwp.handle.ViewResolver;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            doGet(request, response);
            return;
        }
        if (request.getHttpMethod() == HttpMethod.POST) {
            doPost(request, response);
            return;
        }
        ViewResolver.renderPage(response, HttpStatus.NOT_FOUND, "404.html");
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;
}
