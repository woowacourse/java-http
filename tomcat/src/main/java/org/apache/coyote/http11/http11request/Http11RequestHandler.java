package org.apache.coyote.http11.http11request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Http11RequestHandler {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String FIRST_LINE_DELIMITER = " ";
    private static final String HEADER_END_POINT = "";
    private static final String NEW_LINE = "\r\n";

    public Http11Request makeRequest(BufferedReader bufferedReader) throws IOException {
        List<String> headerElements;
        String body;
        try {
            headerElements = extractHeaderElements(bufferedReader);
            body = extractBody(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return new Http11Request(headerElements.get(HTTP_METHOD_INDEX), headerElements.get(URI_INDEX), body);
    }

    private String extractBody(BufferedReader bufferedReader) throws IOException {
        StringJoiner stringJoiner = new StringJoiner(NEW_LINE);
        while (bufferedReader.ready()) {
            stringJoiner.add(bufferedReader.readLine());
        }
        return stringJoiner.toString();
    }


    private List<String> extractHeaderElements(BufferedReader bufferedReader) throws IOException {
        List<String> lineElements = List.of(bufferedReader.readLine().split(FIRST_LINE_DELIMITER));
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.equals(HEADER_END_POINT)) {
                break;
            }
        }
        return lineElements;
    }
}
