package org.apache.coyote.http11.http11handler;

import java.util.List;
import org.apache.coyote.http11.http11handler.exception.NoProperHandlerException;

public class Http11HandlerSelector {

    private List<Http11Handler> http11Handlers = List.of(new IndexPageHandler(), new LoginPageHandler(), new ResourceHandler(), new DefaultPageHandler());

    public Http11Handler getHttp11Handler(String uri) {
        return http11Handlers.stream()
                .filter(it -> it.isProperHandler(uri))
                .findAny()
                .orElseThrow(NoProperHandlerException::new);
    }
}
