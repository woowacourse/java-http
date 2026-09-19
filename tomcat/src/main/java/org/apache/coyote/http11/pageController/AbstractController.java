package org.apache.coyote.http11.pageController;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements PageController {
    @Override
    public HttpResponse run(HttpRequest httpRequest) throws IOException {
        return switch (httpRequest.getMethod()) {
            case GET -> doGet(httpRequest);
            case POST -> doPost(httpRequest);
            default -> methodNotAllowed(httpRequest);
        };
    }

    protected HttpResponse doGet(HttpRequest httpRequest) throws IOException {
        return methodNotAllowed(httpRequest);
    }

    protected HttpResponse doPost(HttpRequest httpRequest) throws IOException {
        return methodNotAllowed(httpRequest);
    }

    protected HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatus.FOUND, Map.of("Location", location), "");
    }

    private HttpResponse methodNotAllowed(HttpRequest httpRequest) {
        return HttpResponse.of(
                HttpStatus.METHOD_NOT_ALLOWED,
                "text/plain",
                "지원하지 않는 HTTP 메서드입니다: " + httpRequest.getMethod()
        );
    }
}
