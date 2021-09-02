package nextstep.jwp.framework.infrastructure.http.response;

public class HttpResponseBody {

    private final String response;

    public HttpResponseBody(String response) {
        this.response = response;
    }

    public String getResponseBody() {
        return response;
    }
}
