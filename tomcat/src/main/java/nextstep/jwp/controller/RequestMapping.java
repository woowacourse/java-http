package nextstep.jwp.controller;

import java.util.Arrays;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.utils.Files;

public enum RequestMapping {

    DEFAULT("/", new DefaultController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private static final Controller NOT_FOUND_CONTROLLER = new NotFoundController();

    private final String path;
    private final Controller controller;

    RequestMapping(final String path, final Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller fromPath(final String path) {
        if (!path.equals(DEFAULT.path) && Files.existsFile(path)) {
            return new FileController();
        }

        return Arrays.stream(RequestMapping.values())
                .filter(it -> it.path.equals(path))
                .findFirst()
                .map(it -> it.controller)
                .orElse(NOT_FOUND_CONTROLLER);
    }
}
