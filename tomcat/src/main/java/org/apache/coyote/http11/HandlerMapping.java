package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.HomeHandler;

public enum HandlerMapping {
    HOME("/", new HomeHandler());

    private String url;
    private Handler handler;

    HandlerMapping(final String url, final Handler handler) {
        this.url = url;
        this.handler = handler;
    }

    public static Handler getHandlerFrom(final String url) {
        return Arrays.stream(HandlerMapping.values())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당하는 handler가 없습니다. " + url))
                .handler;
    }
}
