package nextstep.jwp.controller;

import nextstep.jwp.controller.exception.NotFoundControllerException;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

abstract class AbstractController implements Controller {
    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }

        return doPost(httpRequest);
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) {
        throw new NotFoundControllerException("해당하는 메서드의 컨트롤러를 찾을 수 없습니다. " + httpRequest.getUrl());
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        throw new NotFoundControllerException("해당하는 메서드의 컨트롤러를 찾을 수 없습니다. " + httpRequest.getUrl());
    }
}
