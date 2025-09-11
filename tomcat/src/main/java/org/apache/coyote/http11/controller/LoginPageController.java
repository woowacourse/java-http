package org.apache.coyote.http11.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.request_response.HttpCookie;
import org.apache.coyote.http11.request_response.HttpRequest;
import org.apache.coyote.http11.request_response.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPageController implements Controller {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @Override
    public boolean supports(HttpRequest request) {
        return request.getRequestMethod().equals("GET") && request.getUriPath().equals("/login");
    }

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        Session session = getSession(request.getHttpCookie());
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user != null) {
                return HttpResponse.builder()
                    .status(HttpStatus.Found)
                    .header("Location", "/index.html")
                    .body("")
                    .build();
            }
        }
        String responseBody = readStaticFile("/login.html");
        return HttpResponse.builder()
            .status(HttpStatus.OK)
            .contentType("text/html;charset=utf-8")
            .body(responseBody)
            .build();
    }

    private Session getSession(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return null;
        }
        if (httpCookie.getCookie("JSESSIONID") == null) {
            return null;
        }
        String jsessionid = httpCookie.getCookie("JSESSIONID");
        SessionManager sessionManager = SessionManager.getInstance();
        return sessionManager.findSession(jsessionid);
    }

    private String readStaticFile(String filePath) throws IOException {
        String staticFilePath = STATIC_RESOURCE_PATH + filePath;
        URL resource = getClass().getClassLoader().getResource(staticFilePath);
        if (resource == null) {
            throw new IllegalArgumentException("리소스가 존재하지 않습니다. " + staticFilePath);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
