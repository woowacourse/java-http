package nextstep.jwp.mapper;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.exception.IncorrectMapperException;
import nextstep.jwp.handler.IController;
import nextstep.jwp.handler.LoginController;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;

public class ControllerMapper implements HandlerMapper {

    private final List<IController> controllers;

    public ControllerMapper() {
        controllers = Arrays.asList(new LoginController());
    }

    @Override
    public boolean mapping(RequestLine request) {
        return controllers.stream()
                .anyMatch(controller -> controller.mapping(request.getMethod(), request.getUriPath()));
    }

    @Override
    public ModelAndView service(RequestLine request) {
        String method = request.getMethod();
        RequestUriPath uriPath = request.getUriPath();

        IController controller = searchController(method, uriPath);
        return controller.service(method, uriPath);
    }

    private IController searchController(String method, RequestUriPath uriPath){
        return controllers.stream()
                .filter(controller -> controller.mapping(method, uriPath))
                .findFirst()
                .orElseThrow(IncorrectMapperException::new);
    }
}

