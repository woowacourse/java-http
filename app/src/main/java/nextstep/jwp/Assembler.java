package nextstep.jwp;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.handler.controller.AbstractController;
import nextstep.jwp.handler.resource.ResourceHandler;
import nextstep.jwp.mapper.ControllerMapper;
import nextstep.jwp.mapper.HandlerMapper;
import nextstep.jwp.mapper.HandlerMappers;
import nextstep.jwp.mapper.ResourceHandlerMapper;
import nextstep.jwp.view.ViewResolver;

public class Assembler {

    private final HandlerMapper handlerMapper;
    private final ViewResolver viewResolver;
    private final Dispatcher dispatcher;

    private final ResourceHandlerMapper resourceHandlerMapper;
    private final ControllerMapper controllerMapper;

    public Assembler() {
        controllerMapper = controllerMapper();
        resourceHandlerMapper = resourceHandlerMapper();

        handlerMapper = handlerMapper(controllerMapper, resourceHandlerMapper);
        viewResolver = viewResolver();

        dispatcher = new Dispatcher(handlerMapper, viewResolver);
    }

    private ViewResolver viewResolver() {
        return new ViewResolver(ServerConfig.ROOT_RESPONSE, ServerConfig.RESOURCE_BASE_PATH);
    }

    private HandlerMappers handlerMapper(HandlerMapper... handlerMappers) {
        return new HandlerMappers(handlerMappers);
    }

    private ResourceHandlerMapper resourceHandlerMapper() {
        return new ResourceHandlerMapper(new ResourceHandler());
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
}
