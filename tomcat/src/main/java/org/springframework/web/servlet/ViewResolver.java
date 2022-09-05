package org.springframework.web.servlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.http11.util.ResourceSearcher;

public class ViewResolver {

    private static final ResourceSearcher RESOURCE_SEARCHER = ResourceSearcher.getInstance();
    private static final ViewResolver VIEW_RESOLVER = new ViewResolver();

    private final Map<String, MappingResponse> VALUE = new HashMap<>();

    private ViewResolver() {
    }

    public static ViewResolver getInstance() {
        return VIEW_RESOLVER;
    }

    public void add(final String url, final MappingResponse value) {
        validateUrlIsNotDuplicated(url);
        VALUE.put(url, value);
    }

    public MappingResponse get(final String url) {
        if (RESOURCE_SEARCHER.isFile(url)) {
            return new MappingResponse(url, "OK");
        }
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
