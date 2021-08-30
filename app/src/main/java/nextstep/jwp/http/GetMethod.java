package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class GetMethod extends Method {
    private static final String TEXT_HTML = "text/html";

    private final HttpRequest httpRequest;

    public GetMethod(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public String matchFunction() {
        String urlWithParams = httpRequest.getUrl();
        if (ContentType.matches(urlWithParams)) {
            return getContentType(urlWithParams);
        }
        if (urlWithParams.contains("?") && urlWithParams.contains("=")) {
            return getParam(urlWithParams);
        }
        return getPage(urlWithParams);
    }

    private String getContentType(final String request) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(pageController.mapContent(request).entrySet()).get(0);
            return makeResponse(HttpStatus.OK, "text/css", responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, TEXT_HTML, e.getMessage());
        }
    }

    private String getParam(final String request) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(jwpController.mapResponse(request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), TEXT_HTML, responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, TEXT_HTML, e.getMessage());
        }
    }

    private String getPage(final String request) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(pageController.mapResponse(Optional.empty(), request).entrySet()).get(0);
            return makeResponse(responseEntry.getKey(), TEXT_HTML, responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeResponse(HttpStatus.NOT_FOUND, TEXT_HTML, e.getMessage());
        }

    }

}
