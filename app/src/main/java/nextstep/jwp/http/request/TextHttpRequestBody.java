package nextstep.jwp.http.request;

public class TextHttpRequestBody implements HttpRequestBody {

    private final String body;

    public TextHttpRequestBody(String body) {
        this.body = body;
    }

    @Override
    public Object getAttribute(String key) {
        return new UnsupportedOperationException("텍스트 데이터입니다. getBody()를 활용하세요.");
    }

    @Override
    public String getBody() {
        return body;
    }
}
