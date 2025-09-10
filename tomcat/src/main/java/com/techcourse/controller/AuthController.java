package com.techcourse.controller;

import com.techcourse.service.AuthService;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class AuthController extends AbstractController {
    
    private static final String INDEX_HTML = "/index.html";
    
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
        return new HttpResponse("HTTP/1.1", HttpStatus.NOT_FOUND, HttpHeaders.empty(), "");
    }
    
    private HttpResponse handleRegister(Map<String, String> formData, HttpCookie httpCookie) throws IOException {
        String sessionId = authService.register(formData, httpCookie);
        return handleAuthSuccess(sessionId);
    }
    
    private HttpResponse handleLogin(Map<String, String> formData, HttpCookie httpCookie) throws IOException, URISyntaxException {
        try {
            String sessionId = authService.authenticate(formData, httpCookie);
            return handleAuthSuccess(sessionId);
        } catch (IllegalArgumentException e) {
            return send401Page();
        }
    }
    
    private HttpResponse handleAuthSuccess(String sessionId) {
        return buildAuthRedirectResponse(INDEX_HTML, sessionId);
    }
    
    private HttpResponse buildAuthRedirectResponse(String location, String sessionId) {
        HttpHeaders headers = HttpHeaders.redirect(location);
        
        if (sessionId != null) {
            headers.add("Set-Cookie", "JSESSIONID=" + sessionId);
        }
        return new HttpResponse("HTTP/1.1", HttpStatus.FOUND, headers, "");
    }
    
    private HttpResponse send401Page() throws IOException {
        try (InputStream inputStream = AuthController.class.getClassLoader().getResourceAsStream("static/401.html")) {
            byte[] responseBody = inputStream.readAllBytes();
            HttpHeaders headers = HttpHeaders.html()
                .add("Content-Length", String.valueOf(responseBody.length));
            
            return new HttpResponse(
                    "HTTP/1.1",
                    HttpStatus.UNAUTHORIZED,
                    headers,
                    new String(responseBody, StandardCharsets.UTF_8)
            );
        }
    }
    
    private HttpResponse buildRedirectResponse(String location) {
        return new HttpResponse("HTTP/1.1", HttpStatus.FOUND, HttpHeaders.redirect(location), "");
    }
    
    private HttpResponse handleAuthPageAccess(String pagePath, HttpCookie httpCookie) throws Exception {
        if (authService.isLoggedIn(httpCookie)) {
            return buildRedirectResponse(INDEX_HTML);
        } else {
            // 로그인하지 않은 사용자는 StaticResourceController를 통해 페이지를 보여줌
            String requestLine = "GET " + pagePath + " HTTP/1.1";
            HttpRequest request = HttpRequest.from(requestLine, new HashMap<>(), "");
            StaticResourceController staticController = new StaticResourceController();
            return staticController.service(request);
        }
    }
}
