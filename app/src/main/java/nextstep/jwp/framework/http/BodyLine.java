package nextstep.jwp.framework.http;

import java.util.Objects;

public class BodyLine extends AbstractParsingLine {

    public BodyLine(HttpRequestBuilder httpRequestBuilder) {
        super(httpRequestBuilder);
    }

    BodyLine() {}

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public ParsingLine parseLine(String line) {
        if (Objects.isNull(line) || line.isBlank()) {
            return new EndLine(httpRequestBuilder);
        }

        httpRequestBuilder.body(line);
        return this;
    }
}
