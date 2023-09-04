package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.RequestReader;

public class FrontController {

    private final LoginController loginController = new LoginController();
    private final MainPageController mainPageController = new MainPageController();
    private final IndexController indexController = new IndexController();
    private final OtherController otherController = new OtherController();


    private static final FrontController frontController = new FrontController();

    private FrontController() {
    }

    public static FrontController getInstance() {
        return frontController;
    }

    public Controller findController(RequestReader requestReader) {
        if (requestReader.getRequestUrl().equals("/")) {
            return mainPageController;
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
