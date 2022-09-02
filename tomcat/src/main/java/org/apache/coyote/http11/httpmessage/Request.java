package org.apache.coyote.http11.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Request {

    String requestLine;
    String headers;
    String body;

    public Request(String requestLine, String headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }


    public static Request of(String requestMessage) throws IOException {
        if (requestMessage == null) {
            throw new IllegalStateException("잘못된 요청입니다.");
        }
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(requestMessage));
        String requestLine = bufferedReader.readLine();
        StringBuilder headers = new StringBuilder();
        while (true) {
            String buffer = bufferedReader.readLine();
            headers.append(buffer)
                    .append("\r\n");
            if (buffer == null || buffer.length() == 0) {
                break;
            }
        }
        return new Request(requestLine, headers.toString());
    }

    public boolean isGetMethod() {
        return requestLine.split(" ")[0].equals("GET");
    }

    public String getUri() {
        return requestLine.split(" ")[1];
    }
}
