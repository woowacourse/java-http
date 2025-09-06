package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class Http11InputBuffer {
    public static HttpRequest parseToRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }

        String[] splitRequestLine = requestLine.split(" ");

        String httpMethod = splitRequestLine[0];
        String uri = splitRequestLine[1];
        double httpVersion = Double.parseDouble(splitRequestLine[2].split("/")[1]);

        String host = "";
        String contentType = "";
        int contentLength = 0;
        String rawCookie = "";
        Cookie cookie = null;

        while (true) {
            String nextLine = bufferedReader.readLine();
            if (nextLine == null || nextLine.isEmpty()) {
                break;
            }

            String lowerCaseNextLine = nextLine.toLowerCase();
            if (lowerCaseNextLine.startsWith("host")) {
                host = nextLine.split(":")[1].trim();
            } else if (lowerCaseNextLine.startsWith("content-type")) {
                contentType = nextLine.split(":")[1].trim();
            } else if (lowerCaseNextLine.startsWith("content-length")) {
                contentLength = Integer.parseInt(nextLine.split(":")[1].trim());
            } else if (lowerCaseNextLine.startsWith("rawCookie")) {
                rawCookie = nextLine.split(":")[1].trim();
            }
        }

        String requestBody = null;
        if (httpMethod.equals("POST") && contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            requestBody = new String(bodyChars);
        }

        if (!rawCookie.isEmpty()) {
            cookie = new Cookie(rawCookie);
        }

        return new HttpRequest(
                httpMethod,
                uri,
                httpVersion,
                host,
                contentType,
                requestBody,
                cookie
        );
    }
}
