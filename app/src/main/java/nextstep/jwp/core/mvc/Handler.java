package nextstep.jwp.core.mvc;

import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface Handler {

    boolean matchUrl(String httpUrl, HttpMethod httpMethod);

    String doRequest(HttpRequest httpRequest, HttpResponse httpResponse);
}
