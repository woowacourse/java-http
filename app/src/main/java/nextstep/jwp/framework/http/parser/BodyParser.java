package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import nextstep.jwp.framework.http.HttpHeaders;
import nextstep.jwp.framework.http.HttpRequest;

public class BodyParser extends AbstractHttpParser {

    private static final String EMPTY = "";

    public BodyParser(BufferedReader reader, HttpRequest.Builder builder) {
        super(reader, builder);
    }

    @Override
    public boolean isParsing() {
        return true;
    }

    @Override
    public String readParsingContent() throws IOException {
        if (builder.hasHeader(HttpHeaders.CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(builder.getHeaderValue(HttpHeaders.CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            reader.read(buffer);
            return new String(buffer);
        }

        return EMPTY;
    }

    @Override
    public HttpParser parse() throws IOException {
        return new EndLineParser(reader, builder.body(readParsingContent()));
    }
}
