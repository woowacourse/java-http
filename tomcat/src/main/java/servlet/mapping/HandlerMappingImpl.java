package servlet.mapping;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public class HandlerMappingImpl implements HandlerMapping {
    private final List<Controller> controllers;

    public HandlerMappingImpl(List<Controller> controllers) {
        this.controllers = controllers;
    }

    @Override
    public ResponseEntity map(HttpRequest request) {
        Controller controller = findController(request);

        ResponseEntity entity = new ResponseEntity();
        controller.service(request, entity);
        return entity;
    }

    private Controller findController(HttpRequest request) {
        return controllers.stream()
                .filter(element -> element.isMapped(request))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("매핑되는 컨트롤러 메소드가 없습니다." + request.getPath()));
    }
}
