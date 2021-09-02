package nextstep.jwp.web;

import java.util.Objects;

public class Cookie {
    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cookie cookie = (Cookie) o;
        return Objects.equals(getName(), cookie.getName()) &&
                Objects.equals(getValue(), cookie.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    @Override
    public String toString() {
        return this.name + "=" + this.value;
    }
}
