package nextstep.jwp.framework.http;

public abstract class AbstractParsingLine implements ParsingLine {

    protected final HttpRequestBuilder httpRequestBuilder;

    protected AbstractParsingLine() {
        this.httpRequestBuilder = new HttpRequestBuilder();
    }

    public AbstractParsingLine(HttpRequestBuilder httpRequestBuilder) {
        this.httpRequestBuilder = httpRequestBuilder;
    }

    @Override
    public HttpRequest buildRequest() {
        return httpRequestBuilder.build();
    }
}
