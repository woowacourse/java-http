package nextstep.org.apache.coyote.http11;

import java.util.Set;
import nextstep.jwp.servlet.LoginRequestServlet;
import nextstep.jwp.servlet.RegisterRequestServlet;
import org.apache.catalina.core.RequestHandlerAdaptor;

public class DispatcherServletFixture {

    public static final RequestHandlerAdaptor REQUEST_HANDLER_ADAPTOR = new RequestHandlerAdaptor(
            Set.of(new LoginRequestServlet(), new RegisterRequestServlet())
    );
}
