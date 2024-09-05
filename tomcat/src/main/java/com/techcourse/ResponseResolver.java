package com.techcourse;

import org.apache.HttpRequest;
import org.apache.HttpResponse;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

public class ResponseResolver {

    private final Controller controller;
    private final SessionManager sessionManager;


    public ResponseResolver() {
        this.controller = new Controller();
        this.sessionManager = SessionManager.getInstance();
    }

    public HttpResponse processRequest(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getMethod().equals("GET")) {
            return processGetRequest(httpRequest);
        }
        if (httpRequest.getMethod().equals("POST")) {
            return processPostRequest(httpRequest.getUri(), httpRequest.getPayload());
        }
        throw new IllegalArgumentException("unexpected http method");
    }

    private HttpResponse processGetRequest(HttpRequest httpRequest) throws IOException {
        var uri = httpRequest.getUri();
        if (uri.equals("/")) {
            var responseBody = controller.getHomePage();
            return HttpResponse.ok(uri, responseBody);
        }
        if (uri.equals("/register")) {
            var responseBody = controller.getRegisterPage();
            return HttpResponse.ok(uri, responseBody);
        }
        if (uri.startsWith("/login")) {
            String sessionId = httpRequest.getSessionIdFromCookie();
            if (sessionId == null || sessionManager.findSession(sessionId) == null) {
                var responseBody = controller.getUriPage(uri);
                return HttpResponse.ok(uri, responseBody);
            }
            return HttpResponse.redirect(uri, "/index.html");
        }
        if (uri.startsWith("/favicon.ico")) {
            var favicon = controller.serveFavicon();
            if (favicon == null) {
                return HttpResponse.notFound();
            }
            return HttpResponse.okWithContentType(favicon, "image/x-icon");
        }
        var responseBody = controller.getUriPage(uri);
        return HttpResponse.ok(uri, responseBody);
    }


    private HttpResponse processPostRequest(String uri, Map<String, String> params) {
        if (uri.startsWith("/login")) {
            var redirectUri = "/401.html";
            try {
                var user = controller.login(params.get("account"), params.get("password"));

                String uuid = UUID.randomUUID().toString();
                Session session = new Session(uuid);
                session.setAttribute("user", user);
                sessionManager.add(session);

                redirectUri = "/index.html";
                HttpResponse response = HttpResponse.redirect(uri, redirectUri);
                response.setCookie("JSESSIONID", uuid);
                return response;
            } catch (RuntimeException exception) {
                return HttpResponse.redirect(uri, redirectUri);
            }
        }

        if (uri.startsWith("/register")) {
            var redirectUri = "/401.html";

            boolean isSucceed = controller.register(params.get("account"), params.get("password"), params.get("email"));
            if (isSucceed) {
                redirectUri = "/index.html";
            }

            return HttpResponse.redirect(uri, redirectUri);
        }
        throw new IllegalArgumentException("undefined POST URI");
    }
}
