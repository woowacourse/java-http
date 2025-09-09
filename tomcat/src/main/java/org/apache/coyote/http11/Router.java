package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Router {

    private static final Logger log = LoggerFactory.getLogger(Router.class);
    private final LoginController loginController = new LoginController();
    private final StaticResourceHandler staticHandler = new StaticResourceHandler();

    public void handle(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws Exception {
        try {
            if (httpRequest.method().equals("GET")) {
                doGet(httpRequest, httpResponse);
                return;
            }
            if (httpRequest.method().equals("POST")) {
                doPost(httpRequest, httpResponse);
                return;
            }
            staticHandler.serve(httpRequest, httpResponse);
        } catch (CommonException e) {
            staticHandler.serveErrorPage(httpResponse, e.getHttpStatus());
        }
    }

    private void doGet(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws IOException {
        staticHandler.serve(httpRequest, httpResponse);
    }


    private void doPost(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) {
        if (httpRequest.uri().equals("/login")) {
            loginController.login(httpRequest, httpResponse);
        }
        if (httpRequest.uri().equals("/register")) {
            loginController.register(httpRequest, httpResponse);
        }
    }
}
