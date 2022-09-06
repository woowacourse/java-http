package org.springframework.web.servlet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.Controller;

public class HandlerMapping {

    private static final HandlerMapping HANDLER_MAPPING = new HandlerMapping();

    private final Map<String, Controller> VALUE = new HashMap<>();

    private HandlerMapping() {
    }

    public static HandlerMapping getInstance() {
        return HANDLER_MAPPING;
    }

    public void add(final String url, final Controller value) {
        validateUrlIsNotDuplicated(url);
        VALUE.put(url, value);
    }

    public Controller get(final String url) {
        if (!VALUE.containsKey(url)) {
            throw new IllegalArgumentException(String.format("등록되지 않은 url 입니다. [%s]", url));
        }
        return VALUE.get(url);
    }

    private void validateUrlIsNotDuplicated(final String url) {
        if (VALUE.containsKey(url)) {
            throw new IllegalArgumentException(String.format("이미 등록된 url 입니다. [%s]", url));
        }
    }
}
