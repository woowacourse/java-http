package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record HttpRequest (HttpStartLine httpStartLine, HttpHeaders httpHeaders){

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpStartLine startLine = new HttpStartLine(httpRequestReader.readLine());
        List<String> headers = new ArrayList<>();
        while (httpRequestReader.ready()) {
            String header = httpRequestReader.readLine();
            if (header.isBlank()) break;
            headers.add(header);
        }
        HttpHeaders httpHeaders = new HttpHeaders(headers);
        return new HttpRequest(startLine, httpHeaders);
    }

    public boolean pathStartsWith(String path) {
        return httpStartLine.targetStartsWith(path);
    }

    public Map<String, String> parseQueryString() {
        return httpStartLine.parseQueryString();
    }

    public String getTargetExtension() {
        return httpStartLine.getTargetExtension();
    }

    public Path getPath(){
        URL resource = httpStartLine.getTargetUrl();
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("cannot convert to URI: " + resource);
        }
    }
}
