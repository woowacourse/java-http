package kokodak.http;

import static kokodak.Constants.BLANK;
import static kokodak.Constants.COLON;
import static kokodak.Constants.CRLF;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int HTTP_REQUEST_MESSAGE_START_LINE = 0;

    private HttpMethod httpMethod;

    private RequestTarget requestTarget;

    private HttpVersion httpVersion;

    private Map<String, String> header;

    private String body;

    private HttpRequest(final HttpMethod httpMethod,
                        final RequestTarget requestTarget,
                        final HttpVersion httpVersion,
                        final Map<String, String> header,
                        final String body) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest of(final BufferedReader bufferedReader, final List<String> primitiveRequest) throws IOException {
        final String startLine = getStartLine(primitiveRequest);
        final HttpMethod httpMethod = HttpMethod.from(startLine);
        final RequestTarget requestTarget = RequestTarget.from(startLine);
        final HttpVersion httpVersion = HttpVersion.from(startLine);
        final Map<String, String> header = getHeader(primitiveRequest);
        final String body = getBody(bufferedReader, header);
        return new HttpRequest(httpMethod, requestTarget, httpVersion, header, body);
    }

    private static String getStartLine(final List<String> primitiveRequest) {
        final String startLine = primitiveRequest.get(HTTP_REQUEST_MESSAGE_START_LINE);
        validateStartLine(startLine);
        return startLine;
    }

    private static void validateStartLine(final String startLine) {
        if (startLine.split(" ").length != 3) {
            throw new IllegalArgumentException("Invalid HTTP Request Message");
        }
    }

    private static Map<String, String> getHeader(final List<String> primitiveRequest) {
        final Map<String, String> header = new HashMap<>();
        final int size = primitiveRequest.size();
        for (int i = 1; i < size; i++) {
            final String request = primitiveRequest.get(i);
            if (request.equals(CRLF.getValue())) {
                break;
            }
            final String[] headerKeyValue = request.split(COLON.getValue() + BLANK.getValue());
            header.put(headerKeyValue[0], headerKeyValue[1]);
        }
        return header;
    }

    private static String getBody(final BufferedReader bufferedReader, final Map<String, String> header) throws IOException {
        if (header.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(header.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public boolean hasQueryString() {
        return requestTarget.hasQueryString();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String header(final String key) {
        return header.get(key);
    }

    public String getBody() {
        return body;
    }
}
