package nextstep.jwp.http.http_request;

public enum JwpHttpMethod {
    GET,
    POST;

    public static JwpHttpMethod of(String httpMethod) {
        return valueOf(httpMethod);
    }

    public boolean isGetRequest() {
        return this == GET;
    }

    public boolean isPostRequest() {
        return this == POST;
    }
}
