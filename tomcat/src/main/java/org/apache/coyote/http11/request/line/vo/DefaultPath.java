package org.apache.coyote.http11.request.line.vo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPath {

    private static String DEFAULT_PATH_PATTERN = "^/([\\w/.-]*)$";
    private static Pattern compiledPattern = Pattern.compile(DEFAULT_PATH_PATTERN);

    private final String value;

    public DefaultPath(String value) {
        this.value = value;
    }

    public static DefaultPath from(String defaultPath) {
        validateDefaultPath(defaultPath);
        return new DefaultPath(defaultPath);
    }

    private static void validateDefaultPath(String path) {
        Matcher matcher = compiledPattern.matcher(path);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("유효하지 않은 URI입니다. 올바른 URI인지 다시 확인해주세요.");
        }
    }

    public String value() {
        return value;
    }

}
