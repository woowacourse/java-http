package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.dto.HttpRequestUrl;

public class HttpRequestHandler {

    public HttpRequest process(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestUrl httpRequestUrl = readUrl(reader);
        Map<String, String> headers = readHeaders(reader);

        return new HttpRequest(httpRequestUrl.method(), httpRequestUrl.path(), httpRequestUrl.version(), headers);
    }

    private HttpRequestUrl readUrl(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String path = parts[1];
        String version = parts[2];
        return new HttpRequestUrl(method, path, version);
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].strip(), headerParts[1].strip());
            }
        }

        return headers;
    }
}
