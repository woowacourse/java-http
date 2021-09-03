package nextstep.jwp.http;

public enum HttpMethod {

    GET,
    POST;

    public boolean isGet() {
        return GET == this;
    }

    public boolean isPost() {
        return POST == this;
    }
}
