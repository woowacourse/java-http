package com.techcourse.servlet.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StaticResourceResolver implements ViewResolver {

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(List.of(".html", ".css", ".js", ".ico"));

    @Override
    public View resolve(String viewName) {
        if (support(viewName)) {
            return new StaticResourceView(viewName);
        }
        return null;
    }

    @Override
    public boolean support(String viewName) {
        return SUPPORTED_EXTENSIONS.stream().anyMatch(viewName::endsWith);
    }
}
