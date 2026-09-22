package org.apache.coyote.http11.request;

public final class RequestParams {
 
    private RequestParams() {
    }
 
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean anyBlank(String... values) {
        for (String value : values) {
            if (isBlank(value)) {
                return true;
            }
        }
        return false;
    }
}
