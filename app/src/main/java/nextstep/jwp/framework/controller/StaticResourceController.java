package nextstep.jwp.framework.controller;

import nextstep.jwp.framework.infrastructure.http.content.ContentType;
import nextstep.jwp.framework.infrastructure.http.method.HttpMethod;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.response.HttpResponse;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;

public class StaticResourceController extends AbstractController {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        return httpRequest.getMethod().equals(HttpMethod.GET) &&
            (url.endsWith(".html") || url.endsWith(".css") || url.endsWith(".js"));
    }

    @Override
    public HttpResponse doService(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(HttpStatus.OK)
            .contentType(ContentType.find(httpRequest.getUrl()))
            .responseBody(readFile(url))
            .build();
    }
}
