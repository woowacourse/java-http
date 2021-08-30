package nextstep.jwp.http;

public enum HttpMethod {
    GET, POST;

    public boolean isPost() {
        return this.equals(POST);
    }
}
