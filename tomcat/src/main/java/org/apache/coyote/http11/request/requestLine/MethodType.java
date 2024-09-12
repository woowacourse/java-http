package org.apache.coyote.http11.request.requestLine;

public enum MethodType {
    GET,
    POST,
    ;

    public static MethodType toMethodType(String methodType) {
        return valueOf(methodType);
    }

    public boolean isPost() {
        return this == POST;
    }

    public boolean isGet() {
        return this == GET;
    }
}
