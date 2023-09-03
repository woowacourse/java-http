package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestReader {

    private static final String ACCEPT = "Accept";
    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private UriRequest uriRequest;

    public RequestReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public void read() throws IOException {
        String line;
        uriRequest = UriRequest.of(bufferedReader.readLine());
        while (!(line = bufferedReader.readLine()).isBlank()) {
            putHeader(line);
        }
    }

    public void putHeader(String line) {
        String[] split = line.split(" ");
        String name = split[0].substring(0, split[0].length() - 1);
        String content = split[1];
        headers.put(name, content);
    }

    public String getContentType() {
        return headers.getOrDefault(ACCEPT, ContentType.HTML.getType());
    }

    public String getRequestUrl() {
        return uriRequest.getUrl();
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public UriRequest getUriRequest() {
        return uriRequest;
    }
}
