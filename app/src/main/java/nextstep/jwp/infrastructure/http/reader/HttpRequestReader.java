package nextstep.jwp.infrastructure.http.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.jwp.infrastructure.http.Headers;
import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.request.RequestLine;

public class HttpRequestReader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String EMPTY = "";

    private final BufferedReader reader;

    public HttpRequestReader(final BufferedReader reader) {
        this.reader = reader;
    }

    public HttpRequest readHttpRequest() throws IOException {
        final RequestLine requestLine = requestLineFromReader(reader);
        final Headers headers = headerFromReader(reader);
        final String body = bodyFromReader(reader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private RequestLine requestLineFromReader(final BufferedReader bufferedReader) throws IOException {
        return RequestLine.of(bufferedReader.readLine().trim());
    }

    private Headers headerFromReader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;

        while (Objects.nonNull(line = bufferedReader.readLine())) {
            if (EMPTY.equals(line.trim())) {
                break;
            }
            lines.add(line.trim());
        }

        return Headers.of(lines);
    }

    private String bodyFromReader(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        if (!headers.hasKey(CONTENT_LENGTH)) {
            return EMPTY;
        }

        int contentLength = Integer.parseInt(headers.getValue(CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }
}
