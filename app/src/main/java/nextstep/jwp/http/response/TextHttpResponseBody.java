package nextstep.jwp.http.response;

public class TextHttpResponseBody implements HttpResponseBody {

    private final String body;

    public TextHttpResponseBody(String body) {
        this.body = body;
    }

    @Override
    public String getBody() {
        return body;
    }
}
