package org.apache.coyote.http11;

public class HttpHeader {

    private static final String SEPARATOR = ": ";

    private final String name;
    private final String value;

    protected HttpHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpHeader of(String line) {
        String[] pair = line.split(SEPARATOR);
        return new HttpHeader(pair[0].trim(), pair[1].trim());
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "HttpHeader{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
