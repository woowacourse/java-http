package nextstep.jwp.http.common;

import java.util.Objects;

public class Body {

    private static final Body EMPTY = new Body(null);
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String value;

    public Body(String value) {
        this.value = value;
    }

    public static Body empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return Objects.isNull(value);
    }

    @Override
    public String toString() {
        return NEW_LINE + value;
    }
}
