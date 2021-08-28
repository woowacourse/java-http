package nextstep.jwp.request;

import java.util.Objects;

public class ClientRequest {
    private final HttpMethod httpMethod;
    private final RequestUrl requestUrl;

    public ClientRequest(HttpMethod httpMethod, RequestUrl requestUrl) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }

    public static ClientRequest of(String method, String url) {
        return new ClientRequest(HttpMethod.of(method), RequestUrl.of(url));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRequest that = (ClientRequest) o;
        return Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(requestUrl, that.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUrl);
    }
}
