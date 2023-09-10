package nextstep.jwp.config;

import nextstep.jwp.common.ResourceLoader;
import nextstep.jwp.presentation.ExceptionControllerAdvice;
import nextstep.jwp.presentation.FrontController;
import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.RequestMapping;
import nextstep.jwp.presentation.ResourceController;
import nextstep.jwp.service.LoginService;
import org.apache.catalina.SessionManager;

public class ControllerConfig {

    public static final ExceptionControllerAdvice exceptionControllerAdvice = new ExceptionControllerAdvice();
    private static final ResourceLoader resourceLoader = new ResourceLoader();
    private static final SessionManager sessionManager = new SessionManager();
    private static final LoginService loginService = new LoginService(sessionManager);
    private static final LoginController loginController = new LoginController(resourceLoader, loginService);
    private static final HomeController homeController = new HomeController();
    private static final RegisterController registerController = new RegisterController(resourceLoader);
    private static final ResourceController resourceController = new ResourceController(resourceLoader);
    private static final RequestMapping requestMapping = new RequestMapping(
            homeController,
            loginController,
            registerController,
            resourceController
    );
    public static final FrontController frontController = new FrontController(
            requestMapping,
            exceptionControllerAdvice
    );

    private ControllerConfig() {
    }
}
