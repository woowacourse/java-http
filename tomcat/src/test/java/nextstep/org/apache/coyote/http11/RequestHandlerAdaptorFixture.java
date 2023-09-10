package nextstep.org.apache.coyote.http11;

import java.util.Set;
import nextstep.jwp.servlet.LoginRequestServlet;
import nextstep.jwp.servlet.RegisterRequestServlet;
import org.apache.catalina.core.RequestHandlerAdaptor;
import org.apache.catalina.core.RequestMapping;
import org.apache.catalina.core.servlet.DefaultServlet;

public class RequestHandlerAdaptorFixture {

    public static final RequestHandlerAdaptor REQUEST_HANDLER_ADAPTOR = new RequestHandlerAdaptor(
            new RequestMapping(Set.of(new LoginRequestServlet(), new RegisterRequestServlet()), new DefaultServlet())
    );
}
