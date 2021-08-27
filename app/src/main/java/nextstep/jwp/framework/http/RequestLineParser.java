package nextstep.jwp.framework.http;

public class RequestLineParser extends AbstractLineParser {

    private static final int STATUS_LINE_TOKEN_COUNT = 3;

    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public LineParser parseLine(String line) {
        final String[] statusTokens = line.split(" ");

        if (statusTokens.length != STATUS_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("상태 줄에 4개 이상의 토큰들이 포함되어 있습니다.");
        }

        final HttpMethod httpMethod = HttpMethod.resolve(statusTokens[METHOD_INDEX]);
        final String uri = statusTokens[URI_INDEX];
        final String version = statusTokens[VERSION_INDEX];
        return new HeaderLineParser(httpRequestBuilder.requestLine(httpMethod, uri, version));
    }
}
