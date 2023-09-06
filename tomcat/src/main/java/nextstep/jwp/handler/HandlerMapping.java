package nextstep.jwp.handler;

import java.util.List;
import nextstep.jwp.exception.NotSupportedRequestException;
import org.apache.coyote.http.vo.HttpRequest;

public class HandlerMapping {

    private static final List<Handler> handlers = List.of(
            new DefaultPageHandler(),
            new LoginPageHandler(),
            new RegisterPageHandler(),
            new LoginHandler(),
            new RegisterHandler()
    );

    public static boolean hasSupportedHandler(final HttpRequest request) {
        return handlers.stream()
                .anyMatch(it -> it.isSupported(request));
    }

    public static Handler getSupportedHandler(final HttpRequest request) {
        return handlers.stream()
                .filter(it -> it.isSupported(request))
                .findFirst()
                .orElseThrow(NotSupportedRequestException::new);
    }
}
