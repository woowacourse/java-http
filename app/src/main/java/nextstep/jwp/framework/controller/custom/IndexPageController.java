package nextstep.jwp.framework.controller.custom;

import nextstep.jwp.framework.controller.CustomController;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class IndexPageController extends CustomController {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.getUrl().equals("/") && httpRequest.getMethod().equals(HttpMethod.GET);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        String url = "/index.html";
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(HttpStatus.OK)
            .contentType(ContentType.find(url))
            .responseBody(readFile(url))
            .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
