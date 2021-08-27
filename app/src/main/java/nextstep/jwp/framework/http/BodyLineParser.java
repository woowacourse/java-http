package nextstep.jwp.framework.http;

import java.util.Objects;

public class BodyLineParser extends AbstractLineParser {

    public BodyLineParser(HttpRequest.Builder builder) {
        super(builder);
    }

    BodyLineParser() {}

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public LineParser parseLine(String line) {
        if (Objects.isNull(line) || line.isBlank()) {
            return new EndLineParser(builder);
        }

        builder.body(line);
        return this;
    }
}
