package org.apache.coyote;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

//    public static HttpMethod fromHttp11Request(HttpRequest request) {
//        String method = request.getMethod();
//        if (method.isBlank()) {
//            throw new IllegalArgumentException("Cannot resolve Http Request.");
//        }
//        return Arrays.stream(values())
//                .filter(value -> method.equals(value.name))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Cannot resolve Http Method from request: " + method));
//    }

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
