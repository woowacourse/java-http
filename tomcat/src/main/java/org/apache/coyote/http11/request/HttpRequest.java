package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.exception.TempException;

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
                RequestBody.parse(lines.subList(Math.min(emptyLineIndex + 1, lines.size()), lines.size()))
        );
    }

    private static List<String> readAllLines(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();

        String line;
        while (!(line = readOneLine(reader)).equals("")) {
            lines.add(line);
        }
        return lines;
    }

    private static String readOneLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new TempException();
        }
    }

    private static int findEmptyLine(List<String> lines) {
        int index = lines.indexOf(EMPTY_LINE_SIGNATURE);
        if (index == -1) {
            return lines.size();
        }
        return index;
    }


    @Override
    public String toString() {
        return "HttpRequest{\n" +
                "requestGeneral=" + requestGeneral +
                ", requestHeaders=" + requestHeaders +
                ", requestBody=" + requestBody +
                "\n}";
    }
}
