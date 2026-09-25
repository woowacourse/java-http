package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HttpRequestHeader(
        Map<String, String> header,
        HttpCookie cookie
) {
    private static final String MIME_TYPE_DEFAULT = "text/html";
    private static final String MIME_TYPES_WILDCARD = "*/*";

    private static final String CONTENT_LENGTH = "Content-Length";

    public static HttpRequestHeader from(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        HttpCookie cookie = HttpCookie.empty();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            String[] parts = line.split(": ", 2);
            if (parts[0].equals("Cookie")) {
                cookie = HttpCookie.from(parts[1]);
                continue;
            }
            if (parts.length == 2) {
                headers.put(parts[0], parts[1]);
            }
        }
        return new HttpRequestHeader(headers, cookie);
    }

    public boolean hasContain(String string) {
        return header.containsKey(string);
    }

    public Optional<Integer> getContentLength() {
        return Optional.ofNullable((header.get(CONTENT_LENGTH)))
                .map(Integer::parseInt);
    }

    public String resolveContentType() {
        String accept = header().get("Accept");

        if (accept == null || accept.isEmpty()) {
            return MIME_TYPE_DEFAULT;
        }

        String preferred = accept.split(",")[0].split(";")[0].trim();

        if (MIME_TYPES_WILDCARD.equals(preferred)) {
            return MIME_TYPE_DEFAULT;
        }

        return preferred;
    }
}
