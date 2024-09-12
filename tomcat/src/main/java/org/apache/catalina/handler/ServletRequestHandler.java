package org.apache.catalina.handler;

import com.techcourse.exception.TechcourseException;
import com.techcourse.model.User;
import com.techcourse.model.UserService;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.response.HttpResponse;

public class ServletRequestHandler {

    private static final String DEFAULT_HTML_PATH = ".html";

    private final ViewResolver viewResolver;

    public ServletRequestHandler(final ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public HttpResponse handle(final HttpRequest request) {
        final HttpMethod httpMethod = request.getHttpMethod();
        if (HttpMethod.GET.equals(httpMethod)) {
            return handleGet(request);
        }
        if (HttpMethod.POST.equals(httpMethod)) {
            return handlePost(request);
        }
        throw new IllegalArgumentException("해당 메서드는 지원하지 않습니다: " + httpMethod); // TODO: 405 처리
    }

    private HttpResponse handleGet(final HttpRequest request) {
        final String requestURI = request.getRequestURI();
        if (requestURI.equals("/login")) {
            return handleGetLoginPage(request);
        }
        if (requestURI.equals("/register")) {
            return handleGetRegisterPage();
        }
        if (requestURI.equals("/")) {
            return handleGetRootPage();
        }
        if (requestURI.contains(".")) {
            return handleGetStaticPage(request);
        }
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private HttpResponse handleGetLoginPage(final HttpRequest request) {
        if (checkLogin(request)) {
            return handleGetLoginPageWithLogin();
        }
        return handleGetLoginPageWithoutLogin();
    }

    private boolean checkLogin(final HttpRequest request) {
        Session session = request.getSession();
        return session != null;
    }

    private HttpResponse handleGetLoginPageWithLogin() {
        final String path = "/index.html";
        final String body = viewResolver.resolve(path);
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
        return response;
    }

    private HttpResponse handleGetLoginPageWithoutLogin() {
        final String path = "/login.html";
        final String body = viewResolver.resolve(path);
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
        return response;
    }

    private HttpResponse handleGetRegisterPage() {
        final String path = "/register.html";
        final String body = viewResolver.resolve(path);
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
        return response;
    }

    private HttpResponse handleGetRootPage() {
        final String body = "Hello world!";
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(DEFAULT_HTML_PATH, body);
        return response;
    }

    private HttpResponse handleGetStaticPage(final HttpRequest request) {
        final String path = request.getRequestURI();
        final String body = viewResolver.resolve(path);
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContent(path, body);
        return response;
    }

    private HttpResponse handlePost(final HttpRequest request) {
        final String requestURI = request.getRequestURI();
        if (requestURI.equals("/login")) {
            return handlePostLogin(request);
        }
        if (requestURI.equals("/register")) {
            return handlePostRegister(request);
        }
        throw new IllegalArgumentException("해당 uri는 지원하지 않습니다: " + requestURI); // TODO: 404 처리
    }

    private HttpResponse handlePostLogin(final HttpRequest request) {
        final HttpRequestBody body = request.getBody();
        final UserService userService = UserService.getInstance();
        try {
            final User user = userService.login(body.get("account"), body.get("password"));
            final Session session = request.createSession();
            session.setAttribute("user", user);
            return handlePostLoginSuccess(session);
        } catch (TechcourseException e) {
            return handlePostLoginFailed();
        }
    }

    private HttpResponse handlePostLoginSuccess(final Session session) {
        final String location = "/index.html";
        final HttpCookie cookie = HttpCookie.ofJSessionId(session.getId());
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
        response.setCookie(cookie);
        return response;
    }

    private HttpResponse handlePostLoginFailed() {
        final String location = "/401.html";
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
        return response;
    }

    private HttpResponse handlePostRegister(final HttpRequest request) {
        final HttpRequestBody body = request.getBody();
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
        final HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.FOUND);
        response.setLocation(location);
        return response;
    }
}
