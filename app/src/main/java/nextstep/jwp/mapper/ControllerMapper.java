package nextstep.jwp.mapper;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.exception.IncorrectMapperException;
import nextstep.jwp.handler.Controller;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.handler.RegisterController;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;

public class ControllerMapper implements HandlerMapper {

    private final List<Controller> controllers;

    public ControllerMapper() {
        controllers = Arrays.asList(
                new LoginController(),
                new RegisterController()
        );
    }

    @Override
    public boolean mapping(RequestLine request) {
        return controllers.stream()
                .anyMatch(controller -> controller.mapping(request.getMethod(), request.getUriPath()));
    }

    // TODO :: Mapper와 Servicer를 분리하는 것은?

    @Override
    public ModelAndView service(RequestLine request) {
        String method = request.getMethod();
        RequestUriPath uriPath = request.getUriPath();

        Controller controller = search(method, uriPath);
        return controller.service(method, uriPath);
    }

    private Controller search(String method, RequestUriPath uriPath){
        return controllers.stream()
                .filter(controller -> controller.mapping(method, uriPath))
                .findFirst()
                .orElseThrow(IncorrectMapperException::new);
    }
}

