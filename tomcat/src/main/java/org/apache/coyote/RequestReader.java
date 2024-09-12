package org.apache.coyote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.request.HttpRequest;

public class RequestReader {

    public static HttpRequest readRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<String> headerLines = new ArrayList<>();
        String rawLine;
        int contentLength = 0;
        int headerCount = 0;
        boolean isBody = false;
        while ((rawLine = bufferedReader.readLine()) != null) {
            headerLines.add(rawLine);
            if (rawLine.isEmpty()) {
                isBody = true;
            }
            if (!isBody) {
                headerCount++;
            }
            if (rawLine.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(rawLine.split(": ")[1]);
            }
        }
        String bodyLine = headerLines.get(headerCount + 1);
        return new HttpRequest(headerLines, bodyLine);
    }
}
