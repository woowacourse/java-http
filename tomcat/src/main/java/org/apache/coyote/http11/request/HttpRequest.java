package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;
    private static final String EMPTY_LINE_SIGNATURE = "";

    private final RequestGeneral requestGeneral;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestGeneral requestGeneral, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestGeneral = requestGeneral;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(InputStream inputStream) {
        List<String> lines = readAllLines(inputStream);

        int emptyLineIndex = findEmptyLine(lines);
        return new HttpRequest(
                RequestGeneral.parse(lines.get(0)),
                RequestHeaders.parse(lines.subList(START_LINE_INDEX + 1, emptyLineIndex)),
                RequestBody.parse(lines.subList(emptyLineIndex + 1, lines.size()))
        );
    }

    private static List<String> readAllLines(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().collect(Collectors.toUnmodifiableList());
    }

    private static int findEmptyLine(List<String> lines) {
        return lines.indexOf(EMPTY_LINE_SIGNATURE);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestGeneral=" + requestGeneral +
                ", requestHeaders=" + requestHeaders +
                ", requestBody=" + requestBody +
                '}';
    }
}
