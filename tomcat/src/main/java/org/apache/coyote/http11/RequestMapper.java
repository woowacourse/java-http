package org.apache.coyote.http11;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticPageController;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RequestMapper {

    private static final RequestMapper instance = new RequestMapper();

    private final LoginController loginController;

    private final RegisterController registerController;

    private final StaticPageController staticPageController;

    private RequestMapper() {
        this.loginController = LoginController.getInstance();
        this.registerController = RegisterController.getInstance();
        this.staticPageController = StaticPageController.getInstance();
    }

    public void mapRequest(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        if (request.getHttpMethod().equals(HttpMethod.POST)) {
            if (request.getHttpRequestPath().contains("/login")) {
                loginController.login(request, response);
            }
            if (request.getHttpRequestPath().contains("/register")) {
                registerController.register(request, response);
            }
        }
        if (request.getHttpMethod().equals(HttpMethod.GET)) {
            staticPageController.getStaticPage(request, response);
        }
    }

    public static RequestMapper getInstance() {
        return instance;
    }
}
