package org.apache.coyote.http11;

public enum HttpMethod {

    GET("GET"), POST("POST"), PUT("PUT"), PATCH("PATCH");

    private String httpMethod;

    HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
