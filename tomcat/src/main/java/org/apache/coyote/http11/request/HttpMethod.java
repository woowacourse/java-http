package org.apache.coyote.http11.request;

record HttpMethod(HttpMethodType type) {
    
    public static HttpMethod from(final String method) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP 메서드가 누락 되었습니다.");
        }
        return new HttpMethod(HttpMethodType.of(method.toUpperCase()));
    }
}
