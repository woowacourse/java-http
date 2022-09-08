package org.apache.coyote.http11.http11handler;

import java.util.List;
import org.apache.coyote.http11.http11handler.exception.NoProperHandlerException;
import org.apache.coyote.http11.http11handler.impl.DefaultPageHandler;
import org.apache.coyote.http11.http11handler.impl.ErrorPageHandler;
import org.apache.coyote.http11.http11handler.impl.IndexPageHandler;
import org.apache.coyote.http11.http11handler.impl.LoginHandler;
import org.apache.coyote.http11.http11handler.impl.LoginPageHandler;
import org.apache.coyote.http11.http11handler.impl.RegisterAccountHandler;
import org.apache.coyote.http11.http11handler.impl.RegisterPageHandler;
import org.apache.coyote.http11.http11handler.impl.ResourceHandler;
import org.apache.coyote.http11.http11request.Http11Request;

public class Http11HandlerSelector {

    private List<Http11Handler> http11Handlers = List.of(new IndexPageHandler(), new LoginPageHandler(),
            new ResourceHandler(), new DefaultPageHandler(), new RegisterPageHandler(), new RegisterAccountHandler(),
            new ErrorPageHandler(), new LoginHandler());

    public Http11Handler getHttp11Handler(Http11Request http11Request) {
        return http11Handlers.stream()
                .filter(it -> it.isProperHandler(http11Request))
                .findAny()
                .orElseThrow(NoProperHandlerException::new);
    }
}
