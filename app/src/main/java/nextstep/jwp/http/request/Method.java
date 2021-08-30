package nextstep.jwp.http.request;

public enum Method {
    GET("GET"),
    POST("POST");

    private String methodName;

    Method(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
