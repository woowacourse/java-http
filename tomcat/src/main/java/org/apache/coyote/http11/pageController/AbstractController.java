package org.apache.coyote.http11.pageController;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public abstract class AbstractController implements PageController {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        switch (httpRequest.getMethod()) {
            case GET -> doGet(httpRequest, httpResponse);
            case POST -> doPost(httpRequest, httpResponse);
            default -> methodNotAllowed(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        methodNotAllowed(httpRequest, httpResponse);
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        methodNotAllowed(httpRequest, httpResponse);
    }

    private void methodNotAllowed(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setBody(
                HttpStatus.METHOD_NOT_ALLOWED,
                "text/plain",
                "지원하지 않는 HTTP 메서드입니다: " + httpRequest.getMethod()
        );
    }
}
