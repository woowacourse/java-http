package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nextstep.jwp.framework.http.parser.LineParser;
import nextstep.jwp.framework.http.parser.RequestLineParser;

public class HttpRequestParser {

    private final BufferedReader bufferedReader;
    private LineParser lineParser;

    public HttpRequestParser(InputStream inputStream) {
        this(new BufferedReader(new InputStreamReader(inputStream)), new RequestLineParser());
    }

    public HttpRequestParser(BufferedReader bufferedReader, LineParser lineParser) {
        this.bufferedReader = bufferedReader;
        this.lineParser = lineParser;
    }

    public HttpRequest parseRequest() throws IOException {
        while (lineParser.canParse()) {
            lineParser = lineParser.parseLine(bufferedReader.readLine());
        }
        return lineParser.buildRequest();
    }
}
