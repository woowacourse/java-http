package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HomeHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.ResourceHandler;

public enum HandlerMapper {
    HOME(Pattern.compile("^/$"), new HomeHandler()),
    RESOURCE(Pattern.compile("^(/[a-z|A-Z|가-힣|ㄱ-ㅎ|_|\\-]*)+(\\.[a-z]*)$"), new ResourceHandler()),
    LOGIN(Pattern.compile("^(/login)(\\?([^#\\s]*))?"), new LoginHandler());

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
}
