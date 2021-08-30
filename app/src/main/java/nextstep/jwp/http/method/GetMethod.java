package nextstep.jwp.http.method;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class GetMethod extends Method {
    private static final String TEXT_CSS = "text/css";
    private static final String TEXT_HTML = "text/html";

    private final HttpRequest httpRequest;

    public GetMethod(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public HttpResponse matchFunction() {
        String urlWithParams = httpRequest.getUrl();
        if (ContentType.matches(urlWithParams)) {
            return getContentType(urlWithParams);
        }
        if (urlWithParams.contains("?") && urlWithParams.contains("=")) {
            return getParam(urlWithParams);
        }
        return getPage(urlWithParams);
    }

    private HttpResponse getContentType(final String request) {
        return getHttpResponse(TEXT_CSS, pageController.mapContent(request));
    }

    private HttpResponse getParam(final String request) {
        return getHttpResponse(TEXT_HTML, jwpController.mapResponse(request));
    }

    private HttpResponse getPage(final String request) {
        return getHttpResponse(TEXT_HTML, pageController.mapResponse(HttpStatus.OK, request));
    }

    private HttpResponse getHttpResponse(final String contentType, final Map<HttpStatus, String> mappedResponse) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(mappedResponse.entrySet()).get(0);
            return new HttpResponse(responseEntry.getKey(), contentType, responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, TEXT_HTML, e.getMessage());
        }
    }
}
