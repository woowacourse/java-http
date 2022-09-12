package web.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private static final String WHITE_SPACE = " ";
    private static final String HEADER_KEY_VALUE_DELIMITER = ":";
    private static final String EMPTY_BODY = "";
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    public static final int HEADER_KEY_INDEX = 0;
    public static final int HEADER_VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final String body;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.requestLine = parseRequestLine(bufferedReader.readLine());
        this.header = parseHeader(bufferedReader);
        this.body = parseRequestBody(bufferedReader);
    }

    private RequestLine parseRequestLine(final String firstLine) {
        String[] requestLine = firstLine.split(WHITE_SPACE);
        return new RequestLine(
                requestLine[METHOD_INDEX],
                requestLine[REQUEST_URI_INDEX],
                requestLine[HTTP_VERSION_INDEX]
        );
    }

    private Map<String, String> parseHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        Map<String, String> header = new LinkedHashMap<>();
        for (String s : headerLines) {
            String[] headerLine = s.split(HEADER_KEY_VALUE_DELIMITER);
            header.put(headerLine[HEADER_KEY_INDEX].strip(), headerLine[HEADER_VALUE_INDEX].strip());
        }
        return header;
    }

    private String parseRequestBody(final BufferedReader bufferedReader) throws IOException {
        if (!isHeaderKey("Content-Length")) {
            return EMPTY_BODY;
        }
        int contentLength = Integer.parseInt(getHeaderValue("Content-Length").orElse("0"));
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return new String(body);
    }

    private boolean isHeaderKey(final String key) {
        return this.header.containsKey(key);
    }

    public Optional<String> getHeaderValue(final String key) {
        if (!header.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(header.get(key));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getBody() {
        return body;
    }
}
