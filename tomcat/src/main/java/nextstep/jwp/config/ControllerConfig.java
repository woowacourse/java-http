package nextstep.jwp.config;

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
    private static final SessionManager sessionManager = new SessionManager();
    private static final LoginService loginService = new LoginService(sessionManager);
    private static final RequestMapping requestMapping = new RequestMapping(
            new HomeController(),
            new LoginController(loginService),
            new RegisterController(),
            new ResourceController()
    );
    public static final FrontController frontController = new FrontController(
            requestMapping,
            exceptionControllerAdvice
    );

    private ControllerConfig() {
    }
}
