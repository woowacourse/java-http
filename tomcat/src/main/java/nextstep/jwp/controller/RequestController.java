package nextstep.jwp.controller;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class RequestController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.isSameRequestMethod(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }

        if (request.isSameRequestMethod(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 HTTP Method 입니다.");
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;
}
