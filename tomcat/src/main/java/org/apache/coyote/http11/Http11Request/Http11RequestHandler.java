package org.apache.coyote.http11.Http11Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Http11RequestHandler {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final String INPUT_DELIMITER = " ";

    public Http11Request makeRequest(BufferedReader bufferedReader) throws IOException {
        List<String> requestElements;
        try (bufferedReader) {
            requestElements = List.of(bufferedReader.readLine().split(INPUT_DELIMITER));
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return new Http11Request(requestElements.get(HTTP_METHOD_INDEX), requestElements.get(URI_INDEX));
    }
}
