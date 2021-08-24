package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestExtractor {
    private static final int FIRST_LINE_OF_HTTP_REQUEST = 0;
    private static final String BLANK_DELIMITER = " ";
    private static final int SECOND_WORD_INDEX = 1;
    private List<String> requestLines = new ArrayList<>();

    public HttpRequestExtractor(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            requestLines.add(reader.readLine());
        }
    }

    public String extractURI() {
        String firstLine = requestLines.get(FIRST_LINE_OF_HTTP_REQUEST);
        String requestURI = firstLine.split(BLANK_DELIMITER)[SECOND_WORD_INDEX];
        return requestURI;
    }
}
