package nextstep.jwp.http.request;

public class TextHttpRequestBody implements HttpRequestBody{
    private String body;

    public TextHttpRequestBody(String body) {
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }
}
