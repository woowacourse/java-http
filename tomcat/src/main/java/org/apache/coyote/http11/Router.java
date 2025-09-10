package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.http11.exception.CommonException;

public class Router {

    private final LoginController loginController = new LoginController();
    private final StaticResourceHandler staticHandler = new StaticResourceHandler();

    public void handle(
            HttpRequest httpRequest,
            HttpResponse httpResponse
    ) throws IOException {
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
        } catch (Throwable t) {
            staticHandler.serveErrorPage(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
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
    ) throws IOException {
        if (httpRequest.uri().equals("/login")) {
            loginController.login(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.uri().equals("/register")) {
            loginController.register(httpRequest, httpResponse);
            return;
        }
        staticHandler.serveErrorPage(httpResponse, HttpStatus.NOT_FOUND);
    }
}
