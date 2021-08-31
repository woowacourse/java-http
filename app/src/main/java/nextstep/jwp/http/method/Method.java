package nextstep.jwp.http.method;

import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.PageController;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;

import java.util.ArrayList;
import java.util.Map;

public abstract class Method {
    protected final PageController pageController;
    protected final JwpController jwpController;

    Method() {
        this.pageController = new PageController();
        this.jwpController = new JwpController();
    }

    public abstract HttpResponse matchFunction();

    HttpResponse getHttpResponse(final ContentType contentType, final Map<HttpStatus, String> mappedResponse) {
        try {
            Map.Entry<HttpStatus, String> responseEntry = new ArrayList<>(mappedResponse.entrySet()).get(0);
            return makeHttpResponse(contentType, responseEntry.getKey(), responseEntry.getValue());
        } catch (IllegalArgumentException e) {
            return makeHttpResponse(contentType, HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private HttpResponse makeHttpResponse(ContentType contentType, HttpStatus notFound, String message) {
        return HttpResponse.builder()
                .status(notFound)
                .contentType(contentType)
                .responseBody(message)
                .build();
    }
}
