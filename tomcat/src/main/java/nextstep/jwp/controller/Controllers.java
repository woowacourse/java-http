package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public enum Controllers {

    MAIN(new MainController(), (input) -> input.equals("/")),
    LOGIN(new LoginController(), (input) -> input.contains("/login")),
    RESOURCE(new ResourceController(), (input) -> input.contains("."));

    private final Controller controller;
    private final Predicate<String> canHandle;

    Controllers(Controller controller, Predicate<String> canHandel) {
        this.controller = controller;
        this.canHandle = canHandel;
    }

    public static Optional<Controller> findController(String uri) {
        Optional<Controllers> result = Arrays.stream(Controllers.values())
            .filter(c -> c.canHandle.test(uri))
            .findFirst();

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get().controller);

    }

}
