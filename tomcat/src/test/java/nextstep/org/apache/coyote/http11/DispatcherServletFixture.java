package nextstep.org.apache.coyote.http11;

import java.util.Set;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import org.apache.catalina.servlet.RequestHandlerAdaptor;

public class DispatcherServletFixture {

    public static final RequestHandlerAdaptor DISPATCHER_SERVLET = new RequestHandlerAdaptor(
            Set.of(new LoginRequestHandler(), new RegisterRequestHandler())
    );
}
