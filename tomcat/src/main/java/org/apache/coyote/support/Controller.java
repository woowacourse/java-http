package org.apache.coyote.support;

import nextstep.jwp.presentation.HomeController;
import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.exception.BadRequestException;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpRequestUri;

public class Controller {

    private final HttpRequest httpRequest;

    public Controller(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpResponse execute() {
        HttpRequestUri uri = httpRequest.getUri();
        if (uri.getValue().equals("/")) {
            HomeController controller = new HomeController();
            return controller.index(httpRequest);
        }

        if (isLogin(uri.getValue())) {
            LoginController loginController = new LoginController();
            return loginController.login(httpRequest);
        }

        if (uri.isBasicContentType()) {
            BasicController basicController = new BasicController();
            return basicController.execute(httpRequest);
        }

        throw new BadRequestException(uri.getValue());
    }

    private boolean isLogin(final String uri) {
        if (uri.contains("?")) {
            String requestUri = uri.substring(0, uri.indexOf("?"));
            return requestUri.equals("/login");
        }
        return false;
    }
}
