package http;

import http.exception.InvalidHttpRequestFormatException;
import java.util.Objects;

public class HttpHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int VALID_HEADER_LINE_SIZE = 2;

    private final String name;
    private final String value;

    private HttpHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpHeader of(final String name, final String value) {
        return new HttpHeader(name, value);
    }

    public static HttpHeader parse(final String headerString) {
        String[] splitHeaderLine = headerString.split(HEADER_DELIMITER);
        validateHeaderFormat(splitHeaderLine);
        return new HttpHeader(splitHeaderLine[HEADER_NAME_INDEX].strip(), splitHeaderLine[HEADER_VALUE_INDEX].strip());
    }

    private static void validateHeaderFormat(final String[] splitHeaderLine) {
        if (splitHeaderLine.length != VALID_HEADER_LINE_SIZE) {
            throw new InvalidHttpRequestFormatException();
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String toResponseFormat() {
        return name + HEADER_DELIMITER + value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpHeader that = (HttpHeader) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
