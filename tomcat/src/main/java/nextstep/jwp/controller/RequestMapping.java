package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum RequestMapping {

    MAIN(MainController.getInstance(), (uri) -> uri.equals("/")),
    LOGIN(LoginController.getInstance(), (uri) -> uri.startsWith("/login")),
    RESOURCE(ResourceController.getInstance(), (uri) -> uri.contains(".")),
    REGISTER(RegisterController.getInstance(), (uri) -> uri.startsWith("/register"));

    private final Controller controller;
    private final Predicate<String> canHandle;

    RequestMapping(Controller controller, Predicate<String> canHandel) {
        this.controller = controller;
        this.canHandle = canHandel;
    }

    public static Optional<Controller> getController(String uri) {
        Optional<RequestMapping> result = Arrays.stream(RequestMapping.values())
            .filter(c -> c.canHandle.test(uri))
            .findFirst();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get().controller);

    }

}
