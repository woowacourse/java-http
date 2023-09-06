package nextstep.jwp;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.HttpDispatcher;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
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
    public Http11Response handle(final Http11Request request) throws IOException {
        final Handler handler = handlerResolver.resolve(request.getHttpMethod(), request.getPath());
        if (handler != null) {
            return handler.resolve(request);
        }
        final var resource = getClass().getClassLoader().getResource(STATIC + request.getPath());
        if (resource == null) {
            final var notFoundResource = getClass().getClassLoader().getResource(STATIC + "/404.html");
            return makeHttp11Response(Objects.requireNonNull(notFoundResource), StatusCode.NOT_FOUND);
        }
        return makeHttp11Response(resource, StatusCode.OK);
    }

    private Http11Response makeHttp11Response(final URL resource, final StatusCode statusCode) throws IOException {
        final var actualFilePath = new File(resource.getPath()).toPath();
        final var fileBytes = Files.readAllBytes(actualFilePath);
        final String responseBody = new String(fileBytes, StandardCharsets.UTF_8);
        return new Http11Response(statusCode, ContentType.findByPath(resource.getPath()), responseBody);
    }
}
