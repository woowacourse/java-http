package org.apache.coyote.http11.request;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapper {

    private final LoginController loginController;

    private final RegisterController registerController;

    private final StaticPageController staticPageController;

    public RequestMapper() {
        this.loginController = new LoginController();
        this.registerController = new RegisterController();
        this.staticPageController = new StaticPageController();
    }

    public HttpResponse mapRequest(HttpRequest request) throws URISyntaxException, IOException {
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            if (request.getHttpRequestPath().contains("/login")) {
                return loginController.login(request);
            }
            if (request.getHttpRequestPath().contains("/register")) {
                return registerController.register(request);
            }
        }
        return staticPageController.getStaticPage(request);
    }
}
