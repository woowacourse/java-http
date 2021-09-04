package nextstep.jwp.framework.http.common;

import java.util.Arrays;

public enum FileExtensionHeader {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("application/javascript"),
    SVG("image/svg+xml"),
    OTHER("");

    private String header;

    FileExtensionHeader(String header) {
        this.header = header;
    }

    public static FileExtensionHeader value(final String fileExtension) {
        return Arrays.stream(values())
            .filter(value -> value.name().equalsIgnoreCase(fileExtension))
            .findAny()
            .orElse(OTHER);
    }

    public String getHeader() {
        return header;
    }
}
