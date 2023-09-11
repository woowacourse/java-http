package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.ExceptionHandler;
import org.apache.coyote.http11.controller.IndexController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.MethodNotAllowedController;
import org.apache.coyote.http11.controller.NotFoundController;
import org.apache.coyote.http11.controller.RegisterController;

public class Application {

    public static void main(String[] args) {
        RequestMapping requestMapping = getRequestMapping();
        ExceptionHandler exceptionHandler = getExceptionHandler();
        final var tomcat = new Tomcat(requestMapping, exceptionHandler);
        tomcat.start();
    }

    private static RequestMapping getRequestMapping() {
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.addController("/", new IndexController());
        requestMapping.addController("/login", new LoginController());
        requestMapping.addController("/register", new RegisterController());
        return requestMapping;
    }

    private static ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.builder()
            .methodNotAllowedController(new MethodNotAllowedController())
            .notFoundController(new NotFoundController())
            .build();
    }
}
