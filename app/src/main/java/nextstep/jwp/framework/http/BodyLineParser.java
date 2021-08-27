package nextstep.jwp.framework.http;

import java.util.Objects;

public class BodyLineParser extends AbstractLineParser {

    public BodyLineParser(HttpRequestBuilder httpRequestBuilder) {
        super(httpRequestBuilder);
    }

    BodyLineParser() {}

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public LineParser parseLine(String line) {
        if (Objects.isNull(line) || line.isBlank()) {
            return new EndLineParser(httpRequestBuilder);
        }

        httpRequestBuilder.body(line);
        return this;
    }
}
