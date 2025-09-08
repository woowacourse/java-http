package org.apache.coyote;

import java.util.Arrays;

public enum HttpMethod {

    GET("GET"),
    ;

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public static HttpMethod fromHttp11Request(String request) {
        String method = request.split(System.lineSeparator())[0].split(" ")[0];
        if (method.isBlank()) {
            throw new IllegalArgumentException("Cannot resolve Http Request.");
        }
        return Arrays.stream(values())
                .filter(value -> method.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot resolve Http Method from request: " + method));
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "HttpMethod{" +
                "name='" + name + '\'' +
                '}';
    }
}
