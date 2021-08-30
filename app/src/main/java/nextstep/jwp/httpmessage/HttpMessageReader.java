package nextstep.jwp.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class HttpMessageReader {

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
}
