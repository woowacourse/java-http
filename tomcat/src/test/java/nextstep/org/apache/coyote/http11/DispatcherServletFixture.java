package nextstep.org.apache.coyote.http11;

import java.util.Set;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import org.apache.catalina.core.RequestHandlerAdaptor;

public class DispatcherServletFixture {

    public static final RequestHandlerAdaptor REQUEST_HANDLER_ADAPTOR = new RequestHandlerAdaptor(
            Set.of(new LoginRequestHandler(), new RegisterRequestHandler())
    );
}
