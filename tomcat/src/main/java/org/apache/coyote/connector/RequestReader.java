package org.apache.coyote.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.request.HttpRequest;

public class RequestReader {

    private RequestReader() {
    }

    public static HttpRequest readRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        List<String> headerLines = new ArrayList<>();
        String rawLine;
        String contentLengthHeader = null;

        while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
            headerLines.add(rawLine);
            contentLengthHeader = getContentLength(rawLine);
        }
        String bodyLine = readBody(bufferedReader, contentLengthHeader);

        return new HttpRequest(headerLines, bodyLine);
    }

    private static String getContentLength(String rawLine) {
        if (rawLine.toLowerCase().startsWith("content-length:")) {
            return rawLine.split(":")[1].trim();
        }
        return null;
    }

    private static String readBody(BufferedReader bufferedReader, String contentLengthHeader) throws IOException {
        if (contentLengthHeader == null) {
            return "";
        }
        if (!contentLengthHeader.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid Content-Length header: " + contentLengthHeader);
        }
        int contentLength = Integer.parseInt(contentLengthHeader);

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}

