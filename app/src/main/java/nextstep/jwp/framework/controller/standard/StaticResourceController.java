package nextstep.jwp.framework.controller.standard;

import nextstep.jwp.framework.controller.StandardController;
import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class StaticResourceController extends StandardController {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HttpMethod.GET);
    }

    @Override
    protected HttpResponse doGet(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        HttpStatus httpStatus = HttpStatus.assumeFromHttpStatusPage(url);
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(httpStatus)
            .contentType(ContentType.find(httpRequest.getUrl()))
            .responseBody(readFile(url))
            .build();
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
