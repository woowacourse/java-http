package nextstep;

import java.util.List;
import java.util.Map;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.servlet.DispatcherServletManager;
import nextstep.servlet.StaticResourceResolver;
import nextstep.servlet.interceptor.Interceptor;
import nextstep.servlet.interceptor.SessionInterceptor;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.RequestLine;

public class Application {

    public static void main(String[] args) {
        final var tomcat = configTomcat();
        tomcat.start();
    }

    private static Tomcat configTomcat() {
        final DispatcherServletManager servletManger = configServletManager();
        return new Tomcat(servletManger);
    }

    private static DispatcherServletManager configServletManager() {
        final Map<List<RequestLine>, Interceptor> interceptors = Map.of(
                List.of(
                        new RequestLine(HttpMethod.GET, "/login"),
                        new RequestLine(HttpMethod.POST, "/login")
                )
                , new SessionInterceptor()
        );

        final List<Controller> controllers = List.of(
                new HomeController(),
                new LoginController(),
                new RegisterController()
        );

        final StaticResourceResolver staticResourceResolver = new StaticResourceResolver();

        return new DispatcherServletManager(
                interceptors,
                controllers,
                staticResourceResolver
        );
    }
}
