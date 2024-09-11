package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String JSESSIONID_NAME = "JSESSIONID";
    private static final String PATH_DIRECTIVE = "path";
    private static final String DIRECTIVE_DELIMITER = "=";
    private static final String DIRECTIVES_DELIMITER = "; ";
    private static final String HTTP_ONLY_DIRECTIVE = "HttpOnly";

    private final Map<String, String> directives;

    private boolean isHttpOnly;

    public HttpCookie(Map<String, String> directives) {
        this.directives = directives;
        this.isHttpOnly = false;
    }

    public static HttpCookie createWithRandomJsessionid() {
        return new HttpCookie(new HashMap<>(Map.of(
                "JSESSIONID_NAME", UUID.randomUUID().toString()
        )));
    }

    public void setHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    public void setPath(String path) {
        directives.put(PATH_DIRECTIVE, path);
    }

    public String getJsessionid() {
        return directives.getOrDefault(JSESSIONID_NAME, "");
    }

    public String stringify() {
        String directivesString = directives.entrySet()
                .stream()
                .map(directive -> String.join(DIRECTIVE_DELIMITER, directive.getKey(), directive.getValue()))
                .collect(Collectors.joining(DIRECTIVES_DELIMITER));

        if (!isHttpOnly) {
            return directivesString;
        }

        return String.join(DIRECTIVES_DELIMITER, directivesString, HTTP_ONLY_DIRECTIVE);
    }

    public String assembleDirective(Map.Entry<String, String> directive) {
        return String.join(DIRECTIVE_DELIMITER, directive.getKey(), directive.getValue());
    }
}
