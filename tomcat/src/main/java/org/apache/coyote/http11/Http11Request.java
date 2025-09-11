package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.RequestLine;

public class Http11Request {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private RequestLine requestLine;
    private final Map<String, String> header = new HashMap<>();
    private Http11Cookie cookies;
    private String body;

    public Http11Request(InputStream inputStream) throws IOException {
        extractHeaderAndBody(inputStream);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getRequestMethod() {
        return requestLine.getMethod();
    }

    public String getBody() {
        return body;
    }

    public Http11Cookie getCookies() {
        return cookies;
    }

    private void extractHeaderAndBody(InputStream inputStream) throws IOException {
        final StringBuilder requestHeader = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, DEFAULT_CHARSET));

        String line;
        while (((line = bufferedReader.readLine()) != null) && (!line.isEmpty())) {
            requestHeader.append(line).append("\r\n");
        }
        String header = requestHeader.toString();

        String[] requestConditions = header.split("\r\n");
        if (requestConditions.length < 1) {
            throw new IOException("Invalid HTTP request: empty header");
        }

        extractFirstLineConditions(requestConditions);
        extractHeaders(requestConditions);
        extractCookies();
        extractBody(bufferedReader, getContentLength());
    }

    private void extractCookies() {
        this.cookies = new Http11Cookie(header.get("cookie"));
    }

    private int getContentLength() {
        String contentLength = this.header.get("content-length");
        if (contentLength == null) {
            return 0;
        }

        return Integer.parseInt(contentLength);
    }

    private void extractHeaders(String[] requestConditions) {
        for (int i = 1; i < requestConditions.length; i++) {
            String[] set = requestConditions[i].split(":", 2);
            this.header.put(set[0].trim().toLowerCase(), set[1].trim());
        }
    }

    private void extractBody(BufferedReader br, int contentLength) throws IOException {
        if (contentLength <= 0) {
            this.body = "";
            return;
        }
        char[] buf = new char[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = br.read(buf, off, contentLength - off);
            if (r == -1) {
                throw new IOException("Unexpected EOF while reading body");
            }
            off += r;
        }
        this.body = new String(buf, 0, off);
    }

    private void extractFirstLineConditions(String[] requestConditions) {
        this.requestLine = new RequestLine(requestConditions[0]);
    }
}
