package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMapping {

    MAIN(new MainController(), url -> url.equals("/")),
    LOGIN(new LoginController(), url -> url.contains("/login")),
    RESOURCES(new ResourceController(), url -> url.contains(".")),
    REGISTER(new RegisterController(), url -> url.equals("/register"));

    private final Controller controller;
    private final Predicate<String> predicate;

    RequestMapping(Controller controller, Predicate<String> predicate) {
        this.controller = controller;
        this.predicate = predicate;
    }

    public static Optional<RequestMapping> findController(HttpRequest httpRequest) {
        return Arrays.stream(values())
                .filter(controller -> controller.predicate.test(httpRequest.getUrl()))
                .findFirst();
    }

    public Controller getController() {
        return controller;
    }
}
