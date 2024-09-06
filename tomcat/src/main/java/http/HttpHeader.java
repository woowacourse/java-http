package http;

public record HttpHeader(String name, String value) {

    private static final String HEADER_FORMAT = "%s: %s ";

    public String buildHeader() {
        return HEADER_FORMAT.formatted(name, value);
    }
}
