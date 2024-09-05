package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpVersion;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ":";

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String requestLine = bufferedReader.readLine();

        this.requestLine = RequestLine.from(requestLine);
        this.header = parseHeader(bufferedReader);
        this.body = parseBody(bufferedReader);
    }

    private RequestHeader parseHeader(BufferedReader bufferedReader) throws IOException {
        RequestHeader header = new RequestHeader();
        String readLine = bufferedReader.readLine();
        while (readLine != null && !readLine.equals("")) {
            String[] headerToken = readLine.split(HEADER_DELIMITER);
            String value = reconstructHeaderValue(Arrays.copyOfRange(headerToken, 1, headerToken.length));
            header.addHeader(headerToken[0], value);
            readLine = bufferedReader.readLine();
        }
        return header;
    }

    private RequestBody parseBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBody = new StringBuilder();

        if (!header.hasContentLength()) {
            return RequestBody.empty();
        }

        for (int i = 0; i < header.getContentLength(); i++) {
            stringBody.append((char) bufferedReader.read());
        }

        return new RequestBody(stringBody.toString());
    }

    private String reconstructHeaderValue(String[] headerValues) {
        StringJoiner stringJoiner = new StringJoiner(HEADER_DELIMITER);
        for (String value : headerValues) {
            stringJoiner.add(value.strip());
        }
        return stringJoiner.toString();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public RequestHeader getHeaders() {
        return header;
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public boolean isBodyEmpty() {
        return body.isEmpty();
    }

    public String getBody() {
        return body.getBodyValue();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", header=" + header +
                ", body=" + body +
                '}';
    }
}
