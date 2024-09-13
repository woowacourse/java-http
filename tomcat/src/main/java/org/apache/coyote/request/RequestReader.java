package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    public static HttpRequest readRequest(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        List<String> headerLines = new ArrayList<>();
        String rawLine;
        String contentLengthHeader = "";
        try {
            while ((rawLine = bufferedReader.readLine()) != null && !rawLine.isEmpty()) {
                headerLines.add(rawLine);

                if (rawLine.startsWith("Content-Length")) {
                    contentLengthHeader = rawLine.split(": ")[1];
                }
            }

            String bodyLine = "";
            if (!contentLengthHeader.isEmpty()) {
                bodyLine = readBody(bufferedReader, contentLengthHeader);
            }

            return new HttpRequest(headerLines, bodyLine);

        } catch (IOException e) {
            throw new IllegalStateException("Error reading input stream", e);
        }
    }

    private static String readBody(BufferedReader bufferedReader, String contentLengthHeader) throws IOException {
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength); // 본문을 버퍼로 읽어들임
        return new String(buffer);  // 읽은 버퍼를 문자열로 변환
    }
}
