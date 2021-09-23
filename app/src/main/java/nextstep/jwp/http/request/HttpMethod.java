package nextstep.jwp.http.request;

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
