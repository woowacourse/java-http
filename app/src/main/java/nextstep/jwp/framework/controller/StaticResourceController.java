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
    protected HttpResponse doGet(HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        HttpStatus httpStatus = adjustStatus(url);
        return new HttpResponse.Builder()
            .protocol(httpRequest.getProtocol())
            .httpStatus(httpStatus)
            .contentType(ContentType.find(httpRequest.getUrl()))
            .responseBody(readFile(url))
            .build();
    }

    private HttpStatus adjustStatus(String url) {
        if (url.contains("401.html")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (url.contains("404.html")) {
            return HttpStatus.NOT_FOUND;
        }
        if (url.contains("500.html")) {
            return HttpStatus.INTERNAL_SEVER_ERROR;
        }
        return HttpStatus.OK;
    }

    @Override
    protected HttpResponse doPost(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }
}
