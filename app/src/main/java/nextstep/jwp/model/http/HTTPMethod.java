package nextstep.jwp.model.http;

public enum HTTPMethod {
    GET,
    POST;

    public boolean isPost() {
        return this == POST;
    }
}
