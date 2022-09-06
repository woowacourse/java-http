package nextstep.jwp.handler;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface HttpRequestHandler {

    HttpResponse handleHttpRequest(HttpRequest httpRequest);

    HttpResponse handleHttpGetRequest(HttpRequest httpRequest);

    HttpResponse handleHttpPostRequest(HttpRequest httpRequest);
}
