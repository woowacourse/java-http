package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestStreamReader {

    private static final String CONTENT_LENGTH_OF_HEADER_KEY = "Content-Length:";
    private static final String BLANK = "";
    private final InputStream inputStream;
    private String statusLine;
    private List<String> headerLines = new ArrayList<>();
    private String bodyLine;

    public HttpRequestStreamReader(InputStream inputStream) {
        try {
            this.inputStream = inputStream;
            read();
        } catch (IOException e) {
            throw new CustomException("IOException");
        }
    }

    public void read() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        line = initStatusLine(bufferedReader, line);
        initHeaderLines(bufferedReader, line);
        initBodyLine(bufferedReader);
    }

    private String initStatusLine(BufferedReader bufferedReader, String line) throws IOException {
        if (!BLANK.equals(line)) {
            this.statusLine = line;
            line = bufferedReader.readLine();
        }
        return line;
    }

    private void initBodyLine(BufferedReader bufferedReader) throws IOException {
        int contentLength = parseContentLength(headerLines);
        if (contentLength != 0) {
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            this.bodyLine = new String(buffer);
        }
    }

    private void initHeaderLines(BufferedReader bufferedReader, String line) throws IOException {
        while (!BLANK.equals(line)) {
            headerLines.add(line);
            line = bufferedReader.readLine();
            if (Objects.isNull(line)) {
                break;
            }
        }
    }

    private int parseContentLength(List<String> httpRequestHeaders) {
        Optional<String> contentLengthHeader = httpRequestHeaders.stream()
            .filter(header -> header.startsWith(CONTENT_LENGTH_OF_HEADER_KEY))
            .findAny();

        String[] split = contentLengthHeader.orElseGet(() -> "Content-Length: 0")
            .split(": ");
        if (split.length != 2) {
            throw new CustomException("Header에 포함된 Content-Length의 값의 형태가 적절하지 않습니다.");
        }
        return Integer.parseInt(split[1]);
    }

    public String getStatusLine() {
        return statusLine;
    }

    public List<String> getHeaderLines() {
        return Collections.unmodifiableList(headerLines);
    }

    public String getBodyLine() {
        return bodyLine;
    }

    public List<String> getRequestLines() {
        List<String> result = new ArrayList<>();
        result.add(statusLine);
        result.addAll(new ArrayList<>(headerLines));
        if (!Objects.isNull(bodyLine)) {
            result.add(BLANK);
            result.add(bodyLine);
        }
        return Collections.unmodifiableList(result);
    }
}
