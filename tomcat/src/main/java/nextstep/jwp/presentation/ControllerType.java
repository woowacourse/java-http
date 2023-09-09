package nextstep.jwp.presentation;

import java.util.List;

public enum ControllerType {

    MAIN("/", MainController.getInstance()),
    LOGIN("/login.html", LoginController.getInstance()),
    REGISTER("/register.html", RegisterController.getInstance()),
    INDEX("/index.html", IndexController.getInstance()),
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
                .orElse(OtherController.getInstance());
    }
}
