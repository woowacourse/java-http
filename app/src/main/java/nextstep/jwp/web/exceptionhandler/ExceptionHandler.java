package nextstep.jwp.web.exceptionhandler;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface ExceptionHandler {
    void handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
