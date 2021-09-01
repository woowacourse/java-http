package nextstep.jwp.controller;

import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;

public class NotFoundController extends AbstractController {

    @Override
    public JwpHttpResponse doGet(JwpHttpRequest request) {
        return JwpHttpResponse.notFound();
    }

    @Override
    public JwpHttpResponse doPost(JwpHttpRequest request) {
        return JwpHttpResponse.notFound();
    }
}
