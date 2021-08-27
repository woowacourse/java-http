package nextstep.jwp.framework.http;

public abstract class AbstractLineParser implements LineParser {

    protected final HttpRequestBuilder httpRequestBuilder;

    protected AbstractLineParser() {
        this.httpRequestBuilder = new HttpRequestBuilder();
    }

    public AbstractLineParser(HttpRequestBuilder httpRequestBuilder) {
        this.httpRequestBuilder = httpRequestBuilder;
    }

    @Override
    public HttpRequest buildRequest() {
        return httpRequestBuilder.build();
    }
}
