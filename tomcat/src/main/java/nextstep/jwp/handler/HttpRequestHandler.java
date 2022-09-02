package nextstep.jwp.handler;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public interface HttpRequestHandler {

    HttpResponse handleHttpRequest(HttpRequest httpRequest);
}
