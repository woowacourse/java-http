package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpRequestStartLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private static final String MESSAGE_DELIMITER = " ";

    private HttpRequestHandler() {
    }

    public static HttpRequest newHttpRequest(final BufferedReader bufferedReader) {
        HttpRequestParser parser = new HttpRequestParser(splitToRequest(bufferedReader));
        HttpRequestStartLine httpRequestStartLine = parser.getHttpRequestStartLine();
        HttpHeaders headers = parser.getHeaders();
        return new HttpRequest(httpRequestStartLine, headers);
    }

    private static List<String[]> splitToRequest(final BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !"".equals(line))
                .map(line -> line.split(MESSAGE_DELIMITER))
                .collect(Collectors.toList());
    }
}
