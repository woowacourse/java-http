package nextstep.jwp.http;

import java.util.Objects;

public class Location {

    private static final String EMPTY_LOCATION = "";
    private static final String HEADER_TEMPLATE = "Location: %s ";

    private final String value;

    public Location(final String value) {
        this.value = value;
    }

    public static Location empty() {
        return new Location(EMPTY_LOCATION);
    }

    public String toHeaderFormat() {
        if (value.equals(EMPTY_LOCATION)) {
            return EMPTY_LOCATION;
        }
        return String.format(HEADER_TEMPLATE, value);
    }

    public boolean isEmpty() {
        return value.equals(EMPTY_LOCATION);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Location location = (Location) o;
        return Objects.equals(value, location.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
