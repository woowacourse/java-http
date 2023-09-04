package nextstep.jwp.presentation;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.RequestReader;

public class FrontController {

    private static final Map<String, Controller> controllers = new HashMap<>();
    private static final FrontController frontController = new FrontController();

    static {
        controllers.put("/", frontController.mainPageController);
        controllers.put("/login", frontController.loginController);
        controllers.put("/register", frontController.loginController);
        controllers.put("/index", frontController.indexController);
        controllers.put("/index.html", frontController.indexController);
    }

    private final LoginController loginController = new LoginController();
    private final MainPageController mainPageController = new MainPageController();
    private final IndexController indexController = new IndexController();
    private final OtherController otherController = new OtherController();

    private FrontController() {
    }

    public static FrontController getInstance() {
        return frontController;
    }

    public Controller findController(RequestReader requestReader) {
        return controllers.getOrDefault(requestReader.getRequestUrl(), otherController);
    }
}
