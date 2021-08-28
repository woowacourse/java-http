package nextstep.jwp.request;

public class ClientRequest {
    private final HttpMethod httpMethod;
    private final RequestUrl requestUrl;

    public ClientRequest(HttpMethod httpMethod, RequestUrl requestUrl) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }
}
