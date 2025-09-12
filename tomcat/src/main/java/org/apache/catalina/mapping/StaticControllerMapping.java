package org.apache.catalina.mapping;

import com.techcourse.controller.StaticResourcesController;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.controller.Controller;

public class StaticControllerMapping {

    private final List<String> patterns = new ArrayList<>();

    public StaticControllerMapping() {
        patterns.add("/");
        patterns.add("/css/*");
        patterns.add("/js/*");
        patterns.add("/assets/*");
        patterns.add("/*.html");
    }

    public Controller findController(final String uri) {
        for (String pattern : patterns) {
            if (matches(uri, pattern)) {
                return new StaticResourcesController();
            }
        }
        return null;
    }

    private boolean matches(final String uri, final String pattern) {
        if (pattern.equals(uri)) {
            return true;
        }
        if (pattern.endsWith("/*") && uri.startsWith(pattern.substring(0, pattern.length() - 1))) {
            return true;
        }

        return pattern.equals("/*.html") && uri.endsWith(".html");
    }
}

