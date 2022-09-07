package org.apache.coyote.http11.http;

import static org.apache.coyote.http11.http.request.HttpMethod.GET;
import static org.apache.coyote.http11.http.request.HttpMethod.POST;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HomeHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.LoginPageHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.RegisterPageHandler;
import org.apache.coyote.http11.handler.ResourceHandler;
import org.apache.coyote.http11.http.request.HttpMethod;

public enum HandlerMapper {
    HOME(GET, Constants.HOME_URL_REGEX, new HomeHandler()),
    RESOURCE(GET, Constants.RESOURCE_URL_REGEX, new ResourceHandler()),
    LOGIN_PAGE(GET, Constants.LOGIN_URL_REGEX, new LoginPageHandler()),
    LOGIN(POST, Pattern.compile("^(/login)(\\?([^#\\s]*))?"), new LoginHandler()),
    REGISTER_PAGE(GET, Pattern.compile("^(/register)(\\?([^#\\s]*))?"), new RegisterPageHandler()),
    REGISTER(POST, Pattern.compile("^(/register)(\\?([^#\\s]*))?"), new RegisterHandler());

    private final HttpMethod method;
    private final Pattern urlRegex;
    private final Handler handler;

    HandlerMapper(final HttpMethod method, final Pattern urlRegex, final Handler handler) {
        this.method = method;
        this.urlRegex = urlRegex;
        this.handler = handler;
    }

    public static Handler getHandlerFrom(final HttpMethod httpMethod, final String url) {
        return Arrays.stream(HandlerMapper.values())
                .filter(it -> matchUrl(url, it) && matchMethod(httpMethod, it))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 handler가 없습니다. " + url))
                .handler;
    }

    private static class Constants {
        private static final Pattern HOME_URL_REGEX = Pattern.compile("^/$");
        private static final Pattern RESOURCE_URL_REGEX = Pattern.compile("^(/[a-z|A-Z|가-힣|ㄱ-ㅎ|_|0-9|\\-]*)+(\\.[a-z]*)$");
        private static final Pattern LOGIN_URL_REGEX = Pattern.compile("^(/login)(\\?([^#\\s]*))?");
    }

    private static boolean matchUrl(final String url, final HandlerMapper handlerMapper) {
        return handlerMapper.urlRegex.matcher(url).find();
    }

    private static boolean matchMethod(final HttpMethod httpMethod, final HandlerMapper handlerMapper) {
        return handlerMapper.method.equals(httpMethod);
    }
}
