package org.apache.catalina.handler;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.UserService;
import java.util.Map;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.HttpResponseContent;

public class ServletRequestHandler {

    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String FOUND_STATUS_CODE = "302 Found";
    private static final String DEFAULT_HTML_PATH = ".html";

    private final ViewResolver viewResolver;

    public ServletRequestHandler(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public HttpResponse handle(HttpRequest request) {
        final HttpMethod httpMethod = request.getHttpMethod();
        if (HttpMethod.GET.equals(httpMethod)) {
            return handleGet(request);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            return handlePost(request);
        }
        throw new IllegalArgumentException("해당 메서드는 지원하지 않습니다: " + httpMethod); // TODO: 405 처리
    }

    private HttpResponse handleGet(HttpRequest request) {
        final String requestURI = request.getRequestURI();
        if (requestURI.equals("/login")) {
            return handleGetLoginPage();
        }
        if (requestURI.equals("/register")) {
            return handleGetRegisterPage();
        }
        if (requestURI.equals("/")) {
            return handleGetRootPage();
        }
        if (requestURI.contains(".")) {
            return handleGetStaticPage(requestURI);
        }
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private HttpResponse handleGetLoginPage() {
        final String path = "/login.html";
        final String body = viewResolver.resolve(path);
        return new HttpResponse(SUCCESS_STATUS_CODE, new HttpResponseContent(path, body));
    }

    private HttpResponse handleGetRegisterPage() {
        final String path = "/register.html";
        final String body = viewResolver.resolve(path);
        return new HttpResponse(SUCCESS_STATUS_CODE, new HttpResponseContent(path, body));
    }

    private HttpResponse handleGetRootPage() {
        final String body = "Hello world!";
        return new HttpResponse(SUCCESS_STATUS_CODE, new HttpResponseContent(DEFAULT_HTML_PATH, body));
    }

    private HttpResponse handleGetStaticPage(String requestURI) {
        final String path = requestURI;
        final String body = viewResolver.resolve(path);
        return new HttpResponse(SUCCESS_STATUS_CODE, new HttpResponseContent(path, body));
    }

    private HttpResponse handlePost(HttpRequest request) {
        final String requestURI = request.getRequestURI();
        if (requestURI.equals("/login")) {
            return handlePostLogin(request.getBody());
        }
        if (requestURI.equals("/register")) {
            return handlePostRegister(request.getBody());
        }
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private HttpResponse handlePostLogin(Map<String, String> body) {
        final UserService userService = UserService.getInstance();
        try {
            userService.login(body.get("account"), body.get("password"));
            return handlePostLoginSuccess();
        } catch (TechcourseException e) {
            return handlePostLoginFailed();
        }
    }

    private HttpResponse handlePostLoginSuccess() {
        final String location = "/index.html";
        return new HttpResponse(FOUND_STATUS_CODE, location);
    }

    private HttpResponse handlePostLoginFailed() {
        final String location = "/401.html";
        return new HttpResponse(FOUND_STATUS_CODE, location);
    }

    private HttpResponse handlePostRegister(Map<String, String> body) {
        final UserService userService = UserService.getInstance();
        try {
            userService.register(body.get("account"), body.get("password"), body.get("email"));
            return handlePostRegisterSuccess();
        } catch (TechcourseException e) {
            throw new IllegalArgumentException(e.getMessage()); // TODO: 400 처리
        }
    }

    private HttpResponse handlePostRegisterSuccess() {
        final String location = "/index.html";
        return new HttpResponse(FOUND_STATUS_CODE, location);
    }
}
