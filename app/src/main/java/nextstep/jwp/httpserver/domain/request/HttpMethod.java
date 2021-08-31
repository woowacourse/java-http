package nextstep.jwp.httpserver.domain.request;

public enum HttpMethod {
    GET, POST;

    public static boolean isGet(HttpMethod httpMethod) {
        return GET == httpMethod;
    }

    public static boolean isPost(HttpMethod httpMethod) {
        return POST == httpMethod;
    }
}
