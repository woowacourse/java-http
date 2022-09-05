package nextstep.jwp.support;

import java.util.Arrays;

public enum ResourceSuffix {

    HTML(".html"),
    CSS(".css"),
    JAVASCRIPT(".js");

    private final String value;

    ResourceSuffix(final String value) {
        this.value = value;
    }

    public static boolean isEndWith(final String line) {
        return Arrays.stream(values())
                .anyMatch(it -> line.endsWith(it.value));
    }
}
