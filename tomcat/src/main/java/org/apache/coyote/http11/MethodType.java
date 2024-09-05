package org.apache.coyote.http11;

public enum MethodType {
    GET,
    POST,
    ;

    public static MethodType toMethodType(String methodType) {
        return valueOf(methodType);
    }
}
