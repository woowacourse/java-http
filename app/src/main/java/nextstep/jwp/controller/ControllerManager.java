package nextstep.jwp.controller;

import java.util.stream.Stream;

public enum ControllerManager {

    DEFAULT("/", new DefaultController()),
    LOGIN("/login", new LoginController()),
    REGISTER("/register", new RegisterController());

    private String path;
    private Controller controller;

    ControllerManager(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller findControllerByUri(String uri) {
        return Stream.of(values())
                .filter(manager -> manager.path.equals(uri))
                .map(manager -> manager.controller)
                .findFirst()
                .orElseGet(IndexController::new);
    }
}
