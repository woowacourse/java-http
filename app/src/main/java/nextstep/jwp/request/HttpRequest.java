package nextstep.jwp.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.Http;

public class HttpRequest {
    private final BufferedReader reader;
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public HttpRequest(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.requestLine = new RequestLine(reader.readLine());
        this.requestHeader = new RequestHeader(extractHeaders());
        this.requestBody = new RequestBody(extractRequestBody());

    }

    private String extractHeaders() throws IOException {
        final StringBuilder headerLines = new StringBuilder();
        String header = null;
        while (!Http.EMPTY_LINE.equals(header)) {
            header = reader.readLine();
            if (Objects.isNull(header)) {
                break;
            }
            headerLines.append(header)
                    .append(Http.NEW_LINE);
        }
        return headerLines.toString();
    }

    private String extractRequestBody() throws IOException {
        if (requestHeader.contains(Header.CONTENT_LENGTH.getKey())) {
            int contentLength = Integer.parseInt(requestHeader.get(Header.CONTENT_LENGTH.getKey()));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return Http.EMPTY_LINE;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
