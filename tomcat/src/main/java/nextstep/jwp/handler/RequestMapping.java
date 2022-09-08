package nextstep.jwp.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapping {

    private static final Pattern ROOT_PATH = Pattern.compile("/");
    private static final Pattern LOGIN_PATH = Pattern.compile("/login");
    private static final Pattern REGISTER_PATH = Pattern.compile("/register");
    private static final Pattern STATIC_PATH = Pattern.compile("^\\S+.(?i)(html|css|js)$");

    private final Map<Pattern, HttpRequestHandler> handlers;

    public RequestMapping(final HttpVersion httpVersion) {
        this.handlers = new ConcurrentHashMap<>();
        handlers.put(ROOT_PATH, new RootRequestHandler(httpVersion));
        handlers.put(LOGIN_PATH, new LoginRequestHandler(httpVersion));
        handlers.put(REGISTER_PATH, new RegisterRequestHandler(httpVersion));
        handlers.put(STATIC_PATH, new StaticRequestHandler(httpVersion));
    }

    public HttpRequestHandler getHandler(final HttpRequest httpRequest) {
        return handlers.entrySet()
                .stream()
                .filter(handler -> handler.getKey().matcher(httpRequest.getPath()).matches())
                .findFirst()
                .map(Entry::getValue)
                .orElseThrow(() -> new UncheckedServletException("지원하지 않는 path입니다." + httpRequest.getPath()));
    }
}
