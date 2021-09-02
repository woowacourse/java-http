package nextstep.jwp.framework.controller.custom;

import nextstep.jwp.framework.controller.CustomController;
import nextstep.jwp.framework.controller.ResponseTemplate;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;

public class IndexPageController extends CustomController {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.getUrl().equals("/") && httpRequest.getMethod().equals(HttpMethod.GET);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        return ResponseTemplate.ok("/index.html").build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
