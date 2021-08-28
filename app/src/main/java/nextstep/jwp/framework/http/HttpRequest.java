package nextstep.jwp.framework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest {

    public static final String DELIMITER = " ";
    public static final String LINE_DELIMITER = "\r\n";
    public static final int HTTP_REQUEST_LINE_INDEX = 0;
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int HTTP_PATH_INDEX = 1;
    public static final int HTTP_VERSION_OF_THE_PROTOCOL_INDEX = 2;

    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final String lines = readInputStream(inputStream);
        this.requestLine = createRequestLine(lines);
        this.headers = createHeader(lines);
    }

    private String readInputStream(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder lines = new StringBuilder();

        while (bufferedReader.ready()) {
            lines.append(bufferedReader.readLine())
                .append("\r\n");
        }

        return lines.toString();
    }

    private HttpRequestLine createRequestLine(final String lines) throws IOException {
        final String[] line = lines.split(LINE_DELIMITER)[HTTP_REQUEST_LINE_INDEX].split(DELIMITER);

        final HttpMethod method = HttpMethod.findByString(line[HTTP_METHOD_INDEX]);
        final HttpPath path = new HttpPath(line[HTTP_PATH_INDEX]);
        final ProtocolVersion protocolVersion = new ProtocolVersion(line[HTTP_VERSION_OF_THE_PROTOCOL_INDEX]);

        return new HttpRequestLine(method, path, protocolVersion);
    }

    private HttpHeaders createHeader(final String lines) throws IOException {
        final String[] headers = lines.split(LINE_DELIMITER);
        final StringBuilder headerLines = new StringBuilder();

        for (int i = 1; i < headers.length; i++) {
            headerLines.append(headers[i])
                .append(LINE_DELIMITER);
        }

        return new HttpHeaders(headerLines.toString());
    }

    public HttpResponse toHttpResponse(final HttpStatus httpStatus) {
        return new HttpResponse(requestLine, httpStatus, headers);
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public URL getResource() {
        return requestLine.getPath().findResourceURL();
    }
}
