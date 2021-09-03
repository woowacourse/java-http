package nextstep.jwp.http.message;

import java.util.Objects;
import nextstep.jwp.exception.HttpRequestFormatException;

public class HttpCookie {

    private String name;
    private String value;

    public HttpCookie(String name, String value) {
        if (Objects.isNull(name) || name.length() == 0) {
            throw new HttpRequestFormatException();
        }
        this.name = name;
        this.value = value;
    }

    public static HttpCookie parseFrom(String cookie) {
        String[] splitted = cookie.split("=");
        if (splitted.length != 2) {
            throw new HttpRequestFormatException();
        }
        return new HttpCookie(splitted[0], splitted[1].trim());
    }

    public boolean isSameName(String name) {
        return this.name.equals(name);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String asString() {
        return String.join("=", name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpCookie that = (HttpCookie) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(
            getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }
}
