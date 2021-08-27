package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {
    public void parse(BufferedReader bufferedReader) throws IOException {
        ParsingLine parsingLine = new StatusLine();
        while (parsingLine.canParse()) {
            parsingLine = parsingLine.parse(bufferedReader.readLine());
        }
    }
}
