package nextstep.jwp.model.http;

public enum HttpMethod {
    GET,
    POST;

    public boolean isPost() {
        return this == POST;
    }
}
