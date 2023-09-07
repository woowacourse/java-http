package nextstep.jwp.controller;

import java.util.Arrays;
import nextstep.jwp.controller.page.HelloWorldPageController;
import nextstep.jwp.controller.page.IndexGetPageController;
import nextstep.jwp.controller.page.LoginGetPageController;
import nextstep.jwp.controller.page.LoginPostPageController;
import nextstep.jwp.controller.page.NotFoundPageController;
import nextstep.jwp.controller.page.RegisterPostPageController;
import org.apache.coyote.http11.request.HttpRequest;

public enum PageControllerMapping {

    HELLO_WORLD("/", "GET", HelloWorldPageController.create()),
    REGISTER_POST("/register", "POST", RegisterPostPageController.create()),
    LOGIN_GET("/login", "GET", LoginGetPageController.create()),
    LOGIN_POST("/login", "POST", LoginPostPageController.create()),
    INDEX_GET("/index", "GET", IndexGetPageController.create());

    private final String uri;
    private final String method;
    private final Controller controller;

    PageControllerMapping(final String uri, final String method, final Controller controller) {
        this.uri = uri;
        this.method = method;
        this.controller = controller;
    }

    public static Controller find(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();
        final String requestUriWithoutExtension = requestUri.split("\\.")[0];
        final String requestMethod = httpRequest.getMethod();

        return Arrays.stream(PageControllerMapping.values())
                .filter(value -> value.uri.equals(requestUriWithoutExtension) && value.method.equals(requestMethod))
                .map(value -> value.controller)
                .findFirst()
                .orElse(NotFoundPageController.create());
    }
}
