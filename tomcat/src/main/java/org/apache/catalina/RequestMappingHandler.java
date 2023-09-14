package org.apache.catalina;

import nextstep.jwp.LoginHandler;
import nextstep.jwp.model.User;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.*;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public enum RequestMappingHandler {

    STRING(RequestMappingHandler::isStringGetUrl, new HelloController()),
    FILE(RequestMappingHandler::isFileGetUrl, new FileGetController()),
    LOGIN_GET(RequestMappingHandler::isLoginGetUrl, new LoginGetController()),
    LOGIN_POST(RequestMappingHandler::isLoginPostUrl, new LoginPostController()),
    REGISTER_GET(RequestMappingHandler::isRegisterGetUrl, new RegisterGetController()),
    REGISTER_POST(RequestMappingHandler::isRegisterPostUrl, new RegisterPostController());

    private static final Pattern FILE_REGEX = Pattern.compile(".+\\.(html|css|js|ico)");

    private final BiPredicate<String, HttpMethod> condition;
    private final Controller controller;

    RequestMappingHandler(final BiPredicate<String, HttpMethod> condition, final Controller controller) {
        this.condition = condition;
        this.controller = controller;
    }

    public static Controller findController(final HttpRequest request) {
        final String resourcePath = request.getRequestLine().getRequestUrl();
        final HttpMethod requestMethod = request.getRequestLine().getHttpMethod();

        final Controller findController = Arrays.stream(values())
                .filter(value -> value.condition.test(resourcePath, requestMethod))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 url 요청입니다."))
                .getController();

        if (findController instanceof LoginPostController
                && (!request.hasJSessionId())) {
            addSession(request);
        }
        if (findController instanceof LoginGetController
                && request.hasJSessionId()
                && SessionManager.isExist(request.getJSessionId())) {
            request.setHasValidatedSessionTrue();
        }
        return findController;
    }

    private static void addSession(final HttpRequest request) {
        final SessionManager sessionManager = new SessionManager();
        final LoginHandler loginHandler = new LoginHandler();
        final User user = loginHandler.getUser(request.getRequestBody());
        final HttpSession httpSession = new HttpSession("user", user);
        sessionManager.add(httpSession);
        request.addJSessionId(httpSession.getId());
    }

    public static boolean isFileGetUrl(final String resourcePath, final HttpMethod requestMethod) {
        return FILE_REGEX.matcher(resourcePath).matches() && requestMethod == HttpMethod.GET;
    }

    public static boolean isStringGetUrl(final String resourcePath, final HttpMethod requestMethod) {
        return resourcePath.equals("/") && requestMethod == HttpMethod.GET;
    }

    public static boolean isLoginGetUrl(final String requestUrl, final HttpMethod requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod == HttpMethod.GET;
    }

    public static boolean isLoginPostUrl(final String requestUrl, final HttpMethod requestMethod) {
        return requestUrl.startsWith("/login") && requestMethod == HttpMethod.POST;
    }

    public static boolean isRegisterGetUrl(final String requestUrl, final HttpMethod requestMethod) {
        return requestUrl.startsWith("/register") && requestMethod == HttpMethod.GET;
    }

    public static boolean isRegisterPostUrl(final String requestUrl, final HttpMethod requestMethod) {
        return requestUrl.startsWith("/register") && requestMethod == HttpMethod.POST;
    }

    public Controller getController() {
        return controller;
    }

}
