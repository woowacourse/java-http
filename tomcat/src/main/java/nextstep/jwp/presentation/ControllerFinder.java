package nextstep.jwp.presentation;

import org.apache.coyote.http11.RequestReader;

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

    public Controller findController(RequestReader requestReader) {
        if (requestReader.getRequestUrl().equals("/")) {
            return mainController;
        }
        if (requestReader.getRequestUrl().equals("/login") || requestReader.getRequestUrl().equals("/register")) {
            return loginController;
        }
        if (requestReader.getRequestUrl().equals("/index") || requestReader.getRequestUrl().equals("/index.html")) {
            return indexController;
        }
        return otherController;
    }
}
