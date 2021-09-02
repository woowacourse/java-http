package nextstep.jwp.mvc;

import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface StaticResourceHandler {

    void handleResource(HttpRequest httpRequest, HttpResponse httpResponse);
}
