package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    private final BufferedReader bufferedReader;
    private ParsingLine parsingLine;

    public HttpRequestParser(InputStream inputStream) {
        this(new BufferedReader(new InputStreamReader(inputStream)), new StatusLine());
    }

    public HttpRequestParser(BufferedReader bufferedReader, ParsingLine parsingLine) {
        this.bufferedReader = bufferedReader;
        this.parsingLine = parsingLine;
    }

    public HttpRequest parseRequest() throws IOException {
        while (parsingLine.canParse()) {
            parsingLine = parsingLine.parseLine(bufferedReader.readLine());
        }

        return parsingLine.buildRequest();
    }
}
