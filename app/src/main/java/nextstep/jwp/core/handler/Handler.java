package nextstep.jwp.core.handler;

import nextstep.jwp.request.HttpMethod;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;

public interface Handler {

    boolean matchUrl(String httpUrl, HttpMethod httpMethod);

    String doRequest(HttpRequest httpRequest, HttpResponse httpResponse);
}
