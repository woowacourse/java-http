package nextstep.jwp;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.HttpDispatcher;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

public class JwpHttpDispatcher implements HttpDispatcher {

    private static final String STATIC = "static";

    private final HandlerResolver handlerResolver;

    public JwpHttpDispatcher(final HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        final Handler handler = handlerResolver.resolve(request.getHttpMethod(), request.getPath());
        if (handler != null) {
            return handler.resolve(request);
        }
        final var resource = getClass().getClassLoader().getResource(STATIC + request.getPath());
        if (resource == null) {
            final var notFoundResource = getClass().getClassLoader().getResource(STATIC + "/404.html");
            return HttpResponse.createBy(request.getVersion(), Objects.requireNonNull(notFoundResource), StatusCode.NOT_FOUND);
        }
        return HttpResponse.createBy(request.getVersion(), resource, StatusCode.OK);
    }
}
