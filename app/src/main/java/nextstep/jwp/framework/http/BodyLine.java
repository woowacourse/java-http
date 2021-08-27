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
    public ParsingLine parse(String line) {
        if (Objects.isNull(line) || line.isBlank()) {
            return new EndLine();
        }

        super.httpRequestBuilder.body(line);
        return this;
    }
}
