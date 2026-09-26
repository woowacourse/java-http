package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.session.HttpCookie;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;
    private final Map<String, String> parameters;

    public HttpRequest(InputStream inputStream) throws IOException {
        this.requestLine = new RequestLine(readLine(inputStream));
        this.headers = readHeaders(inputStream);
        this.body = readBody(inputStream, headers.getContentLength());
        this.parameters = mergeParameters(requestLine.getQueryParameters(), body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public HttpCookie getCookie() {
        return new HttpCookie(getHeader("Cookie"));
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int current;
        while ((current = inputStream.read()) != -1 && current != '\n') {
            if (current != '\r') {
                line.write(current);
            }
        }

        if (current == -1 && line.size() == 0) {
            throw new IllegalArgumentException("Request line is empty");
        }
        return line.toString(StandardCharsets.ISO_8859_1);
    }

    private HttpHeaders readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        String line;
        while (!(line = readLine(inputStream)).isBlank()) {
            addHeader(values, line);
        }
        return new HttpHeaders(values);
    }

    private void addHeader(Map<String, String> values, String line) {
        String[] nameAndValue = line.split(":", 2);
        if (nameAndValue.length != 2) {
            throw new IllegalArgumentException("Invalid header: " + line);
        }
        values.put(nameAndValue[0].trim(), nameAndValue[1].trim());
    }

    private String readBody(InputStream inputStream, int contentLength) throws IOException {
        byte[] value = inputStream.readNBytes(contentLength);
        if (value.length != contentLength) {
            throw new IllegalArgumentException("Request body is shorter than Content-Length");
        }
        return new String(value, StandardCharsets.UTF_8);
    }

    private Map<String, String> mergeParameters(Map<String, String> queryParameters, String body) {
        Map<String, String> merged = new LinkedHashMap<>(queryParameters);
        merged.putAll(RequestLine.parseParameters(body));
        return Collections.unmodifiableMap(merged);
    }
}
