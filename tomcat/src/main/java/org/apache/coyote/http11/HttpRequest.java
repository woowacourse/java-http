package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        this(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    public HttpRequest(BufferedReader reader) throws IOException {
        this.requestLine = new RequestLine(readRequestLine(reader));
        this.headers = readHeaders(reader);
        this.body = readBody(reader, headers.getContentLength());
        this.parameters = mergeParameters(requestLine.getQueryParameters(), body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
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

    public String getBody() {
        return body;
    }

    public HttpCookie getCookie() {
        return new HttpCookie(getHeader("Cookie"));
    }

    private String readRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Request line is empty");
        }
        return line;
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> values = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            String[] nameAndValue = line.split(":", 2);
            if (nameAndValue.length != 2) {
                throw new IllegalArgumentException("Invalid header: " + line);
            }
            values.put(nameAndValue[0].trim(), nameAndValue[1].trim());
        }
        return new HttpHeaders(values);
    }

    private String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] value = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(value, totalRead, contentLength - totalRead);
            if (read < 0) {
                break;
            }
            totalRead += read;
        }
        return new String(value, 0, totalRead);
    }

    private Map<String, String> mergeParameters(Map<String, String> queryParameters, String body) {
        Map<String, String> merged = new LinkedHashMap<>(queryParameters);
        merged.putAll(RequestLine.parseParameters(body));
        return Collections.unmodifiableMap(merged);
    }
}
