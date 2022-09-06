package org.apache.coyote.http11.util;

import java.util.ArrayList;
import java.util.List;

public class StaticResourceExtensionSupporter {

    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    private static final String SVG_EXTENSION = ".svg";
    private static final String ICO_EXTENSION = ".ico";
    private static final List<String> EXTENSION_LIST = new ArrayList<>();

    static {
        EXTENSION_LIST.addAll(List.of(HTML_EXTENSION, CSS_EXTENSION, JS_EXTENSION, SVG_EXTENSION, ICO_EXTENSION));
    }

    public static boolean isStaticResource(final String requestURI) {
        return EXTENSION_LIST.stream().anyMatch(requestURI::contains);
    }
}
