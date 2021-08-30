package nextstep.jwp.model.httpmessage.request;

public enum HttpMethod {
    GET,
    POST;

    public boolean isPost() {
        return this == POST;
    }

    public boolean isGet() {
        return this == GET;
    }
}
