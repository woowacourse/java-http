package nextstep.jwp.web.http.request.body;

public class TextHttpRequestBody implements HttpRequestBody<String> {

    private final String body;

    public TextHttpRequestBody(String body) {
        this.body = body;
    }

    @Override
    public String getAttribute(String key) {
        throw new UnsupportedOperationException("텍스트 데이터입니다. getBody()를 활용하세요.");
    }

    @Override
    public String getBody() {
        return body;
    }
}
