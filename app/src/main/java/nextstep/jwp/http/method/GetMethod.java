package nextstep.jwp.http.method;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

public class GetMethod extends Method {
    private final HttpRequest httpRequest;

    public GetMethod(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse matchFunction() {
        String urlWithParams = httpRequest.getUrl();
        ContentType contentType = ContentType.matches(urlWithParams);
        if (urlWithParams.contains("?") && urlWithParams.contains("=")) {
            return getParam(urlWithParams, contentType);
        }
        return getPage(urlWithParams, contentType);
    }

    private HttpResponse getParam(final String request, final ContentType contentType) {
        return getHttpResponse(contentType, jwpController.mapResponse(request));
    }

    private HttpResponse getPage(final String request, final ContentType contentType) {
        if(!contentType.equals(ContentType.NOTHING)) {
            return getHttpResponse(contentType, pageController.mapContent(request));
        }
        return getHttpResponse(contentType, pageController.mapResponse(HttpStatus.OK, request));
    }
}
