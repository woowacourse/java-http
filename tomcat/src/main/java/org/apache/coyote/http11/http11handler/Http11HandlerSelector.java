package org.apache.coyote.http11.http11handler;

import java.util.List;
import org.apache.coyote.http11.http11handler.exception.NoProperHandlerException;

public class Http11HandlerSelector {

    private List<Http11Handler> http11Handlers = List.of(new Http11DefaultPageHandler(), new Http11StaticResourceHandler());

    public Http11Handler getHttp11Handler(String uri) {
        return http11Handlers.stream()
                .filter(it -> it.isProperHandler(uri))
                .findAny()
                .orElseThrow(NoProperHandlerException::new);
    }
}
