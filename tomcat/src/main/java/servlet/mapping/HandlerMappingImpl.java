package servlet.mapping;

import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;
import static org.apache.coyote.http11.response.element.HttpMethod.POST;

import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.controller.ControllerImpl;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.Path;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HandlerMappingImpl implements HandlerMapping {

    private final ControllerImpl controllerImpl;

    public HandlerMappingImpl(ControllerImpl controllerImpl) {
        this.controllerImpl = controllerImpl;
    }

    @Override
    public ResponseEntity map(HttpRequest request) {
        HttpMethod method = request.getMethod();
        Path path = request.getPath();

        if (method == GET && path.contains(List.of("/", ""))) {
            return controllerImpl.welcome();
        }
        if (method == GET && path.same("/login")) {
            return controllerImpl.goLoginPage();
        }
        if (method == POST && path.same("/login")) {
            return controllerImpl.login(request);
        }
        if (method == GET && path.same("/register")) {
            return controllerImpl.goRegisterPage();
        }
        if (method == POST && path.same("/register")) {
            return controllerImpl.register(request);
        }
        if (method == GET && getResource(path) != null) {
            return controllerImpl.findResource(path);
        }

        throw new NoSuchElementException("매핑되는 컨트롤러 메소드가 없습니다." + request.getPath());
    }

    private URL getResource(Path path) {
        String name = ROOT + path.getPath();
        return getClass().getClassLoader().getResource(name);
    }
}
