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
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> headerLines = new ArrayList<>();

        String contentLengthHeader = readHeader(bufferedReader, headerLines);
        String bodyLine = readBody(bufferedReader, contentLengthHeader);
        return new HttpRequest(headerLines, bodyLine);
    }

    private static String readHeader(BufferedReader bufferedReader, List<String> headerLines) throws IOException {
        String rawLine;
        String contentLengthHeader = null;
        while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
            headerLines.add(rawLine);
            contentLengthHeader = getContentLength(rawLine);
        }

        if (contentLengthHeader == null) {
            return "";
        }
        if (!contentLengthHeader.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid Content-Length header: " + contentLengthHeader);
        }
        return contentLengthHeader;
    }

    private static String getContentLength(String rawLine) {
        if (rawLine.toLowerCase().startsWith("content-length:")) {
            return rawLine.split(":")[1].trim();
        }
        return null;
    }

    private static String readBody(BufferedReader bufferedReader, String contentLengthHeader) throws IOException {
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}

