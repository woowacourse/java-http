package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    private final BufferedReader bufferedReader;

    public HttpRequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void parse() throws IOException {
        ParsingLine parsingLine = new StatusLine();
        while (parsingLine.canParse()) {
            parsingLine = parsingLine.parse(bufferedReader.readLine());
        }
    }
}
