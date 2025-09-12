package org.apache.coyote.http11.request_response;

public enum HttpMethod {
    GET,
    POST
    ;

    public static HttpMethod getByName(String name) {
        for (HttpMethod value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 Http Method 입니다 : " + name);
    }
}
