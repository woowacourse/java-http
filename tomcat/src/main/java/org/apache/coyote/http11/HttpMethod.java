package org.apache.coyote.http11;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod from(String method) {
        if (method.equals(GET.name())
                || method.equals(POST.name())
                || method.equals(PUT.name())
                || method.equals(DELETE.name())) {
            return HttpMethod.valueOf(method);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
