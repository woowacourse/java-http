package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.util.Objects;

import nextstep.jwp.framework.http.HttpRequest;

public abstract class AbstractHttpParser implements HttpParser {

    protected final BufferedReader reader;
    protected final HttpRequest.Builder builder;

    protected AbstractHttpParser(BufferedReader reader, HttpRequest.Builder builder) {
        this.builder = builder;
        this.reader = reader;
    }

    public static boolean hasLength(String line) {
        return Objects.nonNull(line) && !line.isBlank();
    }

    @Override
    public HttpRequest buildRequest() {
        return builder.build();
    }
}
