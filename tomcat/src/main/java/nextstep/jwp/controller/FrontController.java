package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.coyote.http11.request.HttpRequest;

public enum FrontController {

    MAIN(new MainController(), url -> url.equals("/")),
    LOGIN(new LoginController(), url -> url.contains("/login")),
    RESOURCES(new ResourceController(), url -> url.contains("."));

    private final Controller controller;
    private final Predicate<String> predicate;

    FrontController(Controller controller, Predicate<String> predicate) {
        this.controller = controller;
        this.predicate = predicate;
    }

    public static Controller findController(HttpRequest httpRequest) {
        return Arrays.stream(values())
                .filter(controller -> controller.predicate.test(httpRequest.getPath().getUrl()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("컨트롤러를 찾을 수 없습니다."))
                .controller;
    }
}
