package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.httpmessage.Request;

public class RequestExtractor {

    private RequestExtractor() {
    }

    public static Request extract(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> requestLines = extractRequestLines(reader);
        return Request.from(requestLines);
    }

    private static List<String> extractRequestLines(BufferedReader reader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            requestLines.add(line);
        }
        return requestLines;
    }
}
