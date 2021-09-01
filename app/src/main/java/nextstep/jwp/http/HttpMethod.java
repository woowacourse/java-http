package nextstep.jwp.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public boolean checkHttpMethod(HttpMethod httpMethod) {
        return this.equals(httpMethod);
    }
}
