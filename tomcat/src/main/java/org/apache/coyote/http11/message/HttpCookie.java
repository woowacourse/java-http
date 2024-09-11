package org.apache.coyote.http11.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;

public class HttpCookie {

    private static final String JSESSIONID_DIRECTIVE = "JSESSIONID";
    private static final String PATH_DIRECTIVE = "path";
    private static final String DIRECTIVE_DELIMITER = "=";
    private static final String DIRECTIVES_DELIMITER = "; ";
    private static final String HTTP_ONLY_DIRECTIVE = "HttpOnly";
    private static final int DIRECTIVE_NAME_INDEX = 0;
    private static final int DIRECTIVE_VALUE_INDEX = 1;

    private final Map<String, String> directives;

    private boolean isHttpOnly;

    public HttpCookie(Map<String, String> directives, boolean isHttpOnly) {
        this.directives = directives;
        this.isHttpOnly = isHttpOnly;
    }

    public static HttpCookie from(Session session) {
        HashMap<String, String> directives = new HashMap<>(Map.of(
                JSESSIONID_DIRECTIVE, session.getId()
        ));
        return new HttpCookie(directives, false);
    }

    public static HttpCookie from(List<String> cookieField) {
        boolean isHttpOnly = cookieField.contains(HTTP_ONLY_DIRECTIVE);
        if (isHttpOnly) {
            cookieField.remove(HTTP_ONLY_DIRECTIVE);
        }

        Map<String, String> directives = cookieField.stream()
                .map(HttpCookie::parseDirective)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return new HttpCookie(directives, isHttpOnly);
    }

    private static Map.Entry<String, String> parseDirective(String directiveString) {
        String[] directiveElements = directiveString.split(DIRECTIVE_DELIMITER);
        String directiveName = directiveElements[DIRECTIVE_NAME_INDEX];
        String directiveValue = directiveElements[DIRECTIVE_VALUE_INDEX];
        return Map.entry(directiveName, directiveValue);
    }

    public void setJsessionid(String jsessionid) {
        directives.put(JSESSIONID_DIRECTIVE, jsessionid);
    }

    public void setHttpOnly(boolean isHttpOnly) {
        this.isHttpOnly = isHttpOnly;
    }

    public void setPath(String path) {
        directives.put(PATH_DIRECTIVE, path);
    }

    public String getJsessionid() {
        return directives.getOrDefault(JSESSIONID_DIRECTIVE, "");
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
