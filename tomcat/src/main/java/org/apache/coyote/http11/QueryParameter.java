package org.apache.coyote.http11;

public record QueryParameter(
    String key,
    String value
) {

    public static QueryParameter from(String original) {
        String key = original.split("=")[0];
        String value = original.split("=")[1];
        return new QueryParameter(key, value);
    }
}
