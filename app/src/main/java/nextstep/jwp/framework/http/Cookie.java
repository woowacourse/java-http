package nextstep.jwp.framework.http;

import nextstep.jwp.framework.util.StringUtils;

public class Cookie {

    private final String name;

    private final String value;

    protected Cookie(String name, String value) {
        this.name = StringUtils.requireNonBlank(name);
        this.value = StringUtils.requireNonBlank(value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
