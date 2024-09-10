package org.apache.catalina.handler;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.UserService;
import java.util.Map;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.Http11ResponseContent;

public class ServletRequestHandler {

    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String FOUND_STATUS_CODE = "302 Found";
    private static final String DEFAULT_HTML_PATH = ".html";

    private final ViewResolver viewResolver;

    public ServletRequestHandler(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public Http11Response handle(Http11Request request) {
        final Http11Method httpMethod = request.getHttpMethod();
        if (Http11Method.POST.equals(httpMethod)) {
            return handlePost(request);
        }
        if (Http11Method.GET.equals(httpMethod)) {
            return handleGet(request);
        }
        throw new IllegalArgumentException("해당 메서드는 지원하지 않습니다: " + httpMethod); // TODO: 405 처리
    }

    private Http11Response handlePost(Http11Request request) {
        final String requestURI = request.getRequestURI();
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private Http11Response handleGet(Http11Request request) {
        final String requestURI = request.getRequestURI();
        final Map<String, String> queryString = request.getQueryParameters();
        if (requestURI.startsWith("/login")) {
            return handleGetLogin(queryString);
        }
        if (requestURI.equals("/register")) {
            return handleGetRegister();
        }
        if (requestURI.equals("/")) {
            return handleGetRoot();
        }
        if (requestURI.contains(".")) {
            return handleGetStatic(requestURI);
        }
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private Http11Response handleGetLogin(Map<String, String> queryString) {
        final UserService userService = new UserService();
        try {
            userService.login(queryString.get("account"), queryString.get("password"));
            return handleGetLoginSuccess();
        } catch (TechcourseException e) {
            return handleGetLoginFailed();
        }
    }

    private Http11Response handleGetLoginSuccess() {
        final String location = "/index.html";
        return new Http11Response(FOUND_STATUS_CODE, location);
    }

    private Http11Response handleGetLoginFailed() {
        final String location = "/401.html";
        return new Http11Response(FOUND_STATUS_CODE, location);
    }

    private Http11Response handleGetRegister() {
        final String path = "/register.html";
        final String body = viewResolver.resolve(path);
        return new Http11Response(SUCCESS_STATUS_CODE, new Http11ResponseContent(path, body));
    }

    private Http11Response handleGetRoot() {
        final String body = "Hello world!";
        return new Http11Response(SUCCESS_STATUS_CODE, new Http11ResponseContent(DEFAULT_HTML_PATH, body));
    }

    private Http11Response handleGetStatic(String requestURI) {
        final String path = requestURI;
        final String body = viewResolver.resolve(path);
        return new Http11Response(SUCCESS_STATUS_CODE, new Http11ResponseContent(path, body));
    }
}
