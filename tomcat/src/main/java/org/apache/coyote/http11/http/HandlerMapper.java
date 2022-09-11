package org.apache.coyote.http11.http;

import static org.apache.coyote.http11.http.request.HttpMethod.GET;
import static org.apache.coyote.http11.http.request.HttpMethod.POST;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import nextstep.jwp.controller.Handler;
import nextstep.jwp.controller.HomeHandler;
import nextstep.jwp.controller.resource.IndexHandler;
import nextstep.jwp.controller.resource.ResourceHandler;
import nextstep.jwp.controller.auth.LoginHandler;
import nextstep.jwp.controller.resource.LoginPageHandler;
import nextstep.jwp.controller.resource.RegisterHandler;
import nextstep.jwp.controller.resource.RegisterPageHandler;
import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;

public enum HandlerMapper {
    HOME(GET, Constants.HOME_URL_REGEX, new HomeHandler()),
    INDEX(GET, Constants.INDEX_PAGE_REGEX, new IndexHandler()),
    LOGIN_PAGE(GET, Constants.LOGIN_URL_REGEX, new LoginPageHandler()),
    LOGIN(POST, Constants.LOGIN_REGEX, new LoginHandler()),
    REGISTER_PAGE(GET, Constants.REGISTER_PAGE_REGEX, new RegisterPageHandler()),
    REGISTER(POST, Constants.REGISTER_REGEX, new RegisterHandler()),
    RESOURCE(GET, Constants.RESOURCE_URL_REGEX, new ResourceHandler());

    private final HttpMethod method;
    private final Pattern urlRegex;
    private final Handler handler;

    HandlerMapper(final HttpMethod method, final Pattern urlRegex, final Handler handler) {
        this.method = method;
        this.urlRegex = urlRegex;
        this.handler = handler;
    }

    public static Handler getHandlerFrom(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String url = httpRequest.getUrl();
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
        private static final Pattern LOGIN_REGEX = Pattern.compile("^(/login)(\\?([^#\\s]*))?");
        private static final Pattern REGISTER_PAGE_REGEX = Pattern.compile("^(/register)(\\?([^#\\s]*))?");
        private static final Pattern REGISTER_REGEX = Pattern.compile("^(/register)(\\?([^#\\s]*))?");
        private static final Pattern INDEX_PAGE_REGEX = Pattern.compile("^(/index\\.html)");
    }

    private static boolean matchUrl(final String url, final HandlerMapper handlerMapper) {
        return handlerMapper.urlRegex.matcher(url).find();
    }

    private static boolean matchMethod(final HttpMethod httpMethod, final HandlerMapper handlerMapper) {
        return handlerMapper.method.equals(httpMethod);
    }
}
