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

        String hostLine = bufferedReader.readLine();
        String[] splitHostLine = hostLine.split(" ");
        String host = splitHostLine[1];

        String contentType = "";
        while (true) {
            String nextLine = bufferedReader.readLine();
            if (nextLine.isEmpty()) {
                break;
            }
            if (nextLine.contains("contentType")) {
                contentType = nextLine.split(":")[1];
            }
        }

        if (httpMethod.equals("POST")) {
            String requestBody = bufferedReader.readLine();

            return new HttpRequest(
                    httpMethod,
                    uri,
                    httpVersion,
                    host,
                    contentType,
                    requestBody
            );
        }

        return new HttpRequest(
                httpMethod,
                uri,
                httpVersion,
                host,
                null,
                null
        );
    }
}
