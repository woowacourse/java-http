package org.apache.coyote.http11.constant;

import java.util.regex.Pattern;

public record ResourcePath(String value) {

    public ContentType extractContentType() {
        final String[] split = value.split("\\.");
        if (split.length != 2) {
            throw new IllegalStateException("정적 파일이 아닙니다.");
        }
        return ContentType.from(split[1]);
    }

    public boolean isQueryString() {
        if (value == null || value.isBlank()) {
            return false;
        }
        long count = value.chars().filter(ch -> ch == '?').count();
        return count == 1;
    }

    public boolean isStaticResource() {
        if (isQueryString() || value == null) {
            return false;
        }
        final Pattern staticResourcePattern = Pattern.compile(
                ".*\\.(html|css|js|png|jpg|jpeg|gif|ico|json)$",
                Pattern.CASE_INSENSITIVE
        );
        return staticResourcePattern.matcher(value).matches();
    }
}
