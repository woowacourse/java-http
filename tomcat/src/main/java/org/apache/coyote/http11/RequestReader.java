package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestReader {

    private final BufferedReader bufferedReader;
    private final Map<String, String> headers = new HashMap<>();
    private UriRequest uriRequest;

    public RequestReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public void read() throws IOException {
        String line;
        uriRequest = UriRequest.of(bufferedReader.readLine());
        while ((line = bufferedReader.readLine()).isEmpty()) {
            putHeader(line);
        }
    }

    public void putHeader(String line) {
        String[] split = line.split(" ");
        String name = split[0].substring(0, split.length - 1);
        String content = split[1];
        headers.put(name, content);
    }

    public void getMediaType(String mediaType) {
        headers.getOrDefault(mediaType, MediaType.HTML.getType());
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public UriRequest getUriRequest() {
        return uriRequest;
    }
}
