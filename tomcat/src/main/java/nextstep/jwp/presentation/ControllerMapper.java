package nextstep.jwp.presentation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public enum ControllerMapper {

    RESOURCE(new ResourceController(), ControllerMapper::isResourceFile),
    MAIN(new MainController(), uri -> uri.equals("/")),
    LOGIN(new LoginController(), uri -> uri.startsWith("/login")),
    REGISTER(new RegisterController(), uri -> uri.startsWith("/register"));

    private static final List<String> FILE_EXTENSIONS = List.of(".html", ".css", ".js", ".ico", ".svg");

    private final Controller controller;
    private final Predicate<String> canHandle;

    ControllerMapper(final Controller controller, final Predicate<String> canHandel) {
        this.controller = controller;
        this.canHandle = canHandel;
    }

    public static Optional<Controller> findController(final String uri) {
        return Arrays.stream(values())
                .filter(it -> it.canHandle.test(uri))
                .map(it -> it.controller)
                .findFirst();
    }

    private static boolean isResourceFile(final String uri) {
        return FILE_EXTENSIONS.stream()
                .anyMatch(uri::contains);
    }
}
