package nextstep.jwp.handler;

import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {

    private static final String ROOT_PATH = "/";
    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final List<String> STATIC_PATHS = List.of(".css", ".js");

    private final HttpRequestHandler rootRequestHandler;
    private final HttpRequestHandler staticRequestHandler;
    private final HttpRequestHandler loginRequestHandler;
    private final HttpRequestHandler registerRequestHandler;

    public RequestMapping(final HttpVersion httpVersion) {
        this.rootRequestHandler = new RootRequestHandler(httpVersion);
        this.staticRequestHandler = new StaticRequestHandler(httpVersion);
        this.loginRequestHandler = new LoginRequestHandler(httpVersion);
        this.registerRequestHandler = new RegisterRequestHandler(httpVersion);
    }

    public HttpRequestHandler getHandler(final HttpRequest httpRequest) {
        if (isLoginRequest(httpRequest.getPath())) {
            return loginRequestHandler;
        }
        if (isRegisterRequest(httpRequest.getPath())) {
            return registerRequestHandler;
        }
        if (isIndexRequest(httpRequest.getPath())) {
            return staticRequestHandler;
        }
        if (isRootRequest(httpRequest.getPath())) {
            return rootRequestHandler;
        }
        if (isStaticRequest(httpRequest.getPath())) {
            return staticRequestHandler;
        }
        throw new UncheckedServletException("지원하지 않는 path입니다." + httpRequest.getPath());
    }

    private boolean isLoginRequest(final String path) {
        return path.equals(LOGIN_PATH);
    }

    private boolean isRegisterRequest(final String path) {
        return path.equals(REGISTER_PATH);
    }

    private boolean isIndexRequest(final String path) {
        return path.equals(INDEX_PATH);
    }

    private boolean isRootRequest(final String path) {
        return path.equals(ROOT_PATH);
    }

    private boolean isStaticRequest(final String path) {
        return STATIC_PATHS.stream()
                .anyMatch(path::endsWith);
    }
}
