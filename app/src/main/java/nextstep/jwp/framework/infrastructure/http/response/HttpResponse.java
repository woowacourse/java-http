package nextstep.jwp.framework.infrastructure.http.response;

public class HttpResponse {

    private final String responseBody;

    public HttpResponse(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
