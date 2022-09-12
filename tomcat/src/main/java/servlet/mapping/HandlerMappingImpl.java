package servlet.mapping;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import servlet.handler.Controller;
import servlet.handler.ResourceFinder;

public class HandlerMappingImpl implements HandlerMapping {

    private final List<Controller> controllers;
    private final ResourceFinder resourceFinder = new ResourceFinder();

    public HandlerMappingImpl(List<Controller> controllers) {
        this.controllers = controllers;
    }

    @Override
    public ResponseEntity map(HttpRequest request) {
        Controller controller = findController(request);
        return controller.service(request);
    }

    private Controller findController(HttpRequest request) {
        Path path = request.getPath();
        return controllers.stream()
                .filter(controller -> isSamePath(path, controller))
                .findFirst()
                .orElse(resourceFinder);
    }

    private boolean isSamePath(Path path, Controller controller) {
        return Path.of(controller.getPath()).equals(path);
    }
}
