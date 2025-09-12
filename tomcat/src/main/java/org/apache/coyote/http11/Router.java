package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.catalina.session.Session;
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
            log.error("exception: ", e);
            staticHandler.serveErrorPage(httpResponse, e.getHttpStatus());
        } catch (Throwable t) {
            log.error("exception: ", t);
            staticHandler.serveErrorPage(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.uri().equals("/login")) {
            Session session = httpRequest.getSession();
            if (session != null && session.getAttribute("user") != null) {
                httpResponse.setStatusCode(HttpStatus.FOUND);
                httpResponse.setHeader("Location", "http://localhost:8080");
                return;
            }
        }
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
