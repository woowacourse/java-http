package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nextstep.jwp.framework.http.parser.HttpParser;
import nextstep.jwp.framework.http.parser.RequestLineParser;

public class HttpRequestParser {

    private final BufferedReader reader;

    public HttpRequestParser(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public HttpRequest parseRequest() throws IOException {
        HttpParser httpParser = new RequestLineParser(reader);
        while (httpParser.isParsing()) {
            httpParser = httpParser.parse();
        }

        return httpParser.buildRequest();
    }
}
