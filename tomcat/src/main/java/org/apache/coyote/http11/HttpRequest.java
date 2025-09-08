package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HttpRequest {

    public static final String REQUEST_LINE_SEPARATOR = " ";
    public static final int METHOD_INDEX = 0;
    public static final int REQUEST_URI_INDEX = 1;

    private final HttpMethod method;
    private final String requestURI;
    private final HttpHeaders httpHeaders;
    private String requestBody;

    public HttpRequest(final InputStream inputStream) throws IOException {
        try {
            final BufferedReader httpRequestReader = new BufferedReader(new InputStreamReader(inputStream));

            // Request Line
            final String firstLine = httpRequestReader.readLine();
            final String[] splitBySP = firstLine.split(REQUEST_LINE_SEPARATOR);
            this.method = HttpMethod.valueOf(splitBySP[METHOD_INDEX]);
            this.requestURI = splitBySP[REQUEST_URI_INDEX];

            // Header
            final List<String> headerLines = new ArrayList<>();
            String line;
            while (!(line = httpRequestReader.readLine()).isEmpty()) {
                headerLines.add(line);
            }
            this.httpHeaders = new HttpHeaders(headerLines);

            // Request Body
            if (httpHeaders.contains(HttpHeaderField.CONTENT_LENGTH)) {
                final int contentLength = Integer.parseInt(httpHeaders.get(HttpHeaderField.CONTENT_LENGTH));
                final char[] buffer = new char[contentLength];
                httpRequestReader.read(buffer, 0, contentLength);
                this.requestBody = new String(buffer);
            }
        } catch (final IllegalArgumentException | ArrayIndexOutOfBoundsException | NullPointerException |
                       IOException e) {
            throw new IOException("HTTP Request를 해석할 수 없습니다.", e);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
