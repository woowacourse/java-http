package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11InputStreamReader {

    private static final Logger log = LoggerFactory.getLogger(Http11InputStreamReader.class);

    public static List<String> read(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> lines = new ArrayList<>(readHeaders(reader));
            int contentLength = getContentLength(lines);
            lines.add(readBody(contentLength, reader));
            return lines;
        }
    }

    private static int getContentLength(List<String> headers) {
        for (String headerLine : headers) {
            if (headerLine.toLowerCase().startsWith("content-length:")) {
                try {
                    return Integer.parseInt(headerLine.split(":")[1].trim());
                } catch (NumberFormatException e) {
                    log.warn("Invalid Content-Length header format");
                }
            }
        }
        return 0;
    }

    private static List<String> readHeaders(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
            if (line.isEmpty()) {
                break;
            }
        }
        if (line == null) {
            log.warn("Incomplete headers received");
        }

        return lines;
    }

    private static String readBody(int contentLength, BufferedReader reader) throws IOException {
        StringBuilder bodyBuilder = new StringBuilder();

        if (contentLength > 0) {
            char[] body = new char[contentLength];
            int read = reader.read(body, 0, contentLength);
            if (read != contentLength) {
                log.warn("Not all body data was read");
            }
            bodyBuilder.append(body, 0, read);
        }

        return bodyBuilder.toString();
    }
}
