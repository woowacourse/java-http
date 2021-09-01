package nextstep.jwp.http;

public enum HttpMethod {

    GET("GET"),
    POST("POST") {
        @Override
        public boolean isBody() {
            return true;
        }
    };

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public boolean isBody() {
        return false;
    }

    @Override
    public String toString() {
        return method;
    }
}
