package nextstep.jwp.controller.page;

import java.util.Arrays;
import nextstep.jwp.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;

public enum RequestMapping {

    HELLO_WORLD("/", HelloWorldController.create()),
    REGISTER_GET("/register", RegisterController.create()),
    LOGIN_GET("/login", LoginController.create()),
    INDEX_GET("/index", IndexController.create());

    private static final String COMMA_REGEX = "\\.";
    private static final int URI_INDEX = 0;

    private final String uri;
    private final Controller controller;

    RequestMapping(final String uri, final Controller controller) {
        this.uri = uri;
        this.controller = controller;
    }

    public static Controller find(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();
        final String requestUriWithoutExtension = requestUri.split(COMMA_REGEX)[URI_INDEX];

        return Arrays.stream(RequestMapping.values())
                .filter(value -> value.uri.equals(requestUriWithoutExtension))
                .map(value -> value.controller)
                .findFirst()
                .orElse(NotFoundController.create());
    }
}
