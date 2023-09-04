package org.apache.coyote.http11;

public class HttpHeader {

    public static final HttpHeader EMPTY = new HttpHeader("", "");

    private static final String SEPARATOR = ": ";

    private final String name;
    private final String value;

    protected HttpHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public HttpHeader(String line) {
        String[] pair = line.split(SEPARATOR);
        this.name = pair[0].trim();
        this.value = pair[1].trim();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String toLine() {
        return getName() + SEPARATOR + getValue();
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
