package nextstep.jwp.httpmessage.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpMessageReader {

    public static final String SP = " ";
    public static final String CRLF = "\r\n";

    private static final String HEADER_DELIMITER = ": ";

    private final BufferedReader bufferedReader;
    private final String startLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

    public HttpMessageReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public HttpMessageReader(InputStreamReader inputStreamReader) {
        this(new BufferedReader(inputStreamReader));
    }

    public HttpMessageReader(BufferedReader reader) {
        this.bufferedReader = reader;
        this.startLine = initializeStartLine();
        this.headers = initializeHeaders();
        this.parameters = initializeParameters();
    }

    public String getStartLine() {
        return startLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    private String initializeStartLine() {
        try {
            final String requestLine = bufferedReader.readLine();
            if (Objects.isNull(requestLine) || requestLine.isEmpty()) {
                return "";
            }
            return requestLine.trim();
        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    private Map<String, String> initializeHeaders() {
        try {
            Map<String, String> httpRequestHeaders = new HashMap<>();
            while (bufferedReader.ready()) {
                final String line = bufferedReader.readLine();
                if (Objects.isNull(line) || line.isEmpty()) {
                    break;
                }
                final String[] header = line.split(HEADER_DELIMITER);
                if (header.length == 1) {
                    continue;
                }
                httpRequestHeaders.put(header[0], header[1].strip());
            }
            return httpRequestHeaders;
        } catch (IOException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    private Map<String, String> initializeParameters() {
        final String contentLengthString = headers.get("Content-Length");
        if (Objects.isNull(contentLengthString)) {
            return Collections.emptyMap();
        }
        return Stream.of(extractQueryParameterString(contentLengthString).split("&"))
                .map(it -> it.split("=", 2))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    private String extractQueryParameterString(String contentLengthString) {
        try {
            final int contentLength = Integer.parseInt(contentLengthString);
            final char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String queryParameterString = new String(buffer);
            return queryParameterString;
        } catch (IOException exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
    }
}
