package org.apache.catalina.handler;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.UserService;
import java.util.Map;
import org.apache.coyote.http11.Http11ContentTypeParser;
import org.apache.coyote.http11.Http11Method;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class ServletRequestHandler {

    private static final String SUCCESS_STATUS_CODE = "200 OK";
    private static final String FOUND_STATUS_CODE = "302 Found";
    private static final String UNAUTHORIZED_STATUS_CODE = "401 Unauthorized";
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
        final String path = "/index.html";
        final String body = viewResolver.resolve(path);
        return createResponse(FOUND_STATUS_CODE, path, body);
    }

    private Http11Response handleGetLoginFailed() {
        final String path = "/401.html";
        final String body = viewResolver.resolve(path);
        return createResponse(UNAUTHORIZED_STATUS_CODE, path, body);
    }

    private Http11Response handleGetRoot() {
        final String body = "Hello world!";
        return createResponse(SUCCESS_STATUS_CODE, DEFAULT_HTML_PATH, body);
    }

    private Http11Response handleGetStatic(String requestURI) {
        final String body = viewResolver.resolve(requestURI);
        return createResponse(SUCCESS_STATUS_CODE, requestURI, body);
    }

    private Http11Response createResponse(String statusCode, String path, String body) {
        final String contentType = Http11ContentTypeParser.parse(path);
        return new Http11Response(statusCode, contentType, body);
    }
}
