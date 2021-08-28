package nextstep.jwp.mapper;

import nextstep.jwp.handler.LoginController;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.view.View;

public class ControllerMapper implements HandlerMapper {

    @Override
    public View mapping(RequestLine request) {
        LoginController loginController = new LoginController();
        return loginController.mapping(request);
    }
}

