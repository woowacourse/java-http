package org.apache.coyote.http11;

public enum MethodType {

    GET("GET"),
    POST("POST");

    private final String type;

    MethodType(String type) {
        this.type = type;
    }

    public static boolean isGetMethod(String method) {
        return GET.type.equals(method);
    }

    public static boolean isPostMethod(String method) {
        return POST.type.equals(method);
    }
}
