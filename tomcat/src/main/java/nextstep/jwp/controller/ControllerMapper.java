package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum ControllerMapper {

    MAIN(new MainController(), (uri) -> uri.equals("/")),
    LOGIN(new LoginController(), (uri) -> uri.startsWith("/login")),
    RESOURCE(new ResourceController(), (uri) -> uri.contains(".")),
    REGISTER(new RegisterController(), (uri) -> uri.startsWith("/register"));

    private final Controller controller;
    private final Predicate<String> canHandle;

    ControllerMapper(Controller controller, Predicate<String> canHandel) {
        this.controller = controller;
        this.canHandle = canHandel;
    }

    public static Optional<Controller> findController(String uri) {
        Optional<ControllerMapper> result = Arrays.stream(ControllerMapper.values())
            .filter(c -> c.canHandle.test(uri))
            .findFirst();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get().controller);

    }

}
