package nextstep.jwp.handler;

import nextstep.jwp.http.request.RequestUriPath;

public interface IController {
    boolean mapping(String method, RequestUriPath uriPath);

    ModelAndView service(String method, RequestUriPath uriPath);
}
