package nextstep.jwp.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class HttpMessageReader {

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
        final Map<String, String> params = new HashMap<>();
        final String[] parameters = extractQueryParameterString(contentLengthString).split("&");
        for (String parameter : parameters) {
            final String[] splitParameter = parameter.split("=");
            if (splitParameter.length < 2) {
                params.put(splitParameter[0], "");
                continue;
            }
            params.put(splitParameter[0], splitParameter[1]);
        }
        return params;
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
