package nextstep.jwp.framework.http;

public class EndLineParser extends AbstractLineParser {

    public EndLineParser(HttpRequestBuilder httpRequestBuilder) {
        super(httpRequestBuilder);
    }

    @Override
    public boolean canParse() {
        return false;
    }

    @Override
    public LineParser parseLine(String line) {
        throw new UnsupportedOperationException("파싱이 종료된 리퀘스트입니다.");
    }
}
