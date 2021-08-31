package nextstep.jwp.http;

public class HttpResponse {

    private String response;

    public HttpResponse(String response) {
        this.response = response;
    }

    public String value() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
