package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestReceiver {

    HttpRequest receiveRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        HttpRequestHeader header = new HttpRequestHeader(receiveRequestLines(bufferedReader));
        HttpRequestBody body = null;

        int contentLength = Integer.parseInt(header.getHeader("content-length"));

        if ("POST".equals(header.getHttpMethod()) || "PUT".equals(header.getHttpMethod())) {
            body = new HttpRequestBody(receiveRequestBody(bufferedReader, contentLength));
        }

        return new HttpRequest(header, body);
    }

    private static String receiveRequestLines(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            String input = bufferedReader.readLine();
            if (input == null || input.isBlank()) {
                break;
            }
            sb.append(input).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static String receiveRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        bufferedReader.read(bodyChars, 0, contentLength);
        return new String(bodyChars);
    }
}
