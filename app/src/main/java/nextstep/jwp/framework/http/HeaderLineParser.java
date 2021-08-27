package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HeaderLineParser extends AbstractLineParser {

    private static final String COLON = ":";
    private static final String COMMA = ",";
    private static final int NAME_INDEX = 0;
    private static final int VALUES_INDEX = 1;

    public HeaderLineParser(HttpRequestBuilder httpRequestBuilder) {
        super(httpRequestBuilder);
    }

    HeaderLineParser() {}

    @Override
    public boolean canParse() {
        return true;
    }

    @Override
    public LineParser parseLine(String line) {
        if (Objects.isNull(line)) {
            return new EndLineParser(httpRequestBuilder);
        }

        if (line.isBlank()) {
            return new BodyLineParser(httpRequestBuilder);
        }

        final List<String> header = separateNameAndValues(line);
        final String name = header.get(NAME_INDEX).trim();
        final List<String> values = separateValues(header.get(VALUES_INDEX));
        httpRequestBuilder.header(name, values);
        return this;
    }

    private List<String> separateNameAndValues(String line) {
        final int colonIndex = line.indexOf(COLON);
        final String headerName = line.substring(0, colonIndex);
        final String values = line.substring(colonIndex + 1);
        return Arrays.asList(headerName, values);
    }

    private List<String> separateValues(String values) {
        return Arrays.asList(values.split(COMMA));
    }
}
