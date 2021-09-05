package nextstep.jwp.controller;

import nextstep.jwp.exception.NotSupportedMethodException;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) {
        throw new NotSupportedMethodException();
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) {
        throw new NotSupportedMethodException();
    }
}
