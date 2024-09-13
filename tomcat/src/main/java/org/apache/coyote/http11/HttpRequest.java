package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.component.ContentType;
import org.apache.coyote.component.HttpHeaderField;
import org.apache.coyote.component.HttpHeaders;
import org.apache.coyote.component.HttpMethod;
import org.apache.coyote.component.Path;
import org.apache.coyote.component.RequestLine;

public class HttpRequest {

    private static final String DEFAULT_PLAIN_TEXT = "";
    private static final String BODY_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final Map<String, String> body;
    private final String plainText;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final Map<String, String> body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.plainText = DEFAULT_PLAIN_TEXT;
    }

    public HttpRequest(final RequestLine requestLine, final HttpHeaders headers, final String plainText) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = Map.of();
        this.plainText = plainText;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = RequestLine.from(reader.readLine());
        final var httpHeaders = HttpHeaders.parse(reader);
        final var contentType = ContentType.from(httpHeaders.get(HttpHeaderField.CONTENT_TYPE.getValue()));
        if (contentType == ContentType.APPLICATION_X_WWW_FORM_URLENCODED) {
            return new HttpRequest(requestLine, httpHeaders, parseRequestBodyKeyValue(httpHeaders, reader));
        }
        return new HttpRequest(requestLine, httpHeaders, parseRequestBody(httpHeaders, reader));
    }

    private static Map<String, String> parseRequestBodyKeyValue(final HttpHeaders headers,
                                                                final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.contains(HttpHeaderField.CONTENT_LENGTH.getValue())) {
            return Map.of();
        }
        final var serialized = read(headers, bufferedReader);
        final var requestBody = new String(serialized);
        return generate(requestBody);
    }

    private static String parseRequestBody(final HttpHeaders headers, final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.contains(HttpHeaderField.CONTENT_LENGTH.getValue())) {
            return "";
        }
        final var buffer = read(headers, bufferedReader);
        return new String(buffer);
    }

    private static char[] read(final HttpHeaders headers, final BufferedReader bufferedReader) throws IOException {
        final int contentLength = Integer.parseInt(headers.get(HttpHeaderField.CONTENT_LENGTH.getValue()));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return buffer;
    }

    private static Map<String, String> generate(final String requestBody) {
        final var body = new HashMap<String, String>();
        final var params = requestBody.split(BODY_SEPARATOR);
        for (final var param : params) {
            final var key = param.split(KEY_VALUE_SEPARATOR)[KEY_INDEX];
            final var value = param.split(KEY_VALUE_SEPARATOR)[VALUE_INDEX];
            body.put(key.strip(), value.strip());
        }
        return body;
    }

    public String getPath() {
        return requestLine.getPath().getRequestPath();
    }

    public int getContentLength() throws IOException {
        final var absolutePath = requestLine.getAbsolutePath();
        if (absolutePath == null) {
            return "".getBytes().length;
        }
        final String file = new String(Files.readAllBytes(new File(absolutePath.getFile()).toPath()));
        return file.getBytes().length;
    }

    private String getFile(final Path path) throws IOException {
        final var resource = path.getAbsolutePath();
        if (resource == null) {
            return "";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public String getResources() throws IOException {
        return getFile(requestLine.getPath());
    }

    public boolean hasMethod(final HttpMethod method) {
        return requestLine.isEqualHttpMethod(method);
    }

    public String getContentTypeToResponseText() {
        final var path = requestLine.getPath();
        final var contentType = ContentType.from(path);
        return contentType.toResponseText();
    }

    public String getBodyValue(final String key) {
        return body.get(key);
    }

    public String getProtocolValue() {
        return requestLine.getProtocol().getValue();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public String getPlainText() {
        return plainText;
    }
}
