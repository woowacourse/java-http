package nextstep.org.apache.coyote.http11;

import java.util.Set;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.catalina.servlet.DispatcherServlet;

public class DispatcherServletFixture {

    public static final DispatcherServlet DISPATCHER_SERVLET = new DispatcherServlet(
            Set.of(
                    new StaticResourceRequestHandler(), new LoginRequestHandler(), new RegisterRequestHandler()
            )
    );
}
