package nextstep.jwp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.handler.controller.AbstractController;
import nextstep.jwp.handler.resource.ResourceHandlerImpl;
import nextstep.jwp.mapper.ControllerMapper;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMappers;
import nextstep.jwp.mapper.ResourceHandlerMapper;
import nextstep.jwp.view.ViewResolver;

public class Assembler {

    private final ViewResolver viewResolver;
    private final Dispatcher dispatcher;

    public Assembler() {
        ControllerMapper controllerMapper = controllerMapper();
        ResourceHandlerMapper resourceHandlerMapper = resourceHandlerMapper();

        HandlerMappers handlerMappers = handlerMappers(controllerMapper, resourceHandlerMapper);
        viewResolver = viewResolver();

        dispatcher = new Dispatcher(handlerMappers, viewResolver);
    }

    private ViewResolver viewResolver() {
        return new ViewResolver(ServerConfig.ROOT_RESPONSE, ServerConfig.RESOURCE_BASE_PATH);
    }

    private HandlerMappers handlerMappers(HandlerMapper... handlerMappers) {
        List<HandlerMapper> handlerMapperBeans = Arrays.asList(handlerMappers);
        return new HandlerMappers(handlerMapperBeans);
    }

    private ResourceHandlerMapper resourceHandlerMapper() {
        List<ResourceHandlerImpl> handlerBeans = Arrays.asList(new ResourceHandlerImpl());
        return new ResourceHandlerMapper(handlerBeans);
    }

    private ControllerMapper controllerMapper() {
        try {
            Map<String, AbstractController> tempMap = new HashMap<>();
            for (String key : ServerConfig.CONTROLLER_MAP.keySet()) {
                AbstractController controller = ServerConfig.CONTROLLER_MAP.get(key).getConstructor().newInstance();
                tempMap.put(key, controller);
            }
            return new ControllerMapper(tempMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("There's an error creating components");
        }
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public ViewResolver viewSolver() {
        return viewResolver;
    }
}
