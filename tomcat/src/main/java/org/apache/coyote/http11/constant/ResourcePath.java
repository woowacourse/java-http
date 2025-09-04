package org.apache.coyote.http11.constant;

public record ResourcePath(String value) {

    public ContentType extractContentType() {
        final String[] split = value.split("\\.");
        if (split.length != 2) {
            throw new IllegalStateException("정적 파일이 아닙니다.");
        }
        return ContentType.from(split[1]);
    }

    public boolean isQueryString() {
        long count = value.chars().filter(ch -> ch == '?').count();
        return value.contains("?") && count == 1;
    }

    public boolean isStaticResource() {
        return value.contains(".");
    }
}
