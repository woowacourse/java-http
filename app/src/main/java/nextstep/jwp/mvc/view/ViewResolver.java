package nextstep.jwp.mvc.view;

import nextstep.jwp.webserver.request.HttpRequest;
import nextstep.jwp.webserver.response.HttpResponse;

public interface ViewResolver {

    void render(ModelAndView modelAndView, HttpRequest httpRequest, HttpResponse httpResponse);
}
