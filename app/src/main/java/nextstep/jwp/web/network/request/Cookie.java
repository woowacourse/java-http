package nextstep.jwp.web.network.request;

public class Cookie {

    private final String key;
    private final String value;

    public Cookie(String keyAndValue) {
        this(keyOf(keyAndValue), valueOf(keyAndValue));
    }

    private Cookie(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private static String keyOf(String keyAndValue) {
        final int indexOfDelimiter = keyAndValue.indexOf("=");
        return keyAndValue.substring(0, indexOfDelimiter);
    }

    private static String valueOf(String keyAndValue) {
        final int indexOfDelimiter = keyAndValue.indexOf("=");
        return keyAndValue.substring(indexOfDelimiter + 1);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
