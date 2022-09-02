package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestLine;

public class HttpCreator {

    public static HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return new HttpRequest(httpRequestLine(bufferReader), httpRequestHeader(bufferReader));
    }

    private static HttpRequestLine httpRequestLine(final BufferedReader bufferReader) throws IOException {
        String line = bufferReader.readLine();
        return HttpRequestLine.from(line);
    }

    private static HttpHeader httpRequestHeader(final BufferedReader bufferReader) throws IOException {
        List<String> headers = new ArrayList<>();
        while (bufferReader.readLine().isBlank()) {
            headers.add(bufferReader.readLine());
        }
        return new HttpHeader(headers);
    }
}
