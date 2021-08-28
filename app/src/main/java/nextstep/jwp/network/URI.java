package nextstep.jwp.network;

import java.util.Objects;

public class URI {

    private final String value;

    public URI(String value) {
        this.value = value;
    }

    public String getPath() {
        if (hasQuery()) {
            final int queryStart = value.indexOf("?");
            return value.substring(0, queryStart);
        }
        return value;
    }

    public boolean hasQuery() {
        return value.contains("?");
    }

    public String getQuery() {
        final int queryStart = value.indexOf("?");
        return value.substring(queryStart + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URI uri = (URI) o;
        return Objects.equals(value, uri.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
