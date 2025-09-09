package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.CommonException;

public class Router {

    private final LoginController loginController = new LoginController();
    private final StaticResourceHandler staticHandler = new StaticResourceHandler();

    public void handle(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws Exception {
        try {
            if ("/login".equals(httpRequest.uri()) && !httpRequest.getQuery().isEmpty()) {
                loginController.login(httpRequest, httpResponse);
                return;
            }
            staticHandler.serve(httpRequest, httpResponse);
        } catch (CommonException e) {
            staticHandler.serveErrorPage(httpResponse, e.getHttpStatus());
        }
    }
}
