package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;

public record RequestHeader(
        int contentLength,
        Cookie cookie
) {

    public static RequestHeader from(BufferedReader reader) throws IOException {
        String line;
        int requestContentLength = 0;
        String cookieForm = "";
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.contains("Content-Length")) {
                String[] contentLengthLine = line.split(":", 2);
                requestContentLength = Integer.parseInt(contentLengthLine[1].trim());
            }
            if (line.contains("Cookie")) {
                String[] cookieLine = line.split(":", 2);
                cookieForm = cookieLine[1].trim();
            }
        }
        return new RequestHeader(requestContentLength, Cookie.from(cookieForm));
    }
}
