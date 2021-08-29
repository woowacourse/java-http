package nextstep.jwp.framework.http;

import java.util.Arrays;

import nextstep.jwp.framework.util.ResourceUtils;

public enum ContentType {
    ANY("*/*"), PLAIN("text/plain"), HTML("text/html"), CSS("text/css");

    private static final String CHARSET_FORMAT = "%s; charset=%s";

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTypeWithUtf8() {
        return getTypeWith("utf-8");
    }

    public String getTypeWith(String charset) {
        return String.format(CHARSET_FORMAT, type, charset);
    }

    public boolean isSameExtension(String extension) {
        return this.name().toLowerCase().equals(extension);
    }

    public static String resolve(String path) {
        final String extension = ResourceUtils.getFileExtension(path);

        return Arrays.stream(values())
                     .filter(contentType -> contentType.isSameExtension(extension))
                     .findAny()
                     .orElse(PLAIN)
                     .getTypeWithUtf8();
    }
}
