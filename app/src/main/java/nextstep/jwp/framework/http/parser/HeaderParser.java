package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import nextstep.jwp.framework.http.HttpRequest;

public class HeaderParser extends AbstractHttpParser {

    private static final String CRLF = "\r\n";

    private static final String COLON = ":";
    private static final int NAME_INDEX = 0;
    private static final int VALUES_INDEX = 1;

    public HeaderParser(BufferedReader reader, HttpRequest.Builder builder) {
        super(reader, builder);
    }

    @Override
    public boolean isParsing() {
        return true;
    }

    @Override
    public String readParsingContent() throws IOException {
        String line;
        StringBuilder headers = new StringBuilder();
        while (hasLength(line = reader.readLine())) {
            headers.append(line).append(CRLF);
        }
        return headers.toString();
    }

    @Override
    public HttpParser parse() throws IOException {
        String headers = readParsingContent();
        for (String headerLine : separateHeaders(headers)) {
            final List<String> header = separateNameAndValue(headerLine);
            final String name = header.get(NAME_INDEX).trim();
            final String values = header.get(VALUES_INDEX).trim();
            builder.header(name, values);
        }

        return new BodyParser(reader, builder);
    }

    private String[] separateHeaders(String headers) {
        return headers.split(CRLF);
    }

    private List<String> separateNameAndValue(String line) {
        final int colonIndex = line.indexOf(COLON);
        final String headerName = line.substring(0, colonIndex);
        final String values = line.substring(colonIndex + 1);
        return Arrays.asList(headerName, values);
    }
}
