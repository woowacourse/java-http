package com.techcourse.controller;

import com.techcourse.service.AuthService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.RequestLineInfo;

public class AuthController extends AbstractController {

    private static final String HTTP_1_1 = "HTTP/1.1";

    private final AuthService authService = new AuthService();
    
    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return handleAuthPageAccess(request.getRequestUri(), request.getHttpCookie());
    }
    
    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return handlePostRequest(request);
    }
    
    private HttpResponse handlePostRequest(HttpRequest request) throws IOException, URISyntaxException {
        String requestUri = request.getRequestUri();
        Map<String, String> formData = request.getFormData();

        if (requestUri.equals("/register")) {
            return handleRegister(formData, request.getHttpCookie());
        } else if (requestUri.equals("/login")) {
            return handleLogin(formData, request.getHttpCookie());
        }
        return new HttpResponse(HTTP_1_1, HttpStatus.NOT_FOUND, HttpHeaders.empty(), "");
    }
    
    private HttpResponse handleRegister(Map<String, String> formData, HttpCookie httpCookie) throws IOException {
        try {
            String sessionId = authService.register(formData, httpCookie);
            return handleAuthSuccess(sessionId);
        } catch (IllegalArgumentException e) {
            return send401Page();
        }
    }
    
    private HttpResponse handleLogin(Map<String, String> formData, HttpCookie httpCookie) throws IOException, URISyntaxException {
        try {
            String sessionId = authService.login(formData, httpCookie);
            return handleAuthSuccess(sessionId);
        } catch (IllegalArgumentException e) {
            return send401Page();
        }
    }
    
    private HttpResponse handleAuthSuccess(String sessionId) {
        return buildAuthRedirectResponse("/index.html", sessionId);
    }
    
    private HttpResponse buildAuthRedirectResponse(String location, String sessionId) {
        HttpHeaders headers = new HttpHeaders()
                .add("Location", location)
                .add("Content-Length", "0");
        
        if (sessionId != null) {
            headers.add("Set-Cookie", "JSESSIONID=" + sessionId);
        }
        return new HttpResponse(HTTP_1_1, HttpStatus.FOUND, headers, "");
    }
    
    private HttpResponse send401Page() throws IOException {
        try (InputStream inputStream = AuthController.class.getClassLoader().getResourceAsStream("static/401.html")) {
            byte[] responseBody = inputStream.readAllBytes();
            HttpHeaders headers = new HttpHeaders()
                    .add("Content-Type", "text/html;charset=utf-8")
                    .add("Content-Length", String.valueOf(responseBody.length));
            
            return new HttpResponse(
                    HTTP_1_1,
                    HttpStatus.UNAUTHORIZED,
                    headers,
                    new String(responseBody, StandardCharsets.UTF_8)
            );
        }
    }
    
    private HttpResponse buildRedirectResponse(String location) {
        return new HttpResponse(
                HTTP_1_1,
                HttpStatus.FOUND,
                new HttpHeaders()
                        .add("Location", location)
                        .add("Content-Length", "0"),
                "");
    }
    
    private HttpResponse handleAuthPageAccess(String pagePath, HttpCookie httpCookie) throws Exception {
        if (authService.isLoggedIn(httpCookie)) {
            return buildRedirectResponse("/index.html");
        } else {
            HttpRequest request = HttpRequest.from(
                    RequestLineInfo.of(HttpMethod.GET, pagePath, HTTP_1_1),
                    HttpHeaders.empty(),
                    ""
            );
            StaticResourceController staticController = new StaticResourceController();
            return staticController.service(request);
        }
    }
}
