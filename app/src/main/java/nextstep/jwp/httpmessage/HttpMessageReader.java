package nextstep.jwp.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpMessageReader {

    private static final String HEADER_DELIMITER = ": ";

    private final BufferedReader bufferedReader;

    public HttpMessageReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public HttpMessageReader(InputStreamReader inputStreamReader) {
        this(new BufferedReader(inputStreamReader));
    }

    public HttpMessageReader(BufferedReader reader) {
        this.bufferedReader = reader;
    }

    public String getStartLine() {
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

    public Map<String, String> getHeaders() {
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
}
