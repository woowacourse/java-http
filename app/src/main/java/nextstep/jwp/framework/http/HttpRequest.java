package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.StringJoiner;

public class HttpRequest {

    public static final String DELIMITER = " ";
    public static final String LINE_DELIMITER = "\r\n";
    public static final String BODY_DELIMITER = LINE_DELIMITER + LINE_DELIMITER;
    private static final int HTTP_REQUEST_LINE_INDEX = 0;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_HEADER_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final int HTTP_VERSION_OF_THE_PROTOCOL_INDEX = 2;

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String lines = readInputStream(bufferedReader);

        this.requestLine = createRequestLine(lines);
        this.headers = createHeader(lines);
        this.body = createBody(bufferedReader);
    }

    private String readInputStream(final BufferedReader bufferedReader) throws IOException {
        final StringJoiner lines = new StringJoiner(LINE_DELIMITER);
        String line = null;

        while (!Objects.equals(line, "")) {
            line = bufferedReader.readLine();
            validate(line);
            lines.add(line);
        }

        return lines.toString();
    }

    private String readBody(final BufferedReader bufferedReader) throws IOException {
        int contentLength = headers.contentLength();
        char[] buffer = new char[contentLength];

        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private void validate(String line) throws IOException {
        if (line == null) {
            throw new IOException();
        }
    }

    private HttpRequestLine createRequestLine(final String lines) {
        final String[] line = lines.split(LINE_DELIMITER)[HTTP_REQUEST_LINE_INDEX].split(DELIMITER);

        final HttpMethod method = HttpMethod.findByString(line[HTTP_METHOD_INDEX]);
        final HttpPath path = new HttpPath(line[HTTP_PATH_INDEX]);
        final ProtocolVersion protocolVersion = new ProtocolVersion(line[HTTP_VERSION_OF_THE_PROTOCOL_INDEX]);

        return new HttpRequestLine(method, path, protocolVersion);
    }

    private HttpHeaders createHeader(final String lines) {
        final String[] headers = lines.split(BODY_DELIMITER)[HTTP_HEADER_INDEX].split(LINE_DELIMITER);
        final StringJoiner headerLines = new StringJoiner(LINE_DELIMITER);

        for (int i = 1; i < headers.length; i++) {
            headerLines.add(headers[i]);
        }

        return new HttpHeaders(headerLines.toString());
    }

    private HttpBody createBody(final BufferedReader reader) throws IOException {
        if (requestLine.isNotPost()) {
            return new HttpBody();
        }

        return new HttpBody(readBody(reader));
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public String pathValue() {
        return requestLine.path().getPath();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public URL getResource() {
        return requestLine.path().findResourceURL();
    }

    public HttpPath path() {
        return requestLine.path();
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }
}
