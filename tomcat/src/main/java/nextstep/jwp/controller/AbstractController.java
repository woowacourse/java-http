package nextstep.jwp.controller;

import nextstep.jwp.controller.exception.NotFoundControllerException;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

abstract class AbstractController implements Controller {
    @Override
    public void service(final HttpRequest httpRequest,
                        final HttpResponse httpResponse) {
        if (httpRequest.isGetMethod()) {
            doGet(httpRequest, httpResponse);
            return;
        }

        doPost(httpRequest, httpResponse);
    }

    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new NotFoundControllerException("해당하는 메서드의 컨트롤러를 찾을 수 없습니다. " + httpRequest.getUrl());
    }

    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        throw new NotFoundControllerException("해당하는 메서드의 컨트롤러를 찾을 수 없습니다. " + httpRequest.getUrl());
    }
}
