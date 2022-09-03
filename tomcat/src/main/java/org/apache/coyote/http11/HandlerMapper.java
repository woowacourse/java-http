package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HomeHandler;
import org.apache.coyote.http11.handler.ResourceHandler;

public enum HandlerMapper {
    HOME(Pattern.compile("^/$"), new HomeHandler()),
    RESOURCE(Pattern.compile("^/[a-z|A-Z]|[\\-]*/?\\.[a-z|A-Z]|[\\-]*"), new ResourceHandler());

    private Pattern urlRegex;
    private Handler handler;

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
