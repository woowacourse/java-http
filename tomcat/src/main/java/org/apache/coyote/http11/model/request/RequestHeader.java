package org.apache.coyote.http11.model.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

public record RequestHeader(
        int contentLength,
        Cookie cookie
) {

    public static RequestHeader from(BufferedReader reader) throws IOException {
        String line;
        int requestContentLength = 0;
        String cookieForm = "";
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(":", 2);
            if (header.length != 2) {
                continue;
            }
            String name = header[0].trim().toLowerCase(Locale.ROOT);
            String value = header[1].trim();
            if ("content-length".equals(name)) {
                requestContentLength = Integer.parseInt(value);
            }
            if ("cookie".equals(name)) {
                cookieForm = value;
            }
        }
        return new RequestHeader(requestContentLength, Cookie.from(cookieForm));
    }
}
