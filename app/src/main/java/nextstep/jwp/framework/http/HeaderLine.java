package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeaderLine extends AbstractParsingLine {

    private static final String COLON = ":";
    private static final String COMMA = ",";
    private static final int NAME_INDEX = 0;
    private static final int VALUES_INDEX = 1;

    HeaderLine() {}

    public HeaderLine(HttpRequestBuilder httpRequestBuilder) {
        super(httpRequestBuilder);
    }

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public ParsingLine parse(String line) {
        if (Objects.isNull(line)) {
            return new EndLine();
        }

        if (line.isBlank()) {
            return new BodyLine();
        }

        final String[] header = separateNameAndValues(line);
        final String name = header[NAME_INDEX].trim();
        final List<String> values = separateValues(header[VALUES_INDEX]);
        super.httpRequestBuilder.header(name, values);
        return this;
    }

    private String[] separateNameAndValues(String line) {
        return line.split(COLON);
    }

    private List<String> separateValues(String values) {
        return Arrays.asList(values.split(COMMA));
    }
}
