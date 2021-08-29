package nextstep.jwp.model.httpmessage.request;

public enum HttpMethod {
    GET,
    POST;

    public boolean isPost() {
        return this == POST;
    }
}
