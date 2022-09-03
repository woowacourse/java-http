package nextstep.jwp.http.reqeust;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.HttpHeader;

public class HttpRequestCreator {

    private static final String LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    public static final int REQUEST_LINE_CONTENT_COUNT = 3;

    public static HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return new HttpRequest(httpRequestLine(bufferReader), httpRequestHeader(bufferReader));
    }

    private static HttpRequestLine httpRequestLine(final BufferedReader bufferReader) throws IOException {
        String[] line = bufferReader.readLine().split(LINE_SEPARATOR);
        validateLineFormat(line);
        return HttpRequestLine.of(line[METHOD_INDEX], line[URL_INDEX], line[VERSION_INDEX]);
    }

    private static void validateLineFormat(final String[] line) {
        if (line.length != REQUEST_LINE_CONTENT_COUNT) {
            throw new IllegalArgumentException();
        }
    }

    private static HttpHeader httpRequestHeader(final BufferedReader bufferReader) throws IOException {
        List<String> headers = new ArrayList<>();
        while (bufferReader.readLine().isBlank()) {
            headers.add(bufferReader.readLine());
        }
        return new HttpHeader(headers);
    }
}
