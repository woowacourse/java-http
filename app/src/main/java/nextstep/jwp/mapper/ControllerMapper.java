package nextstep.jwp.mapper;

import nextstep.jwp.exception.IncorrectMapperException;
import nextstep.jwp.handler.Controller;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.handler.service.LoginService;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;

import java.util.Arrays;
import java.util.List;

public class ControllerMapper implements HandlerMapper {

    private final List<Controller> controllers;

    public ControllerMapper() {
        controllers = Arrays.asList(
                new LoginController(new LoginService()),
                new RegisterController()
        );
    }

    @Override
    public boolean mapping(RequestLine requestLine) {
        return controllers.stream()
                .anyMatch(controller -> controller.mapping(requestLine));
    }

    // TODO :: Mapper와 Servicer를 분리하는 것은?

    @Override
    public ModelAndView service(HttpRequest request) {
        Controller controller = search(request.getRequestLine());
        return controller.service(request);
    }

    private Controller search(RequestLine requestLine) {
        return controllers.stream()
                .filter(controller -> controller.mapping(requestLine))
                .findFirst()
                .orElseThrow(IncorrectMapperException::new);
    }
}

