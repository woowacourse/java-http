package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestStreamReader {
    private final InputStream inputStream;

    public HttpRequestStreamReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List<String> read() throws IOException {
        List<String> requestLines = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            requestLines.add(line);
            line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
        }

        int contentLength = parseContentLength(requestLines);
        if (contentLength != 0) {
            try {
                requestLines.add("");
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String s = new String(buffer);
                requestLines.add(s);
            } catch (IOException exception) {

            }
        }
        return requestLines;
    }

    private int parseContentLength(List<String> httpRequestHeaders) {
        Optional<String> contentLengthHeader = httpRequestHeaders.stream()
            .filter(header -> header.startsWith("Content-Length:"))
            .findAny();
        if (contentLengthHeader.isEmpty()) {
            return 0;
        }
        String[] split = contentLengthHeader.orElseThrow(IllegalStateException::new)
            .split(": ");
        if (split.length != 2) {
            throw new IllegalStateException("Invalid Content Length");
        }
        return Integer.parseInt(split[1]);
    }
}
