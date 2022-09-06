package org.apache.coyote.support;

import static org.apache.coyote.http11.model.ContentType.TEXT_HTML_CHARSET_UTF_8;

import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.exception.BadRequestException;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.model.HttpStatus;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpPath;

public class Controller {

    private final HttpRequest httpRequest;

    public Controller(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse get() {
        HttpPath uri = httpRequest.getUri();
        if (uri.getValue().equals("/")) {
            HomeController controller = new HomeController();
            return controller.index(httpRequest);
        }

        if (uri.getValue().equals("/login.html") || uri.getValue().equals("/login")) {
            BasicController basicController = new BasicController();
            return basicController.execute(httpRequest);
        }

        if (uri.isBasicContentType()) {
            BasicController basicController = new BasicController();
            return basicController.execute(httpRequest);
        }

        return HttpResponse.builder()
                .version(httpRequest.getVersion())
                .status(HttpStatus.OK.getValue())
                .headers("Content-Type: " + TEXT_HTML_CHARSET_UTF_8.getValue())
                .build();
    }

    public HttpResponse post() {
        LoginController controller = new LoginController();
        if (httpRequest.isEqualToUri("/login.html") || httpRequest.isEqualToUri("/login")) {
            return controller.login(httpRequest);
        }
        if (httpRequest.isEqualToUri("/register.html") || httpRequest.isEqualToUri("/register")) {
            return controller.register(httpRequest);
        }
        throw new BadRequestException(httpRequest.getUri().getValue());
    }
}
