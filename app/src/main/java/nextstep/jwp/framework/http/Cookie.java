package nextstep.jwp.framework.http;

import nextstep.jwp.framework.util.StringUtils;

public class Cookie {

    public static final String HTTP_FORMAT = "%s=%s";

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.name = StringUtils.requireNonBlank(name);
        this.value = StringUtils.requireNonBlank(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format(HTTP_FORMAT, name, value);
    }
}
