package servlet.mapping;

import static org.apache.coyote.Constants.ROOT;
import static org.apache.coyote.http11.response.element.HttpMethod.GET;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HandlerMappingImpl implements HandlerMapping {

    private final Controller controller;

    public HandlerMappingImpl(Controller controller) {
        this.controller = controller;
    }

    @Override
    public ResponseEntity map(HttpRequest request) {
        HttpMethod method = request.getMethod();
        String path = request.getPath();

        if (method == GET && List.of("/", "").contains(path)) {
            return controller.welcome(method);
        }
        if (method == GET && path.equals("/login")) {
            return controller.goLoginPage(method);
        }
        if (method == GET && path.split("\\?")[0].equals("/login")) {
            return controller.login(method, path);
        }
        if (method == GET && getClass().getClassLoader().getResource(ROOT + path) != null) {
            return controller.findResource(method, path);
        }

        throw new NoSuchElementException("매핑되는 컨트롤러 메소드가 없습니다." + request.getPath());
    }
}
