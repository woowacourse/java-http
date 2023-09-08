package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestUri;

public class ControllerFinder {

    private static final ControllerFinder INSTANCE = new ControllerFinder();

    private final LoginController loginController = new LoginController();
    private final IndexController indexController = new IndexController();
    private final MainController mainController = new MainController();
    private final OtherController otherController = new OtherController();

    private ControllerFinder() {
    }

    public static ControllerFinder getInstance() {
        return INSTANCE;
    }

    public Controller findController(RequestUri requestUri) {
        String uri = requestUri.getUri();
        if (uri.equals("/")) {
            return mainController;
        }
        if (uri.equals("/login") || uri.equals("/register")) {
            return loginController;
        }
        if (uri.equals("/index") || uri.equals("/index.html")) {
            return indexController;
        }
        return otherController;
    }
}
