package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javassist.NotFoundException;

public class HttpRequestBody {

    private final String body;

    private HttpRequestBody(String body) {
        this.body = body;
    }

    public static HttpRequestBody from(BufferedReader reader, HttpRequestHeader header) throws IOException {
        String body = "";
        if (header.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(header.getValue("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int read = reader.read(bodyChars, 0, contentLength);
            body = new String(bodyChars, 0, read);
        }
        return new HttpRequestBody(body);
    }

    public String getBody() {
        return body;
    }

    public byte[] getPage(String requestUrl) throws IOException, NotFoundException {
        URL resourceUrl = getClass().getClassLoader().getResource("static" + requestUrl);
        if (resourceUrl == null) {
            throw new NotFoundException(requestUrl);
        }
        Path path = new File((resourceUrl).getPath()).toPath();
        return Files.readAllBytes(path);
    }

    public QueryParams getQueryParams() {
        return QueryParams.from(body);
    }
}
