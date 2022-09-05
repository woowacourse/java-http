package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HomeHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.ResourceHandler;

public enum HandlerMapper {
    HOME(Constants.HOME_URL_REGEX, new HomeHandler()),
    RESOURCE(Constants.RESOURCE_URL_REGEX, new ResourceHandler()),
    LOGIN(Constants.LOGIN_URL_REGEX, new LoginHandler());

    private final Pattern urlRegex;
    private final Handler handler;

    HandlerMapper(final Pattern urlRegex, final Handler handler) {
        this.urlRegex = urlRegex;
        this.handler = handler;
    }

    public static Handler getHandlerFrom(final String url) {
        return Arrays.stream(HandlerMapper.values())
                .filter(it -> it.urlRegex.matcher(url).find())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 handler가 없습니다. " + url))
                .handler;
    }

    private static class Constants {
        private static final Pattern HOME_URL_REGEX = Pattern.compile("^/$");
        private static final Pattern RESOURCE_URL_REGEX = Pattern.compile("^(/[a-z|A-Z|가-힣|ㄱ-ㅎ|_|\\-]*)+(\\.[a-z]*)$");
        private static final Pattern LOGIN_URL_REGEX = Pattern.compile("^(/login)(\\?([^#\\s]*))?");
    }
}
