package nextstep.jwp.mvc.handler;

import nextstep.jwp.mvc.view.ModelAndView;
import nextstep.jwp.webserver.request.HttpMethod;
import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface Handler {

    boolean matchUrl(String httpUrl, HttpMethod httpMethod);

    ModelAndView doRequest(HttpRequest httpRequest, HttpResponse httpResponse);
}
