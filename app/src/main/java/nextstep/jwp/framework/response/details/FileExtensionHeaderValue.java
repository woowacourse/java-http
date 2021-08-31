package nextstep.jwp.framework.response.details;

import java.util.Arrays;

public enum FileExtensionHeaderValue {

    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JS("application/javascript"),
    SVG("image/svg+xml"),
    OTHER("");

    private final String headerValue;

    FileExtensionHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public static FileExtensionHeaderValue of(String fileExtension) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(fileExtension))
                .findAny()
                .orElse(OTHER);
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
