package nextstep.jwp.util;

public enum Methods {
    GET("GET"),
    POST("POST");

    private final String method;

    Methods(String method) {
        this.method = method;
    }

    public static boolean isGet(String targetMethod) {
        return "GET".equals(targetMethod);
    }

    public static boolean isPost(String targetMethod) {
        return "POST".equals(targetMethod);
    }
}
