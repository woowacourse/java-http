package nextstep.jwp.presentation;

import java.util.List;

public enum ControllerType {

    MAIN("/", new MainController()),
    LOGIN("/login.html", new LoginController()),
    REGISTER("/register.html", new RegisterController()),
    INDEX("/index.html", new IndexController()),
    ;

    private final String requestUri;
    private final Controller controller;
    private static final List<ControllerType> controllers = List.of(values());

    ControllerType(String requestUri, Controller controller) {
        this.requestUri = requestUri;
        this.controller = controller;
    }

    public static Controller findController(String requestUri) {
        return controllers.stream()
                .filter(findController -> findController.requestUri.equals(requestUri))
                .findFirst()
                .map(findController -> findController.controller)
                .orElse(new OtherController());
    }
}
