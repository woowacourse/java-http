package nextstep.jwp.framework.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import nextstep.jwp.framework.http.HttpRequest;

public class HttpRequestParser {

    private final BufferedReader reader;

    public HttpRequestParser(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    public HttpRequest parseRequest() throws IOException {
        HttpParser httpParser = new RequestLineParser(reader);
        while (httpParser.isParsing()) {
            httpParser = httpParser.parse();
        }

        return httpParser.buildRequest();
    }
}
