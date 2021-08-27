package nextstep.jwp.framework.http;

public abstract class AbstractLineParser implements LineParser {

    protected final HttpRequest.Builder builder;

    protected AbstractLineParser() {
        this.builder = new HttpRequest.Builder();
    }

    public AbstractLineParser(HttpRequest.Builder builder) {
        this.builder = builder;
    }

    @Override
    public HttpRequest buildRequest() {
        return builder.build();
    }
}
