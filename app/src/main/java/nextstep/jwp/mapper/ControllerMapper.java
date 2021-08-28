package nextstep.jwp.mapper;

import nextstep.jwp.handler.LoginController;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.handler.ModelAndView;

public class ControllerMapper implements HandlerMapper {

    @Override
    public ModelAndView mapping(RequestLine request) {
        LoginController loginController = new LoginController();
        return loginController.mapping(request);
    }
}

