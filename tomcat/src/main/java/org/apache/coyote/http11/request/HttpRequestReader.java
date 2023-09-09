package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HttpRequestReader {

    private static final String LINE_DELIMITER = System.lineSeparator();
    private static final String HEADER_DELIMITER = ": ";

    private final String startLine;
    private final String header;
    private final String body;

    private HttpRequestReader(String startLine, String header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequestReader create(BufferedReader bufferedReader) throws IOException {
        List<Integer> contentLengthStorage = new ArrayList<>();
        String startLine = readStartLine(bufferedReader);
        String header = readHeader(bufferedReader, contentLengthStorage);
        String body = "";
        if (!contentLengthStorage.isEmpty()) {
            body = readBody(bufferedReader, contentLengthStorage.get(0));
        }
        return new HttpRequestReader(startLine, header, body);
    }

    private static String readStartLine(BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine();
    }

    private static String readHeader(BufferedReader bufferedReader, List<Integer> contentLengthStorage) throws IOException {
        StringJoiner headerJoiner = new StringJoiner(LINE_DELIMITER);

        String header = "";
        while ((header = bufferedReader.readLine()) != null && !header.isEmpty()) {
            headerJoiner.add(header);
            String[] splitHeader = header.split(HEADER_DELIMITER);
            String headerName = splitHeader[0].trim();
            String headerValue = splitHeader[1].trim();
            if (HttpHeader.CONTENT_LENGTH.isSameValue(headerName)) {
                contentLengthStorage.add(Integer.parseInt(headerValue));
            }
        }
        return headerJoiner.toString();
    }

    private static String readBody(BufferedReader bufferedReader, int bodyLength) throws IOException {
        char[] buffer = new char[bodyLength];
        bufferedReader.read(buffer, 0, bodyLength);
        return new String(buffer);
    }

    public String startLine() {
        return startLine;
    }

    public String header() {
        return header;
    }

    public String body() {
        return body;
    }
}
